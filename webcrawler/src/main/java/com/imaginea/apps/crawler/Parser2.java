package com.imaginea.apps.crawler;

import java.io.IOException;
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

@Component
public class Parser2 {
	
	@Autowired
	private DownloadsExecutorService exec;
	
	private WebClient client = new WebClient(BrowserVersion.CHROME);
	
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);

	public ArrayList<String> extractLinksForMonth(String pageLink) {
			
			ArrayList<?> anchorLinks = new ArrayList<>();
			ArrayList<String> links = new ArrayList<>();
				
			try 
			{
				HtmlPage page = client.getPage(pageLink);
				page.initialize();
				page.getWebClient().waitForBackgroundJavaScript(5000);
				
				boolean next = true;
				
				HtmlPage p = page;
				String pageUrl = p.getUrl().toString();
	
				String s = pageUrl.substring(0,pageUrl.lastIndexOf("/")+1);
				
				String mailbox = s;

				while(next) {				
					
					try 
					{			
						List list = extractLinksForPage(p);						
						links.addAll(list);						
						exec.addDownloadLinks(mailbox, list);
						
						HtmlAnchor a = p.getAnchorByText("Next »");
						p = (HtmlPage)a.click();						
						
						LOG.info("Total mail links for page --- are " + list.size());

					}
					
					catch(Exception e) 
					{
						next = false;
						e.printStackTrace();
					}
				}
				
			} 
			catch (FailingHttpStatusCodeException | IOException e1) 
			{
				e1.printStackTrace();
			}
			
						
			return links;
		}	
	
	
	private List extractLinksForPage(HtmlPage page) {
		
		HtmlPage p = page;
		p.getWebClient().waitForBackgroundJavaScript(5000);
		List list = p.getByXPath(".//*[@id[contains(string(),'msg-')]]/td[2]/a/@href");
		return list;
		
	}


	public ArrayList<String> extractMonthsForYear(String url, String year) {
		
		url = "http://mail-archives.apache.org/mod_mbox/maven-users/";
		
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
