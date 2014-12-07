package com.tritonmon.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.asynctask.battle.CaughtPokemonTask;
import com.tritonmon.asynctask.battle.UpdateAfterBattleTask;
import com.tritonmon.battle.BattleUtil;
import com.tritonmon.battle.PokemonBattle;
import com.tritonmon.battle.requestresponse.BattleResponse;
import com.tritonmon.battle.requestresponse.CatchResponse;
import com.tritonmon.battle.requestresponse.MoveResponse;
import com.tritonmon.exception.PartyException;
import com.tritonmon.global.Audio;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.singleton.MyRandom;
import com.tritonmon.global.util.ImageUtil;
import com.tritonmon.global.util.ProgressBarUtil;
import com.tritonmon.model.BattlingPokemon;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.staticmodel.Pokemon;
import com.tritonmon.toast.TritonmonToast;

import java.util.ArrayList;
import java.util.List;


public class Battle extends Activity {

    private RelativeLayout battleScreen;
    private ImageView enemyPokemonBaseImage;

    private LinearLayout enemyPokemonInfo;
    private TextView enemyPokemonName;
    private TextView enemyPokemonHealth;
    private ProgressBar enemyPokemonHealthBar;
    private ImageView enemyPokemonImage;

    private LinearLayout myPokemonInfo;
    private TextView myPokemonName;
    private TextView myPokemonHealth;
    private TextView myPokemonXP;
    private ProgressBar myPokemonHealthBar;
    private ProgressBar myPokemonXPBar;
    private ImageView myPokemonImage;

    private FrameLayout messagesLayout;
    private TextView messagesText;
    private List<String> messagesList;
    private boolean showingMessages;

    private LinearLayout battleOptions;

    private Button move1Button, move2Button, move3Button, move4Button;
    private Button partyButton, runButton;
    private ImageView pokeballImage;
    private TextView numPokeballsText;

    private BattlingPokemon pokemon1, pokemon2;
    private PokemonBattle pokemonBattle;
    private Integer move1Id;
    private Integer move2Id;
    private Integer move3Id;
    private Integer move4Id;

    private MediaPlayer mp;
    private MediaPlayer looper;

    private Animation translateRightAnim, translateLeftAnim;
    private Animation fadeInAnim;

    private int selectedPokemonIndex;

    private Handler backButtonHandler;
    private boolean backButtonPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_battle);

        // music
        if(mp != null) {
            mp.release();
            mp = null;
        }
        if(looper != null) {
            looper.release();
            looper = null;
        }

        mp = MediaPlayer.create(this, R.raw.battle_first_loop);
        looper = MediaPlayer.create(this, R.raw.battle_loop);
        looper.setLooping(true);
        mp.setNextMediaPlayer(looper);
        Audio.setBackgroundMusic(mp);

        if (Audio.isAudioEnabled()) {
            mp.start();
        }

        selectedPokemonIndex = 0;
        chooseNextPokemon();

        // initialize PokemonBattle
        pokemon1 = CurrentUser.getPokemonParty().getPokemon(selectedPokemonIndex).toBattlingPokemon();
        int pokemon2Level = BattleUtil.getRandomPokemonLevel(CurrentUser.getPokemonParty().getPokemon(0).getLevel());
        pokemon2 = new BattlingPokemon(Pokemon.getPokemonId(BattleUtil.getRandomPokemonId(pokemon2Level)), pokemon2Level, true);
