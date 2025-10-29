// java
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

public class Main implements ApplicationListener {

    private float marcadorPos = 0f;
    private float velocidade = 0.2f;
    private int ultimaTecla = 0;
    private float decaimento = 0.05f;
    private float tempoSpawn = 0f;

    private float multEsquerda = 0.8f;
    private float multMeio = 2.5f;
    private float multDireita = 0.8f;
    private float penalidadeFora = 1.2f;

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
        viewport = new FitViewport(8, 5);
        shape = new ShapeRenderer();
        marcadorImg = new Texture("fogo.png");
        marcadorAzuladoImg = new Texture("fogo_azulado.png");
        marcadorForteImg = new Texture("fogo_forte.png");
        fundo = new Texture("background.png");
        batch = new SpriteBatch();
        heatMeter = new HeatMeter();
        heatMarker = new HeatMarker(marcadorImg, marcadorAzuladoImg, marcadorForteImg);

        rhythmBar = new RhythmBar();
        rhythmBar.setTrackedColor(Color.RED);

        rhythmBar.setSpawnRate(1.5f);
        rhythmBar.setVelocidadeLinha(5f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        atualizarMarcadorComInput(dt);

        tempoSpawn += dt;
        rhythmBar.update(dt, viewport);

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

    private float getMultiplier(String area) {
        switch (area) {
            case "ESQUERDA": return multEsquerda;
            case "MEIO": return multMeio;
            case "DIREITA": return multDireita;
            default: return 1.0f;
        }
    }

    private void atualizarMarcadorComInput(float dt) {
        marcadorPos -= decaimento * dt;
        marcadorPos = Math.max(0f, marcadorPos);

        float baseMove = velocidade * dt * 20f;

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.LEFT) && ultimaTecla != 1) {
            if (rhythmBar.getTrackedLinePos() < 0f) rhythmBar.selectMostLeft();
            rhythmBar.printLinesAreas(viewport);

            String area = rhythmBar.getTrackedAreaPortuguese(viewport);

            float oldPos = marcadorPos;
            if ("FORA".equals(area)) {
                marcadorPos = Math.max(0f, marcadorPos - baseMove * penalidadeFora);
            } else {
                float multiplier = getMultiplier(area);
                marcadorPos = Math.max(0f, marcadorPos + baseMove * multiplier);
            }

            // Decide previous/next automaticamente por área
            rhythmBar.onPlayerClick(viewport);
            ultimaTecla = 1;
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.RIGHT) && ultimaTecla != 2) {
            if (rhythmBar.getTrackedLinePos() < 0f) rhythmBar.selectMostLeft();
            rhythmBar.printLinesAreas(viewport);

            String area = rhythmBar.getTrackedAreaPortuguese(viewport);

            float oldPos = marcadorPos;
            if ("FORA".equals(area)) {
                marcadorPos = Math.max(0f, marcadorPos - baseMove * penalidadeFora);
            } else {
                float multiplier = getMultiplier(area);
                marcadorPos = Math.min(1f, marcadorPos + baseMove * multiplier);
            }

            // Decide previous/next automaticamente por área
            rhythmBar.onPlayerClick(viewport);
            ultimaTecla = 2;
        }
    }

    @Override public void pause() { }
    @Override public void resume() { }

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
