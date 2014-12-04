package com.tritonmon.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Trade {

    private String offerer;
    @SerializedName("offer_users_pokemon_id") private int offerUsersPokemonId;
    private String lister;
    @SerializedName("lister_users_pokemon_id") private int listerUsersPokemonId;
    @SerializedName("seen_offer") private boolean seenOffer;
    private boolean declined;
    @SerializedName("seen_decline") private boolean seenDecline;
    @SerializedName("offer_pokemon_id") private int offerPokemonId;
    @SerializedName("offer_level") private int offerLevel;
    @SerializedName("lister_pokemon_id") private int listerPokemonId;
    @SerializedName("lister_level") private int listerLevel;
    private boolean accepted;
    @SerializedName("seen_acceptance") private boolean seenAcceptance;

//    public Trade(String offerer, int offerUsersPokemonId, String lister, int listerUsersPokemonId,
//                 boolean seenOffer, boolean declined, boolean seenDecline,
//                 int offerPokemonId, int offerLevel, int listerPokemonId, int listerLevel) {
//        this.offerer = offerer;
//        this.offerUsersPokemonId = offerUsersPokemonId;
//        this.lister = lister;
//        this.listerUsersPokemonId = listerUsersPokemonId;
//        this.seenOffer = seenOffer;
//        this.declined = declined;
//        this.seenDecline = seenDecline;
//        this.offerPokemonId = offerPokemonId;
//        this.offerLevel = offerLevel;
//        this.listerPokemonId = listerPokemonId;
//        this.listerLevel = listerLevel;
//    }
}
