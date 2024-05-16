import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Trida reprezentuje Pesce
 *
 * @author Roman Pejs
 */
public class Pawn extends AFigure {

    //Zda se jedna o prvni pohyb a zda je mozne provest en passant
    public boolean enPassantPossible = false;


    //Smer pohybu figurky
    int direction;


    /**
     * Konstruktor pro hraci figurku
     * @param x - souradnice x (vuci hraci desce)
     * @param y - souradnice y (vuci hraci desce)
     * @param player - hrac, kteremu figurka patri
     */
    public Pawn(int x, int y, Player player) {
        super(x, y, player);

        //Nastaveni smeru pohybu
        if(player.getColor() == PLAYERCOLOR.WHITE){
            direction = -1;
        }
        else direction = 1;

    }


    /**
     * Ziskani seznamu platnych pohybu
     * @param calledFromFigure - zda byla metoda zavolana z figurky
     * @return - seznam platnych pohybu
     */
    @Override
    public Point[] getValidMoves(boolean calledFromFigure) {

        //Vytvoreni seznamu platnych pohybu
        ArrayList<Point> validMoves = new ArrayList<>();


        //Zda se jedna o prvni pohyb tak se muze pohnout o dva policka
        if(firstMove){

            validMoves.add(new Point(x,y + direction));

            if(board.getFigureOn(x,y + direction) == null) {
                validMoves.add(new Point(x, y + direction * 2));
            }

        }
        else{

            validMoves.add(new Point(x,y + direction));
        }

        //Odstraneni pohybu mimo hraci desku
        validMoves.removeIf(point -> !board.isOnBoard(point.x, point.y) || board.getFigureOn(point.x, point.y) != null);


        //Pridani pohybu pro klasicke brani do stran
        for(int i = -1; i<2; i = i+2){

            try{
                if(board.isOnBoard(x+i,y+ direction) && board.getFigureOn(x + i,y + direction) != null){

                    if(board.getFigureOn(x + i,y + direction).getPlayer().getColor() != getPlayer().getColor()){

                        validMoves.add(new Point(x + i,y + direction));

                    }
                }
            }catch (NullPointerException ignored){

            }
        }


        //Odstraneni pohybu mimo hraci desku
        validMoves.removeIf(point -> !board.isOnBoard(point.x, point.y));

        //Odstraneni pohybu, ktere by vedly do sachu
        if(calledFromFigure) moves_for_stop_check(validMoves, player);

        //Vrati seznam platnych pohybu
        return validMoves.toArray(new Point[0]);
    }


    /**
     * Ziskani seznamu pohybu pro En Passant
     * @return - seznam pohybu pro En Passant
     */
    public Point[] enPassantMoves(){

        //Vytvoreni seznamu platnych pohybu
        ArrayList<Point> validMoves = new ArrayList<>();

        //Pridani pohybu pro en passant
        for (int i = -1; i < 2; i = i + 2) {

            try {
                //Zda je na policku pesec protivnikove barvy
                if (board.getFigureOn(x + i, y) instanceof Pawn && board.getFigureOn(x + i, y).getPlayer().getColor() != getPlayer().getColor()) {

                    //Zda je mozne provest en passant
                    if (((Pawn) board.getFigureOn(x + i, y)).enPassantPossible) {

                        //Pridani pohybu
                        validMoves.add(new Point(x + i, y + direction));
                    }
                }
            } catch (Exception ignored) {

            }



            //Podle barvy hrace nastaveni smeru pohybu
            int attack = -1;
            if (this.player.getColor() == PLAYERCOLOR.WHITE) attack = +1;

            //Iterace pres vsechny platne pohyby
            Iterator<Point> iterator = validMoves.iterator();

            Board tmp = new Board(board);

            //Odstraneni pohybu, ktere by vedly do sachu
            while (iterator.hasNext()) {

                Point point = iterator.next();

                tmp.setFigureOn(x, y, null);

                AFigure tmpFigure = tmp.getFigureOn(point.x, point.y + attack);
                tmp.setFigureOn(point.x, point.y + attack, null);


                if (player.getKing().isCheck()) {
                    iterator.remove();
                }


                tmp.setFigureOn(point.x, point.y + attack, tmpFigure);
                tmp.setFigureOn(x, y, this);


            }
        }

        //Vrati seznam platnych pohybu
        return validMoves.toArray(new Point[0]);
    }


    /**
     * Vykresleni figurky
     * @param g2 graficky kontext
     */
    public void paint(Graphics2D g2) {

        super.paint(g2);


        //Velikonost jednoho pole
        int field = board.getFieldSize();


        //Vytvoreni trojuhelniku
        Polygon triangle = new Polygon();
        triangle.addPoint((int) Math.ceil(offset + (rectBase/4.0)), offset+ field/4);
        triangle.addPoint(center,  field-offset*2);
        triangle.addPoint((int) Math.ceil(offset + (3*rectBase)/4.0), offset + field/4);

        //Vytvoreni kruhu "Hlava"
        int RADIUS = field/3 ;
        Ellipse2D circle = new Ellipse2D.Double(center - RADIUS/2.0,
                (field-offset*2) - RADIUS, RADIUS, RADIUS);


        //Vytvoreni objektu, ktery bude obsahovat vsechny casti figurky
        Figure.add(new Area(triangle));
        Figure.add(new Area(circle));

        //Vykresleni figurky
        finalPaint(g2);



    }


    public String getFEN(){
        if(player.getColor() == PLAYERCOLOR.WHITE){
            return "P";
        }
        else{
            return "p";
        }
    }

    public String getEnPassantField() {
        String enPassantField = "";
        enPassantField += (char) (x + 97);
        if(player.getColor() == PLAYERCOLOR.WHITE)
            enPassantField += (y + direction);
        else
            enPassantField += (8 - y + direction);

        return enPassantField;
    }
}
