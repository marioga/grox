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

	public WorldRenderer (WorldController worldController, OrthographicCamera camera) { 
		this.worldController = worldController;
		this.camera = camera;
		init();
	}
	
	private void init () { 
		batch = new SpriteBatch();
	}
	
	public void resize (int width, int height) { 
		camera.viewportHeight = (Constants.VIEWPORT_WIDTH / width) *
				height;
		camera.update();
		
		worldController.gameWorld.updateWorldDimensions(camera.viewportHeight);
	}
	
	public void render() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.gameWorld.render(batch);
		batch.end();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
	}
}
