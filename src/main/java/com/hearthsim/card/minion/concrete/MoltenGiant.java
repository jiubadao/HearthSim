package com.hearthsim.card.minion.concrete;

import com.hearthsim.card.minion.Minion;
import com.hearthsim.model.BoardModel;
import com.hearthsim.model.PlayerSide;

public class MoltenGiant extends Minion {
	
	private static final boolean HERO_TARGETABLE = true;
	private static final boolean SUMMONED = false;
	private static final boolean TRANSFORMED = false;
	private static final byte SPELL_DAMAGE = 0;
	
	public MoltenGiant() {
        super();
        spellDamage_ = SPELL_DAMAGE;
        heroTargetable_ = HERO_TARGETABLE;
        summoned_ = SUMMONED;
        transformed_ = TRANSFORMED;
	}
	
	@Override
	public byte getManaCost(PlayerSide side, BoardModel board) {
		byte manaCost = (byte)(baseManaCost - side.getPlayer(board).getHero().getMaxHealth() + side.getPlayer(board).getHero().getHealth());
		if (manaCost < 0)
			manaCost = 0;
		return manaCost;
	}

}