import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Trida reprezentuje Krale
 *
 * @author Roman Pejs
 */
public class King extends AFigure  {


    /**
     * Konstruktor pro vytvoreni krale pohybujiciho se po sachovnici
     * @param x - souradnice x (vuci sachovnici)
     * @param y - souradnice y (vuci sachovnici)
     * @param player - hrac ke kteremu figura patri
     */
    public King(int x, int y, Player player) {
        super(x, y, player);
    }


    /**
     *Metoda pro vytvoreni staticke figurky
     * @param x - souradnice x (vuci panelu)
     * @param y - souradnice y (vuci panelu)
     * @param color - barva figurky
     * @param drawSize - velikost
     */
    public King(int x, int y, PLAYERCOLOR color, int drawSize){
        super(x, y, color,drawSize);
    }


    /**
     * Vykreseni figurky
     * @param g2 graficky kontext
     */
    public void paint(Graphics2D g2){

        super.paint(g2);
        int field;

        if (!menu) {
            field =board.getFieldSize();

        }
        else{
            field = drawSize;
        }


        //Telo (trojuhelnik)
        Polygon body = new Polygon();
        body.addPoint((int) Math.ceil(offset + (rectBase/4.0)), offset+ field/4);
        body.addPoint(center,  field-offset*2);
        body.addPoint((int) Math.ceil(offset + (3*rectBase)/4.0), offset + field/4);


        //Sirka krize
        int RECTANGLE_WIDTH = (int) Math.ceil(rectBase/10.0);

        //Kriz Pillir - svisly
        Rectangle2D crossPillar = new Rectangle2D.Double(center - offset, field/2.0, RECTANGLE_WIDTH, field/2.0 - offset);

        //Kriz vodorovny
        Rectangle2D crossHorizontalRight = new Rectangle2D.Double(center + offset/2.0, field/2.0 + field/4.0, field - field/1.2, RECTANGLE_WIDTH);
        Rectangle2D crossHorizontalLeft = new Rectangle2D.Double( (center - offset/2.0) -(field - field/1.2), field/2.0 + field/4.0, (field - field/1.2), RECTANGLE_WIDTH);


        //Orez pod krizem
        int R = (int) Math.ceil(field/2.5);


        //Vykresleni figurky - vyplneni a obrys
        Area bodyA = new Area(body);
        bodyA.intersect(new Area(new Rectangle2D.Double(0, offset, field, field/2.0)));
        bodyA.intersect(new Area(new Ellipse2D.Double(center - R/2.0, field/10.0, R, R)));


        Figure.add(new Area(bodyA));
        Figure.add(new Area(crossPillar));
        Figure.add(new Area(crossHorizontalRight));
        Figure.add(new Area(crossHorizontalLeft));


        finalPaint(g2);

    }


    /**
     * Metoda pro ziskani vsech moznych tahu
     * @param calledFromFigure - zda-li se vola z figurky
     * @return - pole moznych tahu
     */
    public Point[] getValidMoves(boolean calledFromFigure){

        ArrayList<Point> validMoves = new ArrayList<>();



        //Pridani vsech moznych pohybu
        validMoves.add(new Point(x+1, y));
        validMoves.add(new Point(x-1, y));
        validMoves.add(new Point(x, y+1));
        validMoves.add(new Point(x, y-1));
        validMoves.add(new Point(x+1, y+1));
        validMoves.add(new Point(x-1, y-1));
        validMoves.add(new Point(x+1, y-1));
        validMoves.add(new Point(x-1, y+1));


        //Odebrani pohybu, kdyz bod neni na sachovnici
        validMoves.removeIf(validMove -> !board.isOnBoard(validMove.x, validMove.y));

        //Odebrani pohybu, kdyz na policku je figurka stejne barvy (ale ne vez - rosada)
//        validMoves.removeIf(validMove -> ((board.getFigureOn(validMove.x, validMove.y) instanceof Rook) && (board.getFigureOn(validMove.x, validMove.y).getPlayer() == this.player)));
        validMoves.removeIf(validMove -> ((board.getFigureOn(validMove.x, validMove.y) != null) && (board.getFigureOn(validMove.x, validMove.y).getPlayer() == this.player)));



        //-----------------ROSADA-----------------

        //Ziskani vsech vezi od hrace
        AFigure[] rooks = Arrays.stream(player.getFiguresPack())
                .filter(figure -> figure instanceof Rook)
                .toArray(AFigure[]::new);


        for (AFigure rook : rooks) {
            if (firstMove && rook.firstMove) {


                //Pocet poli, ktere se musi prochazet
                int CONST = 2;


                //nejdrive jedna strana - doprava +1
                //potom druha strana - doleva -1
                for (int i = 1; i > -2; i = i - 2) {

                    //Prochazeni vsech poli v dane strane + 1
                    for (int j = 1; j < CONST + 1; j++) {

                        try {

                            //Pokud je na policku figurka, tak se prochazeni ukonci
                            if (board.getFigureOn(x + i * j, y) != null) break;

                                //Pokud dojde na konec sachovnice, tak se prochazeni ukonci
                            else if (board.getFigureOn(x + i * j, y) == null && j == CONST) {

                                //Zjisteni, zda na policku je vez, ktera muze byt pohnuta
                                if (rook.x == x + i * j + i && rook.y == y && rook.firstMove && firstMove) {
                                    validMoves.add(new Point(x + i * j + i, y));
                                }
                            }

                            //Ignorovani vyjimky
                        } catch (Exception ignored) {
                        }
                    }


                    //Zvyseni poctu poli, ktere se musi prochazet - velka rosada
                    CONST++;
                }

                validMoves.removeIf(validMove -> (board.getFigureOn(validMove.x, validMove.y) != null)
                        && (board.getFigureOn(validMove.x, validMove.y).player == player)
                        && !(board.getFigureOn(validMove.x, validMove.y) instanceof Rook));


            }
        }

        validMoves.removeIf(validmove -> (board.getFigureOn(validmove.x, validmove.y) !=null )&&((board.getFigureOn(validmove.x, validmove.y).getPlayer() == this.player)&&!(board.getFigureOn(validmove.x, validmove.y) instanceof Rook)));





        if(!firstMove) validMoves.removeIf(validMove -> (board.getFigureOn(validMove.x, validMove.y) instanceof Rook && board.getFigureOn(validMove.x, validMove.y).getPlayer() == player));




        //-----------------Zakazani pohybu na policko, kde je kral v ohrozeni-----------------

        if(player.isTurn()) if(calledFromFigure){
            moves_for_stop_check(validMoves, player);
        }


        return validMoves.toArray(new Point[0]);



    }

