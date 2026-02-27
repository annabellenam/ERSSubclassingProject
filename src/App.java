//WHAT TO DO: ~~ FIX FORMATTING!!! CHECK ERRORS ACTUAL GAME IS NOT SHOWING UP + GAME TITLE 

import processing.core.PApplet;

public class App extends PApplet {

    CardGame cardGame;
    boolean selectingPlayers = true;
    int numberOfPlayers = 2;

    public static void main(String[] args) {
        PApplet.main("App");
    }

    @Override
    public void settings() {
        size(600, 600);
    }

    @Override
    public void setup() {
        cardGame = new ERS(); 
    }

    @Override
    public void draw() {
        background(255);

        if (selectingPlayers) {
            cardGame.drawSelectionScreen(this, numberOfPlayers);
            return;
        }

        cardGame.drawGame(this);

        // draw draw button
        cardGame.drawButton.draw(this);
        fill(0);
        textAlign(CENTER, CENTER);
        text("Draw",
            cardGame.drawButton.x + cardGame.drawButton.width / 2,
            cardGame.drawButton.y + cardGame.drawButton.height / 2);
    }

    @Override
    public void mousePressed() {

        if (selectingPlayers) {

            if (cardGame.increaseButton.isClicked(mouseX, mouseY)) {
                if (numberOfPlayers < 4) numberOfPlayers++;
            }

            if (cardGame.decreaseButton.isClicked(mouseX, mouseY)) {
                if (numberOfPlayers > 2) numberOfPlayers--;
            }

            if (cardGame.confirmButton.isClicked(mouseX, mouseY)) {
                cardGame.InitializeGame(numberOfPlayers);
                selectingPlayers = false;
            }
            return;
        }

        cardGame.handleDrawButtonClick(mouseX, mouseY);
    }

    @Override
    public void keyPressed() {
        if (cardGame instanceof ERS) {
            ((ERS) cardGame).handleSlap(key);
        }
    }
}