package br.mackenzie.layout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TopBarUI extends Table {

    public TopBarUI(Skin skin) {
        super(skin); // Passa o skin para o construtor da Table

        this.setBackground(skin.newDrawable("white", Color.GRAY));
        // -----------------------------

        this.add(new Label("BARRA DE CIMA", skin)).expandX().pad(10);

    }
}
