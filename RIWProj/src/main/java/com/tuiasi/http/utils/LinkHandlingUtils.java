package com.tuiasi.http.utils;

public class LinkHandlingUtils {
    public static String processLinkToStandardForm(String link) {
        String filePath =
                (link.toLowerCase().trim().startsWith("http") ?
                        link.split("//")[1]
                        : link);
        filePath = filePath.toLowerCase().startsWith("www") ? filePath : "www." + filePath;
        return filePath;
    }

    public static String processLinkToLocalPath(String link) {

        String filePath = processLinkToStandardForm(link);

        filePath = filePath.replaceAll(":", "/").replaceAll("\\?", "/").replaceAll("\\*", "/").replaceAll("\\|", "/").replaceAll("#", "/");

        if (!(filePath.endsWith(".html") || filePath.endsWith("htm")) && !(link.endsWith("/robots.txt") || link.endsWith("robots.txt"))) {
            if (!filePath.endsWith("/")) {
                filePath += "/";
            }
            filePath += "index.html";
        }

        return filePath;
    }

    public static String processLinkToWorkingDirPath(String link) {
        return HTTPUtils.WORKING_PATH + processLinkToLocalPath(link);
    }
}
