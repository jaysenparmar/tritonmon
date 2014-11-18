package com.tritonmon.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tritonmon.Battle.BattleRequest;
import com.tritonmon.Battle.BattleResponse;
import com.tritonmon.Battle.BattlingPokemon;
import com.tritonmon.Battle.MoveResponse;
import com.tritonmon.Battle.PokeballHandler;
import com.tritonmon.Battle.PokeballResponse;
import com.tritonmon.Battle.PokemonBattle;
import com.tritonmon.global.Constant;
import com.tritonmon.global.CurrentUser;
import com.tritonmon.global.ImageUtil;


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

    PokemonBattle pokemonBattle;
    Integer move1;
    Integer move2;
    Integer move3;
    Integer move4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

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

        move1Button.setOnClickListener(clickMove1);
        move2Button.setOnClickListener(clickMove2);
        move3Button.setOnClickListener(clickMove3);
        move4Button.setOnClickListener(clickMove4);

        throwPokeballButton = (Button) findViewById(R.id.throwPokeballButton);
        runButton = (Button) findViewById(R.id.runButton);
        throwPokeballButton.setOnClickListener(clickThrowPokeball);

        BattlingPokemon pokemon1 = new BattlingPokemon(CurrentUser.getParty().getPokemon(0));
        // init 2nd pokemon (id, level, wild) cuz screw lvl 5 pidgeys
        BattlingPokemon pokemon2 = new BattlingPokemon(16, 5, true);

        pokemonBattle = new PokemonBattle(new BattleRequest(pokemon1, pokemon2));

        otherPokemonName.setText(pokemon2.getName() + " (Lvl " + pokemon2.getLevel() + ")");
        otherPokemonHealth.setText("HP " + pokemon2.getHealth() + "/max");
        otherPokemonImage.setImageResource(ImageUtil.getPokemonFrontImageResource(this, pokemon2.getPokemonId()));

        myPokemonName.setText(pokemon1.getName() + " (Lvl " + pokemon1.getLevel() + ")");
        myPokemonHealth.setText("HP " + pokemon1.getHealth() + "/max");
        myPokemonXP.setText("XP " + (int) (pokemon1.getXp() - Math.pow(pokemon1.getLevel(), 3)) + "/" + (int) (Math.pow(pokemon1.getLevel() + 1, 3) - Math.pow(pokemon1.getLevel(), 3)));
        myPokemonImage.setImageResource(ImageUtil.getPokemonBackImageResource(this, pokemon1.getPokemonId()));


        // TODO: update users pokemon per move (else switching wont work properly)

        move1 = CurrentUser.getParty().getPokemon(0).getMoves().get(0);
        String move1Name = move1 == null ? "None" : Constant.movesData.get(move1).getName() + ", " + CurrentUser.getParty().getPokemon(0).getPps().get(0)+"/"+Constant.movesData.get(move1).getPp();
        move1Button.setText(move1Name);
        move2 = CurrentUser.getParty().getPokemon(0).getMoves().get(1);
        String move2Name = move2 == null ? "None" : Constant.movesData.get(move2).getName() + ", " + CurrentUser.getParty().getPokemon(0).getPps().get(1)+"/"+Constant.movesData.get(move2).getPp();
        move2Button.setText(move2Name);
        move3 = CurrentUser.getParty().getPokemon(0).getMoves().get(2);
        String move3Name = move3 == null ? "None" : Constant.movesData.get(move3).getName() + ", " + CurrentUser.getParty().getPokemon(0).getPps().get(2)+"/"+Constant.movesData.get(move3).getPp();
        move3Button.setText(move3Name);
        move4 = CurrentUser.getParty().getPokemon(0).getMoves().get(3);
        String move4Name = move4 == null ? "None" : Constant.movesData.get(move4).getName() + ", " + CurrentUser.getParty().getPokemon(0).getPps().get(0)+"/"+Constant.movesData.get(move4).getPp();
        move4Button.setText(move4Name);

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

    View.OnClickListener clickMove1 = new View.OnClickListener() {
        public void onClick(View v) {
            if (move1 != null) {
                Log.e("Battle", "scratched some pidgey");
                MoveResponse moveResponse = pokemonBattle.doMove(move1);
                String humanMoveUsed;
                String AIMoveUsed;
                if (moveResponse.isHumanMovedFirst()) {
                    humanMoveUsed = moveResponse.getBattleMessages1().getMoveUsed();
                    AIMoveUsed = moveResponse.getBattleMessages2().getMoveUsed();
                } else {
                    humanMoveUsed = moveResponse.getBattleMessages2().getMoveUsed();
                    AIMoveUsed = moveResponse.getBattleMessages1().getMoveUsed();
                }
                myPokemonHealth.setText("HP " + Integer.toString(moveResponse.getPokemon1().getHealth()) + "\nMoveUsed: " + humanMoveUsed);
                otherPokemonHealth.setText("HP " + Integer.toString(moveResponse.getPokemon2().getHealth()) + "\nMoveUsed: " + AIMoveUsed);
                move1Button.setText(Constant.movesData.get(move1).getName() + ", " + moveResponse.getPokemon1().getPps().get(0) + "/" + Constant.movesData.get(move1).getPp());

            }
        }
    };

    View.OnClickListener clickMove2 = new View.OnClickListener() {
        public void onClick(View v) {
            if (move2 != null) {
                Log.e("Battle", "growled at some pidgey");
                MoveResponse moveResponse = pokemonBattle.doMove(move2);
                String humanMoveUsed;
                String AIMoveUsed;
                if (moveResponse.isHumanMovedFirst()) {
                    humanMoveUsed = moveResponse.getBattleMessages1().getMoveUsed();
                    AIMoveUsed = moveResponse.getBattleMessages2().getMoveUsed();
                } else {
                    humanMoveUsed = moveResponse.getBattleMessages2().getMoveUsed();
                    AIMoveUsed = moveResponse.getBattleMessages1().getMoveUsed();
                }
                myPokemonHealth.setText("HP " + Integer.toString(moveResponse.getPokemon1().getHealth()) + "\nMoveUsed: " + humanMoveUsed);
                otherPokemonHealth.setText("HP " + Integer.toString(moveResponse.getPokemon2().getHealth()) + "\nMoveUsed: " + AIMoveUsed);
                move2Button.setText(Constant.movesData.get(move2).getName() + ", " + moveResponse.getPokemon1().getPps().get(1) + "/" + Constant.movesData.get(move2).getPp());
            }
        }
    };

    View.OnClickListener clickMove3 = new View.OnClickListener() {
        public void onClick(View v) {
            if (move3 != null) {
                Log.e("Battle", "did the other move on some pidgey");
                MoveResponse moveResponse = pokemonBattle.doMove(move3);
                String humanMoveUsed;
                String AIMoveUsed;
                if (moveResponse.isHumanMovedFirst()) {
                    humanMoveUsed = moveResponse.getBattleMessages1().getMoveUsed();
                    AIMoveUsed = moveResponse.getBattleMessages2().getMoveUsed();
                } else {
                    humanMoveUsed = moveResponse.getBattleMessages2().getMoveUsed();
                    AIMoveUsed = moveResponse.getBattleMessages1().getMoveUsed();
                }
                myPokemonHealth.setText("HP " + Integer.toString(moveResponse.getPokemon1().getHealth()) + "\nMoveUsed: " + humanMoveUsed);
                otherPokemonHealth.setText("HP " + Integer.toString(moveResponse.getPokemon2().getHealth()) + "\nMoveUsed: " + AIMoveUsed);
                move3Button.setText(Constant.movesData.get(move3).getName() + ", " + moveResponse.getPokemon1().getPps().get(2) + "/" + Constant.movesData.get(move3).getPp());
            }
        }
    };

    View.OnClickListener clickMove4 = new View.OnClickListener() {
        public void onClick(View v) {
            if (move4 != null) {
                Log.e("Battle", "did the other move on some pidgey");
                MoveResponse moveResponse = pokemonBattle.doMove(move4);
                String humanMoveUsed;
                String AIMoveUsed;
                if (moveResponse.isHumanMovedFirst()) {
                    humanMoveUsed = moveResponse.getBattleMessages1().getMoveUsed();
                    AIMoveUsed = moveResponse.getBattleMessages2().getMoveUsed();
                } else {
                    humanMoveUsed = moveResponse.getBattleMessages2().getMoveUsed();
                    AIMoveUsed = moveResponse.getBattleMessages1().getMoveUsed();
                }
                myPokemonHealth.setText("HP " + Integer.toString(moveResponse.getPokemon1().getHealth()) + "\nMoveUsed: " + humanMoveUsed);
                otherPokemonHealth.setText("HP " + Integer.toString(moveResponse.getPokemon2().getHealth()) + "\nMoveUsed: " + AIMoveUsed);
                move4Button.setText(Constant.movesData.get(move4).getName() + ", " + moveResponse.getPokemon1().getPps().get(3) + "/" + Constant.movesData.get(move4).getPp());
            }
        }
    };

    View.OnClickListener clickThrowPokeball = new View.OnClickListener() {
        public void onClick(View v) {
            Log.e("Battle", "threw some pokeball");
            MoveResponse moveResponse = pokemonBattle.throwPokeball();

            if (moveResponse.isCaughtPokemon()) {
                otherPokemonHealth.setText("caught some pokemon");
                BattleResponse battleResponse = pokemonBattle.endBattle(true);
                // put pokemon in your party
            } else {
                otherPokemonHealth.setText("throw denied");
            }

        }
    };
}
