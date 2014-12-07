package com.tritonmon.staticmodel;

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
public class Geolocation {

    //ex fighting/flying
    private final String name;
    @SerializedName("x_min") private final double xMin;
    @SerializedName("x_max") private final double xMax;
    @SerializedName("y_min") private final double yMin;
    @SerializedName("y_max") private final double yMax;
    @SerializedName("type_id") private final int typeId;
}
