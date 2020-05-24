package com.tuiasi.http;

import com.tuiasi.exception.BadGatewayException;
import com.tuiasi.exception.BadRequestException;
import com.tuiasi.exception.NotFoundException;
import com.tuiasi.exception.UnknownCodeException;
import lombok.SneakyThrows;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tuiasi.http.InternalErrorCodes.*;

public class HTTPUtils {
    public final static String USER_AGENT = "CLIENT RIW";
    public final static String WORKING_PATH = "./http";
    private static final String HTTP_VERSION = "HTTP/1.1";
    public static final Integer HTTP_PORT = 80;

    @SneakyThrows
    public static InternalErrorCodes createRequest(String path, String domain, int port) {
        String response = "";
        HttpsURLConnection httpsRequest = null;
        String httpRequest;
        if (isHttpsRequest(domain)) {
            httpsRequest = generateHTTPSGetRequest(domain, path);
        } else {
            httpRequest = genereateGetRequest(path, domain, USER_AGENT);
            BufferedReader bufferedReader = sendHttpRequest(domain, port, httpRequest);
            response = bufferedReader.lines().collect(Collectors.joining("\n"));
        }

        try {
            handleStatusCode(response, path, domain, Optional.ofNullable(httpsRequest));
            return SUCCESS;
        } catch (BadRequestException | UnknownCodeException e) {
            return REMOVE_FROM_QUEUE;
        } catch (BadGatewayException e) {
            return ADD_DELAY;
        } catch (NotFoundException e) {
            return NOT_FOUND;
        }
    }


    public static boolean isHttpsRequest(String domain) {
        return domain.toLowerCase().trim().startsWith("https");
    }

    @SneakyThrows
    private static String writeHTMLBody(String path, String domain, String response, String headers) {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(response));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (!headers.contains(line))
                sb.append(line).append("\n");
        }

        String filePath = getFilePath(path, domain);

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(sb.toString());
        writer.close();
        return sb.toString();
    }

    private static String getFilePath(String path, String domain) {
        String filePath = WORKING_PATH + "/" +
                (domain.toLowerCase().trim().startsWith("http") ?
                        "www."+domain.split("//")[1]
                        : domain)
                + path;


        if (!(filePath.endsWith(".html") || filePath.endsWith("htm")) && !(path.equals("/robots.txt") || domain.endsWith("robots.txt"))) {
            if (!filePath.endsWith("/")) {
                filePath += "/";
            }
            filePath += "index.html";
        }

        File file = new File(filePath);
        if (!file.exists()) {
            File pDir = file.getParentFile();
            if (!pDir.exists()) {
                pDir.mkdirs(); // mkdir  -p
            }
        }
        return filePath;
    }

    @SneakyThrows
    private static String getResponseHeaders(String response) {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(response));
        String line = null;
        StringBuilder sb = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null) {
            if (line.equals(""))
                break;
            sb.append(line).append("\n");
        }
        bufferedReader.close();
        return sb.toString();
    }

    public static String genereateGetRequest(String path, String domain, String userAgent) {
        StringBuilder sb = new StringBuilder();
        sb.append("GET ").append(path).append(" ").append(HTTP_VERSION).append("\r\n");
        sb.append("Host: ").append(domain).append("\r\n");
        sb.append("User-Agent: ").append(userAgent).append("\r\n");
        sb.append("Connection: close\r\n");
        sb.append("\r\n");
        return sb.toString();
    }


    @SneakyThrows
    public static BufferedReader sendHttpRequest(String domain, int port, String request) {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(domain, port));
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dataOutputStream.writeBytes(request);
        return bufferedReader;
    }

    public static HttpsURLConnection generateHTTPSGetRequest(String domain, String path) throws IOException {
        URL url = new URL(domain + path);
        return (HttpsURLConnection) url.openConnection();
    }

    private static int getStatusCode(String response) {
        return Integer.parseInt(response.substring(HTTP_VERSION.length(), HTTP_VERSION.length() + 4).trim());
    }

    public static void handleStatusCode(String response, String path, String domain, Optional<HttpsURLConnection> connection) throws BadRequestException, NotFoundException, BadGatewayException, UnknownCodeException {
        int statusCode;
        boolean isHttps = connection.isPresent();
        if (isHttps)
            statusCode = getHttpsStatusCode(connection.get());
        else
            statusCode = getStatusCode(response);

        switch (statusCode) {
            case 200:
                if (isHttps)
                    writeHTMLBody(path, domain, getHttpsContent(connection.get()), getResponseHeaders(response));
                else
                    writeHTMLBody(path, domain, response, getResponseHeaders(response));
                break;
            case 301:
            case 302:
                String newLocation = isHttps ? connection.get().getHeaderField("Location")
                        : getLocationHeader(response);
                createRequest("", newLocation, HTTP_PORT);
                break;
            case 400:
                throw new BadRequestException(response);
            case 404:
                throw new NotFoundException(response);
            case 500:
                throw new BadGatewayException(response);
            default:
                throw new UnknownCodeException(response);
        }
    }

    private static String getLocationHeader(String response) throws NotFoundException {
        for (String line : response.split("\n")) {
            if (line.toLowerCase().startsWith("location")) {
                String[] lineElements = line.split(":");
                String protocol = lineElements[1].trim();
                String domain = lineElements[2].trim();
                return String.format("%s:%s", protocol, domain);
            }
        }
        throw new NotFoundException("Header location not found");
    }

    @SneakyThrows
    public static String getHttpsContent(HttpsURLConnection connection) {
        BufferedReader bufferedReader =
                new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        bufferedReader.close();
        return sb.toString();
    }

    @SneakyThrows
    private static int getHttpsStatusCode(HttpsURLConnection connection) {
        return connection.getResponseCode();
    }



}

