package com.marioga.grox.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.PauseableThread;
import com.marioga.grox.game.ai.GroxPlay;
import com.marioga.grox.game.ai.PlayerAI;
import com.marioga.grox.game.objects.Block;
import com.marioga.grox.game.objects.Block.BlockState;

 public class WorldController extends GestureAdapter{
	private static final String TAG =
			WorldController.class.getName();
	
	private static final float AI_DELAY = 1.0f;
	
	private Game game;
	private OrthographicCamera camera;
	
	public GameWorld gameWorld;
	
	private float lastX;
	private float lastY;
	private float initialX;
	private float initialY;
	private int lastShadeRow;
	private int lastShadeColumn;
	
	private Block selectedBlock = null;
	private Block positionedBlock = null;
	private boolean keyWasPressed = false;
	private boolean draggingInProgress = false;

	private Stage stage;
	
//	private TextButton buttonUndo;
//	private TextButton buttonDone;

	public WorldController (Game game, OrthographicCamera camera, Stage stage) { 
		this.game = game;
		this.camera = camera;
		this.stage = stage;
		init();
	}
	
	private void init () {
		gameWorld = new GameWorld();
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new GestureDetector(this));
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		if (!gameWorld.getFirstPlayer().isHuman()) {
			handleAIPlay();
		}
		
//		initStage();
	}
	
