import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class Main {
    public static void main(String[] args) throws Exception {
        TerminalSize ts = new TerminalSize(100, 35);
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminalFactory.setInitialTerminalSize(ts);
        Terminal terminal2 = terminalFactory.createTerminal();
        GameField gameField = new GameField();

        terminal2.setCursorVisible(false); //Gömmer pekaren

        TerminalSize terminalSize = terminal2.getTerminalSize(); //Hämtar storleken på terminalen
        int columns = terminalSize.getColumns(); //sätter en variabel till maxvärdet av bredden (x-värdet)
        int rows = terminalSize.getRows(); //sätter en variabel till maxvärdet av höjden (y-värdet)

        boolean continueReadingInput = true;

    }
}
