import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	public static void main(String[] args) throws Exception {

		String crawlStorageFolder = "/Users/saurabhvajramatti/eclipse-workspace/MyCrawlerProject/data/crawl";
		// TODO Set
		int numberOfCrawlers = 7;
		CrawlConfig config = new CrawlConfig();

		// config.setResumableCrawling(true);

		// TODO set to 16
		int maxDepthOfCrawling = 16;
		config.setMaxDepthOfCrawling(maxDepthOfCrawling);

//TODO set to 20000
		int maxPagesToFetch = 20000;
		config.setMaxPagesToFetch(maxPagesToFetch);

//TODO set
		int politenessDelay = 210;
		config.setPolitenessDelay(politenessDelay);

//TODO set
		String userAgentString = "MyCrawler_Saurabh";
		config.setUserAgentString(userAgentString);

		Boolean includeBinaryContentInCrawling = true;
		config.setIncludeBinaryContentInCrawling(includeBinaryContentInCrawling);

		config.setCrawlStorageFolder(crawlStorageFolder);
		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		/*
		 * For each crawl, you need to add some seed urls. These are the first URLs that
		 * are fetched and then the crawler starts following links which are found in
		 * these pages
		 */
		// controller.addSeed("https://www.latimes.com/");
		controller.addSeed("https://www.latimes.com/");
		/*
		 * Start the crawl. This is a blocking operation, meaning that your code will
		 * reach the line after this only when crawling is finished.
		 */
		controller.start(MyCrawler.class, numberOfCrawlers);

		///////////////////////////////////////////////////////////////
		
		
		
		FileWriter fileWriter = new FileWriter(new File("CrawlReport_latimes.txt"));
		fileWriter.write("Name: Saurabh Vajramatti \n");
		fileWriter.write("USC ID: 5761344286 \n");
		fileWriter.write("News site crawled: latimes.com \n");
		fileWriter.write("Number of threads: 7 \n");
		fileWriter.write("\n");
		fileWriter.write("Fetch Statistics \n");
		fileWriter.write("================ \n");
		fileWriter.write("# fetches attempted: " + MyCrawler.totalFetchCount);
		fileWriter.write("\n# fetches succeeded: " + MyCrawler.fetchSucceededCount);
		fileWriter.write("\n# fetches failed or aborted: " + MyCrawler.fetchFailedCount);
		fileWriter.write("\n");
		fileWriter.write("\nOutgoing URLs:");
		fileWriter.write("\n==============");
		int unique[] = uniqueChecker();
		fileWriter.write("\nTotal URLs extracted: " + MyCrawler.totalExtractedCount);
		// TODO
		fileWriter.write("\n# unique URLs extracted: "+Integer.toString(unique[0]+unique[1]));
		fileWriter.write("\n# unique within News Site: "+unique[0]);
		fileWriter.write("\n# unique outside News Site: "+unique[1]);		
		fileWriter.write("\n");
		fileWriter.write("\nStatus Codes:");
		fileWriter.write("\n==============");
		/*
		fileWriter.write("\n200 OK:" + MyCrawler.statusCodesMap.get(200));
		fileWriter.write("\n301 Moved Permanently:" + MyCrawler.statusCodesMap.get(301));
		fileWriter.write("\n401 Unauthorized:" + MyCrawler.statusCodesMap.get(401));
		fileWriter.write("\n403 Forbidden:" + MyCrawler.statusCodesMap.get(403));
		fileWriter.write("\n404 Not Found:" + MyCrawler.statusCodesMap.get(404));
		*/
		for (Integer statusCode : MyCrawler.statusCodesMap.keySet()) {
			fileWriter.write("\n"+statusCode + ": " + MyCrawler.statusCodesMap.get(statusCode));
		}
		fileWriter.write("\n");
		fileWriter.write("\nFile Sizes:");
		fileWriter.write("\n==============");
		fileWriter.write("\n < 1KB:" + MyCrawler.fileSizeMap.get("s1"));
		fileWriter.write("\n1KB ~ <10KB:" + MyCrawler.fileSizeMap.get("s2"));
		fileWriter.write("\n10KB ~ <100KB:" + MyCrawler.fileSizeMap.get("s3"));
		fileWriter.write("\n100KB ~ <1MB:" + MyCrawler.fileSizeMap.get("s4"));
		fileWriter.write("\n>= 1MB:" + MyCrawler.fileSizeMap.get("s5"));

		fileWriter.write("\n");
		fileWriter.write("\nContent Types:");
		fileWriter.write("\n==============");
		for (String type : MyCrawler.contentTypeMap.keySet()) {
			fileWriter.write("\n"+type + ": " + MyCrawler.contentTypeMap.get(type));
		}
		fileWriter.write("\n");
		fileWriter.flush();
		fileWriter.close();
		////////////////////////////////////////////////////		
	}

	private static int[] uniqueChecker() throws FileNotFoundException {
	Set<String> uniqueIn = new HashSet<String>();
	Set<String> uniqueOut = new HashSet<String>();	
		
	try (Scanner scanner = new Scanner(new File("urls_latimes.csv"));) {
	    while (scanner.hasNextLine()) {
	        String[] lineArray = scanner.nextLine().split(",");
	        if(lineArray[1].equals("OK")) {
	        	uniqueIn.add(lineArray[0]);
	        }
	        else {
	        	uniqueOut.add(lineArray[0]);
	        }
	    }
	}
	int ret[] = {uniqueIn.size(), uniqueOut.size()};
	return ret;
	}
}