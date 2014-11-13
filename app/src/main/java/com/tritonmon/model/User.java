package com.tritonmon.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.tritonmon.global.Constant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
public class User {
    private String username;
    private String password;
    private String gender;
    private String hometown;
    @SerializedName("num_pokeballs") private int numPokeballs;

    // not generated by
    private String encodedUsername;

    public String getEncodedUsername() {
        if (username == null || username.isEmpty()) {
            return null;
        }

        if (encodedUsername == null || encodedUsername.isEmpty()) {
            try {
                encodedUsername = URLEncoder.encode(username, Constant.ENCODING);
            }
            catch (UnsupportedEncodingException e) {
                Log.e("User", "URLEncoder threw UnsupportedEncodingException");
                e.printStackTrace();
            }
        }

        return encodedUsername;
    }
}