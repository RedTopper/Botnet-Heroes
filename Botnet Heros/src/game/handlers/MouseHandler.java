package game.handlers;

import game.Game;
import game.mobs.Mob;
import game.utils.Constants;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener, MouseMotionListener {

	private int x = Integer.MIN_VALUE;
	private int y = Integer.MIN_VALUE;

	private int[] actions = new int[Constants.MOBS_PER_LANE * Constants.LANES];
	private int lastClicked;

	private final Game game;

	public MouseHandler(Game game) {
		game.addMouseListener(this);
		game.addMouseMotionListener(this);
		this.game = game;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (game.stage == Game.Stage.LOADING) {
			game.loadingProgress.setProgress(game.loadingProgress.getProgress() + 15D);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		for (int i = 0; i < game.mobs[game.lane].length; i++) {
			final Mob m = game.mobs[game.lane][i];
			if ((m != null) && (m.getHealth() > 0D)) {
				final boolean within = (getX() >= m.getX()) && (getX() <= (m.getW() + m.getX()))
						&& (getY() >= m.getY()) && (getY() <= (m.getH() + m.getY()));
				if (within) {
					int total = 0;
					for (final int eger : actions) {
						total += eger;
					}
					if (total < Constants.COMMANDS_PER_PACKET) {
						actions[(Constants.MOBS_PER_LANE * game.lane) + i] += 1;
						lastClicked = (Constants.MOBS_PER_LANE * game.lane) + i;
						m.changeToClicked();
					}
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		x = Integer.MIN_VALUE;
		y = Integer.MIN_VALUE;
	}

	public int getX() {
		return x / Game.SCALE;
	}

	public int getY() {
		return y / Game.SCALE;
	}

	public int[] getActions() {
		return actions;
	}

	public int getLastClicked() {
		return lastClicked;
	}

	public void resetActions() {
		actions = new int[Constants.MOBS_PER_LANE * Constants.LANES];
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
