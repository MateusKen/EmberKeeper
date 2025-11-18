package br.mackenzie.layout.gameAreaUI;

import br.mackenzie.layout.gameAreaUI.enums.GameMode;
import br.mackenzie.layout.gameAreaUI.enums.HitResult;
import br.mackenzie.layout.gameAreaUI.enums.PlayerDirection;
import com.badlogic.gdx.Gdx;
import java.util.LinkedList;
import java.util.Queue;

public class RhythmManager {

    // --- Variáveis de Ritmo e Tempo ---
    private float bpm = 70.0f;
    private float secondsPerBeat;
    private float songTime = 0f;
    private float rhythmBarWidth;

    // --- Lógica das Notas ---
    private final Queue<RhythmNote> activeNotes = new LinkedList<>();
    private GameMode currentMode;
    private float nextNoteSpawnTime = 0f;
    private int noteIndex = 0;

    // --- Janelas de Acerto (em segundos) ---
    public static final float PERFECT_WINDOW = 0.1f;
    public static final float GOOD_WINDOW = 0.2f;

    // --- Tempo de Viagem da Nota ---
    private static final float SECONDS_TO_TRAVEL = 2.0f;

    public RhythmManager(float rhythmBarWidth) {
        this.rhythmBarWidth = rhythmBarWidth;
        setupRhythm();
    }

    private void setupRhythm() {
        secondsPerBeat = 60.0f / bpm;
    }

    public void update(float delta) {
        songTime += delta;

        while (!activeNotes.isEmpty() && (songTime > activeNotes.peek().targetTime + GOOD_WINDOW)) {
            activeNotes.poll();
            Gdx.app.log("RhythmManager", "Note missed!");
        }

        if (songTime >= nextNoteSpawnTime && currentMode != null) {
            spawnNote();
            nextNoteSpawnTime += secondsPerBeat;
        }
    }

    private void spawnNote() {
        PlayerDirection direction = (noteIndex % 2 == 0) ? PlayerDirection.LEFT : PlayerDirection.RIGHT;
        float target = songTime + SECONDS_TO_TRAVEL;
        // Define a primeira nota do compasso (assumindo 4/4)
        boolean isFirst = (noteIndex % 4 == 0);
        activeNotes.add(new RhythmNote(target, direction, isFirst)); // Passa o novo valor
        noteIndex++;
    }

    public HitResult checkHit(PlayerDirection hitDirection) {
        if (activeNotes.isEmpty()) {
            return HitResult.MISS;
        }

        RhythmNote nextNote = activeNotes.peek();
        float timeDifference = Math.abs(songTime - nextNote.targetTime);

        if (nextNote.direction != hitDirection) {
            return HitResult.MISS;
        }

        if (timeDifference <= PERFECT_WINDOW) {
            activeNotes.poll();
            return HitResult.PERFECT;
        } else if (timeDifference <= GOOD_WINDOW) {
            activeNotes.poll();
            return HitResult.GOOD;
        }

        return HitResult.MISS;
    }

    public void setMode(GameMode newMode) {
        this.currentMode = newMode;
        activeNotes.clear();
        songTime = 0;
        noteIndex = 0;
        nextNoteSpawnTime = SECONDS_TO_TRAVEL;
        Gdx.app.log("RhythmManager", "Mode changed to: " + newMode.name());
    }

    public Queue<RhythmNote> getActiveNotes() {
        return activeNotes;
    }

    public float getSongTime() {
        return songTime;
    }

    public static float getSecondsToTravel() {
        return SECONDS_TO_TRAVEL;
    }
}
