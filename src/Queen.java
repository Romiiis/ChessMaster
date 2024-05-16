import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * Trida reprezentuje kralovnu
 *
 * @author Roman Pejs
 */
public class Queen extends AFigure {




        /**
         * Konstruktor pro hraci figurku
         * @param x - souradnice x (vuci hraci desce)
         * @param y - souradnice y (vuci hraci desce)
         * @param player - hrac, kteremu figurka patri
         */
        public Queen(int x, int y, Player player) {
            super(x, y, player);
        }


        /**
         * Konstruktor pro statickou figurku
         * @param x - souradnice x (vuci panelu)
         * @param y - souradnice y (vuci panelu)
         * @param color - barva figurky
         * @param drawSize - velikost figurky
         */
        public Queen(int x, int y, PLAYERCOLOR color, int drawSize){
            super(x, y, color,drawSize);
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

                //Vytvoreni pole smeru
                int[][] directions = {{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,1},{0,-1}};


                //Prochazeni vsech smeru
                for(int i = 0;i<8;i++) {

                        //Ziskani smeru
                        int dirX = directions[i][0];
                        int dirY = directions[i][1];

                        //Prochazeni vsech poli v danem smeru
                        for (int j = 1; j < 8; j++) {

                                try {
                                        //Pokud je na poli figurka
                                        if (board.getFigureOn(x + dirX * j, y + dirY * j) != null){

                                                //Pokud je na poli figurka souperova
                                                if(board.getFigureOn(x + dirX * j, y + dirY * j).getPlayer() != getPlayer()){

                                                        //Pridani policka do seznamu
                                                        validMoves.add(new Point(x + dirX * j, y + dirY * j));
                                                }

                                                //Ukonceni prochazeni v danem smeru
                                                break;
                                        }

                                        //Pridani policka do seznamu
                                        validMoves.add(new Point(x + dirX * j, y + dirY * j));


                                  //Pokud je vyjimka, tak se ukonci prochazeni v danem smeru
                                } catch (ArrayIndexOutOfBoundsException e) {
                                        break;
                                }

                        }
                }



                //Pokud byla metoda zavolana z figurky, tak se zkontroluje, zda se nejedna o zakazany tah, ktery by vedl k sachu
                if(calledFromFigure) moves_for_stop_check(validMoves, this.getPlayer());



                //Vrati seznam platnych pohybu
                return validMoves.toArray(new Point[0]);
        }

        @Override
        public String getFEN() {
                return getPlayer().getColor() == PLAYERCOLOR.WHITE ? "Q" : "q";
        }


        /**
         * Vykresleni figurky - kralovna
         * @param g2 - grafika
         */
        @Override
        public void paint(Graphics2D g2) {

                super.paint(g2);

                //Velikost jednoho pole
                int field;


                //Pokud je figurka staticka, tak se pouzije velikost z parametru
                if(!menu){
                        field = board.getFieldSize();
                }
                else{
                        field = drawSize;
                }



                //Vytvoreni trojuhelniku - prostredek koruny
                Polygon crownCenter = new Polygon();
                crownCenter.addPoint((int) Math.ceil(offset + (3*rectBase/8.0)), offset+ field/4);
                crownCenter.addPoint((int) Math.ceil(offset + (4*rectBase/8.0)),  field-offset*4);
                crownCenter.addPoint((int) Math.ceil(offset + (5*rectBase)/8.0), offset + field/4);



                //Vytvoreni trojuhelniku - pravo
                Polygon crownRight = new Polygon();
                crownRight.addPoint((int) Math.ceil(offset + (4.5*rectBase/8.0)), offset+ field/4);
                crownRight.addPoint((int) Math.ceil(offset + (7*rectBase/8.0)),  field-offset*4);
                crownRight.addPoint((int) Math.ceil(offset + (6.4*rectBase)/8.0), offset + field/4);



                //Vytvoreni trojuhelniku - levo
                Polygon crownLeft = new Polygon();
                crownLeft.addPoint((int) Math.ceil(offset + (3.5*rectBase/8.0)), offset+ field/4);
                crownLeft.addPoint((int)Math.ceil(offset + (rectBase/8.0)),  field-offset*4);
                crownLeft.addPoint((int) Math.ceil(offset + (1.5*rectBase)/8.0), offset + field/4);




                Figure.add(new Area(crownCenter));
                Figure.add(new Area(crownRight));
                Figure.add(new Area(crownLeft));

                double R = offset*3;
                Figure.add(new Area(new Ellipse2D.Double( Math.ceil(offset + (4*rectBase/8.0) - R/2),  (field-offset*4) - R/2,R,R)));
                Figure.add(new Area(new Ellipse2D.Double( Math.ceil(offset + (7*rectBase/8.0)- R/2),  (field-offset*4) - R/2,R,R)));
                Figure.add(new Area(new Ellipse2D.Double( Math.ceil(offset + (rectBase/8.0)- R/2),  (field-offset*4) - R/2,R,R)));


                //Pokud je figurka vybrana, tak se vykresli
                finalPaint(g2);



        }
}
