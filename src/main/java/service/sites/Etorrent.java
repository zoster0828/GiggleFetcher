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
public class Etorrent implements SiteParser{
    String originalUrl = "https://etoland.co.kr/plugin/mobile/";
    String articleListPath = "#mw_mobile > div > div.main_tab > div > div > ul > li";
    String titlePath = "head > meta[property=og:title]";
    String countPath = "";
    String thumbnailPath = "#mw_mobile > div.write_content > p:nth-child(2) > img";
    String secondaryThumbnailPath = "#mw_mobile > div.write_content > p:nth-child(1) > img";
    String descPath = "head > meta[property=og:description]";
    String defaultThumbnail = "https://gigglethumbnail.s3.ap-northeast-2.amazonaws.com/etorrent.png";

    Site site;

    public Etorrent(Site site){
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

        List<Element> elementList = articles.subList(0, 8);
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
        String thumbnail_image="";
        Element thumb;
        if(document.select(thumbnailPath).size() > 0) {
            thumb = document.select(thumbnailPath).get(0);
            if(thumb.attr("src").startsWith("http")){
                thumbnail_image = thumb.attr("src");
            }else{
                thumbnail_image = "http://etoland.co.kr/" + thumb.attr("src").replace("../../","");
            }
        }
        else{
            if(document.select(secondaryThumbnailPath).size() > 0) {
                thumb = document.select(secondaryThumbnailPath).get(0);
                if(thumb.attr("src").startsWith("http")){
                    thumbnail_image = thumb.attr("src");
                }else{
                    thumbnail_image = "http://etoland.co.kr/" + thumb.attr("src").replace("../../","");
                }
            }else{
                thumbnail_image = "";
            }
        }
        String s3Thumb="";
        String desc = document.select(descPath).attr("content").replace("인기게시판,HIT,인기,BEST,베스트","");
        return new Content(site.getName(),title,contentUrl,thumbnail_image,s3Thumb,desc);
    }
}
