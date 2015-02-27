package crawler_sample1.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LinksExtractor implements Runnable {

	static LinksExtractor instance;

	String url = "http://mail-archives.apache.org/mod_mbox/maven-users/?format=sitemap";
	String year = "2014";
	File f = new File("sample.xml");

	boolean parsingStatus = true;

	static ArrayList<String> downloadableLinks;

	private LinksExtractor() {

	}

	public static LinksExtractor getInstance() {

		if (instance == null) {
			instance = new LinksExtractor();
		}
		return instance;

	}

	public synchronized void updateDownloadLinks(
			ArrayList<String> newDownloadableLinks) {

		downloadableLinks.clear();
		downloadableLinks = newDownloadableLinks;
	}

	public synchronized ArrayList<String> getDownloadLinks() {
		return downloadableLinks;

	}

	@Override
	public void run() {

		downloadableLinks = new ArrayList<String>();

		Thread t1 = new Thread(new LinksDownloader(this));
		t1.start();

		DocumentBuilder builder;
		try {
			URLConnection ucon = new URL(url).openConnection();

			BufferedReader br = new BufferedReader(new InputStreamReader(ucon.getInputStream()), 500 * 1024);
			String input = "";

			FileWriter fw = new FileWriter(f);
			
			while ((input = br.readLine()) != null)

				fw.write(input);			

			fw.close();
								
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(f);

			NodeList extractedLinks = doc.getElementsByTagName("loc");

			System.out.println(extractedLinks.getLength());

			Pattern p = Pattern.compile("(.)*/" + year + "[0-9]{2}.mbox/(.)*");

			for (int i = 0; i < extractedLinks.getLength(); i++) {

				Node n = extractedLinks.item(i).getLastChild();

				System.out.println(n);

				if (n.getNodeType() == Node.CDATA_SECTION_NODE) {
					Matcher m = p.matcher(n.getNodeValue());
					if (m.find()) {
						downloadableLinks.add(n.getNodeValue().trim());
					}

				}

				if (downloadableLinks != null)

					synchronized (downloadableLinks) {

						if (i % 10 == 0 && i > 0)
							downloadableLinks.wait();
					}
			}

			parsingStatus = false;

		}

		catch (ParserConfigurationException | SAXException | IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	public boolean getLinksRemainingStatus() {
		return parsingStatus;
	}

}
