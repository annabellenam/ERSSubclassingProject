import processing.core.PApplet;
import processing.core.PFont;

public class App extends PApplet {

    ERS cardGame;
    boolean selectingPlayers = false;
    int     numberOfPlayers  = 2;
    boolean startingGame     = true;
    boolean gameManual = false;

    public static void main(String[] args) {
        PApplet.main("App");
    }

    @Override
    public void settings() {
        size(600, 600);
    }

    @Override
    public void setup() {
        PFont font = createFont("Arial", 32, true);
        textFont(font);

        cardGame = new ERS();
        surface.setTitle("Egyptian Rat Screw");
    }

    @Override
    public void draw() {
        if (startingGame) {
            cardGame.startScreen(this);
            return;
        }
        if (gameManual) {
            cardGame.gameManual(this);
            return;
        }
        if (selectingPlayers) {
            cardGame.drawSelectionScreen(this, numberOfPlayers);
            return;
        }
        cardGame.drawGame(this);
    }

    @Override
    public void mousePressed() {
        if (startingGame) {
            if (cardGame.startButton.isClicked(mouseX, mouseY)) {
                selectingPlayers = true;
                startingGame     = false;
            }
            if (cardGame.gameManualButton.isClicked(mouseX, mouseY)) {
                gameManual = true;
                startingGame = false;
            }
            return;
        }
        if (gameManual) {
            if (cardGame.exitButton.isClicked(mouseX,mouseY)) {
                startingGame = true;
                gameManual = false;
            }
        }
        if (selectingPlayers) {
            if (cardGame.increaseButton.isClicked(mouseX, mouseY) && numberOfPlayers < 4) numberOfPlayers++;
            if (cardGame.decreaseButton.isClicked(mouseX, mouseY) && numberOfPlayers > 2) numberOfPlayers--;
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
        if (!startingGame && !selectingPlayers) {
            if (key == ' ') {
                int cur = cardGame.getCurrentPlayer();
                cardGame.playCard(cardGame.handsofpeople.get(cur));
                return;
            }
            cardGame.handleSlap(key);
        }
    }
}