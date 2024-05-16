import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;


/**
 * Trida reprezentuje hrace
 * - Vytvori figurky
 * - Drzi informace o figurkach
 * - Vykresluje Profil hrace
 * - Vykresluje cas hrace
 * - Vykresluje vyhozeny figurky
 *
 * @author Roman Pejs
 */
public class Player {

    //index 0 - 7 jsou pesaci
    //index 8 - 9 jsou vez
    //index 10 je kral
    //index 11 je kralovna
    //index 12 - 13 jsou streleci
    //index 14 - 15 jsou jezdci




    //Sada figur hrace
    private final ArrayList<AFigure> figuresPack = new ArrayList<>();


    //Sada vyhozenych figurek
    private final ArrayList<AFigure> outFigures = new ArrayList<>();


    public boolean isCpu = false;
    //Barva hrace
    private final PLAYERCOLOR pColor;


    //Zda je hrac na tahu
    private boolean turn;


    private boolean speaking = false;


    private String speakText = "";


    //Hracova plocha
    private final Board board;


    //Indikatory na najeti do komponenty
    public boolean mouseEntered = false;
    public boolean tableEntered = false;


    //Area pro kliknuti
    private Area profileButton;

    //Area pro stul
    private Area table;

    //Inverzni transformace pro stul
    public AffineTransform tableInverse;


    //Cas hrace
    public int timeSeconds = -1;

    //Inkrementace casu
    public int increment = 0;


    //Deklarace Timeru
    Timer timer;

    String name = "";



    //Casy tahu hrace
    private final ArrayList<Double> moveTimes = new ArrayList<>();

    ICPU cpu;


    /**
     * Konstruktor hrace
     * @param barva - barva hrace
     * @param board - hraci plocha
     */
    public Player(PLAYERCOLOR barva, Board board, String Name, int timeSeconds, int increment){

        this.pColor = barva;
        this.board = board;

        //Nastaveni tahu
        turn = pColor.equals(PLAYERCOLOR.WHITE);
        this.name = Name;


        if(timeSeconds >0)
            this.timeSeconds = timeSeconds;
        this.increment = increment;


        //Vytvoreni figurek
        createFigures();
        //test();

        //enPTest();
        //promotionTest();
    }

    private void enPTest(){
        if(pColor == PLAYERCOLOR.BLACK){
            AFigure pawn1 = new Pawn(1,4,this);
            figuresPack.add(pawn1);
            board.setFigureOn(1,4,pawn1);
            pawn1.firstMove = false;

            AFigure king = new King(4,0,this);
            figuresPack.add(king);
            board.setFigureOn(4,0,king);



        }
        else{

            AFigure pawn1 = new Pawn(0,6,this);
            figuresPack.add(pawn1);
            board.setFigureOn(0,6,pawn1);

            AFigure king = new King(4,7,this);
            figuresPack.add(king);
            board.setFigureOn(4,7,king);
        }
    }
    public Player(PLAYERCOLOR barva, Board board, String Name, int timeSeconds, int increment, int enemyType){
        this(barva,board,Name,timeSeconds,increment);

        if(enemyType == 1){
            cpu = new RandomCPU(this,board.getPanel());
            isCpu = true;
        }
        if(enemyType == 2){
            cpu = new StockFishCPU(this,board.getPanel());
            isCpu = true;
        }

    }




    /**
     * Pridani casu tahu do seznamu
     * @param time - cas tahu
     */
    public void addMoveTime(double time){
        //Zaokrouhleni casu na 2 desetinna mista
        double roundedValue = Math.round(time * 100.0) / 100.0;

        //Pridani casu do seznamu
        moveTimes.add(roundedValue);


    }

