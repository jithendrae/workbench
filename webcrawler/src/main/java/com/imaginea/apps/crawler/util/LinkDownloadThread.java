package com.imaginea.apps.crawler.util;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/*
 * This class configures the threads created by Worker 
 * and returns the status of each thread to the executor which 
 * then updates the CommitManager of the thread's success
 * in downloading the mails by implementing the Callable interface
 */

@Component("LinkDownloadThread")
@Scope("prototype")
public class LinkDownloadThread implements Callable<Boolean> {
	
	
	@Autowired
	private CommitManager manager;

	private String mailbox_url;
	private String mailId;
	private String linkStatus;
	
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);
	
	public void setDownloadUrl(String url) {
		
		mailbox_url = url;
		
		LOG.info("The mail download link is "+url);
	}

	public String getUrl(String mailbox_url){
		
		if(linkStatus.equalsIgnoreCase("new")){
			
			int decode_index = mailbox_url.lastIndexOf("/");
			String url_first = mailbox_url.substring(0, decode_index + 1);
			String url_last = "<" + mailbox_url.substring(decode_index + 4,	mailbox_url.length() - 3) + ">";
			mailId = mailbox_url.substring(decode_index+1,mailbox_url.length());
			return  url_first + url_last;
		}
		
		else{
			int decode_index = mailbox_url.lastIndexOf("/");
			mailId = "%3C"+mailbox_url.substring(decode_index+2,mailbox_url.length()-1)+"%3E";
			return mailbox_url;

		}		

	}	
	
	@Override
	public Boolean call() throws Exception {	
		
		boolean status = false;	
			
			String url = mailbox_url;
			
				url = getUrl(mailbox_url);
				
				try 
				{
					File f = new File("E://mails//" + mailId);
					//File f = new File(filePath);
					FileUtils.copyURLToFile(new URL(url), f);
					
					LOG.debug("Download for mail "+ mailId + " is successful");
					manager.updateSuccessQueue(url);
					status = true;
				} 
				
				catch (Exception e) 
				{
					e.printStackTrace();
					LOG.debug("Download for mail "+ mailId + " has failed");
					manager.updateFailureQueue(url);
					status = false;					

				}		
		
		return status;
		
		
	}
	
	private String getMailId(String url) {

		int decode_index = url.lastIndexOf("/");		
		return mailId = "%3C"+url.substring(decode_index+2,mailbox_url.length()-1)+"%3E";		
	}

	public String getSaveFileName(String subject){
		
		String fileName = subject.replaceAll("[^a-zA-Z0-9.-_][.]$", "");		
		fileName = fileName.replaceAll("[():\\\\/*\"?|<>]+", "_");
		fileName = StringUtils.trim(fileName);
		
		return fileName;
	}
	
	private Date getDateString(String dateString) {
		
		Date date = null;
		
		SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

		try 
		{
			date = inputFormat.parse(dateString);
		}
		catch (ParseException e) 
		{
			LOG.warn(e.getMessage());

		}
		
		return date;
		
	}
	
	public static String theMonth(int month) {

		String[] monthNames = { "January", "February", "March", "April", "May",
				"June", "July", "August", "September", "October", "November",
				"December" };
		return monthNames[month];
	}

	public void setLinkStatus(String status) {

		linkStatus = status;
	}


}