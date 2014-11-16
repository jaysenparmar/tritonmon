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
    private String username;
    private String password;
    private String gender;
    private String hometown;
    @SerializedName("avatar_id") private int avatarId;
    @SerializedName("num_pokeballs") private int numPokeballs;

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
        avatarId = parcel.readInt();
        numPokeballs = parcel.readInt();
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
        dest.writeInt(avatarId);
        dest.writeInt(numPokeballs);
    }
}