package com.sample.crawler;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
 * Test class to test various String type methods of the class
 * are being properly handled like url encoding, method returns, data values
 * 
 */

public class LinkDownloaderThreadTest {
	
	private LinkDownloadThread linkDownloader;
	private String mailUrl;
	MailObject mail_object;

	@Before
	public void setUp() throws Exception {
		mailUrl = "http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/%3C547C1A5F.7070709%40uni-jena.de%3E";
		linkDownloader = new LinkDownloadThread(mailUrl);
		mail_object = linkDownloader.call();		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {
		
		assertEquals("http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/<547C1A5F.7070709%40uni-jena.de>", linkDownloader.getUrl(mailUrl));
		assertEquals(String.class, linkDownloader.getSaveFileName(new MailObject("","","","",new Date())).getClass());
		Thread.sleep(15000);
		assertEquals("Thomas Scheffler <thomas.scheff...@uni-jena.de>", mail_object.from );
		
	}

}
