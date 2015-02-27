package crawler_sample1.crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class LinksDownloader implements Runnable {

	LinksExtractor two;
	ExecutorService exec;
	File index_file;
	ArrayList<String> newDownloadableLinks;
	ArrayList<Future<MailObject>> runningDownloadLinks = new ArrayList<>();

	public LinksDownloader(LinksExtractor two) {

		this.two = two;
		exec = Executors.newFixedThreadPool(6);
	}

	public void run() {
		
		boolean condition = false, setDownloadableLinksStatus = true;

		try {
			
			Thread.sleep(1000);

			while (!condition) {

				if (LinksExtractor.downloadableLinks != null)

					synchronized (LinksExtractor.downloadableLinks) {

						newDownloadableLinks = two.getDownloadLinks();

						if (newDownloadableLinks != null) {
							
							Collections.synchronizedList(newDownloadableLinks);
							Collections.synchronizedCollection(runningDownloadLinks);

							if (runningDownloadLinks.size() > 0	&& !newDownloadableLinks.isEmpty()) {
								
								Iterator<Future<MailObject>> itr = runningDownloadLinks.iterator();
								
								while(itr.hasNext()){
									
									Future<MailObject> f = itr.next();

									MailObject obj = f.get(5, TimeUnit.SECONDS);

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

									MailObject obj = f.get(5, TimeUnit.SECONDS);

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

						two.updateDownloadLinks(newDownloadableLinks);

						if (two.getLinksRemainingStatus() == false)
							setDownloadableLinksStatus = false;

						if (setDownloadableLinksStatus == false
								&& runningDownloadLinks.size() < 1
								&& newDownloadableLinks.size() < 1) {
							condition = true;
							exec.shutdown();
							exec.awaitTermination(6, TimeUnit.SECONDS);
						}

						LinksExtractor.downloadableLinks.notify();
					}
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private synchronized void runDownloads() {

		writeLinksToDisk();

		List<Future<MailObject>> futs = new ArrayList<>();

		try {
			
			for (int i = 0; i < newDownloadableLinks.size(); i++)
				futs.add(i, exec.submit(new LinkDownloadThread(newDownloadableLinks.get(i))));

			for (int i = 0; i < futs.size(); i++) {

				Future<MailObject> f = futs.get(i);
				MailObject ob = f.get(5, TimeUnit.SECONDS);

				System.out.println(ob.from);

				if (!(ob.from.equalsIgnoreCase("Exception")))
					newDownloadableLinks.remove(ob.mailId);

				else
					runningDownloadLinks.add(f);

			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void writeLinksToDisk() {

	}

}
