package com.tritonmon.global;

import android.content.Context;
import android.widget.ProgressBar;

import com.tritonmon.activity.R;

public class ProgressBarUtil {

    public static void updateHealthBar(Context context, ProgressBar healthBar, int current, int total) {
        float healthPerc = getPercentage(current, total);
        if (healthPerc > Constant.GREEN_HEALTH_THRESHOLD) {
            healthBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_bar_green));
        } else if (healthPerc > Constant.YELLOW_HEALTH_THRESHOLD) {
            healthBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_bar_yellow));
        } else {
            healthBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_bar_red));
        }
        healthBar.setProgress((int) healthPerc);
    }

    public static int getPercentage(int current, int total) {
        return (int)(100f * (float)current / (float)total);
    }
}
