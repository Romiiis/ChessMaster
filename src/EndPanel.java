import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

public class EndPanel extends JPanel implements MouseInputListener {

    JFrame frame;
    Player winner;
    String message;

    Game game;

    private Area newGameButton;
    private boolean newGameButtonHover = false;
    private Area exitButton;
    private boolean exitButtonHover = false;


    private Area Graph;
    private boolean GraphHover = false;

    public EndPanel(String message, Player winner, JFrame frame, Game game){
        this.winner = winner;
        this.frame = frame;
        this.message = message;
        this.game = game;

        GameWindow.frame.setEnabled(false);


        this.addMouseListener(this);
        this.addMouseMotionListener(this);

    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        //Anti aliasing
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawTitle(g2d);
        if(winner != null) drawWin(g2d);
        else createDraw(g2d);
        createNewGame(g2d);
        createGraphButton(g2d);
        createExitButton(g2d);

    }



    /**
     * Vykresleni titulku
     * @param g Graficky kontext
     */
    private void drawTitle(Graphics2D g){

        //Ulozeni transformace
        AffineTransform save = g.getTransform();

        //Ziskani Velikosti fontu z sirky
        String title = message;
        g.setFont(MenuPanel.getFontFromWidth((this.getWidth()), title, g));

        FontMetrics metrics = g.getFontMetrics(g.getFont());


        //Souradnice textu
        int x = (this.getWidth() - metrics.stringWidth(title)) / 2;
        int y = (int) (this.getHeight()/9.0 + metrics.getAscent()/2);


        //Ziskani fontu
        Font font = g.getFont();
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(title, font, frc);
        Shape outline = layout.getOutline(null);


        //Vytvoreni outline
        g.translate(x, y);
        g.setColor(Color.gray);
        g.fill(outline);
        g.setColor(Color.BLACK);
        g.draw(outline);

        //Obnoveni transformace
        g.setTransform(save);

        PLAYERCOLOR color1;
        PLAYERCOLOR color2;

        if(winner == null){
            color1 = PLAYERCOLOR.WHITE;
            color2 = PLAYERCOLOR.BLACK;
        }
        else {

            if (winner.getColor() == PLAYERCOLOR.WHITE) {
                color1 = PLAYERCOLOR.WHITE;
                color2 = PLAYERCOLOR.WHITE;
            } else {
                color1 = PLAYERCOLOR.BLACK;
                color2 = PLAYERCOLOR.BLACK;
            }
        }




        if(winner != null) {
            //Vykresleni bileho krale
            King k = new King((this.getWidth() / 15), y - this.getWidth() / 10, color1,
                    frame.getWidth() / 9);

            k.paint(g);


                //Vykresleni cerne kralovny
            Queen q = new Queen((this.getWidth() - this.getWidth() / 8) - this.getWidth() / 16,
                    y - this.getWidth() / 10, color2,
                    frame.getWidth() / 9);
            q.paint(g);
        }


        if(winner == null){

            King k = new King((this.getWidth() / 15), y - this.getWidth() / 10, color1,
                    frame.getWidth() / 9);


            k.paint(g);


            King k2 = new King((this.getWidth() - this.getWidth() / 8) - this.getWidth() / 16,
                    y - this.getWidth() / 10, color2,
                    frame.getWidth() / 9);


            k2.paint(g);
        }








    }

    public void drawWin(Graphics2D g){

        String text = "Vyhrál hráč:";

        //Ulozeni transformace
        AffineTransform save = g.getTransform();

        //Ziskani Velikosti fontu z sirky
        g.setFont(MenuPanel.getFontFromWidth((int) (this.getWidth()/1.2), text, g));

        FontMetrics metrics = g.getFontMetrics(g.getFont());

        int x = (this.getWidth() - metrics.stringWidth(text)) / 2;
        int y = (int) (this.getHeight()/2.5 + metrics.getAscent()/2);

        //Ziskani fontu
        Font font = g.getFont();
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(text, font, frc);
        Shape outline = layout.getOutline(null);


        //Vytvoreni outline
        g.translate(x, y);
        g.setColor(Color.lightGray);
        g.fill(outline);
        g.setColor(Color.GRAY);
        g.draw(outline);

        //Obnoveni transformace
        g.setTransform(save);


        String name = winner.name;


        Font font2 = new Font("Arial", Font.BOLD, this.getWidth()/10);
        //Ziskani Velikosti fontu z sirky
//        g.setFont(MenuPanel.getFontFromWidth((int) (this.getWidth()), name, g));
        g.setFont(font2);

        metrics = g.getFontMetrics(g.getFont());

        x = (this.getWidth() - metrics.stringWidth(name)) / 2;
        y = (int) (this.getHeight()/1.8 + metrics.getAscent()/2);

        //Ziskani fontu
        font = g.getFont();
        frc = g.getFontRenderContext();
        layout = new TextLayout(name, font, frc);
        outline = layout.getOutline(null);


        Color fill = Color.darkGray;
        Color stroke = Color.lightGray;

        if(winner.getColor() == PLAYERCOLOR.WHITE){
            fill = Color.WHITE;
            stroke = Color.BLACK;
        }


        //Vytvoreni outline
        g.translate(x, y);
        g.setColor(fill);
        g.fill(outline);
        g.setColor(stroke);
        g.draw(outline);

        //Obnoveni transformace
        g.setTransform(save);






    }

