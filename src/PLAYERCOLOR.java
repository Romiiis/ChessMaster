import java.awt.*;

/**
 * Vycet barvy hrace
 *
 * @author Roman Pejs
 */
public enum PLAYERCOLOR {


    //Vycet barvy hrace a jejich vykreslovaci barvy
    WHITE(new Color(248,248,248),Color.BLACK), BLACK(new Color(86,83,82),Color.BLACK);


    //Barva hrace
    private final Color color;

    //Barva obrysu hrace
    private final Color strokeColor;

    /**
     * Konstruktor vycetoveho typu
     * @param color - barva hrace
     * @param StrokeColor - barva obrysu hrace
     */
    PLAYERCOLOR(Color color,Color StrokeColor){
        this.color = color;
        this.strokeColor = StrokeColor;
    }


    /**
     * Vraci barvu hrace
     * @return barva hrace
     */
    public Color getColor(){
        return color;
    }


    /**
     * Vraci barvu obrysu hrace
     * @return barva obrysu hrace
     */
    public Color getStrokeColor(){
        return strokeColor;
    }


}
