package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import db.ContentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.sites.SiteParser;
import vo.Content;
import vo.Site;

public class ContentsFetcher {
	Logger logger = LogManager.getLogger("ContentsFetcher");
	ContentRepository contentRepository = null;
	Map<String, String> siteMap = null;
	ThumbnailDomain thumbnailDomain;
	SiteParserMap siteParserMap;

	public void init(){
		siteMap = loadSiteList();
		thumbnailDomain = new ThumbnailDomain();
		contentRepository = new ContentRepository();
		siteParserMap = new SiteParserMap(siteMap);
	}

	public void run() throws SQLException {
		init();
		for(SiteParser siteParser : siteParserMap.getParserMap().values()){
			List<Content> contentList = siteParser.getContentList();


			for(Content content : contentList){
				String s3ThumbUrl = thumbnailDomain.getS3ThumbmailUrl(content.getThumbnailUrl());

				content.setS3Thumb(s3ThumbUrl);
				try {
					contentRepository.save(content);
					Thread.sleep(100000);
				}catch(Exception e){
					System.out.println("something wrong");
					e.printStackTrace();
				}
				finally {
					Cache.set(content.getUrl());
				}
			}
			logger.info("Done : "+siteParser.getSite().getName());
		}
		contentRepository.close();
	}

	private Map<String, String> loadSiteList() {
		File file = new File("src/main/resources/Site.list");
		HashMap<String,String> listMap = new HashMap<String,String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			logger.error("Site.list File can not found");
			System.exit(1);
		}

		String temp="";

		String jsonList="";

		while(true)
		{
			try {
				if ((temp = br.readLine()) == null) break;
			} catch (IOException e) {
				logger.error("SiteList IOException");
				System.exit(1);
			}
			jsonList+=temp;
		}
		Gson gson = new Gson();
		return gson.fromJson(jsonList, new TypeToken<Map<String, String>>(){}.getType());
	}
}
