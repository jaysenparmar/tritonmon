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
public class MoveMetaAilments {
    @SerializedName("move_meta_ailment_id") private final int moveMetaAilmentId;
    private final String name;
}
