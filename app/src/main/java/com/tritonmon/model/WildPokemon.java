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
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class WildPokemon {
    private int pokemon_id;

    private int level;
    private int xp;
    private int status;
    private int health;
    private List<Integer> moves;
}
