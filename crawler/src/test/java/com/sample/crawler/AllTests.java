package com.sample.crawler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AppTest.class, LinkDownloaderThreadTest.class,
		LinksExtractorTest.class })
public class AllTests {

}
