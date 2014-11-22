package com.tritonmon.global;

import android.content.Context;

import com.tritonmon.staticmodel.Types;

public class ImageUtil {

    public static int getImageResource(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    public static int getAttackButtonImageResource(Context context, int typeId) {
        Types type = Constant.typesData.get(typeId);
        String imageName = Constant.BATTLE_ATK_BTN_NAME + type.getName();
        return getImageResource(context, imageName);
    }

    public static int getPokemonFrontImageResource(Context context, int pokemonId) {
        return getImageResource(context, "p" + padPokemonId(pokemonId));
    }

    public static int getPokemonBackImageResource(Context context, int pokemonId) {
        return getImageResource(context, "p" + padPokemonId(pokemonId) + "b");
    }

    private static String padPokemonId(int pokemonId) {
        String id = Integer.toString(pokemonId);
        if (id.length() == 1) {
            return "00" + id;
        }
        else if (id.length() == 2) {
            return "0" + id;
        }
        else {
            return id;
        }
    }

}
