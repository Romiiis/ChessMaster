import javax.swing.*;
import java.awt.*;


/**
 * Trida reprezentuje tlacitko pro vyber figurky pri promene
 *
 * @author Roman Pejs
 */
public class MyButton extends JButton {


    //Figurka na kterou se bude tlacitko vztahovat
    private AFigure figure;

    private int size;


    /**
     * Konstruktor
     * @param text - text na tlacitku
     * @param color - barva figurky
     */
    public MyButton(String text, PLAYERCOLOR color) {

        //Defaulni velikost
        size = 1;

        //Nastaveni textu (respektive zobrazeni figurky)
        if(text.equals("Queen")) figure = new Queen(0,0, color, size);
        if(text.equals("Rook"))figure = new Rook(0,0, color, size);
        if(text.equals("Bishop"))figure = new Bishop(0,0, color,size);
        if(text.equals("Knight"))figure = new Knight(0,0, color,size);

    }


    /**
     * Vykresleni tlacitka
     * @param g  Graficky kontext
     */
    public void paint(Graphics g){
        super.paint(g);

        //Nastaveni pozadi
        this.setBackground(Color.WHITE);

        //Nastaveni velikosti
        this.setBounds(this.getX(),this.getY(),size,size/16);

        //Pozice figurky
        Graphics2D g2 = (Graphics2D) g;
        int x = (int) ((getWidth()/2 - (size/2)*0.8));
        int y = (int) ((getHeight()/2 + (size/5)*0.8));

        //Vykresleni figurky
        g2.translate(x, y);
        g2.scale(0.8,0.8);

        figure.paint(g2);

    }


    /**
     * Nastaveni velikosti tlacitka
     * @param size - velikost
     */
    public void setSize(int size) {
        this.size = size;
        figure.setDrawSize(size);
        repaint();
    }



}
