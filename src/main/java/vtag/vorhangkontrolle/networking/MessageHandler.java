package vtag.vorhangkontrolle.networking;

/**
 * For handling messages from the client.
 *
 * @author Moritz Höwer
 * @version 1.0 - 18.03.2017
 */
public interface MessageHandler {

    /**
     * Handle a message from the client.
     *
     * @param message the message.
     */
    void handleMessage(String message);
}
