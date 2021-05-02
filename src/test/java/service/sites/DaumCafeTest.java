package service.sites;

import infrastructure.GiggleHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.Cache;
import vo.Content;
import vo.Site;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DaumCafeTest {

    Site daum = new Site("다음카페 인기글", "https://m.cafe.daum.net");
    @Test
    void getListTest(){
        new GiggleHttpClient();
        DaumCafe daumCafe = new DaumCafe(daum);
        List<String> articleList = daumCafe.getArticleList();
        Assertions.assertEquals(10, articleList.size());
        System.out.println(articleList);
    }

    @Test
    void parsingTest(){
        new GiggleHttpClient();
        new Cache();
        DaumCafe daumCafe = new DaumCafe(daum);
        List<Content> daumCafeContentList = daumCafe.getContentList();
        Assertions.assertEquals(10, daumCafeContentList.size());
        System.out.println(daumCafeContentList);
    }
}