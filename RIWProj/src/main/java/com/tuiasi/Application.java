package com.tuiasi;

import com.tuiasi.crawling.CrawlURL;
import com.tuiasi.crawling.Crawler;
import com.tuiasi.dns.DNSResponse;
import com.tuiasi.dns.utils.DNSUtils;
import lombok.SneakyThrows;

import java.net.URI;

import static com.tuiasi.http.HTTPUtils.HTTP_PORT;
import static com.tuiasi.http.HTTPUtils.createRequest;

public class Application {

    @SneakyThrows
    public static void main(String[] args) {

        //LAB05
        String domain = "www.ac.tuiasi.ro/something/else?id=1&a=b#asd";
//        DNSResponse dnsResponse = null;
//        try {
//            dnsResponse = DNSUtils.getDNSResponseFromDomain(domain);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        System.out.println(dnsResponse.getIPAddress());
//
//        //LAB06
//        assert dnsResponse != null;
//         createRequest("/", domain, HTTP_PORT);
        System.out.println(new CrawlURL(domain).toString());
//        Crawler.handleRobotsTxtOfDomain(domain);
    }
}