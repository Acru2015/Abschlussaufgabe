import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.concurrent.CopyOnWriteArrayList;

public class Board extends JPanel implements ActionListener {

    private final int DELAY = 10;
    private Polys polys;
    private Timer timer;
    private Player player;
    private Enemy enemy;
    private CopyOnWriteArrayList<Point2D> currentPointList = new CopyOnWriteArrayList<>();      //Points of line
    private boolean[][] allPoints = new boolean[200][200];          //matrix of all points, whether visited or not

    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.white);

        player = new Player();
        enemy = new Enemy();

        polys = new Polys();

        for (int i = 0; i < 200; i++) {         //generate outer lines
            allPoints[0][i] = true;
            allPoints[199][i] = true;
            allPoints[i][0] = true;
            allPoints[i][199] = true;
        }

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.move();
        enemy.move();
        repaint();
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

        int enemyX = enemy.getX();
        int enemyY = enemy.getY();

        if (playerX >= 0 && playerX < 200 && playerY >= 0 && playerY < 200) {       //if inside boundarys, add to cPL
            currentPointList.add(new Point(playerX, playerY));
        }

        g2d.setStroke(new BasicStroke(2));

        g2d.setColor(Color.red);
        drawLine(g2d);

        g2d.setColor(Color.blue);
        drawPoints(g2d);

        g2d.setColor(Color.green);
        g2d.fillOval(playerX, playerY, player.getWidth(), player.getHeight());

        g2d.setColor(Color.red);
        g2d.fillOval(enemyX, enemyY, enemy.getWidth(), enemy.getHeight());

    }

    private void drawPoints(Graphics2D g2d) {                       //fill the areas by drawing a line from each point to itself
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

        for (Point2D c : currentPointList) {
            g2d.drawLine((int) c.getX(), (int) c.getY(), (int) c.getX(), (int) c.getY());
        }
    }

    public void mergePoints(boolean[][] booleans) {    //Merges all Points of floodfill into allPoints
        if (booleans != null) {
            for (int i = 0; i < booleans.length; i++) {
                for (int j = 0; j < booleans[i].length; j++) {
                    if (booleans[i][j]) {
                        allPoints[i][j] = true;
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