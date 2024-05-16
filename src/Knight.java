import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;


/**
 * Trida reprezentuje jezdec
 *
 * @author Roman Pejs
 */
public class Knight extends AFigure {


    /**
     * Konstruktor pro hraci figurku
     * @param x - souradnice x (vuci hraci desce)
     * @param y - souradnice y (vuci hraci desce)
     * @param player Hrac
     */
    public Knight(int x, int y, Player player) {
        super(x, y, player);
    }


    /**
     * Konstruktor pro statickou figurku
     * @param x - souradnice x (vuci panelu)
     * @param y - souradnice y (vuci panelu)
     * @param color - barva figurky
     * @param drawSize - velikost figurky
     */
    public Knight(int x, int y, PLAYERCOLOR color, int drawSize){
        super(x, y, color,drawSize);
    }


    /**
     * Ziskani seznamu platnych pohybu
     * @param calledFromFigure - zda byla metoda zavolana z figurky
     * @return - seznam platnych pohybu
     */
    @Override
    public Point[] getValidMoves(boolean calledFromFigure) {

        //Vyvoreni seznamu platnych pohybu
        ArrayList<Point> validMoves = new ArrayList<>();

        //Vytvoreni pole smeru
        int[][] directions = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};

        //Prochazeni vsech smeru
        for (int[] direction : directions) {
            validMoves.add(new Point(x + direction[0], y + direction[1]));
        }


        //Odebrani pohybu mimo sachovnici a pohybu na vlastni figurku
        validMoves.removeIf(point -> !board.isOnBoard(point.x, point.y));
        validMoves.removeIf(validMove -> board.getFigureOn(validMove.x, validMove.y) != null && board.getFigureOn(validMove.x, validMove.y).getPlayer().getColor() == this.getPlayer().getColor());

        //Volani metody pro zjisteni, zda je kral ohrozen
        if(calledFromFigure) moves_for_stop_check(validMoves, this.getPlayer());


        //Vraceni pole platnych pohybu
        return validMoves.toArray(new Point[0]);
    }


    /**
     * Vykresleni figurky
     * @param g2 graficky kontext
     */
    public void paint(Graphics2D g2) {

        super.paint(g2);

        int field;

        //Velikost jednoho pole
        if(!menu){
            field = board.getFieldSize();
        }
        else{
            field = drawSize;
        }


        //Cele telo
        Path2D path = new Path2D.Double();
        path.moveTo(center + rectBase/4.0, field/4.0);
        path.quadTo(center + rectBase/6.0, field/2.0, center + rectBase/4.0, field - field/3.0);
        path.quadTo(center , field, center - rectBase/4.0, field - field/3.0);
        path.quadTo(offset, field/2.0, center, field/2.0);
        path.quadTo(center, field/2.0 - offset, center - 2* offset, field/2.0 -2*offset);
        path.quadTo(offset*4, field/2.0 - 4*offset, rectBase/4.0 + offset*2, field/4.0);
        path.closePath();


        //Ucho
        Ellipse2D ear1 = new Ellipse2D.Double(center + 2*offset, field - 5*offset, offset*2, offset*3);
        Ellipse2D ear2 = new Ellipse2D.Double(center, field - 5*offset, offset*2, offset*3);

        //Oko
        Ellipse2D eye = new Ellipse2D.Double(center - 2*offset,field - 6*offset , offset, offset);


        Figure.add(new Area(path));
        Figure.add(new Area(ear1));
        Figure.add(new Area(ear2));
        Figure.subtract(new Area(eye));

        //Vykresleni
        finalPaint(g2);


    }

    public String getFEN(){
        if(player.getColor() == PLAYERCOLOR.WHITE){
            return "N";
        }
        else{
            return "n";
        }
    }
}
