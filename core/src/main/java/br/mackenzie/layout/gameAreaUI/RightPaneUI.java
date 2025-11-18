package br.mackenzie.layout.gameAreaUI;

import br.mackenzie.layout.gameAreaUI.enums.GameMode;
import br.mackenzie.utils.AssetPaths;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

public class RightPaneUI extends Table {

    private final Texture bgTexture;
    private Texture fireTexture;
    private Texture itemIconTexture;
    private Texture checkTexture; // Nova textura para o ícone de "check"

    private Array<Texture> itemAnimationTextures;
    private Array<Drawable> itemAnimationDrawables;
    private Image itemImage;
    private final int completionCount;

    public RightPaneUI(Skin skin, GameMode gameMode, int frameCount, int completionCount) {
        super(skin);
        this.completionCount = completionCount;

        bgTexture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + "fundo.png"));
        Image bgImage = new Image(bgTexture);
        bgImage.setScaling(Scaling.fill);

        Table contentTable;

        if (gameMode == GameMode.FORNO) {
            contentTable = buildFornoContent();
        } else {
            contentTable = buildItemContent(gameMode, frameCount);
        }

        Stack stack = new Stack();
        stack.add(bgImage);
        stack.add(contentTable);

        this.add(stack).grow();
    }

    private Table buildFornoContent() {
        fireTexture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + "fogo_normal1.png"));
        Image fireImage = new Image(fireTexture);
        fireImage.setScaling(Scaling.fit);

        Table contentTable = new Table();
        contentTable.add(fireImage).size(500, 500).expand().center();
        return contentTable;
    }

    private Table buildItemContent(GameMode gameMode, int frameCount) {
        String assetName = gameMode.name().toLowerCase();

        itemAnimationTextures = new Array<>();
        itemAnimationDrawables = new Array<>();

        itemIconTexture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + assetName + ".png"));
        checkTexture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + "check.png")); // Carrega a imagem de check

        for (int i = 1; i <= frameCount; i++) {
            Texture texture = new Texture(Gdx.files.internal(AssetPaths.IMAGES_PATH + assetName + i + ".png"));
            itemAnimationTextures.add(texture);
            itemAnimationDrawables.add(new TextureRegionDrawable(texture));
        }

        itemImage = new Image(itemAnimationDrawables.first());
        itemImage.setScaling(Scaling.fit);

        Table contentTable = new Table();
        contentTable.add(buildTopItemSection()).growX().top();
        contentTable.row();
        contentTable.add(itemImage).size(400, 400).expand().center();

        return contentTable;
    }

    private Table buildTopItemSection() {
        Table topSection = new Table();
        Drawable itemDrawable = new TextureRegionDrawable(itemIconTexture);
        Drawable checkDrawable = new TextureRegionDrawable(checkTexture);

        for (int i = 0; i < 3; i++) {
            Image icon = (i < completionCount) ? new Image(checkDrawable) : new Image(itemDrawable);
            topSection.add(icon).size(80, 80).pad(10);
        }

        return topSection;
    }

    public void updateItemAnimation(int frameIndex) {
        if (itemImage == null || itemAnimationDrawables == null || itemAnimationDrawables.isEmpty()) return;

        if (frameIndex >= 0 && frameIndex < itemAnimationDrawables.size) {
            itemImage.setDrawable(itemAnimationDrawables.get(frameIndex));
        }
    }

    public void dispose() {
        bgTexture.dispose();
        if (fireTexture != null) fireTexture.dispose();
        if (itemIconTexture != null) itemIconTexture.dispose();
        if (checkTexture != null) checkTexture.dispose();

        if (itemAnimationTextures != null) {
            for (Texture texture : itemAnimationTextures) {
                texture.dispose();
            }
        }
    }
}
