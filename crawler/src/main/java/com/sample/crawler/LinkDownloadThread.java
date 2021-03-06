package com.sample.crawler;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class downloads and saves the mail from the url that is supplied to it
 */

public class LinkDownloadThread implements Callable<MailObject> {

	private String mailbox_url;
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);

	public LinkDownloadThread(String url) {

		mailbox_url = url;
	}

	public String getUrl(String mailbox_url){
		
		int decode_index = mailbox_url.lastIndexOf("/");
		String url_first = mailbox_url.substring(0, decode_index + 1);
		String url_last = "<" + mailbox_url.substring(decode_index + 4,	mailbox_url.length() - 3) + ">";
		return  url_first + url_last;
	}
	
	@SuppressWarnings("deprecation")
	public String getSaveFileName(MailObject obj){
		
		String fileName = obj.subject.replaceAll("[^a-zA-Z0-9.-_][.]$", "");		
		fileName = fileName.replaceAll("[():\\\\/*\"?|<>]+", "_");
		fileName = StringUtils.trim(fileName);
		
		String filePath = "E://mails//"
				+ new SimpleDateFormat("yyyy").format(obj.date) + "//"
				+ theMonth(obj.date.getMonth()) + "//" + fileName
				+ "__"
				+ new SimpleDateFormat("dd_HHmm").format(obj.date)
				+ ".txt";
		
		return filePath;
	}
	
	@Override
	public MailObject call() throws Exception {
		
		MailObject obj = new MailObject("Exception", null, null, mailbox_url, null);
		
			try {

				String url = getUrl(mailbox_url);

				Document doc = Jsoup.connect(url).get();
				Elements ele = doc.getElementsByTag("table");

				for (Element l : ele) {
					if (l.id().equalsIgnoreCase("msgview"))
						obj = parseMailAndSaveContents(l);
				}

				if (obj != null) {

					String path = getSaveFileName(obj);
					FileUtils.writeStringToFile(new File(path), obj.contents, "UTF-8");
					
					LOG.debug("Finishing to save mail content for mail " + mailbox_url);		

				}

			} 
			
			catch (Exception e) 
			{
				LOG.debug("Exception saving mail for url " + mailbox_url);
				return new MailObject("Exception", null, e.getMessage(), mailbox_url, null);
			}
				
		return obj;

	}

	private MailObject parseMailAndSaveContents(Element table) {

		Elements ele = table.getElementsByTag("tbody");

		MailObject obj = null;

		for (Element tbody : ele) {

			obj = buildMailObject(tbody.getAllElements());
		}
		return obj;
	}

	private MailObject buildMailObject(Elements rowData) {

		Date date = null;
		String from = null;
		String subject = null;
		String contents = null;
		String dateString = null;

		for (int i = 0; i < rowData.size(); i++) {

			Element tr = rowData.get(i);

			if (tr.className().equalsIgnoreCase("from")) {
				from = tr.getElementsByClass("right").get(0).text();

				Pattern pattern = Pattern.compile("\"([^\"]*)\"");
				Matcher matcher = pattern.matcher(from);
				if (matcher.find()) {
					from = matcher.group(1);
				}

			} else if (tr.className().equalsIgnoreCase("subject")) {
				subject = tr.getElementsByClass("right").get(0).text();

			} else if (tr.className().equalsIgnoreCase("date")) {
				dateString = tr.getElementsByClass("right").get(0).text();

			} else if (tr.className().equalsIgnoreCase("contents")) {
				contents = tr.getElementsByTag("td").text();
			}
		}

		SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		
		try 
		{
			date = inputFormat.parse(dateString);
		}
		catch (ParseException e) 
		{
			LOG.warn(e.getMessage());

		}
		
		if (from != null && from.length() > 2)

			return new MailObject(from, subject, contents, mailbox_url, date);

		else

			return null;
	}

	public static String theMonth(int month) {

		String[] monthNames = { "January", "February", "March", "April", "May",
				"June", "July", "August", "September", "October", "November",
				"December" };
		return monthNames[month];
	}

}