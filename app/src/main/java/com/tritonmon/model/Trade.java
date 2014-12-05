package com.tritonmon.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Trade implements Parcelable {

    @SerializedName("offerer_users_id") private int offererUsersId;
    @SerializedName("offer_users_pokemon_id") private int offerUsersPokemonId;
    @SerializedName("lister_users_id") private int listerUsersId;
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

    // method to recreate a UserPokemon from a Parcel
    public static Creator<Trade> CREATOR = new Creator<Trade>() {

        @Override
        public Trade createFromParcel(Parcel source) {
            return new Trade(source);
        }

        @Override
        public Trade[] newArray(int size) {
            return new Trade[size];
        }
    };

    public Trade(Parcel parcel) {
        byte tmp;
        offererUsersId = parcel.readInt();
        offerUsersPokemonId = parcel.readInt();
        listerUsersId = parcel.readInt();
        listerUsersPokemonId = parcel.readInt();
        tmp = parcel.readByte();
        seenOffer = tmp == 1 ? true : false;
        tmp = parcel.readByte();
        declined = tmp == 1 ? true : false;
        tmp = parcel.readByte();
        seenDecline = tmp == 1 ? true : false;
        offerPokemonId = parcel.readInt();
        offerLevel = parcel.readInt();
        listerPokemonId = parcel.readInt();
        listerLevel = parcel.readInt();
        tmp = parcel.readByte();
        accepted = tmp == 1 ? true : false;
        tmp = parcel.readByte();
        seenAcceptance = tmp == 1 ? true : false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        byte tmp;
        dest.writeInt(offererUsersId);
        dest.writeInt(offerUsersPokemonId);
        dest.writeInt(listerUsersId);
        dest.writeInt(listerUsersPokemonId);
        tmp = seenOffer ? (byte)1 : 0;
        dest.writeByte(tmp);
        tmp = declined ? (byte)1 : 0;
        dest.writeByte(tmp);
        tmp = seenDecline ? (byte)1 : 0;
        dest.writeByte(tmp);
        dest.writeInt(offerPokemonId);
        dest.writeInt(offerLevel);
        dest.writeInt(listerPokemonId);
        dest.writeInt(listerLevel);
        tmp = accepted ? (byte)1 : 0;
        dest.writeByte(tmp);
        tmp = seenAcceptance ? (byte)1 : 0;
        dest.writeByte(tmp);

    }

}