//	private void initStage() {
//		Texture upRegion = new Texture(Gdx.files.internal("images/block_tile.png"));
//		Texture downRegion = new Texture(Gdx.files.internal("images/tile.png"));
//
//		TextButtonStyle style = new TextButtonStyle();
//		style.up = new TextureRegionDrawable(new TextureRegion(upRegion));
//		style.down = new TextureRegionDrawable(new TextureRegion(downRegion));
//		style.font = new BitmapFont(Gdx.files.internal("fonts/test.fnt"));
//		style.font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
//
//		buttonUndo = new TextButton("Undo", style);
//		buttonUndo.setWidth(50);
//		buttonUndo.setHeight(50);
//		buttonUndo.setTransform(true);
//		buttonUndo.setOrigin(buttonUndo.getWidth()/2,buttonUndo.getHeight()/2);
//		buttonUndo.setRotation(90);
//		buttonUndo.setColor(new Color(0.8f,0.2f,0.2f,0.9f));
//		buttonUndo.setVisible(false);
//		buttonUndo.setPosition(-0.492f*stage.getViewport().getCamera().viewportWidth, -buttonUndo.getHeight()/2);
//		stage.addActor(buttonUndo);
//		buttonUndo.addListener(new ChangeListener() {
//
//			@Override
//			public void changed(ChangeEvent event, Actor actor) {
//				Array<Block> myArray = (gameWorld.getTurn() == Turn.PLAYER_1) ? gameWorld.playerOneBlocks : gameWorld.playerTwoBlocks;
//				for (Block block : myArray) {
//					if (block.getBlockState() == BlockState.POSITIONED) {
//						buttonUndo.setVisible(false);
//						buttonDone.setVisible(false);
//						blockPositioned=false;
//						block.setBlockState(BlockState.IDLE);
//						block.setCurrentScaleFactor(block.getInitialScaleFactor());
//						block.getCenterBlockPosition().set(block.getInitialPosition());
//						gameWorld.clearPieceFromBoard(block.getRow(), block.getColumn(), block);
//					}
//				}
//			}
//		});
//
//		buttonDone = new TextButton("Play", style);
//		buttonDone.setWidth(50);
//		buttonDone.setHeight(50);
//		buttonDone.setTransform(true);
//		buttonDone.setOrigin(buttonDone.getWidth()/2,buttonDone.getHeight()/2);
//		buttonDone.setRotation(-90);
//		buttonDone.setColor(new Color(0.8f,0.2f,0.2f,0.9f));
//		buttonDone.setVisible(false);
//		buttonDone.setPosition(0.492f*stage.getViewport().getCamera().viewportWidth-buttonDone.getWidth(), -buttonDone.getHeight()/2);
//		stage.addActor(buttonDone);
//		buttonDone.addListener(new ChangeListener() {
//
//			@Override
//			public void changed(ChangeEvent event, Actor actor) {
//				Array<Block> myArray = (gameWorld.getTurn() == Turn.PLAYER_1) ? gameWorld.playerOneBlocks : gameWorld.playerTwoBlocks;
//				for (Block block : myArray) {
//					if (block.getBlockState() == BlockState.POSITIONED) {
//						buttonUndo.setVisible(false);
//						buttonDone.setVisible(false);
//						blockPositioned=false;
//						block.setBlockState(BlockState.PLAYED);
//						if (gameWorld.getTurn() == Turn.PLAYER_1) {
//							gameWorld.setTurn(Turn.PLAYER_2);
//						} else { 
//							gameWorld.setTurn(Turn.PLAYER_1);
//						}
//						
//					}
//				}
//			}
//		});
//	}
	
	public void update (float deltaTime) {
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			
			if (selectedBlock!=null) {
				selectedBlock.updateCenterBlockX(touchPos.x - lastX);
				selectedBlock.updateCenterBlockY(touchPos.y - lastY);
				updateBoardShades(selectedBlock.getCenterBlockPosition().x,selectedBlock.getCenterBlockPosition().y);
				if ((!draggingInProgress)&&
						((Math.abs(selectedBlock.getCenterBlockPosition().x-initialX)>selectedBlock.getScaledBlockDimension()/2)||
						(Math.abs(selectedBlock.getCenterBlockPosition().y-initialY)>selectedBlock.getScaledBlockDimension()/2))) {
					if (selectedBlock.getBlockState()==BlockState.POSITIONED) {
						changeTurn();
					}
					draggingInProgress=true;
					selectedBlock.setCurrentScaleFactor(1);
				}
			} else if (!keyWasPressed) {
				keyWasPressed = true;
				selectedBlock = blockTouched(touchPos.x, touchPos.y);
				if ((selectedBlock!=null)&&
						(selectedBlock.getBlockOwner().isHuman())&&
						((selectedBlock.getBlockState()==BlockState.POSITIONED)||
								((selectedBlock.getBlockOwner().isPlaying())&&(selectedBlock.getBlockState()==BlockState.IDLE)))) {
					lastShadeRow=0;
					lastShadeColumn=0;
					
					if (selectedBlock.getBlockState()==BlockState.POSITIONED) {
						gameWorld.clearPieceFromBoard(selectedBlock.getRow(),selectedBlock.getColumn(), selectedBlock);
					}
					
					//Only done for the purpose of render ordering
					selectedBlock.getBlockOwner().getBlocks().removeValue(selectedBlock, true);
					selectedBlock.getBlockOwner().getBlocks().add(selectedBlock);
					
				} else {
					selectedBlock=null;
					return;
				}
			}
			lastX=touchPos.x;
			lastY=touchPos.y;
		} else if (keyWasPressed) {
			keyWasPressed = false;
			if (selectedBlock!=null) {
				resetBoardShades();
				handleBlockRelease(draggingInProgress);
				selectedBlock = null;
			}
			draggingInProgress=false;
		}
	}
	
	private void resetBoardShades() {
		for (int i = 0; i<gameWorld.getGameBoard().getHeight(); i++) {
			for (int j = 0; j<gameWorld.getGameBoard().getWidth(); j++) {
				gameWorld.getGameBoard().getShades()[i][j]=false;
			}
		}
	}
	
	private void updateBoardShades(float x, float y) {
		int row = computeBlockRow(y);
		int column = computeBlockColumn(x);
		
		if ((row==lastShadeRow)&&(column==lastShadeColumn)) {
			return;
		}
		
		resetBoardShades();
		
		if ((row>=1)&&(row<=gameWorld.getGameBoard().getHeight())
				&&(column>=1)&&(column<=gameWorld.getGameBoard().getWidth())) {
			if (gameWorld.isValidMove(row, column, selectedBlock)) { //New position is good!
				for (int i = -1; i<=1; i++) {
					for (int j = -1; j<=1; j++) {
						if (selectedBlock.getMatrixRepresentation()[i+1][j+1]==1) {
							gameWorld.getGameBoard().getShades()[i+row-1][j+column-1]=true;
						}
					}
				}
			}
			lastShadeRow=row;
			lastShadeColumn=column;
		} else {
			lastShadeRow=0;
			lastShadeColumn=0;
		}
	}
	
	private int computeBlockRow(float y) {
		return (gameWorld.getGameBoard().getHeight() % 2 == 0) 
				? gameWorld.getGameBoard().getHeight()/2 + 1 - (int) Math.round((y
						-gameWorld.getGameBoard().centerPosition.y)/gameWorld.getGameBoard().blockDimension + 0.5f)
						: (gameWorld.getGameBoard().getHeight()+1)/2 - (int) Math.round((y
								-gameWorld.getGameBoard().centerPosition.y)/gameWorld.getGameBoard().blockDimension);
	}
	
	private int computeBlockColumn(float x) {
		return (gameWorld.getGameBoard().getWidth() % 2 == 0) 
				? gameWorld.getGameBoard().getWidth()/2 + (int) Math.round((x
						-gameWorld.getGameBoard().centerPosition.x)/gameWorld.getGameBoard().blockDimension + 0.5f)
						: (gameWorld.getGameBoard().getWidth()+1)/2 + (int) Math.round((x
								-gameWorld.getGameBoard().centerPosition.x)/gameWorld.getGameBoard().blockDimension);
	}
	
	private int computeBlockRow(Block block) {
		return computeBlockRow(block.getCenterBlockPosition().y);
	}
	
	private int computeBlockColumn(Block block) {
		return computeBlockColumn(block.getCenterBlockPosition().x);
	}
	
	private void changeTurn() {
		gameWorld.changeTurn();
		
		if (computingThread!=null) {
			Gdx.app.debug(TAG, "Yup");
			computingThread.setName("INACTIVE");
			computingThread.setPriority(Thread.MIN_PRIORITY);
			computingThread=null;
		}
		
		if (gameWorld.isGameOver()) {
			Gdx.app.log(TAG, "Game Over!");
			return;
		}
		
		if (!gameWorld.getCurrentPlayer().isHuman()) {
			handleAIPlay();
		}
	}
	
	private Thread computingThread;
	
	private void handleAIPlay() {
		Gdx.app.debug(TAG, "Just been called");
		
		if (computingThread==null) {
			computingThread = new Thread(new Runnable() {
				@Override
				public void run() {
					// do something important here, asynchronously to the rendering thread
					long timeInMillis = System.currentTimeMillis();
					
					final GroxPlay groxPlay = PlayerAI.computeNextPlay(gameWorld.getGameBoard(), 
							gameWorld.getCurrentPlayer(), gameWorld.getInactivePlayer());
					
					while (System.currentTimeMillis()-timeInMillis<1000);
					
					if (Thread.currentThread().getName().equals("ACTIVE")) {
						// post a Runnable to the rendering thread that processes the result
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								// process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
								Block block = groxPlay.getBlock();
								block.setCurrentScaleFactor(1);
								block.rotateBlockTo(groxPlay.getBlockRotation());
								block.setRow(groxPlay.getRow());
								block.setColumn(groxPlay.getColumn());
								block.moveToPositionInBoard(block.getRow(), block.getColumn(), 
										gameWorld.getGameBoard().centerPosition, 
										gameWorld.getGameBoard().getWidth(),
										gameWorld.getGameBoard().getHeight());
								gameWorld.updateGameBoardModel(block.getRow(), block.getColumn(), block);

								block.setBlockState(BlockState.POSITIONED);
								if (positionedBlock!=null) {
									positionedBlock.setBlockState(BlockState.PLAYED);
								}
								positionedBlock=block;
									
								changeTurn();
							}
						});
					}
				}
			},"ACTIVE");
			
			computingThread.start();
		}	
		
