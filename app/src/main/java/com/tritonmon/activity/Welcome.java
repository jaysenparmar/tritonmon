package com.tritonmon.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.MyHttpClient;

import org.apache.http.HttpResponse;

public class Welcome extends Activity {

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

        chooseBulbasaur = (Button) findViewById(R.id.choose_bulbasaur_button);
        chooseBulbasaur.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                new ChoosePokemon().execute(CurrentUser.getUser().getUsername(), getString(R.string.choose_bulbasaur));
            }
        });

        chooseCharmander = (Button) findViewById(R.id.choose_charmander_button);
        chooseCharmander.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                new ChoosePokemon().execute(CurrentUser.getUser().getUsername(), getString(R.string.choose_charmander));
            }
        });

        chooseSquirtle = (Button) findViewById(R.id.choose_squirtle_button);
        chooseSquirtle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                new ChoosePokemon().execute(CurrentUser.getUser().getUsername(), getString(R.string.choose_squirtle));
            }
        });


        if (CurrentUser.exists()) {
            welcomeTitle.setText(welcomeTitle.getText() + " " + CurrentUser.getUser().getUsername());
        }
        else {
            welcomeTitle.setText(welcomeTitle.getText());
        }
    }

    private class ChoosePokemon extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
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

            String url = Constant.SERVER_URL + "/addpokemon/starter/" + params[0] + "/" + pokemonId;
            HttpResponse response = MyHttpClient.post(url);
            return MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS;
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
