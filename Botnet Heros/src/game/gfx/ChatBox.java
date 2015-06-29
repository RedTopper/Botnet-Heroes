package game.gfx;

import game.utils.Constants;
import game.utils.Renderable;
import game.utils.Tickable;

import java.util.ArrayList;

/**
 * A chat box is a box that organizes and splits up strings into a readable
 * format to be projected on a {@link Screen}.
 *
 * @author AJ
 */
public class ChatBox implements Renderable, Tickable {
	private final ArrayList<String> messages = new ArrayList<>();
	private final ArrayList<Integer> messageLengths = new ArrayList<>();
	private final ArrayList<Integer> sentOnTick = new ArrayList<>();
	private final ArrayList<Integer> color = new ArrayList<>();

	private final int x, y;

	private int ticks;

	/**
	 * Constructs a new chat box
	 *
	 * @param x
	 *            X location in pixels
	 * @param y
	 *            Y location in pixels
	 * @param w
	 *            Width in letters
	 * @param h
	 *            Height in letters
	 */
	public ChatBox(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		BOX_HEIGHT = h;
		BOX_WIDTH = w;
	}

	/**
	 * In messages
	 */
	private final int BOX_HEIGHT;
	/**
	 * In chars
	 */
	private final int BOX_WIDTH;

	@Override
	public void render(Screen screen) {
		for (int i = 0; i < BOX_HEIGHT; i++) {
			try {
				Font.render(screen, messages.get(messages.size() - i - 1), x,
						((BOX_HEIGHT * Font.getHeight(false)) - (i * Font.getHeight(false))) + y,
						color.get(color.size() - i - 1), false);
			} catch (final IndexOutOfBoundsException e) {

			}
		}
	}

	@Override
	public void tick() {
		if (sentOnTick.size() > 0) {
			if (ticks > (sentOnTick.get(0) + Constants.MESSAGE_DISAPPEAR_RATE)) { // blaze
				// it
				for (int i = 0; i < messageLengths.get(0); i++) {
					messages.remove(0);
					color.remove(0);
				}
				messageLengths.remove(0);
				sentOnTick.remove(0);
			}
		}
		ticks++;
	}

	/**
	 * Adds a string to the ChatBox.
	 * 
	 * @param message
	 *            Message to add to the chat box.
	 * @param color
	 *            Color of the message.
	 */
	public void addString(String message, int color) {
		final ArrayList<String> add = new ArrayList<>();
		final String[] split = message.split(" ");
		for (int i = 0; i < split.length; i++) {
			split[i] += " ";
		}
		add.add("");
		int index = 0;
		for (final String word : split) {
			if (word.length() > BOX_WIDTH) { // If the word is too long to fit
				// in the box...
				String remaining = add.remove(index) + word;
				index--;
				while (remaining.length() > BOX_WIDTH) {
					add.add(remaining.substring(0, BOX_WIDTH + 1));
					index++;
					remaining = remaining.substring(BOX_WIDTH);
				}
				add.add(remaining);
				index++;
			} else { // If it can fit and...
				// The previous thing fits also
				if ((add.get(index).length() + word.length()) <= BOX_WIDTH) {
					add.set(index, add.get(index) + word);
					// The previous thing does not fit also...
				} else {
					add.add(word);
					index++;
				}
			}
		}
		for (final String adds : add) {
			messages.add(adds);
			this.color.add(color);
		}
		messageLengths.add(add.size());
		sentOnTick.add(ticks);
	}
}
