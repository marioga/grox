package com.marioga.grox.game.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class Player {
	public static enum Level {
		BEGINNER, INTERMEDIATE, ADVANCED, EXPERT, HUMAN
	}
	
	private Level level;
	private Array<Block> blocks;
	private boolean playing; 
	private Color color;
	private Color colorInactive;
	
	public Player(){}
	
	public Player(Level level, boolean playing, Color color) {
		this.level=level;
		this.setPlaying(playing);
		setPlayerColor(color);
	}

	public Player(Level level, Array<Block> blocks, boolean playing, Color color) {
		this.blocks=blocks;
		this.level=level;
		this.setPlaying(playing);
		setPlayerColor(color);
	}
	
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Array<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(Array<Block> blocks) {
		this.blocks = blocks;
	}
	
	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}
	
	public Color getColor() {
		return color;
	}

	public void setPlayerColor(Color color) {
		this.color = color;
		this.colorInactive = new Color(color.r+0.2f, color.g+0.2f, color.b+0.2f, 0.9f); 
	}
	
	public Color getColorInactive() {
		return colorInactive;
	}

	public boolean isHuman() {
		return (level==Level.HUMAN);
	}
}
