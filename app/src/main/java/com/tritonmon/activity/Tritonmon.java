package com.tritonmon.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tritonmon.fragment.FacebookLoginFragment;
import com.tritonmon.global.Audio;
import com.tritonmon.global.Constant;
import com.tritonmon.global.FacebookInfo;
import com.tritonmon.global.StaticData;
import com.tritonmon.global.singleton.MyApplication;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.global.util.ImageUtil;
import com.tritonmon.toast.TritonmonToast;

import org.apache.http.HttpResponse;

import java.text.ParseException;


public class Tritonmon extends ActionBarActivity {

    private static int MAX_SERVER_RETRIES = 5;

    private ImageView loginButton;
    private ImageView registerButton;

    private ScrollView debugScrollView;
    private TextView debugTextView;

    private MediaPlayer mp;

    private int serverRetries;

    private boolean backButtonPressed;
    private Handler backButtonHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Constant.DISABLE_ACTION_BAR) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        initFacebookLogin(savedInstanceState);

        if(mp != null) {
            mp.release();
        }
        mp = MediaPlayer.create(getApplicationContext(), R.raw.tritonmon_title);
        Audio.setBackgroundMusic(mp);
        if (Audio.isAudioEnabled()) {
            mp.start();
        }

        serverRetries = 0;

        loginButton = (ImageView) findViewById(R.id.loginButton);
        registerButton = (ImageView) findViewById(R.id.registerButton);

        debugScrollView = (ScrollView) findViewById(R.id.debugScrollView);
        debugTextView = (TextView) findViewById(R.id.debugTextView);
        if (Constant.DEBUG) {
            debugScrollView.setVisibility(View.VISIBLE);
        }
        else {
            debugScrollView.setVisibility(View.GONE);
        }

        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }

                loginButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "login_btn_en"));
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                }

                registerButton.setImageResource(ImageUtil.getImageResource(getApplicationContext(), "register_btn_en"));
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
        });

        // make sure static data is loaded
        try {
            if (Constant.pokemonData == null) {
                StaticData.load(getAssets());
            }
        }
        catch (ParseException e) {
            TritonmonToast.makeText(getApplicationContext(), "ERROR: Failed to load static data", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        new TestDatabase().execute();

        backButtonPressed = false;
        backButtonHandler = new Handler();

        Tracker t = ((MyApplication) getApplication()).getTracker(
                MyApplication.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_tritonmon;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.logged_out_menu;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        if (Audio.isAudioEnabled()) {
            mp.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mp.pause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mp.release();
    }

    @Override
    public void onBackPressed() {
        if (!backButtonPressed) {
            backButtonPressed = true;
            TritonmonToast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            backButtonHandler.postDelayed(backButtonRunnable, 2000);
        }
        else {
            mp.pause();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private Runnable backButtonRunnable = new Runnable() {
        public void run() {
            backButtonPressed = false;
        }
    };

    private Spanned successMsg(String htmlText) {
        return Html.fromHtml("<font color=#00ff00>SUCCESS</font> " + htmlText);
    }

    private Spanned errorMsg(String htmlText) {
        return Html.fromHtml("<font color=#ff0000>ERROR</font> " + htmlText);
    }

    private void initFacebookLogin(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            FacebookInfo.fragment = new FacebookLoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, FacebookInfo.fragment)
                    .commit();
        }
        else {
            // Or set the fragment from restored state info
            FacebookInfo.fragment = (FacebookLoginFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }

    private class TestDatabase extends AsyncTask<String, Void, String> {

        private String status;

        @Override
        protected String doInBackground(String... params) {
            String url = Constant.SERVER_URL + "/table=pokemon";
            HttpResponse response = MyHttpClient.get(url);

            if (response == null) {
                status = "unreachable, check internet";
                return null;
            }

            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                return MyHttpClient.getJson(response);
            }

            status = response.getStatusLine().toString();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null || result.isEmpty()) {
                debugTextView.append(errorMsg("server " + status + "<br />"));
                if (serverRetries < MAX_SERVER_RETRIES) {
                    serverRetries++;
                    debugTextView.append("retrying... (" + serverRetries + ")\n");
                    new TestDatabase().execute();
                }
            }
            else {
                debugTextView.append(successMsg("fetched data from server<br />"));
            }
        }
    }

}
