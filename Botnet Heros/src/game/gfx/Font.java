package game.gfx;

/**
 * Utilities for drawing a font.
 * 
 * @author AJ
 */
public class Font {

	/**
	 * List of all of the characters that can be typed in the game in order of
	 * the sprite sheet.
	 */
	public static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()      `-=[]\\;',./~_+{}|:\"<>?";

	/**
	 * Renders a font to the given specifications.
	 * 
	 * @param screen
	 *            {@link Screen} to render the font to.
	 * @param str
	 *            Text of the font.
	 * @param x
	 *            X position of the font.
	 * @param y
	 *            Y position of the font.
	 * @param color
	 *            Color of the font.
	 * @param big
	 *            True if the font is big, false otherwise.
	 */
	public static void render(Screen screen, String str, int x, int y, int color, boolean big) {
		if (big) {
			for (int i = 0; i < str.length(); i++) {
				final int dex = Font.CHARS.indexOf(str.substring(i, i + 1));
				if (dex >= 0) {
					screen.render(x + (i * 20), y, "/font.png", dex, color);
				}
			}
		} else {
			for (int i = 0; i < str.length(); i++) {
				final int dex = Font.CHARS.indexOf(str.substring(i, i + 1));
				if (dex >= 0) {
					screen.render(x + (i * 10), y, "/smallfont.png", dex, color);
				}
			}
		}
	}

	/**
	 * Gets the length in pixels of a font.
	 * 
	 * @param str
	 *            String to get the length of
	 * @param big
	 *            True if the font is big, false otherwise.
	 * @return Int in pixels of the width of a font.
	 */
	public static int getLength(String str, boolean big) {
		if (big) {
			return str.length() * 20;
		} else {
			return str.length() * 10;
		}
	}

	/**
	 * Gets the height of a font.
	 * 
	 * @param big
	 *            True if the font is big, false otherwise.
	 * @return The height of the font.
	 */
	public static int getHeight(boolean big) {
		if (big) {
			return 148 / 4; // Height of sheet / 4 rows
		} else {
			return 72 / 4;
		}
	}
}
