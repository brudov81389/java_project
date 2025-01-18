import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TournamentGame {
    private final Player host;
    private final List<Player> players;
    private final Level level;
    private final Scanner scanner;
    private final int format; // BO1, BO3, BO5

    public TournamentGame(Player host, List<Player> players, Level level, int format) {
        this.host = host;
        this.players = new ArrayList<>(players);
        this.level = level;
        this.format = format;
        this.scanner = new Scanner(System.in);
    }

    public void startTournament() {
        System.out.println("Tournament started!");
        System.out.printf("The range for guesses is %d to %d%n", level.getBegin(), level.getEnd());

        // Shuffle all players
        Collections.shuffle(players);

        // Ladder tournament
        List<Player> currentRound = new ArrayList<>(players);
        int roundNumber = 1;

        while (currentRound.size() > 1) {
            System.out.printf("%n--- Round %d --- %n", roundNumber);
            List<Player> nextRound = new ArrayList<>();

            for (int i = 0; i < currentRound.size(); i += 2) {
                if (i + 1 < currentRound.size()) {
                    Player player1 = currentRound.get(i);
                    Player player2 = currentRound.get(i + 1);
                    Player winner = playMatch(player1, player2);
                    nextRound.add(winner);
                } else {
                    // If amount of player is even
                    Player freePass = currentRound.get(i);
                    System.out.printf("%s advances automatically to the next round!%n", freePass.getUsername());
                    nextRound.add(freePass);
                }
            }
            currentRound = nextRound;
            roundNumber++;
        }

        // Winner emergence
        if (!currentRound.isEmpty()) {
            Player champion = currentRound.get(0);
            PlayerPrivileges.grantChampion(champion.getUsername());
            System.out.printf("%nCongratulations %s! You are the tournament champion!%n", champion.getUsername());
        }

        // Save stats
        StatOne.getInstance().save();
    }

    private Player playMatch(Player player1, Player player2) {
        System.out.printf("%s vs %s%n", player1.getUsername(), player2.getUsername());
        int winsPlayer1 = 0;
        int winsPlayer2 = 0;

        for (int i = 0; i < format; i++) {
            System.out.printf("Match %d/%d%n", i + 1, format);
            int numberToGuess = host.inventNumber(level.getBegin(), level.getEnd());
            System.out.println("Host has invented a number!"); // Debug
            System.out.println(numberToGuess);

            Player winner = null;
            int attemptsPlayer1 = 0;
            int attemptsPlayer2 = 0;

            while (winner == null) {
                attemptsPlayer1++;
                int guess1 = player1.guessNumber(level.getBegin(), level.getEnd());
                System.out.printf("%s guessed: %d%n", player1.getUsername(), guess1);

                if (guess1 == numberToGuess) {
                    winner = player1;
                    System.out.printf("%s guessed correctly in %d attempts!%n", player1.getUsername(), attemptsPlayer1);
                    winsPlayer1++;
                    break;
                } else if (guess1 < numberToGuess) {
                    System.out.println("Too low!");
                } else {
                    System.out.println("Too high!");
                }

                attemptsPlayer2++;
                int guess2 = player2.guessNumber(level.getBegin(), level.getEnd());
                System.out.printf("%s guessed: %d%n", player2.getUsername(), guess2);

                if (guess2 == numberToGuess) {
                    winner = player2;
                    System.out.printf("%s guessed correctly in %d attempts!%n", player2.getUsername(), attemptsPlayer2);
                    winsPlayer2++;
                    break;
                } else if (guess2 < numberToGuess) {
                    System.out.println("Too low!");
                } else {
                    System.out.println("Too high!");
                }
            }

            // Check if someone win the match
            if (winsPlayer1 > format / 2) {
                System.out.printf("%s wins the match!%n", player1.getUsername());
                StatOne.getInstance().incrementWins(player1.getUsername());
                StatOne.getInstance().incrementLose(player2.getUsername());
                return player1;
            }

            if (winsPlayer2 > format / 2) {
                System.out.printf("%s wins the match!%n", player2.getUsername());
                StatOne.getInstance().incrementWins(player2.getUsername());
                StatOne.getInstance().incrementLose(player1.getUsername());
                return player2;
            }
        }

        // In draw situation, in theory impossible if match play in (BO1, BO3, BO5 etc.)
        System.out.println("Match ended in a draw (this should not happen)!");
        return null;
    }

    private boolean handleRematch(Player loser) {
        if (PlayerPrivileges.isChampion(loser.getUsername())){
            System.out.println("Do you want to use your rematch? (y/n)");
            String choice = scanner.nextLine();
            return choice.equals("y") || choice.equals("Y");
        }
        return false;
    }
}
