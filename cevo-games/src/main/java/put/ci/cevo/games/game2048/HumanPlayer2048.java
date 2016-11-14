package put.ci.cevo.games.game2048;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.Decision;

public final class HumanPlayer2048 implements Agent<State2048, Action2048> {
	private final Object monitor;
	private Action2048 action;

	public HumanPlayer2048(final Component gui) {
		monitor = new Object();
		gui.addKeyListener(new KeyListener() {
			@Override
			public final void keyPressed(final KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					action = Action2048.UP;
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					action = Action2048.RIGHT;
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					action = Action2048.DOWN;
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					action = Action2048.LEFT;
				}
				if (action != null) {
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

	@Override
	public Decision<Action2048> chooseAction(State2048 state, List<Action2048> actions, RandomDataGenerator random) {
		action = null;
		while (action == null || !actions.contains(action)) {
			synchronized (monitor) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return Decision.of(action);
	}
}