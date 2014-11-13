package com.tritonmon.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

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
public class MoveMetaStatChanges {

    @SerializedName("move_id") private final int moveId;
    @SerializedName("stat_id_to_stat_difference") private final Map<Integer, Integer> statIdToStatDifference;
}
