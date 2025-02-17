import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.abs;

public class Privilege {
    private int amount;
    private final String name;

    public Privilege(int amount, String name) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return this.name;
    }

    public int getAmount() {
        return this.amount;
    }

    public void resetPrivilege(int amount){
        this.amount = amount;
    }

    public void cast() {
        if(this.amount > 0 ){
            this.amount--;
        }
        else {
            System.out.printf("\nYou can`t use %s in this game.\n", this.name);
        }
    }

    public static String generateEquation(int number) {
        Random random = new Random();
        int a = random.nextInt(10) + 1;
        int b = random.nextInt(20) - 10;
        int c = -(a * number * number + b * number);
        String result = a + "x^2";
        if(b < 0) {result += " - " + abs(b);} else {result += " + " + b;}
        if(c < 0) {result += " - " + abs(c);} else {result += " + " + c;}

        return result + " = 0";
    }

    public static boolean leaderPrivilege(String username) {
        System.out.printf("\n%s, press X if you want to pass step.\n", username);
        String playerChoice = new Scanner(System.in).next();
        return playerChoice.equals("x") || playerChoice.equals("X");
    }

    public static boolean championPrivilege(String username) {
        System.out.printf("\n%s, press C if you want to get advice.\n", username);
        Scanner scan = new Scanner(System.in);
        String playerChoice = new Scanner(System.in).next();
        return playerChoice.equals("c") || playerChoice.equals("C");
    }

    public static boolean tournamentPrivilege(String username) {
        System.out.printf("\n%s, press V if you want to repeat a match.\n", username);
        String playerChoice = new Scanner(System.in).next();
        return playerChoice.equals("v") || playerChoice.equals("V");
    }
}
