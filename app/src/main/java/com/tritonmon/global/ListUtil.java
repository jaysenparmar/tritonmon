package com.tritonmon.global;

import java.util.List;

public class ListUtil {

    public static String toCommaSeparatedString(List<Integer> list) {
        while (list.size() < 4) {
            list.add(null);
        }
        String out = "";
        for (Integer i : list) {
            if (!out.isEmpty()) {
                out += ",";
            }
            if (i == null) {
                out += "null";
            } else {
                out += i.toString();
            }
        }
        return out;
    }
}
