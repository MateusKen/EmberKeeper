package br.mackenzie.layout.gameAreaUI;

import br.mackenzie.layout.GameCompletionListener;
import br.mackenzie.layout.gameAreaUI.enums.GameMode;
import br.mackenzie.layout.gameAreaUI.enums.HitResult;
import br.mackenzie.layout.gameAreaUI.enums.PlayerDirection;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GameAreaUI extends Table {

    private final LeftPaneUI leftPane;
    private RightPaneUI rightPane;
    private final GameCompletionListener completionListener;
    private final RhythmManager rhythmManager;

    private final GameMode[] modes = GameMode.values();
    private int currentModeIndex = 0;
    private PlayerDirection lastHitDirection = PlayerDirection.RIGHT;
    private int currentItemFrameIndex = 0;

    public GameAreaUI(Skin skin, GameCompletionListener listener) {
        super(skin);
        this.completionListener = listener;
        this.rhythmManager = new RhythmManager();
        leftPane = new LeftPaneUI(skin);
        rebuildRightPane();
        this.setDebug(true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        rhythmManager.update(delta); // Atualiza a lógica de ritmo
    }

    private void buildLayout() {
        this.clearChildren();
        this.add(leftPane).expand().fill();
        this.add(rightPane).expand().fill();
    }

    public void processHit(PlayerDirection hitDirection) {
        // A alternância agora é verificada pela própria nota no RhythmManager
        HitResult result = rhythmManager.checkHit(hitDirection);

        if (result == HitResult.PERFECT || result == HitResult.GOOD) {
            // Atualiza a direção do jogador para animação
            leftPane.setPlayerDirection(hitDirection);
            lastHitDirection = hitDirection;

            GameMode currentMode = modes[currentModeIndex];
            if (currentMode == GameMode.FORNO) {
                return;
            }

            int frameCount = GameModeConfig.getFrameCount(currentMode);
            if (frameCount > 1) {
                currentItemFrameIndex = (currentItemFrameIndex + 1) % frameCount;
                rightPane.updateItemAnimation(currentItemFrameIndex);

                if (currentItemFrameIndex == 0) {
                    GameModeConfig.incrementCompletion(currentMode);
                    if (GameModeConfig.isComplete(currentMode)) {
                        if (GameModeConfig.areAllModesComplete()) {
                            completionListener.onGameCompleted();
                            return;
                        }
                        changeMode(true);
                    } else {
                        rebuildRightPane();
                    }
                }
            }
        }
    }

    public void changeMode(boolean up) {
        int initialIndex = currentModeIndex;
        int newIndex = currentModeIndex;
        int numModes = modes.length;

        for (int i = 0; i < numModes; i++) {
            if (up) {
                newIndex = (newIndex + 1) % numModes;
            } else {
                newIndex = (newIndex - 1 + numModes) % numModes;
            }

            if (!GameModeConfig.isComplete(modes[newIndex])) {
                currentModeIndex = newIndex;
                rebuildRightPane();
                return;
            }
        }
        currentModeIndex = initialIndex;
    }

    private void rebuildRightPane() {
        if (rightPane != null) {
            rightPane.dispose();
        }

        currentItemFrameIndex = 0;
        GameMode newMode = modes[currentModeIndex];
        rhythmManager.setMode(newMode); // Informa ao manager sobre a mudança de modo

        int frameCount = GameModeConfig.getFrameCount(newMode);
        int completions = GameModeConfig.getCompletions(newMode);

        rightPane = new RightPaneUI(getSkin(), newMode, frameCount, completions);
        buildLayout();
    }

    public RhythmManager getRhythmManager() {
        return rhythmManager;
    }

    public void dispose() {
        leftPane.dispose();
        if (rightPane != null) {
            rightPane.dispose();
        }
    }
}
