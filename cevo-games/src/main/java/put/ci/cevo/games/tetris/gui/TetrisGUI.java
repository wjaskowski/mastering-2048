package put.ci.cevo.games.tetris.gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.*;

import com.google.common.collect.ImmutableList;
import put.ci.cevo.games.tetris.Tetris;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.games.tetris.agents.SzitaSzepesvariSZTetrisAgent;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.util.random.ThreadedContext;

/**
 * The {@code Tetris} class is responsible for handling much of the game logic and
 * reading user input.
 * @author Brendan Jones
 *
 */
public class TetrisGUI extends JFrame {

	/**
	 * The Serial Version UID.
	 */
	private static final long serialVersionUID = -4722429764792514382L;

	/**
	 * The number of milliseconds per frame.
	 */
	private static final long FRAME_TIME = 1000L / 50L;

	/**
	 * The BoardPanel instance.
	 */
	private BoardPanel board;

	/**
	 * The SidePanel instance.
	 */
	private SidePanel side;

	/**
	 * Whether or not the game is paused.
	 */
	private boolean isPaused;

	/**
	 * Whether or not we've played a game yet. This is set to true
	 * initially and then set to false when the game starts.
	 */
	private boolean isNewGame;

	/**
	 * Whether or not the game is over.
	 */
	private boolean isGameOver;

	/**
	 * The current score.
	 */
	private int score;

	/**
	 * The clock that handles the update logic.
	 */
	private Clock logicTimer;

	/**
	 * The current type of tile.
	 */
	private TileType currentType;

	/**
	 * The next type of tile.
	 */
	private TileType nextType;

	/**
	 * The current column of our tile.
	 */
	private int currentCol;

	/**
	 * The current row of our tile.
	 */
	private int currentRow;

	/**
	 * The current rotation of our tile.
	 */
	private int currentRotation;

	/**
	 * The speed of the game.
	 */
	private float gameSpeed;

	/**
	 * Stores tetrominoes {@link TileType} that will occur during the game
	 */
	private List<TileType> tetrominoes;

	private Agent<TetrisState, TetrisAction> agent;
	private ThreadedContext context;

