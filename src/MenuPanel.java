import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;


/**
 * Panel pro vykresleni menu
 *
 * @author Roman Pejs
 */
public class MenuPanel extends JPanel {

    //Instance tridy
    private static MenuPanel instance;

    private JFrame frame;

    //Area pro tlacitko start
    private Area startButton;

    private Area aboutButton;

    private String suffix = "";

    //Zda je mys nad tlacitkem start
    private boolean startButtonHovered = false;

    private boolean aboutButtonHovered = false;

    JTextField player1 = new JTextField("Player WHITE");
    JTextField player2 = new JTextField("Player BLACK");

    JRadioButton PvP = new JRadioButton("hráč vs hráč");
    JRadioButton PvCR = new JRadioButton("hráč vs počítač (náhodný)");
    JRadioButton PvCRL = new JRadioButton("hráč vs počítač (Stockfish)");

    JCheckBox timer = new JCheckBox("Časomíra");

    JTextField p1Time = new JTextField("10");
    JTextField p2Time = new JTextField("10");

    JTextField p1Increment = new JTextField("3");
    JTextField p2Increment = new JTextField("3");










    /**
     * Konstruktor
     * @param frame Okno ve kterym je panel
     */
    public MenuPanel(JFrame frame){
        super(null);

        //Nastaveni velikosti
        setSize(600,600);

        //Nastaveni instance
        instance = this;
        this.frame = frame;
        ToolTipManager.sharedInstance().setInitialDelay(100);

        p1Time.setToolTipText("Čas v minutách 1 - 10");
        p2Time.setToolTipText("Čas v minutách 1 - 10");

        p1Increment.setToolTipText("Inkrement v sekundách 0 - 10");
        p2Increment.setToolTipText("Inkrement v sekundách 0 - 10");


        //Nastaveni kliknuti
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);


                //Pokud je kliknuto na tlacitko start
                if(startButton.contains(e.getPoint()))start();

