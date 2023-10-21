package com.bol.mancala.entity.enumeration;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum PlayerNumber {
    ONE, TWO;

    public PlayerNumber next() {
        return this == ONE ? TWO : ONE;
    }

    public PlayerNumber oppositeSide() {
        return this == ONE ? TWO : ONE;
    }

    public Set<PlayerNumber> getOtherPlayers() {
        return Arrays.stream(PlayerNumber.values()).filter(playerNumber -> playerNumber != this).collect(Collectors.toSet());
    }
}
