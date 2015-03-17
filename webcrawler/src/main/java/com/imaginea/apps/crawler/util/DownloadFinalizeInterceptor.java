/*package com.imaginea.apps.crawler.util;

import java.util.Map.Entry;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imaginea.apps.crawler.MailCrawler;

@Component
@Aspect
public class DownloadFinalizeInterceptor {
	
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);
	
	@Autowired
	MailCrawler crawler;
	
	 @AfterReturning(
		      pointcut = "execution(* com.imaginea.apps.crawler.util.DownloadWorker.execute(..))", returning="result")
	 
	public void afterDownloadSuccess(JoinPoint joinPoint, Object result){
		 		
		 Entry<String, Boolean> m = (Entry) result;
		 
		 if((Boolean)m.getValue() == true)
			 crawler.updateLinksSuccessful((String)m.getKey());
		 
		 else
			 crawler.updateLinksFailed((String)m.getKey());
		 
	}
	 
	 @AfterThrowing(
		      pointcut = "execution(* com.imaginea.apps.crawler.util.DownloadWorker.execute(..))", throwing="error")
	 
	public void onDownloadFailure(JoinPoint joinPoint, Throwable error){
		
		  LOG.error(joinPoint.getTarget().toString() + "\n" + error);
	}

}*/