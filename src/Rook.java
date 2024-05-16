import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;


/**
 * Trida reprezentuje Vez
 *
 * @author Roman Pejs
 */
public class Rook extends AFigure {


    /**
     * Konstruktor pro hraci figurku
     * @param x - souradnice x (vuci hraci desce)
     * @param y - souradnice y (vuci hraci desce)
     * @param player - hrac, kteremu figurka patri
     */
    public Rook(int x, int y, Player player) {
        super(x, y, player);
    }





    /**
     * Konstruktor pro statickou figurku
     * @param x - souradnice x (vuci panelu)
     * @param y - souradnice y (vuci panelu)
     * @param color - barva figurky
     * @param drawSize - velikost figurky
     */
    public Rook(int x, int y, PLAYERCOLOR color, int drawSize){
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
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};


        //Pro kazdy smer
        for(int i = 0;i<4;i++){

            //Ziskani smeru
            int dirX = directions[i][0];
            int dirY = directions[i][1];

            //Vyzkouseni vsech moznych pohybu v danem smeru (maximem je 7)
            for(int j = 1;j<8;j++){

                try {

                    //Pokud je na pozici figurka
                    if (board.getFigureOn(x + dirX * j, y + dirY * j) != null) {

                        //Pokud je na pozici figurka souperova, pridat pozici do seznamu
                        if(board.getFigureOn(x + dirX * j, y + dirY * j).getPlayer() != getPlayer()){
                            validMoves.add(new Point(x + dirX * j, y + dirY * j));
                        }

                        //Ukoncit cyklus
                        break;
                    }

                    //Pokud na pozici neni figurka, pridat pozici do seznamu
                    validMoves.add(new Point(x + dirX * j, y + dirY * j));
                }
                //Pokud je dosazena hranice hraci desky, ukoncit cyklus
                catch (ArrayIndexOutOfBoundsException e){
                    break;
                }

            }
        }



        //-------------------------------------------------
        //                   ROSADA
        //-------------------------------------------------


        //Pokud je to prvni tah
        if(firstMove){

            //Ziskani krale
            King king = player.getKing();


            //Ziskani smeru (1 = doprava, -1 = doleva)
            int direction = (x == 0 ? 1: -1);

            //Ziskani poctu pozic, ktere musi prochazet kral
            int count = (x == 0 ? 3: 2);



            //Pro kazdou pozici
            for (int j = 1; j < count+1; j++) {

                try {

                    //Pokud je na pozici figurka, ukoncit cyklus
                    if (board.getFigureOn(x + direction * j, y) != null) break;

                    //Pokud na pozici neni figurka a je to posledni pozice, zkontrolovat, zda je kral na spravnem miste
                    else if (board.getFigureOn(x + direction * j, y) == null && j == count) {

                        //Pokud je kral na spravnem miste a je to prvni tah, pridat pozici do seznamu
                        if (king.x == x + direction * j + direction && king.y == y && king.firstMove && this.firstMove) {

                            validMoves.add(new Point(x + direction * j + direction, y));
                        }

                    }

                    //Ignorovat vyjimku
                } catch (Exception ignored) {
                    //--//
                }
            }
        }


        //Pokud je volano z figurky, zkontrolovat, ktere predchozi tahy jsou mozne, pro odstraneni sachovani
        if(calledFromFigure) moves_for_stop_check(validMoves, this.getPlayer());


        //Vrati seznam platnych pohybu
        return validMoves.toArray(new Point[0]);
    }

    @Override
    public String getFEN() {
        return getPlayer().getColor() == PLAYERCOLOR.WHITE?"R":"r";
    }


    /**
     * Metoda pro provedeni rosady
     * @param big - velka rosada (true) nebo mala rosada (false)
     * @param panel - panel, na kterem se figurka nachazi
     */
    public void makeRosada(boolean big, JPanel panel){


        //Ziskani pozice, na kterou se ma figurka presunout (velka rosada = 3, mala rosada = 5)
        int rookX = big?3:5;

        //Ziskani puvodni pozice
        Point old = new Point(this.x,this.y);


        //Figurka se presune na pozici, na kterou se ma presunout
        this.x = rookX;


        //Spust animaci
        Animation(old, new Point(this.x, this.y), panel);


        //Nastav figurku na nove pozici
        board.setFigureOn(this.x, this.y, this);

        //Nastav figurce, že již nejde o první tah
        firstMove = false;

        //Ulož původní a novou pozici
        board.setOld_a_new(new Point[]{old, new Point(this.x, this.y)});

        //Odstran figurku z původní pozice
        board.removeFigureOn(old.x, old.y);
    }





    /**
     * Metoda pro vykresleni figurky
     * @param g2 graficky kontext
     */
    public void paint(Graphics2D g2) {

        //Zavolani metody predka
        super.paint(g2);

        //Velikost jednoho pole
        int field;


        //Pokud je volano z menu, pouzit jinou velikost
        if(!menu){
            field = board.getFieldSize();
        }
        else{
            field = drawSize;
        }


        //Vytvoreni veze
        Polygon tower = new Polygon();
        tower.addPoint(offset + (rectBase/4), offset+ field/4); //pocatek
        tower.addPoint(offset + (rectBase/4),offset+ field/2);  //Kolmo nahoru


        int upper = (int)Math.ceil((rectBase - 4*offset)/5.0);
        int xStart =(int) Math.ceil((field- upper*5)/2.0);

        //Prvni zub
        tower.addPoint(xStart, offset+ field/2); //doleva
        tower.addPoint(xStart,offset + 3*field/4);//nahoru
        tower.addPoint(xStart + upper,offset + 3*field/4); //doprava
        tower.addPoint(xStart + upper,offset + field -4*field/10); //dolu

        //Druhy zub
        tower.addPoint(xStart + 2*upper,offset + field -4*field/10); //doprava
        tower.addPoint(xStart + 2*upper,offset + 3*field/4); //nahoru
        tower.addPoint(xStart + 3*upper,offset + 3*field/4); //doprava
        tower.addPoint(xStart + 3*upper,offset + field -4*field/10); //Dolu

        //Treti zub
        tower.addPoint(xStart + 4*upper,offset + field -4*field/10); //doprava
        tower.addPoint(xStart + 4*upper,offset + 3*field/4); //nahoru
        tower.addPoint(xStart + 5*upper,offset + 3*field/4); //doprava
        tower.addPoint(xStart + 5*upper,offset + field -4*field/10); //Dolu
        tower.addPoint(xStart + 5*upper,offset+ field/2); //dolu


        tower.addPoint((int) Math.ceil(offset + (3*rectBase/4.0)), offset+ field/2); //doleva
        tower.addPoint((int) Math.ceil(offset + (3*rectBase/4.0)), offset+ field/4); //konec


        //Vykresleni figurky - vyplneni a obrys
        Figure.add(new Area(tower));

        //Zavolani metody finalPaint z predka
        finalPaint(g2);



    }


}
