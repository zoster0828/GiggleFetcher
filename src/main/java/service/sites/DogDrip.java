package service.sites;

import infrastructure.GiggleHttpClient;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import service.Cache;
import vo.Content;
import vo.Site;

import java.util.ArrayList;
import java.util.List;

@Data
public class DogDrip implements SiteParser{
    String originalUrl = "https://www.dogdrip.net";
    String articleListPath = "#main > div > div.eq.section.secontent.background-color-content > div > div.ed.board-list > table > tbody > tr";
    String titlePath = "head > title";
    String thumbnailPath = "#article_1 > div > p > img";
    String descPath = "head > meta[property=og:description]";
    Site site;


    public DogDrip(Site site){
        this.site = site;
    }


    @Override
    public List<Content> getContentList() {
        List<String> articleList = getArticleList();
        List<Content> contentList = new ArrayList<>();
        for(String contentUrl : articleList){
            if(Cache.dontHas(contentUrl))
                contentList.add(getContent(contentUrl));
        }
        return contentList;
    }

    @Override
    public List<String> getArticleList(){
        String listPage = GiggleHttpClient.get(site.getUrl());
        List<String> articleList = new ArrayList<>();

        Document document = Jsoup.parse(listPage);

        Elements articles = document.select(articleListPath);

        List<Element> elementList = articles.subList(0,articles.size());
        for(Element element : elementList){
            String url = element.select("a").attr("href");
            articleList.add(url);
        }
        return articleList;
    }

    @Override
    public Content getContent(String contentUrl){
        String response = GiggleHttpClient.get(contentUrl);
        Document document = Jsoup.parse(response);
        String title = document.select(titlePath).text().replace("- DogDrip.Net 개드립","");
        String count = "";
        String date = "";
        String thumbnail_image="";
        if(document.select(thumbnailPath).attr("src").startsWith("http")){
            thumbnail_image = document.select(thumbnailPath).attr("src");
        }else {
            thumbnail_image = originalUrl + document.select(thumbnailPath).attr("src");
        }
        String s3Thumb="";
        String desc = "";
        return new Content(site.getName(),title,contentUrl,thumbnail_image,s3Thumb,desc);
    }
}
