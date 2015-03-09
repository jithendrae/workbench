package com.sample.crawler;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
 * Test class to test if any links are extracted and are able to be saved to directories * 
 */

public class LinksExtractorTest {
	
	private String url, url_full_with_sitemap, mail_year_to_download;
	
	
	@Before
	public void setUp() throws Exception {		
 
		url = "http://mail-archives.apache.org/mod_mbox/httpd-announce/";
		url_full_with_sitemap = url + "?format=sitemap";
		mail_year_to_download = "2014";
	}

	@After
	public void tearDown() throws Exception {
		
	}
	
	
	/*
	 * Checks if mails folder is being created after a while so that mails are being stored	 * 
	 */

	@Test
	public void test() throws InterruptedException {	
		
		LinksExtractor obj = new LinksExtractor(url_full_with_sitemap, mail_year_to_download);					
		obj.start();		
		new Thread(new LinksDownloader(obj)).start();	

		Thread.sleep(1000*3);
		
		assertEquals(true , Files.isDirectory(Paths.get("E:\\mails")));
	}

}
