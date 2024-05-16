import java.util.ArrayList;


/**
 * Trida reprezentuje hru
 *
 * @author Roman Pejs
 */
public class Game {

    //Instance tridy Board
    private final Board board;


    /**
     * Konstruktor pro vytvoreni hry
     * @param panel - panel na ktery se bude hra vykreslovat
     */
    public Game(GamePanel panel, String p1, String p2, int[] time, int[] increment, int enemyType){

        board = new Board(panel, this, p1, p2, time, increment,enemyType);
        new MouseListener(panel,board);
    }


    /**
     * Vrati pole s casy tahu hracu
     * @return pole s casy tahu hracu
     */
    public ArrayList[] getPlayerTimes(){

        ArrayList[] times = new ArrayList[2];
        times[0] = (board.getPlayers()[0].getMoveTimes());
        times[1] = (board.getPlayers()[1].getMoveTimes());


        return times;
    }


    /**
     * Vrati instanci tridy Board
     * @return instance tridy Board
     */
    public Board getBoard(){
        return board;
    }
}
