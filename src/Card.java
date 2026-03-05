import processing.core.PImage;
import processing.core.PApplet;

public class Card extends ClickableRectangle {
    String value;
    String suit;
    PImage img;
    boolean turned = false;
    private int clickableWidth = 30;
    private boolean selected = false;
    private int baseY;
    private boolean hasBaseY = false;

    Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    Card(String value, String suit, PImage img) {
        this.value = value;
        this.suit = suit;
        this.img = img;
    }

    public boolean isRed() {
        return suit.equals("Hearts") || suit.equals("Diamonds");
    }

    public String getSuitSymbol() {
        // ASCII only - avoids font/Unicode rendering issues in Processing
        switch (suit) {
            case "Hearts":   return "H";
            case "Diamonds": return "D";
            case "Clubs":    return "C";
            case "Spades":   return "S";
            default:         return suit.substring(0, 1);
        }
    }

    public void setTurned(boolean turned) { this.turned = turned; }
    public void setClickableWidth(int width) { this.clickableWidth = width; }

    public void setSelected(boolean selected, int raiseAmount) {
        if (selected && !this.selected) {
            baseY = y;
            hasBaseY = true;
            y = baseY - raiseAmount;
        } else if (!selected && this.selected && hasBaseY) {
            y = baseY;
        }
        this.selected = selected;
    }

    public boolean isSelected() { return selected; }

    @Override
    public boolean isClicked(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + clickableWidth &&
               mouseY >= y && mouseY <= y + height;
    }

    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public void setSize(int width, int height) { this.width = width; this.height = height; }
    public void setPosition(int x, int y, int width, int height) {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    public void drawFront(PApplet sketch) {
        // Drop shadow
        sketch.noStroke();
        sketch.fill(0, 0, 0, 50);
        sketch.rect(x + 3, y + 3, width, height, 8);

        // Card face — warm white
        sketch.fill(255, 252, 245);
        sketch.stroke(180, 170, 160);
        sketch.strokeWeight(1);
        sketch.rect(x, y, width, height, 8);
        sketch.noStroke();

        // Text color: red for hearts/diamonds, dark for clubs/spades
        int col = isRed() ? sketch.color(190, 20, 20) : sketch.color(15, 15, 15);
        sketch.fill(col);

        String sym = getSuitSymbol(); // single ASCII letter H/D/C/S
        String display = value + sym; // e.g. "AH", "10D", "KS"

        // Top-left corner: value then suit letter stacked
        sketch.textAlign(PApplet.LEFT, PApplet.TOP);
        sketch.textSize(12);
        sketch.text(value, x + 4, y + 3);
        sketch.textSize(10);
        sketch.text(sym, x + 4, y + 17);

        // Bottom-right corner (mirror)
        sketch.textAlign(PApplet.RIGHT, PApplet.BOTTOM);
        sketch.textSize(12);
        sketch.text(value, x + width - 4, y + height - 3);
        sketch.textSize(10);
        sketch.text(sym, x + width - 4, y + height - 17);

        // Center: big value
        sketch.textAlign(PApplet.CENTER, PApplet.CENTER);
        sketch.textSize(width > 55 ? 22 : 14);
        sketch.text(value, x + width / 2, y + height / 2 - 5);

        // Center: suit letter below value
        sketch.textSize(width > 55 ? 13 : 10);
        sketch.text(sym, x + width / 2, y + height / 2 + (width > 55 ? 14 : 10));
    }

    public void drawBack(PApplet sketch) {
        sketch.noStroke();
        sketch.fill(0, 0, 0, 40);
        sketch.rect(x + 3, y + 3, width, height, 8);

        sketch.fill(14, 80, 160);
        sketch.stroke(10, 60, 130);
        sketch.strokeWeight(1.5f);
        sketch.rect(x, y, width, height, 8);

        sketch.noFill();
        sketch.stroke(255, 255, 255, 60);
        sketch.strokeWeight(1);
        sketch.rect(x + 5, y + 5, width - 10, height - 10, 5);

    }

    public void draw(PApplet sketch) {
        if (turned) {
            drawBack(sketch);
        } else {
            drawFront(sketch);
        }
        sketch.strokeWeight(1);
        sketch.noStroke();
    }
}