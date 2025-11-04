// java
package br.mackenzie.level;

import br.mackenzie.RhythmBar;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    private final List<LevelConfig> levels = new ArrayList<>();
    private int current = -1;

    public void addLevel(LevelConfig cfg) { levels.add(cfg); if (current == -1) current = 0; }
    public LevelConfig getCurrent() { return (current >= 0 && current < levels.size()) ? levels.get(current) : null; }
    public void next() { if (!levels.isEmpty()) current = (current + 1) % levels.size(); }
    public void previous() { if (!levels.isEmpty()) current = (current - 1 + levels.size()) % levels.size(); }

    // Aplica parâmetros da fase atual em RhythmBar
    public void applyTo(RhythmBar bar) {
        LevelConfig cfg = getCurrent();
        if (cfg == null) return;
        bar.setSpawnRate(cfg.getSpawnRate());
        bar.setVelocidadeLinha(cfg.getVelocidadeLinha());
        bar.setLineWidth(cfg.getLineWidth());
    }
}
