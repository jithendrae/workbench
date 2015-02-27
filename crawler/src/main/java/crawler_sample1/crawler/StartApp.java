package crawler_sample1.crawler;

import java.io.IOException;
import java.net.MalformedURLException;

public class StartApp {

	public static void main(String args[]) throws MalformedURLException, IOException {

		System.out.println("Downloading Apache mail archieve: http://mail-archives.apache.org/mod_mbox/httpd-announce/ for the year : 2014");

		LinksExtractor obj = LinksExtractor.getInstance();
		new Thread(obj).start();
	}

}
