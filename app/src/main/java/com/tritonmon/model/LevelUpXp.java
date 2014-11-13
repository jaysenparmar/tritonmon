package com.tritonmon.model;

import com.google.gson.annotations.SerializedName;

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
public class LevelUpXp {
    private final int level;
    @SerializedName("levels_base_xp") private final int levelsBaseXp;
    @SerializedName("xp_to_next_level") private final int xpToNextlevel;
}
