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
    private float velocidadeLinha = 3f;
    private List<Float> linhas = new ArrayList<>();

    // Spawn
    private float spawnSpacing = 1.0f;
    private float spawnInterval = 0f;
    private float spawnAccumulator = 0f;
    private float lineWidth = 0.05f;

    // Contorno
    private float borderThickness = 0.06f;

    // Áreas coladas (ajustado: inner ~6% e outers ~10% cada)
    // total = outer + inner + outer = 0.10 + 0.06 + 0.10 = 0.26
    private float innerAreaWidthPerc = 0.06f;  // 6%
    private float outerAreaWidthPerc = 0.10f;  // 10% cada
    private float areaLeftPerc = 0.15f;        // posição da borda esquerda do grupo (mais à direita que antes)

    public void spawnLinha() {
        // começa ligeiramente fora à direita (calcula offset com segurança)
        float startOffset = (larguraBarra > 0f) ? (lineWidth / larguraBarra) : 0.02f;
        linhas.add(1f + startOffset);
    }

    public void update(float dt) {
        if (larguraBarra <= 0f) return;

        if (spawnInterval <= 0f && velocidadeLinha > 0f) {
            spawnInterval = spawnSpacing / velocidadeLinha;
            if (spawnInterval <= 0f) spawnInterval = 0.1f;
        }

        spawnAccumulator += dt;
        while (spawnInterval > 0f && spawnAccumulator >= spawnInterval) {
            spawnLinha();
            spawnAccumulator -= spawnInterval;
        }

        for (int i = 0; i < linhas.size(); ) {
            float pos = linhas.get(i);
            float deltaNormalized = (velocidadeLinha * dt) / larguraBarra;
            float newPos = pos - deltaNormalized;

            if (newPos < 0f - (lineWidth / larguraBarra)) {
                linhas.remove(i);
            } else {
                linhas.set(i, newPos);
                i++;
            }
        }
    }

    public void draw(ShapeRenderer shape, Viewport viewport) {
        larguraBarra = viewport.getWorldWidth() - 2 * margem;
        y = margem;

        // garante que o grupo de áreas não ultrapasse a barra
        float combinedWidthPerc = outerAreaWidthPerc * 2f + innerAreaWidthPerc;
        if (combinedWidthPerc > 1f) combinedWidthPerc = 1f;
        if (areaLeftPerc < 0f) areaLeftPerc = 0f;
        if (areaLeftPerc > 1f - combinedWidthPerc) areaLeftPerc = 1f - combinedWidthPerc;

        // calcula posições contíguas (coladas)
        float leftOuterLeftPerc = areaLeftPerc;
        float leftOuterRightPerc = leftOuterLeftPerc + outerAreaWidthPerc;
        float innerLeftPerc = leftOuterRightPerc;
        float innerRightPerc = innerLeftPerc + innerAreaWidthPerc;
        float rightOuterLeftPerc = innerRightPerc;
        float rightOuterRightPerc = rightOuterLeftPerc + outerAreaWidthPerc;

        // Desenha contorno
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BLACK);
        float border = borderThickness;
        float borderX = margem - border;
        float borderY = y - border;
        float borderW = larguraBarra + 2f * border;
        float borderH = altura + 2f * border;
        shape.rect(borderX, borderY, borderW, borderH);

        // Barra base
        shape.setColor(Color.DARK_GRAY);
        shape.rect(margem, y, larguraBarra, altura);

        // Áreas externas (esquerda e direita) - cinza
        shape.setColor(Color.GRAY);
        if (leftOuterRightPerc > leftOuterLeftPerc) {
            float leftX = margem + larguraBarra * leftOuterLeftPerc;
            float leftW = larguraBarra * (leftOuterRightPerc - leftOuterLeftPerc);
            shape.rect(leftX, y, leftW, altura);
        }
        if (rightOuterRightPerc > rightOuterLeftPerc) {
            float rightX = margem + larguraBarra * rightOuterLeftPerc;
            float rightW = larguraBarra * (rightOuterRightPerc - rightOuterLeftPerc);
            shape.rect(rightX, y, rightW, altura);
        }

        // Área interna (amarela)
        shape.setColor(Color.YELLOW);
        if (innerRightPerc > innerLeftPerc) {
            float innerX = margem + larguraBarra * innerLeftPerc;
            float innerW = larguraBarra * (innerRightPerc - innerLeftPerc);
            shape.rect(innerX, y, innerW, altura);
        }

        // Linhas de ritmo (por cima)
        shape.setColor(Color.WHITE);
        for (float pos : linhas) {
            float x = margem + larguraBarra * pos;
            shape.rect(x, y, lineWidth, altura);
        }
        shape.end();
    }

    /**
     * Retorna true se alguma linha estiver sobre qualquer uma das três áreas
     * (considera largura da linha convertida para fração da barra).
     */
    public boolean isAnyLineOverlappingAreas(Viewport viewport) {
        float lBarra = viewport.getWorldWidth() - 2 * margem;
        if (lBarra <= 0f) return false;

        // recalcula posições das áreas (mesma lógica do draw)
        float combinedWidthPerc = outerAreaWidthPerc * 2f + innerAreaWidthPerc;
        if (combinedWidthPerc > 1f) combinedWidthPerc = 1f;
        float areaLeft = areaLeftPerc;
        if (areaLeft < 0f) areaLeft = 0f;
        if (areaLeft > 1f - combinedWidthPerc) areaLeft = 1f - combinedWidthPerc;

        float leftOuterLeftPerc = areaLeft;
        float leftOuterRightPerc = leftOuterLeftPerc + outerAreaWidthPerc;
        float innerLeftPerc = leftOuterRightPerc;
        float innerRightPerc = innerLeftPerc + innerAreaWidthPerc;
        float rightOuterLeftPerc = innerRightPerc;
        float rightOuterRightPerc = rightOuterLeftPerc + outerAreaWidthPerc;

        float eps = lineWidth / lBarra; // tolerância em unidades normalizadas

        for (float pos : linhas) {
            if (pos + eps >= leftOuterLeftPerc && pos - eps <= leftOuterRightPerc) return true;
            if (pos + eps >= innerLeftPerc && pos - eps <= innerRightPerc) return true;
            if (pos + eps >= rightOuterLeftPerc && pos - eps <= rightOuterRightPerc) return true;
        }
        return false;
    }

    // Setters úteis
    public void setVelocidadeLinha(float velocidadeLinha) {
        this.velocidadeLinha = velocidadeLinha;
        this.spawnInterval = (velocidadeLinha > 0f) ? (spawnSpacing / velocidadeLinha) : 0f;
    }

    public void setSpawnSpacing(float spawnSpacing) {
        this.spawnSpacing = spawnSpacing;
        this.spawnInterval = (velocidadeLinha > 0f) ? (spawnSpacing / velocidadeLinha) : 0f;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setBorderThickness(float borderThickness) {
        this.borderThickness = borderThickness;
    }

    public void setInnerAreaWidthPerc(float innerAreaWidthPerc) {
        this.innerAreaWidthPerc = innerAreaWidthPerc;
    }

    public void setOuterAreaWidthPerc(float outerAreaWidthPerc) {
        this.outerAreaWidthPerc = outerAreaWidthPerc;
    }

    public void setAreaLeftPerc(float areaLeftPerc) {
        this.areaLeftPerc = areaLeftPerc;
    }

    public void reset() {
        linhas.clear();
        spawnAccumulator = 0f;
    }
}
