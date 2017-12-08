package com.ec.orders.queue;

import org.springframework.stereotype.Component;

import com.ec.common.service.QueueExec;
import com.ec.common.service.QueueService;
@Component
public class OrdersQueueService extends QueueService<QueueExec<?>>{

	
	@Override
	public String getNamespace() {
		return "orders-queue";
	}

	@Override
	protected int getMaxPriorityThreads() {
		return 1;
	}

	@Override
	protected int getPoolSize() {
		return 1;
	}
	
	@Override
	protected void dequeue(QueueExec<?> exec) throws Exception {
		exec.exec();
	}

}
