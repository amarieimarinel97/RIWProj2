package com.tuiasi;

import com.tuiasi.crawling.Crawler;
import com.tuiasi.http.CrawlURL;
import lombok.SneakyThrows;

public class Application {

    @SneakyThrows
    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        Crawler.urls.add(new CrawlURL("www.ac.tuiasi.ro"));
        Crawler.urls.add(new CrawlURL("https://www.reddit.com/r/GlobalOffensive/"));
        Crawler.urls.add(new CrawlURL("https://www.imdb.com/chart/moviemeter"));
        Crawler.launchCrawler();
    }
}