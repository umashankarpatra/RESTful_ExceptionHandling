package com.xebia.postit.common.util.rest;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private static final String PREFIX = "X-postit";

    private static final String SUCCESS = PREFIX + "-success";

    private static final String INFO = PREFIX + "-info";

    private static final String WARNING = PREFIX + "-warning";

    private static final String ERROR = PREFIX + "-error";

    private static final String ALERT = PREFIX + "-alert";

    private static final String PARAMS = PREFIX + "-params";

    private HeaderUtil() {
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ALERT, message);
        headers.add(PARAMS, param);
        return headers;
    }

    public static HttpHeaders addInfo(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(INFO, message);
        return headers;
    }

    public static HttpHeaders addSuccess(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SUCCESS, message);
        return headers;
    }
    public static HttpHeaders addWarning(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(WARNING, message);
        return headers;
    }

    public static HttpHeaders addError(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ERROR, message);
        return headers;
    }
}
