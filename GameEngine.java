import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class GameEngine {
    private ArrayList<Room> map;
    private Queue<String> hintQueue;
    private Player player;
    private int turnCount;
    private boolean gameRunning;
    private Room startRoom;

    public GameEngine() {
        this.map = new ArrayList<>();
        this.hintQueue = new LinkedList<>();
        this.turnCount = 0;
        this.gameRunning = true;
        setupGame();
    }

    private void setupGame() {
        // Create rooms
        Room lobby = new Room("Lobby", false);
        Room library = new Room("Library", false);
        Room lab = new Room("Laboratory", false, "LabKey"); // Requires LabKey
        Room storage = new Room("Storage", false);
        Room exitRoom = new Room("Exit", true);
        storage.setRequiredLight("Flashlight");

        // Connect rooms (bidirectional or as desired)
        lobby.addConnectedRoom(library);
        lobby.addConnectedRoom(storage);
        library.addConnectedRoom(lobby);
        library.addConnectedRoom(lab);
        lab.addConnectedRoom(library);
        lab.addConnectedRoom(exitRoom);
        storage.addConnectedRoom(lobby);

        // Add items
        lobby.addContent(new Item("Flashlight", 10, "TOOL", "A sturdy LED flashlight."));
        storage.addContent(new Item("LabKey", 50, "KEY", "The page is dog-eared at a riddle section. It reads: 'The most common keys are Q, W, E, R, T, and Y.'"));
        library.addContent(new Item("OldBook", 5, "CLUE", "A heavy brass key labeled 'LAB'."));
        lab.addContent(new Item("ScrapPaper",1,"CLUE", "To Do: Change the safe password. '1234' is too easy to guess!"));

        // Add puzzles
        library.addContent(new RiddlePuzzle(
                "SphinxRiddle",
                2,
                "Hint: Check the storage for keys",
                "What has keys but no locks, space but no room, and you can enter but can't go inside?",
                "keyboard"
        ));
        lab.addContent(new CodePuzzle(
                "SafeLock",
                3,
                "ExitUnlocked",
                "1234",
                5
        ));

        // Add rooms to map
        map.add(lobby);
        map.add(library);
        map.add(lab);
        map.add(storage);
        map.add(exitRoom);

        // Set starting room and create player
        this.startRoom = lobby;
        this.player = new Player(startRoom);

        // Add some hints to the queue
        hintQueue.add("Hint: Explore all rooms carefully!");
        hintQueue.add("Hint: Some doors need keys to unlock.");
        hintQueue.add("Hint: Solving puzzles may reveal useful information.");
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("========================================");
        System.out.println("  WELCOME TO THE ESCAPE ROOM!");
        System.out.println("========================================");
        System.out.println("You wake up in a locked facility.");
        System.out.println("Find the exit to escape!\n");
        System.out.println("Type 'help' for available commands.\n");

        player.getCurrentRoom().inspect();

        while (gameRunning) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            try {
                processCommand(input);
                turnCount++;
                if (turnCount >= 35) { // Example limit
                    System.out.println("Time has run out! You are trapped forever.");
                    gameRunning = false;
                    return; // Stop the method
                }
                // Show hint every 3 turns
                if (turnCount % 3 == 0 && !hintQueue.isEmpty()) {
                    System.out.println("\n*** " + hintQueue.poll() + " ***");
                }

                // Check win condition
                if (winConditionCheck()) {
                    System.out.println("\n========================================");
                    System.out.println("  CONGRATULATIONS! YOU ESCAPED!");
                    System.out.println("  Turns taken: " + turnCount);
                    System.out.println("========================================");
                    gameRunning = false;
                }
            } catch (InvalidCommandException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (LockedRoomException e) {
                System.out.println("LOCKED: " + e.getMessage());
            } catch (InvalidPuzzleAnswerException e) {
                System.out.println("Puzzle Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Thanks for playing!");
    }
    private boolean playerHasLight() {
        String requiredLight = player.getCurrentRoom().getRequiredLight();
        if (requiredLight == null) {
            return true; // Room is not dark
        }
        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(requiredLight)) {
                return true;
            }
        }
        return false;
    }

    public void processCommand(String cmd) throws InvalidCommandException, LockedRoomException, InvalidPuzzleAnswerException {
        String[] parts = cmd.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "help":
                printHelp();
                break;
            case "look":
                player.getCurrentRoom().inspect();
                break;
            case "move":
                handleMove(argument);
                break;
            case "back":
                player.goBack();
                break;
            case "pickup":
                handlePickup(argument);
                break;
            case "inventory":
                player.showInventory();
                break;
            case "solve":
                handleSolve(argument);
                break;
            case "inspect":
                handleInspect(argument);
                break;
            case "map":
                printMap();
                break;
            case "status":
                printStatus();
                break;
            case "quit":
            case "exit":
                gameRunning = false;
                break;
            default:
                throw new InvalidCommandException("Unknown command: " + command + ". Type 'help' for commands.");
        }
    }
    private void handleLook() {
        Room currentRoom = player.getCurrentRoom();
        if (!playerHasLight()) {
            System.out.println("=== " + currentRoom.getName() + " ===");
            System.out.println("It is pitch black! You can't see anything here.");
            System.out.println("You need: " + currentRoom.getRequiredLight());
        } else {
            currentRoom.inspect();
        }
    }

    private void handleMove(String roomName) throws InvalidCommandException, LockedRoomException {
        if (roomName.isEmpty()) {
            throw new InvalidCommandException("Usage: move <roomName>");
        }
        if (!playerHasLight()) {
            throw new InvalidCommandException("It's too dark to see where you're going! You need: " + player.getCurrentRoom().getRequiredLight());
        }
        Room targetRoom = player.getCurrentRoom().getConnectedRoom(roomName);
        if (targetRoom == null) {
            throw new InvalidCommandException("No room called '" + roomName + "' is connected to this room.");
        }
        player.moveTo(targetRoom);
        handleLook();
    }

    private void handlePickup(String itemName) throws InvalidCommandException {
        if (itemName.isEmpty()) {
            throw new InvalidCommandException("Usage: pickup <itemName>");
        }
        if (!playerHasLight()) {
            throw new InvalidCommandException("It's too dark to find anything! You need: " + player.getCurrentRoom().getRequiredLight());
        }
        player.pickUpItem(itemName);
    }

    private void handleSolve(String puzzleName) throws InvalidCommandException, InvalidPuzzleAnswerException {
        if (puzzleName.isEmpty()) {
            throw new InvalidCommandException("Usage: solve <puzzleName>");
        }
        if (!playerHasLight()) {
            throw new InvalidCommandException("It's too dark to see any puzzles! You need: " + player.getCurrentRoom().getRequiredLight());
        }
        Puzzle puzzle = player.getCurrentRoom().findPuzzle(puzzleName);
        if (puzzle == null) {
            throw new InvalidCommandException("No puzzle called '" + puzzleName + "' in this room.");
        }

        puzzle.inspect();
        System.out.print("Your answer: ");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().trim();

        if (puzzle.attemptSolve(answer)) {
            System.out.println("\n*** REWARD: " + puzzle.getReward() + " ***");
            hintQueue.add(puzzle.getReward());
        } else {
            System.out.println("That's not the correct answer. Try again!");
        }
    }

    private void handleInspect(String targetName) throws InvalidCommandException {
        if (targetName.isEmpty()) {
            throw new InvalidCommandException("Usage: inspect <itemName|puzzleName>");
        }
        if (!playerHasLight()) {
            throw new InvalidCommandException("It's too dark to see anything! You need: " + player.getCurrentRoom().getRequiredLight());
        }
        for (GameComponent gc : player.getCurrentRoom().getContents()) {
            if (gc.getName().equalsIgnoreCase(targetName)) {
                gc.inspect();
                return;
            }
        }
        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(targetName)) {
                item.inspect();
                return;
            }
        }
        throw new InvalidCommandException("Nothing called '" + targetName + "' found in this room or your inventory.");
    }

    private void printHelp() {
        System.out.println("=== COMMANDS ===");
        System.out.println("  look              - Show current room contents");
        System.out.println("  move <roomName>   - Move to a connected room");
        System.out.println("  back              - Go back to previous room");
        System.out.println("  pickup <itemName> - Pick up an item");
        System.out.println("  inventory         - Show your inventory");
        System.out.println("  solve <puzzleName>- Attempt to solve a puzzle");
        System.out.println("  inspect <name>    - Inspect an item or puzzle");
        System.out.println("  map               - Show the full map");
        System.out.println("  status            - Show game status");
        System.out.println("  quit              - Exit the game");
    }

    public void printStatus() {
        System.out.println("=== STATUS ===");
        System.out.println("Current Room: " + player.getCurrentRoom().getName());
        System.out.println("Turn: " + turnCount);
        System.out.println("Items in inventory: " + player.getInventory().size());
    }

    private void printMap() {
        System.out.println("=== FULL MAP (Recursive View) ===");
        startRoom.exploreRecursive(0);
        System.out.println("\nMax Depth: " + startRoom.maxDepthRecursive());
    }

    public boolean winConditionCheck() {
        return player.getCurrentRoom().isExit();
    }

    // Main method to run the game
    public static void main(String[] args) {
        GameEngine game = new GameEngine();
        game.start();
    }
}
