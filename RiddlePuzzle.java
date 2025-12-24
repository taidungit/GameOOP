public class RiddlePuzzle extends Puzzle {
    private String riddle;
    private String correctAnswer;

    public RiddlePuzzle(String name, int difficulty, String reward, String riddle, String correctAnswer) {
        super(name, difficulty, reward);
        this.riddle = riddle;
        this.correctAnswer = correctAnswer.toLowerCase();
    }

    @Override
    public void inspect() {
        System.out.println("=== Riddle Puzzle: " + name + " ===");
        System.out.println("Difficulty: " + difficulty);
        System.out.println("Riddle: " + riddle);
        if (solved) {
            System.out.println("(Already solved!)");
        }
    }

    @Override
    public boolean attemptSolve(String answer) throws InvalidPuzzleAnswerException {
        if (solved) {
            throw new InvalidPuzzleAnswerException("This puzzle has already been solved!");
        }
        if (answer == null || answer.trim().isEmpty()) {
            throw new InvalidPuzzleAnswerException("Answer cannot be empty!");
        }
        if (answer.toLowerCase().equals(correctAnswer)) {
            solved = true;
            System.out.println("Correct! You solved the riddle!");
            return true;
        }
        return false;
    }
}
