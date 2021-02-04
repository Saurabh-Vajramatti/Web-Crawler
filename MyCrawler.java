import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

	static Map<Integer, Integer> statusCodesMap = new HashMap<Integer, Integer>();
	static Map<String, Integer> contentTypeMap = new HashMap<String, Integer>();
	static Map<String, Integer> fileSizeMap = new HashMap<String, Integer>();

	static AtomicInteger totalFetchCount;
	static AtomicInteger fetchSucceededCount;
	static AtomicInteger fetchFailedCount;
	static AtomicInteger totalExtractedCount;

	static ArrayList<String[]> fetchData;
	static ArrayList<String[]> visitData;
	static ArrayList<String[]> urlsData;

	static PrintWriter fetchCsvWriter;
	static PrintWriter visitCsvWriter;
	static PrintWriter urlsCsvWriter;

	static {
		fileSizeMap.put("s1", 0);
		fileSizeMap.put("s2", 0);
		fileSizeMap.put("s3", 0);
		fileSizeMap.put("s4", 0);
		fileSizeMap.put("s5", 0);

		statusCodesMap.put(200,0);
		statusCodesMap.put(301,0);
		statusCodesMap.put(401,0);
		statusCodesMap.put(403,0);
		statusCodesMap.put(404,0);
		
		contentTypeMap.put("text/html", 0);
		contentTypeMap.put("image/gif", 0);
		contentTypeMap.put("image/jpeg", 0);
		contentTypeMap.put("image/png", 0);
		contentTypeMap.put("application/pdf", 0);

		fetchData = new ArrayList<String[]>();
		visitData = new ArrayList<String[]>();
		urlsData = new ArrayList<String[]>();

		totalFetchCount = new AtomicInteger(0);
		fetchSucceededCount = new AtomicInteger(0);
		fetchFailedCount = new AtomicInteger(0);
		totalExtractedCount = new AtomicInteger(0);

		try {
			fetchCsvWriter = new PrintWriter(new File("fetch_latimes.csv"));
			visitCsvWriter = new PrintWriter(new File("visit_latimes.csv"));
			urlsCsvWriter = new PrintWriter(new File("urls_latimes.csv"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//private static final Pattern allowedPatterns = Pattern.compile(".*(\\.(pdf|html|doc|docx|bmp|gif|jpe?g|png|tiff?))$");

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|json" + "|mp3|mp4|zip|gz))$");

	// private final static Pattern FILTERS =
	// Pattern.compile(".*(\\.(html|doc|docx|pdf|gif|jpg" + "|png))$");

	public String convertToCSV(String[] data) {
		return Stream.of(data).collect(Collectors.joining(","));
	}

	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		totalFetchCount.incrementAndGet();

		if (statusCode < 300 && statusCode > 199) {
			fetchSucceededCount.incrementAndGet();
		}
		else {
			if (fetchData.size() > 80) {
				dumpFetchData();
			}
			fetchData.add(new String[] { webUrl.getURL().replaceAll(",", "-"), Integer.toString(statusCode) });
		}

		if (statusCodesMap.containsKey(statusCode)) {
			statusCodesMap.replace(statusCode, statusCodesMap.get(statusCode) + 1);
		} else {
			statusCodesMap.put(statusCode, 1);
		}

		//System.out.println(fetchData);
		super.handlePageStatusCode(webUrl, statusCode, statusDescription);
	}

	@Override
	protected void onContentFetchError(Page page) {
//		if (fetchData.size() > 50) {
//			dumpFetchData();
//		}
//		fetchData.add(new String[] {page.getWebURL().getURL().replaceAll(",", "-"), Integer.toString(page.getStatusCode()) });

		super.onContentFetchError(page);
	}

	@Override
	protected void onParseError(WebURL webUrl) {
//		if (fetchData.size() > 50) {
//			dumpFetchData();
//		}
//		fetchData.add(new String[] {webUrl.getURL().replaceAll(",", "-"), Integer.toString(page.getStatusCode()) });

		super.onParseError(webUrl);
	}

	@Override
	protected void onUnexpectedStatusCode(String urlStr, int statusCode, String contentType, String description) {
		// TODO Auto-generated method stub
		super.onUnexpectedStatusCode(urlStr, statusCode, contentType, description);
	}

	@Override
	protected void onUnhandledException(WebURL webUrl, Throwable e) {
		// TODO Auto-generated method stub
		super.onUnhandledException(webUrl, e);
	}

	/**
	 * This method receives two parameters. The first parameter is the page in which
	 * we have discovered this new url and the second parameter is the new url. You
	 * should implement this function to specify whether the given url should be
	 * crawled or not (based on your crawling logic). In this example, we are
	 * instructing the crawler to ignore urls that have css, js, git, ... extensions
	 * and to only accept urls that start with "http://www.viterbi.usc.edu/". In
	 * this case, we didn't need the referringPage parameter to make the decision.
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		// TODO getContentType() ???

		if (urlsData.size() > 80) {
			dumpUrlsData();
		}

		Boolean flag = (!FILTERS.matcher(href).matches());
		//Boolean flag = allowedPatterns.matcher(href).matches() && (!FILTERS.matcher(href).matches());
		Boolean flag2 = (href.startsWith("http://www.latimes.com/") || href.startsWith("https://www.latimes.com/")
				|| href.startsWith("https://latimes.com/") || href.startsWith("http://latimes.com/"));

		if (flag2) {
			urlsData.add(new String[] { href.replaceAll(",", "-"), "OK" });
		} else {
			urlsData.add(new String[] { href.replaceAll(",", "-"), "N_OK" });
		}
		return flag && flag2;
	}

	/**
	 * This function is called when a page is fetched and ready to be processed by
	 * your program.
	 */
	@Override
	public void visit(Page page) {

		String url = page.getWebURL().getURL();
		// TODO page.getContentType() ???
		/*
		 * try { csvWriter.append(url); csvWriter.append("\n"); } catch (IOException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); }
		 */
		// TODO TASK 1
//		if (fetchData.size() > 50) {
//			dumpFetchData();
//		}
//		fetchData.add(new String[] { url.replaceAll(",", "-"), Integer.toString(page.getStatusCode()) });

		System.out.println("URL: " + url);
		System.out.println("Status Code: " + page.getStatusCode());
		String contentType = page.getContentType().split(";")[0];
		System.out.println("Content Type: " + contentType);
		int contentLength = page.getContentData().length;
		System.out.println("Size: " + contentLength + "bytes");
		
		
		if (fetchData.size() > 80) {
			dumpFetchData();
		}
		fetchData.add(new String[] { url.replaceAll(",", "-"), Integer.toString(page.getStatusCode()) });
		
		
		if (visitData.size() > 50) {
			dumpVisitData();
		}
//		if(contentType.contains("image")||contentType.contains("text/html")||contentType.contains("application/pdf")) {
			visitData.add(new String[] { url.replaceAll(",", "-"), Integer.toString(contentLength),
					Integer.toString(page.getParseData().getOutgoingUrls().size()), contentType });
			if (contentTypeMap.containsKey(contentType)) {
				contentTypeMap.replace(contentType, contentTypeMap.get(contentType) + 1);
			} else {
				contentTypeMap.put(contentType, 1);
			}
			contentLength = contentLength / 1024;
			if (contentLength < 1) {
				fileSizeMap.replace("s1", fileSizeMap.get("s1") + 1);
			} else if (contentLength < 10) {
				fileSizeMap.replace("s2", fileSizeMap.get("s2") + 1);
			} else if (contentLength < 100) {
				fileSizeMap.replace("s3", fileSizeMap.get("s3") + 1);
			} else if (contentLength < 1024) {
				fileSizeMap.replace("s4", fileSizeMap.get("s4") + 1);
			} else {
				fileSizeMap.replace("s5", fileSizeMap.get("s5") + 1);
			}
		/*
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());
			// count += links.size();	
			//}
		}
		*/
	}

	private void dumpFetchData() {
		fetchData.stream().map(this::convertToCSV).forEach(fetchCsvWriter::println);
		fetchData.clear();
	}

	private void dumpVisitData() {
		visitData.stream().map(this::convertToCSV).forEach(visitCsvWriter::println);
		visitData.clear();
	}

	private void dumpUrlsData() {
		totalExtractedCount.set(urlsData.size() + totalExtractedCount.get());
		urlsData.stream().map(this::convertToCSV).forEach(urlsCsvWriter::println);
		urlsData.clear();
	}

	@Override
	public void onBeforeExit() {
		fetchFailedCount.setRelease(totalFetchCount.get() - fetchSucceededCount.get());
		dumpFetchData();
		dumpUrlsData();
		dumpVisitData();
		fetchCsvWriter.flush();
		fetchCsvWriter.close();
		visitCsvWriter.flush();
		visitCsvWriter.close();
		urlsCsvWriter.flush();
		urlsCsvWriter.close();
		System.out.println(MyCrawler.contentTypeMap);
		System.out.println(MyCrawler.statusCodesMap);
	}
}
