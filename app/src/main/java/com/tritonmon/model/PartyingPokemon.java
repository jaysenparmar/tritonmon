package com.tritonmon.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PartyingPokemon extends UsersPokemon implements Parcelable {

    // method to recreate a UserPokemon from a Parcel
    public static Creator<PartyingPokemon> CREATOR = new Creator<PartyingPokemon>() {

        @Override
        public PartyingPokemon createFromParcel(Parcel source) {
            return new PartyingPokemon(source);
        }

        @Override
        public PartyingPokemon[] newArray(int size) {
            return new PartyingPokemon[size];
        }
    };

    public PartyingPokemon(UsersPokemon usersPokemon) {
        super(usersPokemon);
    }

    public PartyingPokemon(Parcel parcel) {
        super(parcel);
    }

    @Override
    public String toString() {
        return super.getName();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
