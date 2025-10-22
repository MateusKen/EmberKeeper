// src/main/java/br/mackenzie/HeatMeter.java
package br.mackenzie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HeatMeter {
    public float altura = 0.5f;
    public float espaco = 0.2f;
    public float margem = 0.3f;
    public float frioPerc = 0.2f;
    public float seguroPerc = 0.6f;
    public float quentePerc = 0.2f;

    public void draw(ShapeRenderer shape, Viewport viewport) {
        float largura = viewport.getWorldWidth() - 2 * margem;
        float x = margem;
        float y = viewport.getWorldHeight() - altura - espaco;

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BLACK);
        shape.rect(x - 2f/60f, y - 2f/60f, largura + 4f/60f, altura + 4f/60f);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(new Color(0.3f, 0.2f, 0.7f, 1f));
        shape.rect(x, y, largura * frioPerc, altura);

        shape.setColor(Color.ORANGE);
        shape.rect(x + largura * frioPerc, y, largura * seguroPerc, altura);

        shape.setColor(new Color(0.7f, 0.1f, 0.1f, 1f));
        shape.rect(x + largura * (frioPerc + seguroPerc), y, largura * quentePerc, altura);

        shape.end();
    }
}
