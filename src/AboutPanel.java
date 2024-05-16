import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

/**
 * Tato trida slouzi pro vytvoreni panelu s informacemi o programu
 *
 * @author Roman Pejs
 */
public class AboutPanel extends JPanel {


    String info = "Tento program slouží k tréninku šachu a umožňuje uživatelům vylepšit své schopnosti a dovednosti v této strategické hře.";


    String head = "Západočeská univerzita v Plzni\n" +
            "Fakulta aplikovaných věd\n" +
            "Katedra informatiky a výpočetní techniky\n" +
            "KIV/UPG\n" +
            "Roman Pejs\n" +
            "2023 ";


    /**
     * Konstruktor
     */
    public AboutPanel(){
        super();
    }


    /**
     * Vykresleni panelu
     * @param g Graficky kontext
     */
    @Override
    public void paint(Graphics g) {

        super.paint(g);

        //Anti aliasing
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        //Vykresleni veci
        drawTitle((Graphics2D) g);
        drawInfo((Graphics2D) g);
        drawHead((Graphics2D) g);

    }

    /**
     * Vykresleni titulku
     * @param g Graficky kontext
     */
    private void drawTitle(Graphics2D g){

        //Ulozeni transformace
        AffineTransform save = g.getTransform();

        //Ziskani Velikosti fontu z sirky
        String title = "Chess Master 2023";
        g.setFont(MenuPanel.getFontFromWidth((int) (this.getWidth()), title, g));


        //Ziskani font metrics
        FontMetrics metrics = g.getFontMetrics(g.getFont());


        //Souradnice textu
        int x = (this.getWidth() - metrics.stringWidth(title)) / 2;
        int y = (int) (this.getHeight()/10.0);


        //Ziskani fontu
        Font font = g.getFont();
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(title, font, frc);
        Shape outline = layout.getOutline(null);


        //Vytvoreni outline
        g.translate(x, y);
        g.setColor(Color.lightGray);
        g.fill(outline);
        g.setColor(Color.BLACK);
        g.draw(outline);

        //Obnoveni transformace
        g.setTransform(save);



        //Vykresleni bileho krale
        Rook k = new Rook((this.getWidth()/8),y - this.getWidth()/19 , PLAYERCOLOR.BLACK,
                this.getWidth()/16);


        k.paint(g);


        //Vykresleni cerne kralovny
        Rook q = new Rook((this.getWidth() -this.getWidth()/8) - this.getWidth()/16,
                y - this.getWidth()/19, PLAYERCOLOR.WHITE,
                this.getWidth()/16);


        q.paint(g);



    }


    /**
     * Vykresleni hlavicky
     * @param g Graficky kontext
     */
    private void drawHead(Graphics2D g){

        Rectangle bounds = new Rectangle(this.getWidth()/10, (int) (this.getHeight()/6.8),
                this.getWidth()/10*8, (int) (this.getHeight()/10*2.5));

        g.setColor(Color.lightGray);
        g.fill(bounds);
        g.setColor(Color.BLACK);
        g.draw(bounds);



        String[] lines = head.split("\n");

        g.setFont(MenuPanel.getFontFromWidth(bounds.width, lines[0], g));
        g.setFont(new Font("Courier New", Font.BOLD, g.getFont().getSize()));

        FontMetrics metrics = g.getFontMetrics(); // g je Graphics objekt
        int maxWidth = (int) bounds.getWidth() - bounds.width/15; // šířka prostoru pro text
        List linesList = new List();
        String currentLine = lines[0];

        for(int i = 1; i < lines.length; i++){
            String word = lines[i];
            int width = metrics.stringWidth(currentLine + " " + word);
            if (width < maxWidth) {
                currentLine += " " + word;
            } else {
                linesList.add(currentLine);
                currentLine = word;
            }
        }
        linesList.add(currentLine); // přidání posledního řádku

        for(int i = 0; i < linesList.getItemCount(); i++){
            int x = (getWidth() - getFontMetrics(g.getFont()).stringWidth(linesList.getItem(i))) / 2;

            g.drawString(linesList.getItem(i), x, (int) (bounds.y  + (i+1.5)*bounds.height/5.5));
        }



    }


    /**
     * Vykresleni informaci
     * @param g Graficky kontext
     */
    private void drawInfo(Graphics2D g){

        Rectangle bounds = new Rectangle(this.getWidth()/10, (int) (this.getHeight()/1.8),
                this.getWidth()/10*8, (int) (this.getHeight()/10*4));

        g.setColor(Color.lightGray);
        g.fill(bounds);
        g.setColor(Color.BLACK);
        g.draw(bounds);

        g.setFont(MenuPanel.getFontFromWidth((int) (bounds.width*5.5), info, g));
        g.setFont(new Font("Courier New", Font.BOLD, g.getFont().getSize()));

        FontMetrics metrics = g.getFontMetrics(); // g je Graphics objekt
        int maxWidth = (int) bounds.getWidth() - bounds.width/15; // šířka prostoru pro text
        List lines = new List();
        String[] words = info.split("\\s"); // rozdělení textu na slova
        String currentLine = words[0];
        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            int width = metrics.stringWidth(currentLine + " " + word);
            if (width < maxWidth) {
                currentLine += " " + word;
            } else {
                lines.add(currentLine);
                currentLine = word;
            }
        }
        lines.add(currentLine); // přidání posledního řádku


        for(int i = 0; i < lines.getItemCount(); i++){
            g.drawString(lines.getItem(i), bounds.x + bounds.width/15, (int) (bounds.y + (i+1)*bounds.height/5.5));
        }





    }
}
