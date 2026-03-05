import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;

public class ERS extends CardGame {

    // Slap KEYS
    char[] slapKeys = {'q', 'p', 'z', ','};

    boolean faceCardActive = false;
    int faceCardChances   = 0;    
    int challengingPlayer = -1;  
    int respondingPlayer  = -1;   

    String slapMessage     = "";
    int    slapMessageTimer = 0;
    int    slapPlayerIndex  = -1;

    int winnerIndex = -1;

    public ERS() {}
    protected boolean checkPattern() {
        int sz = totalPile.size();
        if (sz < 2) return false;

        Card top    = totalPile.get(sz - 1);
        Card second = totalPile.get(sz - 2);
//DPD type format sandwhih
        if (top.value.equals(second.value)) return true;

        if (sz >= 3) {
            Card third = totalPile.get(sz - 3);
            if (top.value.equals(third.value)) return true;
        }

        // marriage
        if ((top.value.equals("Q") && second.value.equals("K")) ||
            (top.value.equals("K") && second.value.equals("Q"))) return true;

        // Top or second is J
        if (top.value.equals("J") || second.value.equals("J")) return true;

        return false;
    }

    private boolean isFaceCard(Card c) {
        return c.value.equals("J") || c.value.equals("Q") ||
               c.value.equals("K") || c.value.equals("A");
    }

    private int chancesForFaceCard(Card c) {
        switch (c.value) {
            case "J": return 1;
            case "Q": return 2;
            case "K": return 3;
            case "A": return 4;
            default:  return 0;
        }
    }

    @Override
    public boolean playCard(Hand hand) {
        if (hand == null || hand.getCards().isEmpty()) return false;

        Card card = hand.getCard(hand.getCards().size() - 1);
        hand.removeCard(card);
        card.setTurned(false);
        totalPile.add(card);
        lastPlayedCard = card;

        positionPlayers();

        if (faceCardActive) {
            faceCardChances--;

            if (isFaceCard(card)) {
                challengingPlayer = respondingPlayer;
                respondingPlayer  = (challengingPlayer + 1) % handsofpeople.size();
                while (handsofpeople.get(respondingPlayer).getSize() == 0 && respondingPlayer != challengingPlayer) {
                    respondingPlayer = (respondingPlayer + 1) % handsofpeople.size();
                }
                faceCardChances = chancesForFaceCard(card);
                setTurnTo(respondingPlayer);
            } else if (faceCardChances <= 0) {
                awardPileToPlayer(challengingPlayer);
                faceCardActive = false;
                setTurnTo(challengingPlayer);
            } else {
                setTurnTo(respondingPlayer);
            }
        } else {
            if (isFaceCard(card)) {
                faceCardActive    = true;
                challengingPlayer = getCurrentPlayer();
                faceCardChances   = chancesForFaceCard(card);
                int next = (challengingPlayer + 1) % handsofpeople.size();
                while (handsofpeople.get(next).getSize() == 0 && next != challengingPlayer) {
                    next = (next + 1) % handsofpeople.size();
                }
                respondingPlayer = next;
                setTurnTo(respondingPlayer);
            } else {
                switchTurns();
            }
        }

        return true;
    }

    private void setTurnTo(int playerIndex) {
        for (Hand h : handsofpeople) h.playerTurn = false;
        handsofpeople.get(playerIndex).playerTurn = true;
    }

    public void handleSlap(char key) {
        if (totalPile.isEmpty()) return;

        for (int i = 0; i < handsofpeople.size(); i++) {
            if (i >= slapKeys.length) break;

            if (slapKeys[i] == Character.toLowerCase(key)) {
                if (checkPattern()) {
                    slapMessage     = "[GOOD] PLAYER " + (i + 1) + " SLAPS!";
                    slapPlayerIndex = i;
                    slapMessageTimer = 90;
                    awardPileToPlayer(i);
                    faceCardActive = false;
                    faceCardChances = 0;
                    setTurnTo(i);
                } else {
                    slapMessage     = "[MISS] P" + (i + 1) + " FALSE SLAP -1";
                    slapPlayerIndex = i;
                    slapMessageTimer = 90;
                    Hand h = handsofpeople.get(i);
                    if (!h.getCards().isEmpty()) {
                        Card penalty = h.getCard(0);
                        h.removeCard(penalty);
                        penalty.setTurned(false);
                        totalPile.add(0, penalty);
                    }
                }
                positionPlayers();
                checkWin();
                return;
            }
        }
    }

