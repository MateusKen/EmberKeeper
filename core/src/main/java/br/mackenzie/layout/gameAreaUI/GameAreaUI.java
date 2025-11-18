package br.mackenzie.layout.gameAreaUI;

import br.mackenzie.layout.GameCompletionListener;
import br.mackenzie.layout.HeatBarUI;
import br.mackenzie.layout.gameAreaUI.enums.GameMode;
import br.mackenzie.layout.gameAreaUI.enums.HitResult;
import br.mackenzie.layout.gameAreaUI.enums.PlayerDirection;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GameAreaUI extends Table {

    private final LeftPaneUI leftPane;
    private RightPaneUI rightPane;
    private final GameCompletionListener completionListener;
    private final RhythmManager rhythmManager;
    private final HeatBarUI heatBar;

    private final GameMode[] modes = GameMode.values();
    private int currentModeIndex = 0;
    private int currentItemFrameIndex = 0;
    private PlayerDirection lastHitDirection = PlayerDirection.RIGHT;

    public GameAreaUI(Skin skin, GameCompletionListener listener, HeatBarUI heatBar) {
        super(skin);
        this.completionListener = listener;
        this.rhythmManager = new RhythmManager(120.0f);
        this.heatBar = heatBar; // Recebe a instância da HeatBar

        leftPane = new LeftPaneUI(skin);
        rebuildRightPane();
        this.setDebug(true);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        rhythmManager.update(delta);
        heatBar.act(delta); // Atualiza a barra de calor para que ela regrida
    }

    private void buildLayout() {
        this.clearChildren();
        // A HeatBar não é mais adicionada aqui
        Table panesTable = new Table();
        panesTable.add(leftPane).expand().fill();
        panesTable.add(rightPane).expand().fill();
        this.add(panesTable).expand().fill();
    }

    public void processHit(PlayerDirection hitDirection) {
        HitResult result = rhythmManager.checkHit(hitDirection);

        if (result == HitResult.PERFECT || result == HitResult.GOOD) {
            heatBar.registerHit(); // Incrementa a barra de calor
            leftPane.showCheeringCrowd(); // Mostra a torcida feliz
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
        } else {
            // Se errou (Miss), mostra a multidão padrão
            leftPane.showDefaultCrowd();
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
        rhythmManager.setMode(newMode);

        int frameCount = GameModeConfig.getFrameCount(newMode);
        int completions = GameModeConfig.getCompletions(newMode);

        rightPane = new RightPaneUI(getSkin(), newMode, frameCount, completions);
        buildLayout();
    }

    public RhythmManager getRhythmManager() {
        return rhythmManager;
    }

    public void handlePlayerAction(PlayerDirection hitDirection) {
        HitResult result = rhythmManager.checkHit(hitDirection);

        if (result == HitResult.PERFECT || result == HitResult.GOOD) {
            // Se o acerto foi bom ou perfeito, mostra a torcida
            leftPane.showCheeringCrowd();
        } else {
            // Se errou, mostra a multidão padrão
            leftPane.showDefaultCrowd();
        }
    }

    public void dispose() {
        leftPane.dispose();
        if (rightPane != null) {
            rightPane.dispose();
        }
        heatBar.dispose();
    }
}
