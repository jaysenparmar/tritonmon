package com.tritonmon.model;

import com.tritonmon.global.CurrentUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
//@AllArgsConstructor(suppressConstructorProperties = true)
public class BattleMessages {

//    public static final String YOUR = "your";
    public static final String MISSED = "the attack missed";
    public static final String CRIT = "it was a critical hit";
    public static final String NOT_EFFECTIVE = "it was not very effective";
    public static final String SUPER_EFFECTIVE = "it was super effective";
    public static final String NO_EFFECT = "it didn't do anything..";

    // only one that is the prefix
    public static final String CAUGHT_POKEMON = CurrentUser.getUsername() + " caught";
    public static final String THREW_POKBEALL = CurrentUser.getUsername() + " threw a pokeball";
    public static final String DID_NOT_CATCH_POKEMON = " broke free";
    public static final String HIT_SELF = "it hit itself in its confusion";

//    public static final String SELF = "self's";
//    public static final String OPPONENT = "opponent's";
    public static final String ROSE = "rose";
    public static final String FELL = "fell";

    public static final String ARE_PARALYZED = "is paralyzed";
    public static final String ARE_ASLEEP = "is asleep";
    public static final String ARE_FROZEN = "is frozen";
    public static final String ARE_BURNED = "is burned";
    public static final String ARE_POISONED = "is poisoned";
    public static final String ARE_CONFUSED = "is confused";

    public static final String AFFLICTED_PARALYSIS = "was paralyzed";
    public static final String AFFLICTED_SLEEP = "fell asleep";
    public static final String AFFLICTED_FREEZE = "was frozen";
    public static final String AFFLICTED_BURN = "was burned";
    public static final String AFFLICTED_POISON = "was poisoned";
    public static final String AFFLICTED_CONFUSION = "was confused";

    public static final String WOKEUP = "woke up";
    public static final String UNFROZE = "thawed out";

    public static final String HURT_BY_POISON = "was hurt by the poison";
    public static final String HURT_BY_BURN = "was hurt by the burn";

    public static final String EMPTY_STAT_CHANGES = "";
    public static final String EMPTY_AILMENT = "";

    private String moveUsed;
    private String prelimAilment;

    // also covered missed message
    private String effectiveness;
    private String crit;

    // also covers hitting yourself
    private String damageDone;
    private List<String> statChanges;
    private String afflictedAilment;
    private String continueAilment;

    public String threwPokeball;
    public String caughtPokemon;

    private List<String> allMessages;

    public BattleMessages() {
        this.moveUsed = "";
        this.prelimAilment = "";
        this.effectiveness = "";
        this.crit = "";
        this.damageDone = "";
        this.statChanges = new ArrayList<String>();
        this.afflictedAilment = "";
        this.continueAilment = "";
        this.threwPokeball = "";
        this.caughtPokemon = "";

        allMessages = new ArrayList<String>();
    }

    // should only be called when caughtpokemon is true
    public BattleMessages(String threwPokeball) {
        this.moveUsed = "";
        this.prelimAilment = "";
        this.effectiveness = "";
        this.crit = "";
        this.damageDone = "";
        this.statChanges = new ArrayList<String>();
        this.afflictedAilment = "";
        this.continueAilment = "";
        this.threwPokeball = threwPokeball;
        this.caughtPokemon = "";

        allMessages = Arrays.asList(caughtPokemon);
    }

//    public BattleMessages(
//            String moveUsed,
//            String prelimAilment,
//            String effectiveness,
//            String crit,
//            String damageDone,
//            List<String> statChanges,
//            String afflictedAilment,
//            String continueAilment,
//            String caughtPokemon) {
//        this.moveUsed = moveUsed;
//        this.prelimAilment = prelimAilment;
//        this.effectiveness = effectiveness;
//        this.crit = crit;
//        this.damageDone = damageDone;
//        this.statChanges = statChanges;
//        this.afflictedAilment = afflictedAilment;
//        this.continueAilment = continueAilment;
//        this.caughtPokemon = caughtPokemon;
//
//        populateAllMessages();
//    }

