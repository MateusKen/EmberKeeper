// java
package br.mackenzie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;

public class RhythmLines {
    private float velocidadeLinha = 2f;

    // Modo por espaçamento (antigo)
    private float spawnSpacing = 2.0f;     // distância normalizada entre linhas
    private float spawnInterval = 0f;      // derivado: spacing/velocidade

    // Modo por frequência (novo)
    private boolean useSpawnRate = false;
    private float spawnRate = 0f;          // linhas por segundo
    private float spawnPeriod = 0f;        // 1/spawnRate

    private float spawnAccumulator = 0f;
    private float lineWidth = 0.05f;

    private final List<Float> linhas = new ArrayList<>();
    // substitui o índice instável por uma posição rastreada
    private float trackedPos = -1f;
    private Color trackedColor = Color.RED;
    private boolean debug = false;

    private boolean initialSpawnDone = false;

    public void setDebug(boolean debug) { this.debug = debug; }
    public void setTrackedColor(Color trackedColor) { this.trackedColor = (trackedColor != null) ? trackedColor : Color.RED; }

    public void update(float dt, RhythmBarAreas bar) {
        if (bar.getLarguraBarra() <= 0f) return;

        // Spawn inicial visível e selecionado
        if (!initialSpawnDone) {
            spawnInitialVisible(bar);
            selectMostLeft();
            initialSpawnDone = true;
        }

        // Spawning: por taxa (preferencial) ou por espaçamento
        if (useSpawnRate && spawnRate > 0f) {
            spawnAccumulator += dt;
            while (spawnAccumulator >= spawnPeriod) {
                spawnLinha(bar);
                spawnAccumulator -= spawnPeriod;
            }
        } else {
            if (spawnInterval <= 0f && velocidadeLinha > 0f) {
                spawnInterval = spawnSpacing / velocidadeLinha;
                if (spawnInterval <= 0f) spawnInterval = 0.1f;
            }
            spawnAccumulator += dt;
            while (spawnInterval > 0f && spawnAccumulator >= spawnInterval) {
                spawnLinha(bar);
                spawnAccumulator -= spawnInterval;
            }
        }

        // Movimento e remoção offscreen
        float deltaNormalized = (velocidadeLinha * dt) / bar.getLarguraBarra();
        float offscreen = 0f - (lineWidth / Math.max(1e-6f, bar.getLarguraBarra()));

        for (int i = 0; i < linhas.size(); ) {
            float pos = linhas.get(i);
            float newPos = pos - deltaNormalized;

            if (newPos < offscreen) {
                linhas.remove(i);
            } else {
                linhas.set(i, newPos);
                i++;
            }
        }

        // Atualiza trackedPos para acompanhar a linha rastreada; se ficar offscreen,
        // tenta apontar para a primeira linha visível (a mais à esquerda dentro da tela).
        // Se não houver nenhuma, reseta para -1f.
        if (trackedPos >= 0f) {
            trackedPos -= deltaNormalized;
            if (trackedPos < offscreen) {
                int firstVisible = findFirstVisibleIndex(offscreen);
                if (firstVisible != -1) {
                    trackedPos = linhas.get(firstVisible);
                } else {
                    trackedPos = -1f;
                }
            }
        }
    }

