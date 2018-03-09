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
import com.ec.customer.model.Visitors;
import com.ec.customer.queue.VisitersQueueService;
import com.ec.customer.service.VisitorsService;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
@RestController
@EnableScheduling
@RequestMapping(path={"/visitor"})
public class VisitorController  extends BaseController {


	private   static final Logger   LOGGER = LoggerFactory.getLogger(VisitorController.class);

	@Autowired
	VisitersQueueService visitersQueueService;
	
	@Autowired
	VisitorsService visitorsService ;
	
	@Value("${security.controller.visiter.inser.rate.limit:10}")
	int rateLimit;
	
	@Value("${security.controller.visiter.inser.rate.unit:0/1 * * * * ?}")
	String rateUnit;
	
	protected static BlockingQueue<Visitors> orderQueue = new LinkedBlockingQueue<Visitors>();


	@RequestMapping(path={"/add"})
	public Response<String> addModelAttribute(@ModelAttribute Visitors visitors,HttpServletRequest request) {
		return addRequestBody(visitors,request);
	}
	@RequestMapping(path={"/add"},produces = { "application/json" }, consumes = { "application/json" })
	public Response<String> addRequestBody(@RequestBody Visitors visitors,HttpServletRequest request) {
		
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));   
		Browser browser = userAgent.getBrowser();    
		OperatingSystem os = userAgent.getOperatingSystem();  
		
		visitors.setBrowserName(browser.getName());
		visitors.setBrowserOs(os.getName());
		visitors.setCreateip(getRemoteAddr(request));
		orderQueue.offer(visitors);
		return new Response<String>(Response.Code.SUCCESS.getValue());
	}
	
	
	
	
	
	
	
	@Scheduled(cron = "${security.controller.visiter.inser.rate.unit:0/1 * * * * ?}")
	private void scheduler() {
		
		if(orderQueue.size()==0)return ;
		
		List<Visitors> list=new ArrayList<>();
		orderQueue.drainTo(list);
		int converted=list.size()>rateLimit?-1:0;
		
		for(Visitors visitors:list){
			visitors.setStatus(converted);
			visitors.setRateUnit(rateUnit);
			visitors.setRateVal(list.size());
			visitors.setCreatedate(new Date());
		}
	
		visitorsService.insert(list);
	}
	
	
	



}
