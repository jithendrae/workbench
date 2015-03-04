package com.sample.crawler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHandler extends DefaultHandler{ 
	
	boolean isLocTag = false;
	ArrayList<String> extractedUrls = new ArrayList<>();
	String yearToExtract;
	
	public SaxHandler(String year){
		
		yearToExtract = year;
	}
	
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException{
		
		if(qName.equalsIgnoreCase("loc"))
			isLocTag = true;
			
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		
		if(isLocTag){
			
			Pattern p = Pattern.compile("(.)*/" + yearToExtract + "[0-9]{2}.mbox/(.)*");
			
			String str = new String(ch, start, length).trim();
			
			if(str!=null && !str.isEmpty() && !str.equalsIgnoreCase(""))			
				
			try 
			{				
				Matcher m = p.matcher(str);
				if (m.find()) {
					extractedUrls.add(str);
				}				
				
			}
			
			catch (Exception e) 
			{
				e.printStackTrace();
			}

		}
	}

}
