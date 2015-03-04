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

public class LinkDownloadThread implements Callable<MailObject> {

	private String mailbox_url;
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);


	public LinkDownloadThread(String url) {

		mailbox_url = url;
	}

	@SuppressWarnings("deprecation")
	@Override
	public MailObject call() throws Exception {
		
		LOG.debug("Starting to save mail content for mail " + mailbox_url);
		
		MailObject obj = null;

		int decode_index = mailbox_url.lastIndexOf("/");
		String url_first = mailbox_url.substring(0, decode_index + 1);
		String url_last = "<" + mailbox_url.substring(decode_index + 4,	mailbox_url.length() - 3) + ">";
		try {

			String url = url_first + url_last;

			Document doc = Jsoup.connect(url).get();

			Elements ele = doc.getElementsByTag("table");

			for (Element l : ele) {
				if (l.id().equalsIgnoreCase("msgview"))
					obj = parseMailAndSaveContents(l);
			}

			if (obj != null) {

				String fileName = obj.subject.replaceAll("[^a-zA-Z0-9.-_][.]$", "");
				fileName = fileName.replaceAll("[():\\\\/*\"?|<>]+", "_");

				fileName = StringUtils.trim(fileName);
								
				File f = new File("E://mails//"
						+ new SimpleDateFormat("yyyy").format(obj.date) + "//"
						+ theMonth(obj.date.getMonth()) + "//" + fileName
						+ "__"
						+ new SimpleDateFormat("dd_HHmm").format(obj.date)
						+ ".txt");

				FileUtils.writeStringToFile(f, obj.contents, "UTF-8");
			}

		} 
		
		catch (Exception e) 
		{
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