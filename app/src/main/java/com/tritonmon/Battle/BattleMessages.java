package com.tritonmon.Battle;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public class BattleMessages {

    public static final String YOUR = "your";
    public static final String MISSED = "missed";
    public static final String CRIT = "crit";
    public static final String NOT_EFFECTIVE = "not effective";
    public static final String SUPER_EFFECTIVE = "super effective";
    public static final String CAUGHT_POKEMON = "caught pokemon";
    public static final String DID_NOT_CATCH_POKEMON = "did not catch pokemon";
    public static final String HIT_SELF = "hit self";

    public static final String SELF = "self's";
    public static final String OPPONENT = "opponent's";
    public static final String ROSE = "rose";
    public static final String FELL = "fell";

    public static final String PARALYZED = "paralyzed";
    public static final String ASLEEP = "asleep";
    public static final String FROZEN = "frozen";
    public static final String BURNED = "burned";
    public static final String POISONED = "poisoned";
    public static final String CONFUSED = "confused";

    public static final String UNFROZE = "unfroze";

    public static final String EMPTY_STAT_CHANGES = "";
    public static final String EMPTY_AILMENT = "";

    private List<String> statusMessages;
    private String statChanges;
    private String moveUsed;
    private String ailmentMessage;
}
