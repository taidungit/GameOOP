import java.util.ArrayList;

public class Room extends GameComponent {
    private ArrayList<GameComponent> contents; // items, puzzles, subrooms
    private ArrayList<Room> connectedRooms;
    private boolean isExit;
    private String requiredKey; // null if no key required

    public Room(String name, boolean isExit) {
        super(name);
        this.contents = new ArrayList<>();
        this.connectedRooms = new ArrayList<>();
        this.isExit = isExit;
        this.requiredKey = null;
    }

    public Room(String name, boolean isExit, String requiredKey) {
        this(name, isExit);
        this.requiredKey = requiredKey;
    }

    @Override
    public void inspect() {
        System.out.println("=== " + name + " ===");
        if (isExit) {
            System.out.println("*** This is the EXIT! ***");
        }

        ArrayList<Puzzle> puzzlesInRoom = new ArrayList<>();
        for (GameComponent gc : contents) {
            if (gc instanceof Puzzle) {
                puzzlesInRoom.add((Puzzle) gc);
            }
        }
        sortPuzzles(puzzlesInRoom);//sort by difficulty
        System.out.println("Contents:");
        if (contents.isEmpty()) {
            System.out.println("  (empty)");
        } else {
            //print the sort by difficulty puzzle
            for (Puzzle p : puzzlesInRoom)
                System.out.println("  - "+p.getName()+" (Difficulty: " + p.getDifficulty() + ")");
            //print the component not puzzle
            for (GameComponent gc : contents) {
                if (!(gc instanceof Puzzle)) {
                    System.out.println("  - " + gc.getName());
                }
            }
        }
        System.out.println("Connected rooms:");
        if (connectedRooms.isEmpty()) {
            System.out.println("  (no connected room)");
        } else {
            for (Room r : connectedRooms) {
                String lockInfo = r.requiredKey != null ? " [LOCKED - requires " + r.requiredKey + "]" : "";
                System.out.println("  -> " + r.getName() + lockInfo);
            }
        }
    }

    private void sortPuzzles(ArrayList<Puzzle> puzzles) {
        for (int i = 1; i < puzzles.size(); i++) {
            Puzzle key = puzzles.get(i);
            int j = i - 1;
            while (j >= 0 && puzzles.get(j).compareTo(key) > 0) {
                puzzles.set(j + 1, puzzles.get(j));
                j--;
            }
            puzzles.set(j + 1, key);
        }
    }


public void exploreRecursive(int depth) {
    String indent = (depth == 0) ? "" : "  ".repeat(depth) + " - ";
    System.out.println(indent + name);
    for (Room connected : connectedRooms) {
        connected.exploreRecursive(depth + 1);
    }
}



    public boolean containsItemRecursive(String itemName) {
        // Check current room contents
        for (GameComponent gc : contents) {
            if (gc instanceof Item && gc.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        // Recurse into connected rooms
        for (Room connected : connectedRooms) {
            if (connected.containsItemRecursive(itemName)) {
                return true;
            }
        }
        return false;
    }


    public int maxDepthRecursive() {
        if (connectedRooms.isEmpty()) {
            return 1;
        }
        int maxChildDepth = 0;
        for (Room connected : connectedRooms) {
            int childDepth = connected.maxDepthRecursive();
            if (childDepth > maxChildDepth) {
                maxChildDepth = childDepth;
            }
        }
        return 1 + maxChildDepth;
    }

    // === HELPER METHODS ===

    public void addContent(GameComponent gc) {
        contents.add(gc);
    }

    public void removeContent(GameComponent gc) {
        contents.remove(gc);
    }

    public void addConnectedRoom(Room room) {
        connectedRooms.add(room);
    }

    public ArrayList<GameComponent> getContents() {
        return contents;
    }

    public ArrayList<Room> getConnectedRooms() {
        return connectedRooms;
    }

    public boolean isExit() {
        return isExit;
    }

    public String getRequiredKey() {
        return requiredKey;
    }

    public void setRequiredKey(String requiredKey) {
        this.requiredKey = requiredKey;
    }

    public Room getConnectedRoom(String roomName) {
        for (Room r : connectedRooms) {
            if (r.getName().equalsIgnoreCase(roomName)) {
                return r;
            }
        }
        return null;
    }

    public Item findItem(String itemName) {
        for (GameComponent gc : contents) {
            if (gc instanceof Item && gc.getName().equalsIgnoreCase(itemName)) {
                return (Item) gc;
            }
        }
        return null;
    }

    public Puzzle findPuzzle(String puzzleName) {
        for (GameComponent gc : contents) {
            if (gc instanceof Puzzle && gc.getName().equalsIgnoreCase(puzzleName)) {
                return (Puzzle) gc;
            }
        }
        return null;
    }
}
