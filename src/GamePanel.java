import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;


/**
 * Panel, ktery je roztahnuty na cele okno
 *
 * @author Roman Pejs
 */
public class GamePanel extends JPanel {


    //Instance hry
    private final Game game;


    /**
     * Konstruktor
     * nastavi velikost panelu
     * vytvori hraci plochu
     * vytvori listener na mys
     */
    public GamePanel(String p1, String p2, int[] time, int[] increment, int enemyType) {

        super();
        //Vytvoreni hry
        game = new Game(this, p1, p2, time, increment,enemyType);


    }


    /**
     * Vykresleni panelu
     * Vytvori graficky kontext 2D
     * Ulozi transformaci
     * Vykresli sachovnici
     * Nastavi transformaci
     *
     * @param g graficky kontext
     */

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        int size = Math.min(this.getWidth(), this.getHeight());
        this.setPreferredSize(new Dimension(size, size));

        //Vytvoreni grafickeho kontextu 2D
        Graphics2D g2 = (Graphics2D) g.create();

        AffineTransform savetransform = g2.getTransform();


        //Vykresleni sachovnice + figurky
        game.getBoard().paint(g2, this.getWidth(), this.getHeight());


        for(Player p : game.getBoard().getPlayers()){
            p.paint(g2);
        }

        //Obnoveni transformace
        for(Player p : game.getBoard().getPlayers()){
            p.paintOutFigures(g2);
        }


        g2.setTransform(savetransform);


    }





}
