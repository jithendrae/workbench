package com.imaginea.apps.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.imaginea.apps.crawler.util.CommitManager;
import com.imaginea.apps.crawler.util.DownloadsExecutorService;
import com.imaginea.apps.crawler.util.LinkDownloadThread;

@Component("MailCrawler")
public class MailCrawler implements Crawler{
	
	@Autowired
	private Parser2 parser;
	
	@Autowired 
	private Downloader downloader;	
	
	@Autowired
	DownloadsExecutorService exec;
	
	@Autowired
	CommitManager manager;
	
	private ArrayList<String> links = new ArrayList<>();
	private String url;
	private String year = "2014";
	
	private Set<String> linksCompleted = new HashSet<>();
	private Set<String> linksFailed = new HashSet<>();
	
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);

	public void crawl() {
		
		if(manager.canResume()){
			manager.doResume();
		}
		
		else{
			exec.setExecutorProfile();
			links = parse(url,year);
			//download(links);
		}	
		
		manager.setResumeStatus();
		
		if(manager.canResume()){			
			manager.serialize();
		}	
		

	}

	/*private void download(ArrayList<String> links) {
		
		downloader.download(links);
		
		boolean linksRemaining = true;
		
		while(linksRemaining){
			
			if(links.size()==linksCompleted.size() && linksFailed.size()<1){
				linksRemaining = false;
			}else{
				downloader.download(new ArrayList<>(linksFailed));
			}
		}
	}*/

	private ArrayList<String> parse(String url, String year) {

		ArrayList<String> pageList = parser.extractMonthsForYear(url,year);
		ArrayList<String> linksList = new ArrayList<>();
		for(String monthLink:pageList) {
			linksList.addAll(parser.extractLinksForMonth(monthLink));
		}
		
		return linksList;
	}
	
	public void setArgs(String url, String year, String logLevel) {
		
		this.url = url;
		this.year = year;
		
		if(logLevel!=null && !logLevel.isEmpty())
			setApplicationLogConfiguration(logLevel);
	}

	public synchronized void updateLinksSuccessful(String key) {
		
		LOG.info("updated link success");

		linksCompleted.add(key);
	}
	
	public synchronized void updateLinksFailed(String key) {
		
		LOG.info("updated link failure");

		linksFailed.add(key);
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

}
