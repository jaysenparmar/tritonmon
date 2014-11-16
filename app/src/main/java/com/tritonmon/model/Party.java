package com.tritonmon.model;

import com.tritonmon.exception.PartyException;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Party {
    private static final int MAX_PARTY_SIZE = 6;

    private List<UsersPokemon> party;

    public Party() {
        party = new ArrayList<UsersPokemon>();
    }

    public boolean isFull() {
        return party.size() == MAX_PARTY_SIZE;
    }

    public int size() {
        return party.size();
    }

    public UsersPokemon getPokemon(int i) {
        return party.get(i);
    }

    public void add(UsersPokemon pokemon) throws PartyException {
        if (isFull()) {
            throw new PartyException("Cannot add " + pokemon.getName() + " to party - party is already full.");
        }
        party.add(pokemon);
    }

    public void add(int i, UsersPokemon pokemon) throws PartyException {
        if (isFull()) {
            throw new PartyException("Cannot add " + pokemon.getName() + " to party - party is already full.");
        }
        party.add(i, pokemon);
    }

    public UsersPokemon remove(int i) throws PartyException {
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

    public boolean remove(UsersPokemon pokemon) throws PartyException {
        if (party.size() == 1 && party.contains(pokemon)) {
            throw new PartyException("Cannot remove " + party.get(0).getName() + " from party - only pokemon in party.");
        }

        return party.remove(pokemon);
    }

    /**
     * Swaps the positions of 2 UsersPokemon in the party
     *
     * @param i1 index of first UsersPokemon
     * @param i2 index of second UsersPokemon
     */
    public void swapSlots(int i1, int i2) {
        UsersPokemon pokemon1 = party.get(i1);
        UsersPokemon pokemon2 = party.get(i2);

        party.remove(i1);
        party.add(i1, pokemon2);
        party.remove(i2);
        party.add(i2, pokemon1);
    }

    /**
     * Swaps in a new UsersPokemon into the party and returns the UsersPokemon that was kicked out
     *
     * @param pokemon UsersPokemon to swap into the party
     * @param i the party slot number
     * @return UsersPokemon that was swapped out of the party, or null if previously empty
     * @throws PartyException
     */
    public UsersPokemon swapIn(UsersPokemon pokemon, int i) throws PartyException {
        UsersPokemon swappedOut = null;
        if (party.get(i) != null) {
            swappedOut = party.get(i);
        }
        this.add(i, pokemon);
        return swappedOut;
    }
}
