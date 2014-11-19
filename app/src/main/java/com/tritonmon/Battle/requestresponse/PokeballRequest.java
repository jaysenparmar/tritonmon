package com.tritonmon.battle.requestresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor(suppressConstructorProperties = true)
public class PokeballRequest {
    int pokemon2Id;
    int pokemon2Level;
    int pokemon2Hp;
    String pokemon2Status;
}
