package com.tritonmon.global;

import android.content.Context;

public class ImageUtil {

    public static int getTrainerImageResource(Context context, String avatar) {
        return context.getResources().getIdentifier("drawable/" + avatar, null, context.getPackageName());
    }

    public static int getPokemonFrontImageResource(Context context, int pokemonId) {
        return context.getResources().getIdentifier(getPokemonFrontImageUri(pokemonId), null, context.getPackageName());
    }

    public static int getPokemonBackImageResource(Context context, int pokemonId) {
        return context.getResources().getIdentifier(getPokemonBackImageUri(pokemonId), null, context.getPackageName());
    }

    private static String getPokemonFrontImageUri(int pokemonId) {
        return "drawable/p" + padPokemonId(pokemonId);
    }

    private static String getPokemonBackImageUri(int pokemonId) {
        return "drawable/p" + padPokemonId(pokemonId) + "b";
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
