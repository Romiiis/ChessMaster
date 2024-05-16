import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class RandomCPU implements ICPU{

    Player player;
    JPanel panel;

    Random random = new Random();

    public RandomCPU(Player player, JPanel panel){
        this.player = player;
        this.panel = panel;

    }

    public void makeMove(){


        int waitTime = random.nextInt(5000) + 1000;



        Timer timer = new Timer(1000, e -> move());
        timer.setRepeats(false);
        timer.start();




    }

    private void move(){


        int randomFigure = 0;
        ArrayList<Point> moves = null;

        while(moves == null || moves.size() == 0){

            if(player.getBoard().end) return;

            System.out.println(" ");
            System.out.println("RandomCPU hledá validní tahy");
            System.out.println(" ");

            randomFigure = random.nextInt(player.getFiguresPack().length);
            moves = new ArrayList<>(java.util.List.of(player.getFiguresPack()[randomFigure].getValidMoves(true)));

        }



        if(player.getFiguresPack()[randomFigure] instanceof Pawn){
            Point[] enpassantMoves = ((Pawn) player.getFiguresPack()[randomFigure]).enPassantMoves();
            moves.addAll(java.util.List.of(enpassantMoves));


        }
        int randomMove = random.nextInt(moves.size());
        Point move = moves.get(randomMove);

        //Convert to panel coordinates
        move.x = (int) (player.getBoard().getCorner().getX() + move.x * player.getBoard().getFieldSize());
        move.y = (int) (player.getBoard().getCorner().getY() + move.y * player.getBoard().getFieldSize());


        player.getFiguresPack()[randomFigure].makeMove(move.x, move.y,player.getFiguresPack()[randomFigure],panel);

        player.getEnemy().getKing().checkState();
    }

    public void promote(Pawn pawn, AFigure figure){
        player.getBoard().promotion(pawn,figure);
//        player.getEnemy().getKing().checkState();
    }




}