    public void awardPileToPlayer(int playerIndex) {
        Hand h = handsofpeople.get(playerIndex);
        ArrayList<Card> shuffled = new ArrayList<>(totalPile);
        Collections.shuffle(shuffled);
        for (Card c : shuffled) {
            c.setTurned(true);
            h.addCard(c);
        }
        totalPile.clear();
        lastPlayedCard = null;
        positionPlayers();
    }

    public boolean checkWin() { //fix 
        int playersWithCards = 0;
        int lastWithCards    = -1;
        for (int i = 0; i < handsofpeople.size(); i++) {
            if (handsofpeople.get(i).getSize() > 0) {
                playersWithCards++;
                lastWithCards = i;
            }
        }
        if (playersWithCards == 1 && totalPile.isEmpty()) {
            winnerIndex = lastWithCards;
            return true;
        }
        return false;
    }

    @Override
    public void drawGame(PApplet app) {
        if (winnerIndex >= 0) {
            drawWinScreen(app);
            return;
        }

        super.drawGame(app);

        if (faceCardActive) {
            app.fill(220, 160, 0, 200);
            app.noStroke();
            app.rect(100, 370, 400, 30, 6);
            app.fill(20);
            app.textAlign(PApplet.CENTER, PApplet.CENTER);
            app.textSize(13);
            app.text(">> FACE CARD CHALLENGE -- P" + (respondingPlayer + 1) +
                      " has " + faceCardChances + " chance(s)", 300, 385);
        }

        if (slapMessageTimer > 0) {
            slapMessageTimer--;
            float alpha = PApplet.map(slapMessageTimer, 0, 90, 0, 255);
            boolean success = slapMessage.startsWith("[GOOD]");
            app.fill(success ? app.color(30, 200, 80, alpha) : app.color(220, 50, 50, alpha));
            app.noStroke();
            app.rect(80, 165, 440, 50, 10);
            app.fill(255, alpha);
            app.textAlign(PApplet.CENTER, PApplet.CENTER);
            app.textSize(20);
            app.text(slapMessage, 300, 190);
        }

        app.fill(150, 200, 150, 160);
        app.textSize(11);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        String keys = "";
        for (int i = 0; i < handsofpeople.size(); i++) {
            keys += "P" + (i + 1) + ":" + Character.toUpperCase(slapKeys[i]) + "  ";
        }
        app.text("SLAP → " + keys.trim(), 300, 590);

        checkWin();
    }
    @Override
    public void gameManual(PApplet app) {
        app.background(10, 55, 30);
        
        app.fill(200, 170, 100);
        app.textSize(40);
        app.text("GAME MANUAL:", 300, 100);
        app.textSize(18);
        app.text("ERS is a fast-paced, 2-4 player game that tests reflexes. \nThe goal of the game is to win all cards by being the first \nto 'slap' the deck when a pattern is spotted.",300,180);
        app.textSize(28);
        app.text("ALL PATTERNS : ",300,240);
        //---
        app.textSize(18);
        app.text("Marriage - King + Queen combination.",300,270);
        app.text("Sandwhich - A card that is sandwhiched in between to cards of \nthe same value.",300,310);
        app.text("Double - Two consecutive cards of the same rank.",300,350);
        //--
        app.textSize(28);
        app.text("FACE CARDS : ",300,375);
        app.textSize(18);
        app.text("When a face card or Ace is played, the next player \nhas limited chances to lay down another \nface card or Ace to avoid losing the pile to the previous player.",300,430);
        
        app.fill(180, 30, 30);
        app.stroke(220, 60, 60);
        app.strokeWeight(2);
        app.rect(exitButton.x, exitButton.y, exitButton.width, exitButton.height, 12);
        app.noStroke();
        app.fill(255);
        app.textSize(22);
        app.text("EXIT", 300, 530);


    }
    private void drawWinScreen(PApplet app) {
        app.background(10, 55, 30);
        app.stroke(255, 255, 255, 12);
        for (int i = 0; i < app.width; i += 30) app.line(i, 0, i, app.height);
        for (int i = 0; i < app.height; i += 30) app.line(0, i, app.width, i);
        app.noStroke();

        app.fill(255, 215, 0, 40);
        app.ellipse(300, 280, 400, 300);

        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.fill(255, 215, 0);
        app.textSize(24);
        app.text("WINNER!", 300, 200);

        app.fill(255);
        app.textSize(52);
        app.text("PLAYER " + (winnerIndex + 1), 300, 270);

        app.fill(180, 220, 180);
        app.textSize(16);
        app.text("wins all " + handsofpeople.get(winnerIndex).getSize() + " cards!", 300, 330);

        app.fill(200, 200, 200);
        app.textSize(13);
        app.text("Restart the sketch to play again", 300, 400);
    }
}