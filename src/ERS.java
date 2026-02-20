import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;

public class ERS extends CardGame {
    // Core game components
    ArrayList<Card> deck = new ArrayList<>();

    ArrayList<Card> discardPile = new ArrayList<>();
    Card selectedCard;
    int selectedCardRaiseAmount = 15;
    int numberofPeople=0;
    // Game state
    boolean playerOneTurn = true;
    Card lastPlayedCard;
    boolean gameActive;

    // UI
    ClickableRectangle drawButton;
    int drawButtonX = 250;
    int drawButtonY = 400;
    int drawButtonWidth = 100;
    int drawButtonHeight = 35;

    
    public ERS() {
        SelectNumberofPlayers();
    }
    protected void SelectNumberofPlayers() {
        
        ClickableRectangle IncreaseButton = new ClickableRectangle();
        IncreaseButton.x = 200;
        IncreaseButton.y = 300;
        IncreaseButton.width = 50;
        IncreaseButton.height = 50;
        if (IncreaseButton.isClicked(mouseX,mouseY)) {
            numberofPeople ++;
        }
        ClickableRectangle DecreaseButton = new ClickableRectangle();
        DecreaseButton.x = 270;
        DecreaseButton.y = 300;
        DecreaseButton.width = 50;
        DecreaseButton.height = 50;
        if (DecreaseButton.isClicked(mouseX,mouseY)) {
            numberofPeople --;
        }
        ClickableRectangle ConfirmButton = new ClickableRectangle();
        ConfirmButton.x = 200;
        ConfirmButton.y = 400;
        ConfirmButton.width =50;
        ConfirmButton.height = 50;
        if (ConfirmButton.isClicked(mouseX,mouseY)) {
            if(numberofPeople<= 2) {
                System.out.println("Please select more than 2 players.");
                break;
            }
            dealCards(52/numberofPeople);
            initializeGame();
        }
        
    }
    ArrayList<Hand> handsofpeople = new ArrayList<>();
    protected void initializeGame() {
        // Initialize draw button
        drawButton = new ClickableRectangle();
        drawButton.x = drawButtonX;
        drawButton.y = drawButtonY;
        drawButton.width = drawButtonWidth;
        drawButton.height = drawButtonHeight;

        // Initialize decks and hands
        deck = new ArrayList<>();
        discardPile = new ArrayList<>();
        for(int i =0; i<numberofPeople; i++) {
            String name = "Player" + i + "Hand";
            Hand name = new Hand();
            handsofpeople.add(name);

        }
        gameActive = true;

        createDeck();
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
        Collections.shuffle(deck);
        for (int i = 0; i < numCards; i++) {
            for (Hand person : handsofpeople) {
                person.addCard(deck.remove(0));
                Card card = deck.remove(0);
            card.setTurned(true);
            playerTwoHand.addCard(card);
            //fix
            }
        }

        for (Hand person : handsofpeople) {
            if (handsofpeople.indexOf(person) == 0) {
                person.positionCards(300, 450, 80, 120, 0);
            }
            if (handsofpeople.indexOf(person) == 1) {
                person.positionCards(300, 50, 80, 120, 0);
            }
            if (handsofpeople.indexOf(person) == 2) {
                person.positionCards(25, 50, 80, 120, 0);
            }
            if (handsofpeople.indexOf(person) == 3) {
                person.positionCards(500, 50, 80, 120, 0);
            }
        }
    }

    protected boolean isValidPlay(Card card) {
        return true;
    }

    public void drawCard(Hand hand) {
        if (deck != null && !deck.isEmpty()) {
            hand.addCard(deck.remove(0));
        } else if (discardPile != null && discardPile.size() > 1) {
            // Reshuffle discard pile into deck if deck is empty
            lastPlayedCard = discardPile.remove(discardPile.size() - 1);
            deck.addAll(discardPile);
            discardPile.clear();
            discardPile.add(lastPlayedCard);
            Collections.shuffle(deck);

            if (!deck.isEmpty()) {
                hand.addCard(deck.remove(0));
            }
        }
    }

    public void handleDrawButtonClick(int mouseX, int mouseY) {
        if (drawButton.isClicked(mouseX, mouseY) && playerOneTurn) {
            drawCard(playerOneHand);
            // Switch turns after drawing
            switchTurns();
        }
    }

    public boolean playCard(Card card, Hand hand) {
        // Check if card is valid to play
        if (!isValidPlay(card)) {
            System.out.println("Invalid play: " + card.value + " of " + card.suit);
            return false;
        }
        // Remove card from hand
        hand.removeCard(card);
        card.setTurned(false);
        // Add to discard pile
        discardPile.add(card);
        lastPlayedCard = card;
        // Switch turns
        switchTurns();
        return true;
    }

    public void switchTurns() {
        playerOneTurn = !playerOneTurn;
        playerOneHand.positionCards(50, 450, 80, 120, 20);
        playerTwoHand.positionCards(50, 50, 80, 120, 20);
    }

    public String getCurrentPlayer() {
        return playerOneTurn ? "Player One" : "Player Two";
    }

    public Card getLastPlayedCard() {
        return lastPlayedCard;
    }

    public int getDeckSize() {
        return deck != null ? deck.size() : 0;
    }

    public Hand getPlayerOneHand() {
        return playerOneHand;
    }

    public Hand getPlayerTwoHand() {
        return playerTwoHand;
    }

    public void handleComputerTurn() {
        drawCard(playerTwoHand);
        switchTurns();
    }

    public void handleCardClick(int mouseX, int mouseY) {
        if (!playerOneTurn) {
            return;
        }
        Card clickedCard = getClickedCard(mouseX, mouseY);
        if (clickedCard == null) {
            return;
        }
        // this is for the first time
        if (selectedCard == null) {
            selectedCard = clickedCard;
            selectedCard.setSelected(true, selectedCardRaiseAmount);
            return;
        }

        if (selectedCard == clickedCard) {
            System.out.println("playing card: " + selectedCard.value + " of " + selectedCard.suit);
            if (playCard(selectedCard, playerOneHand)) {
                selectedCard.setSelected(false, selectedCardRaiseAmount);
                selectedCard = null;
            }
            return;
        }
        // change selection
        selectedCard.setSelected(false, selectedCardRaiseAmount);
        selectedCard = clickedCard;
        selectedCard.setSelected(true, selectedCardRaiseAmount);
    }

    // return the card that is clicked!
    public Card getClickedCard(int mouseX, int mouseY) {
        for (int i = playerOneHand.getSize() - 1; i >= 0; i--) {
            Card card = playerOneHand.getCard(i);
            if (card != null && card.isClicked(mouseX, mouseY)) {
                return card;
            }
        }
        return null;
    }

    public void drawChoices(PApplet app) {
        // this method is available for overriding
        // if you want to draw additional things (like Uno's wild color choices)
    }
}
