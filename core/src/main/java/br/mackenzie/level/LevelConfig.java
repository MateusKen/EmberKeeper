// java
package br.mackenzie.level;

public interface LevelConfig {
    String getName();
    float getBpm();
    float getLinesPerBeat();      // subdivisões por batida (ex: 1 = uma linha por batida, 2 = duas por batida)
    float getSpawnRate();         // linhas por segundo (pode ser calculado a partir de BPM)
    float getVelocidadeLinha();   // velocidade das linhas (método para aplicar em RhythmLines)
    float getLineWidth();         // largura da linha (normalizada)
}
