package com.tuiasi.crawling;

import lombok.Data;
import lombok.SneakyThrows;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Data
public class CrawlURL {
    private String domain;
    private String protocol;
    private String path;
    private Optional<LocalDateTime> nextAccess;

    @SneakyThrows
    public CrawlURL(String link) {
        link = link.toLowerCase().trim().startsWith("www") ? "http://" + link : link;
        URI uri = new URI(link);
        this.path =
                (Objects.isNull(uri.getPath()) ? "" : uri.getPath()) +
                        (Objects.isNull(uri.getQuery()) ? "" : "?" + uri.getQuery());
        this.domain = uri.getHost();
        this.protocol = uri.getScheme();
        this.nextAccess = Optional.empty();
    }
}
