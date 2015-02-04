package com.marioga.grox;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.marioga.grox.screens.GameScreen;

public class Grox extends Game {

	@Override
	public void create() {
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Start game at menu screen
		Gdx.graphics.setContinuousRendering(false);
		setScreen(new GameScreen(this));
	}
}
