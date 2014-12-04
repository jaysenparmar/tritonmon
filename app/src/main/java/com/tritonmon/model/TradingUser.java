package com.tritonmon.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TradingUser implements Parcelable {

    private String username;
    private String hometown;
    private String avatar;
    private List<UsersPokemon> usersPokemon;

    public static Parcelable.Creator<TradingUser> CREATOR = new Parcelable.Creator<TradingUser>() {

        @Override
        public TradingUser createFromParcel(Parcel source) {
            return new TradingUser(source);
        }

        @Override
        public TradingUser[] newArray(int size) {
            return new TradingUser[size];
        }
    };

    public TradingUser(Parcel parcel) {
        username = parcel.readString();
        hometown = parcel.readString();
        avatar = parcel.readString();
        usersPokemon = new ArrayList<UsersPokemon>();
        parcel.readList(usersPokemon, null);
    }

    public TradingUser(String username, String hometown, String avatar, List<UsersPokemon> usersPokemon) {
        this.username = username;
        this.hometown = hometown;
        this.avatar = avatar;
        this.usersPokemon = usersPokemon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(hometown);
        dest.writeString(avatar);
        dest.writeList(usersPokemon);

    }

}
