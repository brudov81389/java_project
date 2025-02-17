import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;


public class Player{
    protected final String username;
    private final boolean is_bot;
    protected HashMap<String, Privilege> privilegeMap;
    private boolean isLeader = false;
    private boolean isChampion = false;


    public Player(boolean isBot, String username) {
        this.is_bot = isBot;
        this.username = username;
        StatOne.getInstance().readStat(this.username);
        loadPrivilege();
    }

    public String getUsername() {
        return username;
    }

    public boolean isBot() {
        return is_bot;
    }


    public int guessNumber(int min, int max) {
        if (is_bot) {
            return new Random().nextInt(min, max);
        } else {
            System.out.printf("%s, enter your guess (%d,%d): ",this.username, min, max);
            int guess = new Scanner(System.in).nextInt();
            if(guess > max) {
                return max;
            }
            else if(guess < min) {
                return min;
            }
            return guess;
        }
    }

    public int inventNumber(int min, int max) {
        if (is_bot) {
            return new Random().nextInt(min+1, max);
        } else {
            System.out.print(this.username + ", invent a number (keep it secret): ");
            int invent = new Scanner(System.in).nextInt();
            if(invent > max) {
                return max;
            }
            else if(invent < min) {
                return min;
            }
            return invent;
        }
    }

    public void loadPrivilege() {
        privilegeMap = new HashMap<>();
        if(PlayerPrivileges.isLeader(this.username)) {
            privilegeMap.put("pass_step", new Privilege(1, "pass_step")); // Leader can pass one step and has a less score than another gamblers can repeat one match in tournament
            isLeader = true;
        }
        if(PlayerPrivileges.isChampion(this.username)){
            isChampion = true;
            privilegeMap.put("repeat_match", new Privilege(1, "repeat_match")); // Champion can repeat one match in tournament
            privilegeMap.put("show_advice", new Privilege(1, "show_advice")); // Show quadratic equation solve it you`ll have a guessing number
        }
    }

    public void castPrivilege(String privileges){
        privilegeMap.get(privileges).cast();
    }

    public boolean checkPrivilege(String privilege){
        return privilegeMap.get(privilege).getAmount() > 0;
    }

    public boolean isChampion() {
        return isChampion;
    }

    public boolean isLeader() {
        return isLeader;
    }

}
