package vtag.vorhangkontrolle;

/**
 * Kind of like an enum of the possible commands.
 *
 * @author Moritz Höwer
 * @version 1.0 - 17.03.2017
 */
public class Command {

    public static final Command OPEN = new Command(new CommandDisplayRule(R.color.white,
            R.drawable.open_symbol, R.string.open_text));

    public static Command forName(String name) {
        switch (name) {
            case "OPEN":
                return OPEN;
            default:
                throw new IllegalArgumentException("There is no command with the name " + name);
        }
    }

    private CommandDisplayRule displayRule;

    private Command(CommandDisplayRule displayRule) {
        this.displayRule = displayRule;
    }

    public CommandDisplayRule getDisplayRule(){
        return displayRule;
    }

}
