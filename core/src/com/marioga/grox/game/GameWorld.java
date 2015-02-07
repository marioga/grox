package com.marioga.grox.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.marioga.grox.game.objects.Block;
import com.marioga.grox.game.objects.Block.BlockState;
import com.marioga.grox.game.objects.Block.BlockType;
import com.marioga.grox.game.objects.GameBoard;
import com.marioga.grox.game.objects.Player;
import com.marioga.grox.utils.Constants;

public class GameWorld {
	private float dimensionTile;

	private GameBoard gameBoard;

	public GameBoard getGameBoard() {
		return gameBoard;
	}

	private Array<Player> players;

	public Player getFirstPlayer() {
		if ((players != null) && (players.get(0) != null)) {
			return players.get(0);
		}
		return null;
	}

	public Player getSecondPlayer() {
		if ((players != null) && (players.get(1) != null)) {
			return players.get(1);
		}
		return null;
	}

	public Player getCurrentPlayer() {
		if (players != null) {
			if ((players.get(0) != null) && (players.get(0).isPlaying())) {
				return players.get(0);
			} else if ((players.get(1) != null) && (players.get(1).isPlaying())) {
				return players.get(1);
			}
		}
		return null;
	}

	public Player getInactivePlayer() {
		if (players != null) {
			if ((players.get(0) != null) && (!players.get(0).isPlaying())) {
				return players.get(0);
			} else if ((players.get(1) != null)
					&& (!players.get(1).isPlaying())) {
				return players.get(1);
			}
		}
		return null;
	}

	public void changeTurn() {
		int activePlayer = (getFirstPlayer().isPlaying()) ? 0 : 1;
		players.get(activePlayer).setPlaying(false);
		players.get(1 - activePlayer).setPlaying(true);
	}

	public GameWorld() {
		init();
	}

	private void init() {
		// Compute dimensions
		int gameBoardWidth = Constants.BOARD_WIDTH;
		int gameBoardHeight = Constants.BOARD_HEIGHT;

		dimensionTile = 0.75f * Constants.VIEWPORT_WIDTH / gameBoardWidth;

		boolean[][] blockedSpaces = 
				new boolean[gameBoardHeight + 2][gameBoardWidth + 2];
		for (int i = 0; i <= gameBoardWidth + 1; i++) {
			blockedSpaces[0][i] = true;
			blockedSpaces[gameBoardHeight + 1][i] = true;
		}
		for (int i = 0; i <= gameBoardHeight + 1; i++) {
			blockedSpaces[i][0] = true;
			blockedSpaces[i][gameBoardWidth + 1] = true;
		}
		blockedSpaces[4][4] = true;
		
		// Create the board
		gameBoard = new GameBoard(
				gameBoardWidth, gameBoardHeight, 
				dimensionTile, blockedSpaces);

		// Create the players
		players = new Array<Player>();
		players.add(new Player(Constants.FIRST_PLAYER_LEVEL, true, new Color(
				0.7f, 0.1f, 0.1f, 1.0f)));
		players.add(new Player(Constants.SECOND_PLAYER_LEVEL, false, new Color(
				0.1f, 0.1f, 0.7f, 1.0f)));

		// Create the blocks
		Array<Block> firstPlayerBlocks = new Array<Block>();
		Array<Block> secondPlayerBlocks = new Array<Block>();
		for (BlockType bt : BlockType.values()) {
			firstPlayerBlocks.add(new Block(bt, 0, dimensionTile,
					getFirstPlayer()));
			secondPlayerBlocks.add(new Block(bt, 0, dimensionTile,
					getSecondPlayer()));
		}

		getFirstPlayer().setBlocks(firstPlayerBlocks);
		getSecondPlayer().setBlocks(secondPlayerBlocks);
	}

