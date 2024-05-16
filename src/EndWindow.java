import javax.swing.*;

public class EndWindow extends JFrame {


        public EndWindow(String message, Player winner, Game game) {


//            GameWindow.frame.setEnabled(false);
//            int des = JOptionPane.showConfirmDialog(null, message + "\nDo you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
//            if(des == JOptionPane.YES_OPTION){
//                Chess_SP_2023.reset();
//            }
//            else System.exit(0);

            this.setSize(GameWindow.gamePanel.getWidth()/2, (int) (GameWindow.gamePanel.getHeight()/1.5));
            this.setLocationRelativeTo(GameWindow.frame);
            this.setAlwaysOnTop(false);
            setResizable(false);
            this.add(new EndPanel(message, winner,this,game));
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setUndecorated(true);


            this.setVisible(true);


        }





}
