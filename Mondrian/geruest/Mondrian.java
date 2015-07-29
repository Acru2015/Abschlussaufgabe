import javax.swing.*;
import java.awt.*;

public class Mondrian extends JFrame {

    private final int WIDTH = 200;
    private final int HEIGHT = 200;

    public Mondrian() {

        initUI();

    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            Mondrian window = new Mondrian();
            window.setVisible(true);
        });
    }

    private void initUI() {

        add(new Board());

        setSize(WIDTH, HEIGHT);
        setResizable(false);
        pack();

        setTitle("Mondrian");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}
