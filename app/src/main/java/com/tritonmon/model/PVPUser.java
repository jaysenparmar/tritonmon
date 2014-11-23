package com.tritonmon.model;

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
public class PVPUser extends User {

    private int highestLevelPokemon;
    private int averageLevelOfTopSixPokemon;

    public PVPUser(User user, int highestLevelPokemon, int averageLevelOfTopSixPokemon) {
        username = user.getUsername();
        password = user.getPassword();
        gender = user.getGender();
        hometown = user.getHometown();
        avatar = user.getAvatar();
        this.highestLevelPokemon = highestLevelPokemon;
        this.averageLevelOfTopSixPokemon = averageLevelOfTopSixPokemon;
    }

}
