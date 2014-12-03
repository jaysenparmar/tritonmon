package com.tritonmon.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.asynctask.battle.UpdateAfterBattleTask;
import com.tritonmon.battle.BattleUtil;
import com.tritonmon.battle.PokemonBattle;
import com.tritonmon.battle.requestresponse.BattleResponse;
import com.tritonmon.battle.requestresponse.CatchResponse;
import com.tritonmon.battle.requestresponse.MoveResponse;
import com.tritonmon.exception.PartyException;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.global.MyRandom;
import com.tritonmon.global.ProgressBarUtil;
import com.tritonmon.global.TritonmonToast;
import com.tritonmon.model.BattlingPokemon;
import com.tritonmon.model.PokemonParty;
import com.tritonmon.staticmodel.Pokemon;


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
    private Animation translateRightAnim, translateLeftAnim;
    private Animation fadeInAnim;

    private int selectedPokemonIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        // music
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if(mp != null) {
            mp.release();
        }
        mp = MediaPlayer.create(this, R.raw.battle);
        mp.setLooping(true);
        mp.start();

        selectedPokemonIndex = 0;
        while (CurrentUser.getPokemonParty().getPokemon(selectedPokemonIndex).getHealth() <= 0) {
            selectedPokemonIndex++;
            if (selectedPokemonIndex >= PokemonParty.MAX_PARTY_SIZE) {
                TritonmonToast.makeText(this, "All the pokemon in your party have fainted!", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        // initialize PokemonBattle
        pokemon1 = CurrentUser.getPokemonParty().getPokemon(selectedPokemonIndex).toBattlingPokemon();
        pokemon2 = new BattlingPokemon(Pokemon.getPokemonId(BattleUtil.getRandomPokemonId()), BattleUtil.getRandomPokemonLevel(CurrentUser.getPokemonParty().getPokemon(0).getLevel()), true);
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

        // start animations
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        translateRightAnim.setAnimationListener(translateRightAnimListener);

        enemyPokemonBaseImage.startAnimation(translateRightAnim);
        enemyPokemonImage.startAnimation(translateRightAnim);

        myPokemonImage.startAnimation(translateLeftAnim);
    }

    Animation.AnimationListener translateRightAnimListener = new Animation.AnimationListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.battle, menu);
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

    @Override
    protected void onDestroy() {
        if(null!=mp){
            mp.release();
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
                if (moveId != null) {
                    MoveResponse moveResponse = pokemonBattle.doMove(moveId);

                    pokemon1 = moveResponse.getPokemon1();
                    pokemon2 = moveResponse.getPokemon2();

                    updateMyPokemonBattleUI();
                    myPokemonName.setText(pokemon1.getName() + " (Lvl " + pokemon1.getLevel() + ")");
                    myPokemonHealth.setText("HP " + pokemon1.getHealth() + " / " + pokemon1.getMaxHealth()
                            + "\nMessages: " + moveResponse.getBattleMessages1().getAllMessages());
//                            + "\nMoveUsed: " + moveResponse.getBattleMessages1().getMoveUsed()
//                            + "\nStatusMessages: " + moveResponse.getBattleMessages1().getStatusMessages().toString()
//                            + "\nStatChanges: " + moveResponse.getBattleMessages1().getStatChanges()
//                            + "\nAilmentMessage: " + moveResponse.getBattleMessages1().getAilmentMessage());

                    updateEnemyPokemonBattleUI();
                    enemyPokemonName.setText(pokemon2.getName() + " (Lvl " + pokemon2.getLevel() + ")");
                    enemyPokemonHealth.setText("HP " + pokemon2.getHealth() + " / " + pokemon2.getMaxHealth()
                            + "\nMessages: " + moveResponse.getBattleMessages2().getAllMessages());
//                            + "\nMoveUsed: " + moveResponse.getBattleMessages2().getMoveUsed()
//                            + "\nStatusMessages: " + moveResponse.getBattleMessages2().getStatusMessages().toString()
//                            + "\nStatChanges: " + moveResponse.getBattleMessages2().getStatChanges()
//                            + "\nAilmentMessage: " + moveResponse.getBattleMessages2().getAilmentMessage());

                    int moveArrayIndex = moveResponse.getPokemon1().getMoves().indexOf(moveId);
                    button.setText(Constant.movesData.get(moveId).getName() + " (" + moveResponse.getPokemon1().getPps().get(moveArrayIndex) + "/" + Constant.movesData.get(moveId).getPp() + ")");

                    if (pokemon2.getHealth() <= 0) {
                        TritonmonToast.makeText(getApplicationContext(), "Player won battle!", Toast.LENGTH_LONG).show();
                        BattleResponse battleResponse = pokemonBattle.endBattle();
                        pokemon1 = battleResponse.getPokemon1();

                        new UpdateAfterBattleTask(pokemon1.toUsersPokemon(), CurrentUser.getUsername(), pokemonBattle.getNumPokeballs());
                        Intent i = new Intent(getApplicationContext(), MainMenu.class);
                        i.putExtra("pokemon1", pokemon1);
                        i.putExtra("numPokeballs", battleResponse.getNumPokeballs());
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
    };

    View.OnClickListener clickParty = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), BattleParty.class);
            i.putExtra("selectedPokemonIndex", selectedPokemonIndex);
            startActivityForResult(i, Constant.REQUEST_CODE_BATTLE_PARTY);
        }
    };

    View.OnClickListener clickThrowPokeball = new View.OnClickListener() {
        public void onClick(View v) {
            Log.e("battle", "threw some pokeball");
            if (pokemonBattle.getNumPokeballs() > 0) {

                MoveResponse moveResponse = pokemonBattle.throwPokeball();
                numPokeballsText.setText(Integer.toString(pokemonBattle.getNumPokeballs()));

                if (moveResponse.isCaughtPokemon()) {
                    Toast.makeText(getApplicationContext(), "Caught a pokemon!!", Toast.LENGTH_LONG).show();
                    CatchResponse catchResponse = pokemonBattle.endBattleWithCatch();

                    Intent i = new Intent(getApplicationContext(), MainMenu.class);
                    i.putExtra("catchResponse", catchResponse);
                    i.putExtra("caughtPokemon", true);
                    startActivity(i);
                }
                else {
                    pokemon1 = moveResponse.getPokemon1();
                    pokemon2 = moveResponse.getPokemon2();

                    myPokemonName.setText(pokemon1.getName());
                    myPokemonHealth.setText("HP " + pokemon1.getHealth() + " / " + pokemon1.getMaxHealth()
                            + "\nMessages: " + moveResponse.getBattleMessages1().getAllMessages());
//                            + "\nMoveUsed: " + moveResponse.getBattleMessages1().getMoveUsed()
//                            + "\nStatusMessages: " + moveResponse.getBattleMessages1().getStatusMessages().toString()
//                            + "\nStatChanges: " + moveResponse.getBattleMessages1().getStatChanges()
//                            + "\nAilmentMessage: " + moveResponse.getBattleMessages1().getAilmentMessage());
                    enemyPokemonName.setText(pokemon2.getName());
                    enemyPokemonHealth.setText("HP " + pokemon2.getHealth() + " / " + pokemon2.getMaxHealth()
                            + "\nMessages: " + moveResponse.getBattleMessages2().getAllMessages());
//                            + "\nMoveUsed: " + moveResponse.getBattleMessages2().getMoveUsed()
//                            + "\nStatusMessages: " + moveResponse.getBattleMessages2().getStatusMessages().toString()
//                            + "\nStatChanges: " + moveResponse.getBattleMessages2().getStatChanges()
//                            + "\nAilmentMessage: " + moveResponse.getBattleMessages2().getAilmentMessage());

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
}
