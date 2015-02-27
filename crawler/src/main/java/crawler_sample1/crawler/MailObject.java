package crawler_sample1.crawler;


import java.util.Date;

public class MailObject {

	String from;
	String subject;
	String contents;
	String mailId;
	Date date;
	
	
	public MailObject(String author,String subject, String contents, String mailId, Date date){
		
		this.from = author;
		this.subject = subject;
		this.date = date;	
		this.mailId = mailId;
		this.contents = contents;
	}
	
}