package com.sample.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class LinksExtractor extends Thread {
	
	private String url;
	private String year;
	private boolean moreLinksToDownload = true;

	static ArrayList<String> downloadableLinks;
	
	static final Logger LOG = LoggerFactory.getLogger(LinksExtractor.class);

	public LinksExtractor(String url_full_with_sitemap, String mail_year_to_download) {

		url = url_full_with_sitemap;
		year = mail_year_to_download;
	}

	public synchronized void updateDownloadLinks(ArrayList<String> newDownloadableLinks) {

		downloadableLinks.clear();
		downloadableLinks = newDownloadableLinks;
	}

	public synchronized ArrayList<String> getDownloadLinks() {
		return downloadableLinks;

	}

	@Override
	public void run() {
		
		LOG.info("Starting to download sitemap index for the provided mailbox");

		downloadableLinks = new ArrayList<String>();
		
		try 
		{
			URLConnection ucon = new URL(url).openConnection();

	        SAXParserFactory factory = SAXParserFactory.newInstance();	      
            factory.setValidating(false);

            InputStream xmlInput  = ucon.getInputStream();            

            SAXParser saxParser = factory.newSAXParser();
            SaxHandler handler   = new SaxHandler(year);
            
            saxParser.parse(xmlInput, handler);
                        
            LOG.info("Total mails to download for the year " + year + " are " + handler.extractedUrls.size() );
            
            int i = 0;
            
            while(moreLinksToDownload){
            	
            	for (; i < handler.extractedUrls.size(); i++) {
    				
    				downloadableLinks.add(handler.extractedUrls.get(i));
    				
    				if (downloadableLinks != null)

    					synchronized (downloadableLinks) {

    						if (i % 100 == 0 && i != 0)
    							downloadableLinks.wait();
    					}
    			}
    			
				synchronized (downloadableLinks) {
					
					if(downloadableLinks.size()<1)
		    			moreLinksToDownload = false;
					
					else
						downloadableLinks.wait();

				}
            	
            }			
			
		}

		catch (ParserConfigurationException | SAXException | IOException | InterruptedException e) {
			LOG.error(e.getMessage());
			moreLinksToDownload = false;
		}

	}

	public synchronized boolean getLinksRemainingStatus() {
		return moreLinksToDownload;
	}

}
