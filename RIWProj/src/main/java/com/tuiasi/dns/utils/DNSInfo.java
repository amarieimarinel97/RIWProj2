package com.tuiasi.dns.utils;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DNSInfo {
    private LocalDateTime timestamp;
    private String domain;
}
