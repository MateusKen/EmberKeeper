package br.mackenzie.layout;

import br.mackenzie.layout.gameAreaUI.RhythmManager;
import br.mackenzie.layout.gameAreaUI.RhythmNote;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import java.util.Queue; // <-- Mude o import aqui

public class RhythmBarUI extends Widget {

    private final Texture barTexture;
    private final Texture noteTexture;
    private final Texture firstNoteTexture;
    private final RhythmManager rhythmManager;

    private final Texture goodZoneTexture;
    private final Texture perfectZoneTexture;

    public RhythmBarUI(RhythmManager rhythmManager) {
        super();
        this.rhythmManager = rhythmManager;

        barTexture = createColoredTexture(1, 1, Color.DARK_GRAY);
        noteTexture = createColoredTexture(1, 1, Color.WHITE);
        firstNoteTexture = createColoredTexture(1, 1, Color.YELLOW);

        goodZoneTexture = createColoredTexture(1, 1, Color.CYAN);
        perfectZoneTexture = createColoredTexture(1, 1, Color.GREEN);
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

        float goodZoneWidth = 200;
        float perfectZoneWidth = 80;
        float goodZoneX = getX() + (getWidth() - goodZoneWidth) / 2;
        float perfectZoneX = getX() + (getWidth() - perfectZoneWidth) / 2;

        batch.setColor(1, 1, 1, 0.5f);
        batch.draw(goodZoneTexture, goodZoneX, getY(), goodZoneWidth, getHeight());

        batch.setColor(1, 1, 1, 0.7f);
        batch.draw(perfectZoneTexture, perfectZoneX, getY(), perfectZoneWidth, getHeight());
        batch.setColor(Color.WHITE);

        Queue<RhythmNote> notes = rhythmManager.getActiveNotes();
        float travelDistance = getWidth();
        float noteWidth = 20f;

        for (RhythmNote note : notes) {
            // A verificação 'if (note.isActive)' foi removida
            float timeUntilHit = note.targetTime - rhythmManager.getSongTime();
            float progress = 1.0f - (timeUntilHit / RhythmManager.getSecondsToTravel());
            float noteX = getX() + (progress * travelDistance);

            // A verificação 'note.isFirstNoteOfBeat' agora funciona
            Texture currentNoteTexture = note.isFirstNoteOfBeat ? firstNoteTexture : noteTexture;
            batch.draw(currentNoteTexture, noteX - noteWidth / 2f, getY(), noteWidth, getHeight());
        }
    }

    public void dispose() {
        barTexture.dispose();
        noteTexture.dispose();
        firstNoteTexture.dispose();
        goodZoneTexture.dispose();
        perfectZoneTexture.dispose();
    }
}
