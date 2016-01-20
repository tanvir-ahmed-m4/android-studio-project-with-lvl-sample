package com.google.android.vending.licensing.util;

import android.util.Log;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * http://loopj.com/android-async-http/
 * https://github.com/loopj/android-async-http/issues/695
 * https://httpbin.org/
 * https://mockbin.org
 *
 * @author Tanvir
 */
public class HttpBin {
    private static final String TAG  = "HttpBin";

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

    public static void post(String msg) {
        HashMap<String, Object> headers = new HashMap<String, Object>();
        headers.put("accept", "application/json");

        RequestParams params = new RequestParams();
        params.add("msg", msg);

        HttpBin.post("", headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.w(TAG, "--- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // TODO Pull out the first item
                try {
                    // Pull out the first event on the public timeline
                    JSONObject firstItem = (JSONObject) response.get(0);
                    String firstText = firstItem.getString("text");
                    // Do something with the response
                    System.out.println(firstText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
