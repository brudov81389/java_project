import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;


public class Player{
    private final String username;
    private final boolean is_bot;


    public Player(boolean isBot, String username) {
        this.is_bot = isBot;
        this.username = username;
        StatOne.getInstance().readStat(this.username);
    }

    public String getUsername() {
        return username;
    }

    public boolean isBot() {
        return is_bot;
    }


    public int guessNumber(int min, int max) {
        if (is_bot) {
            return new Random().nextInt(min+1, max);
        } else {
            System.out.print(this.username + ", enter your guess: ");
            return new Scanner(System.in).nextInt();
        }
    }

    public int inventNumber(int min, int max) {
        if (is_bot) {
            return new Random().nextInt(min+1, max);
        } else {
            System.out.print(this.username + ", invent a number (keep it secret): ");
            return new Scanner(System.in).nextInt();
        }
    }
}
