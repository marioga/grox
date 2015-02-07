package com.marioga.grox.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Block {
	private Texture region;

	public static enum BlockType {
		// Hardcoding matrix forms. 2 is a separator for different forms.
		SHAPE_O(1, "110110000"),
		SHAPE_S(2, "0111100002100110010"),
		SHAPE_Z(2, "1100110002010110100"),
		SHAPE_L(4, "010010011200111100021100100102000111100"),
		SHAPE_J(4, "010010110200011100120110100102100111000"),
		SHAPE_T(4, "000111010201001101020101110002010110010");

		private int numberOfForms;
		private String representation;

		private BlockType(int forms, String repr) {
			numberOfForms = forms;
			representation = repr;
		}

		public int[][] getMatrixRepresentation(int rotation) {
			int[][] matrix = new int[3][3];
			for (int i = rotation * 10; i < rotation * 10 + 9; i++) {
				int row = (i - rotation * 10) / 3;
				int column = (i - rotation * 10) % 3;
				matrix[row][column] = Character.getNumericValue(representation
						.charAt(i));
			}
			return matrix;
		}

		public int getNumberOfForms() {
			return numberOfForms;
		}
	}

	public static enum BlockState {
		IDLE, POSITIONED, PLAYED
	}

	// Integer in [0,3] giving rotation divided by 90 degrees
	private int currentRotation;
	private int[][] matrixRepresentation;

	public int[][] getMatrixRepresentation() {
		return matrixRepresentation;
	}

	private BlockType blockType;

	public BlockType getBlockType() {
		return blockType;
	}

	private BlockState blockState;
	private Color blockColor;
	private Color blockColorInactive;
	private Player blockOwner;

	private float blockDimension;

	private int row;
	private int column;

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	private float currentScaleFactor;

	public float getCurrentScaleFactor() {
		return currentScaleFactor;
	}

	public void setCurrentScaleFactor(float currentScaleFactor) {
		this.currentScaleFactor = currentScaleFactor;
	}

	private float initialScaleFactor;

	public float getInitialScaleFactor() {
		return initialScaleFactor;
	}

	public void setInitialScaleFactor(float initialScaleFactor) {
		this.initialScaleFactor = initialScaleFactor;
	}

	// Initial position of center of center block
	private Vector2 initialPosition;

	public Vector2 getInitialPosition() {
		return initialPosition;
	}

	// Current position of center of center block
	private Vector2 centerBlockPosition;

	public Vector2 getCenterBlockPosition() {
		return centerBlockPosition;
	}

	public Block(BlockType type, int rotation, float dimension, Player owner) {
		blockType = type;
		blockDimension = dimension;
		blockOwner = owner;

		setBlockState(BlockState.IDLE);
		blockColor = blockOwner.getColor();
		blockColorInactive = blockOwner.getColorInactive();
		row = 0;
		column = 0;

		centerBlockPosition = new Vector2();
		initialPosition = new Vector2();

		region = new Texture(Gdx.files.internal("images/block_tile.png"));
		matrixRepresentation = new int[3][3];

		resetMatrixRepresentation();

		currentRotation = 0;
		if (rotation != 0) {
			rotateBlockTo(rotation);
		}
	}

	private void resetMatrixRepresentation() {
		matrixRepresentation = blockType.getMatrixRepresentation(0);
	}

	public int getCurrentRotation() {
		return currentRotation;
	}

	public void rotateBlockTo(int rotation) {
		int numForms = blockType.getNumberOfForms();
		currentRotation = (numForms + rotation) % numForms;

		matrixRepresentation = blockType
				.getMatrixRepresentation(currentRotation);
	}

	public void moveToPositionInBoard(int row, int column,
			Vector2 boardCenterPosition, int boardWidth, int boardHeight) {
		centerBlockPosition.x = boardCenterPosition.x
				+ (column - (float) (boardWidth + 1) / 2) * blockDimension;
		centerBlockPosition.y = boardCenterPosition.y
				- (row - (float) (boardHeight + 1) / 2) * blockDimension;
	}

	public void updateInitialPosition(float posX, float posY) {
		float deltaX = 0;
		float deltaY = 0;
		float offset = 4 * blockDimension * initialScaleFactor;
		switch (blockType) {
		case SHAPE_S:
			deltaX = -offset;
			break;
		case SHAPE_Z:
			deltaX = -offset;
			deltaY = -offset;
			break;
		case SHAPE_L:
			break;
		case SHAPE_J:
			deltaY = -offset;
			break;
		case SHAPE_T:
			deltaX = offset;
			break;
		case SHAPE_O:
			deltaX = offset;
			deltaY = -offset;
			break;
		}
		initialPosition.x = posX + deltaX;
		initialPosition.y = posY + deltaY;
	}

	public void render(SpriteBatch batch) {
		float dimen = blockDimension * currentScaleFactor;
		if (((blockOwner.isPlaying()) && (blockState == BlockState.IDLE))
				|| (blockState == BlockState.POSITIONED)) {
			batch.setColor(blockColor);
		} else {
			batch.setColor(blockColorInactive);
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (matrixRepresentation[i][j] == 1) {
					batch.draw(region, centerBlockPosition.x + (j - 1) * dimen
							- dimen / 2, centerBlockPosition.y - (i - 1)
							* dimen - dimen / 2, dimen, dimen, 0, 0,
							region.getWidth(), region.getHeight(),
							false, false);
				}
			}
		}
		batch.setColor(Color.WHITE);
	}

	public void updateCenterBlockX(float deltaX) {
		centerBlockPosition.x += deltaX;
	}

	public void updateCenterBlockY(float deltaY) {
		centerBlockPosition.y += deltaY;
	}

	public float getScaledBlockDimension() {
		return blockDimension * currentScaleFactor;
	}

	public BlockState getBlockState() {
		return blockState;
	}

	public void setBlockState(BlockState blockState) {
		this.blockState = blockState;
	}

	public Player getBlockOwner() {
		return blockOwner;
	}

	public int getNumberOfRotatedForms() {
		return blockType.getNumberOfForms();
	}

	public int[][] getRotatedMatrixRepresentation(int rotation) {
		int numForms = blockType.getNumberOfForms();

		return blockType.getMatrixRepresentation((numForms + rotation)
				% numForms);
	}
}