    private void test(){

        if(pColor.equals(PLAYERCOLOR.BLACK)) {
            AFigure rook1 = new Rook(0, 0, this);
            figuresPack.add(rook1);
            board.setFigureOn(0, 0, rook1);

            AFigure rook2 = new Rook(7, 0, this);
            figuresPack.add(rook2);
            board.setFigureOn(7, 0, rook2);

            AFigure king = new King(4, 0, this);
            figuresPack.add(king);
            board.setFigureOn(4, 0, king);

            AFigure bishop1 = new Bishop(2, 0, this);
            figuresPack.add(bishop1);
            board.setFigureOn(2, 0, bishop1);

            AFigure bishop2 = new Bishop(2, 3, this);
            figuresPack.add(bishop2);
            board.setFigureOn(2, 3, bishop2);

            AFigure pawn1 = new Pawn(0, 1, this);
            figuresPack.add(pawn1);
            board.setFigureOn(0, 1, pawn1);

            AFigure pawn2 = new Pawn(1, 1, this);
            figuresPack.add(pawn2);
            board.setFigureOn(1, 1, pawn2);

            AFigure pawn3 = new Pawn(2, 1, this);
            figuresPack.add(pawn3);
            board.setFigureOn(2, 1, pawn3);

            AFigure pawn4 = new Pawn(3, 1, this);
            figuresPack.add(pawn4);
            board.setFigureOn(3, 1, pawn4);

            AFigure pawn5 = new Pawn(5, 1, this);
            figuresPack.add(pawn5);
            board.setFigureOn(5, 1, pawn5);

            AFigure pawn6 = new Pawn(6, 1, this);
            figuresPack.add(pawn6);
            board.setFigureOn(6, 1, pawn6);

            AFigure pawn7 = new Pawn(7, 1, this);
            figuresPack.add(pawn7);
            board.setFigureOn(7, 1, pawn7);

            AFigure pawn8 = new Pawn(4, 3, this);
            figuresPack.add(pawn8);
            board.setFigureOn(4, 3, pawn8);

            pawn8.firstMove = false;

            AFigure queen = new Queen(3, 0, this);
            figuresPack.add(queen);
            board.setFigureOn(3, 0, queen);

            AFigure knight1 = new Knight(5, 2, this);
            figuresPack.add(knight1);
            board.setFigureOn(5, 2, knight1);

            AFigure knight2 = new Knight(2, 2, this);
            figuresPack.add(knight2);
            board.setFigureOn(2, 2, knight2);


        }
        else{

            AFigure king = new King(4, 7, this);
            figuresPack.add(king);
            board.setFigureOn(4, 7, king);
//
            AFigure rook1 = new Rook(0, 7, this);
            figuresPack.add(rook1);
            board.setFigureOn(0, 7, rook1);
//
            AFigure rook2 = new Rook(7, 7, this);
            figuresPack.add(rook2);
            board.setFigureOn(7, 7, rook2);
//
            AFigure bishop1 = new Bishop(2, 7, this);
            figuresPack.add(bishop1);
            board.setFigureOn(2, 7, bishop1);

            AFigure bishop2 = new Bishop(2, 4, this);
            figuresPack.add(bishop2);
            board.setFigureOn(2, 4, bishop2);
//
            AFigure pawn1 = new Pawn(0, 6, this);
            figuresPack.add(pawn1);
            board.setFigureOn(0, 6, pawn1);

            AFigure pawn2 = new Pawn(1, 6, this);
            figuresPack.add(pawn2);
            board.setFigureOn(1, 6, pawn2);

            AFigure pawn3 = new Pawn(2, 6, this);
            figuresPack.add(pawn3);
            board.setFigureOn(2, 6, pawn3);

            AFigure pawn4 = new Pawn(3, 6, this);
            figuresPack.add(pawn4);
            board.setFigureOn(3, 6, pawn4);

            AFigure pawn5 = new Pawn(6, 6, this);
            figuresPack.add(pawn5);
            board.setFigureOn(6, 6, pawn5);

            AFigure pawn6 = new Pawn(7, 6, this);
            figuresPack.add(pawn6);
            board.setFigureOn(7, 6, pawn6);

            AFigure pawn7 = new Pawn(4, 5, this);
            figuresPack.add(pawn7);
            board.setFigureOn(4, 5, pawn7);

            pawn7.firstMove = false;
//
            AFigure queen = new Queen(3, 7, this);
            figuresPack.add(queen);
            board.setFigureOn(3, 7, queen);

            AFigure knight1 = new Knight(5, 5, this);
            figuresPack.add(knight1);
            board.setFigureOn(5, 5, knight1);

            AFigure knight2 = new Knight(2, 5, this);
            figuresPack.add(knight2);
            board.setFigureOn(2, 5, knight2);


        }




    }

