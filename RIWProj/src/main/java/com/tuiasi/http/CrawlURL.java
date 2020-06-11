package com.tuiasi.http;

import lombok.Data;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.tuiasi.http.utils.LinkHandlingUtils.processLinkToStandardForm;

@Data
public class CrawlURL {
    private String domain;
    private String protocol;
    private String path;
    private Optional<LocalDateTime> nextAccess;
    private boolean toProcess;
    private String ipAddress;

    @Override
    public String toString() {
        return this.protocol + "://" + this.getDomain() + this.path;
    }

    public CrawlURL(String link) throws URISyntaxException {
        boolean isHttps = false;
        this.toProcess=true;
        if (link.startsWith("https"))
            isHttps = true;

        link = processLinkToStandardForm(link);
        link = link.toLowerCase().trim().startsWith("www") ? "http://" + link : link;

        URI uri = new URI(link);
        this.path =
                (Objects.isNull(uri.getPath()) ? "" : uri.getPath()) +
                        (Objects.isNull(uri.getQuery()) ? "" : "?" + uri.getQuery()) +
                            (Objects.isNull(uri.getFragment()) ? "" : "#" + uri.getFragment());
        this.domain = getFixedDomain(uri.getHost());
        this.protocol = isHttps ? "https" : uri.getScheme();
        this.nextAccess = Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrawlURL crawlURL = (CrawlURL) o;
        return domain.equals(crawlURL.domain) &&
                protocol.equals(crawlURL.protocol) &&
                path.equals(crawlURL.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, protocol, path);
    }

    private String getFixedDomain(String host) throws URISyntaxException {
        int pointsNo = 0;
        if(Objects.isNull(host))
            throw new URISyntaxException("Malformed url","Null host");
        for (int i = 0; i < host.length(); i++)
            if (host.charAt(i) == '.')
                pointsNo++;
        if (pointsNo < 3)
            return host;
        else {
            String[] elements = host.split("\\.");
            this.toProcess=false;
            return "www." + elements[elements.length - 2] + "." + elements[elements.length - 1];
        }
    }
}
