import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;


/**
 * Trida reprezentuje Strelce
 *
 * @author Roman Pejs
 */
public class Bishop extends AFigure {


    /**
     * Konstruktor pro vytvoreni strelce pohybujiciho se po sachovnici
     * @param x - souradnice x (vuci sachovnici)
     * @param y - souradnice y (vuci sachovnici)
     * @param player - hrac ke kteremu figura patri
     */
    public Bishop(int x, int y, Player player) {
        super(x, y, player);
    }


    /**
     * Metoda pro vytvoreni staticke figurky
     * @param x - souradnice x (vuci Panelu)
     * @param y - souradnice y (vuci Panelu)
     * @param color - barva
     * @param drawSize - velikost
     */
    public Bishop(int x, int y, PLAYERCOLOR color, int drawSize){
        super(x, y, color,drawSize);
    }


    /**
     * Ziskani moznych tahu
     * @param calledFromFigure - zda-li se metoda vola z figurky
     * @return pole moznych tahu
     */
    @Override
    public Point[] getValidMoves(boolean calledFromFigure) {

        ArrayList<Point> validMoves = new ArrayList<>();
        int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1}};


        for(int i = 0;i<4;i++){

            int dirX = directions[i][0];
            int dirY = directions[i][1];

            for(int j = 1;j<8;j++){

                try {

                    if (board.getFigureOn(x + dirX * j, y + dirY * j) != null){

                            if(board.getFigureOn(x + dirX * j, y + dirY * j).getPlayer() != getPlayer()){
                                validMoves.add(new Point(x + dirX * j, y + dirY * j));
                            }
                            break;


                    }
                        validMoves.add(new Point(x + dirX * j, y + dirY * j));


                }
                catch (ArrayIndexOutOfBoundsException e){
                    break;
                }

            }
        }


        validMoves.removeIf(point -> !board.isOnBoard(point.x, point.y));


        if(calledFromFigure) moves_for_stop_check(validMoves, this.getPlayer());


        return validMoves.toArray(new Point[0]);
    }

    @Override
    public String getFEN() {
        if(getPlayer().getColor() == PLAYERCOLOR.WHITE){
            return "B";
        }
        else{
            return "b";
        }
    }


    /**
     * Vykresleni figurky
     * @param g2 graficky kontext
     */
    public void paint(Graphics2D g2){

        super.paint(g2);

        int field;
        //Velikost jednoho pole
        if(!menu){
            field = board.getFieldSize();
        }
        else{
            field = drawSize;
        }

        Ellipse2D body = new Ellipse2D.Double(center - (rectBase/2.0)/2.0, center - field/3.0, rectBase/2.0, field - offset*6);


        //Vyrez
        Polygon cutter = new Polygon();
        cutter.addPoint(center, field/2 );
        cutter.addPoint(center + offset, field/2 - offset);
        cutter.addPoint(field, field - offset);
        cutter.addPoint(field - offset, field);



        //Kulicka na strele

        int R = (int) Math.ceil(field/10.0);
        Ellipse2D ball = new Ellipse2D.Double(center - R/2.0, (center - field/3.0) + field - offset*6, R, R);



        Area cutted = new Area(body);
        cutted.subtract(new Area(cutter));


        Figure.add(new Area(cutted));
        Figure.add(new Area(ball));

        finalPaint(g2);


    }
}
