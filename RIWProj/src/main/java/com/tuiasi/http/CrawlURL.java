package com.tuiasi.http;

import lombok.Data;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Data
public class CrawlURL {
    private String domain;
    private String protocol;
    private String path;
    private Optional<LocalDateTime> nextAccess;
    private boolean wasProcessed;

    @Override
    public String toString() {
        return this.protocol+"://"+this.getDomain()+"/"+this.path;
    }

    public CrawlURL(String link) throws URISyntaxException {
        link = link.toLowerCase().trim().startsWith("www") ? "http://" + link : link;
        URI uri = new URI(link);
        this.path =
                (Objects.isNull(uri.getPath()) ? "" : uri.getPath()) +
                        (Objects.isNull(uri.getQuery()) ? "" : "?" + uri.getQuery());
        this.domain = uri.getHost();
        this.protocol = uri.getScheme();
        this.nextAccess = Optional.empty();
        this.wasProcessed=false;
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
}
