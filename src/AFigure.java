import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.Timer;



/**
 * Trida reprezentuje figurku
 * - vykresli podstavu figurky
 * - obsluhuje drag a drop - pohyb a pusteni figurky
 *
 * @author Roman Pejs
 */
public abstract class AFigure {



    //Indikator, ze je utok na krale
    public boolean attackingKing = false;

    //Indikator, ze je figurka v menu
    public boolean menu = false;




    //--------------------------------------------------
    //-----------------Informace o figurce--------------
    //--------------------------------------------------


    //Pozice figurky na sachovnici
    protected int x, y;

    //Instance hrace
    protected Player player;

    //Instance hraci desky
    protected Board board;

    //Boolean pro zjisteni, ze se jedna o první tah
    protected boolean firstMove = true;

    //Boolean pro zjisteni, ze je figurka ziva
    protected boolean isAlive = true;






    //--------------------------------------------------
    //-----------------Drag a Drop----------------------
    //--------------------------------------------------

    //Instance oblasti, ktera je dragovana
    protected Area Figure;

    //Zda je figurka v prubehu dragu
    private boolean isDragged;

    //Zda je figurka v prubehu animace
    public boolean animating = false;

    //Pozice dragu
    public Point2D dragPos;






    //--------------------------------------------------
    //-----------------Vykreslovani---------------------
    //--------------------------------------------------

    //Barva figurky
    protected Color color;

    private final PLAYERCOLOR pColor;
    //Barva okraje
    protected Color borderColor;

    //Offset pro vykresleni figurky
    int offset = 0;

    //Velikost okraje
    float strokeSize = 0;

    //stred figurky - pro zjednoduseni vypoctu
    int center;

    //velikost podstavy figurky
    int rectBase;


    //Transformace - inverzni - pro zjistení pozice mysi
    private AffineTransform inverseTransform;

    //Transformace - původní
    private AffineTransform saveTransform;


    //Souradnice absolutni pozice figurky
    public int drawX,drawY;

    //Velikost figurky
    protected int drawSize;




    //--------------------------------------------------
    //-----------------Animace--------------------------
    //--------------------------------------------------

    //zacatek animace
    long startTime = 0;

    //aktualni cas
    long currentTime = 0;

    //konec animace
    public int xStep, yStep;


    /**
     * Vytvoreni figurky
      * @param x - x souradnice(vuci sachovnici)
     * @param y  -y souradnice(vuci sachovnici)
     * @param player - hrac, kteremu figurka patri
     */
    public AFigure(int x, int y, Player player) {


        //Drag mode se nastavi na false
        isDragged = false;

        //Nastaveni pozice figurky
        this.x = x;
        this.y = y;


        //Nastaveni hrace
        this.player = player;

        this.board = player.getBoard();

        this.pColor = player.getColor();

        this.color = player.getColor().getColor();
        this.borderColor = player.getColor().getStrokeColor();


    }


    /**
     * Vytvoreni staticky figurky
     * @param x  - x souradnice(vuci panelu)
     * @param y - y souradnice(vuci panelu)
     * @param color - barva figurky
     * @param drawSize - velikost figurky
     */
    public AFigure(int x, int y, PLAYERCOLOR color, int drawSize) {

        isAlive = false;

        drawX = x;
        drawY = y;

        this.color = color.getColor();
        this.borderColor = color.getStrokeColor();

        this.pColor = color;

        menu = true;

        this.drawSize = drawSize;



    }



