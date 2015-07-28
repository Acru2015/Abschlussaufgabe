import java.awt.event.KeyEvent;

public class Player {

    private int xChange = 0;
    private int yChange = 0;
    private int x;
    private int y;
    private int width;
    private int height;

    public Player() {
        initPlayer();
    }

    private void initPlayer() {

        x = 0;
        y = 0;
        width = 10;
        height = 10;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void move() {
        x += xChange;
        y += yChange;
    }

    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT && x > 0) {
            xChange = -1;
            yChange = 0;
        }

        if (key == KeyEvent.VK_RIGHT && x < 200) {
            xChange = 1;
            yChange = 0;
        }

        if (key == KeyEvent.VK_UP && y > 0) {
            yChange = -1;
            xChange = 0;
        }

        if (key == KeyEvent.VK_DOWN && y < 200) {
            yChange = 1;
            xChange = 0;
        }
    }

    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            xChange = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            xChange = 0;
        }

        if (key == KeyEvent.VK_UP) {
            yChange = 0;
        }

        if (key == KeyEvent.VK_DOWN) {
            yChange = 0;
        }
    }
}
