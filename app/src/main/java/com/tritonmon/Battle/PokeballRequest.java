package com.tritonmon.Battle;

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
public class PokeballRequest {
    int pokemon2Id;
    int pokemon2Level;
    int pokemon2Hp;
    String pokemon2Status;
}
