package fi.hbp.angr.screens;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import fi.hbp.angr.BodyFactory;
import fi.hbp.angr.GameStage;
import fi.hbp.angr.ItemDestructionList;
import fi.hbp.angr.Preloadable;
import fi.hbp.angr.hud.Hud;
import fi.hbp.angr.hud.HudScoreCounter;
import fi.hbp.angr.logic.GameState;
import fi.hbp.angr.logic.ModelContactListener;
import fi.hbp.angr.models.Destructible;
import fi.hbp.angr.models.levels.Level;

public class GameScreen implements Screen, Preloadable {
    private InputMultiplexer inputMultiplexer;
    private Stage stage;
    private World world;
    Level level;
    ItemDestructionList itemDestructor;
    private GameState gameState = new GameState();
    private Hud hud = new Hud();
    private HudScoreCounter score;

    /**
     * Start game
     * @param level Game level
     */
    public GameScreen(Level level) {
        this.level = level;
        score = new HudScoreCounter(gameState);
        score.loadAssets();
        hud.addActor(score);
    }

    @Override
    public void preload() {
        level.preload();
        BodyFactory.preload();
    }

    @Override
    public void unload() {
        level.unload();
    }

    @Override
    public void render(float delta) {
        destroyActors();
        world.step(delta, 6, 2);

        Gdx.gl.glClearColor(0f, 0.75f, 1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
        hud.draw();
    }

    /**
     * Remove destroyed actors/items.
     */
    private void destroyActors() {
        if (!itemDestructor.isEmpty()) {
            Iterator<Actor> it = itemDestructor.getIterator();
            while(it.hasNext()) {
                Actor a = it.next();
                ((Destructible)a).getBody().setUserData(null);
                world.destroyBody(((Destructible)a).getBody());
                a.remove();
            }
            itemDestructor.clear();
        }
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void show() {
        world = new World(new Vector2(0.0f, -9.8f), true);
        world.setWarmStarting(true);

        int xsize = Gdx.graphics.getWidth() * 2;
        int ysize = Gdx.graphics.getHeight() * 2;
        stage = new GameStage(xsize, ysize, world);

        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexer.addProcessor(stage);

        itemDestructor = new ItemDestructionList();

        ModelContactListener mcl = new ModelContactListener(gameState);
        world.setContactListener(mcl);

        // Create and add map/level actor
        BodyFactory bf = new BodyFactory(stage, world, itemDestructor, inputMultiplexer);
        gameState.clear();
        level.show(bf, gameState);
        stage.addActor(level);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        stage.dispose();
        world.dispose();
        this.unload();
    }
}
