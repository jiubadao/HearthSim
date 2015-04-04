package com.hearthsim.test;

import com.hearthsim.card.Card;
import com.hearthsim.card.Deck;
import com.hearthsim.card.minion.Hero;
import com.hearthsim.card.minion.concrete.*;
import com.hearthsim.card.spellcard.concrete.*;
import com.hearthsim.exception.HSException;
import com.hearthsim.model.BoardModel;
import com.hearthsim.model.PlayerSide;
import com.hearthsim.util.factory.BreadthBoardStateFactory;
import com.hearthsim.util.tree.HearthTreeNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestLethal {

    private Deck deck0;
    private Deck deck1;

    private BreadthBoardStateFactory factory;
    private BoardModel startingBoard;
    private HearthTreeNode root;

    private Hero ownHero;
    private Hero enemyHero;

    @Before
    public void setup() {
        Card cards[] = new Card[10];
        for (int index = 0; index < 10; ++index) {
            cards[index] = new TheCoin();
        }

        deck0 = new Deck(cards);
        deck1 = deck0.deepCopy();

        factory = new BreadthBoardStateFactory(this.deck0, this.deck1);
        startingBoard = new BoardModel();
        startingBoard.getCurrentPlayer().addMana((byte)10);
        startingBoard.getCurrentPlayer().addMaxMana((byte)10);

        root = new HearthTreeNode(startingBoard);
        this.ownHero = startingBoard.getCurrentPlayer().getHero();
        this.enemyHero = startingBoard.getWaitingPlayer().getHero();
    }

    @Test
    public void testMinionAttack() throws HSException {
        this.enemyHero.setHealth((byte)4);
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new ChillwindYeti());

        this.factory.addChildLayers(this.root, 1);
        assertTrue(this.hasLethalAtDepth(this.root, 1));
    }

    @Test
    public void testMinionAttackMultiple() throws HSException {
        this.enemyHero.setHealth((byte)8);
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new ChillwindYeti());
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new ChillwindYeti());

        this.factory.addChildLayers(this.root, 2);
        assertTrue(this.hasLethalAtDepth(this.root, 2));
    }

    @Test
    public void testSpell() throws HSException {
        this.enemyHero.setHealth((byte)1);
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new HolySmite());

        this.factory.addChildLayers(this.root, 1);
        assertTrue(this.hasLethalAtDepth(this.root, 1));
    }

    @Test
    public void testSpellMultiple() throws HSException {
        this.enemyHero.setHealth((byte)3);
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new HolySmite());
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new HolySmite());

        this.factory.addChildLayers(this.root, 2);
        assertTrue(this.hasLethalAtDepth(this.root, 2));
    }

    @Test
    public void testKoboldThenSmite() throws HSException {
        this.enemyHero.setHealth((byte)3);
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new KoboldGeomancer());
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new HolySmite());

        this.factory.addChildLayers(this.root, 2);
        assertTrue(this.hasLethalAtDepth(this.root, 2));
    }

    @Test
    public void testInnervateSmiteSmite() throws HSException {
        this.startingBoard.getCurrentPlayer().setMana((byte)0);
        this.enemyHero.setHealth((byte)3);
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new HolySmite());
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new Innervate());
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new HolySmite());

        this.factory.addChildLayers(this.root, 3);
        assertTrue(this.hasLethalAtDepth(this.root, 3));
    }

    @Test
    public void testMinionAttackCharge() throws HSException {
        this.enemyHero.setHealth((byte)6);
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new ChillwindYeti());
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new BluegillWarrior());

        this.factory.addChildLayers(this.root, 3);
        assertTrue(this.hasLethalAtDepth(this.root, 3));
    }

    @Test
    public void testMinionWithBuff() throws HSException {
        this.enemyHero.setHealth((byte)3);
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new RiverCrocolisk());
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new Boar());
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new TimberWolf());

        this.factory.addChildLayers(this.root, 3);
        assertTrue(this.hasLethalAtDepth(this.root, 3));
    }

    @Test
    public void testRemoveTauntAndAttack() throws HSException {
        this.removeTauntTest(new Assassinate());
    }

    @Test
    public void testSilenceTauntAndAttack() throws HSException {
        this.removeTauntTest(new Silence());
    }

    @Test
    public void testSapTauntAndAttack() throws HSException {
        this.removeTauntTest(new Sap());
    }

    @Test
    public void testMindControlTauntAndAttack() throws HSException {
        this.removeTauntTest(new MindControl());
    }

    @Test
    public void testKillTauntWithSmallerAttack() throws HSException {
        this.enemyHero.setHealth((byte)6);
        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new GoldshireFootman());

        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new BoulderfistOgre());
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new ChillwindYeti());

        this.factory.addChildLayers(this.root, 2);
        assertTrue(this.hasLethalAtDepth(this.root, 2));
    }

    @Test
    public void testComplicatedTauntMath() throws HSException {
        this.enemyHero.setHealth((byte)6);
        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new GoldshireFootman());
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new BluegillWarrior()); // +2 = 2

        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new SenjinShieldmasta());
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new RiverCrocolisk()); // +1 = 3
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new Frostbolt()); // +1 = 4

        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new BoulderfistOgre()); // +1 = 5

        this.factory.addChildLayers(this.root, 5);
        assertTrue(this.hasLethalAtDepth(this.root, 5));
        assertTrue(this.hasLethalAtDepth(this.root, 5));
    }

    @Test
    public void testReallyComplicatedTauntMath() throws HSException {
        this.enemyHero.setHealth((byte)6);
        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new GoldshireFootman());
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new BluegillWarrior()); // +2 = 2

        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new SenjinShieldmasta());
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new RiverCrocolisk()); // +1 = 3
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new RiverCrocolisk()); // +1 = 4
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new SilverHandRecruit()); // +1 = 5

        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new GoldshireFootman());
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new RiverCrocolisk()); // +1 = 6

        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new GoldshireFootman());
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new RiverCrocolisk()); // +1 = 7

        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new BoulderfistOgre()); // +1 = 8

        this.factory.addChildLayers(this.root, 8);
        assertTrue(this.hasLethalAtDepth(this.root, 8));
    }

    @Test
    public void testBattlecries() throws HSException {
        this.enemyHero.setHealth((byte)6);
        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new SenjinShieldmasta());

        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new DarkIronDwarf()); // +2 = 2
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new ShatteredSunCleric()); // +2 = 4
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new ChillwindYeti()); // +1 = 5
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new ChillwindYeti()); // +1 = 6

        this.factory.addChildLayers(this.root, 6);
        assertTrue(this.hasLethalAtDepth(this.root, 6));
    }

    @Test
    public void testCardRng() throws HSException {
        this.enemyHero.setHealth((byte)4);

        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new AnimalCompanion()); // +3 (play, rng, attack) = 3

        this.factory.addChildLayers(this.root, 5);
        assertTrue(this.hasLethalAtDepth(this.root, 3));
    }

    @Test
    public void testCardRngComplicated() throws HSException {
        this.enemyHero.setHealth((byte)4);
        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new SenjinShieldmasta());

        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new AnimalCompanion()); // +3 = 3
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new ShatteredSunCleric()); // +2 = 5
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new ChillwindYeti()); // +1 = 6

        this.factory.addChildLayers(this.root, 6);
        assertTrue(this.hasLethalAtDepth(this.root, 6));
    }

    // http://www.reddit.com/r/hearthstone/comments/316kld/just_had_an_interesting_lethal_puzzle_fancy/
    @Test
    public void testPuzzleMindControlCharge() throws HSException {
        this.enemyHero.setHealth((byte) 17);
        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new KnifeJuggler());
        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new ArcaneGolem());

        this.startingBoard.modelForSide(PlayerSide.CURRENT_PLAYER).setMana((byte)9);
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new ChillwindYeti()); // attack; +1 = 1
        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new SylvanasWindrunner()); // attack; +1 = 2

        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new TheBlackKnight());
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new BigGameHunter()); // play second, target Syl, attack with golem; +3 = 7
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new SavageRoar()); // play first and attack with hero; +2 = 4
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, new AncientOfLore());

        this.factory.addChildLayers(this.root, 7);
        assertTrue(this.hasLethalAtDepth(this.root, 7));
    }

    private void removeTauntTest(Card removal) throws HSException {
        this.enemyHero.setHealth((byte)4);
        this.startingBoard.placeMinion(PlayerSide.WAITING_PLAYER, new SenjinShieldmasta());

        this.startingBoard.placeMinion(PlayerSide.CURRENT_PLAYER, new ChillwindYeti());
        this.startingBoard.placeCardHand(PlayerSide.CURRENT_PLAYER, removal);

        this.factory.addChildLayers(this.root, 2);
        assertTrue(this.hasLethalAtDepth(this.root, 2));
    }

    private boolean hasLethalAtDepth(HearthTreeNode node, int depth) {
        if (depth < 0) {
            return false;
        }
        if (depth == 0) {
            return node.data_.isLethalState();
        }
        if (node.isLeaf()) {
            return false;
        }
        for (HearthTreeNode child : node.getChildren()) {
            if (this.hasLethalAtDepth(child, depth - 1)) {
                return true;
            }
        }

        return false;
    }
}
