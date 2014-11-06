package com.tritonmon.model;

import com.tritonmon.exception.PartyException;

import java.util.List;

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
public class Party {
    private static final int MAX_PARTY_SIZE = 6;

    private List<UserPokemon> party;

    public void add(UserPokemon pokemon) throws PartyException {
        if (fullParty()) {
            throw new PartyException("Cannot add " + pokemon.getName() + " to party - party is already full.");
        }
        party.add(pokemon);
    }

    public void add(int i, UserPokemon pokemon) throws PartyException {
        if (fullParty()) {
            throw new PartyException("Cannot add " + pokemon.getName() + " to party - party is already full.");
        }
        party.add(i, pokemon);
    }

    public UserPokemon remove(int i) throws PartyException {
        if (party.size() == 1) {
            if (i != 0) {
                throw new PartyException("Party index out of bounds.");
            }
            else {
                throw new PartyException("Cannot remove " + party.get(0).getName() + " from party - only pokemon in party.");
            }
        }

        return party.remove(i);
    }

    public boolean remove(UserPokemon pokemon) throws PartyException {
        if (party.size() == 1 && party.contains(pokemon)) {
            throw new PartyException("Cannot remove " + party.get(0).getName() + " from party - only pokemon in party.");
        }

        return party.remove(pokemon);
    }

    public void swap(int i1, int i2) {
        UserPokemon pokemon1 = party.get(i1);
        UserPokemon pokemon2 = party.get(i2);

        party.remove(i1);
        party.add(i1, pokemon2);
        party.remove(i2);
        party.add(i2, pokemon1);
    }

    private boolean fullParty() {
        return party.size() == MAX_PARTY_SIZE;
    }
}