    private void promotionTest(){
        if(pColor == PLAYERCOLOR.WHITE){
            AFigure king = new King(4, 0, this);
            figuresPack.add(king);
            board.setFigureOn(4, 0, king);

            AFigure pawn = new Pawn(0, 1, this);
            figuresPack.add(pawn);
            board.setFigureOn(0, 1, pawn);
        }
        else{
            AFigure king = new King(1, 7, this);
            figuresPack.add(king);
            board.setFigureOn(1, 7, king);

            AFigure pawn = new Pawn(0, 6, this);
            figuresPack.add(pawn);
            board.setFigureOn(0, 6, pawn);

        }
    }


    /** Vytvoreni figurek hrace */
    private void createFigures(){

        int frontLine = 1;
        int backLine = 0;


        if(pColor.equals(PLAYERCOLOR.WHITE)){
            frontLine = 6;
            backLine = 7;
        }

        for(int i = 0;i<16;i++){


            if(i < 8) {

                AFigure pesec = new Pawn(i, frontLine, this);
                figuresPack.add(pesec);
                board.setFigureOn(i, frontLine, pesec);

            }

            if(i > 7){
                switch (i) {
                    case 8 -> {
                        AFigure vez = new Rook(0, backLine, this);
                        figuresPack.add(vez);
                        board.setFigureOn(0, backLine, vez);
                    }
                    case 9 -> {
                        AFigure vez = new Rook(7, backLine, this);
                        figuresPack.add(vez);
                        board.setFigureOn(7, backLine, vez);
                    }
                    case 10 -> {
                        AFigure kralovna = new Queen(3, backLine, this);
                        figuresPack.add(kralovna);
                        board.setFigureOn(3, backLine, kralovna);
                    }
                    case 11 -> {
                        AFigure kral = new King(4, backLine, this);
                        figuresPack.add(kral);
                        board.setFigureOn(4, backLine, kral);
                    }
                    case 12 -> {
                        AFigure strelec = new Bishop(2, backLine, this);
                        figuresPack.add(strelec);
                        board.setFigureOn(2, backLine, strelec);
                    }
                    case 13 -> {
                        AFigure strelec = new Bishop(5, backLine, this);
                        figuresPack.add(strelec);
                        board.setFigureOn(5, backLine, strelec);
                    }
                    case 14 -> {
                        AFigure jezdec = new Knight(1, backLine, this);
                        figuresPack.add(jezdec);
                        board.setFigureOn(1, backLine, jezdec);
                    }
                    case 15 -> {
                        AFigure jezdec = new Knight(6, backLine, this);
                        figuresPack.add(jezdec);
                        board.setFigureOn(6, backLine, jezdec);
                    }
                }
            }

        }
    }



    /**
     * Odebrani figurky z hracovy sady
     * @param figure - figurka k odebrani
     */
    public void removeFigure(AFigure figure){

        figuresPack.remove(figure);
    }


    /**
     * Pridani figurky do hracovy sady
     * @param figure - figurka k pridani
     */
    public void addFigure(AFigure figure){
        figuresPack.add(figure);
    }




    /**
     * Vykresleni ikony hrace a vsechny jeji nalezitosti
     * @param g Graficky kontext
     */
    public void paint(Graphics2D g){

        //Bod, ktery urcuje kde bude pocatek vykreslovani
        Point point;

        //Sirka je 1/20 panelu
        double w = board.getPanel().getWidth()/20.0;

        //Barva podle barvy Hrace
        Color color = pColor.getColor();

        //Obrys cerny
        Color borderColor = Color.BLACK;

        //offset od okraje
        int offsetX = (int) (board.getPanel().getWidth()/30.0);
        int offsetY = (int) (board.getPanel().getHeight()/30.0);

        if(pColor.equals(PLAYERCOLOR.WHITE)) point = new Point((offsetX), (int) ((board.getPanel().getHeight() - offsetY) - w));
        else point = new Point((int) (board.getPanel().getWidth() - offsetX - w ), (int) (offsetY + 2*w));



        //Pripraveni area a path2d
        Area area = new Area();
        Path2D path2D = new Path2D.Double();

        //Vytvoreni "AVATARA" hráče
        path2D.moveTo(point.x,point.y);
        path2D.lineTo(point.x + w,point.y);
        path2D.curveTo(point.x + w,point.y, point.x + w/2.0,point.y - w, point.x,point.y);

        double R = w/2.0;
        path2D.append(new Ellipse2D.Double((point.x + w/2.0) - R/2.0, point.y - w - R/4.0, R, R), false);
        path2D.closePath();


        //Nastaveni antialiasingu
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);




