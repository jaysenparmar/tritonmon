package com.tritonmon.global;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MyGson {

    private static Gson instance = null;

    private MyGson() {

    }

    public static Gson getInstance() {
        if(instance == null) {
            instance = new GsonBuilder().create();
        }
        return instance;
    }
}
