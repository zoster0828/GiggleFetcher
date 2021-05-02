package service.sites;

import infrastructure.GiggleHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.Cache;
import vo.Content;
import vo.Site;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EtorrentTest {

    Site etorrent = new Site("etorrent", "https://etoland.co.kr/plugin/mobile/");

    @Test
    void getListTest(){
        new GiggleHttpClient();
        Etorrent eto = new Etorrent(etorrent);
        List<String> articleList = eto.getArticleList();
        Assertions.assertEquals(8, articleList.size());
        System.out.println(articleList);
    }

    @Test
    void parsingTest(){
        new GiggleHttpClient();
        new Cache();
        Etorrent eto = new Etorrent(etorrent);
        List<Content> articleList = eto.getContentList();
        Assertions.assertEquals(8, articleList.size());
        System.out.println(articleList);
    }
}