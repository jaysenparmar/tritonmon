package com.tritonmon.model;

import com.tritonmon.exception.PartyException;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor(suppressConstructorProperties = true)
public class PokemonParty {
    public static final int MAX_PARTY_SIZE = 6;

    private List<UsersPokemon> pokemonList;

    public PokemonParty() {
        pokemonList = new ArrayList<UsersPokemon>();
    }

    public boolean isFull() {
        return pokemonList.size() == MAX_PARTY_SIZE;
    }

    public int size() {
        return pokemonList.size();
    }

    public UsersPokemon getPokemon(int i) {
        if (i < pokemonList.size()) {
            return pokemonList.get(i);
        }
        return null;
    }

    public UsersPokemon getPokemon(UsersPokemon pokemon) {
        for (UsersPokemon p : pokemonList) {
            if (p.equals(pokemon)) {
                return p;
            }
        }
        return null;
    }

    public void add(UsersPokemon pokemon) throws PartyException {
        if (isFull()) {
            throw new PartyException("Cannot add " + pokemon.getName() + " to party - party is already full.");
        }
        pokemonList.add(pokemon);
    }

    public void add(int i, UsersPokemon pokemon) throws PartyException {
        if (isFull()) {
            throw new PartyException("Cannot add " + pokemon.getName() + " to party - party is already full.");
        }
        pokemonList.add(i, pokemon);
    }

    public UsersPokemon remove(int i) throws PartyException {
        if (pokemonList.size() == 1) {
            if (i != 0) {
                throw new PartyException("Party index out of bounds.");
            }
            else {
                throw new PartyException("Cannot remove " + pokemonList.get(0).getName() + " from party - only pokemon in party.");
            }
        }

        return pokemonList.remove(i);
    }

    public boolean remove(UsersPokemon pokemon) throws PartyException {
        if (pokemonList.size() == 1 && pokemonList.contains(pokemon)) {
            throw new PartyException("Cannot remove " + pokemonList.get(0).getName() + " from party - only pokemon in party.");
        }

        return pokemonList.remove(pokemon);
    }

    /**
     * Swaps the positions of 2 UsersPokemon in the party
     *
     * @param i1 index of first UsersPokemon
     * @param i2 index of second UsersPokemon
     */
    public void swapSlots(int i1, int i2) {
        if (i1 == i2) {
            return;
        }
        int lowerIndex = (i1 < i2) ? i1 : i2;
        int higherIndex = (i1 > i2) ? i1 : i2;

        UsersPokemon lowerPokemon = pokemonList.get(lowerIndex);
        UsersPokemon higherPokemon = pokemonList.get(higherIndex);

        pokemonList.remove(lowerIndex);
        pokemonList.add(lowerIndex, higherPokemon);
        pokemonList.remove(higherIndex);
        pokemonList.add(higherIndex, lowerPokemon);
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
        if (pokemonList.get(i) != null) {
            swappedOut = pokemonList.get(i);
        }
        this.add(i, pokemon);
        return swappedOut;
    }
}
