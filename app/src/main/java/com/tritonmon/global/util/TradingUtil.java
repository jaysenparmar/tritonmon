package com.tritonmon.global.util;

import com.tritonmon.global.Constant;
import com.tritonmon.model.UsersPokemon;

public class TradingUtil {

    public static String getDetailedPokemonInfo(UsersPokemon usersPokemon) {
        String tmp = "";
        tmp+=usersPokemon.getName() + "\n";
        tmp+="Level " + usersPokemon.getLevel() + "\n\n";
        for (Integer ele : usersPokemon.getMoves()) {
            if (ele != null) {
                tmp+=Constant.movesData.get(ele).getName() + "\n";
            }
        }
        return tmp;
    }
}
