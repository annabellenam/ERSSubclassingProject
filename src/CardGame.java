import java.util.ArrayList;
import java.util.Collections;

import processing.core.PApplet;

public class CardGame {
    ArrayList<Card> deck = new ArrayList<>();
    ArrayList<Card> totalPile = new ArrayList<>();
    Card selectedCard;
    int selectedCardRaiseAmount = 15;
    public ArrayList<Hand> handsofpeople = new ArrayList<>();

    Card lastPlayedCard;
    boolean gameActive;

    ClickableRectangle drawButton;
    int drawButtonX = 220;
    int drawButtonY = 540;
    int drawButtonWidth = 160;
    int drawButtonHeight = 40;

    public ClickableRectangle increaseButton;
    public ClickableRectangle decreaseButton;
    public ClickableRectangle confirmButton;
    public ClickableRectangle startButton;
    public ClickableRectangle gameManualButton;
    public ClickableRectangle exitButton;
    int numberOfPlayers;

    public CardGame() {
        increaseButton = new ClickableRectangle(370, 280, 50, 50);
        decreaseButton = new ClickableRectangle(180, 280, 50, 50);
        confirmButton  = new ClickableRectangle(200, 400, 200, 50);
        startButton    = new ClickableRectangle(175, 370, 250, 60);
        gameManualButton = new ClickableRectangle(175, 440, 250, 60);
        exitButton = new ClickableRectangle(175,500,250,60);
    }

    public void startScreen(PApplet app) {
        app.background(10, 55, 30);

        

        // circle
        app.noStroke();
        app.fill(255, 215, 0, 30);
        app.ellipse(300, 200, 350, 120);

        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.fill(255, 215, 0);
        app.textSize(80);
        app.text("ERS", 300, 180);

        app.fill(200, 170, 100);
        app.textSize(18);
        app.text("Egyptian Rat Screw", 300, 240);

        app.fill(180, 30, 30);
        app.stroke(220, 60, 60);
        app.strokeWeight(2);
        app.rect(startButton.x, startButton.y, startButton.width, startButton.height, 12);
        app.noStroke();
        app.fill(255);
        app.textSize(22);
        app.text("PLAY NOW", 300, 400);

        app.fill(180, 30, 30);
        app.stroke(220, 60, 60);
        app.strokeWeight(2);
        app.rect(gameManualButton.x, gameManualButton.y, gameManualButton.width, gameManualButton.height, 12);
        app.noStroke();
        app.fill(255);
        app.textSize(22);
        app.text("GAME MANUAL", 300, 470);

        

        app.fill(150, 200, 150);
        app.textSize(12);
        app.text("Slap keys: Q  P  Z  ,", 300, 560);
    }
    public void gameManual(PApplet app) {
        app.background(10, 55, 30);
        
        app.fill(200, 170, 100);
        app.textSize(18);
        app.text("GAMEMANUAL:", 300, 100);
    }


    public void drawSelectionScreen(PApplet app, int numberOfPlayers) {
        app.background(10, 55, 30);

        

        app.textAlign(PApplet.CENTER, PApplet.CENTER);

        app.fill(255, 215, 0);
        app.textSize(36);
        app.text("HOW MANY PLAYERS?", 300, 160);

        // player num
        app.fill(255, 255, 255, 20);
        app.rect(200, 220, 200, 80, 12);
        app.fill(255);
        app.textSize(48);
        app.text(numberOfPlayers, 300, 260);

        app.fill(180, 30, 30);
        app.stroke(220, 60, 60);
        app.strokeWeight(2);
        app.rect(increaseButton.x, increaseButton.y, increaseButton.width, increaseButton.height, 10);
        app.rect(decreaseButton.x, decreaseButton.y, decreaseButton.width, decreaseButton.height, 10);

        app.noStroke();
        app.fill(255);
        app.textSize(30);
        app.text("+", 395, 305);
        app.text("−", 205, 305);

        // Player labels
        app.fill(180, 220, 180);
        app.textSize(13);
        if (numberOfPlayers == 2) {
            app.text("Player 1: Q to slap", 300, 360);
            app.text("Player 2: P to slap", 300, 380);
        } else if (numberOfPlayers == 3) {
            app.text("P1: Q  |  P2: P  |  P3: Z", 300, 365);
        } else {
            app.text("P1: Q  |  P2: P  |  P3: Z  |  P4: ,", 300, 365);
        }

        app.fill(30, 130, 60);
        app.stroke(50, 200, 90);
        app.strokeWeight(2);
        app.rect(confirmButton.x, confirmButton.y, confirmButton.width, confirmButton.height, 12);
        app.noStroke();
        app.fill(255);
        app.textSize(20);
        app.text("START GAME", 300, 425);
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

        handsofpeople.get(0).playerTurn = true;
        dealCards(52 / numberOfPlayers);
    }