//		if (playAITask==null) {
//			playAITask = new GroxPlayTask(groxPlay);
//		} else {
//			playAITask.setGroxPlay(groxPlay);
//		}
//		
//		Timer.schedule(playAITask, AI_DELAY);
	}
	
	private void handleBlockRelease(boolean afterDrag) {
		int row = computeBlockRow(selectedBlock);
				
		int column = computeBlockColumn(selectedBlock);
		
		if ((row>=1)&&(row<=gameWorld.getGameBoard().getHeight())
				&&(column>=1)&&(column<=gameWorld.getGameBoard().getWidth())) {
			if (gameWorld.isValidMove(row, column, selectedBlock)) { //New position is good!
				if (selectedBlock.getBlockState()!=BlockState.POSITIONED) {
					if (positionedBlock!=null) {
						positionedBlock.setBlockState(BlockState.PLAYED);
					}
					selectedBlock.setBlockState(BlockState.POSITIONED);
					positionedBlock = selectedBlock;
				}
				selectedBlock.setRow(row);
				selectedBlock.setColumn(column);
				selectedBlock.moveToPositionInBoard(row, column, 
						gameWorld.getGameBoard().centerPosition, gameWorld.getGameBoard().getWidth(), gameWorld.getGameBoard().getHeight());
				gameWorld.updateGameBoardModel(row, column, selectedBlock);
				if (afterDrag) {
					changeTurn();
				}
			} else if (selectedBlock.getBlockState()==BlockState.POSITIONED) { //Not a valid move, back to position
				selectedBlock.moveToPositionInBoard(selectedBlock.getRow(), selectedBlock.getColumn(), 
						gameWorld.getGameBoard().centerPosition, gameWorld.getGameBoard().getWidth(), gameWorld.getGameBoard().getHeight());
				gameWorld.updateGameBoardModel(selectedBlock.getRow(), selectedBlock.getColumn(), selectedBlock);
				if (afterDrag) {
					changeTurn();
				}
			} else { //Not a valid move, back to idle
				selectedBlock.setBlockState(BlockState.IDLE);
				selectedBlock.setCurrentScaleFactor(selectedBlock.getInitialScaleFactor());
				selectedBlock.getCenterBlockPosition().set(selectedBlock.getInitialPosition());
			}
		} else {
			if (selectedBlock.getBlockState()==BlockState.POSITIONED) {
				positionedBlock = null;
				selectedBlock.setRow(0);
				selectedBlock.setColumn(0);
			}
			selectedBlock.setBlockState(BlockState.IDLE);
			selectedBlock.setCurrentScaleFactor(selectedBlock.getInitialScaleFactor());
			selectedBlock.getCenterBlockPosition().set(selectedBlock.getInitialPosition());
		}
	}
	
	@Override
	public boolean tap(float x, float y, int count, int button) {
		Vector3 touchPos = new Vector3(x,y,0);
		camera.unproject(touchPos);
		Block blockTapped = blockTouched(touchPos.x, touchPos.y);
		if ((blockTapped!=null)&&(blockTapped.getBlockOwner().isHuman())) {
			if (blockTapped.getBlockState()==BlockState.IDLE) {
				blockTapped.rotateBlockTo(blockTapped.getCurrentRotation()+1);
			} else if (blockTapped.getBlockState()==BlockState.POSITIONED) {
				//Rotate positioned block. Need to take care of adjacent blocks
				if (gameWorld.rotateInBoard(blockTapped)) {
					if (computingThread!=null) {
						Gdx.app.debug(TAG, "Yup");
						computingThread.setName("INACTIVE");
						computingThread.setPriority(Thread.MIN_PRIORITY);
						computingThread=null;
					}
					
					if (gameWorld.isGameOver()) {
						Gdx.app.log(TAG, "Game Over!");
						return false;
					}
					
					if (!gameWorld.getCurrentPlayer().isHuman()) {
						handleAIPlay();
					}
				}
			}
		}
		return false;
	}
	
	private Block blockTouched(float x, float y) {
		int factor;
		
		for (Block block : gameWorld.getFirstPlayer().getBlocks()) {
			factor = (block.getBlockState()==BlockState.IDLE) ? 1 : 2;
			for (int i = -1; i<=1; i++) {
				for (int j = -1; j<=1; j++) {
					if ((factor*Math.abs(block.getCenterBlockPosition().x+i*block.getScaledBlockDimension()-x)<=block.getScaledBlockDimension())&&
							(factor*Math.abs(block.getCenterBlockPosition().y+j*block.getScaledBlockDimension()-y)<=block.getScaledBlockDimension())&&
							(block.getMatrixRepresentation()[1-j][i+1]==1)) {
						return block;
					}
				}
			}
		}

		for (Block block : gameWorld.getSecondPlayer().getBlocks()) {
			factor = (block.getBlockState()==BlockState.IDLE) ? 1 : 2;
			for (int i = -1; i<=1; i++) {
				for (int j = -1; j<=1; j++) {
					if ((factor*Math.abs(block.getCenterBlockPosition().x+i*block.getScaledBlockDimension()-x)<=block.getScaledBlockDimension())&&
							(factor*Math.abs(block.getCenterBlockPosition().y+j*block.getScaledBlockDimension()-y)<=block.getScaledBlockDimension())&&
							(block.getMatrixRepresentation()[1-j][i+1]==1)) {
						return block;
					}
				}
			}
		}
		
		return null;
	}
}
