package com.ec.customer.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
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
import com.ec.customer.model.Visitors;
import com.ec.customer.service.VisitorsService;

@RestController
@EnableScheduling
@RequestMapping(path = { "/visitor" })
public class VisitorController extends BaseController implements InitializingBean, DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(VisitorController.class);

	//@Autowired
	//VisitersQueueService visitersQueueService;

	@Autowired
	VisitorsService visitorsService;

	@Value("${security.controller.visiter.inser.rate.limit:10}")
	int rateLimit;

	@Value("${security.controller.visiter.inser.rate.unit:2}")
	int rateUnit;

	protected static BlockingQueue<Visitors> visitorQueue = new LinkedBlockingQueue<Visitors>();

	@RequestMapping(path = { "/add" })
	public Response<String> addModelAttribute(@ModelAttribute Visitors visitors, HttpServletRequest request) {
		return addRequestBody(visitors, request);
	}

	@RequestMapping(path = { "/add" }, produces = { "application/json" }, consumes = { "application/json" })
	public Response<String> addRequestBody(@RequestBody Visitors visitors, HttpServletRequest request) {

		/*
		 * UserAgent userAgent =
		 * UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		 * Browser browser = userAgent.getBrowser(); OperatingSystem os =
		 * userAgent.getOperatingSystem();
		 * 
		 * visitors.setBrowserName(browser.getName());
		 * visitors.setBrowserOs(os.getName());
		 * visitors.setCreateip(getRemoteAddr(request));
		 * orderQueue.offer(visitors);
		 */

		return new Response<String>(Response.Code.SUCCESS.getValue());
	}

	static final String TASK_NAME = "visitors-insert-task";

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

		/*this.executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {

				Thread t = new Thread(r, TASK_NAME);

				LOGGER.info("Initializing Thread:" + t.getName() + ",Priority:" + t.getPriority());

				return t;

			}
		});

		executor.execute(new VisitorsInsertTask());*/

	}

	private class VisitorsInsertTask implements Runnable {
		@Override
		public void run() {
			try {
				while (!Thread.interrupted()) {

					if (visitorQueue.size() == 0){

						TimeUnit.SECONDS.sleep(rateUnit);
						continue;
					
					}

					List<Visitors> list = new ArrayList<>();

					visitorQueue.drainTo(list);

					int converted = list.size() > rateLimit ? -1 : 0;

					for (Visitors visitors : list) {
						visitors.setStatus(converted);
						visitors.setRateUnit(String.valueOf(rateUnit) + "s");
						visitors.setRateVal(list.size());
						visitors.setCreatedate(new Date());
					}

					visitorsService.insert(list);

					TimeUnit.SECONDS.sleep(rateUnit);

				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}

		}
	}

}
