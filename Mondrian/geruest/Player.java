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

        x = -4;
        y = -4;
        width = 8;
        height = 8;
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
        if(x>-5 && x<=200) {
            x += xChange;
        }
        else{
            x = x < 0 ? -4 : 198;
        }
        if(y>-5 && y<=200) {
            y += yChange;
        }
        else {
            y = y <0 ?-4 : 198;
        }
    }

    public void keyPressed(KeyEvent e) {
        System.out.println(y);

        xChange = 0;
        yChange = 0;

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
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
