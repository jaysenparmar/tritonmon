package com.tritonmon.global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Constant {

    // server url
    public static final String SERVER_BASE_URL = "http://ec2-54-193-111-74.us-west-1.compute.amazonaws.com:8080";
    public static final String SERVER_URL = SERVER_BASE_URL + "/tritonmon-server";

    // server response status codes
    public static final int STATUS_CODE_SUCCESS = 200;
    public static final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;

    // from http://stackoverflow.com/questions/4457492/how-do-i-use-the-simple-http-client-in-android
    public static String convertStreamToString(InputStream is) {
            /*
             * To convert the InputStream to String we use the BufferedReader.readLine()
             * method. We iterate until the BufferedReader return null which means
             * there's no more data to read. Each line will appended to a StringBuilder
             * and returned as String.
             */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
