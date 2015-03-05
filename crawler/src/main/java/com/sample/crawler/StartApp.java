package com.sample.crawler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

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
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class StartApp {

	static final Logger LOG = LoggerFactory.getLogger(StartApp.class);
	
	public static void main(String args[]) throws MalformedURLException, IOException {
		
		LOG.info("Provide the Apache mail archieve url to download");
		
		Scanner inputReader = new Scanner(System.in);
		
		String url = inputReader.nextLine();
		
		LOG.info("Provide the archiving  year from which to download the mails from");
		
		String mail_year_to_download = inputReader.nextLine();
		
		LOG.info("Provide logging level for the application(optional)");
		
		String logLevel = inputReader.nextLine();
		
		if(logLevel!=null && !logLevel.isEmpty()){
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = null;
			try 
			{
				documentBuilder = documentBuilderFactory.newDocumentBuilder();
				Document document = documentBuilder.parse(StartApp.class.getResource("/logback.xml").getPath());
				
				Node root = document.getElementsByTagName("root").item(0);
				NamedNodeMap attr = root.getAttributes();
				Node nodeAttr = attr.getNamedItem("level");
				nodeAttr.setTextContent(logLevel);
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				File f = new File(StartApp.class.getResource("/logback.xml").getPath());
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
			
			catch (ParserConfigurationException | SAXException | TransformerException | JoranException e) 
			{
				LOG.warn("Exception setting Log level \n" + e.getLocalizedMessage() + " LOG level set to default: INFO");
			}			

		}

		String url_full_with_sitemap = url + "?format=sitemap";
		
		LinksExtractor obj = new LinksExtractor(url_full_with_sitemap, mail_year_to_download);
					
		obj.start();
		
		new Thread(new LinksDownloader(obj)).start();
		
		inputReader.close();				
	}

}
