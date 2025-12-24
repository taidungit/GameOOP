public abstract class GameComponent {
    protected String name;
    public abstract void inspect();

    public GameComponent(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
