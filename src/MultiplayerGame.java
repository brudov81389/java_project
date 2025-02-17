import java.util.*;

public class MultiplayerGame extends Game {
    private final List<Player> gamblers;

    public MultiplayerGame(Player host, List<Player> gamblers, Level lvl) {
        super(host, null, lvl); // To base class put null for gambler, cause here we have list of gamblers
        this.gamblers = gamblers;
    }

    private void prepareMatch(){
        System.out.printf("Match started! The range is %d to %d", level.getBegin(), level.getEnd());
        this.number_to_guess = host.inventNumber(level.getBegin(), level.getEnd());
        System.out.println("Host has invented a number!"); // Debug
        System.out.println(this.number_to_guess);
    }

    private void writeResults(Player winner){
        StatOne statManager = StatOne.getInstance();
        for (Player gambler : gamblers) {
            if (gambler.equals(winner)) {
                statManager.incrementWins(gambler.getUsername());
            } else {
                statManager.incrementLose(gambler.getUsername());
            }
        }
        PlayerPrivileges.grantLeader(winner.getUsername());
    }

    public Player playMatch(boolean isTournament){
        prepareMatch();

        List<Player> randomOrder = new ArrayList<>(gamblers);
        Collections.shuffle(randomOrder); // Random order for gamblers
        boolean guessed = false;
        Player winner = null;
        int min = level.getBegin();
        int max = level.getEnd();

        while (!guessed) {
            for (Player gambler : randomOrder) {
                if (gambler.isLeader() && gambler.checkPrivilege("pass_step")  &&
                    !isTournament && Privilege.leaderPrivilege(gambler.getUsername())) {

                    gambler.castPrivilege("pass_step");
                    continue;
                }
                if (gambler.isChampion() && gambler.checkPrivilege("show_advice")  &&
                    !isTournament && Privilege.championPrivilege(gambler.getUsername())) {

                    gambler.castPrivilege("show_advice");
                    System.out.printf("\nSolve this quadratic equation %s and find out the number.", Privilege.generateEquation(this.number_to_guess));
                }

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
        return winner;
    }

    @Override
    public void play() {
        boolean isPlay = true;
        while (isPlay) {

            Player winner = playMatch(false);
            writeResults(winner);
            grantNewLeader(winner);

            isPlay = repeat(winner);
            if(isPlay) {
                changeLevel();
                reloadLeaderBoard();
            }
        }
        System.out.println("Game over. Statistics saved.");
    }

    private void grantNewLeader(Player newLeader){
        List<Player> exLeaderBord = new ArrayList<>();
        for(Player gambler : gamblers) {
            if(gambler.isLeader())
                exLeaderBord.add(gambler);
        }

        if(!exLeaderBord.contains(newLeader))
            PlayerPrivileges.grantLeader(newLeader.getUsername());

        for(Player gambler : exLeaderBord) {
            PlayerPrivileges.revokeLeader(gambler.getUsername());
        }
    }

    private void reloadLeaderBoard(){
        for(Player gambler : gamblers) {
            gambler.loadPrivilege();
        }
    }
}