    public void addMoveUsed(String moveUsed) {
//        this.moveUsed = moveUsed.isEmpty() ? "" : pokemonName + " used " + moveUsed;
        this.moveUsed = moveUsed;
    }

    public void pruneMoveUsed(String pokemonName) {
        moveUsed = moveUsed.isEmpty() ? "" : pokemonName + " used " + moveUsed;
    }

    public void addPrelimAilment(String prelimAilment) {
        this.prelimAilment = prelimAilment;
    }

    public void removePrelimAilment() {
        this.prelimAilment = "";
    }

    public void prunePrelimAilment(String pokemonName) {
        prelimAilment = prelimAilment.isEmpty() ? "" : pokemonName + " " + prelimAilment;
    }

    public void addEffectiveness(String effectiveness) {
        this.effectiveness = effectiveness;
    }

    public void addCrit(String crit) {
        this.crit = crit.isEmpty() ? "" : crit;
    }

    public void addDamageDone(String damageDone, boolean self) {
//        this.damageDone = self ? damageDone : "did " + damageDone + " damage";
        this.damageDone = self ? damageDone : "";
    }

    public void addStatChanges(List<String> statChanges) {
        this.statChanges = statChanges.isEmpty() ? new ArrayList<String>() : statChanges;
    }

    public void pruneStatChanges(String pokemon1Name, String pokemon2Name) {
        // not sure if this is valid.. assumes good stat changes always happen to self
        // and a stat changes dont
        List<String> newStatChanges = new ArrayList<String>();
        String tmp;

        if (!statChanges.isEmpty()) {
            for (String ele : statChanges) {
                if (ele.contains(BattleMessages.ROSE)) {
                    tmp = pokemon1Name + "'s " + ele;
                } else {
                    tmp = pokemon2Name + "'s " + ele;
                }
                newStatChanges.add(tmp);
            }
            statChanges = newStatChanges;
        }
    }

    public void addAfflictedAilment(String afflictedAilment) {
        this.afflictedAilment = afflictedAilment;
    }

    public void pruneAfflictedAilment(String pokemonName) {
        afflictedAilment = afflictedAilment.isEmpty() ? "" : pokemonName + " " + afflictedAilment;
    }

    public void addContinueAilment(String continueAilment) {
        this.continueAilment = continueAilment.isEmpty() ? "" : continueAilment;
    }

    public void removeContinueAilment() {
        this.continueAilment = "";
    }

    public void pruneContinueAilment(String pokemonName) {
        continueAilment = continueAilment.isEmpty() ? "" : pokemonName + " " + continueAilment;
    }

    public void addThrewPokeball(String threwPokeball) {
        this.threwPokeball = threwPokeball;
    }

    public void addCaughtPokemon(String caughtPokemon) {
        this.caughtPokemon = caughtPokemon;
    }

    public void pruneCaughtPokemon(String pokemonName, boolean caught) {
        if (!caught) {
            caughtPokemon = caughtPokemon.isEmpty() ? "" : pokemonName + " " + caughtPokemon;
        } else {
            caughtPokemon = caughtPokemon.isEmpty() ? "" : caughtPokemon + " " + pokemonName;
        }
    }



    public void populateAllMessages() {
        allMessages = new ArrayList<String>();
        allMessages.add(moveUsed);
        allMessages.add(prelimAilment);
        allMessages.add(effectiveness);
        allMessages.add(crit);
        allMessages.add(damageDone);
        allMessages.addAll(statChanges);
        allMessages.add(afflictedAilment);
        allMessages.add(continueAilment);
        allMessages.add(threwPokeball);
        allMessages.add(caughtPokemon);
        allMessages.removeAll(Collections.singleton(""));
    }

    // TODO
    public void pruneAll(String pokemon1Name, String pokemon2Name, boolean caught) {
        pruneMoveUsed(pokemon1Name);
        prunePrelimAilment(pokemon1Name);
        pruneStatChanges(pokemon1Name, pokemon2Name);
        pruneAfflictedAilment(pokemon2Name);
        pruneContinueAilment(pokemon1Name);
        pruneCaughtPokemon(pokemon2Name, caught);
    }
}
