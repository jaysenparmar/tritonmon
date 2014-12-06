package com.tritonmon.global;

import android.media.MediaPlayer;

import lombok.Getter;
import lombok.Setter;

public class Audio {
    public static MediaPlayer sfx;

    @Getter
    @Setter
    private static boolean audioEnabled = true;

    @Getter
    @Setter
    private static MediaPlayer backgroundMusic;
}
