package com.imaginea.apps.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.imaginea.apps.crawler.util.LinkDownloadThread;

@Component
public class Parser {
	
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);

	public ArrayList<String> extractLinksForMonth(HtmlPage pageLink) {
			
			ArrayList<?> anchorLinks = new ArrayList<>();
			ArrayList<String> links = new ArrayList<>();
				
			try 
			{
				HtmlPage page = pageLink;
				page.initialize();
				page.getWebClient().waitForBackgroundJavaScript(5000);
				
				boolean next = true;
				
				HtmlPage p = page;
				String pageUrl = p.getUrl().toString();
	
				while(next) {				
					
					try 
					{					
						HtmlAnchor a = p.getAnchorByText("Next »");
						p = (HtmlPage)a.click();
						p.getWebClient().waitForBackgroundJavaScript(5000);
						List list = p.getByXPath(".//*[@id[contains(string(),'msg-')]]/td[2]/a");
						anchorLinks.addAll(list);
					}
					
					catch(Exception e) 
					{
						next = false;
						e.printStackTrace();
					}
				}
				
				String s = pageUrl.substring(0,pageUrl.lastIndexOf("/")+1);
				
				for(Object link:anchorLinks) {
					
					links.add(s + ((HtmlAnchor) link).getAttribute("href"));
				}
	
			} 
			catch (FailingHttpStatusCodeException | IOException e1) 
			{
				e1.printStackTrace();
			}
			
			LOG.info("Total mail links for month --- are " + links.size());
						
			return links;
		}	
	
	
	public ArrayList<HtmlPage> extractMonthsForYear(String url, String year) {
		
		url = "http://mail-archives.apache.org/mod_mbox/maven-users/";
		
		ArrayList<HtmlPage> pageList = new ArrayList<>();
		
		WebClient client = new WebClient(BrowserVersion.CHROME);
		
		try 
		{
			HtmlPage mainPage = client.getPage(url);
			
			HtmlTable hTable = mainPage.getFirstByXPath("//table[@id=\"grid\"]/tbody/tr/td/table[.//text()[contains(.,'Year " + year +"')]]");
			DomNodeList<HtmlElement> monthsList = hTable.getElementsByTagName("a");
			
			for(HtmlElement month:monthsList) {
				HtmlPage page = month.click();
				page.getWebClient().waitForBackgroundJavaScript(5000);
				pageList.add(page);
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
