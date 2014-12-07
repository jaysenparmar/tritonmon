package com.tritonmon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.tritonmon.asynctask.user.UpdateCurrentUserTask;
import com.tritonmon.global.Audio;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.FacebookInfo;

public abstract class ActionBarActivity extends FragmentActivity {

    protected abstract int getLayoutResourceId();
    protected abstract int getMenuResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(getLayoutResourceId());

        Audio.setBackgroundMusic(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(getMenuResourceId(), menu);

        MenuItem enableSound = menu.findItem(R.id.enable_sound);
        enableSound.setChecked(Audio.isAudioEnabled());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.enable_sound) {
            if (item.isChecked()) {
                item.setChecked(false);
                if (Audio.getBackgroundMusic() != null) {
                    Audio.getBackgroundMusic().pause();
                }
            }
            else {
                item.setChecked(true);
                if (Audio.getBackgroundMusic() != null) {
                    Audio.getBackgroundMusic().start();
                }
            }

            Audio.setAudioEnabled(item.isChecked());

            return true;
        }
        else if(id == R.id.refresh) {
            new UpdateCurrentUserTask(this).execute();
            return true;
        }
        else if(id == R.id.logout) {
            this.setProgressBarIndeterminateVisibility(true);

            if (CurrentUser.isFacebookUser()) {
                FacebookInfo.session.closeAndClearTokenInformation();
                FacebookInfo.session = null;
            }

            CurrentUser.logout();
            FacebookInfo.clear();

            Intent i = new Intent(getApplicationContext(), Tritonmon.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