	private TetrisGUI(Agent<TetrisState, TetrisAction> agent, List<TileType> tetrominoes, ThreadedContext context) {
		super("Tetris");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		this.board = new BoardPanel(this);
		this.side = new SidePanel(this);
		this.agent = agent;
		this.context = context;
		this.tetrominoes = ImmutableList.copyOf(tetrominoes);

		add(board, BorderLayout.CENTER);
		add(side, BorderLayout.EAST);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				switch (e.getKeyCode()) {
					//pause
					case KeyEvent.VK_P:
						if (!isGameOver && !isNewGame) {
							isPaused = !isPaused;
							logicTimer.setPaused(isPaused);
						}
						break;

					//start
					case KeyEvent.VK_ENTER:
						if (isGameOver || isNewGame) {
							resetGame();
							chooseAction();
						}
						break;

				}
			}
		});

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Starts the game running. Initializes everything and enters the game loop.
	 */
	private void startGame() {
		this.isNewGame = true;
		this.gameSpeed = 30.0f;
		this.logicTimer = new Clock(gameSpeed);
		logicTimer.setPaused(true);

		while(true) {
			//Get the time that the frame started.
			long start = System.nanoTime();

			//Update the logic timer.
			logicTimer.update();

			/*
			 * If a cycle has elapsed on the timer, we can update the game and
			 * move our current piece down.
			 */
			if(logicTimer.hasElapsedCycle()) {
				updateGame();
			}

			//Display the window to the user.
			renderGame();

			/*
			 * Sleep to cap the framerate.
			 */
			long delta = (System.nanoTime() - start) / 1000000L;
			if(delta < FRAME_TIME) {
				try {
					Thread.sleep(FRAME_TIME - delta);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void chooseAction() {
		TetrisState tetrisState = new TetrisState(board.getTetrisBoard(), currentType.getTetromino());
		TetrisAction action = agent.chooseAction(tetrisState, Tetris.newSZTetris().getPossibleActions(tetrisState),
				context.getRandomForThread()).getAction();

		// setup the column and rotation
		currentCol = action.getPosition();
		rotatePiece(action.getRotation());
	}

	/**
	 * Updates the game and handles the bulk of it's logic.
	 */
	private void updateGame() {
		if(board.isValidAndEmpty(currentType, currentCol, currentRow + 1, currentRotation)) {
			currentRow++;
		} else {
			board.addPiece(currentType, currentCol, currentRow, currentRotation);

			int cleared = board.checkLines();
			if(cleared > 0) {
				score += cleared;
			}

			spawnPiece();
			chooseAction();
		}
	}

	/**
	 * Forces the BoardPanel and SidePanel to repaint.
	 */
	private void renderGame() {
		board.repaint();
		side.repaint();
	}

	/**
	 * Resets the game variables to their default values at the start
	 * of a new game.
	 */
	private void resetGame() {
		this.score = 0;
		this.gameSpeed = 30.0f;
		this.nextType = tetrominoes.get(context.getRandomForThread().nextInt(0, tetrominoes.size() - 1));
		this.isNewGame = false;
		this.isGameOver = false;
		board.clear();
		logicTimer.reset();
		logicTimer.setCyclesPerSecond(gameSpeed);
		spawnPiece();
	}

	/**
	 * Spawns a new piece and resets our piece's variables to their default
	 * values.
	 */
	private void spawnPiece() {
		/*
		 * Poll the last piece and reset our position and rotation to
		 * their default variables, then pick the next piece to use.
		 */
		this.currentType = nextType;
		this.currentCol = currentType.getSpawnColumn();
		this.currentRow = currentType.getSpawnRow();
		this.currentRotation = 0;
		this.nextType = tetrominoes.get(context.getRandomForThread().nextInt(0, tetrominoes.size() - 1));

		/*
		 * If the spawn point is invalid, we need to pause the game and flag that we've lost
		 * because it means that the pieces on the board have gotten too high.
		 */
		if(!board.isValidAndEmpty(currentType, currentCol, currentRow, currentRotation)) {
			this.isGameOver = true;
			logicTimer.setPaused(true);
		}
	}

	/**
	 * Attempts to set the rotation of the current piece to newRotation.
	 * @param newRotation The rotation of the new peice.
	 */
	private void rotatePiece(int newRotation) {
		/*
		 * Sometimes pieces will need to be moved when rotated to avoid clipping
		 * out of the board (the I piece is a good example of this). Here we store
		 * a temporary row and column in case we need to move the tile as well.
		 */
		int newColumn = currentCol;
		int newRow = currentRow;

		/*
		 * Get the insets for each of the sides. These are used to determine how
		 * many empty rows or columns there are on a given side.
		 */
		int left = currentType.getLeftInset(newRotation);
		int right = currentType.getRightInset(newRotation);
		int top = currentType.getTopInset(newRotation);
		int bottom = currentType.getBottomInset(newRotation);

		/*
		 * If the current piece is too far to the left or right, move the piece away from the edges
		 * so that the piece doesn't clip out of the map and automatically become invalid.
		 */
		if(currentCol < -left) {
			newColumn -= currentCol - left;
		} else if(currentCol + currentType.getDimension() - right >= BoardPanel.COL_COUNT) {
			newColumn -= (currentCol + currentType.getDimension() - right) - BoardPanel.COL_COUNT + 1;
		}

		/*
		 * If the current piece is too far to the top or bottom, move the piece away from the edges
		 * so that the piece doesn't clip out of the map and automatically become invalid.
		 */
		if(currentRow < -top) {
			newRow -= currentRow - top;
		} else if(currentRow + currentType.getDimension() - bottom >= BoardPanel.ROW_COUNT) {
			newRow -= (currentRow + currentType.getDimension() - bottom) - BoardPanel.ROW_COUNT + 1;
		}

		/*
		 * Check to see if the new position is acceptable. If it is, update the rotation and
		 * position of the piece.
		 */
		if(board.isValidAndEmpty(currentType, newColumn, newRow, newRotation)) {
			currentRotation = newRotation;
			currentRow = newRow;
			currentCol = newColumn;
		}
	}

	/**
	 * Checks to see whether or not the game is paused.
	 * @return Whether or not the game is paused.
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * Checks to see whether or not the game is over.
	 * @return Whether or not the game is over.
	 */
	public boolean isGameOver() {
		return isGameOver;
	}

	/**
	 * Checks to see whether or not we're on a new game.
	 * @return Whether or not this is a new game.
	 */
	public boolean isNewGame() {
		return isNewGame;
	}

	/**
	 * Gets the current score.
	 * @return The score.
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Gets the current type of piece we're using.
	 * @return The piece type.
	 */
	public TileType getPieceType() {
		return currentType;
	}

	/**
	 * Gets the next type of piece we're using.
	 * @return The next piece.
	 */
	public TileType getNextPieceType() {
		return nextType;
	}

	/**
	 * Gets the column of the current piece.
	 * @return The column.
	 */
	public int getPieceCol() {
		return currentCol;
	}

	/**
	 * Gets the row of the current piece.
	 * @return The row.
	 */
	public int getPieceRow() {
		return currentRow;
	}

	/**
	 * Gets the rotation of the current piece.
	 * @return The rotation.
	 */
	public int getPieceRotation() {
		return currentRotation;
	}

	public static void visualizeSZTetrisAgent(Agent<TetrisState, TetrisAction> agent, ThreadedContext context) {
		new TetrisGUI(agent, ImmutableList.of(TileType.TypeS, TileType.TypeZ), context).startGame();
	}

	public static void main(String[] args) {
		TetrisGUI.visualizeSZTetrisAgent(new SzitaSzepesvariSZTetrisAgent().create(), new ThreadedContext(123, 1));
	}
}
