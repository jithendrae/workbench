package com.imaginea.apps.crawler.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/*
 * The DownloadWorker is the worker class for the DownloadsExecutorService that 
 * creates the worker threads which will be used for downloading the mails 
 */

@Component("DownloadWorker")
@Scope("prototype")

public class DownloadWorker {
	
	private Callable<?> c;
	private String url;
	
    @Autowired
    private LinkDownloadThread thread;
	
	public void setDownloadLink(String link) {		
		
		url = link;
		thread.setDownloadUrl(url);
		c = thread;		
	}
	
	@Async
	public Entry<String,Boolean> execute(){		
		
		Boolean status = false;
		
			try 
			{
				status = (Boolean) c.call();
			} 
			
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			Map.Entry<String,Boolean> entry =
				    new AbstractMap.SimpleEntry<String, Boolean>(url, status);
			
			return entry;		
	}
 
}