    public void draw(ShapeRenderer shape, RhythmBarAreas bar, Viewport viewport) {
        if (debug) {
            float eps = bar.computeEps(lineWidth);
            for (int i = 0; i < linhas.size(); i++) {
                float pos = linhas.get(i);
                float worldX = bar.worldX(pos);
                String area = bar.resolveAreaPortuguese(pos, eps);
                System.out.printf("DEBUG: linha #%d pos=%.4f x=%.2f área=%s%n", i, pos, worldX, area);
            }
        }

        int currentTrackedIdx = findNearestIndexForTrackedPos();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < linhas.size(); i++) {
            float pos = linhas.get(i);
            float x = bar.worldX(pos);
            boolean isTracked = (i == currentTrackedIdx);

            if (isTracked) {
                shape.setColor(trackedColor);
                float trackedWidth = lineWidth * 1.6f;
                shape.rect(x, bar.getY(), trackedWidth, bar.getAltura());

                float triW = Math.max(trackedWidth * 1.2f, 0.06f);
                float triH = Math.max(bar.getAltura() * 0.35f, 0.04f);
                float triCenterX = x + trackedWidth * 0.5f;
                float triTopY = bar.getY() + bar.getAltura() + triH + 0.01f;

                shape.setColor(Color.CYAN);
                shape.triangle(
                    triCenterX, triTopY,
                    triCenterX - triW * 0.5f, triTopY - triH,
                    triCenterX + triW * 0.5f, triTopY - triH
                );
            } else {
                shape.setColor(Color.WHITE);
                shape.rect(x, bar.getY(), lineWidth, bar.getAltura());
            }
        }
        shape.end();
    }

    public void spawnLinha(RhythmBarAreas bar) {
        float startOffset = (bar.getLarguraBarra() > 0f) ? (lineWidth / bar.getLarguraBarra()) : 0.02f;
        linhas.add(1f + startOffset); // nasce fora da barra pela direita
    }

    private void spawnInitialVisible(RhythmBarAreas bar) {
        float eps = bar.computeEps(lineWidth);
        float margin = Math.max(0.02f, eps + 0.01f);
        float pos = Math.max(0f, Math.min(1f - margin, 0.9f));
        linhas.add(pos);
    }

    public void selectMostLeft() {
        if (linhas.isEmpty()) { trackedPos = -1f; return; }
        float minPos = Float.MAX_VALUE;
        for (float p : linhas) {
            if (p < minPos) minPos = p;
        }
        trackedPos = minPos;
    }

    public void moveTrackedToPrevious() {
        if (linhas.isEmpty()) { trackedPos = -1f; return; }
        int curIdx = findNearestIndexForTrackedPos();
        if (curIdx == -1) { selectMostLeft(); return; }
        float currentPos = linhas.get(curIdx);
        int prevIdx = findNearestLeftIndex(currentPos);
        if (prevIdx == -1) prevIdx = findNearestRightIndex(currentPos);
        if (prevIdx != -1) trackedPos = linhas.get(prevIdx);
    }

    public void moveTrackedToNext() {
        if (linhas.isEmpty()) { trackedPos = -1f; return; }
        int curIdx = findNearestIndexForTrackedPos();
        if (curIdx == -1) { selectMostLeft(); return; }
        float currentPos = linhas.get(curIdx);
        int nextIdx = findNearestRightIndex(currentPos);
        if (nextIdx == -1) nextIdx = findNearestLeftIndex(currentPos);
        if (nextIdx != -1) trackedPos = linhas.get(nextIdx);
    }

    private int findNearestLeftIndex(float pos) {
        int best = -1;
        float bestPos = -Float.MAX_VALUE;
        for (int i = 0; i < linhas.size(); i++) {
            float p = linhas.get(i);
            if (p < pos && p > bestPos) {
                bestPos = p;
                best = i;
            }
        }
        return best;
    }

    private int findNearestRightIndex(float pos) {
        int best = -1;
        float bestPos = Float.MAX_VALUE;
        for (int i = 0; i < linhas.size(); i++) {
            float p = linhas.get(i);
            if (p > pos && p < bestPos) {
                bestPos = p;
                best = i;
            }
        }
        return best;
    }

    // encontra o índice mais próximo da trackedPos (ou -1 se não houver linhas)
    private int findNearestIndexForTrackedPos() {
        if (trackedPos < 0f || linhas.isEmpty()) return -1;
        int bestIdx = -1;
        float bestDist = Float.MAX_VALUE;
        for (int i = 0; i < linhas.size(); i++) {
            float d = Math.abs(linhas.get(i) - trackedPos);
            if (d < bestDist) {
                bestDist = d;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    // retorna a primeira linha que está visível (a mais à esquerda dentro do limite offscreen)
    private int findFirstVisibleIndex(float offscreenThreshold) {
        int best = -1;
        float bestPos = Float.MAX_VALUE;
        for (int i = 0; i < linhas.size(); i++) {
            float p = linhas.get(i);
            if (p >= offscreenThreshold && p < bestPos) {
                bestPos = p;
                best = i;
            }
        }
        return best;
    }

    public void onPlayerClick(Viewport viewport) {
        moveTrackedToPrevious();
    }

    public float getTrackedLinePos() {
        int idx = findNearestIndexForTrackedPos();
        if (idx >= 0 && idx < linhas.size()) return linhas.get(idx);
        return -1f;
    }

    public boolean isTrackedLineOverlappingAreas(RhythmBarAreas bar) {
        int idx = findNearestIndexForTrackedPos();
        if (idx < 0 || idx >= linhas.size()) return false;
        if (bar.getLarguraBarra() <= 0f) return false;
        float eps = bar.computeEps(lineWidth);
        float pos = linhas.get(idx);
        String area = bar.resolveAreaPortuguese(pos, eps);
        return !"FORA".equals(area);
    }

    public void printLinesAreas(RhythmBarAreas bar) {
        if (bar.getLarguraBarra() <= 0f) {
            System.out.println("DEBUG: largura da barra inválida");
            return;
        }
        float eps = bar.computeEps(lineWidth);
        int trackedIdx = findNearestIndexForTrackedPos();
        for (int i = 0; i < linhas.size(); i++) {
            float pos = linhas.get(i);
            String area = bar.resolveAreaPortuguese(pos, eps);
            String trackedMark = (i == trackedIdx) ? " (TRACKED)" : "";
            System.out.printf("TECLA: linha #%d pos=%.4f área=%s%s%n", i, pos, area, trackedMark);
        }
    }

    public String getTrackedAreaPortuguese(RhythmBarAreas bar) {
        int idx = findNearestIndexForTrackedPos();
        if (idx < 0 || idx >= linhas.size()) return "FORA";
        if (bar.getLarguraBarra() <= 0f) return "FORA";
        float eps = bar.computeEps(lineWidth);
        float pos = linhas.get(idx);
        return bar.resolveAreaPortuguese(pos, eps);
    }

    public void setVelocidadeLinha(float velocidadeLinha) {
        this.velocidadeLinha = velocidadeLinha;
        // Recalcula intervalo apenas quando em modo espaçamento
        if (!useSpawnRate) {
            this.spawnInterval = (velocidadeLinha > 0f) ? (spawnSpacing / velocidadeLinha) : 0f;
        }
    }

    // Ativa modo por espaçamento e desativa modo por taxa
    public void setSpawnSpacing(float spawnSpacing) {
        this.spawnSpacing = spawnSpacing;
        this.useSpawnRate = false;
        this.spawnRate = 0f;
        this.spawnPeriod = 0f;
        this.spawnAccumulator = 0f;
        this.spawnInterval = (velocidadeLinha > 0f) ? (spawnSpacing / velocidadeLinha) : 0f;
    }

    // Ativa modo por taxa (linhas/segundo) e desativa modo por espaçamento
    public void setSpawnRate(float linesPerSecond) {
        if (linesPerSecond > 0f) {
            this.useSpawnRate = true;
            this.spawnRate = linesPerSecond;
            this.spawnPeriod = 1f / linesPerSecond;
            this.spawnAccumulator = 0f;
        } else {
            // Desliga taxa -> sem spawn até configurar spacing ou taxa > 0
            this.useSpawnRate = true;
            this.spawnRate = 0f;
            this.spawnPeriod = 0f;
            this.spawnAccumulator = 0f;
        }
    }

    public void setLineWidth(float lineWidth) { this.lineWidth = lineWidth; }

    public void reset() {
        linhas.clear();
        spawnAccumulator = 0f;
        trackedPos = -1f;
        initialSpawnDone = false;
    }
}
