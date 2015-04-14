package com.imaginea.apps.crawler;

import org.springframework.stereotype.Service;

/*
 * The base service interface for the application which defines the 
 * list of methods for the application 
 */

@Service
public interface Crawler {
	
	public void crawl();

}