                if(aboutButton.contains(e.getPoint()))about();
            }
        });


        //Pro pohyb nad tlacitkem start
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                startButtonHovered = startButton.contains(e.getPoint());
                aboutButtonHovered = aboutButton.contains(e.getPoint());
                repaint();
            }
        });

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeHandle();
            }
        });


        PvCR.addActionListener(e -> {
            if(PvCR.isSelected()){
                suffix = " (R)";
                player2.setText(generateBotName());
                player2.setEnabled(false);

            }
        });

        PvCRL.addActionListener(e -> {
            if(PvCRL.isSelected()){
                suffix = " (S)";
                player2.setText(generateBotName());
                player2.setEnabled(false);
            }
        });

        PvP.addActionListener(e -> {
            if(PvP.isSelected()){
                player2.setText("Player BLACK");
                player2.setEnabled(true);
            }
        });

        setUpSettings();
        repaint();






    }


    String[] names = {"Han Solo","Chewbacca","Anakin","Obi-Wan","Yoda","Leia","Padme"};
    private String generateBotName(){
        Random random = new Random();
        int index = random.nextInt(names.length);
        return names[index]  + " " +suffix;

    }

    private void setUpSettings(){

        player1.setBounds((int) (2* this.getWidth()/16.0),this.getHeight()/2 + (this.getWidth()/4)/2  ,this.getWidth()/4 , this.getHeight()/16);
        player2.setBounds((int) (2* this.getWidth()/16.0), (int) (this.getHeight()/2.5) + (this.getWidth()/4)/2 , this.getWidth()/4, this.getHeight()/16);

        timer.setBounds((int) (2* this.getWidth()/16.0), (int) (this.getHeight()/2.5) + (this.getWidth()/4)/2 + 50, timer.getPreferredSize().width, timer.getPreferredSize().height);


        timer.setSelected(true);
        timer.addActionListener(e -> {
            if(timer.isSelected()){
                p1Time.setEnabled(true);
                p2Time.setEnabled(true);
                p1Increment.setEnabled(true);
                p2Increment.setEnabled(true);
            }
            else{
                p1Time.setEnabled(false);
                p2Time.setEnabled(false);
                p1Increment.setEnabled(false);
                p2Increment.setEnabled(false);
            }

            repaint();
        });


        add(timer);

        add(player1);
        add(player2);

        add(p1Time);
        add(p1Increment);

        add(p2Time);
        add(p2Increment);

        ButtonGroup group = new ButtonGroup();
        group.add(PvP);
        group.add(PvCR);
        group.add(PvCRL);

        PvP.setBounds((int) (2* this.getWidth()/16.0), (int) (this.getHeight()/2.5) + (this.getWidth()/4)/2 + 100, PvP.getPreferredSize().width, PvP.getPreferredSize().height);
        PvCR.setBounds((int) (2* this.getWidth()/16.0), (int) (this.getHeight()/2.5) + (this.getWidth()/4)/2 + 130, PvCR.getPreferredSize().width, PvCR.getPreferredSize().height);
        PvCRL.setBounds((int) (2* this.getWidth()/16.0), (int) (this.getHeight()/2.5) + (this.getWidth()/4)/2 + 160, PvCRL.getPreferredSize().width, PvCRL.getPreferredSize().height);

        PvP.setSelected(true);

        add(PvP);
        add(PvCR);
        add(PvCRL);






    }

    /**
     * Vykresleni panelu
     * @param g Graficky kontext
     */
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Vykresleni Titulku
        drawTitle(g2);

        //Vykresleni tlacitka start
        getStartButton(g2);
        createAboutButton(g2);
        drawSettings(g);



    }


    /**
     * Vykresleni tlacitka start
     * @param g Graficky kontext
     */
    private void getStartButton(Graphics2D g){


        //Ziskani velikosti tlacitka
        double btnWidth = this.getWidth() / 4.0;
        double btnHeight = this.getHeight() / 10.0;

        //Ziskani pozice tlacitka
        double x = this.getWidth() / 2.0 - btnWidth / 2.0;
        double y = this.getHeight() / 4.0 - btnHeight / 2.0;

        //Text tlacitka
        String text = "Začít hru";

        //Ziskani fontu
        g.setFont(getFontFromWidth((int) btnWidth, text, g));
        g.setColor(Color.BLACK);


        FontMetrics metrics = g.getFontMetrics(g.getFont());

        //Souradnice textu
        int xT = (int) (x + (btnWidth - metrics.stringWidth(text)) / 2);
        int yT = (int) (y + ((btnHeight - metrics.getHeight()) / 2) + metrics.getAscent());


        //Area tlacitka
        startButton = new Area(new RoundRectangle2D.Double(x, y, btnWidth, btnHeight, 10, 10));

        if(startButtonHovered) g.setColor(Color.GRAY);
        else g.setColor(Color.LIGHT_GRAY);


        g.fill(startButton);

        g.setColor(Color.BLACK);
        g.draw(startButton);

        g.drawString(text, xT, yT);


    }

    /**
     * Tlacitko O aplikaci
     */
    private void createAboutButton(Graphics2D g){
        //Ziskani velikosti tlacitka
        double btnWidth = this.getWidth() / 4.0;
        double btnHeight = this.getHeight() / 10.0;

        //Ziskani pozice tlacitka
        double x = this.getWidth() / 2.0 - btnWidth / 2.0;
        double y = this.getHeight() / 2.5 - btnHeight / 2.0;

        //Text tlacitka
        String text = "O aplikaci";

        //Ziskani fontu
        g.setFont(getFontFromWidth((int) ((int) btnWidth*1.05), text, g));
        g.setColor(Color.BLACK);


        FontMetrics metrics = g.getFontMetrics(g.getFont());

        //Souradnice textu
        int xT = (int) (x + (btnWidth - metrics.stringWidth(text)) / 2);
        int yT = (int) (y + ((btnHeight - metrics.getHeight()) / 2) + metrics.getAscent());


        //Area tlacitka
        aboutButton = new Area(new RoundRectangle2D.Double(x, y, btnWidth, btnHeight, 10, 10));

        if(aboutButtonHovered) g.setColor(Color.GRAY);
        else g.setColor(Color.LIGHT_GRAY);


        g.fill(aboutButton);

        g.setColor(Color.BLACK);
        g.draw(aboutButton);

        g.drawString(text, xT, yT);

        //Vykresleni obrysu
        double offset = this.getWidth()/16.0;

        Rectangle2D.Double rect = new Rectangle2D.Double(offset,this.getHeight()/2.0, this.getWidth() - 2*offset, this.getHeight() - this.getHeight()/2.0 - offset);

        g.setStroke(new BasicStroke((float) (getWidth()/400)));
        g.setColor(Color.BLACK);
        g.draw(rect);

        Rectangle2D.Double rectP = new Rectangle2D.Double(offset,this.getHeight()/2.0 + (this.getHeight()/2.0 - offset)/2, (this.getWidth() + 4*offset)/2, (this.getHeight()/2.0 - offset)/2);
        g.draw(rectP);

        Rectangle2D.Double rectH = new Rectangle2D.Double(offset + (this.getWidth() + 4*offset)/2 ,this.getHeight()/2.0 + (this.getHeight()/2.0 - offset)/2,(this.getWidth() - 2*offset)/3.5,(this.getHeight()/2.0 - offset)/2);
        g.draw(rectH);


        //Vykresleni textu

        String textS = "Nastavení";
        g.setFont(getFontFromWidth((int) ((this.getWidth() - 2*offset)/3), textS, g));

        FontMetrics metrics2 = g.getFontMetrics(g.getFont());

        int x1 = (int) offset;
        int y1 = (int) (this.getHeight()/2.0 - metrics2.getHeight()/4);

        g.drawString(textS, x1, y1);


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
        g.setFont(getFontFromWidth((this.getWidth()), title, g));

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
        King k = new King((this.getWidth()/8),y - this.getWidth()/19 , PLAYERCOLOR.WHITE,
                MenuPanel.instance.getWidth()/16);


        k.paint(g);


        //Vykresleni cerne kralovny
        Queen q = new Queen((this.getWidth() -this.getWidth()/8) - this.getWidth()/16,
                y - this.getWidth()/19, PLAYERCOLOR.BLACK,
                MenuPanel.instance.getWidth()/16);


        q.paint(g);



    }





    /**
     * Staticka metoda pro ziskani velikosti fontu z sirky
     * @param width Sirka
     * @param text Text
     * @param g Graficky kontext
     * @return Font
     */
    public static Font getFontFromWidth(int width, String text, Graphics g){

        //Dekodovani
        Font font = Font.decode("Arial-BOLD-12");

        //Ziskani velikosti textu
        Rectangle2D r2d = g.getFontMetrics(font).getStringBounds(text, g);

        //Zmena velikosti fontu
        font = font.deriveFont((float)(font.getSize2D() * (width/2)/r2d.getWidth()));

        return font;
    }


    private void drawSettings(Graphics g){


        int offset = this.getWidth()/16;
        int wows = (int) (this.getHeight() - this.getHeight()/2.0 - offset);

        String text = "Hráč 1";


        int w = (int) ((this.getWidth() / 2.0 - 2*offset)/2);


        Font f = new Font("Arial", Font.BOLD, getMaxFontSize(text, w, wows/8, (Graphics2D) g));

        g.setFont(f);
        g.drawString(text, offset + w, (int) ((int) (this.getHeight()/2.0) + 1.5*wows/8));

        g.drawString("Hráč 2", offset + w, (int) (this.getHeight()/2.0) + 3*wows/8);



        if(timer.isSelected())g.setColor(Color.BLACK);
        else g.setColor(Color.LIGHT_GRAY);

        g.setFont(new Font("Arial", Font.BOLD, getMaxFontSize(text, w, wows/15, (Graphics2D) g)));
        g.drawString("Hráč 1", (int) (offset*1.2),(int) ((int) (this.getHeight()/2.0) + 6.05*wows/8));

        g.drawString("Hráč 2", (int) (offset*1.2),(int) ((int) (this.getHeight()/2.0) + 7.2*wows/8));

        g.drawString("Čas [min]", (int) (w*1.2),(int) ((int) (this.getHeight()/2.0) + 6.05*wows/8));
        g.drawString("Inkrement [s]", (int) (w*2.4),(int) ((int) (this.getHeight()/2.0) + 6.05*wows/8));

        g.drawString("Čas [min]", (int) (w*1.2),(int) ((int) (this.getHeight()/2.0) + 7.2*wows/8));
        g.drawString("Inkrement [s]", (int) (w*2.4),(int) ((int) (this.getHeight()/2.0) + 7.2*wows/8));


    }

    private void resizeHandle(){

        int offset = this.getWidth()/16;
        int wows = (int) (this.getHeight() - this.getHeight()/2.0 - offset);

        String text = "Hráč 1";


        int w = (int) ((this.getWidth() / 2.0 - 2*offset)/2);


        Font f = new Font("Arial", Font.BOLD, getMaxFontSize(text, w, wows/8, (Graphics2D) this.getGraphics()));

        player1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        player2.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        f = new Font("Arial", Font.BOLD, getMaxFontSize(text, w, wows/10, (Graphics2D) this.getGraphics()));
        player1.setFont(f);
        player2.setFont(f);

        player1.setBounds(offset + 2*w, (int) ((this.getHeight()/2.0) + wows/8) - player1.getHeight()/4, 2*w, wows/8);
        player2.setBounds(offset + 2*w, (int) ((int) (this.getHeight()/2.0) + 3*wows/8- player2.getHeight()*0.7), 2*w,wows/8);


        timer.setFont(f);
//        timer.setBounds((int) (1.2*offset), (int) ((int) (this.getHeight()/2.0) + 4.1*wows/8), timer.getPreferredSize().width,timer.getHeight() + 5);
        timer.setBounds((int) (1.2*offset), (int) ((int) (this.getHeight()/2.0) + 4.1 * wows/8), timer.getPreferredSize().width,wows/8);

        p1Time.setBounds(5*offset, (int) ((int) (this.getHeight()/2.0) + 5.4*wows/8), w/2,wows/8);
        p1Increment.setBounds(9*offset,(int) ((int) (this.getHeight()/2.0) + 5.4*wows/8), w/2,wows/8);

        p2Time.setBounds(5*offset, (int) ((int) (this.getHeight()/2.0) + 6.6*wows/8), w/2,wows/8);
        p2Increment.setBounds(9*offset,(int) ((int) (this.getHeight()/2.0) + 6.6*wows/8), w/2,wows/8);

        p1Time.setFont(f);
        p1Increment.setFont(f);
        p2Time.setFont(f);
        p2Increment.setFont(f);


        f= new Font("Arial", Font.BOLD, getMaxFontSize(text, w, wows/15, (Graphics2D) this.getGraphics()));

        PvP.setFont(f);
        PvCR.setFont(f);
        PvCRL.setFont(f);


        PvP.setBounds((int) (offset + (this.getWidth() + 4.5*offset)/2) , (int) (this.getHeight()/2.0 + (this.getHeight()/2.0 - offset)/1.8), PvP.getPreferredSize().width, PvP.getPreferredSize().height);

        PvCR.setBounds((int) (offset + (this.getWidth() + 4.5*offset)/2 ), (int) (this.getHeight()/2.0 + (this.getHeight()/2.0 - offset)/1.45), PvCR.getPreferredSize().width, PvCR.getPreferredSize().height);
        PvCRL.setBounds((int) (offset + (this.getWidth() + 4.5*offset)/2) , (int) (this.getHeight()/2.0 + (this.getHeight()/2.0 - offset)/1.2), PvCRL.getPreferredSize().width, PvCRL.getPreferredSize().height);



    }



    public static int getMaxFontSize(String text, int width, int height, Graphics2D g2d) {
        int fontSize = 1;
        int stringHeight = 0;
        int stringWidth = 0;
        do {
            Font font = new Font("Arial", Font.PLAIN, fontSize);
            g2d.setFont(font);
            FontMetrics metrics = g2d.getFontMetrics();
            stringWidth = metrics.stringWidth(text);
            stringHeight = metrics.getHeight();
            fontSize++;
        } while (stringHeight < height && stringWidth < width);
        return fontSize - 2;
    }

    private void start(){
        if(player1.getText().isEmpty() || player2.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Jména nesmí být prázdná!",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        else if(player1.getText().length() > 15 || player2.getText().length() > 15){
            JOptionPane.showMessageDialog(null,
                    "Jméno nesmí být delší než 15 znaků!",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try{
            int p1TimeInt = Integer.parseInt(p1Time.getText());
            int p2TimeInt = Integer.parseInt(p2Time.getText());
            if(p1TimeInt < 1 || p2TimeInt < 1 || p1TimeInt > 10 || p2TimeInt > 10){
                JOptionPane.showMessageDialog(null,
                        "Čas musí být celé číslo v rozsahu 1 - 10 min!",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }


        }catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(null,
                    "Čas musí být celé číslo v rozsahu 1 - 10 min!",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }


        try{
            int p1IncrementInt = Integer.parseInt(p1Increment.getText());
            int p2IncrementInt = Integer.parseInt(p2Increment.getText());

            if(p1IncrementInt < 0 || p2IncrementInt < 0 || p1IncrementInt > 10 || p2IncrementInt > 10){
                JOptionPane.showMessageDialog(null,
                        "Increment musí být celé číslo v rozsahu 0 - 10 sekund!",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

        }catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(null,
                    "Increment musí být celé číslo v rozsahu 0 - 10 sekund!",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }


        int[] increment;
        int[] time;
        if(timer.isSelected()){
            time = new int[]{Integer.parseInt(p1Time.getText())*60, Integer.parseInt(p2Time.getText())*60};
            increment = new int[]{Integer.parseInt(p1Increment.getText()), Integer.parseInt(p2Increment.getText())};
        }
        else{
            time = new int[]{-1,-1};
            increment = new int[]{0,0};
        }

        //Hrac proti hraci
        int type = 0;
        if(PvCR.isSelected())
            type = 1;
        else if(PvCRL.isSelected())
            type = 2;


        //Vytvoreni noveho okna s hrou
        new GameWindow(player1.getText(),player2.getText(),time ,increment,type,frame.getExtendedState() == JFrame.MAXIMIZED_BOTH);


        frame.dispose();

    }


    private void about(){
        JFrame frame = new JFrame("O aplikaci - Roman Pejs - A22B0197P");
        frame.setSize((int) (this.getWidth()/1.5), (int) (this.getHeight()/1.2));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);

        frame.setContentPane(new AboutPanel());

        frame.setVisible(true);

    }



}
