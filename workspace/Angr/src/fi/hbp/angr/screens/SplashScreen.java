package fi.hbp.angr.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;

/**
 * Splash screen/Loading screen.
 *
 * This screen will preload the assets of the next screen
 * while showing its own graphics.
 */
public class SplashScreen extends Timer.Task implements Screen {
    /**
     * Camera for projection.
     */
    private OrthographicCamera camera;
    /**
     * Sprite batch.
     */
    private SpriteBatch batch;
    /**
     * Splash screen sprite.
     */
    private Sprite sprite;

    /**
     * Delay of this splash screen.
     */
    private final float delay;
    /**
     * Splash screen timer.
     */
    private final Timer splashTimer = new Timer();
    /**
     * Screen loader for preloading next screen.
     */
    private ScreenLoader loader;

    /**
     *
     * @param g main game object.
     * @param gs screen to be shown after this splash screen.
     * @param delay minimum delay between show and swap to the next screen.
     * Actual delay may vary depending on the time needed for preloading
     * all assets of the next screen (gs).
     */
    public SplashScreen(Game g, Screen gs, float delay) {
        this.delay = delay;
        loader = new ScreenLoader(g, gs);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        sprite.draw(batch);
        batch.end();

        loader.update();
    }

    @Override
    public void resize(int width, int height) {
    }

    /**
     * Initializes this splash screen
     */
    @Override
    public void show() {
        Texture texture;

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(1, height / width);
        batch = new SpriteBatch();

        texture = new Texture(Gdx.files.internal("data/splash.png"));
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        TextureRegion region = new TextureRegion(texture, 0, 0, 512, 512);

        sprite = new Sprite(region);
        sprite.setSize(0.7f, 0.7f * sprite.getHeight() / sprite.getWidth());
        sprite.setOrigin(sprite.getWidth() / 2f, sprite.getHeight() / 2f);
        sprite.setPosition(-sprite.getWidth() / 2f, -sprite.getHeight() / 2f);

        // Change to Screen gs given in constructor after a short delay
        splashTimer.scheduleTask(this, delay);

        // Start loading the next screen
        loader.start();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void run() {
        loader.swap();
        loader = null;
    }
}
