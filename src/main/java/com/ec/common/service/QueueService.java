package com.ec.common.service;


import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;



/**
 * QueueService abstract class
 * 
 * @author Easily
 *
 * @param <T>
 */
public abstract class QueueService<T extends QueueExec<?>> implements InitializingBean,DisposableBean{

	private   static final Logger   LOGGER = LoggerFactory.getLogger(QueueService.class);

	protected BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
	
	public int getQueueSize(){
		return queue.size();
	}
	
	protected ExecutorService executor;
	
	public abstract String getNamespace();
	
	protected abstract int getMaxPriorityThreads() ;

	protected abstract int getPoolSize() ;

	protected abstract void dequeue (T t) throws Exception;
	
	@Override
	public void afterPropertiesSet() throws Exception {

		this.executor = Executors.newFixedThreadPool(getPoolSize(), new ThreadFactory() {
			int pz=0;
			int mp=getMaxPriorityThreads();
			@Override
			public Thread newThread(Runnable r) {
				
				Thread t = new Thread(r, getNamespace() + --pz);

				if (getMaxPriorityThreads() > 0) {
					t.setPriority(Thread.MAX_PRIORITY);
					mp--;
				}
				
				t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					
					@Override
					public void uncaughtException(Thread t, Throwable ex) {
						LOGGER.error(ex.getMessage(),ex);
						executor.execute(new DequeueTask());
					}
					
				});
				
				if(LOGGER.isInfoEnabled())LOGGER.info("Initializing Thread:"+t.getName()+",Priority:"+t.getPriority());
				
				return t;
				
			}
		});
		
		
		if(LOGGER.isInfoEnabled())LOGGER.info("Initializing ExecutorService:"+getNamespace());
		
		int pz=getPoolSize();
		while (pz > 0) {
			executor.execute(new DequeueTask());
			pz--;
		}
	
	}
	
	@Override
	public void destroy() throws Exception {
		this.executor.shutdown();
		if(LOGGER.isInfoEnabled())LOGGER.info("Destroy ExecutorService:"+getNamespace());
	}
	
	
	/**
	 * Enqueue,call by controller
	 * 
	 * @param e
	 * @return
	 */
	public boolean enqueue(T t) {
		
		
		boolean enqueue= this.queue.offer(t);
		
		if(!enqueue){
			LOGGER.error("Queue offer error!");
			throw new RuntimeException("Queue offer failed,queue size="+this.queue.size()+", params="+t);
		}
		
		return enqueue;

	}
	
	
	/**
	 * @author winall
	 *
	 */
	private class DequeueTask implements Runnable{

		@Override
		public void run() {
			while (!Thread.interrupted()) {
				try {
					
					T t = queue.take();
						
					
					dequeue(t);
					 
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	
}





