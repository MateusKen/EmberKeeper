package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    private float marcadorPos = 0f;
    private float velocidade = 0.2f;
    private int ultimaTecla = 0;
    private float decaimento = 0.05f;
    private float tempoSpawn = 0f;

    private ShapeRenderer shape;

    private Texture fundo;
    private Texture marcadorImg;
    private Texture marcadorAzuladoImg;
    private Texture marcadorForteImg;

    private SpriteBatch batch;

    private Viewport viewport;

    private HeatMeter heatMeter;
    private HeatMarker heatMarker;
    private RhythmBar rhythmBar;

    @Override
    public void create() {
        viewport = new FitViewport(8, 5); // Tamanho virtual

        shape = new ShapeRenderer();

        marcadorImg = new Texture("fogo.png");
        marcadorAzuladoImg = new Texture("fogo_azulado.png");
        marcadorForteImg = new Texture("fogo_forte.png");
        fundo = new Texture("background.png");
        batch = new SpriteBatch();

        heatMeter = new HeatMeter();
        heatMarker = new HeatMarker(marcadorImg, marcadorAzuladoImg, marcadorForteImg);
        rhythmBar = new RhythmBar();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        atualizarMarcadorComInput(dt);

        // Spawna linhas de ritmo a cada 1 segundo (exemplo)
        tempoSpawn += dt;
        if (tempoSpawn > 1f) {
            //rhythmBar.spawnLinha();
            tempoSpawn = 0f;
        }
        rhythmBar.update(dt);

        draw();
    }


    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(fundo, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        shape.setProjectionMatrix(viewport.getCamera().combined);
        heatMeter.draw(shape, viewport);
        heatMarker.draw(batch, viewport, marcadorPos, heatMeter);

        rhythmBar.draw(shape, viewport);
    }

    private void atualizarMarcadorComInput(float dt) {
        // Decresce o marcador com o tempo
        marcadorPos -= decaimento * dt;
        marcadorPos = Math.max(0f, marcadorPos);

        // checa se alguma linha está sobre as áreas no momento do input
        boolean acerto = rhythmBar.isAnyLineOverlappingAreas(viewport);
        float baseMove = velocidade * dt * 10f;
        float multiplier = acerto ? 2f : 1f; // dobra o movimento em caso de acerto

        // Se apertar esquerda e a última não foi esquerda
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.LEFT) && ultimaTecla != 1) {
            marcadorPos = Math.max(0f, marcadorPos + baseMove * multiplier);
            ultimaTecla = 1;
        }
        // Se apertar direita e a última não foi direita
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.RIGHT) && ultimaTecla != 2) {
            marcadorPos = Math.min(1f, marcadorPos + baseMove * multiplier);
            ultimaTecla = 2;
        }
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        marcadorImg.dispose();
        marcadorAzuladoImg.dispose();
        marcadorForteImg.dispose();
        shape.dispose();
        batch.dispose();
        fundo.dispose();
    }
}
