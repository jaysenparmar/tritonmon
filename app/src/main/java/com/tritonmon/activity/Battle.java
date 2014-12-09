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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import com.tritonmon.global.singleton.MyApplication;
import com.tritonmon.global.singleton.MyRandom;
import com.tritonmon.global.util.ImageUtil;
import com.tritonmon.global.util.ProgressBarUtil;
import com.tritonmon.model.BattlingPokemon;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.model.UsersPokemon;
import com.tritonmon.staticmodel.Pokemon;
import com.tritonmon.toast.TritonmonToast;

import java.util.ArrayList;
import java.util.List;


public class Battle extends Activity {

    private RelativeLayout battleScreen;
    private ImageView enemyPokemonBaseImage;

    private LinearLayout enemyPokemonInfo;
    private TextView enemyPokemonName;
    private TextView enemyPokemonType;
    private TextView enemyPokemonAilment;
    private TextView enemyPokemonHealth;
    private ProgressBar enemyPokemonHealthBar;
    private ImageView enemyPokemonImage;

    private LinearLayout myPokemonInfo;
    private TextView myPokemonName;
    private TextView myPokemonType;
    private TextView myPokemonAilment;
    private TextView myPokemonHealth;
    private TextView myPokemonXP;
    private ProgressBar myPokemonHealthBar;
    private ProgressBar myPokemonXPBar;
    private ImageView myPokemonImage;

    private FrameLayout messagesLayout;
    private TextView messagesText;
    private List<String> messagesList;
    private boolean showingMessages;
    private boolean lastMessage;
    private Boolean hasAnotherPokemon;

    private LinearLayout battleOptions;

    private Button move1Button, move2Button, move3Button, move4Button;
    private Button partyButton, runButton;
    private ImageView pokeballImage;

    private FrameLayout numPokeballsLayout;
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


    // should set this when swap a pokemon out too
    private int oldXp;

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

        // anurag
        selectedPokemonIndex = getIntent().getIntExtra("selectedPokemonIndex", 0);

        // initialize PokemonBattle
        pokemon1 = CurrentUser.getPokemonParty().getPokemon(selectedPokemonIndex).toBattlingPokemon();
        int pokemon2Level = BattleUtil.getRandomPokemonLevel(CurrentUser.getPokemonParty().getPokemon(0).getLevel());
        pokemon2 = new BattlingPokemon(Pokemon.getPokemonId(BattleUtil.getRandomPokemonId(pokemon2Level)), pokemon2Level, true);
//        pokemon2 = new BattlingPokemon(147, pokemon2Level, true);

        pokemonBattle = new PokemonBattle(pokemon1, pokemon2, CurrentUser.getUser().getNumPokeballs());

        oldXp = pokemon1.getXp();

        // initialize screen elements
        battleScreen = (RelativeLayout) findViewById(R.id.battleScreenLayout);
        int randBG = MyRandom.getInstance().nextInt(Constant.NUM_BATTLE_BGS) + 1;
        battleScreen.setBackgroundResource(ImageUtil.getImageResource(this, Constant.BATTLE_BG_NAME + randBG));

        enemyPokemonBaseImage = (ImageView) findViewById(R.id.enemyBaseImage);
        int randBase = MyRandom.getInstance().nextInt(Constant.NUM_BATTLE_BASES) + 1;
        enemyPokemonBaseImage.setImageResource(ImageUtil.getImageResource(this, Constant.BATTLE_BASE_NAME + randBase));

        enemyPokemonInfo = (LinearLayout) findViewById(R.id.enemyPokemonInfo);
        enemyPokemonName = (TextView) findViewById(R.id.enemyPokemonName);
        enemyPokemonType = (TextView) findViewById(R.id.enemyPokemonType);
        enemyPokemonAilment = (TextView) findViewById(R.id.enemyPokemonAilment);
        enemyPokemonHealth = (TextView) findViewById(R.id.enemyPokemonHealth);
        enemyPokemonHealthBar = (ProgressBar) findViewById(R.id.enemyPokemonHealthBar);
        enemyPokemonImage = (ImageView) findViewById(R.id.enemyPokemonImage);

