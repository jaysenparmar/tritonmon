package com.tritonmon.global;

import java.util.List;

public class ListUtil {

    public static String convertMovesToString(List<Integer> moves) {
        while (moves.size() < 4) {
            moves.add(null);
        }
        String movesString = "";
        for (Integer move : moves) {
            if (!movesString.isEmpty()) {
                movesString += ",";
            }
            if (move == null) {
                movesString += "null";
            } else {
                movesString += move.toString();
            }
        }
        return movesString;
    }

    public static String convertPpsToString(List<Integer> pps) {
        while (pps.size() < 4) {
            pps.add(null);
        }
        String ppsString = "";
        for (Integer pp : pps) {
            if (!ppsString.isEmpty()) {
                ppsString += ",";
            }
            if (pp == null) {
                ppsString += "null";
            } else {
                ppsString += pp.toString();
            }
        }
        return ppsString;
    }
}
