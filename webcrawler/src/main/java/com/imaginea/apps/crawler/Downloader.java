package com.imaginea.apps.crawler;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.imaginea.apps.crawler.util.DownloadWorker;
import com.imaginea.apps.crawler.util.LinkDownloadThread;

@Component
public class Downloader {
	
	@Autowired
	private ApplicationContext ctx;
	
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);

	public void download(ArrayList<String> links) {
		
		LOG.info("Total Mails to Download:" + links.size());
		
		
		//links.forEach((string)->((DownloadWorker) ctx.getBean("DownloadWorker")).setDownloadLink(string));
		
		for(String link:links) {
			DownloadWorker worker = (DownloadWorker) ctx.getBean("DownloadWorker");
			worker.setDownloadLink(link);
			worker.execute();
		}
	}

}
