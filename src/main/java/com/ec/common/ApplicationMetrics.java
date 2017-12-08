package com.ec.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.ec.common.service.QueueService;

@Component
@ConditionalOnClass({ Servlet.class, Tomcat.class })
@ConditionalOnWebApplication
public class ApplicationMetrics implements PublicMetrics, ApplicationContextAware {

	private ApplicationContext applicationContext;


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public Collection<Metric<?>> metrics() {
		
		if (this.applicationContext instanceof EmbeddedWebApplicationContext) {
			
			EmbeddedServletContainer embeddedServletContainer = ((EmbeddedWebApplicationContext) applicationContext).getEmbeddedServletContainer();
			
			if (embeddedServletContainer instanceof TomcatEmbeddedServletContainer) {
				
				Connector connector = ((TomcatEmbeddedServletContainer) embeddedServletContainer).getTomcat().getConnector();
				
				ProtocolHandler handler = connector.getProtocolHandler();
				
				org.apache.tomcat.util.threads.ThreadPoolExecutor executor = (org.apache.tomcat.util.threads.ThreadPoolExecutor) handler.getExecutor();

				List<Metric<?>> metrics = new ArrayList<Metric<?>>();

				metrics.add(new Metric<Integer>("tomcat.queue_size", executor.getQueue().size()));
				metrics.add(new Metric<Integer>("tomcat.threads.active_count", executor.getActiveCount()));
				metrics.add(new Metric<Long>("tomcat.threads.task_count", executor.getTaskCount()));
				metrics.add(new Metric<Long>("tomcat.threads.completed_task_count", executor.getCompletedTaskCount()));
				metrics.add(new Metric<Integer>("tomcat.threads.submitted_count", executor.getSubmittedCount()));

				
				Map<String, QueueService> queueServiceMap=applicationContext.getBeansOfType(QueueService.class, false, true);
				for(QueueService qs:queueServiceMap.values()){
					metrics.add(new Metric<Integer>("queue_size."+qs.getNamespace().toLowerCase(),qs.getQueueSize()));
				}  
				metrics.add(new Metric<Integer>("queue_size.tomcat",executor.getQueue().size()));
				return metrics;
			}
		}
		
		return Collections.emptySet();
	}


}
