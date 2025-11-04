// java
package br.mackenzie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RhythmBar {

    private final RhythmBarAreas barAreas = new RhythmBarAreas();
    private final RhythmLines rhythmLines = new RhythmLines();

    private Viewport lastViewport;
    private float lineWidth = 0.05f;

    // Posição configurável (margem horizontal e coordenada Y)
    private float posMargem = 0.3f;
    private float posY = 0.3f;

    public void setPosition(float margem, float y) {
        this.posMargem = margem;
        this.posY = y;
    }

    public void update(float dt) {
        if (lastViewport == null) return;
        barAreas.updateLayout(lastViewport, posMargem, posY);
        rhythmLines.update(dt, barAreas);
    }

    public void update(float dt, Viewport viewport) {
        this.lastViewport = viewport;
        barAreas.updateLayout(viewport, posMargem, posY);
        rhythmLines.update(dt, barAreas);
    }

    public void draw(ShapeRenderer shape, Viewport viewport) {
        this.lastViewport = viewport;
        barAreas.updateLayout(viewport, posMargem, posY);
        // Agora a RhythmBar é responsável por desenhar a barra (via barAreas) e as linhas
        barAreas.draw(shape, viewport);
        rhythmLines.draw(shape, barAreas, viewport);
    }

    public void spawnLinha() { rhythmLines.spawnLinha(barAreas); }
    public void selectMostLeft() { rhythmLines.selectMostLeft(); }
    public void moveTrackedToPrevious() { rhythmLines.moveTrackedToPrevious(); }
    public void moveTrackedToNext() { rhythmLines.moveTrackedToNext(); }

    public void onPlayerClick(Viewport viewport) {
        this.lastViewport = viewport;
        barAreas.updateLayout(viewport, posMargem, posY);

        float pos = rhythmLines.getTrackedLinePos();
        if (pos < 0f) { rhythmLines.selectMostLeft(); return; }

        float eps = barAreas.computeEps(lineWidth);
        String area = barAreas.resolveAreaPortuguese(pos, eps);

        if ("DIREITA".equals(area)) {
            rhythmLines.moveTrackedToNext();
        } else {
            rhythmLines.moveTrackedToPrevious();
        }
    }

    public float getTrackedLinePos() { return rhythmLines.getTrackedLinePos(); }

    public boolean isTrackedLineOverlappingAreas(Viewport viewport) {
        this.lastViewport = viewport;
        barAreas.updateLayout(viewport, posMargem, posY);
        return rhythmLines.isTrackedLineOverlappingAreas(barAreas);
    }

    public void printLinesAreas(Viewport viewport) {
        this.lastViewport = viewport;
        barAreas.updateLayout(viewport, posMargem, posY);
        rhythmLines.printLinesAreas(barAreas);
    }

    public String getTrackedAreaPortuguese(Viewport viewport) {
        this.lastViewport = viewport;
        barAreas.updateLayout(viewport, posMargem, posY);
        if (rhythmLines.getTrackedLinePos() < 0f) return "FORA";
        float eps = barAreas.computeEps(lineWidth);
        return barAreas.resolveAreaPortuguese(rhythmLines.getTrackedLinePos(), eps);
    }

    public void setTrackedColor(Color color) { rhythmLines.setTrackedColor(color); }
    public void setDebug(boolean debug) { rhythmLines.setDebug(debug); }

    public void setAltura(float altura) { barAreas.setAltura(altura); }
    public void setMargem(float margem) { this.posMargem = margem; barAreas.setMargem(margem); }
    public void setBorderThickness(float borderThickness) { barAreas.setBorderThickness(borderThickness); }
    public void setInnerAreaWidthPerc(float innerAreaWidthPerc) { barAreas.setInnerAreaWidthPerc(innerAreaWidthPerc); }
    public void setOuterAreaWidthPerc(float outerAreaWidthPerc) { barAreas.setOuterAreaWidthPerc(outerAreaWidthPerc); }
    public void setAreaLeftPerc(float areaLeftPerc) { barAreas.setAreaLeftPerc(areaLeftPerc); }

    public void setVelocidadeLinha(float velocidadeLinha) { rhythmLines.setVelocidadeLinha(velocidadeLinha); }
    public void setSpawnSpacing(float spawnSpacing) { rhythmLines.setSpawnSpacing(spawnSpacing); }
    public void setSpawnRate(float linesPerSecond) { rhythmLines.setSpawnRate(linesPerSecond); }
    public void setLineWidth(float lineWidth) { this.lineWidth = lineWidth; rhythmLines.setLineWidth(lineWidth); }

    public void reset() { rhythmLines.reset(); }

    public RhythmBarAreas getBarAreas() { return barAreas; }
    public RhythmLines getRhythmLines() { return rhythmLines; }
}
