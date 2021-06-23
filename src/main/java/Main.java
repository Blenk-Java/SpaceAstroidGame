import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.googlecode.lanterna.input.KeyType.ArrowDown;

public class Main {

    static ArrayList<GameObject> gameObjects = new ArrayList<>();

    static Player player;
    static Random random = new Random();

    public static void main(String[] args) throws Exception {

        TerminalSize ts = new TerminalSize(100, 35);
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminalFactory.setInitialTerminalSize(ts);
        Terminal terminal = terminalFactory.createTerminal();

        terminal.setCursorVisible(false); //Gömmer pekaren
        TerminalSize terminalSize = terminal.getTerminalSize(); //Hämtar storleken på terminalen
        int columns = terminalSize.getColumns(); //sätter en variabel till maxvärdet av bredden (x-värdet)
        int rows = terminalSize.getRows(); //sätter en variabel till maxvärdet av höjden (y-värdet)

        GameField gameField = new GameField(columns,rows);

        int startY = rows/2; //ska vi byta till int så blir det bättre koppling till columns & rows?
        int startX = columns/10;
        player = new Player(startX, startY);
        boolean continueReadingInput = true;


    }

    private static void InputOutput(Boolean continueReadingInput, Terminal terminal, int rows) throws Exception {
        while (continueReadingInput) {

            KeyStroke keyStroke = null;
            int oldX = player.getX(); //Dubbelkolla så att dessa get-metoder finns
            int oldY = player.getY();

            int timeStep = 0;

            do {
                Thread.sleep(8); //might throw InterruptedException
                keyStroke = terminal.pollInput();
                if (timeStep > 100) {
                    timeStep = 0;
                    newPosition(terminal); //metod för side scroller
                    moveAstroids(terminal); //metod för objekthanteraren
                    if (checkCrash()) { //metod för kollisionskontroll
                        gameOver(terminal); //metod för game over
                    }
                }
                timeStep++;
            } while (keyStroke == null);

            KeyType type = keyStroke.getKeyType();
            Character c = keyStroke.getCharacter();
            System.out.println(type + ", " + c);

            if (c == Character.valueOf('q') || c == Character.valueOf('Q')) {
                continueReadingInput = false;
                terminal.close();
                System.out.println("quit");
            }

            if (!checkPlayer(rows)) { //Kollar så att spelaren fortfarande är inom terminalfönster
                putPlayerBack(rows);
            }
            callMovementManeuver(keyStroke);

            if (LocalTime.now().isAfter(lastTimeMode.plusNanos(800000))) {
                newPosition(terminal);
                lastTimeMode = LocalTime.now();
            }

            if (checkCrash()) {
                gameOver(terminal);
                continueReadingInput = false;

            } else {
                printOnScreen(terminal, oldX, oldY);
            }
            terminal.flush();
        }
    }
    private static void gameOver(Terminal terminal,int rows,int columns) throws Exception {
        terminal.clearScreen();
        terminal.setCursorPosition(columns/2,rows/2);
        String str = "GAME OVER GAME OVER GAME OVER";

        for (int i = 0; i < str.length(); i++) {
            terminal.putCharacter(str.charAt(i));
        }

    }

    private static boolean checkCrash() {
        int[][] playerXY = new int[player.getSizeWidth()][player.getSizeHeight()];
        for (GameObject object : gameObjects) {
            int[][] objectXY = new int[object.getSizeWidth()][object.getSizeHeight()];


            for (int i = 0; i < player.getSizeHeight(); i++) {


            }



        }
        return false;
    }

    private static boolean checkPlayer(int rows) {
        if (player.getY < 0 || player.getY > rows) {
            return false;
        }
        return true;
    }

    private static boolean checkGameObject(GameObject gameObject) {
        if (gameObject.x < 0) {
            return false;
        }
        return true;
    }

    private static void putPlayerBack(int rows) {
        if (player.getY() < 0) {
            player.setY(0);
        } else {
            player.setY(rows);
        }
    }

    private static void callMovementManeuver(KeyStroke keyStroke) {
        switch (keyStroke.getKeyType()) {
            case ArrowDown -> {
                player.setY(player.getY + 2); //se till så att det finns en set-metod i player (som plusar på y med värdet som skickas in)
            }
            case ArrowUp -> {
                player.setY(player.getY -2);
            }
            default -> { //kanske inte behövs?
                return;
            }
        }
    }
    private static void moveAsteroids(Terminal terminal2) throws Exception {
        for (GameObject asteroid : gameObjects) {
            terminal2.setCursorPosition(asteroid.oldX, asteroid.oldY);
            terminal2.putCharacter(' ');
            terminal2.setCursorPosition(asteroid.x, asteroid.y);
            terminal2.putCharacter(asteroid.shape);
        }
    }
    private static void movePlayer(Terminal terminal2) throws Exception {
        for (GameObject player : gameObjects) {
            terminal2.setCursorPosition(player.oldX, player.oldY);
            terminal2.putCharacter(' ');
            terminal2.setCursorPosition(player.x, player.y);
            terminal2.putCharacter(player.shape);
        }
    }

    private static void removeGameObject() {
        for (GameObject gameObject : gameObjects) {
            if (!checkGameObject(gameObject)) {
                gameObjects.remove(gameObject);
            }
        }
    }

    private static void createNewGameObjects(int columns, int rows) {
        int randomPosition = random.nextInt(rows);
        while (checkGameObjectsPositions(randomPosition)) {
            randomPosition = random.nextInt(rows);
        }
        gameObjects.add(new Astroid(columns, randomPosition, columns, randomPosition, 5, 5, '\u25CF'));
    }

    private static boolean checkGameObjectsPositions(int randomPosition) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.y == randomPosition) {
                return true;
            }
        }
        return false;
    }
    private static void newPosition(Terminal terminal) throws Exception{
       //Loopar igenom listan av astroider och sätter ett nytt x värde
        for (GameObject astroid : gameObjects){
            astroid.x--;
        }
        //Loopar igenom och skriver ut samt tar bort den gamla positionen
        for (GameObject astroid : gameObjects){
            terminal.setCursorPosition(astroid.oldX,astroid.oldY);
            terminal.putCharacter(' ');
            terminal.setCursorPosition(astroid.x,astroid.y);
            terminal.putCharacter('*');
        }
    }

    private static void spaceShipCreator(){

        char[][] spaceship =   {{' ',' ','\\','\\', ' '},
                               {' ','_','_','_', ' '},
                               {'#','[','=','=', '>'},
                               {' ','_','_','_', '_'},
                               {' ',' ','/','/', ' '}};

    }
}
