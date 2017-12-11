package com.ec.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import com.ec.common.model.Response;
import com.ec.common.utils.IDGen;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

@ConditionalOnProperty(prefix = "security", name = "filter", matchIfMissing = true)
@Configuration
@EnableWebMvc
public class ApplicationAuth extends org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationAuth.class);

	@Value("${security.filter.ip.limits:10}")
	Integer ipLimits;

	@Value("${security.filter.admin.path}")
	String adminPath;

	private static final Map<String,AtomicInteger> IP_MAP=new HashMap<>();
	
	private boolean authorityIP(HttpServletRequest request,HttpServletResponse response) throws IOException {
		if(request.getRequestURI().indexOf(adminPath)>-1)
			return true;
		
		String ip=request.getRemoteAddr();
		if(!IP_MAP.containsKey(ip))
			IP_MAP.put(ip, new AtomicInteger(0));
		
		
		if(IP_MAP.get(ip).incrementAndGet()<=ipLimits)
			return true;
		
		response.getWriter().println(new Response(Response.Code.PER_IP_LIMIT.getValue(),"PER-IP SESSION LIMIT "+ipLimits).toString());
		LOGGER.warn("PER-IP SESSION LIMIT "+request.getRemoteAddr());
		return false;
	}

	private boolean authorityLogin(HttpServletRequest request,HttpServletResponse response) throws IOException {
		if(request.getRequestURI().indexOf(adminPath)<0)return true;
		if(request.getSession().getAttribute(ApplicationAttribute.AUTHORIZED_ID) != null)return true;
		response.getWriter().println(new Response(Response.Code.NOT_LOGIN.getValue(),"NOT LOGIN").toString());
		LOGGER.warn("NOT LOGIN "+request.getRemoteAddr());
		return false;
	}
	
	private boolean authorityBrowser(HttpServletRequest request,HttpServletResponse response) throws IOException{
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));   
		Browser browser = userAgent.getBrowser();    
		OperatingSystem os = userAgent.getOperatingSystem();  
		
		if(browser.getBrowserType().toString().toUpperCase().indexOf("BROWSER")>-1)return true;
		response.getWriter().println(new Response(Response.Code.NOT_BROWSER.getValue(),"NOT BROWSER").toString());
		LOGGER.warn("NOT BROWSER "+request.getRemoteAddr());
		return false;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authenticationInterceptor()).addPathPatterns("/**"); // ������������ͨ���ж��Ƿ���
		super.addInterceptors(registry);
	}
	
	@Bean
    public HandlerInterceptor authenticationInterceptor() {
		
		return new HandlerInterceptor() {
			
			@Override
			public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object arg2) throws Exception {
				
				res.setHeader("Access-Control-Allow-Origin",req.getHeader("Origin"));  
				res.setHeader("Access-Control-Allow-Credentials", "true");  
				res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");  
				res.setHeader("Access-Control-Max-Age", "3600");  
				res.setHeader("Access-Control-Allow-Headers", "x-requested-with");  
		        
				return authorityIP(req,res)&&authorityLogin(req,res)&&authorityBrowser(req,res);
			}
			
			@Override
			public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
					throws Exception {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
					throws Exception {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	@Scheduled(cron = "${security.filter.ip.limits.clean.cron:0 1 0 * * ?}")
	private void scheduler() {
		LOGGER.info("CLEAN IP LIMIT:"+IP_MAP.size());
		IP_MAP.clear();
	}

}
