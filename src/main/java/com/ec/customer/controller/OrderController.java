package com.ec.customer.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ec.common.controller.BaseController;
import com.ec.common.model.Response;
import com.ec.common.util.IDGen;
import com.ec.customer.model.Orders;
import com.ec.customer.queue.OrdersQueueService;
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
@RequestMapping(path={"/order"})
public class OrderController extends BaseController {

	private   static final Logger   LOGGER = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	OrdersQueueService ordersQueueService;
	
	@Autowired
	OrdersService ordersService ;
	
	@Value("${security.controller.order.inser.rate.limit:10}")
	int rateLimit;
	
	@Value("${security.controller.order.inser.rate.unit:0/1 * * * * ?}")
	String rateUnit;
	
	
	protected static BlockingQueue<Orders> orderQueue = new LinkedBlockingQueue<Orders>();


	@RequestMapping(path={"/add"})
	public Response<String> addModelAttribute(@ModelAttribute Orders orders,HttpServletRequest request) {
		return addRequestBody(orders,request);
	}
	@RequestMapping(path={"/add"},produces = { "application/json" }, consumes = { "application/json" })
	public Response<String> addRequestBody(@RequestBody Orders orders,HttpServletRequest request) {
		
		if(
				orders.getCustomMobile()==null 
				|| orders.getCustomMobile().equals("")
				
				
				
				||orders.getDeliverAdderss()==null 
				||orders.getDeliverAdderss().equals("")
				
		){
			return new Response<String>(Response.Code.PARAMETER_MISS.getValue());
		}
		
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
	
	
	
	
	
	
	
	@Scheduled(cron = "${security.controller.order.inser.rate.unit:0/1 * * * * ?}")
	private void scheduler() {
		
		if(orderQueue.size()==0)return ;
		
		List<Orders> list=new ArrayList<>();
		orderQueue.drainTo(list);
		int converted=list.size()>rateLimit?-1:0;
		
		for(Orders orders:list){
			
			orders.setStatus(converted);
			
			orders.setRateUnit(rateUnit);
			orders.setRateVal(list.size());
			orders.setId(IDGen.next());
			orders.setCreatedate(new Date());
			orders.setCreatetime(new Date());
		}
	
		ordersService.insert(list);
	}
	
	
	


}
