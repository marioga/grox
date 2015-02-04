package com.marioga.grox.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.marioga.grox.utils.Constants;

public class WorldRenderer implements Disposable {
	private static final String TAG =
			WorldRenderer.class.getName();
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private WorldController worldController;
	private Stage stage;

	public WorldRenderer (WorldController worldController, OrthographicCamera camera, Stage stage) { 
		this.worldController = worldController;
		this.camera = camera;
		this.stage = stage;
		init();
	}
	
	private void init () { 
		batch = new SpriteBatch();
	}
	
	public void resize (int width, int height) { 
		camera.viewportHeight = (Constants.VIEWPORT_WIDTH / width) *
				height;
		camera.update();
		
		stage.getViewport().getCamera().viewportHeight = 
				(stage.getViewport().getCamera().viewportWidth / width) * height;
		stage.getViewport().getCamera().update();
		
		worldController.gameWorld.updateWorldDimensions(camera.viewportHeight);
	}
	
	public void render() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.gameWorld.render(batch);
		batch.end();
		
		stage.draw();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
	}
}
