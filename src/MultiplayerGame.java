import java.util.*;

public class MultiplayerGame extends Game {
    private final List<Player> gamblers;

    public MultiplayerGame(Player host, List<Player> gamblers, Level lvl) {
        super(host, null, lvl); // To base class put null for gambler, cause here we have list of gabmlers
        this.gamblers = gamblers;
    }

    @Override
    public void play() {
        System.out.printf("Multiplayer Game started! The range is %d to %d", level.getBegin(), level.getEnd());
        this.number_to_guess = host.inventNumber(level.getBegin(), level.getEnd());
        System.out.println("Host has invented a number!"); // Debug
        System.out.println(this.number_to_guess);

        List<Player> randomOrder = new ArrayList<>(gamblers);
        Collections.shuffle(randomOrder); // Random order for gamblers

        boolean guessed = false;
        Player winner = null;
        int min = level.getBegin();
        int max = level.getEnd();

        while (!guessed) {
            for (Player gambler : randomOrder) {
                int guess = gambler.guessNumber(min, max);

                System.out.printf("%s guessed: %d%n", gambler.getUsername(), guess);

                if (guess == this.number_to_guess) {
                    guessed = true;
                    winner = gambler;
                    System.out.printf("%s you WIN! Secret number was %d%n", gambler.getUsername(), this.number_to_guess);
                    break;
                } else if (guess < this.number_to_guess) {
                    System.out.printf("%s: Too low!%n", gambler.getUsername());
                    min = guess;
                } else {
                    System.out.printf("%s: Too high!%n", gambler.getUsername());
                    max = guess;
                }
            }
        }

        if (winner != null) {
            StatOne statManager = StatOne.getInstance();
            for (Player gambler : gamblers) {
                if (gambler.equals(winner)) {
                    statManager.incrementWins(gambler.getUsername());
                } else {
                    statManager.incrementLose(gambler.getUsername());
                }
            }
        }
        PlayerPrivileges.grantLeader(winner.getUsername());
        StatOne.getInstance().save(); // Save stats to file
        System.out.println("Game over. Statistics saved.");
    }
}
