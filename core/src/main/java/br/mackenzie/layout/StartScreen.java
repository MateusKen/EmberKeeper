// java
package br.mackenzie.layout;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class StartScreen extends ScreenAdapter {

    private final Game game;
    private Stage stage;
    private Skin skin;
    private Table mainTable;

    public StartScreen(Game game) {
        this.game = game;
    }

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

        // Fontes escaladas: uma para o texto geral e outra maior para o título
        BitmapFont defaultFont = new BitmapFont();
        defaultFont.getData().setScale(3f); // aumenta todo o texto
        skin.add("default-font", defaultFont);

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(5f); // título ainda maior
        skin.add("title-font", titleFont);

        // Estilo do texto normal
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // Estilo do título (maior)
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = skin.getFont("title-font");
        titleStyle.fontColor = Color.WHITE;
        skin.add("title", titleStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default-font");
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        buttonStyle.down = skin.newDrawable("white", Color.LIGHT_GRAY);
        buttonStyle.over = skin.newDrawable("white", Color.GRAY);
        skin.add("default", buttonStyle);

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Label title = new Label("EmberKeeper", labelStyle);
        Label instructions = new Label(
            "Instruções:\n" +
                "- Use as setas da esquerda e para direita para acertar o ritmo\n" +
                "- as para cima e para baixo para mudar o modo\n" +
                "- Acertos aumentam a barra de calor\n\n" +
                "Pressione Começar para iniciar o jogo",
            labelStyle
        );
        instructions.setWrap(true);

        TextButton startButton = new TextButton("Começar", buttonStyle);
        startButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new LayoutScreen());
            }
        });

        mainTable.add(title).padBottom(20).row();
        mainTable.add(instructions).width(800).padBottom(30).row();
        mainTable.add(startButton).width(200).height(60);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    game.setScreen(new LayoutScreen());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.07f, 0.07f, 0.07f, 1);
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
    }
}