        //Ramecek pro avatara
        Rectangle rectangle = path2D.getBounds();
        rectangle.setLocation((int) (rectangle.x - w/4.0), (int) (rectangle.y - w/4.0));
        rectangle.width += w/2;
        rectangle.height += w/2;



        //Nastaveni textu
        g.setFont(new Font("Arial", Font.BOLD, (int) (w/3.5)));
        g.setColor(Color.BLACK);
        FontMetrics metrics = g.getFontMetrics(g.getFont());

        //Spocitani lokace a vykresleni
        int x = (int) (rectangle.x + ((rectangle.getWidth() - metrics.stringWidth(name)) / 2));
        int y = (int) (rectangle.y+rectangle.getHeight() + ((metrics.getHeight()) / 2) + metrics.getAscent());
        g.drawString(name, x, y);


        //Ramecek slouzi jako tlacitko pro graf
        profileButton = new Area(rectangle);

        Font save;

        //Pokud neni mys uvnitr, klasicka barva
        if(!mouseEntered){
            g.setColor(new Color(227, 220, 220, 255));
            g.setStroke(new BasicStroke(3));

            save = g.getFont();
        }
        //Pokud je tak sediva aby bylo poznat, ze je to tlacitko
        else{

            //Tooltip pro napovedu
            String toolTipText = "Otevřít graf tahů";
            Font font = Font.decode("Arial-BOLD-12");

            //Vypocty pro font

            Rectangle2D r2d = g.getFontMetrics(font).getStringBounds(toolTipText, g);
            font = font.deriveFont((float)(font.getSize2D() * profileButton.getBounds2D().getWidth()/r2d.getWidth()));

             save = g.getFont();

            g.setFont(font);
            g.setColor(Color.BLACK);

            metrics = g.getFontMetrics(g.getFont());

            //Pozice
            x = (int) (profileButton.getBounds2D().getX());
            y = (int) (profileButton.getBounds2D().getY() - metrics.getHeight() + metrics.getAscent());

            //Vykresleni
            g.drawString(toolTipText, x, y);

            g.setColor(new Color(131, 129, 129, 200));
            g.setStroke(new BasicStroke(1));

        }

        //Vykresleni
        g.fill(profileButton);
        g.draw(profileButton);

