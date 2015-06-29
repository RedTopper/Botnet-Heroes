package game.utils;

import game.Game;
import game.gfx.TextBox;

import java.util.ArrayList;

/**
 * List of text boxes and Enums representing all of the text boxes in the game.
 * @author AJ
 */
public class TextBoxList {
	private final ArrayList<TextBox> textboxes = new ArrayList<>();
	private final ArrayList<Game.TN> textboxnames = new ArrayList<>();

	/**
	 * Adds a unique text box to the game.
	 * @param t Text box to add.
	 * @param name Name to add (MUST NOT ALREADY BE ADDED!!!!).
	 */
	public void add(TextBox t, Game.TN name) {
		boolean adding = true;
		for (final Game.TN names : textboxnames) {
			if (names == name) {
				Debug.out(Out.SEVERE, "game.utils.TextBoxList", "Name not unique! Abort!");
				adding = false;
			}
		}
		if (adding) {
			textboxes.add(t);
			textboxnames.add(name);
		}
	}

	/**
	 * Gets a text box under an enum name.
	 * @param name Enum to look up.
	 * @return Text box object.
	 */
	public TextBox get(Game.TN name) {
		int index = 0;
		for (final Game.TN names : textboxnames) {
			if (name == names) {
				return textboxes.get(index);
			}
			index++;
		}
		return null;
	}

	/**
	 * Gets an array list of all of the Text boxes.
	 * @return Text!
	 */
	public ArrayList<TextBox> getAll() {
		return textboxes;
	}

}
