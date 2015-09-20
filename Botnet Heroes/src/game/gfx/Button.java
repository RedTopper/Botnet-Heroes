package game.gfx;

import game.utils.Renderable;

/**
 * A button is a class that helps construct a clickable button in game.
 * 
 * @author AJ
 */
public class Button implements Renderable {

	private final int x, y, w, h, tileWidth, tileHeight;

	private final String enabled, disabled, pressed;

	/**
	 * Text that appears within the button.
	 */
	public String text;

	/**
	 * Current state of the button.
	 */
	public States state = States.HIDDEN;

	private States previousState = States.HIDDEN;

	private boolean clicked = false;

	/**
	 * Creates a new button.
	 *
	 * @param screen
	 *            The screen to render to.
	 * @param x
	 *            X position to render the button (Top left)
	 * @param y
	 *            Y position to render the button (Top left)
	 * @param w
	 *            The width of the button in units (NOT PIXELS).
	 * @param h
	 *            The height of the button in units (NOT PIXELS).
	 * @param enabled
	 *            Path to the enabled button image.
	 * @param disabled
	 *            Path to the disabled button image.
	 * @param pressed
	 *            Path to the pressed button image.
	 */
	public Button(Screen screen, int x, int y, int w, int h, String enabled, String disabled, String pressed) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.enabled = enabled;
		this.disabled = disabled;
		this.pressed = pressed;
		// Hopefully the programmer made all of the sprites the same.
		tileWidth = screen.lookupSprite(disabled, 0).width;
		tileHeight = screen.lookupSprite(disabled, 0).height;
	}

	@Override
	public void render(Screen screen) {
		for (int width = 0; width < w; width++) {
			for (int height = 0; height < h; height++) {
				switch (state) {
				default:
				case DISABLED:
					if (width == 0) {
						if (height == 0) {
							screen.render(x, y, disabled, 0);
						} else if (height == (h - 1)) {
							screen.render(x, y + (height * tileHeight), disabled, 6);
						} else {
							screen.render(x, y + (height * tileHeight), disabled, 3);
						}
					} else if (width == (w - 1)) {
						if (height == 0) {
							screen.render(x + (width * tileWidth), y, disabled, 2);
						} else if (height == (h - 1)) {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), disabled, 8);
						} else {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), disabled, 5);
						}
					} else {
						if (height == 0) {
							screen.render(x + (width * tileWidth), y, disabled, 1);
						} else if (height == (h - 1)) {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), disabled, 7);
						} else {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), disabled, 4);
						}
					}
					break;
				case PRESSED:
					if (width == 0) {
						if (height == 0) {
							screen.render(x, y, pressed, 0);
						} else if (height == (h - 1)) {
							screen.render(x, y + (height * tileHeight), pressed, 6);
						} else {
							screen.render(x, y + (height * tileHeight), pressed, 3);
						}
					} else if (width == (w - 1)) {
						if (height == 0) {
							screen.render(x + (width * tileWidth), y, pressed, 2);
						} else if (height == (h - 1)) {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), pressed, 8);
						} else {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), pressed, 5);
						}
					} else {
						if (height == 0) {
							screen.render(x + (width * tileWidth), y, pressed, 1);
						} else if (height == (h - 1)) {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), pressed, 7);
						} else {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), pressed, 4);
						}
					}
					break;
				case OUT:
				case ENABLED:
					if ((previousState == States.PRESSED) && (state != States.OUT)) {
						clicked = true;
					}
					if (width == 0) {
						if (height == 0) {
							screen.render(x, y, enabled, 0);
						} else if (height == (h - 1)) {
							screen.render(x, y + (height * tileHeight), enabled, 6);
						} else {
							screen.render(x, y + (height * tileHeight), enabled, 3);
						}
					} else if (width == (w - 1)) {
						if (height == 0) {
							screen.render(x + (width * tileWidth), y, enabled, 2);
						} else if (height == (h - 1)) {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), enabled, 8);
						} else {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), enabled, 5);
						}
					} else {
						if (height == 0) {
							screen.render(x + (width * tileWidth), y, enabled, 1);
						} else if (height == (h - 1)) {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), enabled, 7);
						} else {
							screen.render(x + (width * tileWidth), y + (height * tileHeight), enabled, 4);
						}
					}
					break;
				case HIDDEN:
					break;
				}
				previousState = state;
			}
		}
		if ((text != null) && (state != States.HIDDEN)) {
			Font.render(screen, text, (x + ((w * tileWidth) / 2)) - (Font.getLength(text, false) / 2),
					((y + ((h * tileHeight) / 2)) - (Font.getHeight(false) / 2)) + 1, 0xFF000000, false);
		}
	}

	/**
	 * Gets the X value of this button.
	 *
	 * @return The x.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the Y value of this button.
	 *
	 * @return The y.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the width of the button IN UNITS! See the getTileWidth() method to
	 * get the width in pixels of each unit.
	 *
	 * @return The width.
	 * @see Button#getTileWidth()
	 */
	public int getW() {
		return w;
	}

	/**
	 * Gets the height of the button IN UNITS! See the getTileHeight() method to
	 * get the height in pixels of each unit.
	 *
	 * @return The width.
	 * @see Button#getTileWidth()
	 */
	public int getH() {
		return h;
	}

	/**
	 * Gets the width in pixels each unit is.
	 *
	 * @return Pixels of one unit.
	 */
	public int getTileWidth() {
		return tileWidth;
	}

	/**
	 * Gets the height in pixels each unit is.
	 *
	 * @return Pixels of one unit.
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	/**
	 * Determines if the button has been clicked on. The first time this method
	 * is called after a click, it will return true. Any other times, this
	 * method returns false.
	 *
	 * @return true if clicked once, false any other time.
	 */
	public boolean isClicked() {
		if (clicked) {
			clicked = false;
			return true;
		}
		return false;
	}

	public enum States {
		/**
		 * The button is visible, but cannot be clicked.
		 */
		DISABLED,

		/**
		 * The button can be clicked.
		 */
		ENABLED,

		/**
		 * The button is being clicked on.
		 */
		PRESSED,

		/**
		 * The button is hidden from view.
		 */
		HIDDEN,

		/**
		 * The button was clicked, but the user dragged it out of the button.
		 */
		OUT
	}
}
