// java
package br.mackenzie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RhythmBarAreas {

    // Dimensões e posicionamento
    private float altura = 0.4f;
    private float margem = 0.3f;
    private float borderThickness = 0.06f;

    // Áreas coladas (percentuais sobre a largura útil da barra)
    private float innerAreaWidthPerc = 0.06f;
    private float outerAreaWidthPerc = 0.08f;
    private float areaLeftPerc = 0.15f;

    // Cache de layout calculado por frame
    private float larguraBarra = 0f;
    private float y = 0f;

    // Atualiza layout usando a margem/y configurados internamente
    public void updateLayout(Viewport viewport) {
        updateLayout(viewport, this.margem, this.margem);
    }

    // Nova sobrecarga: permite passar margem (x) e y explicitamente
    public void updateLayout(Viewport viewport, float margem, float y) {
        this.margem = margem;
        this.y = y;
        this.larguraBarra = viewport.getWorldWidth() - 2f * margem;

        // Clamps para manter áreas válidas
        float combinedWidthPerc = outerAreaWidthPerc * 2f + innerAreaWidthPerc;
        if (combinedWidthPerc > 1f) combinedWidthPerc = 1f;
        if (areaLeftPerc < 0f) areaLeftPerc = 0f;
        if (areaLeftPerc > 1f - combinedWidthPerc) areaLeftPerc = 1f - combinedWidthPerc;
    }

    public void draw(ShapeRenderer shape, Viewport viewport) {
        // Recalcula layout caso não tenha sido chamado
        if (larguraBarra <= 0f) updateLayout(viewport);

        float combinedWidthPerc = outerAreaWidthPerc * 2f + innerAreaWidthPerc;
        if (combinedWidthPerc > 1f) combinedWidthPerc = 1f;
        float areaLeft = Math.max(0f, Math.min(areaLeftPerc, 1f - combinedWidthPerc));

        float leftOuterLeftPerc = areaLeft;
        float leftOuterRightPerc = leftOuterLeftPerc + outerAreaWidthPerc;
        float innerLeftPerc = leftOuterRightPerc;
        float innerRightPerc = innerLeftPerc + innerAreaWidthPerc;
        float rightOuterLeftPerc = innerRightPerc;
        float rightOuterRightPerc = rightOuterLeftPerc + outerAreaWidthPerc;

        shape.begin(ShapeRenderer.ShapeType.Filled);

        // Contorno
        shape.setColor(Color.BLACK);
        shape.rect(margem - borderThickness, y - borderThickness,
            larguraBarra + 2f * borderThickness, altura + 2f * borderThickness);

        // Fundo da barra
        shape.setColor(Color.DARK_GRAY);
        shape.rect(margem, y, larguraBarra, altura);

        // Áreas externas
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

        // Área central
        shape.setColor(Color.YELLOW);
        if (innerRightPerc > innerLeftPerc) {
            float innerX = margem + larguraBarra * innerLeftPerc;
            float innerW = larguraBarra * (innerRightPerc - innerLeftPerc);
            shape.rect(innerX, y, innerW, altura);
        }

        shape.end();
    }

    // Auxiliares de área
    public String resolveAreaPortuguese(float pos, float eps) {
        float combinedWidthPerc = outerAreaWidthPerc * 2f + innerAreaWidthPerc;
        if (combinedWidthPerc > 1f) combinedWidthPerc = 1f;
        float areaLeft = Math.max(0f, Math.min(areaLeftPerc, 1f - combinedWidthPerc));

        float leftOuterLeftPerc = areaLeft;
        float leftOuterRightPerc = leftOuterLeftPerc + outerAreaWidthPerc;
        float innerLeftPerc = leftOuterRightPerc;
        float innerRightPerc = innerLeftPerc + innerAreaWidthPerc;
        float rightOuterLeftPerc = innerRightPerc;
        float rightOuterRightPerc = rightOuterLeftPerc + outerAreaWidthPerc;

        if (pos + eps >= leftOuterLeftPerc && pos - eps <= leftOuterRightPerc) return "ESQUERDA";
        if (pos + eps >= innerLeftPerc && pos - eps <= innerRightPerc) return "MEIO";
        if (pos + eps >= rightOuterLeftPerc && pos - eps <= rightOuterRightPerc) return "DIREITA";
        return "FORA";
    }

    public float computeEps(float lineWidth) {
        return (larguraBarra > 0f) ? (lineWidth / larguraBarra) : 0f;
    }

    public float worldX(float pos) {
        return margem + larguraBarra * pos;
    }

    // Getters/Setters
    public float getAltura() { return altura; }
    public float getMargem() { return margem; }
    public float getLarguraBarra() { return larguraBarra; }
    public float getY() { return y; }

    public void setAltura(float altura) { this.altura = altura; }
    public void setMargem(float margem) { this.margem = margem; }
    public void setBorderThickness(float borderThickness) { this.borderThickness = borderThickness; }
    public void setInnerAreaWidthPerc(float innerAreaWidthPerc) { this.innerAreaWidthPerc = innerAreaWidthPerc; }
    public void setOuterAreaWidthPerc(float outerAreaWidthPerc) { this.outerAreaWidthPerc = outerAreaWidthPerc; }
    public void setAreaLeftPerc(float areaLeftPerc) { this.areaLeftPerc = areaLeftPerc; }
}
