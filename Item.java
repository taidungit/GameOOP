public class Item extends GameComponent implements Collectible, Comparable<Item> {
    private int value;
    private String itemType;
    private String description;
    public Item(String name, int value, String itemType, String description){
        super(name);
        this.value = value;
        this.itemType = itemType;
        this.description = description;
    }
    @Override
    public void inspect() {
        System.out.println("Item: " + name + " [" + itemType + "] - Value: " + value);
        // NEW: Print text if it exists
        if (description != null) {
            System.out.println("You read the " + name + ": \"" + description + "\"");
        }
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public void collect(Player p){
        p.addToInventory(this);
        System.out.println("Collected " + name);
    }
    public int getValue(){return value;}
    public String getItemType(){return itemType;}
    @Override
    public int compareTo(Item other){
        int valueCompare = Integer.compare(this.value, other.value);
        if (valueCompare != 0) return valueCompare;
        return this.name.compareTo(other.name);
    }
}
