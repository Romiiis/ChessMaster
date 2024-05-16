import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Trida reprezentuje sachovnici
 * - Vytvori hrace
 * - Vykresli sachovnici
 * - Vykresli figurky
 *
 * @author Roman Pejs
 */
public class Board{



    //Pole sachovnice
    private AFigure[][] gameField = new AFigure[8][8];


    //Velikost jednoho policka
    private int fieldSize = 0;

    //Panel, na kterem se sachovnice vykresluje
    private  JPanel panel;


    //Levy horni roh sachovnice
    private  Point2D corner;

    //Instance hracu
    private  Player PWhite,PBlack;

    //Souradnice stareho a noveho pohybu
    private Point[] old_a_new = new Point[2];

    //Zacatecni cas
    private long startTime;

    //Jestli probiha promovani
    boolean promoting = false;

    //Instance hry
    public Game game;

    int turn = 0;

    boolean PvP = true;

    boolean end = false;


    /**
     * Konstruktor sachovnice - vytvori hrace
     */
    public Board(JPanel panel, Game game, String p1, String p2, int[] time, int[] increment,int enemyType){

        corner = new Point(0,0);
        this.game = game;
        this.panel = panel;
        PWhite = new Player(PLAYERCOLOR.WHITE, this,p1,time[0],increment[0]);
        PWhite.setTurn(true);

        if(enemyType == 1 || enemyType == 2) PvP = false;
        PBlack = new Player(PLAYERCOLOR.BLACK, this,p2,time[1],increment[1],enemyType);

        startTime = System.currentTimeMillis();
        turn++;
        System.out.println();
        System.out.println("Na tahu hráč " + getTurnPlayer().name +" : " + (((turn)/2)+1));
        System.out.println();


        if(time[0] != -1){
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    //Kazdou sekundu se snizi cas
                    getTurnPlayer().timeSeconds--;
//                System.out.println(getTurnPlayer().timeSeconds + " " + getTurnPlayer().name);
                    if(getTurnPlayer().timeSeconds < 0){
                        timer.cancel();
                        timer.purge();

                        new EndWindow("Čas vypršel",getTurnPlayer().getEnemy(),game);
                        System.out.println("Konec hry - vypršel čas");
                        System.out.println("Vítěz: " + getTurnPlayer().getEnemy().name);
//                        end = true;
                    }
                    if(end){
                        timer.cancel();
                        timer.purge();
                    }

                    getPanel().repaint();

                }
            }, 1000, 1000);
        }




    }


    /**
     * Toto vytvori pouze kopii Hraci plochy
     */
    public Board(Board board){

        this.gameField = board.gameField;


    }






    /**
     * Zmeni hrace, ktery je na tahu
     */
    public void changeTurn() {
        getTurnPlayer().addMoveTime(System.currentTimeMillis()/1000.0 - startTime/1000.0);
        getTurnPlayer().timeSeconds += getTurnPlayer().increment;
        PWhite.setTurn(!PWhite.isTurn());
        PBlack.setTurn(!PBlack.isTurn());


        startTime = System.currentTimeMillis();




        if(PBlack.isTurn() && !promoting && !PvP && !end)PBlack.cpu.makeMove();

        System.out.println();
        System.out.println("Na tahu hráč " + getTurnPlayer().name +" : " + (((turn)/2)+1));
        System.out.println();
//        System.out.println(createFEN());
        turn ++;


    }


    /**
     * Ziskani vsech hracu
     * @return - pole hracu
     */
    public Player[] getPlayers(){
        return new Player[]{PWhite, PBlack};
    }



    //-----------------------------//
    //-------Správa figurek--------//
    //-----------------------------//


    /**
     * Vraci instanci figurky na pozici x,y
     * @param x - x-ova souradnice
     * @param y - y-ova souradnice
     * @return instance figurky
     */
    public AFigure getFigureOn(int x, int y){
        return gameField[x][y];
    }

    /**
     * Nastavi figurku na pozici x,y
     * @param x - x-ova souradnice
     * @param y - y-ova souradnice
     * @param figure - instance figurky
     */
    public void setFigureOn(int x, int y, AFigure figure){
        gameField[x][y] = figure;
    }

    /**
     * Odstrani figurku na pozici x,y
     * @param x - x-ova souradnice
     * @param y - y-ova souradnice
     */
    public void removeFigureOn(int x, int y){
        gameField[x][y] = null;
    }






    //-----------------------------//
    //--------Vykreslovani---------//
    //-----------------------------//


    /**
     * Vykresleni sachovnice
     * @param g2 - graficky kontext
     * @param panWidth - sirka panelu
     * @param panHeight - vyska panelu
     */
    private void paintBoard(Graphics2D g2, int panWidth, int panHeight) {

        //Ulozeni transformace
        AffineTransform save = g2.getTransform();



        //velikost sachovnice (min z obou stran)
        int size = (int) Math.min(3*panWidth/4.0, panHeight);




        //Velikost cele plochy sachovnice + ramecek
        int deskSize = size;


        //Presunuti do v teto pozici bylo 0,0
        AffineTransform at = g2.getTransform();
        at.translate(panWidth/2.0 - size/2.0, panHeight/2.0 - size/2.0);
        g2.setTransform(at);


        //Vykresleni "Rámu"
        g2.setColor( new Color(61, 56, 56));
        g2.fillRect(0,0,size,size);

        //Zmenseni sachovnice o 1/8 - aby byl "Rám" videt
        size -= size/8.0;


        //velikost jednoho policka sachovnice - zaokrouhleno nahoru
        fieldSize = (int) Math.ceil(size/8.0);


        //Posunuti o 1/16 velikosti sachovnice
        at.translate(deskSize/16.0,deskSize/16.0);
        g2.setTransform(at);



        //Vytvoreni transformace inverzni a vypocteni levyho horniho rohu sachovnice
        try {
            AffineTransform inv = at.createInverse();
            int x = (int) Math.abs(inv.transform(new Point(0, 0), null).getX());
            int y = (int) Math.abs(inv.transform(new Point(0, 0), null).getY());
            corner.setLocation(x, y);

        }catch (NoninvertibleTransformException e){
            System.out.println(e.getMessage());
        }


        //Vykrelovani sachovnice
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {


                //Pokud je soucet indexu sudý, vykresli se svetle hnede (bile) policko jinak tmave hnede (cerna)
                if ((i + j) % 2 == 0) {
                    g2.setColor(new Color(238,238,210));
                } else {

                    g2.setColor(new Color(118,150,86));
                }




                //Vykresleni policka
                g2.fillRect( i * fieldSize, j * fieldSize, fieldSize, fieldSize);

                //Vykresleni "Rámecku kolem policka"
                g2.setColor(Color.BLACK);
                g2.drawRect(i * fieldSize, j * fieldSize, fieldSize, fieldSize);
            }


        }

        //Vykresleni cisel a pismen
        paintNumbers(g2, size);

        //Obnoveni transformace
        g2.setTransform(save);


    }

    /**
     * Vykresleni cisel a pismen
     * @param g2 - graficky kontext
     * @param size - velikost sachovnice
     */
    private void paintNumbers(Graphics2D g2, int size){


        //Stringy s pismeny a cisly
        final String s = "ABCDEFGH";
        final String n = "87654321";


        //Nastaveni fontu
        Font font = new Font("serif", Font.BOLD, size/20);
        g2.setFont(font);



        for (int j = 0; j < 8; j++) {

            AffineTransform save = g2.getTransform();

            //Ziskani jednotliveho cisla podle indexu
            String partN = String.valueOf(n.charAt(j));

            //rozdeleni stringu na jednotlive pismena
            String partS = String.valueOf(s.charAt(j));

            //Nastaveni barvy
            g2.setColor(Color.WHITE);


            //Souradnice pro cisla:

            //Vycisleni souradnic: centerX + pulka sirky textu - polovina velikosti policka
            int x = g2.getFontMetrics().stringWidth(partN) / 2 - fieldSize / 2;
            //Vycisleni souradnic: centerY + j* velikosti policka + polovina policka + ctvrtina
            int y = j * fieldSize + fieldSize / 2 + g2.getFontMetrics().getHeight() / 4;


            //Souadnice pro pismena:

            //Vycisleni souradnic: centerX + i * velikosti policka + polovina policka - polovina sirky textu
            int x2 = j * fieldSize + fieldSize/2 - g2.getFontMetrics().stringWidth(partS)/2;

            //Vycisleni souradnic: centerY + 8* velikosti policka + polovina policka
            int y2 = (int) (8*fieldSize+ fieldSize/2.5);


            //Vykresleni normalnich cisel
            g2.drawString(partN, x, y);

            //Vykresleni normalnich pismen
            g2.drawString(partS,x2,y2);


            //Vykresleni rotovanych cisel
            g2.rotate(Math.toRadians(180), 4 * fieldSize, 4 * fieldSize);

            partN = String.valueOf(n.charAt(7 - j));
            partS = String.valueOf(s.charAt(7-j));

            g2.drawString(partN, x, y);
            g2.drawString(partS,x2,y2);

            g2.setTransform(save);




        }


    }

    /**
     * Vykresleni figur
     * @param g - graficky kontext
     */
    public void paintFigures(Graphics2D g){

        //Prioritni vykresleni figurky, ktere jsou prave presouvany
        ArrayList<AFigure> priorFigures = new ArrayList<>();

        for(AFigure[] figures : gameField){
            for(AFigure figure : figures){
                if(figure != null && figure.isAlive()) {
                    if(figure.isDragged() || figure.animating) priorFigures.add(figure);
                    else figure.paint(g);
                }


            }
        }


        priorFigures.forEach(e -> e.paint(g));

    }

    /**
     * Vykresleni sachovnice a figur
     * @param g2 - graficky kontext
     * @param panWidth - sirka panelu
     * @param panHeight - vyska panelu
     */
    public void paint(Graphics2D g2, int panWidth, int panHeight){


        paintBoard(g2, panWidth, panHeight);
        paintOldMoves(g2);
        paintCheck(g2);
        paintFigures(g2);

    }


    /**
     * Vykresleni jaky pohyb byl proveden
     * @param g2 Graficky kontext
     */
    public void paintOldMoves(Graphics2D g2){

        if((old_a_new[0] != null || old_a_new[1] != null)) {

            g2.setColor(new Color(34, 255, 0, 98));

            Point oldCoord = new Point(1 + (int) (corner.getX() + old_a_new[0].x * fieldSize), (int) (corner.getY() + old_a_new[0].y * fieldSize));
            Point newCoord = new Point(1 + (int) (corner.getX() + old_a_new[1].x * fieldSize), (int) (corner.getY() + old_a_new[1].y * fieldSize));
            g2.fillRect(oldCoord.x, oldCoord.y, fieldSize, fieldSize);
            g2.fillRect(newCoord.x, newCoord.y, fieldSize, fieldSize);
        }




    }


    /**
     * Vykresleni Šachu a vsech figurek, ktere ho ohrozuji, ktere mohou zabranit sachu
     * @param g2 Graficky kontext
     */
    public void paintCheck(Graphics2D g2){


        King king = getTurnPlayer().getKing();

        Player enemy;
        Player[] players = getPlayers();

        if(getTurnPlayer() == players[0]) enemy = players[1];
        else enemy = players[0];

        if(king.isCheck()){
            Point coords = new Point(1+(int)(corner.getX() +king.getPos().x* fieldSize), (int) (corner.getY() + king.getPos().y * fieldSize));
                g2.setColor(new Color(255, 0, 0, 255));
                g2.fillRect(coords.x,coords.y, fieldSize, fieldSize);



                AFigure[] figures = enemy.getFiguresPack();

                for(AFigure figure : figures){

                    if(figure != null && !(figure instanceof King)){
                        if(figure.attackingKing){
                            Point coords2 = new Point(1+(int)(corner.getX() +figure.getPos().x* fieldSize), (int) (corner.getY() + figure.getPos().y * fieldSize));
                            g2.setColor(new Color(255, 0, 0, 255));
                            g2.setStroke(new BasicStroke((float) (fieldSize/25.0)));
                            g2.drawRect(coords2.x,coords2.y, fieldSize, fieldSize);
                        }
                    }
                }


                Player player = getTurnPlayer();
                for(AFigure fig : player.getFiguresPack()){
                    if(fig.getValidMoves(true).length > 0){
                        Point coords2 = new Point(1+(int)(corner.getX() +fig.getPos().x* fieldSize), (int) (corner.getY() + fig.getPos().y * fieldSize));
                        g2.setColor(new Color(0, 243, 255, 255));
                        g2.setStroke(new BasicStroke((float) (fieldSize/30.0)));
                        g2.drawRect(coords2.x,coords2.y, fieldSize, fieldSize);
                    }
                }


        }







    }


    /**
     * Vykresli Validni tahy a utoky pro danou figurku
     *
     * @param g2d - graficky kontext
     */
    public void drawValid(Graphics2D g2d,AFigure figure) {

        Point[] points = figure.getValidMoves(true);

        Point[] enpassant;
        if(figure instanceof Pawn) enpassant = ((Pawn) figure).enPassantMoves();
        else enpassant = new Point[0];


        //Ziskani souradnic levého horního rohu hraci plochy
        Point2D corner = getCorner();

        //Ziskani velikosti jednoho policka
        int size = getFieldSize();
        int R;

        for(Point pass: enpassant){

            R = size / 3;
            Ellipse2D e = new Ellipse2D.Double(corner.getX() + pass.x * size + size / 2.0 - R / 2.0, corner.getY() + pass.y * size + size / 2.0 - R / 2.0, R, R);

            R = R / 2;
            Ellipse2D cut = new Ellipse2D.Double(corner.getX() + pass.x * size + size / 2.0 - R / 2.0, corner.getY() + pass.y * size + size / 2.0- R / 2.0, R, R);

            Area at = new Area(e);
            at.subtract(new Area(cut));

            g2d.setColor(Color.RED);
            g2d.fill(at);

        }



        //Vykresleni moznych tahu
        for (Point point : points) {


            int x = (int) point.getX();
            int y = (int) point.getY();
            R = size / 3;



            if(getFigureOn(point.x, point.y) == null){




                g2d.setColor(new Color(107, 103, 103, 150));
                g2d.fillOval((int) (corner.getX() + x * size + size / 2 - R / 2), (int) (corner.getY() + y * size + size / 2 - R / 2), R, R);



            }

            //Vykresleni rozhodnuti o rosade
            else if(((figure instanceof King && this.getFigureOn(x,y) instanceof Rook)
                    || (figure instanceof Rook && this.getFigureOn(x,y) instanceof King))&& figure.getPlayer().getColor() == this.getFigureOn(x,y).getPlayer().getColor()){


                g2d.setColor(Color.GREEN);
                g2d.fillOval((int) (corner.getX() + x * size + size / 2 - R / 2), (int) (corner.getY() + y * size + size / 2 - R / 2), R, R);
            }
            else if((this.getFigureOn(x, y) != null && this.getFigureOn(x, y).getPlayer().getColor() != figure.getPlayer().getColor())){

                Ellipse2D e = new Ellipse2D.Double(corner.getX() + x * size + size / 2.0 - R / 2.0, corner.getY() + y * size + size / 2.0 - R / 2.0, R, R);

                R = R / 2;
                Ellipse2D cut = new Ellipse2D.Double(corner.getX() + x * size + size / 2.0 - R / 2.0, corner.getY() + y * size + size / 2.0 - R / 2.0, R, R);

                Area at = new Area(e);
                at.subtract(new Area(cut));

                g2d.setColor(Color.RED);
                g2d.fill(at);
            }

        }



    }





    /**
     * Udela promotion pesaka
     * @param pawn - pesak
     * @param figure - figura na kterou se pesak promeni
     */
    public void promotion(Pawn pawn, AFigure figure) {

        Player pa = pawn.getPlayer();
        Point pos = pawn.getPos();

        pa.removeFigure(pawn);
        setFigureOn(pos.x, pos.y, null);

        pa.addFigure(figure);
        setFigureOn(pos.x, pos.y, figure);

        pa.getEnemy().getKing().checkState();


    }



    /**
     * Nastaveni stare a nove pozice
     * @param old_a_new - pole souradnic
     */
    public void setOld_a_new(Point[] old_a_new) {
        this.old_a_new = old_a_new;
    }

    /**
     * Ziskani instance panelu
     * @return - panel
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * Vraci velikost sachovnice
     * @return velikost sachovnice
     */
    public int getFieldSize() {
        return fieldSize;
    }

    /**
     * Vraci roh sachovnice
     * @return roh sachovnice
     */
    public Point2D getCorner() {return corner;}

    /**
     * Zjistí zda jsou souradnice na hraci plose
     * @param x x-ova souradnice
     * @param y y-ova souradnice
     * @return true - souradnice jsou na hraci plose / false - souradnice nejsou na hraci plose
     */
    public boolean isOnBoard(int x, int y){
        return (x >= 0 && x < 8 && y >= 0 && y < 8);
    }


    /**
     * Vraci hrace, ktery je na tahu
     * @return hrac, ktery je na tahu
     */
    public Player getTurnPlayer(){
        if(PWhite.isTurn()) return PWhite;
        else return PBlack;
    }


    public String createFEN(){

        String fen = "";
        int empty = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++) {
                if (getFigureOn(j, i) == null) empty++;
                else {
                    if (empty != 0) {
                        fen += empty;
                        empty = 0;
                    }
                    fen += getFigureOn(j, i).getFEN();
                }
            }
            if(empty != 0){
                fen += empty;
                empty = 0;
            }
            if(i != 7) fen += "/";
        }



        fen += " " + getTurnPlayer().getColor().toString().toLowerCase().charAt(0);

        String castling = "";
        if(PWhite.getKing().canRosada(false))castling += "K";
        if(PWhite.getKing().canRosada(true))castling += "Q";

        if(PBlack.getKing().canRosada(false))castling += "k";
        if(PBlack.getKing().canRosada(true))castling += "q";

        if(castling.length() == 0) castling = "-";

        fen += " " + castling;
        fen += " ";
        fen += getTurnPlayer().getEnemy().enPassantforFEN();

        fen += " ";
        fen += "0";
        fen += " ";
        fen += turn;


        return fen;
    }





}

