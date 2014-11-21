package com.tritonmon.battle.requestresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.tritonmon.model.BattlingPokemon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor(suppressConstructorProperties = true)
public class CatchResponse implements Parcelable {

    private BattlingPokemon oldPokemon;
    private BattlingPokemon newPokemon;

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
        oldPokemon = new BattlingPokemon(parcel);
        newPokemon = new BattlingPokemon(parcel);

        numPokeballs = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        oldPokemon.writeToParcel(dest, flags);
        newPokemon.writeToParcel(dest, flags);

        dest.writeInt(numPokeballs);
    }
}
