package com.tuiasi.crawling;

import com.tuiasi.http.HTTPUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.tuiasi.http.HTTPUtils.HTTP_PORT;
import static com.tuiasi.http.HTTPUtils.createRequest;
import static com.tuiasi.http.InternalErrorCodes.*;

public class Crawler {

    private static Map<String, List<String>> robotsDisallowRules = new HashMap<>();

    List<CrawlURL> urls;

    public static void launchCrawler(List<CrawlURL> urls){
//        while()
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
            File robotsTxtFile = new File(HTTPUtils.WORKING_PATH + "/" + url + "/" + ROBOTS_TXT_PATH);
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
