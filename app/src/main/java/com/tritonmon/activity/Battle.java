package com.tritonmon.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tritonmon.battle.requestresponse.BattleResponse;
import com.tritonmon.battle.BattleUtil;
import com.tritonmon.model.BattlingPokemon;
import com.tritonmon.battle.requestresponse.MoveResponse;
import com.tritonmon.battle.PokemonBattle;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;
import com.tritonmon.staticmodel.Pokemon;
import com.tritonmon.staticmodel.Stats;


public class Battle extends Activity {

    TextView otherPokemonName;
    TextView otherPokemonHealth;
    ImageView otherPokemonImage;

    TextView myPokemonName;
    TextView myPokemonHealth;
    TextView myPokemonXP;
    ImageView myPokemonImage;

    Button move1Button, move2Button, move3Button, move4Button;
    Button throwPokeballButton, runButton;

    BattlingPokemon pokemon1, pokemon2;
    PokemonBattle pokemonBattle;
    int pokemon1MaxHP, pokemon2MaxHP;
    Integer move1Id;
    Integer move2Id;
    Integer move3Id;
    Integer move4Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        pokemon1 = new BattlingPokemon(CurrentUser.getParty().getPokemon(0));
        pokemon2 = new BattlingPokemon(Pokemon.getPokemonId("pidgey"), 3, true);

        pokemonBattle = new PokemonBattle(pokemon1, pokemon2);

        pokemon1MaxHP = BattleUtil.getMaxStat(Stats.HP, pokemon1.getPokemonId(), pokemon1.getLevel());
        pokemon2MaxHP = BattleUtil.getMaxStat(Stats.HP, pokemon2.getPokemonId(), pokemon2.getLevel());

        move1Id = pokemon1.getMoves().get(0);
        move2Id = pokemon1.getMoves().get(1);
        move3Id = pokemon1.getMoves().get(2);
        move4Id = pokemon1.getMoves().get(3);

        otherPokemonName = (TextView) findViewById(R.id.opponentPokemonName);
        otherPokemonHealth = (TextView) findViewById(R.id.opponentPokemonHealth);
        otherPokemonImage = (ImageView) findViewById(R.id.opponentPokemonImage);

        myPokemonName = (TextView) findViewById(R.id.myPokemonName);
        myPokemonHealth = (TextView) findViewById(R.id.myPokemonHealth);
        myPokemonXP = (TextView) findViewById(R.id.myPokemonXP);
        myPokemonImage = (ImageView) findViewById(R.id.myPokemonImage);

        move1Button = (Button) findViewById(R.id.move1Button);
        move2Button = (Button) findViewById(R.id.move2Button);
        move3Button = (Button) findViewById(R.id.move3Button);
        move4Button = (Button) findViewById(R.id.move4Button);

        setMoveOnClickListener(move1Button, move1Id);
        setMoveOnClickListener(move2Button, move2Id);
        setMoveOnClickListener(move3Button, move3Id);
        setMoveOnClickListener(move4Button, move4Id);

        // TODO: update users pokemon per move (else switching wont work properly)
        setMoveButtonLabel(move1Button, 0, move1Id);
        setMoveButtonLabel(move2Button, 1, move2Id);
        setMoveButtonLabel(move3Button, 2, move3Id);
        setMoveButtonLabel(move4Button, 3, move4Id);

        throwPokeballButton = (Button) findViewById(R.id.throwPokeballButton);
        runButton = (Button) findViewById(R.id.runButton);
        throwPokeballButton.setOnClickListener(clickThrowPokeball);

        myPokemonName.setText(pokemon1.getName() + " (Lvl " + pokemon1.getLevel() + ")");
        myPokemonHealth.setText("HP " + pokemon1.getHealth() + " / " + pokemon1MaxHP);
        myPokemonXP.setText("XP " + pokemon1.getCurrentXPBar()+ " / " + pokemon1.getTotalXPBar());
        myPokemonImage.setImageResource(ImageUtil.getPokemonBackImageResource(this, pokemon1.getPokemonId()));

        otherPokemonName.setText(pokemon2.getName() + " (Lvl " + pokemon2.getLevel() + ")");
        otherPokemonHealth.setText("HP " + pokemon2.getHealth() + " / " + pokemon2MaxHP);
        otherPokemonImage.setImageResource(ImageUtil.getPokemonFrontImageResource(this, pokemon2.getPokemonId()));
    }

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

    private void setMoveOnClickListener(final Button button, final Integer moveId){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moveId != null) {
                    MoveResponse moveResponse = pokemonBattle.doMove(moveId);

                    pokemon1 = moveResponse.getPokemon1();
                    pokemon2 = moveResponse.getPokemon2();

                    myPokemonName.setText(pokemon1.getName());
                    myPokemonHealth.setText("HP " + pokemon1.getHealth() + " / " + pokemon1MaxHP
                            + "\nMoveUsed: " + moveResponse.getBattleMessages1().getMoveUsed()
                            + "\nStatusMessages: " + moveResponse.getBattleMessages1().getStatusMessages().toString()
                            + "\nStatChanges: " + moveResponse.getBattleMessages1().getStatChanges()
                            + "\nAilmentMessage: " + moveResponse.getBattleMessages1().getAilmentMessage());
                    otherPokemonName.setText(pokemon2.getName());
                    otherPokemonHealth.setText("HP " + pokemon2.getHealth() + " / " + pokemon2MaxHP
                            + "\nMoveUsed: " + moveResponse.getBattleMessages2().getMoveUsed()
                            + "\nStatusMessages: " + moveResponse.getBattleMessages2().getStatusMessages().toString()
                            + "\nStatChanges: " + moveResponse.getBattleMessages2().getStatChanges()
                            + "\nAilmentMessage: " + moveResponse.getBattleMessages2().getAilmentMessage());
                    button.setText(Constant.movesData.get(moveId).getName() + " (" + moveResponse.getPokemon1().getPps().get(0) + "/" + Constant.movesData.get(move1Id).getPp() + ")");

                    if (pokemon2.getHealth() <= 0) {
                        Toast.makeText(getApplicationContext(), "Player won battle!", Toast.LENGTH_LONG).show();
                        BattleResponse battleResponse = pokemonBattle.endBattle();
                        pokemon1 = battleResponse.getPokemon1();
                        Intent i = new Intent(getApplicationContext(), MainMenu.class);
                        i.putExtra("pokemon1", pokemon1);
                        startActivity(i);

                    }
                    else if (pokemon1.getHealth() <= 0) {
                        Toast.makeText(getApplicationContext(), "Opponent won battle!", Toast.LENGTH_LONG).show();
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
            button.setEnabled(true);
        }
    };

    View.OnClickListener clickThrowPokeball = new View.OnClickListener() {
        public void onClick(View v) {
            Log.e("battle", "threw some pokeball");
            MoveResponse moveResponse = pokemonBattle.throwPokeball();

            if (moveResponse.isCaughtPokemon()) {
                otherPokemonHealth.setText("caught some pokemon");
//                BattleResponse battleResponse = pokemonBattle.endBattle();
                // put pokemon in your party
            } else {
                otherPokemonHealth.setText("throw denied");
            }

        }
    };
}
