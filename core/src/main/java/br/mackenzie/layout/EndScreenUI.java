package br.mackenzie.layout;

import br.mackenzie.utils.AssetPaths;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class EndScreenUI extends Table {

    private final Texture congratsTexture;

    public EndScreenUI(Skin skin) {
        super(skin);

        // Carrega a imagem de parabenização
        congratsTexture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + "fim.png"));
        Image congratsImage = new Image(congratsTexture);
        congratsImage.setScaling(Scaling.fit);

        Label messageLabel = new Label("Parabens, voce completou o jogo!", getSkin());
        messageLabel.setAlignment(Align.center);
        messageLabel.setFontScale(1.5f);

        // Monta o layout da tela final
        this.add(messageLabel).padBottom(40);
        this.row();
        this.add(congratsImage).size(600, 400);
    }

    public void dispose() {
        congratsTexture.dispose();
    }
}
