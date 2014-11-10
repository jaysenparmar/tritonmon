package com.tritonmon.global;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class MyHttpClient {
    private static final HttpClient instance = new DefaultHttpClient();

    private MyHttpClient() {

    }

    private static HttpClient getInstance() {
        return instance;
    }

    public static HttpResponse get(String url) {
        try {
            HttpGet httpget = new HttpGet(url);
            Log.d("MyHttpClient", "GET request " + url);
            HttpResponse response = instance.execute(httpget);
            Log.d("MyHttpClient", "GET response " + response.getStatusLine().toString());
            return response;
        }
        catch (IOException e) {
            Log.e("MyHttpClient", "get(url) threw IOException: http protocol error or the connection was aborted");
            e.printStackTrace();
        }
        return null;
    }

    public static HttpResponse post(String url) {
        try {
            HttpPost httppost = new HttpPost(url);
            Log.d("MyHttpClient", "POST request " + url);
            HttpResponse response = instance.execute(httppost);
            Log.d("MyHttpClient", "POST response " + response.getStatusLine().toString());
            return response;
        }
        catch (IOException e) {
            Log.e("MyHttpClient", "post(url) threw IOException: http protocol error or the connection was aborted");
            e.printStackTrace();
        }
        return null;
    }

    public static int getStatusCode(HttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    public static String getJson(HttpResponse response) {
        try {
            return IOUtils.toString(response.getEntity().getContent(), Constant.ENCODING);
        }
        catch (IOException e) {
            Log.e("MyHttpClient", "getJson() threw IOException: the stream could not be created");
            e.printStackTrace();
        }
        return null;
    }
}
