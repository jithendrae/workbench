package com.imaginea.apps;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.imaginea.apps.crawler.MailCrawler;

public class CrawlerImpl {
	
	@Autowired
	private ApplicationContext ctx;
	
	public static void main(String args[]) {		
		
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/spring-config.xml");
        MailCrawler crawler = (MailCrawler) ctx.getBean("MailCrawler");

		crawler.setArgs("http://mail-archives.apache.org/mod_mbox/maven-users/","2014",null);
		crawler.crawl();
	}

}
