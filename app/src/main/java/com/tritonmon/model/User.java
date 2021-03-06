package com.tritonmon.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class User implements Parcelable {
    @SerializedName("users_id") private int usersId;
    private String username;
    @SerializedName("is_facebook") private boolean facebookUser;
    private String password;
    private String gender;
    private String hometown;
    private String avatar;
    @SerializedName("num_pokeballs") private int numPokeballs;
    @SerializedName("available_for_trading") private boolean availableForTrading;
    private int wins;
    private int losses;

    @SerializedName("facebook_name") private String facebookName;

    // method to recreate a User from a Parcel
    public static Creator<User> CREATOR = new Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(Parcel parcel) {
        username = parcel.readString();
        password = parcel.readString();
        gender = parcel.readString();
        hometown = parcel.readString();
        avatar = parcel.readString();
        numPokeballs = parcel.readInt();
        availableForTrading = parcel.readByte()!=0;
        wins = parcel.readInt();
        losses = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(gender);
        dest.writeString(hometown);
        dest.writeString(avatar);
        dest.writeInt(numPokeballs);
        byte availableForTradingByte = availableForTrading ? (byte) 1 : 0;
        dest.writeByte(availableForTradingByte);
        dest.writeInt(wins);
        dest.writeInt(losses);
    }

    public String getName() {
        if (this.isFacebookUser()) {
            return this.getFacebookName();
        }
        else {
            return this.getUsername();
        }
    }
}