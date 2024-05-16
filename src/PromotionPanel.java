import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


/**
 * Tento panel se zobrazi pri promoci pesaka,
 * Poskytuje 4 tlacitka pro vyber figurky
 *
 * @author Roman Pejs
 */
public class PromotionPanel extends JPanel {


    //Dialog, ktery obsahuje tento panel
    private JDialog dialog;

    //Hraci deska
    private final Board board;

    //Pesak, ktery se promuje
    private final Pawn pawn;

    //ArrayList tlacitek pro vyber figurky
    private final ArrayList<MyButton> buttons = new ArrayList<>();


    /**
     * Konstruktor
     * @param board - hraci deska
     * @param pawn - pesak, ktery se promuje
     */
    public PromotionPanel(Board board, Pawn pawn) {

        super();

        //Prirazeni promennych
        this.board = board;
        this.pawn = pawn;

        //Nastaveni statusu promovani
        board.promoting = true;



        //Layout panelu
        setLayout(new GridLayout(4,1));

        //Vytvoreni tlacitek
        buttons.add(new MyButton("Queen", board.getTurnPlayer().getColor()));
        buttons.add(new MyButton("Rook", board.getTurnPlayer().getColor()));
        buttons.add(new MyButton("Bishop", board.getTurnPlayer().getColor()));
        buttons.add(new MyButton("Knight", board.getTurnPlayer().getColor()));



        //Nastaveni jednotlivych akci tlacitek

        //Promoci na damu
        buttons.get(0).addActionListener(e -> {
            board.promotion(pawn, new Queen(pawn.getPos().x,pawn.getPos().y,pawn.getPlayer()));
            dialog.dispose();
            board.promoting = false;
            if(pawn.getPlayer().getEnemy().isCpu) pawn.getPlayer().getEnemy().cpu.makeMove();


        });


        //Promoci na vez
        buttons.get(1).addActionListener(e -> {
            board.promotion(pawn, new Rook(pawn.getPos().x,pawn.getPos().y,pawn.getPlayer()));
            dialog.dispose();
            board.promoting = false;
            if(pawn.getPlayer().getEnemy().isCpu) pawn.getPlayer().getEnemy().cpu.makeMove();


        });


        //Promoci na strelec
        buttons.get(2).addActionListener(e -> {
            board.promotion(pawn, new Bishop(pawn.getPos().x,pawn.getPos().y,pawn.getPlayer()));
            dialog.dispose();
            board.promoting = false;
            if(pawn.getPlayer().getEnemy().isCpu) pawn.getPlayer().getEnemy().cpu.makeMove();


        });


        //Promoci na jezdce
        buttons.get(3).addActionListener(e -> {
            board.promotion(pawn, new Knight(pawn.getPos().x,pawn.getPos().y,pawn.getPlayer()));
            dialog.dispose();
            board.promoting = false;

            if(pawn.getPlayer().getEnemy().isCpu) pawn.getPlayer().getEnemy().cpu.makeMove();
        });



        //Pridani tlacitek do panelu
        add(buttons.get(0));
        add(buttons.get(1));
        add(buttons.get(2));
        add(buttons.get(3));



        //Vytvoreni dialogu
        dialog = new JDialog(GameWindow.frame);

        //Bez ramu
        dialog.setUndecorated(true);

        //Nastaveni panelu do dialogu
        dialog.setContentPane(this);


        //Nastaveni vychoziho chovani dialogu
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(board.getPanel());
        dialog.setAlwaysOnTop(false);
        dialog.pack();


        //Panel hraci desky se deaktivuje
        board.getPanel().setEnabled(false);


        //Nastaveni velikosti dialogu podle velikosti hraci desky
        dialog.setSize(board.getFieldSize(),board.getFieldSize()*5);


        dialog.setVisible(true);




    }


    /**
     * Metoda pro vykresleni panelu
     * @param g  Graficky kontext
     */
    public void paint(Graphics g){

        //Zavolani predka
        super.paint(g);


        //Ziskani souradnic !panelu! na obrazovce
        int PX = board.getPanel().getLocationOnScreen().x;
        int PY = board.getPanel().getLocationOnScreen().y;


        //Vypocet rozdilu mezi souradnicemi celeho okna a panelu
        int difX = Math.abs(PX - GameWindow.frame.getLocationOnScreen().x);
        int difY = Math.abs(PY - GameWindow.frame.getLocationOnScreen().y);

        int y;

        //Zjisteni barvy hrace, ktery ma na tahu
        if(pawn.getPlayer().getColor() == PLAYERCOLOR.WHITE){

            //Nastaveni pozice dialogu podle pozice pesaka
             y = (int) (GameWindow.frame.getLocationOnScreen().y + board.getCorner().getY() + board.getFieldSize()*pawn.getPos().y)  + difY + board.getFieldSize();
        }
        else{

            //Nastaveni pozice dialogu podle pozice pesaka
            y = (int) (GameWindow.frame.getLocationOnScreen().y + board.getCorner().getY() + board.getFieldSize()*pawn.getPos().y)  + difY - board.getFieldSize()*5;
        }

        //Nastaveni pozice dialogu podle pozice pesaka
        int x = (int) (GameWindow.frame.getLocationOnScreen().x + board.getCorner().getX() + board.getFieldSize()*pawn.getPos().x) + difX;

        //Nastaveni pozice dialogu
        dialog.setLocation(x,y);

        //Nastaveni velikosti dialogu podle velikosti hraci desky
        dialog.setSize(board.getFieldSize(),board.getFieldSize()*5);

        //Nastaveni velikosti tlacitek podle velikosti hraci desky
        for (MyButton button : buttons) {
            button.setSize(board.getFieldSize());
        }

        //Prekresleni dialogu
        dialog.revalidate();
    }




}


