package br.mackenzie.layout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

public class HeatBarUI extends Widget implements Disposable {

    private final Drawable coldDrawable;
    private final Drawable warmDrawable;
    private final Drawable hotDrawable;
    private final Drawable meterDrawable;

    private final Pixmap pixmap;
    private final Texture texture;

    private float currentValue = 0f; // Valor de 0 a 100
    private final float regressionRate = 2.5f; // Pontos por segundo
    private final float incrementAmount = 20.0f; // Pontos por acerto

    public HeatBarUI() {
        // Cria um Pixmap de 1x1 pixel para gerar texturas coloridas
        pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        texture = new Texture(pixmap);

        // Cria os Drawables para cada seção da barra
        coldDrawable = createColorDrawable(Color.BLUE);
        warmDrawable = createColorDrawable(Color.YELLOW);
        hotDrawable = createColorDrawable(Color.RED);
        meterDrawable = createColorDrawable(Color.WHITE);
    }

    private Drawable createColorDrawable(Color color) {
        pixmap.setColor(color);
        pixmap.fill();
        // Atualiza a textura com a nova cor do pixmap
        texture.draw(pixmap, 0, 0);
        return new TextureRegionDrawable(new Texture(pixmap));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Regride o valor continuamente
        currentValue -= regressionRate * delta;
        if (currentValue < 0) {
            currentValue = 0;
        }
    }

    public void registerHit() {
        currentValue += incrementAmount;
        if (currentValue > 100) {
            currentValue = 100;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();

        // Desenha as seções da barra com base na proporção 3:5:2
        float coldWidth = width * 0.3f;
        float warmWidth = width * 0.5f;
        float hotWidth = width * 0.2f;

        coldDrawable.draw(batch, x, y, coldWidth, height);
        warmDrawable.draw(batch, x + coldWidth, y, warmWidth, height);
        hotDrawable.draw(batch, x + coldWidth + warmWidth, y, hotWidth, height);

        // Desenha o medidor
        float meterX = x + (currentValue / 100f) * width;
        float meterWidth = 4f; // Largura do medidor
        meterDrawable.draw(batch, meterX - (meterWidth / 2), y, meterWidth, height);
    }

    @Override
    public void dispose() {
        // A textura criada a partir do pixmap precisa ser descartada
        ((TextureRegionDrawable) coldDrawable).getRegion().getTexture().dispose();
        ((TextureRegionDrawable) warmDrawable).getRegion().getTexture().dispose();
        ((TextureRegionDrawable) hotDrawable).getRegion().getTexture().dispose();
        ((TextureRegionDrawable) meterDrawable).getRegion().getTexture().dispose();
        texture.dispose();
        pixmap.dispose();
    }
}
