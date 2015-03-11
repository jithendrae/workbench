package com.sample.crawler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class manages those extracted link to be downloaded by sending each link from the 
 * ArrayList to a Callable service that returns the status of its completeion in the form an 
 * MailObject.class, If the mailobject's from attribute is not an exception, it is understood 
 * that it downloaded or otherwise that an exception arrived and download has failed  
 */

public class LinksDownloader implements Runnable {

	private LinksExtractor linksExtractorRef;
	private ExecutorService exec;
	private ArrayList<String> newDownloadableLinks;
	private ArrayList<Future<MailObject>> runningDownloadLinks = new ArrayList<>();

	static final Logger LOG = LoggerFactory.getLogger(LinksDownloader.class);

	public LinksDownloader(LinksExtractor obj) {

		this.linksExtractorRef = obj;
		exec = Executors.newFixedThreadPool(1111);
	}

	public void run() {

		boolean threadExitCondition = false, setDownloadableLinksStatus = true;

		try {

			LOG.info("Adding links to download corresponding mails");

			while (!threadExitCondition) {

				if (LinksExtractor.downloadableLinks != null)

					synchronized (LinksExtractor.downloadableLinks) {

						newDownloadableLinks = linksExtractorRef.getDownloadLinks();

						if (newDownloadableLinks != null) {

							Collections.synchronizedList(newDownloadableLinks);
							Collections.synchronizedCollection(runningDownloadLinks);

							if (runningDownloadLinks.size() > 0
									&& !newDownloadableLinks.isEmpty()) {

								Iterator<Future<MailObject>> itr = runningDownloadLinks.iterator();
								
								Future<MailObject> f;

								MailObject obj = null;

								while (itr.hasNext()) {

									f = itr.next();
									
									try{
										obj = f.get(400, TimeUnit.SECONDS);
										
										if (!(obj.from.equalsIgnoreCase("Exception")) && obj!=null) {
	
											newDownloadableLinks.remove(obj.mailId);
									
											itr.remove();
										}
	
										else {
	
											LOG.debug("Corresponding mail "
													+ obj.mailId
													+ " download failed with on"
													+ obj.contents);
	
											itr.remove();
										}									
									}
									catch(Exception e){
										LOG.error(e.getLocalizedMessage());
										itr.remove();
									}
								}

								runDownloads();
							}

							else if (runningDownloadLinks.size() > 0
									&& newDownloadableLinks.isEmpty()) {

								Iterator<Future<MailObject>> itr = runningDownloadLinks.iterator();
								
								Future<MailObject> f;

								MailObject obj = null;

								while (itr.hasNext()) {

									f = itr.next();
									
									try{
										obj = f.get(400, TimeUnit.SECONDS);
										
										if (!(obj.from.equalsIgnoreCase("Exception")) && obj!= null) {
	
											itr.remove();
										}
	
										else {
	
											LOG.debug("Corresponding mail "
													+ obj.mailId
													+ " download failed with on"
													+ obj.from);
	
											itr.remove();
											newDownloadableLinks.add(obj.mailId);
										}
									}
									catch(Exception e){
										
										if(obj != null)
											itr.remove();
										newDownloadableLinks.add(obj.mailId);
									}									

								}

								runDownloads();

							}

							else if (runningDownloadLinks.isEmpty()
									&& !newDownloadableLinks.isEmpty()) {

								runDownloads();
							}

						}

						linksExtractorRef.updateDownloadLinks(newDownloadableLinks);

						if (linksExtractorRef.getLinksRemainingStatus() == false)
							setDownloadableLinksStatus = false;

						if (setDownloadableLinksStatus == false
								&& runningDownloadLinks.size() < 1
								&& newDownloadableLinks.size() < 1) {

							threadExitCondition = true;

							exec.shutdown();

							LOG.info("Application finishing...");

							exec.awaitTermination(300, TimeUnit.SECONDS);
						}

						LinksExtractor.downloadableLinks.notify();
					}
				
			}

		}

		catch (Exception e) {
			LOG.error("Downloads not started with following error \n"
					+ e.getMessage());
		}

	}

	/*
	 * Method to create callable threads to download the current set of urls and
	 * check their status to add to list that stores them (runningDownloads) or
	 * to update the links in the LinksExtractor thread
	 */

	private synchronized void runDownloads() {

		List<Future<MailObject>> futs = new ArrayList<>();

		try {

			for (int i = 0; i < newDownloadableLinks.size(); i++) {

				String link = newDownloadableLinks.get(i);

				futs.add(i, exec.submit(new LinkDownloadThread(link)));
				LOG.info("Link: " + link);
			}
			
			Iterator<Future<MailObject>> itr = futs.iterator();
			
			while(itr.hasNext()){
				
				Future<MailObject> f = itr.next();
				MailObject ob = null;
				try{
					ob = f.get(100, TimeUnit.SECONDS);
					if (!(ob.from.equalsIgnoreCase("Exception")) && ob != null)
						newDownloadableLinks.remove(ob.mailId);
	
					else {
						runningDownloadLinks.add(f);
						LOG.info("Mail download not complete for link: "
								+ ob.mailId);
					}
				}
				catch(Exception e){
					LOG.error("Exception fetching status of downloaded mail" + ob.mailId + "with " + e.getMessage());
					runningDownloadLinks.add(f);
				}			
				
			}

		}

		catch (Exception e) {
			LOG.warn(e.toString());
		}
	}

}
