package com.tritonmon.global;

public class Constant {
    public static final String ENCODING = "UTF-8";

    // server url
    public static final String SERVER_BASE_URL = "http://ec2-54-193-111-74.us-west-1.compute.amazonaws.com:8080";
    public static final String SERVER_URL = SERVER_BASE_URL + "/tritonmon-server";

    // server response status codes
    public static final int STATUS_CODE_SUCCESS = 200; // success
    public static final int STATUS_CODE_500 = 500; // cannot find table,column,attribute or insert/update into table
    public static final int STATUS_CODE_204 = 204; // returned 0 rows or trying to insert already existing data
    public static final int STATUS_CODE_404 = 404; // invalid query string
}
