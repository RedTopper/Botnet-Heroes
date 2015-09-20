package game.handlers;

import game.Game;
import game.net.packets.P01Quit;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * The {@link WindowHandler} class is added to the applications
 * <code>frame</code>. This class implements {@link WindowListener}. The only
 * method used is {@link WindowHandler#windowClosing(WindowEvent)}
 *
 * @author AJ Walter
 */
public class WindowHandler implements WindowListener {

	private final Game game;

	public WindowHandler(Game game) {
		this.game = game;
		this.game.frame.addWindowListener(this);
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	/**
	 * Sends a disconnect packet when the user clicks the X button of their
	 * game.
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		if ((game.getClient() != null) && game.getClient().isRunning()) {
			final P01Quit packet = new P01Quit(game.getClient().getUsername(), true);
			packet.writeData(game.getClient());
		}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
