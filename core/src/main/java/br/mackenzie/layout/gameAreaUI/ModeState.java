package br.mackenzie.layout.gameAreaUI;

public class ModeState {
    public final int frameCount;
    private int completions;
    private static final int REQUIRED_COMPLETIONS = 3;

    public ModeState(int frameCount) {
        this.frameCount = frameCount;
        this.completions = 0;
    }

    public int getCompletions() {
        return completions;
    }

    public boolean isComplete() {
        return completions >= REQUIRED_COMPLETIONS;
    }

    public void incrementCompletion() {
        if (!isComplete()) {
            completions++;
        }
    }
}
