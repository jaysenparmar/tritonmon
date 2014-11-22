package com.tritonmon.global;

import java.util.Random;

public class MyRandom {
    private static Random instance = null;

    private MyRandom() {

    }

    public static Random getInstance() {
        if(instance == null) {
            instance = new Random(System.currentTimeMillis());
        }
        return instance;
    }
}
