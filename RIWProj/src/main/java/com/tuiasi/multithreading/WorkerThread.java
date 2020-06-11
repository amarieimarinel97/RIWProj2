package com.tuiasi.multithreading;

import com.tuiasi.crawling.Crawler;
import com.tuiasi.http.CrawlURL;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class WorkerThread extends NotifyingThread{
    private CrawlURL crawlURL;
    private List<CrawlURL> urls;


    @Override
    public void doRun() {
        System.out.println("Thread "+this.getName()+" started.");
        Crawler.processNextUrl(crawlURL, urls);
    }

}