    /**
     * Pokud je figurka pustena na platno, zjisti, zda je na spravnem miste
     *
     * @param x      - x souradnice pusteni - mysi
     * @param y      - y souradnice pusteni - mysi
     * @param figure - instance figurky
     * @return - true, pokud je figurka pustena na spravnem miste, jinak false
     */
    protected boolean dropHere(int x, int y, AFigure figure, JPanel panel) {


        //Ziskani souradnic levého horního rohu hraci plochy
        Point2D start = board.getCorner();



        //Zjisteni, na kterem policku je figurka pustena

            if (x - start.getX() < 0 || y - start.getY() < 0) return false;
            int newX = (x - (int) start.getX()) / board.getFieldSize();
            int newY = (y - (int) start.getY()) / board.getFieldSize();






        //Pokud je figurka pustena mimo hraci plochu, vrat false
        if (board.isOnBoard(newX, newY) && (newX != this.x || newY != this.y)) {



            //Ziskani vsech platnych tahu
            ArrayList<Point> validMoves = new ArrayList<>(Arrays.asList(getValidMoves(true)));

            if(this instanceof Pawn) {
                for(Point point : ((Pawn) this).enPassantMoves()){
                    Point oldMove = new Point(this.x, this.y);

                    if(point.x == newX && point.y == newY){
                        //Pokud je figurka pustena na spravnem miste, nastav nove souradnice
                        this.x = newX;
                        this.y = newY;


                        //Spust animaci
                        Animation(oldMove, new Point(this.x, this.y), panel);



                        board.getFigureOn(this.x, this.y + (-1*((Pawn) this).direction)).killFigure();


                        //Nastav figurku na nove pozici
                        board.setFigureOn(this.x, this.y, figure);

                        //Nastav figurce, že již nejde o první tah
                        firstMove = false;

                        //board.changeTurn();

                        //Ulož původní a novou pozici
                        board.setOld_a_new(new Point[]{oldMove, new Point(this.x, this.y)});

                        return true;
                    }
                }
            }

            //Projed vsechny platne tahy
            for (Point validPoint : validMoves) {


                //Pokud je figurka pustena na spravnem miste, nastav nove souradnice
                if (validPoint.getX() == newX && validPoint.getY() == newY) {



                    Point oldMove = new Point(this.x, this.y);


                    //Kontrola na rosadu
                    if ((this instanceof King king && board.getFigureOn(validPoint.x, validPoint.y) instanceof Rook rook)
                            && king.getPlayer() == rook.getPlayer()) {


                        rosada(rook, king, validPoint.x == 0);
                        //board.changeTurn();
                        return true;
                    }

                    if (this instanceof Rook rook && board.getFigureOn(validPoint.x, validPoint.y) instanceof King king
                            && king.getPlayer() == rook.getPlayer()) {
                        rosada(rook, king, rook.x == 0);
                        //board.changeTurn();
                        return true;
                    }






                    //Pokud je figurka pustena na spravnem miste, nastav nove souradnice
                    this.x = newX;
                    this.y = newY;


                    //Spust animaci
                    Animation(oldMove, new Point(this.x, this.y), panel);


                    //Pokud na nove pozici je jina figurka, odstran ji z hraci plochy
                    if (board.getFigureOn(this.x, this.y) != null) {
                        board.getFigureOn(this.x, this.y).killFigure();
                    }


                    Arrays.stream(player.getEnemy().getFiguresPack()).filter(figure1 -> figure1 instanceof Pawn).forEach(figure1 -> ((Pawn) figure1).enPassantPossible = false);

                    if(this instanceof Pawn pawn){
                        ((Pawn) this).enPassantPossible = firstMove && (pawn.y == 3 || pawn.y == 4);
                    }

                    //Nastav figurku na nove pozici
                    board.setFigureOn(this.x, this.y, figure);

                    //Nastav figurce, že již nejde o první tah
                    firstMove = false;

                    //Změň hráče
                    //board.changeTurn();

                    //Ulož původní a novou pozici
                    board.setOld_a_new(new Point[]{oldMove, new Point(this.x, this.y)});


                    if(this instanceof Pawn pawn){
                        if(pawn.y == 0 || pawn.y == 7){

                            if(pawn.getPlayer().isCpu){
                                if(pawn.getPlayer().cpu instanceof RandomCPU) {


                                    Random random = new Random();
                                    int index = random.nextInt(4);


                                    AFigure fig;
                                    if (index == 1)
                                        fig = new Queen(pawn.getPos().x, pawn.getPos().y, pawn.getPlayer());
                                    else if (index == 2)
                                        fig = new Rook(pawn.getPos().x, pawn.getPos().y, pawn.getPlayer());
                                    else if (index == 3)
                                        fig = new Bishop(pawn.getPos().x, pawn.getPos().y, pawn.getPlayer());
                                    else fig = new Knight(pawn.getPos().x, pawn.getPos().y, pawn.getPlayer());


                                    pawn.getPlayer().cpu.promote(pawn, fig);
                                }
                            }
                            else {
                                new PromotionPanel(board, pawn);
                            }
                        }
                    }
                    //Vrat true
                    return true;

                }

            }

        }
                return false;
    }

