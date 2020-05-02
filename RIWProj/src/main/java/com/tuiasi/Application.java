package com.tuiasi;

import com.tuiasi.DNSHandling.DNSResponse;

import java.io.IOException;
import java.io.InvalidObjectException;

import static com.tuiasi.DNSHandling.utils.DNSUtils.createDNSRequest;
import static com.tuiasi.DNSHandling.utils.DNSUtils.sendDNSRequest;

public class Application {

    public static void main(String[] args) throws IOException {
        String domain = "www.tuiasi.ro";

        byte[] requestBuffer = createDNSRequest(domain);
        byte[] responseBuffer = sendDNSRequest(requestBuffer, true);

        try {
            DNSResponse dnsResponse = new DNSResponse(responseBuffer);
            System.out.println(dnsResponse.toString());
        } catch (InvalidObjectException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }


}