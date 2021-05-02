package service.sites;

import infrastructure.GiggleHttpClient;
import lombok.Data;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import service.Cache;
import vo.Content;
import vo.Site;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SoccerLine implements SiteParser{
    String originalUrl = "http://mlbpark.donga.com/";
    String articleListPath = "#colBrdCon > div > ul.type.left > li:nth-child(1)";
    String titlePath = "head > title";
    String thumbnailPath = "div > div.txtBox > div > img";
    String secondaryThumbnailPath = "div > div.txtBox > div > p:nth-child(1) > img";
    String descPath = "head > meta[property=og:description]";
    Site site;

    public SoccerLine(Site site){
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

        List<Element> elementList = articles.subList(0,25);
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
        String title = document.select(titlePath).attr("content").replace(" : MLBPARK","");
        String count = "";
        String date = "";
        String thumbnail_image="";

        Element thumb;
        Elements el = document.select(thumbnailPath);
        if(document.select(thumbnailPath).size() > 0) {
            thumb = document.select(thumbnailPath).get(0);
            if(thumb.attr("src").startsWith("http")){
                thumbnail_image = thumb.attr("src");
            }else{
                thumbnail_image = "http://" + thumb.attr("src");
            }
        }
        else{
            if(document.select(secondaryThumbnailPath).size() > 0) {
                thumb = document.select(secondaryThumbnailPath).get(0);
                if(thumb.attr("src").startsWith("http")){
                    thumbnail_image = thumb.attr("src");
                }else{
                    thumbnail_image = "http://" + thumb.attr("src");
                }
            }else{
                thumbnail_image = "";
            }
        }


        String s3Thumb="";
        String desc = document.select(descPath).attr("content");
        return new Content(site.getName(),title,contentUrl,thumbnail_image,s3Thumb,desc);
    }
}
