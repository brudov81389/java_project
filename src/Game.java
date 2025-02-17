import java.util.Scanner;


public class Game {
    protected final Player host;
    private final Player gambler;
    protected int guess_number;
    protected int number_to_guess;
    protected Scanner scan;
    protected Level level;

    public Game(Player host, Player gambler, Level lvl) {
        this.host = host;
        this.gambler = gambler;
        this.level = lvl;
        this.number_to_guess = -1;
        this.scan = new Scanner(System.in);
    }

    public void play()
    {
        System.out.printf("Game started! The range is %d to %d\n",level.getBegin(), level.getEnd());
        int attempts;
        boolean isPlay;
        do{
            if (host.isBot()) {
                attempts = playerGuesses();
            }
            else {
                attempts = computerGuesses();
            }
            int b_score = StatOne.getInstance().getBScore(gambler.getUsername(), level.getNameLevel());
            if ((attempts < b_score) || (b_score == -1)){
                StatOne.getInstance().writeStat(gambler.getUsername(), level.getNameLevel(), attempts);
            }
            isPlay = repeat(null);
            if(isPlay)
                changeLevel();
        }
        while (isPlay);

    }

    private int playerGuesses() {
        int numberToGuess = host.inventNumber(level.getBegin(), level.getEnd());
        System.out.println(numberToGuess);
        int attempts = 0;

        while (true) {
            attempts++;
            int guess = gambler.guessNumber(level.getBegin(), level.getEnd());

            if (guess == numberToGuess) {
                System.out.println(gambler.getUsername() + " guessed it right in " + attempts + " attempts!");
                break;
            } else if (guess < numberToGuess) {
                System.out.println("Too low!");
            } else {
                System.out.println("Too high!");
            }
        }

        return attempts;
    }

    private int computerGuesses() {
        int min = level.getBegin();
        int max = level.getEnd();
        this.number_to_guess = host.inventNumber(level.getBegin(), level.getEnd());
        int attempts = 0;

        while (true) {
            attempts++;
            this.guess_number = gambler.guessNumber(min, max);

            if (this.guess_number == this.number_to_guess) {
                System.out.println(gambler.getUsername() + " guessed it right in " + attempts + " attempts!");
                break;
            } else if (this.guess_number < this.number_to_guess) {
                System.out.printf("%d - Too low!\n", this.guess_number);
                min = this.guess_number + 1;
            } else {
                System.out.printf("%d - Too high!\n", this.guess_number);
                max = this.guess_number - 1;
            }
        }

        return attempts;
    }


    protected Boolean repeat(Player player)
    {
        if(player == null) {
            System.out.println("\nDo you want try again ?");
            System.out.println("Press 'y' if you want to continue or another key for back to menu game and change another mode\n");
        }
        else {
            System.out.printf("\n%s, do you want try again ?", player.getUsername());
        }
        String player_choice;
        player_choice = this.scan.nextLine();
        System.out.println("Press 'y' if you want to continue or another key for back to menu game and change another mode\n");
        return player_choice.equals("y") || player_choice.equals("Y");
    }

    protected void changeLevel(){
        System.out.println("\nDo you want change difficulty ?");
        System.out.println("Press 'y' if you want, or another key for play with current difficulty.\n");
        String player_choice = this.scan.nextLine();
        if(player_choice.equals("y") || player_choice.equals("Y")){
            level = Level.chooseLevel();
        }
    }

}