    /**
     * Metoda, ktera se zavola pri pusteni figurky
     *
     * @param x      - x souradnice pusteni - mysi
     * @param y      - y souradnice pusteni - mysi
     * @param figure - instance figurky
     */
    public boolean makeMove(int x, int y, AFigure figure, JPanel panel) {

        //Ziskani souradnic stare pozice
        int oldX = this.x;
        int oldY = this.y;


        //Pokud je figurka pustena na spravnem miste - odstran figurku z puvodni pozice
        if (figure.dropHere(x, y, figure, panel)){
            board.removeFigureOn(oldX, oldY);
            //Kontrola na sachu

            //Změň hráče
            board.changeTurn();


            if(board.getTurnPlayer().getEnemy().isCpu){
                return false;
            }

            return true;

        }
        return false;


    }


    /**
     * Vymaze figurku z hraci plochy a z hrace
     */
    public void killFigure() {

        isAlive = false;

        board.removeFigureOn(x, y);
        player.getEnemy().getOutFigs().add(this);
        player.removeFigure(this);
    }




    //--------------------------------------------//
    //--------------Vykreslovani------------------//
    //--------------------------------------------//

    /**
     * Vrati inverzni transformaci pro zjisteni polohy figurky na BasicPanel
     *
     * @return inverzni transformace
     */
    public AffineTransform getInverseTransform() {

        try {
            return inverseTransform.createInverse();
        } catch (Exception ignored) {
            return null;
        }

    }



    /**
     * Zde se provede transformace grafickeho kontextu a vykresleni podstavy figurky
     *
     * @param g2 graficky kontext
     */
    public void paint(Graphics2D g2) {

        //Ulozeni transformace - puvodni
        saveTransform = g2.getTransform();


        //Ziskani velikosti jednoho pole
        int field;
        if(!menu){
            field = board.getFieldSize();
        }
        else{
            field = drawSize;
        }


        if (animating) {

            drawX = xStep;
            drawY = yStep;

        } else if (isDragged) {

            try{
                drawX = (int) (dragPos.getX() - board.getFieldSize() / 2.0);
                drawY = (int) (dragPos.getY() - board.getFieldSize() / 2.0);
                board.drawValid(g2,this);
            }catch (Exception ignored){}


        }
        else if(isAlive){
            //Vypocet souradnic levého horního rohu pole na kterém se figurka nachází
            drawX = (int) (x * field + board.getCorner().getX());
            drawY = (int) (y * field + board.getCorner().getY());
        }




        //Nastaveni antialiasingu
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);


        //Vypocet offsetu, jak je figurka daleko od kraje pole
        offset = field / 16;

        //Vypocet velikosti podstavy
        rectBase = (int) Math.ceil(field - 2 * offset)- offset/16;


        //Vytvoreni podstavy
        Rectangle2D rect = new Rectangle2D.Double(offset, offset, rectBase, field / 4.0);

        //X souradnice stredu podstavy
        center = (int) Math.ceil(offset + (2 * rectBase) / 4.0);

