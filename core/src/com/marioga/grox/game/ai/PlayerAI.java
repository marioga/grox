package com.marioga.grox.game.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.marioga.grox.game.GameWorld;
import com.marioga.grox.game.objects.Block;
import com.marioga.grox.game.objects.Block.BlockState;
import com.marioga.grox.game.objects.GameBoard;
import com.marioga.grox.game.objects.Player;

public class PlayerAI {
	public static GroxPlay computeNextPlay(GameBoard board, Player player,
			Player opponent) {
		Array<Block> copyPlayerBlocks = new Array<Block>(player.getBlocks());
		Array<Block> copyOpponentBlocks = new Array<Block>(opponent.getBlocks());

		int i = 0;
		while (i < copyPlayerBlocks.size) {
			if (copyPlayerBlocks.get(i).getBlockState() != BlockState.IDLE) {
				copyPlayerBlocks.removeIndex(i);
			} else {
				i++;
			}
		}

		i = 0;
		while (i < copyOpponentBlocks.size) {
			if (copyOpponentBlocks.get(i).getBlockState() != BlockState.IDLE) {
				copyOpponentBlocks.removeIndex(i);
			} else {
				i++;
			}
		}

		switch (player.getLevel()) {
		case BEGINNER:
			return computeNextPlayBeginner(board.getGameBoardModel(),
					copyPlayerBlocks, copyOpponentBlocks);
		case INTERMEDIATE:
			return computeNextPlayIntermediate(board.getGameBoardModel(),
					copyPlayerBlocks, copyOpponentBlocks);
		case ADVANCED:
			return computeNextPlayAdvanced(board.getGameBoardModel(),
					copyPlayerBlocks, copyOpponentBlocks);
		case EXPERT:
			return computeNextPlayExpert(board.getGameBoardModel(),
					copyPlayerBlocks, copyOpponentBlocks);
		default:
			return null;
		}
	}

	private static GroxPlay computeNextPlayBeginner(int[][] boardModel,
			Array<Block> playerBlocks, Array<Block> opponentBlocks) {

		// This one will use a random algorithm

		Array<GroxPlay> myArray = new Array<GroxPlay>();

		for (Block block : playerBlocks) {
			for (int rot = 0; rot < block.getNumberOfRotatedForms(); rot++) {
				for (int row = 1; row < boardModel.length - 1; row++) {
					for (int column = 1; column < boardModel[0].length - 1; column++) {
						if (GameWorld.isValidMove(boardModel,
								block.getRotatedMatrixRepresentation(rot), row,
								column)) {
							myArray.add(new GroxPlay(row, column, block, rot));
						}
					}
				}
			}
		}

		return myArray.random();
	}

	private static GroxPlay computeNextPlayIntermediate(int[][] boardModel,
			Array<Block> playerBlocks, Array<Block> opponentBlocks) {
		// Makes the play that leaves the least options to the opponent,
		// except for the first play which is random

		if (getPlayNumber(boardModel) == 1) {
			return computeNextPlayBeginner(boardModel, playerBlocks,
					opponentBlocks);
		}

		int maxPlays = -1000;
		GroxPlay bestPlay = null;

		for (Block block : playerBlocks) {
			for (int rot = 0; rot < block.getNumberOfRotatedForms(); rot++) {
				for (int row = 1; row < boardModel.length - 1; row++) {
					for (int column = 1; column < boardModel[0].length - 1; column++) {
						if (GameWorld.isValidMove(boardModel,
								block.getRotatedMatrixRepresentation(rot), row,
								column)) {
							GroxPlay play = new GroxPlay(row, column, block,
									rot);
							int oppPlays = computePlaysAfter(play, boardModel,
									opponentBlocks, true);
							if (oppPlays == 0) {
								return play;
							}
							int myPlays = computePlaysAfter(play, boardModel,
									playerBlocks, false);
							if ((bestPlay == null)
									|| (myPlays - oppPlays > maxPlays)) {
								bestPlay = play;
								maxPlays = myPlays - oppPlays;
							}
						}
					}
				}
			}
		}

		Gdx.app.debug("Best", String.valueOf(maxPlays));
		return bestPlay;
	}

	private static int getPlayNumber(int[][] boardModel) {
		int sum = 0;
		for (int row = 1; row < boardModel.length - 1; row++) {
			for (int column = 1; column < boardModel[0].length - 1; column++) {
				sum += boardModel[row][column];
			}
		}
		return (int) Math.floor(sum / 8) + 1;
	}

	private static int computePlaysAfter(GroxPlay play, int[][] boardModel,
			Array<Block> blocks, boolean opponent) {
		Array<Block> tempArray = new Array<Block>(blocks);
		if (!opponent) {
			tempArray.removeValue(play.getBlock(), true);
		}

		int[][] boardCopy = getCopyOfBoardAfterPlay(boardModel, play);

		int counter = 0;

		for (Block block : tempArray) {
			for (int rot = 0; rot < block.getNumberOfRotatedForms(); rot++) {
				for (int row = 1; row < boardModel.length - 1; row++) {
					for (int column = 1; column < boardModel[0].length - 1; column++) {
						if (GameWorld.isValidMove(boardCopy,
								block.getRotatedMatrixRepresentation(rot), row,
								column)) {
							counter++;
						}
					}
				}
			}
		}

		return counter;
	}

