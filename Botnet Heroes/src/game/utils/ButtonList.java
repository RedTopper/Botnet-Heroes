package game.utils;

import game.Game;
import game.gfx.Button;

import java.util.ArrayList;

/**
 * List of buttons and Enums representing all of the buttons in the game.
 * @author AJ
 */
public class ButtonList {
	private final ArrayList<Button> buttons = new ArrayList<>();
	private final ArrayList<Game.BN> buttonnames = new ArrayList<>();

	/**
	 * Adds a unique button to the game.
	 * @param b Button to add.
	 * @param name Name to add (MUST NOT ALREADY BE ADDED!!!!).
	 */
	public void add(Button b, Game.BN name) {
		boolean adding = true;
		for (final Game.BN names : buttonnames) {
			if (names == name) {
				Debug.out(Out.SEVERE, "game.utils.ButtonList", "Name not unique! Abort!");
				adding = false;
			}
		}
		if (adding) {
			buttons.add(b);
			buttonnames.add(name);
		}
	}

	/**
	 * Gets a button under an enum name.
	 * @param name Enum to look up.
	 * @return Button object.
	 */
	public Button get(Game.BN name) {
		int index = 0;
		for (final Game.BN names : buttonnames) {
			if (name == names) {
				return buttons.get(index);
			}
			index++;
		}
		return null;
	}

	/**
	 * Gets an array list of all of the buttons.
	 * @return Buttons!
	 */
	public ArrayList<Button> getAll() {
		return buttons;
	}

}