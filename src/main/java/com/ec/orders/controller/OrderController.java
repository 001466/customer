package com.ec.orders.controller;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ec.common.controller.BaseController;
import com.ec.common.model.Response;
import com.ec.common.service.QueueExec;
import com.ec.common.utils.IDGen;
import com.ec.orders.model.Orders;
import com.ec.orders.model.OrdersExample;
import com.ec.orders.queue.OrdersQueueService;
import com.ec.orders.service.OrdersService;


/**
 *
 * @author winall
 *
 */
@RestController
@RequestMapping(value = "/order")
public class OrderController extends BaseController {

	
	@Autowired
	OrdersQueueService ordersQueueService;
	
	@Autowired
	OrdersService ordersService ;

	@RequestMapping(method=RequestMethod.POST)
	public Response<String> addModelAttribute(@ModelAttribute Orders orders,HttpServletRequest request) {
		return addRequestBody(orders,request);
	}
	@RequestMapping(method=RequestMethod.POST,produces = { "application/json" }, consumes = { "application/json" })
	public Response<String> addRequestBody(@RequestBody Orders orders,HttpServletRequest request) {
		ordersQueueService.enqueue(new QueueExec<Orders>(orders) {
			@Override
			public void exec() {
				
				orders.setId(IDGen.next());
				orders.setCreatedate(new Date());
				orders.setCreatetime(new Date());
				orders.setCreateip(request.getRemoteAddr());
				
				ordersService.insert(orders);
			}
		});
		return new Response<String>(Response.Code.SUCCESS.getValue());
	}
	
	
	
	
	@RequestMapping(method=RequestMethod.PUT)
	public Response<String> modModelAttribute(@ModelAttribute Orders orders,HttpServletRequest request) {
		return modRequestBody(orders,request);
	}
	@RequestMapping(method=RequestMethod.PUT,produces = { "application/json" }, consumes = { "application/json" })
	public Response<String> modRequestBody(@RequestBody Orders orders,HttpServletRequest request) {
		ordersQueueService.enqueue(new QueueExec<Orders>(orders) {
			@Override
			public void exec() {
				orders.setUpdatetime(new Date());
				ordersService.update(orders);
			}
		});
		return new Response<String>(Response.Code.SUCCESS.getValue());
	}
	
	
	
	@RequestMapping(method = { RequestMethod.GET })
	public Response<List<Orders>> get(HttpServletRequest request) throws Exception {
		Response<List<Orders>> res=new Response<List<Orders>>(Response.Code.SUCCESS.getValue());
		
		ordersQueueService.enqueue(new QueueExec<HttpServletRequest>(request) {
			@Override
			public void exec() {

				OrdersExample example=new OrdersExample();
				example.createCriteria().andCreateipEqualTo(request.getRemoteAddr());
				res.setData(ordersService.select(example));
				res.setMessage(request.getRemoteAddr());

				LockSupport.unpark(this.getThread());
				
			}
		});
		LockSupport.park(Thread.currentThread());
		return res;
		
		
	}
	


}
