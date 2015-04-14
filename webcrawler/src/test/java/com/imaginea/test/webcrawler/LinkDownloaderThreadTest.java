package com.imaginea.test.webcrawler;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.imaginea.apps.crawler.util.LinkDownloadThread;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:**spring-test.xml"})
@Component("LinkDownloaderThreadTest")
public class LinkDownloaderThreadTest {

	@Autowired
	LinkDownloadThread linkDownloader;
	
	private String mailUrl;
	
	@Before
	public void setUp() throws Exception {
		
		mailUrl = "http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/%3C547C1A5F.7070709%40uni-jena.de%3E";
		linkDownloader.setDownloadUrl(mailUrl);
		linkDownloader.setLinkStatus("new");
		linkDownloader.call();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		assertEquals("http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/<547C1A5F.7070709%40uni-jena.de>", linkDownloader.getUrl(mailUrl));
		assertEquals(true, linkDownloader.call());
	}

}