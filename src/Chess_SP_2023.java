import javax.swing.*;
import java.awt.*;


/**
 * Hlavni trida programu
 * @author Roman Pejs
 */
public class Chess_SP_2023{



	/**
	 * Hlavni metoda
	 * Vygeneruje okno a zobrazi ho
	 * @param args nevyuzito
	 */
	public static void main(String[] args) {

		//vytvoreni okna
		createFrame();


	}


	/**
	 * Resetuje hru
	 */
	public static void reset() {
		GameWindow.frame.dispose();
		createFrame();

	}


	/**
	 * Vytvori okno
	 */
	private static void createFrame(){
		JFrame menu = new JFrame();

		menu.setTitle("Chess master 2023 - Roman Pejs - A22B0197P");



		MenuPanel menuPanel = new MenuPanel(menu);
		menu.add(menuPanel);
		//Defaultni velikost okna

		menu.pack();


		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		int w = (int) screenSize.getWidth()/2;
		int h = (int) ((int) screenSize.getHeight()/1.5);



		menu.setMinimumSize(new Dimension(w,h));
//		menu.setResizable(false);

		menu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		menu.setLocationRelativeTo(null);
		menu.setVisible(true);
	}





}
