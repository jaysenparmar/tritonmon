package com.tritonmon.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tritonmon.battle.BattleUtil;
import com.tritonmon.battle.handler.XPHandler;
import com.tritonmon.global.Audio;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.singleton.MyHttpClient;
import com.tritonmon.global.util.ListUtil;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class Welcome extends Activity {

    private static final int BOY_OR_GIRL_DIALOGUE = 2;
    private static final int CHOOSE_POKEMON_DIALOGUE = 4;

    private TextView line1Text;
    private TextView line2Text;

    private ImageView choosePokemonWhiteFadeImage;
    private LinearLayout choosePokemonLayout;
    private ImageButton bulbasaurButton;
    private ImageButton charmanderButton;
    private ImageButton squirtleButton;

    private ImageView profImage;

    private LinearLayout boyOrGirlLayout;
    private Button boyButton;
    private Button girlButton;

    private MediaPlayer mp;
    private MediaPlayer looper;

    int screenTapCount;
    boolean pauseScreenTap;
    boolean animDisableTouch;
    List<String> line1Array;
    List<String> line2Array;
    AnimatorSet textAnimSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        if(mp != null) {
            mp.release();
        }
        if(looper != null) {
            looper.release();
        }
        mp = MediaPlayer.create(this, R.raw.welcome_first_loop);
        looper = MediaPlayer.create(this, R.raw.welcome_loop);
        looper.setLooping(true);
        mp.start();
        mp.setNextMediaPlayer(looper);

        line1Text = (TextView) findViewById(R.id.line1Text);
        line1Text.setAlpha(0f);
        ObjectAnimator line1Anim = ObjectAnimator.ofFloat(line1Text, "alpha", 0f, 1f)
                .setDuration(Constant.ANIM_FADE_LENGTH);
        line2Text = (TextView) findViewById(R.id.line2Text);
        line2Text.setAlpha(0f);
        ObjectAnimator line2Anim = ObjectAnimator.ofFloat(line2Text, "alpha", 0f, 1f)
                .setDuration(Constant.ANIM_FADE_LENGTH);

        choosePokemonWhiteFadeImage = (ImageView) findViewById(R.id.choosePokemonWhiteFadeImage);
        choosePokemonWhiteFadeImage.setAlpha(0f);
        choosePokemonWhiteFadeImage.setVisibility(View.GONE);

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
        animDisableTouch = false;
        String[] line1TempArray = getResources().getStringArray(R.array.welcome_line1_array);
        String[] line2TempArray = getResources().getStringArray(R.array.welcome_line2_array);

        line1Array = new ArrayList<String>();
        for (String line : line1TempArray) {
            line = line.replaceAll("PLAYER", Constant.redText(CurrentUser.getName()));
            line = replaceHometown(line);
            line1Array.add(line);
        }
        line2Array = new ArrayList<String>();
        for (String line : line2TempArray) {
            line = line.replaceAll("PLAYER", Constant.redText(CurrentUser.getName()));
            line = replaceHometown(line);
            line2Array.add(line);
        }

        updateText();

        textAnimSet = new AnimatorSet();
        textAnimSet.addListener(disableTouchAnimListener);
        textAnimSet.playTogether(line1Anim, line2Anim);

        AnimatorSet profFadeAnimSet = new AnimatorSet();
        profFadeAnimSet.addListener(disableTouchAnimListener);
        profFadeAnimSet.play(profImageAnim).before(textAnimSet);
        profFadeAnimSet.start();
    }

    @Override
    public void onBackPressed() {
        // disable back button during user registration
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!pauseScreenTap && !animDisableTouch) {
                screenTapCount++;

                if (screenTapCount == BOY_OR_GIRL_DIALOGUE) {
                    pauseScreenTap = true;
                    updateText();
                    boyOrGirlLayout.setVisibility(View.VISIBLE);

                    ObjectAnimator boyOrGirlAnim = ObjectAnimator.ofFloat(boyOrGirlLayout, "alpha", 0f, 1f)
                            .setDuration(Constant.ANIM_FADE_LENGTH);
                    AnimatorSet boyOrGirlAnimSet = new AnimatorSet();
                    boyOrGirlAnimSet.addListener(disableTouchAnimListener);
                    boyOrGirlAnimSet.play(boyOrGirlAnim).after(textAnimSet);
                    boyOrGirlAnimSet.start();
                } else if (screenTapCount == CHOOSE_POKEMON_DIALOGUE) {
                    pauseScreenTap = true;
                    updateText();
                    choosePokemonWhiteFadeImage.setVisibility(View.VISIBLE);
                    choosePokemonLayout.setVisibility(View.VISIBLE);

                    ObjectAnimator choosePokemonWhiteFadeAnim = ObjectAnimator.ofFloat(choosePokemonWhiteFadeImage, "alpha", 0f, 1f)
                            .setDuration(Constant.ANIM_FADE_LENGTH);
                    ObjectAnimator choosePokemonAnim = ObjectAnimator.ofFloat(choosePokemonLayout, "alpha", 0f, 1f)
                            .setDuration(Constant.ANIM_FADE_LENGTH);

                    AnimatorSet choosePokemonAnimSet = new AnimatorSet();
                    choosePokemonAnimSet.addListener(disableTouchAnimListener);
                    choosePokemonAnimSet.play(choosePokemonAnim).after(textAnimSet);
                    choosePokemonAnimSet.play(choosePokemonWhiteFadeAnim).after(choosePokemonAnim);
                    choosePokemonAnimSet.start();
                }
                else if (screenTapCount < line1Array.size()) {
                    updateText();
                    textAnimSet.start();
                }
                else {
                    mp.release();
                    looper.release();
                    Intent i = new Intent(getApplicationContext(), MainMenu.class);
                    startActivity(i);
                }
            }
        }

        return super.onTouchEvent(event);
    }

    private Animator.AnimatorListener disableTouchAnimListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            animDisableTouch = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animDisableTouch = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            animDisableTouch = false;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            animDisableTouch = true;
        }
    };

    private View.OnClickListener clickBoy = new View.OnClickListener() {
        public void onClick(View v) {
            if(Audio.isAudioEnabled()) {
                Audio.sfx.start();
            }
            v.setBackgroundResource(android.R.drawable.button_onoff_indicator_on);
            new BoyOrGirlAsyncTask().execute("M", getResources().getResourceEntryName(R.drawable.maletrainer000));

        }
    };

    private View.OnClickListener clickGirl = new View.OnClickListener() {
        public void onClick(View v) {
            if(Audio.isAudioEnabled()) {
                Audio.sfx.start();
            }
            v.setBackgroundResource(android.R.drawable.button_onoff_indicator_on);
            new BoyOrGirlAsyncTask().execute("F", getResources().getResourceEntryName(R.drawable.femaletrainer001));
        }
    };

    private View.OnClickListener clickBulbasaur = new View.OnClickListener() {
        public void onClick(View v) {
            if(Audio.isAudioEnabled()) {
                Audio.sfx.start();
            }
            new ChoosePokemonAsyncTask().execute(getString(R.string.bulbasaur));
        }
    };

    private View.OnClickListener clickCharmander = new View.OnClickListener() {
        public void onClick(View v) {
            if(Audio.isAudioEnabled()) {
                Audio.sfx.start();
            }
            new ChoosePokemonAsyncTask().execute(getString(R.string.charmander));
        }
    };

    private View.OnClickListener clickSquirtle = new View.OnClickListener() {
        public void onClick(View v) {
            if(Audio.isAudioEnabled()) {
                Audio.sfx.start();
            }
            new ChoosePokemonAsyncTask().execute(getString(R.string.squirtle));
        }
    };

    private String replaceHometown(String line) {
        if (CurrentUser.isFacebookUser()) {
            return line.replaceAll("HOMETOWN", Constant.redText("UCSD"));
        }
        else {
            return line.replaceAll("HOMETOWN", Constant.redText(CurrentUser.getUser().getHometown()));
        }
    }

    private void updateText() {
        line1Text.setText(Html.fromHtml(line1Array.get(screenTapCount)));
        line2Text.setText(Html.fromHtml(line2Array.get(screenTapCount)));
    }

    private class BoyOrGirlAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            CurrentUser.getUser().setGender(params[0]);
            CurrentUser.getUser().setAvatar(params[1]);

            String url = Constant.SERVER_URL + "/update/table=users"
                    + "/setcolumn=gender,avatar"
                    + "/setvalue=" + Constant.encode("\"" + params[0] + "\"") + "," + Constant.encode("\"" + params[1] + "\"")
                    + "/column=username"
                    + "/value=" + Constant.encode("\"" + CurrentUser.getUser().getUsername() + "\"");

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

    private class ChoosePokemonAsyncTask extends AsyncTask<String, Void, Boolean> {

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

            // get starter pokemon's moves
            List<Integer> moves = XPHandler.getNewMoves(pokemonId, 0, 5);
            if (moves.size() > 4) {
                Log.e("Welcome", "Starter Pokemon " + Constant.pokemonData.get(pokemonId) + " can learn more than 4 moves by level 5.");
                return false;
            }

            // get starter pokemon moves' pps
            List<Integer> pps = new ArrayList<Integer>();
            for (int move : moves) {
                pps.add(Constant.movesData.get(move).getPp());
            }

            String url = Constant.SERVER_URL + "/addpokemon/starter/"
                    + CurrentUser.getUsersId() + "/"
                    + pokemonId + "/"
                    + Constant.encode("nick") + "/"
                    + BattleUtil.getMaxStat("hp", pokemonId, 5) + "/"
                    + "moves=" + ListUtil.toCommaSeparatedString(moves) + "/"
                    + "pps=" + ListUtil.toCommaSeparatedString(pps);
            HttpResponse response = MyHttpClient.post(url);
            return MyHttpClient.getStatusCode(response) == Constant.STATUS_CODE_SUCCESS;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                CurrentUser.updatePokemon();
                pauseScreenTap = false; // disable screen taps
                screenTapCount++; // go to next message

                choosePokemonWhiteFadeImage.setVisibility(View.GONE);
                choosePokemonLayout.setVisibility(View.GONE); // make starter pokemon buttons disappear

                line1Text.setText(Html.fromHtml(line1Array.get(screenTapCount))); // update text
                line2Text.setText(Html.fromHtml(line2Array.get(screenTapCount)));
                textAnimSet.start(); // start fade anim
            }
        }
    }

}
