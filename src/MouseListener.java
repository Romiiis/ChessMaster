import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Tato trida reprezentuje a implementuje Listenery pro klikani mysi
 * - Drag and Drop
 * - Kliknuti
 * - Pohyb mysi
 * - Pusteni tlacitka
 *
 * @author Roman Pejs
 */
public class MouseListener {

    //Reference na panel
    private final GamePanel panelRef;

    //Reference na sachovnici
    private final Board board;

    //Reference na figurku, ktera je prave presouvana
    private AFigure figurka;

    //Zda je tlacitko mysi stisknuto
    private boolean isPressed;







    /**
     * Konstruktor
     * @param panel - reference na panel
     * @param board - reference na sachovnici
     */
    public MouseListener(GamePanel panel, Board board){

        panelRef = panel;
        this.board = board;

        mouseClickLis();
        mouseReleaseLis();
        mouseDragLis();
        mouseMoveLis();

    }


    /**
     * Metoda pro nastaveni listeneru pro pohyb mysi
     */
    private void mouseMoveLis(){
        panelRef.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {

                super.mouseMoved(e);

                //Pokud se provadi promena figurky tak se neprovadi nic
                if(board.promoting){
                    return;
                }

                for(Player p: board.getPlayers()){

                    //Pokud je tlacitko mysi nad avatarem hrace

                    p.mouseEntered = p.getButton().contains(e.getPoint());

                    //Pokud je tlacitko nad stolem pro vyhozene figurky
                    Point2D transformedPoint = p.tableInverse.transform(e.getPoint(),null);

                    p.tableEntered = p.getTableArea().contains(transformedPoint);


                    //Prekresleni panelu
                    panelRef.repaint();
                }
            }
        });
    }


    /**
     * Nastaveni listeneru pro klik mysi
     */
    private void mouseClickLis(){
        panelRef.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                super.mousePressed(e);

                //Pokud se provadi promena figurky tak se neprovadi nic
                if(board.promoting){
                    return;
                }

                //Ziskani souradnic mysi
                int x = e.getX();
                int y = e.getY();


                //Pokud se kliklo na avatar hrace tak se zobrazi graf
                for(Player player : board.getPlayers()) if(player.getButton().contains(new Point2D.Double(x,y))) new GraphWindow(board.game.getPlayerTimes(), board.getPlayers(), GameWindow.frame);

                //Ziskani hrace, ktery je na tahu
                Player turn = board.getTurnPlayer();
                if(turn.isCpu) return;

                //Prochazeni figurkami hrace
                for(int i = 0;i<turn.getFiguresPack().length;i++){

                    if(turn.getFiguresPack()[i] != null && isFigHere(x,y,i,turn)){

                        //Nastaveni figurky
                        figurka = turn.getFiguresPack()[i];
                            //Nastaveni drag mode
                            figurka.setDragged(true);
                            //Nastaveni stisknuti tlacitka
                            isPressed = true;

                    }
                }



            }
        });
    }


    /**
     * Nastaveni listeneru pro pusteni tlacitka mysi
     *
     */
    private void mouseReleaseLis(){

        panelRef.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                //Pokud je tlacitko mysi pusteno a bylo stisknuto
                if (e.getButton() == MouseEvent.BUTTON1 && isPressed && figurka != null) {

                    //Nastaveni stisknuti tlacitka na false
                    isPressed = false;

                    //Nastaveni drag mode na false
                    figurka.setDragged(false);

                    boolean moved = false;
                    //Provede tah figurky (Polozi na novou pozici, vymaze ze stare, a pokud na novem policku je figurka, tak ji vymaze)
                    if(figurka.makeMove(e.getX(), e.getY(),figurka,panelRef)){
                        moved = true;
                    };

                    figurka.getValidMoves(true);

                    //Smaze referenci na figurku
                    figurka = null;

                    //Prekresleni panelu
                    panelRef.repaint();

                    if(moved){
                        King king = board.getTurnPlayer().getKing();
                        king.checkState();
                    }


                    //print();

                }
            }
        });
    }


    /**
     * Nastaveni listeneru pro Drag mysi
     */
    private void mouseDragLis(){

        panelRef.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                //Pokud je tlacitko mysi stisknuto
                if(isPressed){
                    //Pokud je figurka v drag mode
                    if(figurka.isDragged()){
                        //Pohyb figurky
                        moveFigure(e, figurka);
                    }
                }

            }
        });
    }


    /**
     * Vypise sachovnici do konzole
     */
    private void print(){


        for(int i = 0;i<8*25;i++){
            System.out.format("%s","_");
        }
        System.out.println();
        for(int i = 0;i<8;i++){
            for(int j = 0;j<8;j++){

                if(board.getFigureOn(j,i) != null){
                    AFigure figure = board.getFigureOn(j,i);
                    String figureString = figure.getClass().getSimpleName()+" "+figure.getPlayer().getColor();
                    System.out.format("| %-20s",figureString);
                }
                else System.out.format("| %-20s","-");
            }
            System.out.println();
        }

        for(int i = 0;i<8*25;i++){
            System.out.format("%s","_");
        }
        System.out.println();


    }


    /**
     * Zjisti zda je kliknuti na figurku
     * @param mX - souradnice mysi x
     * @param mY - souradnice mysi y
     * @param packI index figurky u hrace
     * @param p hrac
     * @return (true) pokud ano, (false) pokud ne
     */

    private boolean isFigHere(int mX, int mY, int packI, Player p){

        //Vybrana figurka
        AFigure chosenFig = p.getFiguresPack()[packI];

        //Inverzni transformace
        AffineTransform af = chosenFig.getInverseTransform();

        //Transformace souradnic mysi
        Point2D inv =af.transform(new Point(mX,mY), null);

        //Vraceni zda je kliknuti na figurku
        return chosenFig.getArea().contains(inv.getX(), inv.getY());
    }


    /**
     * Drag and move figurky
     * @param e - event
     * @param figurka - figurka
     */
    private void moveFigure(MouseEvent e, AFigure figurka){


        //Nastaveni drag pozice
        figurka.dragPos = new Point(e.getX(),e.getY());

        //Prekresleni panelu
        panelRef.repaint();

    }


}
