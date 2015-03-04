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

public class LinksDownloader implements Runnable {

	private LinksExtractor linksExtractorRef;
	private ExecutorService exec;
	private ArrayList<String> newDownloadableLinks;
	private ArrayList<Future<MailObject>> runningDownloadLinks = new ArrayList<>();
	
	static final Logger LOG = LoggerFactory.getLogger(LinksDownloader.class);


	public LinksDownloader(LinksExtractor obj) {

		this.linksExtractorRef = obj;
		exec = Executors.newFixedThreadPool(110);
	}

	public void run() {
		
		boolean condition = false, setDownloadableLinksStatus = true;

		try {
			
			LOG.info("Adding links to download corresponding mails");
			
			Thread.sleep(1000);

			while (!condition) {

				if (LinksExtractor.downloadableLinks != null)

					synchronized (LinksExtractor.downloadableLinks) {

						newDownloadableLinks = linksExtractorRef.getDownloadLinks();

						if (newDownloadableLinks != null) {
							
							Collections.synchronizedList(newDownloadableLinks);
							Collections.synchronizedCollection(runningDownloadLinks);

							if (runningDownloadLinks.size() > 0	&& !newDownloadableLinks.isEmpty()) {
								
								Iterator<Future<MailObject>> itr = runningDownloadLinks.iterator();
								
								while(itr.hasNext()){
									
									Future<MailObject> f = itr.next();

									MailObject obj = f.get(10, TimeUnit.SECONDS);

									if (!(obj.from.equalsIgnoreCase("Exception"))) {

										newDownloadableLinks.remove(obj.mailId);
										itr.remove();
									}

									else

										itr.remove();
								}
								
								runDownloads();
							}

							else if (runningDownloadLinks.size() > 0 && newDownloadableLinks.isEmpty()) {
								
								Iterator<Future<MailObject>> itr  = runningDownloadLinks.iterator();

								while(itr.hasNext()){

									Future<MailObject> f = itr.next();

									MailObject obj = f.get(10, TimeUnit.SECONDS);

									if (!(obj.from.equalsIgnoreCase("Exception"))) {

										itr.remove();
									}

									else {
										itr.remove();
										newDownloadableLinks.add(obj.mailId);
									}

								}

								runDownloads();

							}

							else if (runningDownloadLinks.isEmpty()	&& !newDownloadableLinks.isEmpty()) {

								runDownloads();
							}

						}

						linksExtractorRef.updateDownloadLinks(newDownloadableLinks);

						if (linksExtractorRef.getLinksRemainingStatus() == false)
							setDownloadableLinksStatus = false;

						if (setDownloadableLinksStatus == false
								&& runningDownloadLinks.size() < 1
								&& newDownloadableLinks.size() < 1) {
							condition = true;
							exec.shutdown();
							exec.awaitTermination(10, TimeUnit.SECONDS);
						}

						LinksExtractor.downloadableLinks.notify();
					}
			}

		}

		catch (Exception e) {
			e.printStackTrace();
			//log severe
		}
		
	}

	private synchronized void runDownloads() {

		List<Future<MailObject>> futs = new ArrayList<>();

		try {
			
			for (int i = 0; i < newDownloadableLinks.size(); i++){
				
				String link = newDownloadableLinks.get(i);
				
				futs.add(i, exec.submit(new LinkDownloadThread(link)));
				LOG.info("Link: " + link);
			}

			for (int i = 0; i < futs.size(); i++) {

				Future<MailObject> f = futs.get(i);
				MailObject ob = f.get(5, TimeUnit.SECONDS);

				if (!(ob.from.equalsIgnoreCase("Exception")))
					newDownloadableLinks.remove(ob.mailId);

				else{
					runningDownloadLinks.add(f);
					LOG.info("Mail download not complete for link: " + f.get().mailId);
				}
					
			}
		}

		catch (Exception e) {
			LOG.warn(e.toString());
		}
	}

}
