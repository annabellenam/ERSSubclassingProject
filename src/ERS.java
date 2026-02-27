import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;

public class ERS extends CardGame {
    // Core game components
    Card selectedCard;
    int selectedCardRaiseAmount = 15;
    int numberofPeople = 0;
    // Game state
    Card lastPlayedCard;
    Card secondlastPlayedCard;
    boolean gameActive;
    char[] slapKeys = {'Q','P','Z',','} ;

    
    public ERS() {
        
    }
    

    protected boolean isValidPlay(Card card) {
        return true;
    }

    protected boolean checkPattern(Card card) {

        if (lastPlayedCard == null)
            return false;

        // Double
        if (card.value.equals(lastPlayedCard.value))
            return true;

        // Sandwich
        if (secondlastPlayedCard != null &&
                card.value.equals(secondlastPlayedCard.value))
            return true;

        // Marriage (Q + K)
        if ((card.value.equals("Q") && lastPlayedCard.value.equals("K")) ||
                (card.value.equals("K") && lastPlayedCard.value.equals("Q")))
            return true;

        return false;
    }

    public void drawCard(Hand hand) {
        if (deck != null && !deck.isEmpty()) {
            hand.addCard(deck.remove(0));
        } else if (totalPile != null && totalPile.size() > 1) {
            
            lastPlayedCard = totalPile.remove(totalPile.size() - 1);
            deck.addAll(totalPile);
            totalPile.clear();
            totalPile.add(lastPlayedCard);
            Collections.shuffle(deck);

            if (!deck.isEmpty()) {
                hand.addCard(deck.remove(0));
            }
        }
    }


    public boolean playCard(Card card, Hand hand) {
        
        hand.removeCard(card);
        card.setTurned(false);
        
        totalPile.add(card);
        secondlastPlayedCard = lastPlayedCard;
        lastPlayedCard = card;
        
        switchTurns();
        return true;
    }

    public Card getLastPlayedCard() {
        return lastPlayedCard;
    }

    public int getDeckSize() {
        return deck != null ? deck.size() : 0;
    }


    public void awardPileToPlayer(int playerIndex) {
        Hand h = handsofpeople.get(playerIndex);
        for (Card c : totalPile) {
            h.addCard(c);
        }
        totalPile.clear();
    }
    public void handleSlap(char key) {
        for (int i = 0; i < slapKeys.length; i++) {
            if (slapKeys[i] == key) {

                if (checkPattern(lastPlayedCard)) {
                    System.out.println("Player " + (i + 1) + " successful slap!");
                    awardPileToPlayer(i);
                } else {
                    System.out.println("Player " + (i + 1) + " false slap — draw penalty");
                    drawCard(handsofpeople.get(i));
                }
            }
        }
    }
}