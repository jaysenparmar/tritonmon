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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.MyHttpClient;

import org.apache.http.HttpResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Welcome extends Activity {

    private static final int BOY_OR_GIRL_DIALOGUE = 2;
    private static final int CHOOSE_POKEMON_DIALOGUE = 5;

    private TextView line1Text;
    private TextView line2Text;

    private LinearLayout choosePokemonLayout;
    private Button bulbasaurButton;
    private Button charmanderButton;
    private Button squirtleButton;

    private LinearLayout boyOrGirlLayout;
    private Button boyButton;
    private Button girlButton;

    int screenTapCount;
    boolean pauseScreenTap;
    List<String> line1Array;
    List<String> line2Array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        line1Text = (TextView) findViewById(R.id.line1Text);
        line2Text = (TextView) findViewById(R.id.line2Text);

        choosePokemonLayout = (LinearLayout) findViewById(R.id.choosePokemonLayout);
        choosePokemonLayout.setVisibility(View.GONE);
        bulbasaurButton = (Button) findViewById(R.id.bulbasaurButton);
        bulbasaurButton.setOnClickListener(clickBulbasaur);
        charmanderButton = (Button) findViewById(R.id.charmanderButton);
        charmanderButton.setOnClickListener(clickCharmander);
        squirtleButton = (Button) findViewById(R.id.squirtleButton);
        squirtleButton.setOnClickListener(clickSquirtle);

        boyOrGirlLayout = (LinearLayout) findViewById(R.id.boyOrGirlLayout);
        boyOrGirlLayout.setVisibility(View.INVISIBLE);

        boyButton = (Button) findViewById(R.id.boyButton);
        boyButton.setOnClickListener(clickBoy);

        girlButton = (Button) findViewById(R.id.girlButton);
        girlButton.setOnClickListener(clickGirl);

        screenTapCount = 0;
        pauseScreenTap = false;
        String[] line1TempArray = getResources().getStringArray(R.array.welcome_line1_array);
        String[] line2TempArray = getResources().getStringArray(R.array.welcome_line2_array);

        line1Array = new ArrayList<String>();
        for (String line : line1TempArray) {
            line1Array.add(line.replaceAll("PLAYER", CurrentUser.getUser().getUsername()));
        }
        line1Text.setText(line1Array.get(0));

        line2Array = new ArrayList<String>();
        for (String line : line2TempArray) {
            line2Array.add(line.replaceAll("PLAYER", CurrentUser.getUser().getUsername()));
        }
        line2Text.setText(line2Array.get(0));
    }

    View.OnClickListener clickBoy = new View.OnClickListener() {
        public void onClick(View v) {
            new BoyOrGirl().execute("M");
        }
    };

    View.OnClickListener clickGirl = new View.OnClickListener() {
        public void onClick(View v) {
            new BoyOrGirl().execute("F");
        }
    };

    View.OnClickListener clickBulbasaur = new View.OnClickListener() {
        public void onClick(View v) {
            new ChoosePokemon().execute(getString(R.string.bulbasaur));
        }
    };

    View.OnClickListener clickCharmander = new View.OnClickListener() {
        public void onClick(View v) {
            new ChoosePokemon().execute(getString(R.string.charmander));
        }
    };

    View.OnClickListener clickSquirtle = new View.OnClickListener() {
        public void onClick(View v) {
            new ChoosePokemon().execute(getString(R.string.squirtle));
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!pauseScreenTap) {
                screenTapCount++;
            }

            if (screenTapCount < line1Array.size()) {
                line1Text.setText(line1Array.get(screenTapCount));
                line2Text.setText(line2Array.get(screenTapCount));
            }

            if (screenTapCount == BOY_OR_GIRL_DIALOGUE) {
                boyOrGirlLayout.setVisibility(View.VISIBLE);
                pauseScreenTap = true;
            }
            else if (screenTapCount == CHOOSE_POKEMON_DIALOGUE) {
                choosePokemonLayout.setVisibility(View.VISIBLE);
                pauseScreenTap = true;
            }
            else if (screenTapCount >= line1Array.size()) {
                Intent i = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(i);
            }
        }

        return super.onTouchEvent(event);
    }

    private class BoyOrGirl extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String url = null;
            try {
                url = Constant.SERVER_URL + "/update/table=users" +
                        "/setcolumn=gender/setvalue=" + URLEncoder.encode("\"" + params[0] + "\"", Constant.ENCODING) +
                        "/column=username/value=" + URLEncoder.encode("\"" + CurrentUser.getUser().getUsername() + "\"", Constant.ENCODING);
            }
            catch (UnsupportedEncodingException e) {
                Log.e("Welcome", "URLEncoder threw UnsupportedEncodingException");
                e.printStackTrace();
            }

            HttpResponse response = MyHttpClient.post(url);
            if (MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS) {
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                pauseScreenTap = false;
                screenTapCount++;
                boyOrGirlLayout.setVisibility(View.INVISIBLE);
                line1Text.setText(line1Array.get(screenTapCount));
                line2Text.setText(line2Array.get(screenTapCount));
            }
        }
    }

    private class ChoosePokemon extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            int pokemonId;
            if (params[0].equals(getString(R.string.bulbasaur))) {
                pokemonId = 1;
            } else if(params[0].equals(getString(R.string.charmander))) {
                pokemonId = 4;
            } else if (params[0].equals(getString(R.string.squirtle))) {
                pokemonId = 7;
            } else {
                return false;
            }

            String url = Constant.SERVER_URL + "/addpokemon/starter/" + CurrentUser.getUser().getUsername() + "/" + pokemonId;
            HttpResponse response = MyHttpClient.post(url);
            return MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                pauseScreenTap = false;
                screenTapCount++;
                choosePokemonLayout.setVisibility(View.GONE);
                line1Text.setText(line1Array.get(screenTapCount));
                line2Text.setText(line2Array.get(screenTapCount));
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

        //noinspection SimplifiableIfStatement
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
