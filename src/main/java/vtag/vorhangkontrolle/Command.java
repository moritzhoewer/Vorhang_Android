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

    public static final Command CLOSE = new Command(new CommandDisplayRule(R.color.white,
            R.drawable.close_symbol, R.string.close_text));

    public static final Command STOP = new Command(new CommandDisplayRule(R.color.red,
            R.drawable.stop_symbol, R.string.stop_text));

    public static final Command WAIT = new Command(new CommandDisplayRule(R.color.yellow,
            R.drawable.wait_symbol, R.string.wait_text));

    public static final Command PERFECT = new Command(new CommandDisplayRule(R.color.lightGreen,
            R.drawable.perfect_symbol, R.string.perfect_text));

    public static Command forName(String name) {
        switch (name) {
            case "CMD_OPEN":
                return OPEN;
            case "CMD_CLOSE":
                return CLOSE;
            case "CMD_STOP":
                return STOP;
            case "CMD_WAIT":
                return WAIT;
            case "CMD_PERFECT":
                return PERFECT;
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
