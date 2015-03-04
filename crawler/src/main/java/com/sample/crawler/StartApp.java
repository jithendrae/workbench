package com.sample.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartApp {

	static final Logger LOG = LoggerFactory.getLogger(StartApp.class);
	
	public static void main(String args[]) throws MalformedURLException, IOException {
		
		LOG.info("Provide the Apache mail archieve url to download");
		
		Scanner inputReader = new Scanner(System.in);
		
		String url = inputReader.nextLine();
		
		LOG.info("Provide the archiving  year from which to download the mails from");
		
		String mail_year_to_download = inputReader.nextLine();

		String url_full_with_sitemap = url + "?format=sitemap";
		
		LinksExtractor obj = new LinksExtractor(url_full_with_sitemap, mail_year_to_download);
					
		obj.start();
		
		new Thread(new LinksDownloader(obj)).start();
		
		inputReader.close();
				
	}

}
