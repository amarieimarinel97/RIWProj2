package com.tuiasi.multithreading;

import com.tuiasi.http.CrawlURL;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;


public class MainThread implements ThreadListener {
    private List<CrawlURL> urls;
    private final int WORKERS_NUMBER_LIMIT = 12;
    private int currentWorkersAlive = 0;
    private int urlsCrawled = 0;
    long startTime = System.currentTimeMillis();

    public MainThread(List<CrawlURL> urls) {
        this.urls = urls;
    }

    public void run() throws InterruptedException {
        List<WorkerThread> workers = new ArrayList<>();
        while (!urls.isEmpty()) {
            for (int i = 0; i < urls.size(); ) {
                workers = new ArrayList<>();
                int maxNoOfWorkers = Math.min(urls.size() - i, WORKERS_NUMBER_LIMIT);
                for (int j = i; j < i + maxNoOfWorkers; ++j) {
                    CrawlURL currentUrl = urls.get(j);
                    if (currentUrl.isToProcess()) {
                        WorkerThread worker = new WorkerThread(currentUrl, urls);
                        workers.add(worker);
                    }
                }
                String threads = "";
                for (NotifyingThread worker : workers) {
                    threads += worker.getName() + ", ";
                    worker.addListener(this);
                    worker.start();
                }
                System.out.println("Started threads: " + threads);

                for (NotifyingThread worker : workers)
                    worker.join();
                i += maxNoOfWorkers;


            }
        }
    }

    @Override
    public void onThreadComplete(Thread thread) {
        ++urlsCrawled;
        currentWorkersAlive--;
        if (System.currentTimeMillis() - startTime > 60000) {
            System.out.println("Crawled " + urlsCrawled + " in 1 minute");
            urlsCrawled = 0;
            startTime = System.currentTimeMillis();
        }
        ((WorkerThread) thread).getCrawlURL().setToProcess(false);
        System.out.println("Thread " + thread.getName() + " finished.");
    }
}
