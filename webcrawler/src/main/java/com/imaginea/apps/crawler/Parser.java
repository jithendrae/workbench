package com.imaginea.apps.crawler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.imaginea.apps.crawler.util.DownloadsExecutorService;
import com.imaginea.apps.crawler.util.LinkDownloadThread;

/*
 * This class parses the crawled information generated by MailCrawler
 * and extracts relevant links for downloading 
 */
@Component("Parser")
public class Parser {
	
	@Autowired
	private DownloadsExecutorService exec;
	
	private WebClient client = new WebClient(BrowserVersion.CHROME);
	
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);

	public ArrayList<String> extractLinksForMonth(String pageLink) throws Throwable {
			
			ArrayList<?> anchorLinks = new ArrayList<>();
			ArrayList<String> links = new ArrayList<>();
				
				HtmlPage page = client.getPage(pageLink);
				page.initialize();
				page.getWebClient().waitForBackgroundJavaScript(5000);
				page.getWebClient().getOptions().setThrowExceptionOnFailingStatusCode(true);				
				boolean next = true;				
				String pageUrl = page.getUrl().toString();	
				String s = pageUrl.substring(0,pageUrl.lastIndexOf("/")+1);				
				String mailbox = s;				
				HtmlPage p = page;

				while(next) 
				{					
							
					new URL(p.getUrl().toExternalForm()).openConnection().getContent();			
					
					List list = extractLinksForPage(p);						
					//links.addAll(list);						
					exec.addDownloadLinks(mailbox, list);
					
					try
					{
						p = getNextPaginatedPage(p);					
					}
					
					catch(Exception e)
					{
						next = false;
					}						
					
					LOG.info("Total mail links for page --- are " + list.size());			
					
				}			
						
			return links;
		}	
	
	
	private List extractLinksForPage(HtmlPage page) throws Exception{
		
		
		page.getWebClient().waitForBackgroundJavaScript(5000);
		List list = page.getByXPath(".//*[@id[contains(string(),'msg-')]]/td[2]/a/@href");
		return list;
		
	}
	
	private HtmlPage getNextPaginatedPage(HtmlPage p) throws IOException{
		
		HtmlAnchor a = p.getAnchorByText("Next »");		
		return (HtmlPage) a.click();		
	}


	public ArrayList<String> extractMonthsForYear(String url, String year) {
				
		ArrayList<String> pageList = new ArrayList<>();		
		
		try 
		{
			HtmlPage mainPage = client.getPage(url);
			
			HtmlTable hTable = mainPage.getFirstByXPath("//table[@id=\"grid\"]/tbody/tr/td/table[.//text()[contains(.,'Year " + year +"')]]");
			DomNodeList<HtmlElement> monthsList = hTable.getElementsByTagName("a");
			
			for(HtmlElement month:monthsList) {
				HtmlPage page = month.click();
				page.getWebClient().waitForBackgroundJavaScript(5000);
				pageList.add(page.getUrl().toString());
			}			
		} 
		catch (FailingHttpStatusCodeException | IOException e) 
		{
			e.printStackTrace();
		}	
		
		LOG.info("Total No of Months to be parsed are: " + pageList.size());
		
		return pageList;
	}
}
