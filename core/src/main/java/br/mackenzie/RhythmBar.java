// java
package br.mackenzie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;

public class RhythmBar {
    private float altura = 0.4f;
    private float margem = 0.3f;
    private float larguraBarra;
    private float y;
    private float velocidadeLinha = 3f; // velocidade das linhas (unidade: world units por segundo)
    private float areaInputPerc = 0.15f; // porcentagem da área de input

    private List<Float> linhas = new ArrayList<>(); // posições normalizadas X das linhas (0..1)

    public void spawnLinha() {
        linhas.add(1f); // começa na ponta direita (1f = 100%)
    }

    public void update(float dt) {
        // Se larguraBarra ainda não foi calculada (ex: antes do primeiro draw), evita divisão por zero.
        if (larguraBarra <= 0f) return;

        // Atualiza posições usando índice para evitar ConcurrentModificationException
        for (int i = 0; i < linhas.size(); ) {
            float pos = linhas.get(i);
            float deltaNormalized = (velocidadeLinha * dt) / larguraBarra; // movimentação normalizada
            float newPos = pos - deltaNormalized;

            if (newPos < 0f) {
                // remove linha que saiu da tela
                linhas.remove(i);
            } else {
                // atualiza posição
                linhas.set(i, newPos);
                i++;
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
