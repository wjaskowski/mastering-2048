package put.ci.cevo.experiments.cig2048.sanitykey;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

final class HumanPlayer extends AbstractPlayer {
	private final Object monitor;
	private int action;

	public HumanPlayer(final Random random, final Component gui) {
		super(random);
		monitor = new Object();
		gui.addKeyListener(new KeyListener() {
			@Override
			public final void keyPressed(final KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					action = Game.UP;
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					action = Game.RIGHT;
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					action = Game.DOWN;
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					action = Game.LEFT;
				}
				if (action != -1) {
					synchronized (monitor) {
						monitor.notify();
					}
				}
			}

			@Override
			public final void keyReleased(final KeyEvent e) {

			}

			@Override
			public final void keyTyped(final KeyEvent e) {

			}

		});
	}

	public final Object getMonitor() {
		return monitor;
	}

	public final void setAction(final int action) {
		this.action = action;
	}

	@Override
	public final int getAction() {
		action = -1;
		while (action == -1) {
			synchronized (monitor) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return action;
	}
}