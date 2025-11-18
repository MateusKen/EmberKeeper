package br.mackenzie.layout;

import br.mackenzie.layout.gameAreaUI.GameAreaUI;
import br.mackenzie.layout.gameAreaUI.enums.PlayerDirection;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LayoutScreen extends ScreenAdapter implements GameCompletionListener {

    private Stage stage;
    private Skin skin;
    private Table mainTable;

    private HeatBarUI topBar;
    private GameAreaUI gameArea;
    private RhythmBarUI rhythmBar;
    private EndScreenUI endScreen;

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();
        skin.add("default-font", new com.badlogic.gdx.graphics.g2d.BitmapFont());
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default-font");
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        buttonStyle.down = skin.newDrawable("white", Color.LIGHT_GRAY);
        buttonStyle.over = skin.newDrawable("white", Color.GRAY);
        skin.add("default", buttonStyle);

        topBar = new HeatBarUI();
        gameArea = new GameAreaUI(skin, this, topBar);
        rhythmBar = new RhythmBarUI(gameArea.getRhythmManager());

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        setupGameLayout();
        mainTable.setDebug(true);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                // --- Log de Captura de Input ---
                Gdx.app.log("InputCapture", "Tecla pressionada: " + Input.Keys.toString(keycode));
                // --- Fim da modificação ---

                if (gameArea == null) {
                    return false;
                }
                switch (keycode) {
                    case Input.Keys.LEFT:
                        gameArea.processHit(PlayerDirection.LEFT);
                        return true;
                    case Input.Keys.RIGHT:
                        gameArea.processHit(PlayerDirection.RIGHT);
                        return true;
                    case Input.Keys.UP:
                        gameArea.changeMode(true);
                        return true;
                    case Input.Keys.DOWN:
                        gameArea.changeMode(false);
                        return true;
                }
                return false;
            }
        });
    }

    private void setupGameLayout() {
        mainTable.clear();
        mainTable.add(topBar).expandX().fillX().height(60);
        mainTable.row();
        mainTable.add(gameArea).expand().fill();
        mainTable.row();
        mainTable.add(rhythmBar).expandX().fillX().height(80);
    }

    @Override
    public void onGameCompleted() {
        if (gameArea != null) {
            gameArea.dispose();
            gameArea = null;
        }
        if (rhythmBar != null) {
            rhythmBar.dispose();
            rhythmBar = null;
        }
        topBar = null;

        mainTable.clear();
        endScreen = new EndScreenUI(skin);
        mainTable.add(endScreen).expand().fill();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (gameArea != null) {
            gameArea.dispose();
        }
        if (endScreen != null) {
            endScreen.dispose();
        }
        if (rhythmBar != null) {
            rhythmBar.dispose();
        }
    }
}