    public void drawGame(PApplet app) {
        app.background(10, 55, 30);


        app.noStroke();
        app.fill(0, 0, 0, 60);
        app.ellipse(300, 270, 200, 180);

        if (lastPlayedCard != null) {
            lastPlayedCard.setPosition(260, 210, 80, 120);
            lastPlayedCard.draw(app);
        }

        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.fill(255, 215, 0);
        app.textSize(13);
        app.text("PILE: " + totalPile.size(), 300, 350);

        for (Hand h : handsofpeople) {
            h.draw(app);
        }

        int cur = getCurrentPlayer();
        app.noStroke();
        app.fill(255, 215, 0);
        app.textSize(14);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text("▶ PLAYER " + (cur + 1) + "'S TURN", 300, 30);

        String[] positions = {"BOTTOM", "LEFT", "TOP", "RIGHT"};
        int[][] labelPos = {{300, 510}, {60, 270}, {300, 60}, {540, 270}};
        for (int i = 0; i < handsofpeople.size(); i++) {
            Hand h = handsofpeople.get(i);
            app.fill(h.playerTurn ? app.color(255, 215, 0) : app.color(200, 200, 200));
            app.textSize(12);
            app.text("P" + (i + 1) + ": " + h.getSize() + " cards", labelPos[i][0], labelPos[i][1]);
        }

        app.fill(180, 30, 30);
        app.stroke(220, 60, 60);
        app.strokeWeight(2);
        app.rect(drawButton.x, drawButton.y, drawButton.width, drawButton.height, 10);
        app.noStroke();
        app.fill(255);
        app.textSize(16);
        app.text("PLACE CARD  [SPACE]", drawButton.x + drawButton.width / 2, drawButton.y + drawButton.height / 2);
    }

    protected void createDeck() {
        String[] suits   = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] values  = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
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
                    Card c = deck.remove(0);
                    c.setTurned(true);
                    person.addCard(c);
                }
            }
        }
        positionPlayers();
    }

    protected void positionPlayers() {
        for (int i = 0; i < handsofpeople.size(); i++) {
            Hand person = handsofpeople.get(i);
            if (i == 0) person.positionCards(220, 450, 50, 75, 0);  // bottom
            if (i == 1) person.positionCards(10,  160, 50, 75, 0);  // left
            if (i == 2) person.positionCards(220, 30,  50, 75, 0);  // top
            if (i == 3) person.positionCards(490, 160, 50, 75, 0);  // right
        }
    }

    protected boolean isValidPlay(Card card) { return true; }

    public void handleDrawButtonClick(int mouseX, int mouseY) {
        if (drawButton != null && drawButton.isClicked(mouseX, mouseY)) {
            playCard(handsofpeople.get(getCurrentPlayer()));
        }
    }

    public void switchTurns() {
        int current = getCurrentPlayer();
        handsofpeople.get(current).playerTurn = false;
        int next = (current + 1) % handsofpeople.size();
        int attempts = 0;
        while (handsofpeople.get(next).getSize() == 0 && attempts < handsofpeople.size()) {
            next = (next + 1) % handsofpeople.size();
            attempts++;
        }
        handsofpeople.get(next).playerTurn = true;
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
            if (!deck.isEmpty()) hand.addCard(deck.remove(0));
        }
    }

    public boolean playCard(Hand hand) {
        if (hand == null || hand.getCards().isEmpty()) return false;

        lastPlayedCard = hand.getCard(hand.getCards().size() - 1);
        hand.removeCard(lastPlayedCard);
        lastPlayedCard.setTurned(false);

        totalPile.add(lastPlayedCard);
        positionPlayers();
        switchTurns();
        return true;
    }

    public int getCurrentPlayer() {
        for (int i = 0; i < handsofpeople.size(); i++) {
            if (handsofpeople.get(i).playerTurn) return i;
        }
        return 0;
    }

    public Card getLastPlayedCard() { return lastPlayedCard; }

    public void drawChoices(PApplet app) {}
}