    /**
     * Vykresleni tlacitka start
     * @param g Graficky kontext
     */
    private void createNewGame(Graphics2D g){


        //Ziskani velikosti tlacitka
        double btnWidth = this.getWidth() / 3.2;
        double btnHeight = this.getHeight() / 7.0;

        //Ziskani pozice tlacitka
        double x = btnWidth/15;
        double y = this.getHeight() - btnHeight*1.2;

        //Text tlacitka
        String text = "Nová hra";

        //Ziskani fontu
        g.setFont(MenuPanel.getFontFromWidth((int) btnWidth, text, g));
        g.setColor(Color.BLACK);


        FontMetrics metrics = g.getFontMetrics(g.getFont());

        //Souradnice textu
        int xT = (int) (x + (btnWidth - metrics.stringWidth(text)) / 2);
        int yT = (int) (y + ((btnHeight - metrics.getHeight()) / 2) + metrics.getAscent());


        //Area tlacitka
        newGameButton = new Area(new RoundRectangle2D.Double(x, y, btnWidth, btnHeight, 10, 10));

        if(newGameButtonHover) g.setColor(Color.GRAY);
        else g.setColor(Color.LIGHT_GRAY);


        g.fill(newGameButton);

        g.setColor(Color.BLACK);
        g.draw(newGameButton);

        g.drawString(text, xT, yT);


    }

    /**
     * Tlacitko O aplikaci
     */
    private void createGraphButton(Graphics2D g){
        //Ziskani velikosti tlacitka
        double btnWidth = this.getWidth() / 3.2;
        double btnHeight = this.getHeight() / 7.0;

        //Ziskani pozice tlacitka
        double x = this.getWidth()/2.0 - btnWidth/2;
        double y = this.getHeight() - btnHeight*1.2;

        //Text tlacitka
        String text = "Graf";

        //Ziskani fontu
//        g.setFont(MenuPanel.getFontFromWidth(((int) btnWidth), text, g));
        g.setColor(Color.BLACK);


        FontMetrics metrics = g.getFontMetrics(g.getFont());

        //Souradnice textu
        int xT = (int) (x + (btnWidth - metrics.stringWidth(text)) / 2);
        int yT = (int) (y + ((btnHeight - metrics.getHeight()) / 2) + metrics.getAscent());


        //Area tlacitka
        Graph = new Area(new RoundRectangle2D.Double(x, y, btnWidth, btnHeight, 10, 10));

        if(GraphHover) g.setColor(Color.GRAY);
        else g.setColor(Color.LIGHT_GRAY);


        g.fill(Graph);

        g.setColor(Color.BLACK);
        g.draw(Graph);

        g.drawString(text, xT, yT);



    }

    private void createDraw(Graphics2D g){
        String text = "Remíza";

        //Ulozeni transformace
        AffineTransform save = g.getTransform();

        //Ziskani Velikosti fontu z sirky
        g.setFont(MenuPanel.getFontFromWidth((int) (this.getWidth()), text, g));

        FontMetrics metrics = g.getFontMetrics(g.getFont());

        int x = (this.getWidth() - metrics.stringWidth(text)) / 2;
        int y = (int) (this.getHeight()/2  + metrics.getAscent()/2);

        //Ziskani fontu
        Font font = g.getFont();
        FontRenderContext frc = g.getFontRenderContext();
        TextLayout layout = new TextLayout(text, font, frc);
        Shape outline = layout.getOutline(null);


        //Vytvoreni outline
        g.translate(x, y);
        g.setColor(Color.lightGray);
        g.fill(outline);
        g.setColor(Color.GRAY);
        g.draw(outline);

        //Obnoveni transformace
        g.setTransform(save);

    }

    private void createExitButton(Graphics2D g) {
        //Ziskani velikosti tlacitka
        double btnWidth = this.getWidth() / 3.2;
        double btnHeight = this.getHeight() / 7.0;

        //Ziskani pozice tlacitka
        double x = this.getWidth() - btnWidth - btnWidth / 15;
        double y = this.getHeight() - btnHeight * 1.2;

        //Text tlacitka
        String text = "Konec";

        //Ziskani fontu
        g.setColor(Color.BLACK);

        FontMetrics metrics = g.getFontMetrics(g.getFont());

        //Souradnice textu
        int xT = (int) (x + (btnWidth - metrics.stringWidth(text)) / 2);
        int yT = (int) (y + ((btnHeight - metrics.getHeight()) / 2) + metrics.getAscent());


        //Area tlacitka
        exitButton = new Area(new RoundRectangle2D.Double(x, y, btnWidth, btnHeight, 10, 10));

        if(exitButtonHover) g.setColor(Color.GRAY);
        else g.setColor(Color.LIGHT_GRAY);


        g.fill(exitButton);

        g.setColor(Color.BLACK);
        g.draw(exitButton);

        g.drawString(text, xT, yT);



    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(newGameButton.contains(e.getPoint())){
            Chess_SP_2023.reset();
            frame.dispose();

//            frame.revalidate();
        }
        if(exitButton.contains(e.getPoint())){
            System.exit(0);
        }
        if(Graph.contains(e.getPoint())){
            new GraphWindow(game.getPlayerTimes(),game.getBoard().getPlayers(),frame);
            frame.revalidate();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        posX = e.getX();
        posY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getXOnScreen() - posX;
        int y = e.getYOnScreen() - posY;
        frame.setLocation(x, y);
    }

    private int posX;
    private int posY;
    @Override
    public void mouseMoved(MouseEvent e) {
        newGameButtonHover = newGameButton.contains(e.getPoint());
        exitButtonHover = exitButton.contains(e.getPoint());
        GraphHover = Graph.contains(e.getPoint());
        repaint();
    }
}