    @Override
    public String getFEN() {
        return player.getColor().equals(PLAYERCOLOR.WHITE)?"K":"k";
    }


    /**
     * Provedeni rosady
     * @param big - velka rosada (true) nebo mala rosada (false)
     * @param panel - panel, na kterem se rosada provede
     */
    public void makeRosada(boolean big, JPanel panel){


        int kingX = big?2:6;

        Point old = new Point(this.x,this.y);

        //Pokud je figurka pustena na spravnem miste, nastav nove souradnice
        this.x = kingX;


        Animation(old, new Point(this.x, this.y), panel);



        //Nastav figurku na nove pozici
        board.setFigureOn(this.x, this.y, this);

        //Nastav figurce, že již nejde o první tah
        firstMove = false;


        //Ulož původní a novou pozici
        board.setOld_a_new(new Point[]{old, new Point(this.x, this.y)});
        board.removeFigureOn(old.x, old.y);


    }

    public boolean canRosada(boolean big){
        if(!firstMove) return false;
        if(big){
            AFigure rook = board.getFigureOn(0, y);
            if(!(rook instanceof Rook)) return false;;
            return firstMove && rook.firstMove;
        }
        else{
            AFigure rook = board.getFigureOn(7, y);
            if(!(rook instanceof Rook)) return false;
            return firstMove && rook.firstMove;
        }

    }


    /**
     * Zjisteni, zda je kral v sachu
     * @return - true, pokud je kral v sachu, jinak false
     */
    public boolean isCheck(){

            //Ziskani vsech figur od protihrace
            Player enemy;

            Player[] players = board.getPlayers();
            if (player == players[0]) enemy = players[1];
            else enemy = players[0];

            ArrayList<AFigure> enemyFigures = new ArrayList<>(Arrays.asList(enemy.getFiguresPack()));

            boolean check = false;
            enemyFigures.forEach(figure -> figure.attackingKing = false);


            Board copy = new Board(board);

            //Ziskani vsech moznych pohybu vsech figur od protihrace

        for(AFigure figure:enemyFigures){

            Point[] validMoves = figure.getValidMoves(false);
//
                for(Point validMove:validMoves){

                    if(copy.getFigureOn(validMove.x, validMove.y) == this){
                        figure.attackingKing = true;
                        check = true;
                    }

                }
        }

            return check;

    }


    boolean end = false;
    /**
     * Ziskani statusu hry
     */


    protected void checkState(){

        //Ziskani vsech figur od hrace
        AFigure[] figures = board.getTurnPlayer().getFiguresPack();

        //Zjisteni, zda je mozne provest sachu mat
        boolean isCheckMatePossible = true;


        for (AFigure f : figures) {
            if (f != null) {
                if (f.getValidMoves (true).length > 0){
                    isCheckMatePossible = false;
                }
            }
        }



        //Zjisteni, zda je kral v sachu
        if (this.isCheck()) {

            if (isCheckMatePossible) {


                if(!end){

                    end = true;
                    board.end = true;
                    board.getTurnPlayer().getEnemy().say("Šach mat");
                    board.getPlayers()[1].cpu = null;



                    Timer tm = new Timer();

                    //Delay 500ms
                    tm.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new EndWindow("Šach mat",player.getEnemy(),board.game);
                            System.out.println("Konec hry - Šach mat");
                            System.out.println("Vítěz: " + player.getEnemy().name);
                        }
                    }, 500);
                }





            }
            if(isCheck() && !isCheckMatePossible){

                board.getTurnPlayer().getEnemy().say("Šach");
            }

        }
        if(!this.isCheck() && isCheckMatePossible){
            if(!end){
                board.end = true;
                new EndWindow("Patová situace",null,board.game);
                System.out.println("Konec hry - Patová situace");
                end = true;
            }

        }
    }



}