        //Vypocet velikosti okraje - 1/20 velikosti pole
        strokeSize = (float) (field / 45.0);


        //Vytvoreni orezu - zaobleny okraj
        final int CUTT_RADIUS = field - 5 * offset;
        Ellipse2D cutter = new Ellipse2D.Double(center - CUTT_RADIUS / 2.0, offset - CUTT_RADIUS / 2.0, CUTT_RADIUS, CUTT_RADIUS);


        //Vytvoreni objektu, ktery bude obsahovat vsechny casti figurky
        Figure = new Area(rect);
        Figure.intersect(new Area(cutter));

        inverseTransform = new AffineTransform();
        inverseTransform.translate(drawX, drawY + field);
        inverseTransform.scale(1, -1);
        g2.transform(inverseTransform);

    }


    /**
     * Zde se provede vykresleni figurky
     * @param g2 graficky kontext
     */
    protected void finalPaint(Graphics2D g2) {

        g2.setStroke(new BasicStroke(strokeSize));


        if(pColor == PLAYERCOLOR.WHITE){
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Double(0, 0),
                    (float)(rectBase),
                    new float[] {.3f, .6f},
                    new Color[] {
                            Color.LIGHT_GRAY,color})
            );
        }
        else{
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Double(0, 0),
                    (float)(rectBase),
                    new float[] {.04f, .6f},
                    new Color[] {
                            Color.WHITE,color})
            );
        }

        g2.fill(Figure);

        g2.setColor(borderColor);
        g2.draw(Figure);


        g2.setTransform(saveTransform);
    }





    /**
     * Animace pohybu figurky
     * @param from - zacatek animace (souradnice pole)
     * @param to - konec animace (souradnice pole)
     * @param panel - panel, na kterem se ma figurka vykreslovat
     */
    protected void Animation(Point from, Point to, JPanel panel) {


        Point2D toC;
        Point2D fromC;
        //Inicializace timeru
        Timer tm = new Timer();



        fromC = new Point2D.Double(board.getCorner().getX() + from.getX() * board.getFieldSize(), board.getCorner().getY() + from.getY() * board.getFieldSize());
        toC = new Point2D.Double(board.getCorner().getX() + to.getX() * board.getFieldSize(), board.getCorner().getY() + to.getY() * board.getFieldSize());




        //Pocatecni cas
        startTime = System.currentTimeMillis();

        //Figurka se pohybuje
        animating = true;


        lastx = 0;
        lasty = 0;

        tm.schedule(new TimerTask() {
            @Override
            public void run() {


                currentTime = System.currentTimeMillis();
                animate(fromC, toC,currentTime - startTime);

                panel.repaint();

                if (currentTime - startTime > 500) {

                    animating = false;
                    tm.cancel();

                }


            }
        }, 0, 1);


    }

    int lastx = 0;
    int lasty = 0;

    /**
     * Metoda pro animaci pohybu figurky
     * @param from - pocatecni pozice
     * @param to - konecna pozice
     * @param currentTime - aktualni cas
     */
    private void animate(Point2D from,Point2D to, long currentTime) {

        //get Linear interpolation step in time 0.5s with step max 10px
        double x = (to.getX() - from.getX()) / 500.0;
        double y = (to.getY() - from.getY()) / 500.0;

        int xS = (int) (x * currentTime);
        int yS = (int) (y * currentTime);



        //Omezeni pohybu na 10px
//        int stepX = xS - lastx;
//        int stepY = yS - lasty;
//
//        if(stepX > 10){
//            xS = lastx + 10;
//        }
//        if(stepX < -10){
//            xS = lastx - 10;
//        }
//        if(stepY > 10){
//            yS = lasty + 10;
//        }
//        if(stepY < -10){
//            yS = lasty - 10;
//        }





        //Ulozeni posledni pozice pohybu
        lastx = xS;
        lasty = yS;

        xStep = (int) (from.getX() + xS);
        yStep = (int) (from.getY() + yS);

//        xStep = (int) (from.getX() + x * currentTime);
//        yStep = (int) (from.getY() + y * currentTime);








    }


    /**
     * Metoda pro udelani rosady
     * @param rook - vez
     * @param king - kral
     * @param big - velka rosada (true) nebo mala rosada (false)
     */
    public void rosada(Rook rook, King king, boolean big){

        rook.makeRosada(big,board.getPanel());
        king.makeRosada(big,board.getPanel());
        king.checkState();

    }




    /**
     * Metoda, ktera vrati vsechny mozne tahy pro figurku aby zabranila sachu
     * @param validMoves - vsechny mozne tahy (referencni promenna)
     * @param player - hrac, ktery ma na tahu
     */
    protected void moves_for_stop_check(ArrayList<Point> validMoves, Player player){

            //Ziskani vsech moznych tahu
//            Iterator<Point> iterator = validMoves.iterator();
//
//
//            //Prochazeni vsech moznych tahu
//            while (iterator.hasNext()){
//
//
//                Point point = iterator.next();
//
//                AFigure tmpFigure = board.getFigureOn(point.x, point.y);
//
//
//                Player enemy;
//
//                if(tmpFigure != null){
//
//                    if(tmpFigure.getPlayer() != this.player){
//
//
//                        Player[] pl = board.getPlayers();
//
//                        if(this.player == pl[0]) enemy = pl[1];
//                        else enemy = pl[0];
//
//                        enemy.removeFigure(tmpFigure);
//
//                        board.setFigureOn(x, y, null);
//                        board.setFigureOn(point.x, point.y, this);
//
//                        if(player.getKing().isCheck()){
//                            iterator.remove();
//                        }
//
//                        board.setFigureOn(point.x, point.y, tmpFigure);
//
//                        enemy.addFigure(tmpFigure);
//                        board.setFigureOn(x, y, this);
//                    }
//
//
//
//                    King king = null;
//                    Rook rook = null;
//
//
//                    if(this instanceof King && tmpFigure.getPlayer() == this.player && tmpFigure instanceof Rook){
//                        king = (King)this;
//                        rook = (Rook)tmpFigure;
//                    } else if (this instanceof Rook && tmpFigure.getPlayer() == this.player && tmpFigure instanceof King){
//                        king = (King)tmpFigure;
//                        rook = (Rook)this;
//                    }
//
//                    if(rook != null){
//                        int rookFinal,direction;
//                        if(rook.x == 0){
//                            direction = -1;
//                            rookFinal = 3;
//                        }
//                        else{
//                            direction = 1;
//                            rookFinal = 5;
//                        }
//
//                        board.setFigureOn(x, y, null);
//                        board.setFigureOn(rook.x,rook.y,null);
//
//                        board.setFigureOn(rookFinal, rook.y, rook);
//
//
//
//                        for(int i = 0; i < 3; i++){
//                            board.setFigureOn(4 + direction* i, point.y, king);
//
//                            if(player.getKing().isCheck()){
//                                iterator.remove();
//                                board.setFigureOn(4+ direction*i, point.y, null);
//                                break;
//                            }
//
//                            board.setFigureOn(4 + direction* i, point.y, null);
//
//
//                        }
//
//
//
//                        board.setFigureOn(rookFinal, rook.y, null);
//
//                        board.setFigureOn(4, point.y, king);
//                        board.setFigureOn(rook.x, rook.y, rook);
//
//
//                    }
//
//
//                }
//                else{
//                    board.setFigureOn(x, y, null);
//                    board.setFigureOn(point.x, point.y, this);
//
//                    if(player.getKing().isCheck()){
//                        iterator.remove();
//                    }
//
//
//                    board.setFigureOn(point.x, point.y, null);
//                    board.setFigureOn(x, y, this);
//
//                }
//
//            }


        Board copy = new Board(board);

        //Ziskani vsech moznych tahu
            Iterator<Point> iterator = validMoves.iterator();


            //Prochazeni vsech moznych tahu
            while (iterator.hasNext()){


                Point point = iterator.next();

                AFigure tmpFigure = copy.getFigureOn(point.x, point.y);


                Player enemy;

                if(tmpFigure != null){

                    if(tmpFigure.getPlayer() != this.player){


                        Player[] pl = board.getPlayers();

                        if(this.player == pl[0]) enemy = pl[1];
                        else enemy = pl[0];

                        enemy.removeFigure(tmpFigure);

                        copy.setFigureOn(x, y, null);
                        copy.setFigureOn(point.x, point.y, this);

                        if(player.getKing().isCheck()){
                            iterator.remove();
                        }

                        copy.setFigureOn(point.x, point.y, tmpFigure);

                        enemy.addFigure(tmpFigure);
                        copy.setFigureOn(x, y, this);
                    }



                    King king = null;
                    Rook rook = null;


                    if(this instanceof King && tmpFigure.getPlayer() == this.player && tmpFigure instanceof Rook){
                        king = (King)this;
                        rook = (Rook)tmpFigure;
                    } else if (this instanceof Rook && tmpFigure.getPlayer() == this.player && tmpFigure instanceof King){
                        king = (King)tmpFigure;
                        rook = (Rook)this;
                    }

                    if(rook != null){
                        int rookFinal,direction;
                        if(rook.x == 0){
                            direction = -1;
                            rookFinal = 3;
                        }
                        else{
                            direction = 1;
                            rookFinal = 5;
                        }

                        copy.setFigureOn(x, y, null);
                        copy.setFigureOn(rook.x,rook.y,null);

                        copy.setFigureOn(rookFinal, rook.y, rook);



                        for(int i = 0; i < 3; i++){
                            copy.setFigureOn(4 + direction* i, point.y, king);

                            if(player.getKing().isCheck()){
                                iterator.remove();
                                copy.setFigureOn(4+ direction*i, point.y, null);
                                break;
                            }

                            copy.setFigureOn(4 + direction* i, point.y, null);


                        }



                        copy.setFigureOn(rookFinal, rook.y, null);

                        copy.setFigureOn(4, point.y, king);
                        copy.setFigureOn(rook.x, rook.y, rook);


                    }


                }
                else{
                    copy.setFigureOn(x, y, null);
                    copy.setFigureOn(point.x, point.y, this);

                    if(player.getKing().isCheck()){
                        iterator.remove();
                    }


                    copy.setFigureOn(point.x, point.y, null);
                    copy.setFigureOn(x, y, this);

                }

            }






    }



    /**
     * Nastaveni velikosti figurky
     * @param drawSize - velikost figurky
     */
    public void setDrawSize(int drawSize){
        this.drawSize = drawSize;
    }

    /**
     * Vrati, zdali je figurka na Alive
     * @return - true, pokud je figurka na Alive, jinak false
     */
    public boolean isAlive() {
        return isAlive;
    }


    /**
     * Ziskani hrace
     * @return - hrac, kteremu figurka patri
     */
    public Player getPlayer() {
        return player;
    }


    /**
     * Ziskani pozice
     * @return - pozice
     */
    public Point getPos(){
        return new Point(x, y);
    }

    /**
     * Vrati oblast figurky
     * @return - oblast figurky
     */
    public Area getArea() {
        return Figure;
    }


    /**
     * Nastavi drag mode
     */
    public void setDragged(boolean dragged) {
        isDragged = dragged;
    }


    /**
     * Vrati drag mode
     * @return - true, pokud je figurka v drag modu, jinak false
     */

    public boolean isDragged() {
        return isDragged;
    }


    /**
     * Abstraktni metoda, ktera vrati vsechny platne tahy pro danou figurku
     *
     * @return - pole platnych tahu
     */
    public abstract Point[] getValidMoves(boolean calledFromFigure);

    public abstract String getFEN();


}
