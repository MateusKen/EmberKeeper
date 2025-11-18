package br.mackenzie.layout.gameAreaUI;

import br.mackenzie.layout.gameAreaUI.enums.PlayerDirection;

public class RhythmNote {
    public final float targetTime;
    public final PlayerDirection direction;
    public final boolean isFirstNoteOfBeat; // Campo adicionado

    public RhythmNote(float targetTime, PlayerDirection direction, boolean isFirstNoteOfBeat) {
        this.targetTime = targetTime;
        this.direction = direction;
        this.isFirstNoteOfBeat = isFirstNoteOfBeat; // Construtor atualizado
    }
}
