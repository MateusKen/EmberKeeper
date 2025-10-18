package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    private float marcadorPos = 0f;
    private float velocidade = 0.2f;
    private int ultimaTecla = 0;
    private float decaimento = 0.05f;

    private float barraOffset = 0f; // posição horizontal da barra
    private float barraVelocidade = 2f; // unidades por segundo

    private ShapeRenderer shape;

    private Texture fundo;
    private Texture marcadorImg;
    private Texture marcadorAzuladoImg;
    private Texture marcadorForteImg;

    private SpriteBatch batch;

    private OrthographicCamera camera;
    private Viewport viewport;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(8, 5); // Tamanho virtual

        shape = new ShapeRenderer();

        marcadorImg = new Texture("fogo.png");
        marcadorAzuladoImg = new Texture("fogo_azulado.png");
        marcadorForteImg = new Texture("fogo_forte.png");
        fundo = new Texture("background.png");
        batch = new SpriteBatch();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        // Decresce o marcador com o tempo
        marcadorPos -= decaimento * dt;
        marcadorPos = Math.max(0f, marcadorPos);

        // Se apertar esquerda e a última não foi esquerda
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.LEFT) && ultimaTecla != 1) {
            marcadorPos = Math.max(0f, marcadorPos + velocidade * dt * 10f);
            ultimaTecla = 1;
        }
        // Se apertar direita e a última não foi direita
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.RIGHT) && ultimaTecla != 2) {
            marcadorPos = Math.min(1f, marcadorPos + velocidade * dt * 10f);
            ultimaTecla = 2;
        }

        draw();
    }


    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(fundo, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        shape.setProjectionMatrix(viewport.getCamera().combined);
        desenharBarra(shape, viewport);
        desenharMarcador(batch, viewport, marcadorPos);
    }

    private void desenharBarra(ShapeRenderer shape, Viewport viewport) {
        float barraAltura = 0.5f;
        float barraEspaco = 0.2f;
        float barraMargem = 0.3f;
        float barraLargura = viewport.getWorldWidth() - 2 * barraMargem;
        float barraX = barraMargem;
        float barraY = viewport.getWorldHeight() - barraAltura - barraEspaco;

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BLACK);
        shape.rect(barraX - 2f/60f, barraY - 2f/60f, barraLargura + 4f/60f, barraAltura + 4f/60f);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        float frioPerc = 0.2f;
        float seguroPerc = 0.6f;
        float quentePerc = 0.2f;

        shape.setColor(new Color(0.3f, 0.2f, 0.7f, 1f));
        shape.rect(barraX, barraY, barraLargura * frioPerc, barraAltura);

        shape.setColor(Color.ORANGE);
        shape.rect(barraX + barraLargura * frioPerc, barraY, barraLargura * seguroPerc, barraAltura);

        // Tom vermelho mais escuro
        shape.setColor(new Color(0.7f, 0.1f, 0.1f, 1f));
        shape.rect(barraX + barraLargura * (frioPerc + seguroPerc), barraY, barraLargura * quentePerc, barraAltura);

        shape.end();
    }

    private void desenharMarcador(SpriteBatch batch, Viewport viewport, float pos) {
        float barraAltura = 0.5f;
        float barraMargem = 0.3f;
        float barraLargura = viewport.getWorldWidth() - 2 * barraMargem;
        float barraX = barraMargem;
        float barraY = viewport.getWorldHeight() - barraAltura - 0.2f;

        float marcadorRaio = barraAltura * 0.7f;
        float minCentroX = barraX + marcadorRaio;
        float maxCentroX = barraX + barraLargura - marcadorRaio;
        float marcadorCentroX = minCentroX + pos * (maxCentroX - minCentroX);
        float marcadorCentroY = barraY + barraAltura / 2f;

        // Divisórias
        float divisoriaFrio = barraX + barraLargura * 0.2f;
        float divisoriaQuente = barraX + barraLargura * 0.8f;

        float marcadorEsquerda = marcadorCentroX - marcadorRaio;
        float marcadorDireita = marcadorCentroX + marcadorRaio;

        Texture marcadorAtual;
        if (marcadorEsquerda <= divisoriaFrio) {
            marcadorAtual = marcadorAzuladoImg;
        } else if (marcadorDireita >= divisoriaQuente) {
            marcadorAtual = marcadorForteImg;
        } else {
            marcadorAtual = marcadorImg;
        }

        batch.begin();
        batch.draw(
            marcadorAtual,
            marcadorCentroX - marcadorRaio,
            marcadorCentroY - marcadorRaio,
            marcadorRaio * 2,
            marcadorRaio * 2
        );
        batch.end();
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        marcadorImg.dispose();
        marcadorAzuladoImg.dispose();
        marcadorForteImg.dispose();
        shape.dispose();
        batch.dispose();
        fundo.dispose();
    }
}
