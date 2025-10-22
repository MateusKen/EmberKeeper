package br.mackenzie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RhythmBar {
    private float altura = 0.4f;
    private float margem = 0.3f;
    private float larguraBarra;
    private float y;
    private float velocidadeLinha = 3f; // velocidade das linhas
    private float areaInputPerc = 0.15f; // porcentagem da área de input

    private List<Float> linhas = new ArrayList<>(); // posições X das linhas

    public void spawnLinha() {
        linhas.add(1f); // começa na ponta direita (1f = 100%)
    }

    public void update(float dt) {
        Iterator<Float> it = linhas.iterator();
        while (it.hasNext()) {
            float x = it.next() - velocidadeLinha * dt / larguraBarra;
            if (x < 0f) it.remove();
            else {
                it.remove();
                linhas.add(x);
            }
        }
    }

    public void draw(ShapeRenderer shape, Viewport viewport) {
        larguraBarra = viewport.getWorldWidth() - 2 * margem;
        y = margem;

        // Barra base
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.DARK_GRAY);
        shape.rect(margem, y, larguraBarra, altura);

        // Área de input
        shape.setColor(Color.YELLOW);
        float inputX = margem + larguraBarra * areaInputPerc;
        float inputLargura = larguraBarra * 0.1f;
        shape.rect(inputX, y, inputLargura, altura);

        // Linhas de ritmo
        shape.setColor(Color.WHITE);
        for (float pos : linhas) {
            float x = margem + larguraBarra * pos;
            shape.rect(x, y, 0.05f, altura);
        }
        shape.end();
    }
}
