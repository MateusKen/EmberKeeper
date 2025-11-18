package br.mackenzie.layout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class BottomBarUI extends Table {

    public BottomBarUI(Skin skin) {
        super(skin);

        // Pinta de Vermelho Escuro
        this.setBackground(skin.newDrawable("white", Color.MAROON));

        this.add(new Label("BARRA DE BAIXO", skin)).expandX().pad(10);
    }
}
