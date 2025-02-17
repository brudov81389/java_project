import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TournamentGame {
    private final Player host;
    private final List<Player> gamblers;
    private final Level level;
    private final int format; // BO1, BO3, BO5

    public TournamentGame(Player host, List<Player> players, Level level, int format) {
        this.host = host;
        this.gamblers = new ArrayList<>(players);
        this.level = level;
        this.format = format;
    }

    public void startTournament() {
        System.out.println("Tournament started!");
        System.out.printf("The range for guesses is %d to %d%n", level.getBegin(), level.getEnd());

        // Shuffle all players
        Collections.shuffle(gamblers);

        // Ladder tournament
        List<Player> currentRound = new ArrayList<>(gamblers);
        int roundNumber = 1;

        while (currentRound.size() > 1) {
            System.out.printf("%n--- Round %d --- %n", roundNumber);
            List<Player> nextRound = new ArrayList<>();

            for (int i = 0; i < currentRound.size(); i += 2) {
                if (i + 1 < currentRound.size()) {
                    Player winner = playMatch(currentRound.get(i), currentRound.get(i + 1));
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
            Player champion = currentRound.getFirst();
            grantNewChampion(champion);
            System.out.printf("\nCongratulations %s! You are the tournament champion!\n", champion.getUsername());
        }
    }

    private Player playMatch(Player player1, Player player2) {
        System.out.printf("%s vs %s%n", player1.getUsername(), player2.getUsername());
        int winsPlayer1 = 0;
        int winsPlayer2 = 0;

        List<Player> tempList = new ArrayList<>();
        tempList.add(player1);
        tempList.add(player2);
        MultiplayerGame match = new MultiplayerGame(host, tempList, level);

        for (int i = 0; i < format; i++) {
            System.out.printf("Match %d/%d%n", i + 1, format);
            Player winner = match.playMatch(true);

            if(winner == player1){
                if(player2.isChampion() &&  player2.checkPrivilege("repeat_match") && Privilege.tournamentPrivilege(player2.getUsername())) {
                    i--;
                    player2.castPrivilege("repeat_match");
                }
                else {
                    winsPlayer1++;
                }
            }
            else{
                if(player1.isChampion() && player1.checkPrivilege("repeat_match") && Privilege.tournamentPrivilege(player2.getUsername())){
                    i--;
                    player1.castPrivilege("repeat_match");
                }
                else {
                    winsPlayer2++;
                }

            }
            // Check if someone win the round
            if (winsPlayer1 > format / 2) {
                System.out.printf("%s wins the match!%n", player1.getUsername());
                return player1;
            }

            if (winsPlayer2 > format / 2) {
                System.out.printf("%s wins the match!%n", player2.getUsername());
                return player2;
            }
        }
        System.out.println("Match ended in a draw (this should not happen)!");
        return null;
    }

    private void grantNewChampion(Player newChampion){
        List<Player> exChampionBoard = new ArrayList<>();
        for(Player gambler : gamblers) {
            if(gambler.isChampion())
                exChampionBoard.add(gambler);
        }

        if(!exChampionBoard.contains(newChampion))
            PlayerPrivileges.grantChampion(newChampion.getUsername());

        for(Player gambler : exChampionBoard) {
            PlayerPrivileges.revokeChampion(gambler.getUsername());
        }
    }
}
