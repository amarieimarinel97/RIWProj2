package com.tuiasi.crawling;

import com.tuiasi.exception.InternalErrorCodes;
import com.tuiasi.files.utils.FileUtils;
import com.tuiasi.http.CrawlURL;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.util.*;

import static com.tuiasi.exception.InternalErrorCodes.NOT_FOUND;
import static com.tuiasi.exception.InternalErrorCodes.SUCCESS;
import static com.tuiasi.files.utils.FileUtils.checkIfStringIsNotEmpty;
import static com.tuiasi.http.utils.HTTPUtils.HTTP_PORT;
import static com.tuiasi.http.utils.HTTPUtils.createRequest;
import static com.tuiasi.http.utils.LinkHandlingUtils.processLinkToWorkingDirPath;

public class Crawler {

    private static Map<String, List<String>> robotsDisallowRules = new HashMap<>();

    public static List<CrawlURL> urls = new ArrayList<>();

    public static void launchCrawler() {
        int urlsCrawled = 0;
        long startTime = System.currentTimeMillis();


        while (!urls.isEmpty()) {
            for (int i = 0; i < urls.size(); ++i) {
                ++urlsCrawled;
                if (urls.get(i).isToProcess())
                    Crawler.processNextUrl(urls.get(i));
                if (System.currentTimeMillis() - startTime > 60000) {
                    System.out.println("Crawled " + urlsCrawled + " in 1 minute");
                    urlsCrawled = 0;
                    startTime = System.currentTimeMillis();
                }
            }
        }
    }

    private static void processNextUrl(CrawlURL crawlURL) {
        System.out.println("===============\nINFO: Crawling " + crawlURL.toString());
        if (!robotsDisallowRules.containsKey(crawlURL.getDomain()))
            handleRobotsTxtOfDomain(crawlURL);
        for (String forbiddenPath : robotsDisallowRules.get(crawlURL.getDomain())) {
            if (crawlURL.toString().endsWith(forbiddenPath) && !forbiddenPath.equals("/"))
                return;
        }
        InternalErrorCodes code;
        try {
            code = createRequest(crawlURL, HTTP_PORT);
        } catch (UncheckedIOException e) {
            code = NOT_FOUND;
        }
        switch (code) {
            case SUCCESS:
                urls.addAll(retrieveLinksFromHtml(processLinkToWorkingDirPath(crawlURL.toString())));
                System.out.println("INFO: Crawled " + crawlURL.toString());
                break;
            case REMOVE_FROM_QUEUE:
                urls.remove(crawlURL);
                System.out.println("WARN: Removed from queue " + crawlURL.toString());
                break;
            case NOT_FOUND:
                System.out.println("WARN: Url not found");
            case ADD_DELAY:
        }


    }


    private static List<CrawlURL> retrieveLinksFromHtml(String filePath) {
        List<CrawlURL> result = new ArrayList<>();
        String htmlContent = "";
        try {
            htmlContent = FileUtils.readFromFile(filePath);
        } catch (FileNotFoundException e) {
            System.out.println("WARN: Couldn't access file " + filePath);
            return result;
        }
        Document doc = Jsoup.parse(htmlContent);

        doc.select("a").forEach(el -> {
            if (el.hasAttr("href")) {
                String absoluteUrl = el.absUrl("href");
                if (checkIfStringIsNotEmpty(absoluteUrl)) {
                    try {
                        CrawlURL crawlURL = new CrawlURL(absoluteUrl);
                        if (!urls.contains(crawlURL)) {
                            result.add(crawlURL);
                        }
                    } catch (URISyntaxException ignored) {
                    }
                }
            }
        });
        return result;
    }

    @SneakyThrows
    public static void handleRobotsTxtOfDomain(CrawlURL crawlURL) {
        if (!robotsDisallowRules.containsKey(crawlURL.getDomain())) {
            List<String> disallow = getRobotsTxt(crawlURL);
            robotsDisallowRules.put(crawlURL.getDomain(), disallow);
        }
    }


    public static List<String> getRobotsTxt(CrawlURL crawlURL) throws URISyntaxException {
        CrawlURL robotsUrl = new CrawlURL(crawlURL.getDomain() + ROBOTS_TXT_PATH);
        if (createRequest(robotsUrl, HTTP_PORT) == SUCCESS) {

            List<String> disallowRules = new ArrayList<>();
            File robotsTxtFile = new File(processLinkToWorkingDirPath(crawlURL.getDomain() + ROBOTS_TXT_PATH));
            Scanner myReader = null;
            try {
                myReader = new Scanner(robotsTxtFile);
            } catch (FileNotFoundException e) {
                return disallowRules;
            }
            boolean isGeneralUserAgent = false;
            while (myReader.hasNextLine()) {

                String data = myReader.nextLine();
                if (data.toLowerCase().trim().equals(GENERAL_USER_AGENT))
                    isGeneralUserAgent = true;

                if (isGeneralUserAgent && data.toLowerCase().trim().startsWith(DISALLOW))
                    disallowRules.add(data.split(":")[1].trim());

            }
            myReader.close();
            return disallowRules;
        }
        return new ArrayList<>();
    }

    private static final String ROBOTS_TXT_PATH = "/robots.txt";
    private static final String DISALLOW = "disallow";
    private static final String GENERAL_USER_AGENT = "user-agent: *";

}
