package com.tuiasi;

import com.tuiasi.dns.DNSResponse;
import com.tuiasi.dns.utils.DNSUtils;

import static com.tuiasi.http.HTTPUtils.createRequest;

public class Application {

    public static void main(String[] args) {

        //LAB05
        String domain = "www.tuiasi.ro";
        DNSResponse dnsResponse = null;
        try {
            dnsResponse = DNSUtils.getDNSResponseFromDomain(domain);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //LAB06
        assert dnsResponse != null;
        createRequest("/", domain, 80);

    }
}