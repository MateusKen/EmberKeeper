package br.mackenzie.layout.gameAreaUI;

import br.mackenzie.layout.gameAreaUI.enums.GameMode;
import java.util.EnumMap;
import java.util.Map;

public class GameModeConfig {

    private static final Map<GameMode, ModeState> MODE_STATES = new EnumMap<>(GameMode.class);

    static {
        MODE_STATES.put(GameMode.FORNO, new ModeState(1));
        MODE_STATES.put(GameMode.BETERRABA, new ModeState(2));
        MODE_STATES.put(GameMode.COGUMELO, new ModeState(3));
        MODE_STATES.put(GameMode.BATATA, new ModeState(4));
        MODE_STATES.put(GameMode.FRUTA, new ModeState(5));
    }

    public static int getFrameCount(GameMode mode) {
        return MODE_STATES.getOrDefault(mode, new ModeState(1)).frameCount;
    }

    public static int getCompletions(GameMode mode) {
        return MODE_STATES.getOrDefault(mode, new ModeState(1)).getCompletions();
    }

    public static boolean isComplete(GameMode mode) {
        if (mode == GameMode.FORNO) {
            return false;
        }
        return MODE_STATES.getOrDefault(mode, new ModeState(1)).isComplete();
    }

    public static void incrementCompletion(GameMode mode) {
        if (MODE_STATES.containsKey(mode)) {
            MODE_STATES.get(mode).incrementCompletion();
        }
    }

    /**
     * Verifica se todos os modos, exceto o FORNO, estão completos.
     * @return true se todos os modos jogáveis foram concluídos, false caso contrário.
     */
    public static boolean areAllModesComplete() {
        for (GameMode mode : GameMode.values()) {
            if (mode != GameMode.FORNO && !isComplete(mode)) {
                return false; // Encontrou um modo que ainda não está completo
            }
        }
        return true; // Todos os modos estão completos
    }
}
