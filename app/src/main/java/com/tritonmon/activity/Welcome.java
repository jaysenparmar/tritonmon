package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;


public class Welcome extends Activity {

    public final int STATUS_CODE_INTERNAL_SERVER_ERROR = 500;

    private TextView welcomeTitle;
    private Button begin;
    private Button chooseBulbasaur;
    private Button chooseCharmander;
    private Button chooseSquirtle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        welcomeTitle = (TextView) findViewById(R.id.welcomeTitle);

        begin = (Button) findViewById(R.id.begin_journey_button);
        begin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(i);
            }
        });

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        welcomeTitle.setText(welcomeTitle.getText() + " " + username);

        chooseBulbasaur = (Button) findViewById(R.id.choose_bulbasaur_button);
        chooseBulbasaur.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                new ChoosePokemon().execute(username, getString(R.string.choose_bulbasaur));
            }
        });

        chooseCharmander = (Button) findViewById(R.id.choose_charmander_button);
        chooseCharmander.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                new ChoosePokemon().execute(username, getString(R.string.choose_charmander));
            }
        });

        chooseSquirtle = (Button) findViewById(R.id.choose_squirtle_button);
        chooseSquirtle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                new ChoosePokemon().execute(username, getString(R.string.choose_squirtle));
            }
        });
    }

    private class ChoosePokemon extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String rootUrl = "http://ec2-54-193-111-74.us-west-1.compute.amazonaws.com:8080";
            String appUrl = "/tritonmon-server";
            int pokemonId;
            if (params[1] == getString(R.string.choose_bulbasaur)) {
                pokemonId = 1;
            } else if(params[1] == getString(R.string.choose_charmander)) {
                pokemonId = 4;
            } else if (params[1] == getString(R.string.choose_squirtle)) {
                pokemonId = 7;
            } else {
                return false;
            }

            String queryUrl = "/addpokemon/starter/" + params[0] + "/" + pokemonId;
            String url = rootUrl + appUrl + queryUrl;

            Log.i("request", url);

            HttpClient httpclient = new DefaultHttpClient();

            // Prepare a request object
            HttpPost httpPost = new HttpPost(url);

            // Execute the request
            HttpResponse response;
            try {
                response = httpclient.execute(httpPost);
                // Examine the response status
                Log.i("response", response.getStatusLine().toString());
                return response.getStatusLine().getStatusCode() != STATUS_CODE_INTERNAL_SERVER_ERROR;
            } catch (Exception e) { // FIXME should not be catching all exceptions
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Intent i = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(i);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
            return rootView;
        }
    }
}
