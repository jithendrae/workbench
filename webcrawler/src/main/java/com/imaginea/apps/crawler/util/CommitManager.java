package com.imaginea.apps.crawler.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.wiring.BeanConfigurerSupport;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.imaginea.apps.crawler.MailCrawler;
import com.imaginea.apps.crawler.Parser2;

@Component
public class CommitManager implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private MailCrawler crawler;
	
	@Autowired
	private Parser2 parser;
	
	@Autowired
	private DownloadsExecutorService exec;
	
	private ConcurrentLinkedQueue<String> failedLinks = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<String> succeededLinks = new ConcurrentLinkedQueue<>();
	
	private LinkedHashMap<String,Boolean> applicationPipeline = new LinkedHashMap<>();
	private HashMap<String,Boolean> monthsLinksMap = new HashMap<>();
	private ArrayList<String> downloadLinkslist = new ArrayList<>();
	
	private boolean canResume = false;

	private boolean retried = false;
	
	static final Logger LOG = LoggerFactory.getLogger(LinkDownloadThread.class);

	@Async
	public void updateSuccessQueue(String link){
		
		LOG.info("Updating commit: success");
		
		succeededLinks.add(link);
	}
	
	@Async
	public void updateFailureQueue(String link){
		
		LOG.info("Updating commit: failure");
	
		failedLinks.add(link);
	}
	
	@PostConstruct
	private void applicationWorkflow(){
		
		LOG.info("Setting application pipeline");
		
		applicationPipeline.put("setArgs", false);
		applicationPipeline.put("crawl", false);
		applicationPipeline.put("parse", false);
		applicationPipeline.put("Step1", false);
		applicationPipeline.put("Step2", false);
		//applicationPipeline.put("Step3", false);	
		applicationPipeline.put("finalize", false);		
	}
	
	public void setWorkflowStatus(String key, Boolean value){
		
		applicationPipeline.put(key, value);
	}
	
	public void setUpStep1(ArrayList<String> list){
		
		for(String s:list)
			monthsLinksMap.put(s, false);
	}

	public void setUpStep2(String monthLink, ArrayList<String> list) {

		monthsLinksMap.put(monthLink, true);
		
		if(!retried)
			downloadLinkslist.addAll(list);
	}
	
	public void doResume(){
		
		canResume = false;
		retried = true;
		
		LOG.info("Resuming app from previous state");
		
		Set<Entry<String, Boolean>> entrySet = applicationPipeline.entrySet();
		Iterator<Entry<String, Boolean>> itr = entrySet.iterator();
		while(itr.hasNext()){
			Entry<String, Boolean> entry = itr.next();
			if(entry.getValue() == false){
				if(entry.getKey().equalsIgnoreCase("setArgs")){
					rerun();
					break;
				}				
				else if(entry.getKey().equalsIgnoreCase("crawl")){
					
					for(Entry e:entrySet){
						if(e.getKey().equals("parse") && e.getValue().equals(true)){
							initiateFinalize();break;
						}
						else if(e.getKey().equals("parse") && e.getValue().equals(false)){
							for(Entry e1:entrySet){
								if(e1.getKey().equals("Step1") && e1.getValue().equals(true)){
									for(Entry e2:entrySet){
										if(e2.getKey().equals("Step2")){											
													initiateStep2();
													break;
											}
										}
									}								
								else if(e1.getKey().equals("Step1") && e1.getValue().equals(false)){
									rerun();
									break;
								}
							}								
						}
					}
				}
				
				else if(entry.getKey().equalsIgnoreCase("Step2")){
					initiateStep2();
					break;
				}
				
				else if(entry.getKey().equalsIgnoreCase("finalize")){
					initiateFinalize();
					break;
				}
			}
			/*else{
				canResume = false;
				break;
			}*/
		}
	}
	
	public boolean canResume(){
		
		if(canResume && !retried )
			return true;
		else
			return false;
	}
	
	public void rerun(){
		crawler.setArgs("http://mail-archives.apache.org/mod_mbox/maven-users/","2014",null);
		crawler.crawl();
	}
	
	public void initiateStep2(){
		//load from disk serialized objects;
		//continue app processing
		
		Iterator<Entry<String, Boolean>> itr = monthsLinksMap.entrySet().iterator();
		
		while(itr.hasNext()){
			
			Entry<String, Boolean> e = itr.next();
			
			if(e.getValue().equals(false)){
				parser.extractLinksForMonth(e.getKey());
			}
		}
		
		initiateFinalize();
		
	}
	
	public void initiateFinalize(){
		
		//canResume = true;
		
		while(succeededLinks.size() != downloadLinkslist.size() && failedLinks.size()>1){
			
			ArrayList<String> list = new ArrayList<String>(failedLinks);
			exec.addDownloadLinks(list);
			failedLinks.clear();
		}
		
		canResume = false;
		LOG.error("resume failed, restart the app again...");
		
		//canResume = false;
		File file = new File(".");
		File[] fileList = file.listFiles();
		for (File f : fileList) {
		    if (f.getName().endsWith(".ser")) {
		        f.delete(); // may fail mysteriously - returns boolean you may want to check
		    }
		}
		
		
	}

	public void setResumeStatus() {

		if(succeededLinks.size() != downloadLinkslist.size() && failedLinks.size()>1)
			canResume = true;
	}
	
	public void serialize(){		
		
		try 
		{
			FileOutputStream mSer = new FileOutputStream("failedLinks.ser");
			ObjectOutputStream out=new ObjectOutputStream(mSer); 			  
			out.writeObject(failedLinks);  
			out.flush();
			out.close();
		} 
		catch (IOException e) 
		{
			LOG.error("failedLinks object could not be saved");
		}  
		
		try 
		{
			FileOutputStream mSer = new FileOutputStream("succeededLinks.ser");
			ObjectOutputStream out=new ObjectOutputStream(mSer); 			  
			out.writeObject(succeededLinks);  
			out.flush();
			out.close();
		} 
		catch (IOException e) 
		{
			LOG.error("succeededLinks object  could not be saved");
		}  
		
		try 
		{
			FileOutputStream mSer = new FileOutputStream("applicationPipeline.ser");
			ObjectOutputStream out=new ObjectOutputStream(mSer); 			  
			out.writeObject(applicationPipeline);  
			out.flush();
			out.close();
		} 
		catch (IOException e) 
		{
			LOG.error("applicationPipeline object could not be saved");
		}  
		
		try 
		{
			FileOutputStream mSer = new FileOutputStream("monthsLinksMap.ser");
			ObjectOutputStream out=new ObjectOutputStream(mSer); 			  
			out.writeObject(monthsLinksMap);  
			out.flush();
			out.close();
		} 
		catch (IOException e) 
		{
			LOG.error("monthsLinksMap object could not be saved");
		}  
		
		try 
		{
			FileOutputStream mSer = new FileOutputStream("downloadLinkslist.ser");
			ObjectOutputStream out=new ObjectOutputStream(mSer); 			  
			out.writeObject(downloadLinkslist);  
			out.flush();
			out.close();
		} 
		catch (IOException e) 
		{
			LOG.error(downloadLinkslist.size() + " object could not be saved");
		}  
		
		try 
		{
			FileOutputStream mSer = new FileOutputStream("canResume.ser");
			ObjectOutputStream out=new ObjectOutputStream(mSer); 			  
			out.writeObject(canResume);  
			out.flush();
			out.close();
		} 
		catch (IOException e) 
		{
			LOG.error("canResume could not be saved");
		}  
	}
	
	@PostConstruct
	public void initializeManager(){
		
		try 
		{
			FileInputStream mSer = new FileInputStream("downloadLinkslist.ser");
			ObjectInputStream in=new ObjectInputStream(mSer);
			
				ArrayList list = (ArrayList<String>) in.readObject();
				downloadLinkslist = list;
			
			in.close();
		} 
		catch (IOException | ClassNotFoundException e) 
		{
			e.printStackTrace();
			LOG.error("downloadLinkslist could not be read");
		}  
		
		try 
		{
			FileInputStream mSer = new FileInputStream("monthsLinksMap.ser");
			ObjectInputStream in=new ObjectInputStream(mSer); 			  
			monthsLinksMap = (HashMap<String, Boolean>) in.readObject();  
			in.close();
		} 
		catch (IOException | ClassNotFoundException e) 
		{
			LOG.error("monthsLinksMap could not be read");
		}  
		
		try 
		{
			FileInputStream mSer = new FileInputStream("applicationPipeline.ser");
			ObjectInputStream in=new ObjectInputStream(mSer); 			  
			applicationPipeline = (LinkedHashMap<String, Boolean>) in.readObject();  
			in.close();
		} 
		catch (IOException | ClassNotFoundException e) 
		{
			LOG.error("applicationPipeline could not be read");
		}  
		
		try 
		{
			FileInputStream mSer = new FileInputStream("canResume.ser");
			ObjectInputStream in=new ObjectInputStream(mSer); 			  
			canResume = (boolean) in.readObject();  
			in.close();
		} 
		catch (IOException | ClassNotFoundException e) 
		{
			LOG.error("canResume could not be read");
		}  
		
		try 
		{
			FileInputStream mSer = new FileInputStream("succeededLinks.ser");
			ObjectInputStream in=new ObjectInputStream(mSer); 			  
			succeededLinks = (ConcurrentLinkedQueue<String>) in.readObject();  
			in.close();
		} 
		catch (IOException | ClassNotFoundException e) 
		{
			LOG.error("succeededLinks could not be read");
		}  
		
		try 
		{
			FileInputStream mSer = new FileInputStream("failedLinks.ser");
			ObjectInputStream in=new ObjectInputStream(mSer); 			  
			failedLinks = (ConcurrentLinkedQueue<String>) in.readObject();  
			in.close();
		} 
		
		catch (IOException | ClassNotFoundException e) 
		{
			LOG.error("failedLinks could not be read");
		}
	
	}

	public boolean isResumed() {
		return retried;
	}
	
}
