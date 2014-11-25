package com.tritonmon.model;

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
public class PVPUser {

    private String username;
    private String hometown;
    private String avatar;
    private List<UsersPokemon> usersPokemon;

    public PVPUser(String username, String hometown, String avatar, List<UsersPokemon> usersPokemon) {
        this.username = username;
        this.hometown = hometown;
        this.avatar = avatar;
        this.usersPokemon = usersPokemon;
    }

}
