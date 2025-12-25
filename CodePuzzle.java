public class CodePuzzle extends Puzzle {
    private String correctCode;
    private int maxAttempts;
    private int attempts;

    public CodePuzzle(String name, int difficulty, Item reward, String correctCode, int maxAttempts) {
        super(name, difficulty, reward);
        this.correctCode = correctCode;
        this.maxAttempts = maxAttempts;
        this.attempts = 0;
    }

    @Override
    public void inspect() {
        System.out.println("=== Code Lock: " + name + " ===");
        System.out.println("Difficulty: " + difficulty);
        System.out.println("Enter the correct code to unlock.");
        System.out.println("Attempts remaining: " + (maxAttempts - attempts));
        if (solved) {
            System.out.println("(Already unlocked!)");
        }
    }

    @Override
    public boolean attemptSolve(String answer) throws InvalidPuzzleAnswerException {
        if (solved) {
            throw new InvalidPuzzleAnswerException("This lock has already been opened!");
        }
        if (attempts >= maxAttempts) {
            throw new InvalidPuzzleAnswerException("No attempts remaining! The lock is jammed.");
        }
        if (answer == null || answer.trim().isEmpty()) {
            throw new InvalidPuzzleAnswerException("Code cannot be empty!");
        }

        attempts++;
        if (answer.equals(correctCode)) {
            solved = true;
            System.out.println("*Click* The lock opens!");
            return true;
        }
        System.out.println("Wrong code. Attempts remaining: " + (maxAttempts - attempts));
        return false;
    }
}
