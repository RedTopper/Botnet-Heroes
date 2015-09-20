package game.handlers;

import game.Game;
import game.gfx.Button;
import game.gfx.Font;
import game.gfx.TextBox;
import game.utils.Constants;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles the input used by the user.
 *
 * @author AJ
 *
 */
public class InputHandler implements KeyListener {

	private final Game game;

	public InputHandler(Game game) {
		game.addKeyListener(this);
		game.setFocusTraversalKeysEnabled(false);
		this.game = game;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			for (final Button b : game.buttons.getAll()) {
				if ((b.state == Button.States.ENABLED) || (b.state == Button.States.PRESSED)) {
					if (b.text.equals("Connect") || b.text.equals("Host") || b.text.equals("Send")) {
						b.state = Button.States.PRESSED;
					} else {
						b.state = Button.States.ENABLED;
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_TAB) {
			int index = -1;
			for (int i = 0; i < game.textboxes.getAll().size(); i++) {
				if (game.textboxes.getAll().get(i).state == TextBox.States.SELECTED) {
					game.textboxes.getAll().get(i).state = TextBox.States.ENABLED;
					index = i;
					break;
				}
			}
			boolean selectFirst = true;
			for (int i = index + 1; i < game.textboxes.getAll().size(); i++) {
				if (game.textboxes.getAll().get(i).state == TextBox.States.ENABLED) {
					game.textboxes.getAll().get(i).state = TextBox.States.SELECTED;
					selectFirst = false;
					break;
				}
			}
			if (selectFirst) {
				for (int i = 0; i < game.textboxes.getAll().size(); i++) {
					if (game.textboxes.getAll().get(i).state == TextBox.States.ENABLED) {
						game.textboxes.getAll().get(i).state = TextBox.States.SELECTED;
						break;
					}
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		final TextBox t = game.getSelectedTextBox();
		if (t == null) {
			return;
		}
		if (Font.CHARS.indexOf(e.getKeyChar() + "") >= 0) {
			if (t.text.length() < Constants.MESSAGE_LENGTH) {
				t.text += e.getKeyChar() + "";
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
		if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
			if (t.text.length() > 0) {
				t.text = t.text.substring(0, t.text.length() - 1);
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}
}
