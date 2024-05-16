import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class StockFishCPU implements ICPU{


    private final Player player;
    private JPanel panel;


    volatile String bestMove = null;



    public StockFishCPU(Player player, JPanel panel){
        this.player = player;
        this.panel = panel;

    }

    @Override
    public void makeMove() {


        String FEN = player.getBoard().createFEN();



        Thread t = new Thread(() -> {
            Process engineProcess;
            try {
                engineProcess = Runtime.getRuntime().exec("lib\\stockfish\\StockFish.exe");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            BufferedReader engineInput = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            PrintWriter engineOutput = new PrintWriter(engineProcess.getOutputStream(), true);

            engineOutput.println("isready");

            String response;
            try {
                while ((response = engineInput.readLine()) != null) {
                    if (response.equals("readyok")) {
                        break;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

            engineOutput.println("uci");

            // Wait for the engine to respond with "uciok"
            String response2;

            try {
                while ((response2 = engineInput.readLine()) != null) {
                    if (response2.equals("uciok")) {
                        break;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }




            engineOutput.println("position fen " + FEN);

            engineOutput.println("setoption name Skill Level value 20");
//            engineOutput.println("go depth 50 movetime 5000");
            engineOutput.println("go movetime 5000");


            String bestMove;

            try {
                while ((response = engineInput.readLine()) != null) {
                    if (response.startsWith("bestmove")) {
                        String[] parts = response.split("\\s+");
                        bestMove = parts[1];
                        engineOutput.println("quit");

                        decodeMove(bestMove);
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        player.getEnemy().getKing().checkState();
                        System.out.println("Vybraný tah: " + bestMove);

                        break;
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }


        });

        t.start();
        try {
            t.join(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }



    @Override
    public void promote(Pawn pawn, AFigure figure) {
        pawn.getPlayer().getBoard().promotion(pawn,figure);

    }

        private void decodeMove(String move){


        char[] chars = move.toCharArray();

            int fromX = move.charAt(0) - 'a';
            int fromY = 8 - (Integer.parseInt(String.valueOf(move.charAt(1))));

            int toX = move.charAt(2) - 'a';
            int toY = 8 - (Integer.parseInt(String.valueOf(move.charAt(3))));

            String FEN = player.getBoard().createFEN();
            String[] FENParts = FEN.split(" ");
            String enPassant = FENParts[3];

            Point enPassantPoint;
            if(enPassant.equals("-")){
                enPassantPoint = null;
            }
            else{
                int enPassantX = enPassant.charAt(0) - 'a';
                int enPassantY = 8 - (Integer.parseInt(String.valueOf(enPassant.charAt(1))));
                enPassantPoint = new Point(enPassantX,enPassantY);
            }

            if(player.getBoard().getFigureOn(toX, toY) == null && enPassantPoint != null) {
                if (enPassantPoint.x == toX && enPassantPoint.y == toY) {
                    player.getBoard().getFigureOn(enPassantPoint.x, enPassantPoint.y - 1).makeMove((int) (player.getBoard().getCorner().getX() + toX * player.getBoard().getFieldSize()), (int) (player.getBoard().getCorner().getY() + toY * player.getBoard().getFieldSize()), player.getBoard().getFigureOn(enPassantPoint.x, enPassantPoint.y - 1), panel);

                }
            }





        if(chars.length == 4){


            AFigure figure = player.getBoard().getFigureOn(fromX, fromY);


            if(figure instanceof King king){

                int YR = (int) (player.getBoard().getCorner().getY() + king.getPos().y * player.getBoard().getFieldSize());
                int XR = toX;


                if(toX == 6){

                    XR = (int) (player.getBoard().getCorner().getX() + 7 * player.getBoard().getFieldSize());
                }
                if(toX == 2){
                    XR = (int) (player.getBoard().getCorner().getX() + 0);
                }


                figure.makeMove(XR,YR,figure,panel);
                figure.board.getTurnPlayer().getEnemy().getKing().checkState();

            }


            int x = (int) (player.getBoard().getCorner().getX() + toX * player.getBoard().getFieldSize());
            int y = (int) (player.getBoard().getCorner().getY() + toY * player.getBoard().getFieldSize());

            figure.makeMove(x, y,figure,panel);

        }
        else if(chars.length == 5) {
                char promotion = chars[4];

                Pawn pawn = (Pawn) player.getBoard().getFigureOn(fromX, fromY);
                AFigure figure = null;

            switch (promotion) {
                case 'q' -> figure = new Queen(toX,toY, pawn.getPlayer());
                case 'r' -> figure = new Rook(toX,toY, pawn.getPlayer());
                case 'b' -> figure = new Bishop(toX,toY, pawn.getPlayer());
                case 'n' -> figure = new Knight(toX,toY, pawn.getPlayer());
            }

                int x = (int) (player.getBoard().getCorner().getX() + toX * player.getBoard().getFieldSize());
                int y = (int) (player.getBoard().getCorner().getY() + toY * player.getBoard().getFieldSize());

                pawn.makeMove(x,y,pawn,panel);
                promote(pawn,figure);

        }



        }



}

