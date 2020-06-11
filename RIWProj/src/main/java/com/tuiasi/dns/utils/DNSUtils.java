package com.tuiasi.dns.utils;

import com.tuiasi.dns.DNSResponse;
import lombok.SneakyThrows;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNSUtils {

    public static byte[] createDNSRequest(String domain) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeShort(ID);
        dataOutputStream.writeShort(QUERY_FLAGS);
        dataOutputStream.writeShort(QUESTION_COUNT);
        dataOutputStream.writeShort(ANSWER_COUNT);
        dataOutputStream.writeShort(AUTHORITY_COUNT);
        dataOutputStream.writeShort(ADDITIONAL_COUNT);

        writeDomainToBuffer(dataOutputStream, domain);
        dataOutputStream.writeByte(ZERO_PADDING);
        dataOutputStream.writeShort(REQUEST_TYPE);
        dataOutputStream.writeShort(CLASS_TYPE);

        return byteArrayOutputStream.toByteArray();
    }

    public static String readIPAddress(DataInputStream din) throws IOException {
        short addrLen = din.readShort();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < addrLen; i++)
            sb.append(String.format("%d", (din.readByte() & 0xFF)) + ".");
        return sb.toString().substring(0, sb.length() - 1);
    }

    public static void writeDomainToBuffer(DataOutputStream dos, String domain) throws IOException {
        String[] domainParts = domain.split("\\.");

        for (int i = 0; i < domainParts.length; i++) {
            byte[] domainBytes = domainParts[i].getBytes();
            dos.writeByte(domainBytes.length);
            dos.write(domainBytes);
        }
    }

    public static byte[] sendDNSRequest(byte[] DNSMessage, boolean printMessages) throws IOException {
        if (printMessages) {
            System.out.println("\n==================================");
            for (int i = 0; i < DNSMessage.length; i++) {
                System.out.print(" 0x" + String.format("%x", DNSMessage[i]) + "\t");
                if (i % 8 == 7)
                    System.out.println();
            }
            System.out.println("\n==================================");
        }

        InetAddress ipAddress = InetAddress.getByName(DNS_SERVER_ADDRESS);

        DatagramSocket socket = new DatagramSocket();
        DatagramPacket dnsRequest = new DatagramPacket(DNSMessage, DNSMessage.length, ipAddress, DNS_SERVER_PORT);
        socket.send(dnsRequest);
        System.out.println("Request sent");

        byte[] responseBuffer = new byte[512];
        DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length);
        socket.receive(response);
        System.out.println("Response received");

        if (printMessages) {
            System.out.println("\n==================================");
            for (int i = 0; i < response.getLength(); i++) {
                System.out.print(" 0x" + String.format("%x", responseBuffer[i]) + "\t");
                if (i % 8 == 7)
                    System.out.println();
            }
            System.out.println("\n==================================");

        }

        return responseBuffer;
    }

    public static DNSResponse getDNSResponseFromDomain(String domain) throws IOException {
        byte[] requestBuffer = createDNSRequest(domain);
        byte[] responseBuffer = sendDNSRequest(requestBuffer, false);
        return new DNSResponse(responseBuffer);
    }


    public static final int ID = 0x0010;
    public static final int QUERY_FLAGS = 0x0100;
    public static final int QUESTION_COUNT = 0x0001;
    public static final int ANSWER_COUNT = 0x0000;
    public static final int AUTHORITY_COUNT = 0x0000;
    public static final int ADDITIONAL_COUNT = 0x0000;
    public static final int ZERO_PADDING = 0x00;
    public static final int REQUEST_TYPE = 0x0001; //Host request
    public static final int CLASS_TYPE = 0x0001;


    public static final String DNS_SERVER_ADDRESS = "8.8.8.8";
    public static final int DNS_SERVER_PORT = 53;
}