	public void updateWorldDimensions(float viewportHeight) {
		if (getFirstPlayer() != null) {
			for (Block block : getFirstPlayer().getBlocks()) {
				block.setInitialScaleFactor((viewportHeight - gameBoard
						.getHeight() * dimensionTile)
						/ 18 / dimensionTile);
				block.updateInitialPosition(0, gameBoard.centerPosition.y
						- gameBoard.getHeight() * dimensionTile / 2 - 2.5f
						* dimensionTile * block.getInitialScaleFactor());
				if (block.getBlockState() == BlockState.IDLE) {
					block.setCurrentScaleFactor(block.getInitialScaleFactor());
					block.getCenterBlockPosition().set(
							block.getInitialPosition());
				}
			}
		}
		if (getSecondPlayer() != null) {
			for (Block block : getSecondPlayer().getBlocks()) {
				block.setInitialScaleFactor((viewportHeight - gameBoard
						.getHeight() * dimensionTile)
						/ 18 / dimensionTile);
				block.updateInitialPosition(0, gameBoard.centerPosition.y
						+ gameBoard.getHeight() * dimensionTile / 2 + 6.5f
						* dimensionTile * block.getInitialScaleFactor());
				if (block.getBlockState() == BlockState.IDLE) {
					block.setCurrentScaleFactor(block.getInitialScaleFactor());
					block.getCenterBlockPosition().set(
							block.getInitialPosition());
				}
			}
		}
	}

	public void render(SpriteBatch batch) {
		gameBoard.render(batch);

		// We use alternative to compare less and speed up the rendering process
		int inactivePlayer = (getFirstPlayer().isPlaying()) ? 1 : 0;
		for (Block block : players.get(inactivePlayer).getBlocks()) {
			block.render(batch);
		}
		for (Block block : players.get(1 - inactivePlayer).getBlocks()) {
			block.render(batch);
		}
	}

	public static boolean isValidMove(int[][] boardMatrix,
			int[][] blockMatrix, int row, int column) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if ((blockMatrix[i + 1][j + 1] == 1)
						&& (boardMatrix[row + i][column + j] != 0)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isValidMove(int row, int column, Block block) {
		return isValidMove(gameBoard.getGameBoardModel(),
				block.getMatrixRepresentation(), row, column);
	}

	public void updateGameBoardModel(int row, int column, Block block) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (block.getMatrixRepresentation()[i + 1][j + 1] == 1) {
					gameBoard.getGameBoardModel()[row + i][column + j] = 1;
				}
			}
		}
	}

	public void clearPieceFromBoard(int row, int column, Block block) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if ((gameBoard.getGameBoardModel()[row + i][column + j] == 1)
						&& (block.getMatrixRepresentation()[i + 1][j + 1] == 1)) {
					gameBoard.getGameBoardModel()[row + i][column + j] = 0;
				}
			}
		}
	}

	public boolean rotateInBoard(Block block) {
		int row = block.getRow();
		int column = block.getColumn();
		clearPieceFromBoard(row, column, block);

		int currentRotation = block.getCurrentRotation();
		block.rotateBlockTo(currentRotation++);
		while (!isValidMove(getGameBoard().getGameBoardModel(),
				block.getMatrixRepresentation(), row, column)) {
			block.rotateBlockTo(currentRotation++);
		}
		updateGameBoardModel(row, column, block);
		return (block.getCurrentRotation() == currentRotation);
	}

	public boolean isGameOver() {
		Array<Block> myArray = getCurrentPlayer().getBlocks();
		for (Block block : myArray) {
			if (block.getBlockState() == BlockState.PLAYED) {
				continue;
			}

			for (int rot = 0; rot < block.getNumberOfRotatedForms(); rot++) {
				for (int row = 1; row <= getGameBoard().getHeight(); row++) {
					for (int column = 1; column <= getGameBoard().getWidth(); column++) {
						if (isValidMove(getGameBoard().getGameBoardModel(),
								block.getRotatedMatrixRepresentation(rot), row,
								column)) {
							return false;
						}
					}
				}
			}

		}
		return true;
	}
}
