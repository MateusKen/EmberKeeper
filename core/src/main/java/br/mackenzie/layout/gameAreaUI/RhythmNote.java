package br.mackenzie.layout.gameAreaUI;

import br.mackenzie.layout.gameAreaUI.enums.PlayerDirection;

public class RhythmNote {
    public final float targetTime;
    public boolean isActive = true;
    public final PlayerDirection requiredDirection;
    public final boolean isFirstNoteOfBeat; // <-- Nova propriedade

    public RhythmNote(float targetTime, PlayerDirection requiredDirection, boolean isFirstNoteOfBeat) {
        this.targetTime = targetTime;
        this.requiredDirection = requiredDirection;
        this.isFirstNoteOfBeat = isFirstNoteOfBeat; // <-- Atribuição no construtor
    }
}
