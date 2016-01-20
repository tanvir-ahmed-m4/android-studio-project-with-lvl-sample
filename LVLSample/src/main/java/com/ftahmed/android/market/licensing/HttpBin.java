package com.ftahmed.android.market.licensing;

import com.loopj.android.http.*;

import java.util.Map;

/**
 * http://loopj.com/android-async-http/
 * https://github.com/loopj/android-async-http/issues/695
 * https://httpbin.org/
 * https://mockbin.org
 *
 * @author Tanvir
 */
public class HttpBin {
//    private static final String BASE_URL = "https://httpbin.org/post";
//    private static final String BASE_URL = "https://mockbin.org/bin/d598bff6-13ae-419c-816c-ddba9b3e6d44/log";
    private static final String BASE_URL = "http://mockbin.org/bin/d598bff6-13ae-419c-816c-ddba9b3e6d44/log";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, Map<String, Object> headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("X-HttpBin-Debug-Header", "GET Method");
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                if (entry.getValue().getClass().isArray()) {
                    Object[] values = (Object[]) entry.getValue();
                    for (Object value : values) {
                        client.addHeader(entry.getKey(), value.toString());
                    }
                }
                client.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, Map<String, Object> headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("X-HttpBin-Debug-Header", "POST Method");
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                if (entry.getValue().getClass().isArray()) {
                    Object[] values = (Object[]) entry.getValue();
                    for (Object value : values) {
                        client.addHeader(entry.getKey(), value.toString());
                    }
                }
                client.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