        myPokemonInfo = (LinearLayout) findViewById(R.id.myPokemonInfo);
        myPokemonName = (TextView) findViewById(R.id.myPokemonName);
        myPokemonType = (TextView) findViewById(R.id.myPokemonType);
        myPokemonAilment = (TextView) findViewById(R.id.myPokemonAilment);
        myPokemonHealth = (TextView) findViewById(R.id.myPokemonHealth);
        myPokemonXP = (TextView) findViewById(R.id.myPokemonXP);
        myPokemonHealthBar = (ProgressBar) findViewById(R.id.myPokemonHealthBar);
        myPokemonXPBar = (ProgressBar) findViewById(R.id.myPokemonXPBar);
        myPokemonImage = (ImageView) findViewById(R.id.myPokemonImage);

        battleOptions = (LinearLayout) findViewById(R.id.battleOptions);

        messagesLayout = (FrameLayout) findViewById(R.id.messagesLayout);
        messagesText = (TextView) findViewById(R.id.messagesText);
        lastMessage = false;
        hasAnotherPokemon = null;

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

        numPokeballsLayout = (FrameLayout) findViewById(R.id.numPokeballsFrameLayout);
        numPokeballsLayout.setVisibility(View.INVISIBLE);

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
        String battleIntro = "An " + pokemon2.getName() + " appeared!"
                + "<br />Go get 'em " + pokemon1.getName() + "!";
        messagesList = new ArrayList<String>();
        messagesList.add(battleIntro);
        handleMessages();

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
    public void onBackPressed() {
        if (!backButtonPressed) {
            backButtonPressed = true;
            TritonmonToast.makeText(getApplicationContext(), "Press back again to run from battle", Toast.LENGTH_SHORT).show();
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
                    new UpdateAfterBattleTask(pokemon1, CurrentUser.getUsersId(), pokemonBattle.getNumPokeballs());
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
                if (moveId != null) {

                    // make sure PP > 0
                    int moveIndex = pokemon1.getMoves().indexOf(moveId);
                    if (pokemon1.getPps().get(moveIndex) <= 0) {
                        return;
                    }

                    Audio.sfx.start();

                    MoveResponse moveResponse = pokemonBattle.doMove(moveId);

                    pokemon1 = moveResponse.getPokemon1();
                    pokemon2 = moveResponse.getPokemon2();

                    updateMyPokemonBattleUI();
                    updateEnemyPokemonBattleUI();

                    addBattleMessages(moveResponse);
                    handleMessages();

                    int moveArrayIndex = moveResponse.getPokemon1().getMoves().indexOf(moveId);
                    button.setText(Constant.movesData.get(moveId).getName() + " (" + moveResponse.getPokemon1().getPps().get(moveArrayIndex) + "/" + Constant.movesData.get(moveId).getPp() + ")");

                    if (pokemon2.getHealth() <= 0) {
                        BattleResponse battleResponse = pokemonBattle.endBattle();
                        UsersPokemon oldPokemon1 = battleResponse.getPokemon1Initial();
                        pokemon1 = battleResponse.getPokemon1();

                        handleAfterBattle(pokemon1, pokemonBattle.getNumPokeballs());

                        String winMessage = "";
                        winMessage += pokemon2.getName() + " fainted<br />";
                        Log.e("xp diff", "old: " + oldPokemon1.getXp() + ", new " + pokemon1.getXp());

                        winMessage += oldPokemon1.getName() + " gained " + (pokemon1.getXp() - oldPokemon1.getXp()) + " xp";
                        messagesList.add(winMessage);

                        if (oldPokemon1.getLevel() != pokemon1.getLevel()) {
                            messagesList.add(oldPokemon1.getName() + " leveled up to level " + pokemon1.getLevel() + "!");
                        }
                        if (!battleResponse.getMovesLearned().isEmpty()) {
                            List<String> learned = battleResponse.getMovesLearned();
                            List<String> forgotten = battleResponse.getMovesForgotten();

                            for (int i=0; i<learned.size(); i++) {
                                if (!forgotten.isEmpty()) {
                                    messagesList.add(oldPokemon1.getName() + " forgot " + forgotten.get(i) + " and learned " + learned.get(i) + "!");
                                }
                                else {
                                    messagesList.add(oldPokemon1.getName() + " learned " + learned.get(i) + "!");
                                }
                            }
                        }
                        if (battleResponse.isEvolved()) {
                            messagesList.add(oldPokemon1.getName() + " evolved into " + pokemon1.getName() + "!");
                        }

                        lastMessage = true;
                        handleMessages();
                    }
                    else if (pokemon1.getHealth() <= 0) {
                        handleAfterBattle(pokemon1, pokemonBattle.getNumPokeballs());
                        CurrentUser.getPokemonParty().getPokemon(pokemon1).setHealth(pokemon1.getHealth());

                        messagesList.add(pokemon1.getName() + " fainted");
                        hasAnotherPokemon = false;
                        for (UsersPokemon pokemon : CurrentUser.getPokemonParty().getPokemonList()) {
                            if (pokemon.getHealth() > 0) {
                                hasAnotherPokemon = true;
                            }
                        }

                        if (!hasAnotherPokemon) {
                            lastMessage = true;
                            messagesList.add("All your Pokemon have fainted!<br />You ran away!");
                        }
                        else {
                            lastMessage = false;
                        }

                        handleMessages();
                    }
                }
            }
        });
    }

    private void setMoveButtonLabel(Button button, int moveArrayIndex, Integer moveId) {
        if (moveId == null) {
            button.setAlpha(0.5f);
            button.setText("-");
            button.setBackgroundResource(ImageUtil.getImageResource(getApplicationContext(), "atk_btn"));
            button.setEnabled(false);
        }
        else {
            button.setAlpha(1f);
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
            updateMyPokemonBattleUI();
            enemyPokemonInfo.setVisibility(View.VISIBLE);
            updateEnemyPokemonBattleUI();
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
            if (Audio.isAudioEnabled()) {
                Audio.sfx.start();
            }
            Intent i = new Intent(getApplicationContext(), BattleParty.class);
            i.putExtra("selectedPokemonIndex", selectedPokemonIndex);
            startActivityForResult(i, Constant.REQUEST_CODE_BATTLE_PARTY);
        }
    };

    private View.OnClickListener clickThrowPokeball = new View.OnClickListener() {
        public void onClick(View v) {
            if (Audio.isAudioEnabled()) {
                Audio.sfx.start();
            }
            if (pokemonBattle.getNumPokeballs() > 0) {

                MoveResponse moveResponse = pokemonBattle.throwPokeball();
                numPokeballsText.setText(Integer.toString(pokemonBattle.getNumPokeballs()));

                if (moveResponse.isCaughtPokemon()) {
                    CatchResponse catchResponse = pokemonBattle.endBattleWithCatch();

                    handleCaughtPokemon(catchResponse);

                    messagesList.add("Congratulations!<br />You caught the " + pokemon2.getName() + "!");
                    lastMessage = true;
                    handleMessages();
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

    private String getPokemonTypes(int pokemonId) {
        List<Integer> typeIds = Constant.pokemonData.get(pokemonId).getTypeIds();
        String typeString = "";
        for (Integer typeId : typeIds) {
            if (!typeString.isEmpty()) {
                typeString += ", ";
            }
            typeString += Constant.typesData.get(typeId).getName();
        }

        typeString += " type";

        return typeString;
    }

    private void updateMyPokemonBattleUI() {
        myPokemonName.setText(pokemon1.getName() + " (Lvl " + pokemon1.getLevel() + ")");
        myPokemonType.setText(getPokemonTypes(pokemon1.getPokemonId()));
        myPokemonAilment.setText(shortenAilment(pokemon1.getStatus()));
        myPokemonHealth.setText("HP " + pokemon1.getHealth() + " / " + pokemon1.getMaxHealth());
        myPokemonXP.setText("XP " + pokemon1.getCurrentXPBar() + " / " + pokemon1.getTotalXPBar());
        ProgressBarUtil.updateHealthBar(this, myPokemonHealthBar, pokemon1.getHealth(), pokemon1.getMaxHealth());
        myPokemonXPBar.setProgress(ProgressBarUtil.getPercentage(pokemon1.getCurrentXPBar(), pokemon1.getTotalXPBar()));
        myPokemonImage.setImageResource(ImageUtil.getPokemonBackImageResource(this, pokemon1.getPokemonId()));
    }

    private void updateEnemyPokemonBattleUI() {
        enemyPokemonName.setText(pokemon2.getName() + " (Lvl " + pokemon2.getLevel() + ")");
        enemyPokemonType.setText(getPokemonTypes(pokemon2.getPokemonId()));
        enemyPokemonAilment.setText(shortenAilment(pokemon2.getStatus()));
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
                CurrentUser.getUsersId(),
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
            if (lastMessage) {
                if (Audio.isAudioEnabled()) {
                    mp.release();
                }

                Intent i = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(i);
            }
            else {
                showingMessages = false;
                messagesLayout.setVisibility(View.INVISIBLE);
                numPokeballsLayout.setVisibility(View.VISIBLE);
                battleOptions.setVisibility(View.VISIBLE);

                if (hasAnotherPokemon != null && hasAnotherPokemon) {
                    Intent i = new Intent(getApplicationContext(), BattleParty.class);
                    i.putExtra("selectedPokemonIndex", selectedPokemonIndex);
                    startActivityForResult(i, Constant.REQUEST_CODE_BATTLE_PARTY);
                    startActivity(i);
                }
            }
        }
        else {
            String message = messagesList.remove(0);
            message = message.replaceAll(pokemon1.getName(), Constant.redText(pokemon1.getName()));
            message = message.replaceAll(pokemon2.getName(), "enemy " + Constant.redText(pokemon2.getName()));

            messagesText.setText(Html.fromHtml(message));
            showingMessages = true;
            messagesLayout.setVisibility(View.VISIBLE);
            numPokeballsLayout.setVisibility(View.INVISIBLE);
            battleOptions.setVisibility(View.INVISIBLE);
        }
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
            String humanMessages = listToString(moveResponse.getBattleMessages1().getAllMessages());
            if (!humanMessages.isEmpty()) {
                messagesList.add(humanMessages);
            }
            String enemyMessages = listToString(moveResponse.getBattleMessages2().getAllMessages());
            if (!enemyMessages.isEmpty()) {
                messagesList.add(enemyMessages);
            }
        }
        else {
            String enemyMessages = listToString(moveResponse.getBattleMessages2().getAllMessages());
            if (!enemyMessages.isEmpty()) {
                messagesList.add(enemyMessages);
            }
            String humanMessages = listToString(moveResponse.getBattleMessages1().getAllMessages());
            if (!humanMessages.isEmpty()) {
                messagesList.add(humanMessages);
            }
        }

        myPokemonAilment.setText(shortenAilment(pokemon1.getStatus()));
        enemyPokemonAilment.setText(shortenAilment(pokemon2.getStatus()));
    }

    private String shortenAilment(String ailment) {
        ailment = ailment.replaceAll("none", "");
        ailment = ailment.replaceAll("paralysis", "PRZ");
        ailment = ailment.replaceAll("sleep", "SLP");
        ailment = ailment.replaceAll("freeze", "FRZ");
        ailment = ailment.replaceAll("burn", "BRN");
        ailment = ailment.replaceAll("poison", "PSN");
        ailment = ailment.replaceAll("confusion", "CON");
        return ailment;
    }

}
