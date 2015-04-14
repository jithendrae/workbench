package com.imaginea.apps.crawler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.imaginea.apps.crawler.util.CommitManager;
import com.imaginea.apps.crawler.util.LinkDownloadThread;

/*
 * MailCrawler is the implementation of the application interface. 
 * It implements the service layer methods of the application and 
 * is therefore the service class that include the application's workflow
 */

@Component("MailCrawler")
public class MailCrawler implements Crawler{
	
	@Autowired
	private Parser parser;	
	
	@Autowired
	private CommitManager manager;
	
	private String url;
	private String year = "2014";
	
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);

	public void crawl() {
		
		manager.setExecutorProfile();
		ArrayList list = new ArrayList();
		
		if(manager.canResume()){
			manager.doResume();
		}
		
		else{
			list = parse(url,year);
		}	
		
		manager.shutdownExecutor();
	}

	private ArrayList parse(String url, String year) {

		ArrayList<String> pageList = parser.extractMonthsForYear(url,year);
		ArrayList linksList = new ArrayList();
		for(String monthLink:pageList) {
			try 
			{
				linksList.addAll(parser.extractLinksForMonth(monthLink));
			} 
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return linksList;
	}
	
	public void setArgs(String url, String year, String logLevel) {
		
		this.url = url;
		this.year = year;
		
		if(logLevel!=null && !logLevel.isEmpty())
			setApplicationLogConfiguration(logLevel);
	}

	
	private void setApplicationLogConfiguration(String logLevel){
			
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder = null;
		try 
		{
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(this.getClass().getResource("/logback.xml").getPath());
			
			Node root = document.getElementsByTagName("root").item(0);
			NamedNodeMap attr = root.getAttributes();
			Node nodeAttr = attr.getNamedItem("level");
			nodeAttr.setTextContent(logLevel);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			File f = new File(this.getClass().getResource("/logback.xml").getPath());
			StreamResult result = new StreamResult(f);
			transformer.transform(source, result);
			
			LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory(); 
			JoranConfigurator configurator = new JoranConfigurator();
			 
		    configurator.setContext(context);
		      // Call context.reset() to clear any previous configuration, e.g. default 
		      // configuration. For multi-step configuration, omit calling context.reset().
		    context.reset(); 
		    configurator.doConfigure(f);
		}
		
		catch (ParserConfigurationException | SAXException | TransformerException | JoranException | IOException e) 
		{
			LOG.warn("Exception setting Log level \n" + e.getLocalizedMessage() + " LOG level set to default: INFO");
		}
	}
	
	public void resume_crawl(List<String> downloadResumeLinks){
		
		ArrayList list = resume_parse(downloadResumeLinks);
		
	}
	
	public ArrayList resume_parse(List<String> downloadResumableLinks){
		
		ArrayList linksList = new ArrayList();
		
		for(String monthLink:downloadResumableLinks)
			
			try 
			{
				linksList.addAll(parser.extractLinksForMonth(monthLink));
			} 
			catch (Throwable e) 
			{
				e.printStackTrace();
			}
		
		return linksList;
	}

}
