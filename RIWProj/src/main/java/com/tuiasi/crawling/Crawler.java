package com.tuiasi.crawling;

import com.tuiasi.files.utils.FileUtils;
import com.tuiasi.http.CrawlURL;
import com.tuiasi.http.utils.HTTPUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

import static com.tuiasi.exception.InternalErrorCodes.*;
import static com.tuiasi.http.utils.HTTPUtils.*;
import static com.tuiasi.http.utils.LinkHandlingUtils.processLinkToWorkingDirPath;

public class Crawler {

    private static Map<String, List<String>> robotsDisallowRules = new HashMap<>();
    private static final Logger log = Logger.getLogger("Crawler");

    public static List<CrawlURL> urls;

    public static void launchCrawler(){
        while(!urls.isEmpty()){
            urls.forEach(Crawler::processNextUrl);
        }
    }

    private static void processNextUrl(CrawlURL url){
        handleRobotsTxtOfDomain(url.getDomain());
        for(String forbiddenPath : robotsDisallowRules.get(url.getDomain())){
            if(url.toString().endsWith(forbiddenPath))
                return;
        }
        createRequest("/"+url.getPath(), url.getDomain(), HTTP_PORT);
        urls.addAll(retrieveLinksFromHtml(processLinkToWorkingDirPath(url.toString())));
        log.info("Crawled "+url.toString());
    }


    private static List<CrawlURL> retrieveLinksFromHtml(String filePath){
        List<CrawlURL> result = new ArrayList<>();
        String htmlContent = FileUtils.readFromFile(filePath);
        return result;
    }



    public static void handleRobotsTxtOfDomain(String url) {
        if (!robotsDisallowRules.containsKey(url)) {
            List<String> disallow = getRobotsTxt(url);
            robotsDisallowRules.put(url,disallow);
        }
    }

    @SneakyThrows
    public static List<String> getRobotsTxt(String url) {
        if (createRequest(ROBOTS_TXT_PATH, url, HTTP_PORT) == SUCCESS) {

            List<String> disallowRules = new ArrayList<>();
            File robotsTxtFile = new File(processLinkToWorkingDirPath(url+ROBOTS_TXT_PATH));
            Scanner myReader = new Scanner(robotsTxtFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.toLowerCase().trim().startsWith(DISALLOW))
                    disallowRules.add(data.split(":")[1].trim());

            }
            myReader.close();
            return disallowRules;
        }
        return new ArrayList<>();
    }

    private static final String ROBOTS_TXT_PATH = "/robots.txt";
    private static final String DISALLOW = "disallow";

}
