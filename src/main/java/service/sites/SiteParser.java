package service.sites;

import vo.Content;
import vo.Site;

import java.util.List;

public interface SiteParser {
    public Site getSite();
    public List<Content> getContentList();
    public Content getContent(String contentUrl);
    public List<String> getArticleList();
}
