import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static ArrayList<GameObject> gameObjects = new ArrayList<>();

    static Player player;

    public static void main(String[] args) throws Exception {

        TerminalSize ts = new TerminalSize(100, 35);
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminalFactory.setInitialTerminalSize(ts);
        Terminal terminal2 = terminalFactory.createTerminal();

        terminal2.setCursorVisible(false); //Gömmer pekaren
        TerminalSize terminalSize =
                terminal2.getTerminalSize(); //Hämtar storleken på terminalen
        int columns =
                terminalSize.getColumns(); //sätter en variabel till maxvärdet av bredden (x-värdet)
        int rows =
                terminalSize.getRows(); //sätter en variabel till maxvärdet av höjden (y-värdet)

        GameField gameField = new GameField(columns,rows);

        long startY = rows/2;
        long startX = columns/10;
        player = new Player(startX, startY);

        boolean continueReadingInput = true;



    }

    private static void InputOutput(){
        while (continueReadingInput) {

            KeyStroke keyStroke = null;
            int oldX = player.x;
            int oldY = player.y;

            int timeStep = 0;

            do {
                Thread.sleep(8); //might throw InterruptedException
                keyStroke = terminal2.pollInput();
                if (timeStep > 100) {
                    timeStep = 0;
                    newPosition();
                    moveMonsters(terminal2);
                    if (checkCrash()) {
                        gameOver(terminal2);
                    }
                }
                timeStep++;
            } while (keyStroke == null);

            KeyType type = keyStroke.getKeyType();
            Character c = keyStroke.getCharacter();
            System.out.println(type + ", " + c);

            if (c == Character.valueOf('q') || c == Character.valueOf('Q')) {
                continueReadingInput = false;
                terminal2.close();
                System.out.println("quit");
            }

            callSwitch(keyStroke);

            if (LocalTime.now().isAfter(lastTimeMode.plusNanos(800000))) {
                newPosition();
                lastTimeMode = LocalTime.now();
            }

            /*if (bombPosition.x == x && bombPosition.y == y) {
                crashIntoBomb = true;
            }*/

            if (checkCrash()) {
                gameOver(terminal2);
                continueReadingInput = false;

            } else {
                printOnScreen(terminal2, oldX, oldY);
            }
            terminal2.flush();
        }
    }
}
