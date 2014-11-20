package com.tritonmon.battle.requestresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.tritonmon.model.BattlingPokemon;

import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor(suppressConstructorProperties = true)
public class CatchResponse implements Parcelable {

    private BattlingPokemon pokemon1;
    private BattlingPokemon pokemon2;

    private int numPokeballs;

    public static Parcelable.Creator<CatchResponse> CREATOR = new Parcelable.Creator<CatchResponse>() {

        @Override
        public CatchResponse createFromParcel(Parcel source) {
            return new CatchResponse(source);
        }

        @Override
        public CatchResponse[] newArray(int size) {
            return new CatchResponse[size];
        }
    };

    public CatchResponse(Parcel parcel) {

        pokemon1 = new BattlingPokemon(parcel);
        pokemon2 = new BattlingPokemon(parcel);

        numPokeballs = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // i assume this works
        pokemon1.writeToParcel(dest, flags);
        pokemon2.writeToParcel(dest, flags);

        dest.writeInt(numPokeballs);
    }
}