	private static GroxPlay computeNextPlayAdvanced(int[][] boardModel,
			Array<Block> playerBlocks, Array<Block> opponentBlocks) {
		// Plays where max(min (Plays after opponent play)) is achieved

		if (getPlayNumber(boardModel) == 1) {
			return computeNextPlayBeginner(boardModel, playerBlocks,
					opponentBlocks);
		}

		int maxMinPlaysAfter = -1000;
		GroxPlay bestPlay = null;

		for (Block block : playerBlocks) {
			for (int rot = 0; rot < block.getNumberOfRotatedForms(); rot++) {
				for (int row = 1; row < boardModel.length - 1; row++) {
					for (int column = 1; column < boardModel[0].length - 1; column++) {
						if (GameWorld.isValidMove(boardModel,
								block.getRotatedMatrixRepresentation(rot), row,
								column)) {
							GroxPlay play = new GroxPlay(row, column, block,
									rot);
							int minPlaysAfter = computeMinPlaysAfter(play,
									boardModel, playerBlocks, opponentBlocks);
							if ((minPlaysAfter == -1) || (minPlaysAfter >= 20)) {
								// -1 means no more plays left for opponent,
								// 20 is a reasonable amount of plays
								return play;
							}
							if ((bestPlay == null)
									|| (minPlaysAfter > maxMinPlaysAfter)) {
								bestPlay = play;
								maxMinPlaysAfter = minPlaysAfter;
							}
						}
					}
				}
			}
		}

		Gdx.app.debug("Best", String.valueOf(maxMinPlaysAfter));
		return bestPlay;
	}

	private static int computeMinPlaysAfter(GroxPlay play, int[][] boardModel,
			Array<Block> playerBlocks, Array<Block> opponentBlocks) {
		Array<Block> copyPlayerArray = new Array<Block>(playerBlocks);
		copyPlayerArray.removeValue(play.getBlock(), true);

		int[][] boardCopy = getCopyOfBoardAfterPlay(boardModel, play);

		int counter = 0;
		int minPlays = 1000;

		for (Block block : opponentBlocks) {
			for (int rot = 0; rot < block.getNumberOfRotatedForms(); rot++) {
				for (int row = 1; row < boardModel.length - 1; row++) {
					for (int column = 1; column < boardModel[0].length - 1; column++) {
						if (GameWorld.isValidMove(boardCopy,
								block.getRotatedMatrixRepresentation(rot), row,
								column)) {
							counter++;
							GroxPlay opponentPlay = new GroxPlay(row, column,
									block, rot);
							int myPlaysAfterOpponent = computePlaysAfter(
									opponentPlay, boardCopy, copyPlayerArray,
									false);
							if (myPlaysAfterOpponent < minPlays) {
								minPlays = myPlaysAfterOpponent;
							}
						}
					}
				}
			}
		}

		if (counter == 0) {
			return -1;
		} else {
			return minPlays;
		}
	}

	private static int[][] getCopyOfBoardAfterPlay(int[][] boardModel,
			GroxPlay play) {

		int[][] boardCopy = new int[boardModel.length][boardModel[0].length];
		for (int row = 0; row < boardModel.length; row++) {
			for (int column = 0; column < boardModel[0].length; column++) {
				boardCopy[row][column] = boardModel[row][column];
			}
		}

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (play.getBlock().getRotatedMatrixRepresentation(
						play.getBlockRotation())[i + 1][j + 1] == 1) {
					boardCopy[play.getRow() + i][play.getColumn() + j] = 1;
				}
			}
		}

		return boardCopy;
	}

	private static GroxPlay computeNextPlayExpert(int[][] boardModel,
			Array<Block> playerBlocks, Array<Block> opponentBlocks) {
		// Makes the play that minimizes number of losses

		if (getPlayNumber(boardModel) <= 2) {
			return computeNextPlayBeginner(boardModel, playerBlocks,
					opponentBlocks);
		}

		int minLosses = 1000;
		GroxPlay bestPlay = null;

		for (Block block : playerBlocks) {
			for (int rot = 0; rot < block.getNumberOfRotatedForms(); rot++) {
				for (int row = 1; row < boardModel.length - 1; row++) {
					for (int column = 1; column < boardModel[0].length - 1; column++) {
						if (GameWorld.isValidMove(boardModel,
								block.getRotatedMatrixRepresentation(rot), row,
								column)) {
							GroxPlay play = new GroxPlay(row, column, block,
									rot);
							int losses = computeNumberOfLosses(play,
									boardModel, playerBlocks, opponentBlocks);
							if (losses == 0) {
								Gdx.app.debug("Best", "Boom");
								return play;
							}
							if (losses < minLosses) {
								minLosses = losses;
								bestPlay = play;
							}
						}
					}
				}
			}
		}

		Gdx.app.debug("Best", String.valueOf(minLosses));
		return bestPlay;
	}

	private static int computeNumberOfLosses(GroxPlay play, int[][] boardModel,
			Array<Block> playerBlocks, Array<Block> opponentBlocks) {
		Array<Block> copyPlayerArray = new Array<Block>(playerBlocks);
		copyPlayerArray.removeValue(play.getBlock(), true);

		int[][] boardCopy = getCopyOfBoardAfterPlay(boardModel, play);

		int numLosses = 0;

		for (Block block : opponentBlocks) {
			for (int rot = 0; rot < block.getNumberOfRotatedForms(); rot++) {
				for (int row = 1; row < boardCopy.length - 1; row++) {
					for (int column = 1; column < boardCopy[0].length - 1; column++) {
						if (GameWorld.isValidMove(boardCopy,
								block.getRotatedMatrixRepresentation(rot), row,
								column)) {
							GroxPlay opponentPlay = new GroxPlay(row, column,
									block, rot);
							if (computeNumberOfLosses(opponentPlay, boardCopy,
									opponentBlocks, copyPlayerArray) == 0) {
								numLosses++;
							}
						}
					}
				}
			}
		}
		return numLosses;
	}
}
