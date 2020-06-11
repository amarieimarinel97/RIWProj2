package com.tuiasi.dns;

import com.tuiasi.dns.utils.DNSInfo;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.tuiasi.dns.utils.DNSUtils.createDNSRequest;
import static com.tuiasi.dns.utils.DNSUtils.sendDNSRequest;

public class DNSCache {
    private static final double CACHE_AVAILABILITY = 60; // in minutes
    private static Map<DNSInfo, DNSResponse> dnsCacheMap = new HashMap<>();


    public static DNSResponse getDNSResponseFromDomainWithCache(String domain) throws IOException {
        Optional<DNSResponse> cachedDnsResponse = getCachedDns(domain);
        if (cachedDnsResponse.isPresent())
            return cachedDnsResponse.get();

        byte[] requestBuffer = createDNSRequest(domain);
        byte[] responseBuffer = sendDNSRequest(requestBuffer, false);
        DNSResponse dnsResponse = new DNSResponse(responseBuffer);
        setDnsCache(domain, dnsResponse);
        return dnsResponse;
    }

    public static Optional<DNSResponse> getCachedDns(String domain) {
        for (DNSInfo dnsInfo : dnsCacheMap.keySet())
            if (dnsInfo.getDomain().toLowerCase().trim().equals(domain.toLowerCase().trim())) {
                if (Duration.between(dnsInfo.getTimestamp(), LocalDateTime.now()).toMinutes() > CACHE_AVAILABILITY)
                    return Optional.of(dnsCacheMap.get(dnsInfo));
                else
                    dnsCacheMap.remove(dnsInfo);
            }

        return Optional.empty();
    }

    public static void setDnsCache(String domain, DNSResponse dnsResponse) {
        dnsCacheMap.put(DNSInfo.builder().domain(domain).timestamp(LocalDateTime.now()).build(),
                dnsResponse);
    }
}
