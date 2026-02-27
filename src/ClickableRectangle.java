import processing.core.PApplet;

public class ClickableRectangle {
    int x;
    int y;
    int width;
    int height;

    boolean isClicked(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public void draw(PApplet app) {
        app.rect(x, y, width, height);
    }

    public ClickableRectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public ClickableRectangle() {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }
}
