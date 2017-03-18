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
    private final int backgroundColorID;

    /**
     * the image to display
     */
    private final int imageID;

    /**
     * the text to display
     */
    private final int textID;

    public CommandDisplayRule(int backgroundColorID, int imageID, int textID) {
        this.backgroundColorID = backgroundColorID;
        this.imageID = imageID;
        this.textID = textID;
    }

    public int getBackgroundColorID() {
        return backgroundColorID;
    }

    public int getImageID() {
        return imageID;
    }

    public int getTextID() {
        return textID;
    }
}
