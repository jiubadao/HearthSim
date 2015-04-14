package com.hearthsim.event;

import com.hearthsim.card.Card;
import com.hearthsim.model.BoardModel;
import com.hearthsim.model.PlayerSide;

public class HandFilter implements HandFilterInterface {
    protected boolean includeEnemyHand() {
        return false;
    }

    protected boolean includeOwnHand() {
        return false;
    }

    protected boolean excludeSource() {
        return true;
    }

    @Override
    public boolean targetMatches(PlayerSide originSide, Card origin, PlayerSide targetSide, Card targetCard, BoardModel board) {
        if (this.excludeSource() && origin == targetCard) { // need reference check to avoid duplicates
            return false;
        }

        if (!this.includeEnemyHand() && originSide != targetSide) {
            return false;
        }

        if (!this.includeOwnHand() && originSide == targetSide) {
            return false;
        }

        return true;
    }

    public static final HandFilter OWN = new HandFilter() {
        @Override
        protected boolean includeOwnHand() {
            return true;
        }
    };
}
