public abstract class Puzzle extends GameComponent implements Comparable<Puzzle> {
    protected int difficulty;
    protected boolean solved;
    protected String reward; // What solving this puzzle unlocks (item name or room name)

    public Puzzle(String name, int difficulty, String reward) {
        super(name);
        this.difficulty = difficulty;
        this.solved = false;
        this.reward = reward;
    }

    public abstract boolean attemptSolve(String answer) throws InvalidPuzzleAnswerException;

    public boolean isSolved() {
        return solved;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getReward() {
        return reward;
    }

    @Override
    public int compareTo(Puzzle other) {
        return Integer.compare(this.difficulty, other.difficulty);
    }
}
