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
public class DaumCafe implements SiteParser{
    String articleListPath = "ul.popular-list > li";
    String titlePath = "h3.tit_subject";
    String countPath = "span.num_subject";
    String thumbnailPath = "head > meta[property=og:image]";
    String descPath = "head > meta[property=og:description]";
    Site site;

    public DaumCafe(Site site){
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

        if(articles.size() > 9) {
            List<Element> elementList = articles.subList(0, 10);
            for (Element element : elementList) {
                String url = site.getUrl() + element.select("a").attr("href");
                articleList.add(url);
            }
        }
        return articleList;
    }

    @Override
    public Content getContent(String contentUrl){
        Cache.set(contentUrl);
        String response = GiggleHttpClient.get(contentUrl);
        Document document = Jsoup.parse(response);
        String title = document.select(titlePath).text();
        String count = document.select(countPath).text();
        String date = "";
        String thumbnail_image=document.select(thumbnailPath).attr("content");
        String s3Thumb="";
        String desc = document.select(descPath).attr("content");
        return new Content(site.getName(),title,contentUrl,thumbnail_image,s3Thumb,desc);
    }
}
