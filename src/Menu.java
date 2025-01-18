import java.util.*;

class Menu {
    private static final Scanner scanner = new Scanner(System.in);
    private final Player mainPlayer;

    public Menu() {
        System.out.print("Write your username: ");
        String username = scanner.nextLine();
        this.mainPlayer = new Player(false, username);
        System.out.printf("\n\tWelcome to the Number Guessing Game, %s!\n", username);
        Level.initLevels();
    }

    public Boolean show() {

        System.out.println("""

               Choose mode:
                 1. Single Player.
                 2. Cross Game.
                 3. Multiplayer Game.
                 4. Tournament Game.
                 5. Add New Level.
                 ----------------------
                 0. Exit.\s
                 \
                """);
        Scanner scan = new Scanner(System.in);
        String choose = scan.nextLine();
        return switch (choose) {
            case "1" -> {
                singleGame().play();
                yield true;
            }
            case "2" -> {
                crossGame().play();
                yield true;
            }
            case "3" -> {
                multiPlayerGame().play();
                yield true;
            }
            case "4" -> {
                tournament().startTournament();
                yield true;
            }
            case "5" -> {
                addNewLevel();
                yield true;
            }
            default -> false;
        };
    }

    public Game singleGame() {
        Boolean isBotHost = chooseHost();

        Player host;
        Player gambler;

        if (isBotHost) {
            System.out.println("Computer invent the number, your task to guess it.\n");
            host = new Player(true, "BOT");
            gambler = this.mainPlayer;
        } else {
            System.out.println("You are host!\n Invent the number and computer should guess it :)\n");
            host = this.mainPlayer;
            gambler = new Player(true, "BOT");
        }

        return new Game(host, gambler, Level.chooseLevel());
    }

    public Game crossGame(){
        Player host = new Player(true, "HOST");
        List<Player> gamblers = new ArrayList<Player>();

        gamblers.add(this.mainPlayer);

        Player gambler = createBot();
        gamblers.add(gambler);

        return new MultiplayerGame(host, gamblers, Level.chooseLevel());
    }

    public Game multiPlayerGame(){
        // Create bot, which invent number for gamblers
        Player host = new Player(true, "HOST");
        // Get amount of gambler
        System.out.print("Choose amount of gamblers: ");
        Scanner scan = new Scanner(System.in);
        int amountPlayers = Integer.parseInt(scan.nextLine());
        List<Player> gamblers = new ArrayList<Player>();
        gamblers.add(this.mainPlayer);
        System.out.printf("\nPlayer 1: %s\n", this.mainPlayer.getUsername());

        for(int i = 1; i < amountPlayers; i++){
            System.out.printf("\nPlayer %d, username : \n", i + 1);
            String username = scan.nextLine();
            Player gambler = new Player(false, username);
            gamblers.add(gambler);
        }

        return new MultiplayerGame(host, gamblers, Level.chooseLevel());
    }

    public TournamentGame tournament(){
        // Create bot as host for all games in tournament
        Player host = new Player(true, "HOST");
        // Get amount of players
        System.out.println("Choose amount of gamblers: ");
        Scanner scan = new Scanner(System.in);
        int amountPlayers = scan.nextInt();

        System.out.print("\nChoose amount of matches for each round it should odd number: ");
        int format = scan.nextInt();

        if ((format % 2 == 0) && (format > 0))
            format -= 1;

        List<Player> gamblers = new ArrayList<Player>();
        gamblers.add(this.mainPlayer);
        System.out.printf("\nPlayer 1: %s\n", this.mainPlayer.getUsername());
        scan = new Scanner(System.in);
        for(int i = 1; i < amountPlayers; i++){
            System.out.printf("\nPlayer %d, username : \n", i + 1);
            String username = scan.next();
            //String username = scanner.nextLine();
            Player gambler = new Player(false, username);
            gamblers.add(gambler);
        }

        return new TournamentGame(host, gamblers,Level.chooseLevel(),format);

    }

    public void addNewLevel(){
        System.out.println("Level name: ");
        String name = scanner.nextLine().toUpperCase().trim();
        System.out.println("Lower limit: ");
        int begin = Integer.parseInt(scanner.nextLine());
        System.out.println("Upper limit: ");
        int end = Integer.parseInt(scanner.nextLine());
        Level.addLevel(begin, end, name);
    }

    private Boolean chooseHost() {
        System.out.println("\n Do you want to invent number ( Y - yes, N - no) ?\n If you press another key Bot`ll invent the number.\n");
        String playerChoice = scanner.nextLine();
        return playerChoice.equals("y") || playerChoice.equals("Y");
    }

    private Player createBot(){

        System.out.print("Write BOT name: ");
        String botName = scanner.nextLine();
        return new Player(true, botName);
    }
}