package br.mackenzie.layout.gameAreaUI;

import br.mackenzie.layout.gameAreaUI.enums.GameMode;
import br.mackenzie.layout.gameAreaUI.enums.HitResult;
import br.mackenzie.layout.gameAreaUI.enums.PlayerDirection;
import com.badlogic.gdx.utils.Queue;

import java.util.EnumMap;
import java.util.Map;

public class RhythmManager {

    // Configurações de Ritmo
    private static final float BPM = 90.0f;
    private static final float BEAT_INTERVAL = 60.0f / BPM;
    private static final float SECONDS_TO_TRAVEL = 2.0f;

    // Janelas de tempo para julgamento (em segundos)
    private static final float PERFECT_WINDOW = 0.08f;
    private static final float GOOD_WINDOW = 0.16f;

    private float songTime = 0;
    private float nextNoteTime = 0;
    private int divisions = 1;
    private PlayerDirection nextDirection = PlayerDirection.LEFT;
    private GameMode currentMode;
    private int noteInBeatCounter = 0;

    private final Queue<RhythmNote> notes = new Queue<>();

    private static final Map<GameMode, Integer> MODE_DIVISIONS = new EnumMap<>(GameMode.class);

    static {
        MODE_DIVISIONS.put(GameMode.FORNO, 1);
        MODE_DIVISIONS.put(GameMode.BETERRABA, 2);
        MODE_DIVISIONS.put(GameMode.COGUMELO, 3);
        MODE_DIVISIONS.put(GameMode.BATATA, 4);
        // A linha para GameMode.FRUTA foi removida para corresponder aos modos 1, 2, 3 e 4.
    }

    public void update(float delta) {
        songTime += delta;

        while (songTime >= nextNoteTime) {
            float targetTime = nextNoteTime + SECONDS_TO_TRAVEL;

            boolean isFirstOfBeat = (noteInBeatCounter == 0);
            notes.addLast(new RhythmNote(targetTime, nextDirection, isFirstOfBeat));

            noteInBeatCounter = (noteInBeatCounter + 1) % divisions;

            nextDirection = (nextDirection == PlayerDirection.LEFT) ? PlayerDirection.RIGHT : PlayerDirection.LEFT;

            float noteInterval;
            if (currentMode == GameMode.FORNO) {
                noteInterval = BEAT_INTERVAL * 4;
            } else {
                noteInterval = BEAT_INTERVAL / divisions;
            }
            nextNoteTime += noteInterval;
        }

        while (notes.size > 0 && notes.first().targetTime < songTime - GOOD_WINDOW) {
            notes.removeFirst().isActive = false;
        }
    }

    public HitResult checkHit(PlayerDirection hitDirection) {
        if (notes.size == 0) {
            return HitResult.NONE;
        }

        RhythmNote note = notes.first();

        if (note.requiredDirection != hitDirection) {
            return HitResult.MISS;
        }

        float timeDifference = Math.abs(songTime - note.targetTime);

        if (timeDifference <= PERFECT_WINDOW) {
            notes.removeFirst().isActive = false;
            return HitResult.PERFECT;
        } else if (timeDifference <= GOOD_WINDOW) {
            notes.removeFirst().isActive = false;
            return HitResult.GOOD;
        }

        return HitResult.NONE;
    }

    public void setMode(GameMode mode) {
        this.currentMode = mode;
        this.divisions = MODE_DIVISIONS.getOrDefault(mode, 1);
        this.songTime = 0;
        this.nextNoteTime = 0;
        this.notes.clear();
        this.noteInBeatCounter = 0;
    }

    public Queue<RhythmNote> getActiveNotes() {
        return notes;
    }

    public float getSongTime() {
        return songTime;
    }

    public static float getSecondsToTravel() {
        return SECONDS_TO_TRAVEL;
    }
}
