package com.marioga.grox.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class GameBoard {
	private Texture region;

	private int width;
	private int height;

	public float blockDimension;
	public Vector2 centerPosition;

	private int[][] gameBoardModel;
	private boolean[][] shades;

	public boolean[][] getShades() {
		return shades;
	}

	public int[][] getGameBoardModel() {
		return gameBoardModel;
	}

	public void setGameBoardModel(int[][] gameBoardModel) {
		this.gameBoardModel = gameBoardModel;
	}

	public GameBoard(int width, int height, float dimensionTile) {
		this.width = width;
		this.height = height;
		blockDimension = dimensionTile;
		init();
	}

	private void init() {
		centerPosition = new Vector2();

		gameBoardModel = new int[height + 2][width + 2];
		for (int i = 0; i <= width + 1; i++) {
			gameBoardModel[0][i] = -1;
			gameBoardModel[height + 1][i] = -1;
		}
		for (int i = 0; i <= height + 1; i++) {
			gameBoardModel[i][0] = -1;
			gameBoardModel[i][width + 1] = -1;
		}

		shades = new boolean[height][width];

		region = new Texture(Gdx.files.internal("images/tile.png"));
	}

	public void render(SpriteBatch batch) {
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				if (shades[i][j]) {
					batch.setColor(Color.GRAY);
				}
				batch.draw(region, centerPosition.x
						+ (j - (float) this.width / 2) * blockDimension,
						centerPosition.y - (i + 1 - (float) this.height / 2)
								* blockDimension, blockDimension,
						blockDimension, 0, 0, region.getWidth(),
						region.getHeight(), false, false);
				if (shades[i][j]) {
					batch.setColor(Color.WHITE);
				}
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
