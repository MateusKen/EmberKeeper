// java
package br.mackenzie.level;

/**
 * Configuração de fase baseada em BPM.
 * spawnRateOverride <= 0 significa usar cálculo a partir do BPM: bpm/60 * linesPerBeat
 */
public class BpmLevelConfig implements LevelConfig {
    private final String name;
    private final float bpm;
    private final float linesPerBeat;
    private final float velocidadeLinha;
    private final float lineWidth;
    private final float spawnRateOverride;

    public BpmLevelConfig(String name, float bpm, float linesPerBeat,
                          float velocidadeLinha, float lineWidth, float spawnRateOverride) {
        this.name = name;
        this.bpm = bpm;
        this.linesPerBeat = Math.max(0.0001f, linesPerBeat);
        this.velocidadeLinha = velocidadeLinha;
        this.lineWidth = lineWidth;
        this.spawnRateOverride = spawnRateOverride;
    }

    public BpmLevelConfig(String name, float bpm, float linesPerBeat,
                          float velocidadeLinha, float lineWidth) {
        this(name, bpm, linesPerBeat, velocidadeLinha, lineWidth, -1f);
    }

    @Override public String getName() { return name; }
    @Override public float getBpm() { return bpm; }
    @Override public float getLinesPerBeat() { return linesPerBeat; }

    @Override
    public float getSpawnRate() {
        if (spawnRateOverride > 0f) return spawnRateOverride;
        return (bpm / 60f) * linesPerBeat;
    }

    @Override public float getVelocidadeLinha() { return velocidadeLinha; }
    @Override public float getLineWidth() { return lineWidth; }
}
