// src/main/java/br/mackenzie/HeatMarker.java
package br.mackenzie;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HeatMarker {
    private Texture normalImg;
    private Texture azulImg;
    private Texture forteImg;

    public HeatMarker(Texture normalImg, Texture azulImg, Texture forteImg) {
        this.normalImg = normalImg;
        this.azulImg = azulImg;
        this.forteImg = forteImg;
    }

    public void draw(SpriteBatch batch, Viewport viewport, float pos, HeatMeter meter) {
        float altura = meter.altura;
        float margem = meter.margem;
        float largura = viewport.getWorldWidth() - 2 * margem;
        float x = margem;
        float y = viewport.getWorldHeight() - altura - 0.2f;

        float raio = altura * 0.7f;
        float minCentroX = x + raio;
        float maxCentroX = x + largura - raio;
        float centroX = minCentroX + pos * (maxCentroX - minCentroX);
        float centroY = y + altura / 2f;

        float divisoriaFrio = x + largura * meter.frioPerc;
        float divisoriaQuente = x + largura * (meter.frioPerc + meter.seguroPerc);

        float esquerda = centroX - raio;
        float direita = centroX + raio;

        Texture atual;
        if (esquerda <= divisoriaFrio) {
            atual = azulImg;
        } else if (direita >= divisoriaQuente) {
            atual = forteImg;
        } else {
            atual = normalImg;
        }

        batch.begin();
        batch.draw(atual, centroX - raio, centroY - raio, raio * 2, raio * 2);
        batch.end();
    }
}
