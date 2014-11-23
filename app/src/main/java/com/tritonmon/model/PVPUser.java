package com.tritonmon.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class PVPUser extends User {

    private int maxLevelPokemon;
    private int averageLevelOfTopSixPokemon;

    public PVPUser(User user, int maxLevelPokemon, int averageLevelOfTopSixPokemon) {
        super.setUsername(user.getUsername());
        super.setPassword(user.getPassword());
        super.setGender(user.getGender());
        super.setHometown(user.getHometown());
        super.setAvatar(user.getAvatar());
        super.setWins(user.getWins());
        super.setLosses(user.getLosses());
        this.maxLevelPokemon = maxLevelPokemon;
        this.averageLevelOfTopSixPokemon = averageLevelOfTopSixPokemon;
    }

}
