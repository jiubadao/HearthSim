package com.hearthsim.test.card;

import com.hearthsim.card.Card;
import com.hearthsim.card.Deck;
import com.hearthsim.card.minion.Minion;
import com.hearthsim.card.spellcard.concrete.HuntersMark;
import com.hearthsim.card.spellcard.concrete.TheCoin;
import com.hearthsim.exception.HSException;
import com.hearthsim.model.BoardModel;
import com.hearthsim.model.PlayerModel;
import com.hearthsim.model.PlayerSide;
import com.hearthsim.util.tree.HearthTreeNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestHuntersMark {


    private HearthTreeNode board;
    private Deck deck;
    private static final byte mana = 2;
    private static final byte attack0 = 5;
    private static final byte health0 = 3;
    private static final byte health1 = 7;

    @Before
    public void setup() throws HSException {
        board = new HearthTreeNode(new BoardModel());

        Minion minion0_0 = new Minion("" + 0, mana, attack0, health0, attack0, health0, health0);
        Minion minion0_1 = new Minion("" + 0, mana, attack0, (byte)(health1 - 1), attack0, health1, health1);
        Minion minion1_0 = new Minion("" + 0, mana, attack0, health0, attack0, health0, health0);
        Minion minion1_1 = new Minion("" + 0, mana, attack0, (byte)(health1 - 1), attack0, health1, health1);

        board.data_.placeMinion(PlayerSide.CURRENT_PLAYER, minion0_0);
        board.data_.placeMinion(PlayerSide.CURRENT_PLAYER, minion0_1);

        board.data_.placeMinion(PlayerSide.WAITING_PLAYER, minion1_0);
        board.data_.placeMinion(PlayerSide.WAITING_PLAYER, minion1_1);

        Card cards[] = new Card[10];
        for (int index = 0; index < 10; ++index) {
            cards[index] = new TheCoin();
        }

        deck = new Deck(cards);

        Card fb = new HuntersMark();
        board.data_.getCurrentPlayer().placeCardHand(fb);

        board.data_.getCurrentPlayer().setMana((byte)7);
        board.data_.getWaitingPlayer().setMana((byte)4);

        board.data_.getCurrentPlayer().setMaxMana((byte)7);
        board.data_.getWaitingPlayer().setMaxMana((byte)4);

    }

    @Test
    public void test2() throws HSException {
        board.data_.getCurrentPlayer().getHero().setHealth((byte)23);
        Card theCard = board.data_.getCurrentPlayer().getHand().get(0);
        HearthTreeNode ret = theCard.useOn(PlayerSide.WAITING_PLAYER, 1, board, deck, null);

        assertFalse(ret == null);
        PlayerModel currentPlayer = board.data_.modelForSide(PlayerSide.CURRENT_PLAYER);
        PlayerModel waitingPlayer = board.data_.modelForSide(PlayerSide.WAITING_PLAYER);

        assertEquals(board.data_.getCurrentPlayer().getHand().size(), 0);
        assertEquals(currentPlayer.getNumMinions(), 2);
        assertEquals(waitingPlayer.getNumMinions(), 2);
        assertEquals(board.data_.getCurrentPlayer().getMana(), 7);
        assertEquals(board.data_.getWaitingPlayer().getMana(), 4);
        assertEquals(board.data_.getCurrentPlayer().getHero().getHealth(), 23);
        assertEquals(board.data_.getWaitingPlayer().getHero().getHealth(), 30);
        assertEquals(currentPlayer.getMinions().get(0).getHealth(), health0);
        assertEquals(currentPlayer.getMinions().get(1).getHealth(), health1 - 1);
        assertEquals(waitingPlayer.getMinions().get(0).getHealth(), 1);
        assertEquals(waitingPlayer.getMinions().get(1).getHealth(), health1 - 1);

        assertEquals(currentPlayer.getMinions().get(0).getTotalAttack(), attack0);
        assertEquals(currentPlayer.getMinions().get(1).getTotalAttack(), attack0);
        assertEquals(waitingPlayer.getMinions().get(0).getTotalAttack(), attack0);
        assertEquals(waitingPlayer.getMinions().get(1).getTotalAttack(), attack0);
    }

    @Test
    public void test3() throws HSException {
        board.data_.getCurrentPlayer().getHero().setHealth((byte)23);
        Card theCard = board.data_.getCurrentPlayer().getHand().get(0);
        HearthTreeNode ret = theCard.useOn(PlayerSide.CURRENT_PLAYER, 1, board, deck, null);

        assertFalse(ret == null);
        PlayerModel currentPlayer = board.data_.modelForSide(PlayerSide.CURRENT_PLAYER);
        PlayerModel waitingPlayer = board.data_.modelForSide(PlayerSide.WAITING_PLAYER);

        assertEquals(board.data_.getCurrentPlayer().getHand().size(), 0);
        assertEquals(currentPlayer.getNumMinions(), 2);
        assertEquals(waitingPlayer.getNumMinions(), 2);
        assertEquals(board.data_.getCurrentPlayer().getMana(), 7);
        assertEquals(board.data_.getWaitingPlayer().getMana(), 4);
        assertEquals(board.data_.getCurrentPlayer().getHero().getHealth(), 23);
        assertEquals(board.data_.getWaitingPlayer().getHero().getHealth(), 30);
        assertEquals(currentPlayer.getMinions().get(0).getHealth(), 1);
        assertEquals(currentPlayer.getMinions().get(1).getHealth(), health1 - 1);
        assertEquals(waitingPlayer.getMinions().get(0).getHealth(), health0);
        assertEquals(waitingPlayer.getMinions().get(1).getHealth(), health1 - 1);

        assertEquals(currentPlayer.getMinions().get(0).getTotalAttack(), attack0);
        assertEquals(currentPlayer.getMinions().get(1).getTotalAttack(), attack0);
        assertEquals(waitingPlayer.getMinions().get(0).getTotalAttack(), attack0);
        assertEquals(waitingPlayer.getMinions().get(1).getTotalAttack(), attack0);
    }
}
