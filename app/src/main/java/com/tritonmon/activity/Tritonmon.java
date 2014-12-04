package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.global.Constant;
import com.tritonmon.global.MyHttpClient;
import com.tritonmon.global.StaticData;

import org.apache.http.HttpResponse;

import java.text.ParseException;


public class Tritonmon extends Activity {

    private static int MAX_SERVER_RETRIES = 5;

    private Button fbLogin;
    private Button loginButton;
    private Button registerButton;

    private TextView jsonText;

    private int serverRetries;
    private boolean loadedStaticData;

    private void init() {
        fbLogin = (Button) findViewById(R.id.fb_login_button);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        jsonText = (TextView) findViewById(R.id.json_text_view);
        serverRetries = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tritonmon);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

            if (getIntent().getExtras() != null) {
                loadedStaticData = getIntent().getExtras().getBoolean("loadedStaticData");
            }
        }

        init();

        fbLogin.setOnClickListener(new OnClickListener(){
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });

        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
        });

        if (loadedStaticData) {
            jsonText.append(successMsg("loaded static data<br />"));
        }
        else {
            jsonText.append(errorMsg("failed to load static data<br />"));
            jsonText.append("retrying...\n");

            try {
                StaticData.load(getAssets());
                loadedStaticData = true;
                jsonText.append(successMsg("loaded static data<br />"));
            } catch (ParseException e) {
                loadedStaticData = false;
                jsonText.append(errorMsg("failed to load static data<br />"));
                e.printStackTrace();
            }
        }

        new TestDatabase().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tritonmon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tritonmon, container, false);
            return rootView;
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
                jsonText.append(errorMsg("server " + status + "<br />"));
                if (serverRetries < MAX_SERVER_RETRIES) {
                    serverRetries++;
                    jsonText.append("retrying... (" + serverRetries + ")\n");
                    new TestDatabase().execute();
                }
            }
            else {
                jsonText.append(successMsg("fetched data from server<br />"));
            }
        }

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Closed Tritonmon", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private Spanned successMsg(String htmlText) {
        return Html.fromHtml("<font color=#00ff00>SUCCESS</font> " + htmlText);
    }

    private Spanned errorMsg(String htmlText) {
        return Html.fromHtml("<font color=#ff0000>ERROR</font> " + htmlText);
    }
}
