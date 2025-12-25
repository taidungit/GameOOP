import java.util.ArrayList;
import java.util.Stack;

public class Player {
    private Stack<Room> moveHistory;
    private ArrayList<Item> inventory;
    private Room currentRoom;

    public Player(Room startingRoom) {
        this.moveHistory = new Stack<>();
        this.inventory = new ArrayList<>();
        this.currentRoom = startingRoom;
    }

    public void moveTo(Room r) throws LockedRoomException {
        // Check if the room requires a key
        if (r.getRequiredKey() != null && !hasKey(r.getRequiredKey())) {
            throw new LockedRoomException("This room requires: " + r.getRequiredKey());
        }
        moveHistory.push(currentRoom);
        currentRoom = r;
        System.out.println("You moved to: " + r.getName());
    }

    public void goBack() {
        if (moveHistory.isEmpty()) {
            System.out.println("No previous room to go back to!");
            return;
        }
        currentRoom = moveHistory.pop();
        System.out.println("You went back to: " + currentRoom.getName());
    }

    public void pickUpItem(String name) {
        Item item = currentRoom.findItem(name);
        if (item == null) {
            System.out.println("No item called '" + name + "' in this room.");
            return;
        }
        currentRoom.removeContent(item);
        item.collect(this);
    }

    public void addToInventory(Item item) {
        inventory.add(item);
    }

    public boolean hasKey(String keyName) {
        for (Item item : inventory) {
            if (item.getItemType().equals("KEY") && item.getName().equalsIgnoreCase(keyName)) {
                return true;
            }
        }
        return false;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public void showInventory() {
        System.out.println("=== Inventory ===");
        if (inventory.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        // Sort inventory using insertion sort (as required)
        sortInventory();
        for (Item item : inventory) {
            System.out.println("  - " + item.getName() + " [" + item.getItemType() + "] (value: " + item.getValue() + ")");
        }
    }

     //Insertion sort for ArrayList<Item> using Comparable
    private void sortInventory() {
        for (int i = 1; i < inventory.size(); i++) {
            Item key = inventory.get(i);
            int j = i - 1;
            while (j >= 0 && inventory.get(j).compareTo(key) > 0) {
                inventory.set(j + 1, inventory.get(j));
                j--;
            }
            inventory.set(j + 1, key);
        }
    }
}
