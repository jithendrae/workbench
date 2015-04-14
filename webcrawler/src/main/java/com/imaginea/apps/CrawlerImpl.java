package com.imaginea.apps;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.imaginea.apps.crawler.MailCrawler;

/*
 * This is the application main class for the Apache Mail Archive Web Crawler 
 * 
 * author: jithendrae
 */

public class CrawlerImpl {
	
	@Autowired
	private ApplicationContext ctx;
	
	public static void main(String args[]) {		
		
        @SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/spring-config.xml");
        MailCrawler crawler = (MailCrawler) ctx.getBean("MailCrawler");
        
        System.out.println("Provide the Apache mail archieve url to download");		
		Scanner inputReader = new Scanner(System.in);		
		String url = inputReader.nextLine();		
		System.out.println("Provide the archiving  year from which to download the mails from");		
		String mail_year = inputReader.nextLine();
		
		inputReader.close();
		
		crawler.setArgs(url, mail_year, null);
		crawler.crawl();
	}

}
