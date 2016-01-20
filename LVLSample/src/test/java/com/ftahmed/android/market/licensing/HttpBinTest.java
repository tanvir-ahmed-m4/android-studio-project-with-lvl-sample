package com.ftahmed.android.market.licensing;

import org.junit.Test;

import com.google.android.vending.licensing.util.HttpBin;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

class HttpBinTest {
    @Test
    public void getHttpBin() throws JSONException {
        HttpBin.get("", null, null, new JsonHttpResponseHandler() {
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
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first item
                try {
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

    @Test
    private void postHttpBin(String msg) {
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
                // Pull out the first item
                try {
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