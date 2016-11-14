package put.ci.cevo.games.game2048;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.rl.environment.AgentTransition;

public final class GUI2048 {
	public static final int DEAFULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 544;
	public static final Color DEFAULT_BACKGROUND_COLOR = new Color(0xbbada0);
	public static final Color DEFAULT_EMPTY_TILE_COLOR = new Color(0xCDC1B4);
	public static final Color[] DEFAULT_TILE_BACKGROUND_COLORS = new Color[] { new Color(0xeee4da),
		new Color(0xede0c8), new Color(0xf2b179), new Color(0xf59563), new Color(0xf67c5f), new Color(0xf65e3b),
		new Color(0xedcf72), new Color(0xedcc61), new Color(0xedc850), new Color(0xedc53f), new Color(0xedc22e) };
	public static final Color[] DEFAULT_TILE_TEXT_COLORS = new Color[] { new Color(0x776e65), new Color(0x776e65),
		new Color(0xf9f6f2), new Color(0xf9f6f2), new Color(0xf9f6f2), new Color(0xf9f6f2), new Color(0xf9f6f2),
		new Color(0xf9f6f2), new Color(0xf9f6f2), new Color(0xf9f6f2), new Color(0xf9f6f2) };
	public static Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 55);
	private final JFrame frame;

	private volatile State2048 currentState;
	private volatile int currentScore;
	private volatile Boolean hasWon;

	public GUI2048(final int width, final int height) {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, new File("clear-sans/ClearSans-Bold.ttf"));
			DEFAULT_FONT = font.deriveFont(55.0f);
		} catch (FontFormatException | IOException e1) {
			e1.printStackTrace();
		}

		frame = new JFrame();
		final JMenuItem restart = new JMenuItem();
		restart.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		restart.setText("Restart");
		restart.addActionListener(new ActionListener() {
			@Override
			public final void actionPerformed(final ActionEvent e) {
				// currentState = game.sampleInitialStateDistribution(random);
			}
		});
		restart.setAccelerator(KeyStroke.getKeyStroke('R'));

		final JMenuItem screenshot = new JMenuItem();
		screenshot.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		screenshot.setText("Make screenshot");
		screenshot.addActionListener(new ActionListener() {
			@Override
			public final void actionPerformed(final ActionEvent e) {
				Container c = frame.getContentPane();
				BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
				c.paint(im.getGraphics());
				try {
					long timeMillis = System.currentTimeMillis();
					ImageIO.write(im, "PNG", new File("shot_" + timeMillis + ".png"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		screenshot.setAccelerator(KeyStroke.getKeyStroke('S'));

		final JMenuItem noPlayer = new JMenuItem();
		noPlayer.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		noPlayer.setText("None");
		noPlayer.addActionListener(new ActionListener() {
			@Override
			public final void actionPerformed(final ActionEvent e) {
				// game.setPlayer(null);
			}
		});

		final JMenuItem randomPlayer = new JMenuItem();
		randomPlayer.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		randomPlayer.setText("Random");
		randomPlayer.addActionListener(new ActionListener() {
			@Override
			public final void actionPerformed(final ActionEvent e) {
				// game.setPlayer(new RandomPlayer(game.getRandom()));
			}
		});

		final JMenuItem humanPlayer = new JMenuItem();
		humanPlayer.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		humanPlayer.setText("Human");
		humanPlayer.addActionListener(new ActionListener() {
			@Override
			public final void actionPerformed(final ActionEvent e) {
				// game.setPlayer(new HumanPlayer(game.getRandom(), frame));
			}
		});

		final JMenuItem alphaBetaPlayer = new JMenuItem();
		alphaBetaPlayer.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		alphaBetaPlayer.setText("AlphaBeta");
		alphaBetaPlayer.addActionListener(new ActionListener() {
			@Override
			public final void actionPerformed(final ActionEvent e) {
				// game.setPlayer(new IDDFSABPlayer(game.getRandom(), game));
			}
		});

		final JMenu player = new JMenu();
		player.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		player.setText("Player");
		player.add(noPlayer);
		player.add(randomPlayer);
		player.add(humanPlayer);
		player.add(alphaBetaPlayer);

		final JMenuItem exit = new JMenuItem();
		exit.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		exit.setText("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public final void actionPerformed(final ActionEvent e) {
				System.exit(0);
			}
		});

		final JMenu file = new JMenu();
		file.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		file.setText("File");
		file.add(screenshot);
		file.add(restart);
		file.add(player);
		file.add(exit);

		final JLabel currentPlayerLabel = new JLabel("Player: ");
		currentPlayerLabel.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		currentPlayerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

		final JLabel score = new JLabel();
		score.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		score.setText("Score: 0");
		score.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

		final JLabel bestScore = new JLabel();
		bestScore.setForeground(DEFAULT_TILE_TEXT_COLORS[0]);
		bestScore.setText("Best: 0");
		bestScore.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(file);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(currentPlayerLabel);
		menuBar.add(score);
		menuBar.add(bestScore);

		final JPanel contentPane = new JPanel() {
			private static final long serialVersionUID = -9218529938518563404L;

			@Override
			public final void paintComponent(final Graphics g) {
				super.paintComponent(g);
				frame.setTitle("2048");
				final int[][] grid = currentState.getPowerGrid();
				final Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				final float alpha;
//				final Boolean hasWon = game.getHasWon();
				if (hasWon != null) {
					alpha = 0.5f;
				} else {
					alpha = 1.0f;
				}
				g2d.setColor(getColor(DEFAULT_BACKGROUND_COLOR, alpha));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				final int maxWidthPerTile = getWidth() / grid[0].length;
				final int maxHeightPerTile = getHeight() / grid.length;
				final int tileWidth = (int) (0.8d * maxWidthPerTile);
				final int tileHeight = (int) (0.8d * maxHeightPerTile);
				final int tileArcWidth = tileWidth / 50;
				final int tileArcHeight = tileHeight / 50;
				final int tileWidthPadding = (int) (0.1d * maxWidthPerTile);
				final int tileHeightPadding = (int) (0.1d * maxHeightPerTile);
				for (int row = 0; row < grid.length; row++) {
					for (int column = 0; column < grid[row].length; column++) {
						if (grid[row][column] == 0) {
							g2d.setColor(getColor(DEFAULT_EMPTY_TILE_COLOR, alpha));
						} else {
							final int power = (int) (Math.log(grid[row][column]) / Math.log(2));
							g2d.setColor(getColor(DEFAULT_TILE_BACKGROUND_COLORS[power - 1], alpha));
						}
						int tileX = column * maxWidthPerTile + tileWidthPadding;
						int tileY = row * maxHeightPerTile + tileHeightPadding;
						g2d.fillRoundRect(tileX, tileY, tileWidth, tileHeight, tileArcWidth, tileArcHeight);
						if (grid[row][column] > 0) {
							final int power = (int) (Math.log(grid[row][column]) / Math.log(2));
							g2d.setColor(getColor(DEFAULT_TILE_TEXT_COLORS[power - 1], alpha));
							final String text = String.valueOf(grid[row][column]);
							g2d.setFont(scaleFont(text, tileWidth * 0.75f, g2d, DEFAULT_FONT));
							// g2d.setFont(DEFAULT_FONT);
							final FontMetrics fontMetrics = g.getFontMetrics(g2d.getFont());
							final int textWidth = fontMetrics.stringWidth(text);
							final int textHeight = fontMetrics.getHeight();
							g2d.drawString(text, (int) ((tileX + 0.5d * tileWidth) - (textWidth * 0.5d)),
								(int) ((tileY + 0.45d * tileHeight) + (textHeight * 0.3d)));
						}
					}
				}
//				final Player currentPlayer = game.getCurrentPlayer();
//				String p = "";
//				if (currentPlayer != null) {
//					if (currentPlayer.getClass().getSimpleName().matches("RandomPlayer")) {
//						p = "Random";
//					} else if (currentPlayer.getClass().getSimpleName().matches("HumanPlayer")) {
//						p = "Human";
//					} else if (currentPlayer.getClass().getSimpleName().matches("IDDFSABPlayer")) {
//						p = "AlphaBeta";
//					}
//				}
//				currentPlayerLabel.setText("Player: " + p);
				score.setText("Score: " + String.valueOf(currentScore));
				// bestScore.setText("Best: " + String.valueOf(game.getBestScore()));
				if (hasWon != null) {
					if (hasWon) {
						final String text = "You win!";
						final FontMetrics fontMetrics = g.getFontMetrics(g2d.getFont());
						final int textWidth = fontMetrics.stringWidth(text);
						final int textHeight = fontMetrics.getHeight();
						g2d.setColor(DEFAULT_TILE_TEXT_COLORS[2]);
						g2d.drawString(text, (int) ((0.5d * getWidth()) - (textWidth * 0.5d)),
							(int) ((0.5d * getHeight()) + (textHeight * 0.3d)));
					} else if (!hasWon) {
						final String text = "Game over!";
						final FontMetrics fontMetrics = g.getFontMetrics(g2d.getFont());
						final int textWidth = fontMetrics.stringWidth(text);
						final int textHeight = fontMetrics.getHeight();
						g2d.setColor(DEFAULT_TILE_TEXT_COLORS[0]);
						g2d.drawString(text, (int) ((0.5d * getWidth()) - (textWidth * 0.5d)),
							(int) ((0.5d * getHeight()) + (textHeight * 0.3d)));
					}
				}
			}
		};
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(contentPane);
		frame.setJMenuBar(menuBar);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public GUI2048() {
		this(DEAFULT_WIDTH, DEFAULT_HEIGHT);
	}

	public final JFrame getFrame() {
		return frame;
	}

	public final void update() {
		frame.revalidate();
		frame.repaint();
	}

	private static Font scaleFont(final String text, final float width, final Graphics g, final Font font) {
		final float fontWidth = g.getFontMetrics(font).stringWidth(text);
		if (fontWidth <= width) {
			return font;
		} else {
			final float fontSize = ( width / fontWidth) * font.getSize();
			return font.deriveFont(fontSize);
		}
	}

	private static Color getColor(final Color color, final float alpha) {
		final float[] components = color.getRGBComponents(null);
		return new Color(components[0], components[1], components[2], alpha);
	}

	public void playGame(Game2048 game, HumanPlayer2048 player, RandomDataGenerator random) {
		hasWon = null;
		currentScore = 0;
		currentState = game.sampleInitialStateDistribution(random);
		while (!game.isTerminal(currentState)) {
			Action2048 action = player.chooseAction(currentState, game.getPossibleActions(currentState), random).getAction();
			AgentTransition<State2048, Action2048> agentTransition = game.getAgentTransition(currentState, action
			);

			currentState = game.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();
			currentScore += agentTransition.getReward();
		}
		hasWon = false;
	}

	public static void main(final String[] args) {
		RandomDataGenerator random = new RandomDataGenerator();
		final Game2048 game = new Game2048();

		final GUI2048 gui = new GUI2048();
		final HumanPlayer2048 player = new HumanPlayer2048(gui.frame);

		final ActionListener taskPerformer = e -> gui.update();

		new Timer(100, taskPerformer).start();

		while (true) {
			gui.playGame(game, player, random);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
}