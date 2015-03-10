package com.sample.crawler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
 * This class accepts an input stream and continuously reads XML extracting required data
 * and adds to an ArrayList the extracted links upon parsing the XMLStream * 
 */

public class SaxHandler extends DefaultHandler {

	boolean isLocTag = false;
	public ArrayList<String> extractedUrls = new ArrayList<>();
	private String yearToExtract;
	private Pattern p;

	public SaxHandler(String year) {

		yearToExtract = year;
		p = Pattern.compile("(.)*/" + yearToExtract	+ "[0-9]{2}.mbox/(.)*");
	}

	/*
	 * This method extracts the required chunk of XML tree that has the node
	 * representing the value 'loc' that represents the value which is required
	 * to be extracted
	 */

	public void startElement(String uri, String localName, String qName, 
			Attributes attributes) throws SAXException {

		if (qName.equalsIgnoreCase("loc"))
			isLocTag = true;
		else
			isLocTag = false;
	}

	/*
	 * This method finds the required mail box urls from among the incoming set
	 * of urls and adds them to the ArrayList
	 */

	public void characters(char ch[], int start, int length)
			throws SAXException {
		
		String str = new String(ch, start, length).trim();

		if (isLocTag) {

			if (str != null && !str.isEmpty() && !str.equalsIgnoreCase(""))

				try {
					Matcher m = p.matcher(str);
					if (m.find()) {
						extractedUrls.add(str);
					}
				}

				catch (Exception e) {
					e.printStackTrace();
				}
			
		}
	}

}
