
import javax.swing.*;
import java.awt.*;

/**
 * Tato trida slouzi pro vytvoreni Hraciho okna
 */
public class GameWindow {


    //Instance tohoto okna
    public static JFrame frame = new JFrame();

    //Instance herniho panelu
    static GamePanel gamePanel;


    /**
     * Konstruktor
     */
    public GameWindow(String p1,String p2, int[] time, int[] increment, int enemyType,boolean maximized){

        //vytvoreni okna
        frame = new JFrame();
        frame.setTitle("Chess master 2023 - Roman Pejs - A22B0197P");

        gamePanel = new GamePanel(p1,p2,time,increment,enemyType);

        frame.add(gamePanel);







        //Defaultni velikost okna

//        frame.pack();
        if(maximized) frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        else frame.setSize(900, 600);

        frame.setMinimumSize(new Dimension(900, 600));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }



}
