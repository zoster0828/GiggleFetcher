package controller;

import infrastructure.GiggleHttpClient;
import service.Cache;
import service.ContentsFetcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.SQLException;

public class FetcherRunner {
	static Logger logger = LogManager.getLogger("Main");
	public static void main(String args[]) throws InterruptedException, SQLException {
		logger.info("Start content fetcher");
		removePreviousThumbnails();
		new GiggleHttpClient();
		new Cache();
		ContentsFetcher contentsFetcher = new ContentsFetcher();
		while(true) {
			contentsFetcher.run();
			Thread.sleep(600000);
			Cache.removeExpiredSites();
			removePreviousThumbnails();
		}
	}
	private static void removePreviousThumbnails() {
		File file = new File("src/main/resources/thumbnail");
		File[] files = file.listFiles();
		for(File thumbs : files){
			thumbs.delete();
		}
	}
}