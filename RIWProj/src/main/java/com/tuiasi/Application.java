package com.tuiasi;

import com.tuiasi.crawling.Crawler;
import com.tuiasi.http.CrawlURL;
import lombok.SneakyThrows;

public class Application {

    @SneakyThrows
    public static void main(String[] args) {
        Crawler.urls.add(new CrawlURL("https://www.imdb.com/chart/moviemeter"));
        Crawler.urls.add(new CrawlURL("http://www.cnn.com"));
        Crawler.urls.add(new CrawlURL("https://www.reddit.com/r/all"));

        Crawler.launchCrawler();
    }
}