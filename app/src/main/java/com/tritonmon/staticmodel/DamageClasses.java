package com.tritonmon.staticmodel;

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
public class DamageClasses {

    // damage classes are physical, special etc
    @SerializedName("damage_class_id") private final int damageClassId;
    private final String name;

    public static final String STATUS = "status";
    public static final String PHYSICAL = "physical";
    public static final String SPECIAL = "special";
}
