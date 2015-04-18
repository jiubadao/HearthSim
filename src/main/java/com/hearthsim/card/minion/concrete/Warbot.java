package com.hearthsim.card.minion.concrete;

import com.hearthsim.card.minion.MinionWithEnrage;

public class Warbot extends MinionWithEnrage {

    public Warbot() {
        super();
    }

    @Override
    public void enrage() {
        attack_ = (byte)(attack_ + 1);
    }

    @Override
    public void pacify() {
        attack_ = (byte)(attack_ - 1);
    }

}
