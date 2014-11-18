package com.tritonmon.Battle;

import android.util.Log;

import com.tritonmon.global.Constant;

public class PokeballHandler {

    // a = (3*hpmax - 2*hpcurr) * pokemon's_capture_rate * ball_bonus * bonus_state / (3*hp_max)
    // b = (2^16 - 1) * (a/(2^8 - 1))^0.25
    // if a>255: caught
    // else: prob(caught) = ((b+1)/2^16)^4
    public static PokeballResponse didCatchPokemon(PokeballRequest pokeballRequest) {
        int maxHp = BattleUtil.getMaxStat("hp", pokeballRequest.getPokemon2Id(), pokeballRequest.getPokemon2Level());
        int pokemon2_capture_rate = Constant.pokemonData.get(pokeballRequest.getPokemon2Id()).getCaptureRate();
        Log.e("PokeballHandler", "status: " + pokeballRequest.getPokemon2Status());
        Log.e("PokeballHandler", "movemeta: " + Constant.moveMetaAilmentsData.get(pokeballRequest.getPokemon2Status().toString()));
        float status_capture_rate =  Constant.moveMetaAilmentsData.get(pokeballRequest.getPokemon2Status()).getCatchRate();
        float a = ((3*maxHp) - (2*pokeballRequest.getPokemon2Hp())) * pokemon2_capture_rate * 1 * status_capture_rate / (3*maxHp);

        if (a >= 255) {
            return new PokeballResponse(true);
        } else {
            // might need doubles
            float b = (float)((Math.pow(2.0,  16.0)-1) * Math.pow((a/(Math.pow(2.0, 8.0)-1)),0.25));
            float p = (float)(Math.pow(((b+1.0)/Math.pow(2.0, 16.0)),4.0));
            return new PokeballResponse(BattleUtil.didRandomEvent(p));
        }
    }
}
