import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    static ArrayList<GameObject> gameObjects = new ArrayList<>();

    static Player player;
    static Random random = new Random();

    public static void main(String[] args) throws Exception {

        TerminalSize ts = new TerminalSize(90, 36);
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminalFactory.setInitialTerminalSize(ts);
        Terminal terminal = terminalFactory.createTerminal();

        terminal.setCursorVisible(false); //Gömmer pekaren
        TerminalSize terminalSize = terminal.getTerminalSize(); //Hämtar storleken på terminalen
        int columns = terminalSize.getColumns(); //sätter en variabel till maxvärdet av bredden (x-värdet)
        int rows = terminalSize.getRows(); //sätter en variabel till maxvärdet av höjden (y-värdet)

        GameField gameField = new GameField(columns, rows);

        int startY = rows / 2; //ska vi byta till int så blir det bättre koppling till columns & rows?
        int startX = columns / 10;
        int playerWidth = 6;
        int playerHeight = 3;
        //String shape = "Hej"; //FIXA!!!
        player = new Player(startX, startY, startX, startY, playerWidth, playerHeight);

        //boolean continueReadingInput = true;
        inputOutput(terminal, rows, columns);


    }

    private static void inputOutput(Terminal terminal, int rows, int columns) throws Exception {
        LocalTime lastTimeMode = LocalTime.now();
        boolean continueReadingInput = true;
        while (continueReadingInput) {

            KeyStroke keyStroke = null;

            int timeStep = 0;
            int timeStepForAsteroids = 0;

            do {
                Thread.sleep(8); //might throw InterruptedException
                keyStroke = terminal.pollInput();
                if (timeStep > 10) {
                    timeStep = 0;
                    newPosition(terminal); //metod för side scroller
                    moveObjects(terminal); //metod för objekthanteraren
                    movePlayer(terminal);
                    removeGameObject(terminal);

                    if (timeStepForAsteroids > 50) {
                        timeStepForAsteroids = 2;
                        createNewGameObjects(rows, columns);
                        System.out.println(gameObjects.size());
                    }

                    if (checkCrash()) { //metod för kollisionskontroll
                        gameOver(terminal, rows, columns); //metod för game over
                        terminal.flush();
                        Thread.sleep(1000 * 5);
                        terminal.close();
                        continueReadingInput = false;
                        break;
                    }
                }

                timeStepForAsteroids++;
                timeStep++;
                terminal.flush();
            } while (keyStroke == null);
            if (keyStroke == null) {
                break;
            }

            KeyType type = keyStroke.getKeyType();
            Character c = keyStroke.getCharacter();
            System.out.println(type + ", " + c);

            if (c == Character.valueOf('q') || c == Character.valueOf('Q')) {
                continueReadingInput = false;
                terminal.close();
                System.out.println("quit");
            }

            /*if (!checkPlayer(rows)) { //Kollar så att spelaren fortfarande är inom terminalfönster
                putPlayerBack(rows);
            }*/
            callMovementManeuver(keyStroke, rows); //Kolla sen när spelet körs om vi måste öka eller minska hastighet

            if (LocalTime.now().isAfter(lastTimeMode.plusNanos(800000000))) {
                createNewGameObjects(rows, columns);
                newPosition(terminal);
                lastTimeMode = LocalTime.now();
            }

            if (checkCrash()) {
                gameOver(terminal, rows, columns);
                continueReadingInput = false;

            } else {

                moveObjects(terminal);
                movePlayer(terminal);

                removeGameObject(terminal);
            }
            terminal.flush();
        }
    }

    private static void gameOver(Terminal terminal, int rows, int columns) throws Exception {
        terminal.clearScreen();
        terminal.setCursorPosition(columns / 2, rows / 2);
        String str = "GAME OVER GAME OVER GAME OVER";

        for (int i = 0; i < str.length(); i++) {
            terminal.putCharacter(str.charAt(i));
        }

    }

    private static boolean checkCrash() {

        int playerX, playerY;
        int objectX, objectY;
        for (GameObject object : gameObjects) {

            for (int pX = 0; pX < player.getSizeWidth(); pX++) {

                for (int pY = 0; pY < player.getSizeHeight(); pY++) {

                    for (int oX = 0; oX < object.getSizeWidth(); oX++) {

                        for (int oY = 0; oY < object.getSizeHeight(); oY++) {

                            playerX = player.getX() + pX;
                            playerY = player.getY() + pY;
                            objectX = object.getX() + oX;
                            objectY = object.getY() + oY;

                            if (playerX == objectX && playerY == objectY) {
                                return true;
                            }


                        }

                    }

                }


            }


        }
        return false;
    }


    private static boolean checkPlayer(int rows) {
        if (player.y < 0 || player.y > rows) {
            return false;
        }
        return true;
    }

    private static boolean checkGameObject(GameObject gameObject) {
        if (gameObject.x < 1) {
            return false;
        }
        return true;
    }

    private static boolean putPlayerBackUP(int rows, int stepSize) {
        if ((player.y + stepSize) > rows - player.sizeHeight) {
            return true;
        }
        return false;
    }

    private static boolean putPlayerBackDOWN(int rows, int stepSize) {
        if ((player.getY() - stepSize) < 0) {
            return true;
        }
        return false;
    }

    private static void callMovementManeuver(KeyStroke keyStroke, int rows) {
        int stepSize = 1;
        switch (keyStroke.getKeyType()) {
            case ArrowDown -> {
                if (!putPlayerBackUP(rows, stepSize)) {
                    player.oldY = player.y;
                    player.setY(player.y + stepSize); //se till så att det finns en set-metod i player (som plusar på y med värdet som skickas in)
                }
            }
            case ArrowUp -> {
                if (!putPlayerBackDOWN(rows, stepSize)) {
                    player.oldY = player.y;
                    player.setY(player.y - stepSize);
                }
            }
            default -> { //kanske inte behövs?
                return;
            }
        }
    }

    private static void drawScoreBoard(Terminal terminal2) throws Exception {
        String scoreboard = "Scoreboard: " + points;
        for (int i = 0; i < scoreboard.length(); i++) {
            terminal2.setCursorPosition(i + 75, 2);
            terminal2.putCharacter(scoreboard.charAt(i));
        }
    }

    private static void moveObjects(Terminal terminal2) throws Exception {
        //System.out.println("moveAsteroids()");
        for (GameObject object : gameObjects) {
            terminal2.setCursorPosition(object.oldX, object.oldY);
            terminal2.putCharacter(' ');
            terminal2.setCursorPosition(object.x, object.y);
            if (object instanceof Asteroid a) {
                terminal2.putCharacter(a.getShape());
            }
            if (object instanceof Point sp) {
                terminal2.putCharacter(sp.getShape());
            }
        }
    }

    private static void movePlayer(Terminal terminal2) throws Exception {
        erasePlayer(terminal2);
        int i1 = 0;
        for (char[] charArray : player.getShape()) {
            int i2 = 0;
            for (char c : charArray) {

                terminal2.setCursorPosition(player.x + i2, player.y + i1);
                terminal2.putCharacter(c);
                i2++;
            }
            i1++;

        }

    }

    private static void erasePlayer(Terminal terminal) throws Exception {

        int i1 = 0;
        for (char[] charArray : player.getShape()) {
            int i2 = 0;
            for (char c : charArray) {
                terminal.setCursorPosition(player.oldX + i2, player.oldY + i1);
                terminal.putCharacter(' ');

                i2++;
            }
            i1++;

        }
        terminal.flush();
    }

    private static void removeGameObject(Terminal terminal) throws IOException {
        //System.out.println("removeGameObject()");
        for (GameObject gameObject : gameObjects) {
            if (!checkGameObject(gameObject)) {
                terminal.setCursorPosition(gameObject.oldX, gameObject.oldY);
                terminal.putCharacter(' ');
                terminal.setCursorPosition(gameObject.x, gameObject.y);
                terminal.putCharacter(' ');
                gameObjects.remove(gameObject);
                return;
            }
        }
    }

    private static void createNewGameObjects(int rows, int columns, String gameObjectType) {
        int randomPosition = random.nextInt(rows);
        while (checkGameObjectsPositions(randomPosition, columns)) {
            randomPosition = random.nextInt(rows);
        }
        gameObjects.add(new Asteroid(columns, randomPosition, columns, randomPosition, 1, 1, '\u25CF'));
    }

    private static boolean checkGameObjectsPositions(int randomPosition, int columns) {
        //System.out.println("checkGameObjectsPositions");

        for (GameObject gameObject : gameObjects) {
            if (gameObject.y == randomPosition && gameObject.x == columns) {
                return true;
            }
        }


        return false;
    }

    private static void newPosition(Terminal terminal) throws Exception {
        //Loopar igenom listan av astroider och sätter ett nytt x värde
        // System.out.println("newPositions");
        for (GameObject asteroid : gameObjects) {
            asteroid.oldX = asteroid.x;
            asteroid.x -= 1;
        }
    }

    private static void spaceShipCreator() {

        char[][] spaceship = {{' ', ' ', '\\', '\\', ' '},
                {' ', '_', '_', '_', ' '},
                {'#', '[', '=', '=', '>'},
                {' ', '_', '_', '_', '_'},
                {' ', ' ', '/', '/', ' '}};

    }

}

