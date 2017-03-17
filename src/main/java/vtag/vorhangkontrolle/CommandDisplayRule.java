package vtag.vorhangkontrolle;

/**
 * How to display the commands on screen.
 *
 * @author Moritz Höwer
 * @version 1.0 - 17.03.2017
 */
public class CommandDisplayRule {

    /**
     * the background color
     */
    public final int backgroundColorID;

    /**
     * the image to display
     */
    public final int imageID;

    /**
     * the text to display
     */
    public final int textID;

    public CommandDisplayRule(int backgroundColorID, int imageID, int textID){
        this.backgroundColorID = backgroundColorID;
        this.imageID = imageID;
        this.textID = textID;
    }

}
