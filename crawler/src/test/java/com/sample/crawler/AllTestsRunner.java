package com.sample.crawler;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllTestsRunner {
	static final Logger LOG = LoggerFactory.getLogger(LinksExtractor.class);

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
