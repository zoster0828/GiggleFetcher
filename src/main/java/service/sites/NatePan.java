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
public class NatePan implements SiteParser{
    String originalUrl = "https://m.pann.nate.com";
    String articleListPath = "ol.list.list_type2 > li";
    String titlePath = "head > meta[property=og:title]";
    String countPath = "#wrap > div.view-wrap.talk > div.pann-title > div.writer > div:nth-child(2) > span:nth-child(1)";
    String thumbnailPath = "head > meta[property=og:image]";
    String descPath = "head > meta[property=og:description]";
    String defaultThumbnail = "https://gigglethumbnail.s3.ap-northeast-2.amazonaws.com/nate.png";
    Site site;

    public NatePan(Site site){
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
            String url = originalUrl+element.select("a").attr("href");
            articleList.add(url);
        }
        return articleList;
    }

    @Override
    public Content getContent(String contentUrl){
        String response = GiggleHttpClient.get(contentUrl);
        Document document = Jsoup.parse(response);
        String title = document.select(titlePath).attr("content");
        String count = "";
        String date = "";
        String thumbnail_image=document.select(thumbnailPath).attr("content");
        if(thumbnail_image.isEmpty()){
            thumbnail_image = defaultThumbnail;
        }
        String s3Thumb="";
        String desc = document.select(descPath).attr("content");
        return new Content(site.getName(),title,contentUrl,thumbnail_image,s3Thumb,desc);
    }
}
