import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.time.LocalTime;

public class GameField {

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
