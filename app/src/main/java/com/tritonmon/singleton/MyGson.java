package com.tritonmon.singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Anurag on 11/5/2014.
 */
public class MyGson {

    private static Gson instance = null;

    protected MyGson() {
        // blank ctor for lazy instantiation (only created once needed to be used)
    }

    public static Gson getInstance() {
        if(instance == null) {
            instance = new GsonBuilder().create();
        }
        return instance;
    }
}
