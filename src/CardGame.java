import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;

public class CardGame {
    // Core game components
    ArrayList<Card> deck = new ArrayList<>();
    ArrayList<Card> totalPile = new ArrayList<>();
    Card selectedCard;
    int selectedCardRaiseAmount = 15;
    public ArrayList<Hand> handsofpeople = new ArrayList<>();
    // Game state
    Card lastPlayedCard;
    boolean gameActive;

    // UI
    ClickableRectangle drawButton;
    int drawButtonX = 250;
    int drawButtonY = 400;
    int drawButtonWidth = 100;
    int drawButtonHeight = 35;

    public ClickableRectangle increaseButton;
    public ClickableRectangle decreaseButton;
    public ClickableRectangle confirmButton;
    int numberOfPlayers;

    public CardGame() {
        increaseButton = new ClickableRectangle(325, 250, 50, 50);
        decreaseButton = new ClickableRectangle(225, 250, 50, 50);
        confirmButton = new ClickableRectangle(250, 350, 100, 50);
        // dealCards(6);
    }

    public void drawSelectionScreen(PApplet app, int numberOfPlayers) {
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textSize(24);

        app.fill(0);
        app.text("Select Number of Players", 300, 150);
        app.text(numberOfPlayers, 300, 220);

        app.fill(200);
        increaseButton.draw(app);
        decreaseButton.draw(app);
        confirmButton.draw(app);

        app.fill(0);
        app.text("+", 350, 275);
        app.text("-", 250, 275);
        app.text("Start", 300, 375);
    }

    public void InitializeGame(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        createDeck();
        Collections.shuffle(deck);

        handsofpeople.clear();
        for (int i = 0; i < numberOfPlayers; i++) {
            Hand h = new Hand();
            h.name = "Player " + (i + 1);
            handsofpeople.add(h);
        }
        drawButton = new ClickableRectangle(drawButtonX, drawButtonY, drawButtonWidth, drawButtonHeight);
        
        int turn = 0;
        while (!deck.isEmpty()) {
            handsofpeople.get(turn).addCard(deck.remove(0));
            turn = (turn + 1) % numberOfPlayers;
        }

        handsofpeople.get(0).playerTurn = true;
    }

    public void drawGame(PApplet app) {
        for (Hand h : handsofpeople) {
            h.draw(app);
        }

        if (lastPlayedCard != null) {
            lastPlayedCard.setPosition(app.width / 2 - 40, app.height / 2 - 60, 80, 120);
            lastPlayedCard.draw(app);
        }

        app.fill(0);
        app.textSize(16);
        app.text("Current Player: " + (getCurrentPlayer() + 2), app.width / 2, 20);
    }

    protected void createDeck() {
        // Create a standard deck of cards (for simplicity, using numbers and suits)
        String[] suits = { "Hearts", "Diamonds", "Clubs", "Spades" };
        String[] values = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };
        for (String suit : suits) {
            for (String value : values) {
                deck.add(new Card(value, suit));
            }
        }
    }

    protected void dealCards(int numCards) {

        for (int i = 0; i < numCards; i++) {
            for (Hand person : handsofpeople) {
                if (!deck.isEmpty()) {
                    person.addCard(deck.remove(0));
                }
            }
        }

        // position players
        for (int i = 0; i < handsofpeople.size(); i++) {

            Hand person = handsofpeople.get(i);

            if (i == 0)
                person.positionCards(300, 450, 80, 120, 20);

            if (i == 1)
                person.positionCards(300, 50, 80, 120, 20);

            if (i == 2)
                person.positionCards(25, 200, 80, 120, 20);

            if (i == 3)
                person.positionCards(500, 200, 80, 120, 20);
        }
    }

    protected boolean isValidPlay(Card card) {
        return true;
    }

    public void drawCard(Hand hand) {
        if (deck != null && !deck.isEmpty()) {
            hand.addCard(deck.remove(0));
        } else if (totalPile != null && totalPile.size() > 1) {
            
            lastPlayedCard = totalPile.remove(totalPile.size() - 1);
            deck.addAll(totalPile);
            totalPile.clear();
            totalPile.add(lastPlayedCard); // fix thi s
            Collections.shuffle(deck);

            if (!deck.isEmpty()) {
                hand.addCard(deck.remove(0));
            }
        }
    }

    public void handleDrawButtonClick(int mouseX, int mouseY) {
        if (drawButton.isClicked(mouseX, mouseY)) {
            drawCard(handsofpeople.get(getCurrentPlayer()));
            
            switchTurns();
        }
    }

    public void switchTurns() {

        int current = getCurrentPlayer();
        handsofpeople.get(current).playerTurn = false;
        int next = (current + 1) % handsofpeople.size();
        handsofpeople.get(next).playerTurn = true;
    }

    public boolean playCard(Card card, Hand hand) {
        
        if (!isValidPlay(card)) {
            System.out.println("Invalid play: " + card.value + " of " + card.suit);
            return false;
        }
        
        hand.removeCard(card);
        card.setTurned(false);
        
        totalPile.add(card);
        lastPlayedCard = card;
        
        switchTurns();
        return true;
    }

    public int getCurrentPlayer() {
        for (int i = 0; i < handsofpeople.size(); i++) {
            if (handsofpeople.get(i).playerTurn) {
                return i;
            }
        }
        return 0; 
    }

    public Card getLastPlayedCard() {
        return lastPlayedCard;
    }

    public int getDeckSize() {
        return deck != null ? deck.size() : 0;
    }

    // return the card that is clicked!

    public void drawChoices(PApplet app) {
        // this method is available for overriding
        // if you want to draw additional things (like Uno's wild color choices)
    }
}