        //Nastaveni ramecku na zaklade tahu
        if(turn){
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(3));
            g.draw(rectangle);
        }
        else{

            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1));
            g.draw(rectangle);


        }


        //Kompletace avatara
        area.add(new Area(path2D));

        g.setPaint(borderColor);
        g.setStroke(new BasicStroke(3));

        g.draw(area);

        g.setPaint(color);
        g.fill(area);


        //Reset tloustky
        g.setStroke(new BasicStroke(1));


        g.setFont(save);
        //Pokud je speaking nastaveno na true, ukaz mluvici bublinu
        if(speaking) showBubble(g,rectangle);

    }


    /**
     * Getter na "Tlacitko"
     * @return Area instance tlacitka
     */
    public Area getButton(){
        return profileButton;
    }



    Timer tm;
    /**
     * Metoda, ktera sepne mechanismus zobrazeni bubliny
     * @param text text, ktery se ma zobrazit
     */
    public void say(String text){

        //Nastav text, kterej se ma zobrazit
        this.speakText = text;


        if(tm != null) tm.cancel();
        //Nastav timer, ktery bude zobrazovat bublinu
        tm = new Timer();

        //Nastav cas, kdy se bublina zobrazila
        long startTime = System.currentTimeMillis();

        //Nastav, ze se bublina zobrazuje
        speaking = true;

        //Nastav timer, ktery bude zobrazovat bublinu
        tm.schedule(new TimerTask() {
            @Override
            public void run() {

                //Znovu vykresli panel
                board.getPanel().repaint();

                //Pokud je cas vetsi nez 2 sekundy, zrus timer a nastav, ze se bublina uz nezobrazuje
                if(System.currentTimeMillis() - startTime > 2000){
                    tm.cancel();
                    speaking = false;
                }

            }
        }, 0, 20);


    }


    /**
     * Metoda, ktera zobrazi bublinu
     * @param g2d graficky kontext
     * @param rectangle Ramecek pro avatara (pro vypocet pozice)
     */
    private void showBubble(Graphics2D g2d, Rectangle rectangle){

        //Směr bubliny
        int d = 1;
        if(pColor.equals(PLAYERCOLOR.BLACK)) d = -1;

        //Vypocet stredu avatara
        Point profileImageCenter = new Point((int) (rectangle.x + rectangle.getWidth() / 2.0), (int) (rectangle.y + rectangle.getHeight() / 2.0));


        //Vypocet textu
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
        double tW = metrics.stringWidth(speakText);
        double tH = metrics.getHeight();

        //Vypocet velikosti bubliny
        int w = rectangle.width;
        int h = rectangle.height;

        //Vypocet pozice bubliny
        profileImageCenter.y -= h/8;

        //Vypocet pozice textu podle smeru
        int sx,sy;

        RoundRectangle2D rect;
        if(d == -1){
            sx = (int) (((profileImageCenter.x + d*w)+d*tW*2) -d*tW/2);
            sy = (int) (profileImageCenter.y + tH/4);
            rect = new RoundRectangle2D.Double((profileImageCenter.x + d*w)+d*tW*2, profileImageCenter.y-h/4.0, tW*2, h/2.0, 20, 20);
        }
        else{
            rect = new RoundRectangle2D.Double((profileImageCenter.x + d*w), profileImageCenter.y-h/4.0, tW*2, h/2.0, 20, 20);
            sx = (int) ((profileImageCenter.x + d*w)+tW/2);
            sy = (int) (profileImageCenter.y + tH/4);
        }


        //Vytvoreni pathu
        Path2D path2D = new Path2D.Double();
        path2D.append(rect, false);
        path2D.moveTo(profileImageCenter.x, profileImageCenter.y);
        path2D.lineTo(profileImageCenter.x + d*w, profileImageCenter.y-h/6.0);
        path2D.lineTo(profileImageCenter.x + d*w, profileImageCenter.y+h/6.0);
        path2D.lineTo(profileImageCenter.x, profileImageCenter.y);

        path2D.closePath();

        //Nastaveni barvy
        g2d.setColor(Color.LIGHT_GRAY);

        //Vykresleni
        g2d.fill(path2D);

        //Nastaveni barvy
        g2d.setColor(Color.BLACK);
        g2d.drawString(speakText, sx, sy);

        g2d.draw(path2D);


    }




    /**
     * Metoda, ktera vraci figurky, ktere jsou vyhozeny
     * @param g graficky kontext
     */
    public void paintOutFigures(Graphics2D g){

        //Ulozeni transformace
        AffineTransform save = g.getTransform();

        //Roztrideni figurek
        ArrayList<AFigure> pawns = outFigures.stream().filter(figure -> figure instanceof Pawn).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<AFigure> rooks = outFigures.stream().filter(figure -> figure instanceof Rook).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<AFigure> knights = outFigures.stream().filter(figure -> figure instanceof Knight).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<AFigure> bishops = outFigures.stream().filter(figure -> figure instanceof Bishop).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<AFigure> queens = outFigures.stream().filter(figure -> figure instanceof Queen).collect(Collectors.toCollection(ArrayList::new));

        //Vytvoreni seznamu seznamu figurek
        ArrayList<ArrayList<AFigure>> figures = new ArrayList<>();
        figures.add(pawns);
        figures.add(rooks);
        figures.add(knights);
        figures.add(bishops);
        figures.add(queens);



        //Vytvoreni oblasti "stolu" pro vyhozeni figurek
        Rectangle2D.Double tableR = new Rectangle2D.Double(0,0,10.5*((board.getFieldSize()/2.0)*0.5),5*(board.getFieldSize()));
        table = new Area(tableR);


        //Vypocet rohu "stolu"
        Point corner;
        if(pColor.equals(PLAYERCOLOR.WHITE)){
            corner = new Point(board.getFieldSize()/11, board.getFieldSize()/11);
        }
        else{
            corner = new Point((int) ((board.getPanel().getWidth()) - 5.5*((board.getFieldSize()/2)*0.4)),
                    (int) ((board.getPanel().getHeight()) - 2.1*(board.getFieldSize())));
        }


        //Vytvoreni transformace
        AffineTransform at = new AffineTransform();

        //Presunuti do pozice
        at.translate(corner.x, corner.y);

        //Zmena velikosti
        at.scale(0.4, 0.4);

        //Nastaveni transformace
        g.transform(at);


        //Vytvoreni inverse transformace
        try {
            tableInverse = at.createInverse();
        } catch (NoninvertibleTransformException e) {
            throw new RuntimeException(e);
        }

        //Vykresleni "stolu"
        g.setColor(new Color(213, 213, 213, 255));
        g.fill(table);


        //Pokud neni "stul" v oblasti mysi, tak se vykresli rozdil ceny na pozadi
        if(!tableEntered) drawPriceDiff(g);


        //Nastaveni clipu
        g.setClip(table);

        //Vytvoreni "policek"
        int gap = ((board.getFieldSize()));

        for(int i = 1;i<5;i++){
            g.setColor(new Color(54, 54, 54));
            g.drawLine(0,gap*i,board.getPanel().getWidth(),gap*i);
        }

        g.setClip(null);

        //Vykresleni "stolu"
        g.setStroke(new BasicStroke((float) (board.getFieldSize()/15.0)));
        g.setColor(Color.BLACK);
        g.draw(table);
        g.setStroke(new BasicStroke(1));





        //Vykresleni figurek
        int counterRow = 0;

        for(ArrayList<AFigure> type : figures){
            int counterCol = 0;


            for(AFigure figure : type){

                figure.drawX = (int) (counterCol*(board.getFieldSize()/2)*0.5);
                figure.drawY = counterRow *(board.getFieldSize());


                counterCol++;
                figure.paint(g);

            }

            counterRow++;

        }


        //Pokud je "stul" v oblasti mysi, tak se vykresli rozdil ceny na popredi
        if(tableEntered) drawPriceDiff(g);

        //Vykresleni hodin
        drawHours((int) (tableR.getWidth()),g);

        //Obnoveni transformace
        g.setTransform(save);
    }


    /**
     * Metoda, ktera vykresli rozdil ceny na pozadi nebo popredi
     * @param g graficky kontext
     */
    private void drawPriceDiff(Graphics2D g){

        //Ziskani celkove ceny vyhozenych figurek
        int thisPrice = getPrice();
        int enemyPrice = getEnemy().getPrice();

        //Vypocet rozdilu
        int price = thisPrice - enemyPrice;


        //Nastaveni alfy - pruhlednost
        int alpha = 150;
        if(tableEntered) alpha = 255;


        //Defaulni hodnota
        String priceS = "0";

        //Pokud je rozdil vetsi nez 0, tak se prida znak +
        if(price >0) priceS = "+" + price;
        //Pokud je rozdil mensi nez 0, tak se prida znak -
        else if(price <0) priceS = "-" + Math.abs(price);

        //Nastaveni barvy
        if(price > 0) g.setColor(new Color(16, 133, 16, alpha));
        else if(price < 0) g.setColor(new Color(114, 13, 13, alpha));
        else g.setColor(new Color(99, 99, 100, alpha));

        //Spocitani velikosti fontu z sirky
        Font font = MenuPanel.getFontFromWidth((int) (table.getBounds2D().getWidth()*1.8), priceS, g);
        g.setFont(font);

        //Ziskani font metrics
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(priceS);
        int h = fm.getHeight();


        //Vykresleni textu
        g.drawString(priceS, (int) (table.getBounds2D().getWidth()/2 - w/2),+ (int) (table.getBounds2D().getHeight()/2 + h/4));


    }

    /**
     * Metoda, ktera vykresli hodiny
     * @param width sirka
     * @param g graficky kontext
     */
    private void drawHours(int width,Graphics2D g){

        //Ulozeni transformace
        AffineTransform save = g.getTransform();

        //Vytvoreni "hodin" ramecek
        Rectangle2D.Double hourFrame = new Rectangle2D.Double(0,0,width,board.getFieldSize());

        //Nastaveni transformace
        if(pColor.equals(PLAYERCOLOR.WHITE)){
            g.translate(0,5.5*board.getFieldSize());
        }
        else{
            g.translate(0,-1.5*board.getFieldSize());
        }

        //Vykresleni "hodin" ramecku
        g.setColor(new Color(213, 213, 213, 255));
        g.fill(hourFrame);
        g.setColor(Color.BLACK);
        g.draw(hourFrame);

        //Vypocet casu
        int seconds = timeSeconds%60;
        int minutes = timeSeconds/60;
        String result = String.format("%02d:%02d", minutes, seconds);
        if(timeSeconds == -1) result = "--:--";

        //Nastaveni fontu
        Font font = MenuPanel.getFontFromWidth((int) (hourFrame.getWidth()*1.8), result, g);
        g.setFont(font);

        //Ziskani font metrics
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(result);
        int h = fm.getHeight();

        //Vykresleni textu
        g.drawString(result, (int) (hourFrame.getWidth()/2 - w/2),+ (int) (hourFrame.getHeight()/2 + h/3.5));

        //Obnoveni transformace
        g.setTransform(save);
    }

    /**
     * Metoda, ktera vraci cenu vyhozenych figurek
     * @return cena vyhozenych figurek
     */
    private int getPrice(){

        //Cena vyhozenych figurek
        int price = 0;

        for(AFigure f : outFigures){

            if(f instanceof Pawn) price += 1;
            else if(f instanceof Rook) price += 5;
            else if(f instanceof Knight) price += 3;
            else if(f instanceof Bishop) price += 3;
            else if(f instanceof Queen) price += 9;
        }
        return price;

    }


    /**
     * Metoda, ktera vraci seznam vyhozenych figurek
     * @return seznam vyhozenych figurek
     */
    public ArrayList<AFigure> getOutFigs() {
        return outFigures;
    }



    /**
     * Metoda, ktera vraci area stolu
     * @return seznam vyhozenych figurek
     */
    public Area getTableArea(){
        return table;
    }

    /**
     * Metoda, ktera vraci barvu hrace
     * @return barva hrace
     */
    public PLAYERCOLOR getColor(){
        return pColor;
    }


    /**
     * Metoda vraci retezec s informacemi o hraci
     * @return String s informacemi o hraci
     */
    public String toString(){
        return name;
    }


    /**
     * Ziska protivnika hrace
     * @return protivnik hrace
     */
    public Player getEnemy(){
        //Ziskani hracu
        Player enemy;
        Player[] players = board.getPlayers();

        //Ziskani protivnika
        if(players[0].equals(this)) enemy = players[1];
        else enemy = players[0];

        //Vrat protivnika
        return enemy;
    }


    /**
     * Ziskani seznamu casu tahu
     * @return - seznam casu tahu
     */
    public ArrayList<Double> getMoveTimes(){
        return moveTimes;
    }

    /**
     * Ziskani boardu na kterem hrac hraje
     * @return - board hrace
     */
    public Board getBoard(){
        return board;
    }


    /**
     * Ziskani zda je hrac na tahu
     * @return (true) - hrac je na tahu, (false) - hrac neni na tahu
     */
    public boolean isTurn(){
        return turn;
    }


    /**
     * Nastaveni tahu hrace
     * @param turn - (true) - hrac je na tahu, (false) - hrac neni na tahu
     */
    public void setTurn(boolean turn){

        //Pokud se nastavuje danemu hraci tah, tak se spusti casovac
//        if(turn){
//
//            if(timeSeconds != -1) {
//            }
//
//
//        }
//        else{
//            if(timeSeconds != -1){
//                //Pokud existuje casovac, tak se zrusi
//                if(timer != null){
//                    timer.purge();
//                    timer.cancel();
//                    System.gc();
//
//                    //Pridani inkrementu k casu
//                    timeSeconds += increment;
//
//                    //Prekresleni okna
//                    board.getPanel().repaint();
//                }
//            }
//
//
//
//        }

        //Nastaveni tahu
        this.turn = turn;

    }


    /**
     * Ziskani sady figurek hrace
     * @return sada figurek hrace
     */
    public AFigure[] getFiguresPack(){
        return figuresPack.toArray(new AFigure[0]);
    }


    /**
     * Ziskani krále hrace
     * @return - kral hrace
     */
    public King getKing(){

        for(AFigure f : figuresPack){
            if(f instanceof King) return (King) f;
        }
        return null;

    }

    public String enPassantforFEN(){
        ArrayList<AFigure> pawns = figuresPack.stream().filter(figure -> figure instanceof Pawn).collect(Collectors.toCollection(ArrayList::new));
        for(AFigure p : pawns){
            if(((Pawn) p).enPassantPossible){
                return ((Pawn) p).getEnPassantField();
            }

        }
        return "-";
    }

}
