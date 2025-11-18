package br.mackenzie.layout;

import br.mackenzie.layout.gameAreaUI.RhythmManager;
import br.mackenzie.layout.gameAreaUI.RhythmNote;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Queue;

public class RhythmBarUI extends Widget {

    private final Texture barTexture;
    private final Texture noteTexture;
    private final Texture firstNoteTexture;
    private final RhythmManager rhythmManager;

    // --- Texturas da zona de acerto modificadas ---
    private final Texture goodZoneTexture;
    private final Texture perfectZoneTexture;
    // --- Fim da modificação ---

    public RhythmBarUI(RhythmManager rhythmManager) {
        super();
        this.rhythmManager = rhythmManager;

        barTexture = createColoredTexture(1, 1, Color.DARK_GRAY);
        noteTexture = createColoredTexture(1, 1, Color.WHITE);
        firstNoteTexture = createColoredTexture(1, 1, Color.YELLOW);

        // --- Inicialização das novas texturas ---
        goodZoneTexture = createColoredTexture(1, 1, Color.CYAN);    // <-- Cor para a zona "Boa"
        perfectZoneTexture = createColoredTexture(1, 1, Color.GREEN); // <-- Cor para a zona "Perfeita"
        // --- Fim da modificação ---
    }

    private Texture createColoredTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw(barTexture, getX(), getY(), getWidth(), getHeight());

        // --- Lógica de desenho da zona de acerto modificada ---
        float goodZoneWidth = 200;
        float perfectZoneWidth = 80;
        float goodZoneX = getX() + (getWidth() - goodZoneWidth) / 2;
        float perfectZoneX = getX() + (getWidth() - perfectZoneWidth) / 2;

        // Desenha a zona "Boa" (mais larga) com cor ciano e transparência
        batch.setColor(1, 1, 1, 0.5f);
        batch.draw(goodZoneTexture, goodZoneX, getY(), goodZoneWidth, getHeight());

        // Desenha a zona "Perfeita" (mais estreita) por cima com cor verde e mais opaca
        batch.setColor(1, 1, 1, 0.7f);
        batch.draw(perfectZoneTexture, perfectZoneX, getY(), perfectZoneWidth, getHeight());
        batch.setColor(Color.WHITE); // Reseta a cor para o desenho das notas
        // --- Fim da modificação ---

        Queue<RhythmNote> notes = rhythmManager.getActiveNotes();
        float travelDistance = getWidth();
        float noteWidth = 20f;

        for (RhythmNote note : notes) {
            if (note.isActive) {
                float timeUntilHit = note.targetTime - rhythmManager.getSongTime();
                float progress = 1.0f - (timeUntilHit / RhythmManager.getSecondsToTravel());
                float noteX = getX() + (progress * travelDistance);

                Texture currentNoteTexture = note.isFirstNoteOfBeat ? firstNoteTexture : noteTexture;
                batch.draw(currentNoteTexture, noteX - noteWidth / 2f, getY(), noteWidth, getHeight());
            }
        }
    }

    public void dispose() {
        barTexture.dispose();
        noteTexture.dispose();
        firstNoteTexture.dispose();
        // --- Libera as novas texturas ---
        goodZoneTexture.dispose();
        perfectZoneTexture.dispose();
        // --- Fim da modificação ---
    }
}
