import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;


/**
 *
 * Okno s grafem pro zobrazeni casu tahu hracu
 * Sloupcový graf
 *
 * @author Roman Pejs
 */
public class GraphWindow extends JFrame {


    int page = 0;
    ArrayList<Double>[] data;
    ChartPanel chartPanel;



    JPanel panel;

    Player[] players;

    ArrayList<Double>[] playerTimes;

    JButton next;
    JButton prev;

    /**
     * Konstruktor
     * @param playerTimes pole s casy tahu hracu
     */
    public GraphWindow(ArrayList<Double>[] playerTimes, Player[] players, JFrame parent){


        this.playerTimes = playerTimes;
        next = new JButton(">>");
        prev = new JButton("<<");


        panel = new JPanel();

        panel.setLayout(new BorderLayout());
        panel.add(next,BorderLayout.EAST);
        panel.add(prev,BorderLayout.WEST);
        prev.setEnabled(false);



        int l = Math.max(playerTimes[0].size(), playerTimes[1].size());

        if(l == 0)
            l = 1;

        int pages = (int) Math.ceil(l/(float)DATACOUNT);

        if(pages == 1)
            next.setEnabled(false);



        next.addActionListener(e -> {
            page++;
            updatePanel();
            if(page == pages-1)
                next.setEnabled(false);
            prev.setEnabled(true);

        });

        prev.addActionListener(e -> {

            page--;

            updatePanel();
            if(page == 0)
                prev.setEnabled(false);
            next.setEnabled(true);
        });


        reduceData(playerTimes);

        //vytvoreni okna
        setTitle("Graf tahů - Roman Pejs - A22B0197P");

        this.players = players;

        //Defaultni velikost okna
        pack();
        setMinimumSize(new Dimension(800, 600));


        chartPanel = new ChartPanel(createBarChart(data));
        //vytvoreni grafu a pridani do okna
        panel.add(chartPanel,BorderLayout.CENTER);

        add(panel);
        //zakazani okna s hrou
        parent.setEnabled(false);

        //Pri zavreni okna s grafem se okno s hrou znovu povoli
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.setEnabled(true);
                parent.toFront();
            }
        });

        //Pri zavreni okna se toto disposne
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        setLocationRelativeTo(null);

        //zobrazeni okna
        setVisible(true);


    }


    private void updatePanel(){
        panel.remove(chartPanel);


        reduceData(playerTimes);
        panel.remove(chartPanel);
        ChartPanel newChartPanel = new ChartPanel(createBarChart(data));

        // add new chart panel in place of old chart panel
        panel.add(newChartPanel, BorderLayout.CENTER);
        chartPanel = newChartPanel;

        // redraw the frame
        panel.revalidate();
        panel.repaint();

    }
    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        panel.add(chartPanel,BorderLayout.CENTER);

    }

    final int DATACOUNT = 15;

    private void reduceData(ArrayList<Double>[] playerTimes){
        data = new ArrayList[2];
        data[0] = new ArrayList<>();
        data[1] = new ArrayList<>();


        int start = page*DATACOUNT;

        for(int i = start;i<DATACOUNT + start ;i++){
            try{
                data[0].add(playerTimes[0].get(i));
            }catch (Exception ignored){}

            try{
                data[1].add(playerTimes[1].get(i));
            }catch (Exception ignored){}

        }
    }

    /**
     * Vytvori graf z pole s casy tahu hracu
      * @param data pole s casy tahu hracu
     * @return graf
     */
    private JFreeChart createBarChart(ArrayList<Double>[] data) {


        //vytvoreni datasetu
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();


        //Ziskani delky nejdelsiho pole
        int l = Math.max(data[0].size(), data[1].size());


        //Pridani dat do datasetu
        for (int i = 0; i < l; i++) {

            try{
                dataset.addValue(data[0].get(i), players[0].toString(), (String.valueOf(i+1 + page*DATACOUNT)));
            }catch (Exception ignored){}

            try{
                dataset.addValue(data[1].get(i),players[1].toString(), (String.valueOf(i+1 +page*DATACOUNT)));
            }catch (Exception ignored){}


        }


        //Vygenerovani grafu
        JFreeChart chart = ChartFactory.createBarChart(
                "Časy hráčů",
                "Tah" + " (strana " + (page+1) + ")",
                "Čas",
                dataset,
                PlotOrientation.VERTICAL, // Chart orientation
                false,         // Include legend
                true,         // Include tooltips
                false
        );



        //Nastaveni stylu grafu
        CategoryPlot plot = chart.getCategoryPlot();

        //Nastaveni pozadi
        plot.setBackgroundPaint(new Color(201, 201, 201));

        //Nastaveni car
        plot.setRangeGridlinePaint(Color.black);

        //Nastaveni popisku osy X
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.STANDARD);






        //Nastaveni popisku
        CategoryItemRenderer renderer = plot.getRenderer();

        //Labely nad sloupci
        renderer.setDefaultItemLabelsVisible(true);

        //Jaka data se maji zobrazovat
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "{2}s", NumberFormat.getNumberInstance()
        ));

        //Styl popisku
        renderer.setDefaultItemLabelFont(new Font("Arial", Font.PLAIN, 9));


        //Nastaveni jednotlivych sloupcu
        BarRenderer br =(BarRenderer) renderer;

        //Nastaveni mezery mezi sloupci
        br.setItemMargin(-0.2);

        //Nastaveni stinu
        ((BarRenderer) renderer).setShadowVisible(false);

        //Maximalni sirka sloupce
        br.setMaximumBarWidth(0.01);

        //Nastaveni barvy sloupcu
        br.setBarPainter(new StandardBarPainter());
        br.setSeriesPaint(1, new Color(66, 64, 64));
        br.setSeriesPaint(0, new Color(255, 255, 255));

        //Vrati graf
        return chart;
    }



}
