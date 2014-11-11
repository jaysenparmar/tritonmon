package com.tritonmon.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private static final int CHOOSE_POKEMON_DIALOGUE = 4;

    private TextView line1Text;
    private TextView line2Text;

    private LinearLayout choosePokemonLayout;
    private ImageButton bulbasaurButton;
    private ImageButton charmanderButton;
    private ImageButton squirtleButton;

    private ImageView profImage;

    private LinearLayout boyOrGirlLayout;
    private Button boyButton;
    private Button girlButton;

    int screenTapCount;
    boolean pauseScreenTap;
    List<String> line1Array;
    List<String> line2Array;
    AnimatorSet textAnimSet;

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
        line1Text.setAlpha(0f);
        ObjectAnimator line1Anim = ObjectAnimator.ofFloat(line1Text, "alpha", 0f, 1f)
                .setDuration(Constant.ANIM_FADE_LENGTH);
        line2Text = (TextView) findViewById(R.id.line2Text);
        line2Text.setAlpha(0f);
        ObjectAnimator line2Anim = ObjectAnimator.ofFloat(line2Text, "alpha", 0f, 1f)
                .setDuration(Constant.ANIM_FADE_LENGTH);

        choosePokemonLayout = (LinearLayout) findViewById(R.id.choosePokemonLayout);
        choosePokemonLayout.setAlpha(0f);
        choosePokemonLayout.setVisibility(View.GONE);
        bulbasaurButton = (ImageButton) findViewById(R.id.bulbasaurButton);
        bulbasaurButton.setOnClickListener(clickBulbasaur);
        charmanderButton = (ImageButton) findViewById(R.id.charmanderButton);
        charmanderButton.setOnClickListener(clickCharmander);
        squirtleButton = (ImageButton) findViewById(R.id.squirtleButton);
        squirtleButton.setOnClickListener(clickSquirtle);

        profImage = (ImageView) findViewById(R.id.profImage);
        profImage.setAlpha(0f);
        ObjectAnimator profImageAnim = ObjectAnimator.ofFloat(profImage, "alpha", 0f, 1f)
                .setDuration(2*Constant.ANIM_FADE_LENGTH);

        boyOrGirlLayout = (LinearLayout) findViewById(R.id.boyOrGirlLayout);
        boyOrGirlLayout.setAlpha(0f);
        boyOrGirlLayout.setVisibility(View.GONE);
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
            line = line.replaceAll("PLAYER", redFont(CurrentUser.getUser().getUsername()));
            line = line.replaceAll("HOMETOWN", redFont(CurrentUser.getUser().getHometown()));
            line1Array.add(line);
        }
        line2Array = new ArrayList<String>();
        for (String line : line2TempArray) {
            line = line.replaceAll("PLAYER", redFont(CurrentUser.getUser().getUsername()));
            line = line.replaceAll("HOMETOWN", redFont(CurrentUser.getUser().getHometown()));
            line2Array.add(line);
        }

        updateText();

        textAnimSet = new AnimatorSet();
        textAnimSet.playTogether(line1Anim, line2Anim);

        AnimatorSet profFadeAnimSet = new AnimatorSet();
        profFadeAnimSet.play(profImageAnim).before(textAnimSet);
        profFadeAnimSet.start();
    }

    private String redFont(String text) {
        return "<font color=#ff0000>" + text + "</font>";
    }

    private void updateText() {
        line1Text.setText(Html.fromHtml(line1Array.get(screenTapCount)));
        line2Text.setText(Html.fromHtml(line2Array.get(screenTapCount)));
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

                if (screenTapCount == BOY_OR_GIRL_DIALOGUE) {
                    pauseScreenTap = true;
                    updateText();
                    boyOrGirlLayout.setVisibility(View.VISIBLE);

                    ObjectAnimator boyOrGirlAnim = ObjectAnimator.ofFloat(boyOrGirlLayout, "alpha", 0f, 1f)
                            .setDuration(Constant.ANIM_FADE_LENGTH);
                    AnimatorSet boyOrGirlAnimSet = new AnimatorSet();
                    boyOrGirlAnimSet.play(boyOrGirlAnim).after(textAnimSet);
                    boyOrGirlAnimSet.start();
                } else if (screenTapCount == CHOOSE_POKEMON_DIALOGUE) {
                    pauseScreenTap = true;
                    updateText();
                    choosePokemonLayout.setVisibility(View.VISIBLE);

                    ObjectAnimator choosePokemonAnim = ObjectAnimator.ofFloat(choosePokemonLayout, "alpha", 0f, 1f)
                            .setDuration(Constant.ANIM_FADE_LENGTH);
                    AnimatorSet choosePokemonAnimSet = new AnimatorSet();
                    choosePokemonAnimSet.play(choosePokemonAnim).after(textAnimSet);
                    choosePokemonAnimSet.start();
                }
                else if (screenTapCount < line1Array.size()) {
                    updateText();
                    textAnimSet.start();
                } else {
                    Intent i = new Intent(getApplicationContext(), MainMenu.class);
                    startActivity(i);
                }
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
                boyOrGirlLayout.setVisibility(View.GONE);
                line1Text.setText(Html.fromHtml(line1Array.get(screenTapCount)));
                line2Text.setText(Html.fromHtml(line2Array.get(screenTapCount)));
                textAnimSet.start();
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
                line1Text.setText(Html.fromHtml(line1Array.get(screenTapCount)));
                line2Text.setText(Html.fromHtml(line2Array.get(screenTapCount)));
                textAnimSet.start();
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

    @Override
    public void onBackPressed() {
        // disable back button during user registration
    }
}
