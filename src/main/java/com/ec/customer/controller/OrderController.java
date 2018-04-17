package com.ec.customer.controller;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ec.common.controller.BaseController;
import com.ec.common.model.Response;
import com.ec.common.util.IDGen;
import com.ec.customer.model.Orders;
import com.ec.customer.service.OrdersService;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

/**
 *
 * @author winall
 *
 */
@RestController
@EnableScheduling
@RequestMapping(path = { "/order" })
public class OrderController extends BaseController implements InitializingBean, DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

	// @Autowired
	// OrdersQueueService ordersQueueService;

	@Autowired
	OrdersService ordersService;

	@Value("${security.controller.order.inser.rate.limit:10}")
	int rateLimit;

	@Value("${security.controller.order.inser.rate.unit:2}")
	int rateUnit;

	protected static BlockingQueue<Orders> orderQueue = new LinkedBlockingQueue<Orders>();

	@RequestMapping(path = { "/add" })
	public Response<String> addModelAttribute(@ModelAttribute Orders orders, HttpServletRequest request) {
		return addRequestBody(orders, request);
	}

	@RequestMapping(path = { "/add" }, produces = { "application/json" }, consumes = { "application/json" })
	public Response<String> addRequestBody(@RequestBody Orders orders, HttpServletRequest request) {

		if (orders.getCustomMobile() == null || orders.getCustomMobile().equals("")

				|| orders.getDeliverAdderss() == null || orders.getDeliverAdderss().equals("")

		) {
			throw new RuntimeException("Receive Error Custome Order");
		}

		LOGGER.warn("Get order:" + orders.toString());

		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		Browser browser = userAgent.getBrowser();
		OperatingSystem os = userAgent.getOperatingSystem();

		orders.setBrowserName(browser.getName());
		orders.setBrowserType(browser.getBrowserType().toString());
		orders.setBrowserOs(os.getName());
		orders.setCreateip(getRemoteAddr(request));
		orderQueue.offer(orders);
		return new Response<String>(Response.Code.SUCCESS.getValue());
	}

	static final String TASK_NAME = "orders-insert-task";

	private ExecutorService executor;

	@Override
	public void destroy() throws Exception {
		if (this.executor != null) {
			this.executor.shutdown();
			LOGGER.info("Destroy ExecutorService:" + TASK_NAME);
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {

		this.executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {

				Thread t = new Thread(r, TASK_NAME);
				
				t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						executor.execute(new OrdersInsertTask());
					}
					
				});

				LOGGER.info("Initializing Thread:" + t.getName() + ",Priority:" + t.getPriority());

				return t;

			}
		});

		executor.execute(new OrdersInsertTask());

	}

	private class OrdersInsertTask implements Runnable {
		@Override
		public void run() {

			while (!Thread.interrupted()) {
				try {
					
					//LOGGER.info("Check orders:" + orderQueue.size());

					if (orderQueue.size() == 0) {

						TimeUnit.SECONDS.sleep(rateUnit);
						continue;

					}

					List<Orders> list = new ArrayList<>();

					orderQueue.drainTo(list);

					int converted = list.size() > rateLimit ? -1 : 0;

					for (Orders orders : list) {

						orders.setStatus(converted);

						orders.setRateUnit(String.valueOf(rateUnit) + "s");
						orders.setRateVal(list.size());
						orders.setId(IDGen.next());
						orders.setCreatedate(new Date());
						orders.setCreatetime(new Date());

					}

					ordersService.insert(list);

					TimeUnit.SECONDS.sleep(rateUnit);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}

			}

		}
	}

}