//        pokemon2 = new BattlingPokemon(308, 20, true); // for testing

        pokemonBattle = new PokemonBattle(pokemon1, pokemon2, CurrentUser.getUser().getNumPokeballs());

        // initialize screen elements
        battleScreen = (RelativeLayout) findViewById(R.id.battleScreenLayout);
        int randBG = MyRandom.getInstance().nextInt(Constant.NUM_BATTLE_BGS) + 1;
        battleScreen.setBackgroundResource(ImageUtil.getImageResource(this, Constant.BATTLE_BG_NAME + randBG));

        enemyPokemonBaseImage = (ImageView) findViewById(R.id.enemyBaseImage);
        int randBase = MyRandom.getInstance().nextInt(Constant.NUM_BATTLE_BASES) + 1;
        enemyPokemonBaseImage.setImageResource(ImageUtil.getImageResource(this, Constant.BATTLE_BASE_NAME + randBase));

        enemyPokemonInfo = (LinearLayout) findViewById(R.id.enemyPokemonInfo);
        enemyPokemonName = (TextView) findViewById(R.id.enemyPokemonName);
        enemyPokemonHealth = (TextView) findViewById(R.id.enemyPokemonHealth);
        enemyPokemonHealthBar = (ProgressBar) findViewById(R.id.enemyPokemonHealthBar);
        enemyPokemonImage = (ImageView) findViewById(R.id.enemyPokemonImage);

        myPokemonInfo = (LinearLayout) findViewById(R.id.myPokemonInfo);
        myPokemonName = (TextView) findViewById(R.id.myPokemonName);
        myPokemonHealth = (TextView) findViewById(R.id.myPokemonHealth);
        myPokemonXP = (TextView) findViewById(R.id.myPokemonXP);
        myPokemonHealthBar = (ProgressBar) findViewById(R.id.myPokemonHealthBar);
        myPokemonXPBar = (ProgressBar) findViewById(R.id.myPokemonXPBar);
        myPokemonImage = (ImageView) findViewById(R.id.myPokemonImage);

        battleOptions = (LinearLayout) findViewById(R.id.battleOptions);

        messagesLayout = (FrameLayout) findViewById(R.id.messagesLayout);
        messagesText = (TextView) findViewById(R.id.messagesText);

        move1Button = (Button) findViewById(R.id.move1Button);
        move2Button = (Button) findViewById(R.id.move2Button);
        move3Button = (Button) findViewById(R.id.move3Button);
        move4Button = (Button) findViewById(R.id.move4Button);
        updateMyPokemonMovesUI();

        myPokemonInfo.setVisibility(View.INVISIBLE);
        updateMyPokemonBattleUI();

        enemyPokemonInfo.setVisibility(View.INVISIBLE);
        updateEnemyPokemonBattleUI();

        partyButton = (Button) findViewById(R.id.partyButton);
        partyButton.setOnClickListener(clickParty);
        runButton = (Button) findViewById(R.id.runButton);

        pokeballImage = (ImageView) findViewById(R.id.pokeballButton);
        pokeballImage.setOnClickListener(clickThrowPokeball);
        numPokeballsText = (TextView) findViewById(R.id.numPokeballsText);
        numPokeballsText.setText(Integer.toString(pokemonBattle.getNumPokeballs()));

        runButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                handleAfterBattle(pokemon1, CurrentUser.getUser().getNumPokeballs());
                if (Audio.isAudioEnabled()) {
                    Audio.sfx.start();
                    mp.release();
                }
                Intent i = new Intent(getApplicationContext(), MainMenu.class);
                i.putExtra("ranFromBattle", true);
                startActivity(i);
            }
        });

        // start animations
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        translateRightAnim.setAnimationListener(translateRightAnimListener);

        enemyPokemonBaseImage.startAnimation(translateRightAnim);
        enemyPokemonImage.startAnimation(translateRightAnim);

        myPokemonImage.startAnimation(translateLeftAnim);

        // back button
        backButtonPressed = false;
        backButtonHandler = new Handler();

        // messages
        String battleIntro = "A wild " + pokemon2.getName() + " appeared!"
                + "<br />Go get 'em " + pokemon1.getName() + "!";
        messagesList = new ArrayList<String>();
        messagesList.add(battleIntro);
        handleMessages();
    }

    @Override
    public void onBackPressed() {
        if (!backButtonPressed) {
            backButtonPressed = true;
            TritonmonToast.makeText(getApplicationContext(), "Press again to run from battle", Toast.LENGTH_SHORT).show();
            backButtonHandler.postDelayed(backButtonRunnable, 2000);
        }
        else {
            if (mp != null) {
                mp.release();
            }
            if (looper != null) {
                looper.release();
            }

            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if(mp != null) {
            mp.release();
        }
        if(looper != null) {
            looper.release();
        }

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (requestCode == Constant.REQUEST_CODE_BATTLE_PARTY) {
            if (resultCode == RESULT_OK) {

                selectedPokemonIndex = i.getExtras().getInt("selectedPokemonIndex");
                pokemon1.clearStatus();
                try {
                    CurrentUser.getPokemonParty().remove(pokemon1.getSlotNum());
                    CurrentUser.getPokemonParty().add(pokemon1.getSlotNum(), pokemon1);
                    new UpdateAfterBattleTask(pokemon1, CurrentUser.getUsername(), pokemonBattle.getNumPokeballs());
                }
                catch (PartyException e) {
                    Log.e("Battle", "PartyException thrown when saving old pokemon before swapping out");
                    e.printStackTrace();
                    return;
                }

                pokemon1 = CurrentUser.getPokemonParty().getPokemon(selectedPokemonIndex).toBattlingPokemon();
                pokemonBattle.setPokemon1(pokemon1);
                swapPokemon();
            }
        }
    }

    private void setMoveOnClickListener(final Button button, final Integer moveId){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Audio.sfx.start();
                if (moveId != null) {
                    MoveResponse moveResponse = pokemonBattle.doMove(moveId);

                    pokemon1 = moveResponse.getPokemon1();
                    pokemon2 = moveResponse.getPokemon2();

                    updateMyPokemonBattleUI();
                    myPokemonName.setText(pokemon1.getName() + " (Lvl " + pokemon1.getLevel() + ")");
                    myPokemonHealth.setText("HP " + pokemon1.getHealth() + " / " + pokemon1.getMaxHealth()
                            + "\nMessages: " + moveResponse.getBattleMessages1().getAllMessages());

                    updateEnemyPokemonBattleUI();
                    enemyPokemonName.setText(pokemon2.getName() + " (Lvl " + pokemon2.getLevel() + ")");
                    enemyPokemonHealth.setText("HP " + pokemon2.getHealth() + " / " + pokemon2.getMaxHealth()
                            + "\nMessages: " + moveResponse.getBattleMessages2().getAllMessages());

                    addBattleMessages(moveResponse);
                    handleMessages();

                    int moveArrayIndex = moveResponse.getPokemon1().getMoves().indexOf(moveId);
                    button.setText(Constant.movesData.get(moveId).getName() + " (" + moveResponse.getPokemon1().getPps().get(moveArrayIndex) + "/" + Constant.movesData.get(moveId).getPp() + ")");

                    if (pokemon2.getHealth() <= 0) {
                        TritonmonToast.makeText(getApplicationContext(), "Player won battle!", Toast.LENGTH_LONG).show();
                        BattleResponse battleResponse = pokemonBattle.endBattle();
                        pokemon1 = battleResponse.getPokemon1();

                        handleAfterBattle(pokemon1, pokemonBattle.getNumPokeballs());
                        if (Audio.isAudioEnabled()) {
                            mp.release();
                        }
                        Intent i = new Intent(getApplicationContext(), MainMenu.class);
                        i.putExtra("wonBattle", true);
                        startActivity(i);
                    }
                    else if (pokemon1.getHealth() <= 0) {
                        TritonmonToast.makeText(getApplicationContext(), "Opponent won battle!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void setMoveButtonLabel(Button button, int moveArrayIndex, Integer moveId) {
        if (moveId == null) {
            button.setText("-");
            button.setEnabled(false);
        }
        else {
            button.setText(
                    Constant.movesData.get(moveId).getName()
                            + " (" + pokemon1.getPps().get(moveArrayIndex) + "/" + Constant.movesData.get(moveId).getPp() + ")"
            );
            button.setBackgroundResource(ImageUtil.getAttackButtonImageResource(this, Constant.movesData.get(moveId).getTypeId()));
            button.setEnabled(true);
        }
    }

    private Runnable backButtonRunnable = new Runnable() {
        public void run() {
            backButtonPressed = false;
        }
    };

    private Animation.AnimationListener translateRightAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            myPokemonInfo.setVisibility(View.VISIBLE);
            enemyPokemonInfo.setVisibility(View.VISIBLE);
            myPokemonInfo.startAnimation(fadeInAnim);
            enemyPokemonInfo.startAnimation(fadeInAnim);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private View.OnClickListener clickParty = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Audio.sfx.start();
            Intent i = new Intent(getApplicationContext(), BattleParty.class);
            i.putExtra("selectedPokemonIndex", selectedPokemonIndex);
            startActivityForResult(i, Constant.REQUEST_CODE_BATTLE_PARTY);
        }
    };

    private View.OnClickListener clickThrowPokeball = new View.OnClickListener() {
        public void onClick(View v) {
            Log.e("battle", "threw some pokeball");
            Audio.sfx.start();
            if (pokemonBattle.getNumPokeballs() > 0) {

                MoveResponse moveResponse = pokemonBattle.throwPokeball();
                numPokeballsText.setText(Integer.toString(pokemonBattle.getNumPokeballs()));

                if (moveResponse.isCaughtPokemon()) {
                    TritonmonToast.makeText(getApplicationContext(), "Caught a pokemon!!", Toast.LENGTH_LONG).show();
                    CatchResponse catchResponse = pokemonBattle.endBattleWithCatch();

                    handleCaughtPokemon(catchResponse);

                    if (Audio.isAudioEnabled()) {
                        mp.release();
                    }
                    Intent i = new Intent(getApplicationContext(), MainMenu.class);
                    i.putExtra("caughtPokemon", true);
                    startActivity(i);
                }
                else {
                    pokemon1 = moveResponse.getPokemon1();
                    pokemon2 = moveResponse.getPokemon2();

                    myPokemonName.setText(pokemon1.getName());
                    myPokemonHealth.setText("HP " + pokemon1.getHealth() + " / " + pokemon1.getMaxHealth()
                            + "\nMessages: " + moveResponse.getBattleMessages1().getAllMessages());
                    enemyPokemonName.setText(pokemon2.getName());
                    enemyPokemonHealth.setText("HP " + pokemon2.getHealth() + " / " + pokemon2.getMaxHealth()
                            + "\nMessages: " + moveResponse.getBattleMessages2().getAllMessages());

                    addBattleMessages(moveResponse);
                    handleMessages();
                }
            }

        }
    };

    private void updateMyPokemonBattleUI() {
        myPokemonName.setText(pokemon1.getName() + " (Lvl " + pokemon1.getLevel() + ")");
        myPokemonHealth.setText("HP " + pokemon1.getHealth() + " / " + pokemon1.getMaxHealth());
        myPokemonXP.setText("XP " + pokemon1.getCurrentXPBar() + " / " + pokemon1.getTotalXPBar());
        ProgressBarUtil.updateHealthBar(this, myPokemonHealthBar, pokemon1.getHealth(), pokemon1.getMaxHealth());
        myPokemonXPBar.setProgress(ProgressBarUtil.getPercentage(pokemon1.getCurrentXPBar(), pokemon1.getTotalXPBar()));
        myPokemonImage.setImageResource(ImageUtil.getPokemonBackImageResource(this, pokemon1.getPokemonId()));
    }

    private void updateEnemyPokemonBattleUI() {
        enemyPokemonName.setText(pokemon2.getName() + " (Lvl " + pokemon2.getLevel() + ")");
        enemyPokemonHealth.setText("HP " + pokemon2.getHealth() + " / " + pokemon2.getMaxHealth());
        ProgressBarUtil.updateHealthBar(this, enemyPokemonHealthBar, pokemon2.getHealth(), pokemon2.getMaxHealth());
        enemyPokemonImage.setImageResource(ImageUtil.getPokemonFrontImageResource(this, pokemon2.getPokemonId()));
    }

    private void updateMyPokemonMovesUI() {
        move1Id = pokemon1.getMoves().get(0);
        move2Id = pokemon1.getMoves().get(1);
        move3Id = pokemon1.getMoves().get(2);
        move4Id = pokemon1.getMoves().get(3);

        setMoveButtonLabel(move1Button, 0, move1Id);
        setMoveButtonLabel(move2Button, 1, move2Id);
        setMoveButtonLabel(move3Button, 2, move3Id);
        setMoveButtonLabel(move4Button, 3, move4Id);

        setMoveOnClickListener(move1Button, move1Id);
        setMoveOnClickListener(move2Button, move2Id);
        setMoveOnClickListener(move3Button, move3Id);
        setMoveOnClickListener(move4Button, move4Id);
    }

    private void swapPokemon() {
        updateMyPokemonMovesUI();
        updateMyPokemonBattleUI();
    }

    private void handleAfterBattle(BattlingPokemon pokemon, int numPokeballs) {
        new UpdateAfterBattleTask(
                pokemon.toUsersPokemon(),
                CurrentUser.getUsername(),
                numPokeballs
        ).execute();
    }

    // TODO reduce to 1 server call
    private void handleCaughtPokemon(CatchResponse catchResponse) {
        // server call 1 - update current user's pokemon
        handleAfterBattle(catchResponse.getOldPokemon(), catchResponse.getNumPokeballs());

        // server call 2 - add caught pokemon to user's pokmeon
        BattlingPokemon caughtPokemon = catchResponse.getNewPokemon();
        int slotNum = (CurrentUser.getPokemonParty().size() != PokemonParty.MAX_PARTY_SIZE) ? CurrentUser.getPokemonParty().size() : -1;
        caughtPokemon.setSlotNum(slotNum);
        caughtPokemon.setNickname("oneWithNature");

        new CaughtPokemonTask(caughtPokemon, CurrentUser.getUsersId()).execute();
    }

    private void chooseNextPokemon() {
        while (CurrentUser.getPokemonParty().getPokemon(selectedPokemonIndex).getHealth() <= 0) {
            selectedPokemonIndex++;
            if (selectedPokemonIndex >= PokemonParty.MAX_PARTY_SIZE) {
                TritonmonToast.makeText(this, "All the pokemon in your party have fainted!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private String listToString(List<String> stringList) {
        String out = "";
        for (String s : stringList) {
            if (!out.isEmpty()) {
                out += "<br />";
            }
            out += s;
        }

        return out;
    }

    private void handleMessages() {
        if (messagesList.isEmpty()) {
            showingMessages = false;
            messagesLayout.setVisibility(View.INVISIBLE);
            battleOptions.setVisibility(View.VISIBLE);
        }
        else {
            String message = messagesList.remove(0);
            message = message.replaceAll(CurrentUser.getName(), redText(CurrentUser.getName()));
            message = message.replaceAll(pokemon1.getName(), redText(pokemon1.getName()));
            message = message.replaceAll(pokemon2.getName(), "enemy " + redText(pokemon2.getName()));

            messagesText.setText(Html.fromHtml(message));
            showingMessages = true;
            messagesLayout.setVisibility(View.VISIBLE);
            battleOptions.setVisibility(View.INVISIBLE);
        }
    }

    private String redText(String text) {
        return "<font color=#ff0000>" + text + "</font>";
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (showingMessages) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handleMessages();
            }
        }

        return super.onTouchEvent(event);
    }

    private void addBattleMessages(MoveResponse moveResponse) {
        if (moveResponse.isHumanMovedFirst()) {
            messagesList.add(listToString(moveResponse.getBattleMessages1().getAllMessages()));
            messagesList.add(listToString(moveResponse.getBattleMessages2().getAllMessages()));
        }
        else {
            messagesList.add(listToString(moveResponse.getBattleMessages2().getAllMessages()));
            messagesList.add(listToString(moveResponse.getBattleMessages1().getAllMessages()));
        }
    }
}
