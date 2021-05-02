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
import java.util.HashMap;
import java.util.List;

@Data
public class TheQoo implements SiteParser{
    String originalUrl = "https://theqoo.net";
    String articleListPath = "#bd_801402415_0 > div > table > tbody > tr:not(.notice) > td.title";
    String titlePath = "body > meta[property=og:title]";
    String thumbnailPath = "article img";
    String descPath = "body > meta[property=og:description]";
    Site site;

    public TheQoo(Site site){
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

//        List<Element> elementList = articles.subList(0,articles.size());
        List<Element> elementList = articles;
        for(Element element : elementList){
            String url = element.select("a").attr("href");
            articleList.add(originalUrl+url);
        }
        return articleList;
    }

    @Override
    public Content getContent(String contentUrl){
        HashMap<String, String> headers = new HashMap<>();
        headers.put(":autority:","theqoo.net");
        headers.put(":method:","GET");
        headers.put(":path:",contentUrl.replace("https://theqoo.net/",""));
        headers.put(":scheme:","https");
        String response = GiggleHttpClient.get(contentUrl, headers);
        Document document = Jsoup.parse(response);
        String title = document.select(titlePath).attr("content");
        String count = "";
        String date = "";
        String thumbnail_image="";

        Element thumb;
        Elements el = document.select(thumbnailPath);
        if(document.select(thumbnailPath).size() > 0) {
            thumb = document.select(thumbnailPath).get(0);
            if(thumb.attr("src").startsWith("http")){
                thumbnail_image = document.select(thumbnailPath).attr("src");
            }else{
                thumbnail_image = "http:" + thumb.attr("src");
            }
        }
        else{
            thumbnail_image = "";
        }


        String s3Thumb="";
        String desc = document.select(descPath).attr("content");
        return new Content(site.getName(),title,contentUrl,thumbnail_image,s3Thumb,desc);
    }
}
