package com.imaginea.test.webcrawler;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.imaginea.apps.crawler.util.LinkDownloadThread;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:**spring-test.xml"})
public class AllTestsRunner {
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);

	public static void main(String[] args) {
	      Result result = JUnitCore.runClasses(AllTests.class);
	      for (Failure failure : result.getFailures()) {
	         LOG.info(failure.getDescription().toString());
	      }
	      
	      if(result.wasSuccessful())	     
	    	  LOG.info("true");
	      
	      else	    	  
	    	  LOG.info("false");
	      
	   }

}