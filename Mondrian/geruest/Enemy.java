import java.awt.*;

public class Enemy {

    private int xChange = 1;
    private int yChange = 1;
    private int x;
    private int y;
    private int width;
    private int height;

    public Enemy() {
        initEnemy();
    }

    private void initEnemy() {

        x = 100;
        y = 100;
        width = 12;
        height = 12;
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

    //randomly moves the enemy. 8 directions are possible.
    //every time the timer hits 0, the enemy receives a new randomly generated number and thus changes directions.
    //is called in actionPerformed()
    //enemy can´t move in filled area.

    public void move(boolean[][] allpoints) {
        //if the enemy hits the upper border OR the lower border OR reaches an already filled area
        //+enemySize/2 // || new Board().isFilled(x,y)
        if(y==0 || y==193 || allpoints[x][y] || allpoints[x+6][y] || allpoints[x][y+6] ||  allpoints[x+6][y+6]) {
            yChange *= -1; //invert its direction
        }
        //...left OR right...
        if(x==0 || x==193 || allpoints[x][y] || allpoints[x+6][y] || allpoints[x][y+6] ||  allpoints[x+6][y+6]) {
            xChange *= -1; //invert its direction
        }

        //always add a pixel (for smooth movement) to the enemys position.
        x += xChange;
        y += yChange;

    }


    //changes direction to the position of the player in order to make the game harder.
    public void kill(Point playerposition){

        if(playerposition.x < x){
            xChange = -1;
        }
        else if(playerposition.x > x){
            xChange = 1;
        }
        if(playerposition.y < y){
            yChange = -1;
        }
        if(playerposition.y > y){
            yChange = 1;
        }

        return;
    }
}
