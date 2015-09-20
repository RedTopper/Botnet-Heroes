package game.gfx;

public class TextBox {

	private final int MAX_CHARS;

	private boolean tooltip = true;

	private final Screen screen;

	private final int x, y, w, h;

	private final boolean big;

	public States state = States.HIDDEN;

	public String text;

	private final String backgroundtext;

	public TextBox(Screen screen, int x, int y, int w, boolean big, String backgroundtext) {
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.w = w;
		this.big = big;
		h = Font.getHeight(big) + 4;
		MAX_CHARS = (w / Font.getLength("A", big)) - 1;
		this.backgroundtext = backgroundtext;
	}

	public void render() {
		int color = 0x0;
		switch (state) {
		default:
		case HIDDEN:
			break;
		case SELECTED:
			color = 0xFFE0E0E0;
			if (tooltip) {
				text = "";
				tooltip = false;
			}
			break;
		case ENABLED:
			color = 0xFFA0A0A0;
			break;
		case DISABLED:
			color = 0xFF737373;
			break;
		}
		if (state != States.HIDDEN) {
			if (big) {
				for (int width = 0; width < w; width++) {
					for (int height = 0; height < h; height++) {
						if ((width < 2) || (height < 2) || (width > (w - 3)) || (height > (h - 3))) {
							screen.pixels[x + width + ((y + height) * screen.width)] = 0xFF000000;
						} else {
							screen.pixels[x + width + ((y + height) * screen.width)] = color - (height * 0xFF030303);
						}
					}
				}
				if ((text != null) && !text.equals("")) {
					if (MAX_CHARS < text.length()) {
						Font.render(screen, text.substring(text.length() - MAX_CHARS), x + 4, y + 4, 0xFF000000, big);
					} else {
						Font.render(screen, text, x + 4, y + 4, 0xFF000000, big);
					}
				} else {
					Font.render(screen, backgroundtext, x + 4, y + 3, 0xFF404040, big);
				}
			} else {
				for (int width = 0; width < w; width++) {
					for (int height = 0; height < h; height++) {
						if ((width == 0) || (height == 0) || (width == (w - 1)) || (height == (h - 1))) {
							screen.pixels[x + width + ((y + height) * screen.width)] = 0xFF000000;
						} else {
							screen.pixels[x + width + ((y + height) * screen.width)] = color - (height * 0xFF030303);
						}
					}
				}
				if ((text != null) && !text.equals("")) {
					if (MAX_CHARS < text.length()) {
						Font.render(screen, text.substring(text.length() - MAX_CHARS), x + 4, y + 3, 0xFF000000, big);
					} else {
						Font.render(screen, text, x + 4, y + 3, 0xFF000000, big);
					}
				} else {
					Font.render(screen, backgroundtext, x + 4, y + 3, 0xFF404040, big);
				}
			}
		}
	}

	/**
	 * @return the x position of this text box
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y position of this text box
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return the width of this text box.
	 */
	public int getW() {
		return w;
	}

	/**
	 * @return the height of this text box.
	 */
	public int getH() {
		return h;
	}

	public enum States {
		/**
		 * The text box is visible, but cannot be used.
		 */
		DISABLED,

		/**
		 * The text box can be used.
		 */
		ENABLED,

		/**
		 * The text box is selected and being typed in.
		 */
		SELECTED,

		/**
		 * The text box is hidden from view.
		 */
		HIDDEN
	}
}
