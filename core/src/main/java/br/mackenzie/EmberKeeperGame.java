package br.mackenzie;

import br.mackenzie.layout.StartScreen;
import com.badlogic.gdx.Game;

public class EmberKeeperGame extends Game {

    @Override
    public void create() {
        // Define a tela inicial
        setScreen(new StartScreen(this));
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        super.dispose();
    }
}
