package com.imaginea.test.webcrawler;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.imaginea.apps.crawler.Parser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:**spring-test.xml"})
@Component("MailCrawler_ParseTest")
public class MailCrawler_ParseTest {

	@Autowired
	private Parser parser;
	
	private String url ;
	private String year;
	
	@Before
	public void setUp() throws Exception {
		url = "http://mail-archives.apache.org/mod_mbox/maven-users/";
		year = "2014";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Throwable {
	
		assertEquals("[http://mail-archives.apache.org/mod_mbox/maven-users/201412.mbox/browser, "
				+ "http://mail-archives.apache.org/mod_mbox/maven-users/201411.mbox/browser, "
				+ "http://mail-archives.apache.org/mod_mbox/maven-users/201410.mbox/browser, "
				+ "http://mail-archives.apache.org/mod_mbox/maven-users/201409.mbox/browser, "
				+ "http://mail-archives.apache.org/mod_mbox/maven-users/201408.mbox/browser, "
				+ "http://mail-archives.apache.org/mod_mbox/maven-users/201407.mbox/browser, "
				+ "http://mail-archives.apache.org/mod_mbox/maven-users/201406.mbox/browser, "
				+ "http://mail-archives.apache.org/mod_mbox/maven-users/201405.mbox/browser, "
				+ "http://mail-archives.apache.org/mod_mbox/maven-users/201404.mbox/browser, "
				+ "http://mail-archives.apache.org/mod_mbox/maven-users/201403.mbox/browser, "
				+ "http://mail-archives.apache.org/mod_mbox/maven-users/201402.mbox/browser, "
				+ "http://mail-archives.apache.org/mod_mbox/maven-users/201401.mbox/browser]", parser.extractMonthsForYear(url, year).toString());
		
		assertEquals(ArrayList.class, parser.extractLinksForMonth(url+"201412.mbox/").getClass());
	}

}
