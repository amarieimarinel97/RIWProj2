package com.tuiasi.dns;

import com.tuiasi.dns.utils.DNSInfo;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.tuiasi.dns.utils.DNSUtils.createDNSRequest;
import static com.tuiasi.dns.utils.DNSUtils.sendDNSRequest;

public class DNSCache {
    private static final double CACHE_AVAILABILITY = 60; // in minutes
    private static  Map<DNSInfo, DNSResponse> dnsCacheMap = new HashMap<>();


    public synchronized static DNSResponse getDNSResponseFromDomainWithCache(String domain) throws IOException {
        Optional<DNSResponse> cachedDnsResponse = getCachedDns(domain);
        if (cachedDnsResponse.isPresent())
            return cachedDnsResponse.get();

        byte[] requestBuffer = createDNSRequest(domain);
        byte[] responseBuffer = sendDNSRequest(requestBuffer, false);
        DNSResponse dnsResponse = new DNSResponse(responseBuffer);
        setDnsCache(domain, dnsResponse);
        return dnsResponse;
    }

    private synchronized static Optional<DNSResponse> getCachedDns(String domain) {
        List<DNSInfo> invalidCacheCopies = new ArrayList<>();
        for (DNSInfo dnsInfo : dnsCacheMap.keySet())
            if (dnsInfo.getDomain().toLowerCase().trim().equals(domain.toLowerCase().trim())) {
                if (Duration.between(dnsInfo.getTimestamp(), LocalDateTime.now()).toMinutes() > CACHE_AVAILABILITY)
                    return Optional.of(dnsCacheMap.get(dnsInfo));
                else
                    invalidCacheCopies.add(dnsInfo);
            }
        removeInvalidDnsCache(invalidCacheCopies);
        return Optional.empty();
    }

    private synchronized static void removeInvalidDnsCache(List<DNSInfo> invalidCacheCopies) {
        for (DNSInfo dnsInfo : invalidCacheCopies)
            dnsCacheMap.remove(dnsInfo);
    }

    private synchronized static void setDnsCache(String domain, DNSResponse dnsResponse) {
        dnsCacheMap.put(DNSInfo.builder().domain(domain).timestamp(LocalDateTime.now()).build(),
                dnsResponse);
    }
}
