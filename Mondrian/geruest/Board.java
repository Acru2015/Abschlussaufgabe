import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Board extends JPanel implements ActionListener {

    private final int DELAY = 10;
    Polys polys;
    private Timer timer;
    private Player player;
    private Enemy enemy;
    private CopyOnWriteArrayList<Point2D> currentPointList = new CopyOnWriteArrayList<>();
    private ArrayList<Point> borderPointList = new ArrayList<>();
    private ArrayList<Point> whereWasI = new ArrayList<>();
    private boolean[][] allPoints = new boolean[205][205];
    private int percentage = 0;
    private int offset;
    private int enemyOffset;

    private final Color[] COLORS = new Color[]{Color.red, Color.yellow, Color.blue, Color.black, Color.lightGray};
    //array of colors a polygon can be drawn in.

    private final String[] WINMSGS = new String[]{"You actually did it, you absolute madman!", "I stand in awe!", "Awesome, you won",
            "You made the right choices.", "You only got lucky, next time you wont´t get away that easyly.", "WINRAR!"};
    //array of messages. one of those is shown if the game was lost.

    private final String[] LOSTMSGS = new String[]{"C´mon man, u can do better!", "Das war suboptimal..", "Spielst du um zu gewinnen, oder um diese Nachricht zu sehen?",
            "You´re embarrassing yourself!", "That´s not how you play this game", "Your score is 0.", "Vielleicht beim nächsten Mal, aber ich glaube eher nicht", "Dein wievielter " +
            "Versuch war das jetzt? Ich hab aufgehört mitzuzählen", "You lost. Again.", "Here I stand again, THE LOSER!"};
    //array of messages. one of those is shown if the game was lost.


    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.white);

        setPreferredSize(new Dimension(200, 200));
        setDoubleBuffered(true);


        player = new Player();
        enemy = new Enemy();

        offset = player.getHeight()/2;
        enemyOffset = enemy.getHeight()/2;

        polys = new Polys();

        for (int i = 0; i < 200; i++) {
            allPoints[0][i] = true;
            borderPointList.add(new Point(0, i));
            allPoints[199][i] = true;
            borderPointList.add(new Point(199, i));
            allPoints[i][0] = true;
            borderPointList.add(new Point(i, 0));
            allPoints[i][199] = true;
            borderPointList.add(new Point(i, 199));
        }

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.move();

        int liveAndLetLive = (int)(Math.random()*100000);
        if(liveAndLetLive < 1000){
            enemy.kill(new Point(player.getX(), player.getY()));
        }
        enemy.move(allPoints);


        repaint();

        //checks if >80% have been filled, and shows a popup
        if(didIWinYet()){
            JOptionPane.showMessageDialog(null, WINMSGS[(int)(Math.random()*WINMSGS.length)] + "\n" + "Your Score is "
                    + String.format("%,d", percentage) + "!", "You Win!", 3);
            System.exit(0);
        }
        //checks if there was a collission between a line which is currently being drawn or the player and the enemy.
        if(hitBox()){
            JOptionPane.showMessageDialog(null, LOSTMSGS[(int)(Math.random()*LOSTMSGS.length)], "You Lost", 3);
            System.exit(0);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);

        Toolkit.getDefaultToolkit().sync();
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        int playerX = player.getX();
        int playerY = player.getY();
        //System.out.println("X: " + playerX + " Y: " + playerY);
        //System.out.println(allPoints[playerX][playerY]);

        if (playerX >= 0 && playerX < 200 && playerY >= 0 && playerY < 200) {
            //allPoints[playerX][playerY] = true;
            currentPointList.add(new Point(playerX, playerY));
            if(!allPoints[playerX][playerY]) {
                whereWasI.add(new Point(playerX, playerY));
            }
        }

        g2d.setStroke(new BasicStroke(2));

        //COLORS[(int) (Math.random() * COLORS.length)]
        g2d.setColor(Color.yellow);
        //polys.drawPolys(g2d);
        drawPoints(g2d);

        g2d.setColor(Color.black);
        drawLine(g2d);

        g2d.setColor(Color.blue);
        g2d.fillOval(playerX, playerY, player.getWidth(), player.getHeight());

        g2d.setColor(Color.red);
        g2d.fillOval(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());


    }

    private void drawPoints(Graphics2D g2d) {
        for (int i = 0; i < allPoints.length; i++) {
            for (int j = 0; j < allPoints[i].length; j++) {
                if (allPoints[i][j]) {
                    g2d.drawLine(i, j, i, j);
                }
            }
        }
    }

    private void drawLine(Graphics g) { //draw a line out of the current points
        Graphics2D g2d = (Graphics2D) g;

        for (Point c : whereWasI) {
            g2d.drawLine((int) c.getX()+offset, (int) c.getY()+offset, (int) c.getX()+offset, (int) c.getY()+offset); // offset so that line
            //is centered
        }
    }

    public void mergePoints(boolean[][] booleans) {    //Merges all Points of floodfill into allPoints
        if (booleans != null) {
            for (int i = 0; i < booleans.length; i++) {
                for (int j = 0; j < booleans[i].length; j++) {
                    if (booleans[i][j]) {
                        allPoints[i][j] = true;
                        for(int a = i; a<=i+offset; a++){
                            for(int b = j; b <=j+offset; b++){
                                allPoints[a][b]=true;
                            }
                        }
                    }
                }
            }
        }
    }

    private void mergePoints(CopyOnWriteArrayList<Point2D> currentPointList) {
        for (Point2D aCurrentPointList : currentPointList) {
            allPoints[((int) aCurrentPointList.getY())][((int) aCurrentPointList.getY())] = true;
        }
    }

    private boolean didIWinYet(){
        percentage = 0;
        for(boolean[] b : allPoints){
            for(boolean c : b){
                if(c){
                    percentage++;
                }
            }
        }

        return ((percentage*100)/(200*200) >= 80);
    }

    //calculates a hit between either the player and the enemy or a line currently being drawn and the enemy
    //if either one is a hit, the game is lost and a message pops up (see keypressed method)
    //may not be the best way to do this, but works.
    private boolean hitBox(){

        int a = player.getX();
        int b = player.getY();

        boolean xHit = false;
        boolean yHit = false;

        boolean lineHit;
        int lineX = -99;
        int lineY = -99;

        //checks for the whole size of the player and the enemy in x and y coordinates. if there is a hit in x AND
        //y coordinates, the player has touched the enemy and thus lost the game.
        for(int i = player.getX()+2; i<=player.getX()+player.getHeight()-2; i++){
            for(int j = enemy.getX()+2; j<=enemy.getX()+enemy.getHeight()-2; j++){
                if(i==j) {
                    xHit = true;
                    break;
                }
            }
        }
        for(int i = player.getY()+2; i<=player.getY()+player.getHeight()-2; i++){
            for(int j = enemy.getY()+2; j<=enemy.getY()+enemy.getHeight()-2; j++) {
                if (i == j) {
                    yHit = true;
                    break;
                }
            }
        }

        //checks every point in the line currently being drawn with the position of the enemy.
        //if both exist, the enemy has touched a line currently being drawn which means the player has lost the game.
        for(Point2D p : currentPointList){
            for(int k = enemy.getX()+2; k<=enemy.getX()+enemy.getHeight()-2; k++){
                if(p.getX()==k) {
                    lineX = (int)p.getX();
                    break;
                }
            }
        }

        for(Point2D p : currentPointList){
            for(int l = enemy.getY()+2; l<=enemy.getY()+enemy.getHeight()-2; l++){
                if(p.getY()==l) {
                    lineY = (int)p.getY();
                    break;
                }
            }
        }

        //just because there is a matching x and y variable somewhere in the points of the current line doesn´t mean
        //the enemy has actually touched the line. those x and y variables must come from the same point.
        Point hitpoint = new Point(lineX, lineY);

        //and the deque must contain this point. && !whereWasI.contains(hitpoint)
        lineHit = currentPointList.contains(hitpoint) && !borderPointList.contains(new Point(hitpoint.x-offset, hitpoint.y-offset));

        return ((xHit&&yHit) || lineHit) && !allPoints[a+offset][b+offset];
    }


    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
            polys.checkPoints(currentPointList, allPoints);
            mergePoints(polys.getPointsToMerge());
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
            polys.checkPoints(currentPointList, allPoints);
            mergePoints(polys.getPointsToMerge());
        }
    }
}
