package service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.sites.*;
import vo.Site;

import java.util.HashMap;
import java.util.Map;

public class SiteParserMap {
    Logger logger = LogManager.getLogger("ArticleParser");

    public Map<String, SiteParser> getParserMap() {
        return parserMap;
    }

    Map<String, SiteParser> parserMap;

    public SiteParserMap(Map<String, String> siteMap){
        parserMap = new HashMap<>();
        Site daum = new Site("다음카페 인기글",siteMap.get("다음카페 인기글"));
        parserMap.put(daum.getName(), new DaumCafe(daum));
        Site nate = new Site("네이트판",siteMap.get("네이트판"));
        parserMap.put(nate.getName(), new NatePan(nate));
        Site dogDrip = new Site("개드립",siteMap.get("개드립"));
        parserMap.put(dogDrip.getName(), new DogDrip(dogDrip));
        Site theqoo = new Site("theqoo",siteMap.get("theqoo"));
        parserMap.put(theqoo.getName(), new TheQoo(theqoo));
        Site MLBPark = new Site("MLBPark",siteMap.get("MLBPark"));
        parserMap.put(MLBPark.getName(), new MLBPark(MLBPark));
        Site etorrent = new Site("etorrent",siteMap.get("etorrent"));
        parserMap.put(etorrent.getName(), new Etorrent(etorrent));
        Site soccerline = new Site("soccerline",siteMap.get("soccerline"));
        parserMap.put(soccerline.getName(), new SoccerLine(soccerline));
        Site TodayHumor = new Site("TodayHumor",siteMap.get("TodayHumor"));
        parserMap.put(TodayHumor.getName(), new TodayHumor(TodayHumor));
        Site RuliWeb = new Site("RuliWeb",siteMap.get("RuliWeb"));
        parserMap.put(RuliWeb.getName(), new RuliWeb(RuliWeb));
    }
}
