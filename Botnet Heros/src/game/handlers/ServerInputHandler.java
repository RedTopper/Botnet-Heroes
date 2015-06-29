package game.handlers;

import game.net.SocketServer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class ServerInputHandler implements KeyListener {

	private final SocketServer s;
	private final JTextField f;

	public ServerInputHandler(JTextField frame, SocketServer server) {
		s = server;
		f = frame;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			final String message = f.getText().replace(",", "");
			if ((message != null) && !message.equals("")) {
				s.writeLog("[INFO][SIR] Sent: " + message);
				s.handleCommand("null", message, true);
				f.setText(null);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
