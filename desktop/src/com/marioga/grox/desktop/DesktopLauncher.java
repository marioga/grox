package com.marioga.grox.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.marioga.grox.Grox;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 512;
		config.height = 800;
		config.resizable = false;
		config.title = "Grox";
		new LwjglApplication(new Grox(), config);
	}
}
