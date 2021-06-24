import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import static com.googlecode.lanterna.input.KeyType.ArrowDown;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.googlecode.lanterna.input.KeyType.ArrowDown;

public class Main {

    static ArrayList<GameObject> gameObjects = new ArrayList<>();
    static int points;
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

        int startY = rows / 2; //ska vi byta till int så blir det bättre koppling till columns & rows?
        int startX = columns / 10;
        int playerWidth = 6;
        int playerHeight = 3;

        player = new Player(startX, startY, startX, startY, playerWidth, playerHeight, 3);

        inputOutput(terminal, rows, columns);


    }

    private static void inputOutput(Terminal terminal, int rows, int columns) throws Exception {
        LocalTime lastTimeMode = LocalTime.now();
        LocalTime lastTimePoint = LocalTime.now();
        LocalTime pointsTime = LocalTime.now();
        boolean continueReadingInput = true;
        while (continueReadingInput) {

            KeyStroke keyStroke = null;

            int timeStep = 0;
            int timeStepForAsteroids = 0;
            int timeStepForPoints = 0;
            int pointsInThousands = 0;
            int thousand = 1;

            do {
                Thread.sleep(8); //might throw InterruptedException
                keyStroke = terminal.pollInput();
                if (timeStep > 10) {
                    timeStep = 0;
                    newPosition(terminal); //metod för side scroller
                    moveObjects(terminal); //metod för objekthanteraren
                    movePlayer(terminal);
                    removeGameObject(terminal);
                    pointsInThousands ++;
                    if (points >= (thousand*1000)  && points <= (thousand*1000)+50 && pointsInThousands >= 10){
                        player.setHealth(player.getHealth() +1);
                        thousand++;
                    }
                    pointsTime = pointsCheck(pointsTime); // Passiv inkomst

                    if (timeStepForAsteroids > 5) {
                        timeStepForAsteroids = 0;
                        createNewGameObjects(rows, columns,GameObjectType.ASTEROID);
                    }

                    if (timeStepForPoints > 20) {
                        timeStepForPoints= 0;
                        createNewGameObjects(rows, columns,GameObjectType.POINT);
                    }

                    if (checkCrash()) { //metod för kollisionskontroll
                        gameOver(terminal, rows, columns); //metod för game over
                        terminal.flush();
                        Thread.sleep(1000*5);
                        terminal.close();
                        continueReadingInput = false;
                        break;
                    }
                    timeStepForAsteroids++;
                    timeStepForPoints++;

                }
                timeStep++;
                drawScoreBoard(terminal);
                terminal.flush();
            } while (keyStroke == null);
            if(keyStroke == null){
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

            callMovementManeuver(keyStroke, rows); //Kolla sen när spelet körs om vi måste öka eller minska hastighet

            if (LocalTime.now().isAfter(lastTimeMode.plusNanos(800000000))) {
                createNewGameObjects(rows, columns, GameObjectType.ASTEROID);
                newPosition(terminal);
                lastTimeMode = LocalTime.now();
            }
            if(LocalTime.now().isAfter(lastTimePoint.plusSeconds(4))){
                createNewGameObjects(rows, columns, GameObjectType.POINT);
                lastTimePoint = LocalTime.now();
            }

            if (checkCrash()) {
                gameOver(terminal, rows, columns); //metod för game over
                terminal.flush();
                Thread.sleep(1000*5);
                terminal.close();
                break;

            } else {

                moveObjects(terminal);
                movePlayer(terminal);
                pointsTime = pointsCheck(pointsTime);
                if (points >= (thousand*1000)  && points <= (thousand*1000)+50 && pointsInThousands >= 10){
                    player.setHealth(player.getHealth() +1);
                    thousand++;
                }
                removeGameObject(terminal);
            }
            drawScoreBoard(terminal);
            terminal.flush();
        }
    }
    private static void gameOver(Terminal terminal,int rows,int columns) throws Exception {
        String gameOver = "GAME OVER GAME OVER GAME OVER GAME OVER GAME OVER GAME OVER";
        terminal.clearScreen();
        terminal.setCursorPosition(columns/2 - (gameOver.length()/2),rows/2);

        for (int i = 0; i < gameOver.length(); i++) {
            terminal.putCharacter(gameOver.charAt(i));
        }

        String finalPoints = "POINTS: " + points;
        terminal.setCursorPosition(columns/2 - (finalPoints.length()/2), rows/3);
        for (char point : finalPoints.toCharArray()) {
            terminal.putCharacter(point);
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
                                if (object instanceof Point) {
                                    points += 50; // refactor?
                                    gameObjects.remove(object);
                                    return false;
                                } else if (object instanceof Asteroid) {
                                    return true;
                                }
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
        if  ((player.y+stepSize) > rows - player.sizeHeight){
            return true;
        }
        return false;
    }

    private static boolean putPlayerBackDOWN(int rows, int stepSize) {
        if ((player.getY()-stepSize) < 0) {
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
            terminal2.setCursorPosition(i + 60, 2);
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

    private static void createNewGameObjects(int rows,
                                             int columns,
                                             GameObjectType gameObjectType) {
        int randomPosition = random.nextInt(rows);
        while (checkGameObjectsPositions(randomPosition,columns)) {
            randomPosition = random.nextInt(rows);
        }
        if (gameObjectType == GameObjectType.ASTEROID) {
            gameObjects.add(
                    new Asteroid(columns, randomPosition, columns, randomPosition, 1, 1,
                                 '\u25CF'));
        } else if (gameObjectType == GameObjectType.POINT) {
            gameObjects.add(
                    new Point(columns, randomPosition, columns, randomPosition, 1, 1,
                              '\u20BF'));
        }
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

    private static void newPosition(Terminal terminal) throws Exception{
       //Loopar igenom listan av astroider och sätter ett nytt x värde
       // System.out.println("newPositions");
        for (GameObject asteroid : gameObjects) {
            asteroid.oldX = asteroid.x;
            asteroid.x-=1;
        }
    }


    private static LocalTime pointsCheck (LocalTime pointsTime) {
        if (LocalTime.now().isAfter(pointsTime.plusSeconds(2))) {
            points++;
            return LocalTime.now();
        } return pointsTime;
    }

}

