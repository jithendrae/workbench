package com.imaginea.apps.crawler.util;

import java.util.ArrayList;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imaginea.apps.crawler.MailCrawler;

@Component
@Aspect
public class SavePointCommitInterceptor {
	
	@Autowired
	CommitManager manager;
	
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);
	
	@Autowired
	MailCrawler crawler;
	
	@AfterReturning(
		      pointcut = "execution(* com.imaginea.apps.crawler.MailCrawler.setArgs(..))", returning="result")
	 
	public void afterSetArgs(JoinPoint joinPoint, Object result){
		 			
		manager.setWorkflowStatus("setArgs", true);
	}
	
	@AfterReturning(
		      pointcut = "execution(* com.imaginea.apps.crawler.MailCrawler.crawl(..))", returning="result")
	 
	public void afterCrawl(JoinPoint joinPoint, Object result){
		 				
		manager.setWorkflowStatus("crawl", true);

	}
	
	@AfterReturning(
		      pointcut = "execution(* com.imaginea.apps.crawler.MailCrawler.parse(..))", returning="result")
	 
	public void afterParse(JoinPoint joinPoint, Object result){
		 				 
		manager.setWorkflowStatus("parse", true);

	}
	
	 @AfterReturning(
		      pointcut = "execution(* com.imaginea.apps.crawler.Parser2.extractMonthsForYear(..))", returning="result")
	 
	public void afterStep1(JoinPoint joinPoint, Object result){
		 			
			manager.setWorkflowStatus("Step1", true);
			
			ArrayList list = (ArrayList) result;
			
			manager.setUpStep1(list);		

	}
	 
	 @AfterReturning(
		      pointcut = "execution(* com.imaginea.apps.crawler.Parser2.extractLinksForMonth(..))", returning="result")
	 
	public void afterStep2(JoinPoint joinPoint, Object result){
		 				 
			manager.setWorkflowStatus("Step2", true);
			
			ArrayList list = (ArrayList) result;
			
			//MethodSignature signature = (MethodSignature)joinPoint.getSignature();
			//String[] parameterNames = signature.getParameterNames();
			Object[] parameterValues = joinPoint.getArgs();

			manager.setUpStep2((String) parameterValues[0], list);

	}

	/* @AfterReturning(
		      pointcut = "execution(* com.imaginea.apps.crawler.util.Parser2.extractLinksForPage(..))", returning="result")
	 
	public void afterStep3(JoinPoint joinPoint, Object result){
		 				 
			manager.setWorkflowStatus("Step3", true);

	}*/
 

}