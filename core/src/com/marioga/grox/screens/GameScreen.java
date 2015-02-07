package com.marioga.grox.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.marioga.grox.game.WorldController;
import com.marioga.grox.game.WorldRenderer;
import com.marioga.grox.utils.Constants;

public class GameScreen extends ScreenAdapter {
	private static final String TAG = GameScreen.class.getName();

	private WorldController worldController;
	private WorldRenderer worldRenderer;
	
	private Game game;

	private boolean paused;

	public GameScreen (Game game) {
		this.game=game;
	}

	@Override
	public void render (float deltaTime) {
		// Do not update game world when paused.
		if (!paused) {
			// Update game world by the time that has passed
			// since last rendered frame.
			worldController.update(deltaTime);
		}
		
		Gdx.gl.glClearColor(0x93 / 255.0f,
				0x75 / 255.0f,
				0x75 / 255.0f,
				0x93 / 255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Render game world to screen
		worldRenderer.render();
	}

	@Override
	public void resize (int width, int height) {
		worldRenderer.resize(width, height);
	}

	@Override
	public void show () {
		// Initially start with a square camera; it will be resized
		OrthographicCamera camera = new OrthographicCamera(
				Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_WIDTH);
		camera.position.set(0, 0, 0);
		camera.update();
		
		worldController = new WorldController(game, camera);
		worldRenderer = new WorldRenderer(worldController, camera);
	}

	@Override
	public void dispose () {
		worldRenderer.dispose();
	}

	@Override
	public void pause () {
		paused = true;
	}

	@Override
	public void resume () {
		super.resume();
		// Only called on Android!
		paused = false;
	}
}
