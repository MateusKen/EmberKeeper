package br.mackenzie.layout.gameAreaUI;

import br.mackenzie.layout.gameAreaUI.enums.PlayerDirection;
import br.mackenzie.utils.AssetPaths;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

public class LeftPaneUI extends Table {

    private final Image playerImage;
    private final Image fanImage;
    private final Image crowdImage;
    private final TextureRegionDrawable leftDrawable;
    private final TextureRegionDrawable rightDrawable;
    private final TextureRegionDrawable crowdDefaultDrawable;
    private final TextureRegionDrawable crowdCheeringDrawable;

    private final Texture leftTexture;
    private final Texture rightTexture;
    private final Texture bgTexture;
    private final Texture fanTexture;
    private final Texture crowdTexture;
    private final Texture crowdCheeringTexture;

    public LeftPaneUI(Skin skin) {
        super(skin);

        bgTexture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + "fundo.png"));
        leftTexture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + "bicicleta_esquerda.png"));
        rightTexture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + "bicicleta_direita.png"));
        fanTexture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + "ventilador.png"));
        crowdTexture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + "multidao.png"));
        crowdCheeringTexture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + "torcida_esquerda.png"));

        // Configura a imagem do jogador
        this.leftDrawable = new TextureRegionDrawable(leftTexture);
        this.rightDrawable = new TextureRegionDrawable(rightTexture);
        this.playerImage = new Image(leftDrawable);
        this.playerImage.setScaling(Scaling.fit);

        // Configura a imagem do ventilador
        this.fanImage = new Image(fanTexture);
        this.fanImage.setScaling(Scaling.fit);

        // Configura a imagem da multidão
        this.crowdDefaultDrawable = new TextureRegionDrawable(crowdTexture);
        this.crowdCheeringDrawable = new TextureRegionDrawable(crowdCheeringTexture);
        this.crowdImage = new Image(crowdDefaultDrawable);
        this.crowdImage.setScaling(Scaling.fit);

        // Cria a imagem de fundo
        Image bgImage = new Image(bgTexture);
        bgImage.setScaling(Scaling.fill);

        // Tabela principal para o conteúdo, construída com métodos para cada seção
        Table contentTable = new Table();
        contentTable.add(buildCrowdSection()).growX(); // Seção do topo
        contentTable.row();
        contentTable.add().expandY(); // Célula vazia para empurrar a próxima linha para baixo
        contentTable.row();
        contentTable.add(buildPlayerSection()).bottom(); // Seção de baixo

        // Stack para empilhar o fundo e o conteúdo
        Stack stack = new Stack();
        stack.add(bgImage);
        stack.add(contentTable);

        this.add(stack).grow();
    }


    private Table buildCrowdSection() {
        Table section = new Table();
        section.add(crowdImage).growX().height(250f).top().padTop(20f);
        return section;
    }

    private Table buildPlayerSection() {
        Table section = new Table();
        section.add(playerImage).size(300, 300).padBottom(-20f);
        section.add(fanImage).size(300, 300).padBottom(-60f).padLeft(40f);
        return section;
    }

    public void setPlayerDirection(PlayerDirection direction) {
        if (direction == PlayerDirection.RIGHT) {
            playerImage.setDrawable(leftDrawable);
        } else {
            playerImage.setDrawable(rightDrawable);
        }
    }

    public void showCheeringCrowd() {
        crowdImage.setDrawable(crowdCheeringDrawable);
    }

    public void showDefaultCrowd() {
        crowdImage.setDrawable(crowdDefaultDrawable);
    }

    public void dispose() {
        leftTexture.dispose();
        rightTexture.dispose();
        bgTexture.dispose();
        fanTexture.dispose();
        crowdTexture.dispose();
        crowdCheeringTexture.dispose();
    }
}
