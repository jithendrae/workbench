package com.imaginea.apps.crawler.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.html.DomAttr;

@Component("DownloadsExecutorService")
public class DownloadsExecutorService {

	private ThreadPoolTaskExecutor exec;

	
	@Autowired
	private ApplicationContext ctx;	
	
	private ConcurrentLinkedQueue<Future> futs = new ConcurrentLinkedQueue<>();

	public void setExecutorProfile(){
		exec = new ThreadPoolTaskExecutor();
		exec.setKeepAliveSeconds(30);
		exec.setQueueCapacity(300);
		exec.setCorePoolSize(200);
		exec.setMaxPoolSize(200);
		exec.initialize();
		exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	public void addDownloadLinks(String link){
		
	}
	
	public void addDownloadLinks(ArrayList<String> list){
		
		for(String link:list){
			LinkDownloadThread worker = (LinkDownloadThread) ctx.getBean("LinkDownloadThread");
			worker.setDownloadUrl(link);
			futs.add(exec.submit(worker));			
		}
	}
	
	public void addDownloadLinks(String mailbox, List<DomAttr> list){
		
		for(DomAttr link:list){
			LinkDownloadThread worker = (LinkDownloadThread) ctx.getBean("LinkDownloadThread");
			worker.setDownloadUrl(mailbox + link.getTextContent());
			futs.add(exec.submit(worker));			
		}
	}
	
	public void shutdownExecutor(){
		
		try
		{
			exec.setWaitForTasksToCompleteOnShutdown(false);
			exec.getThreadPoolExecutor().awaitTermination(10, TimeUnit.SECONDS);	
			exec.shutdown();
		} 
		catch (IllegalStateException | InterruptedException e) 
		{
			e.printStackTrace();
		}
	}


}
