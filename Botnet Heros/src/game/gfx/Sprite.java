package game.gfx;

import game.utils.Debug;
import game.utils.Out;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

/**
 * A single, standalone sprite.
 * 
 * @author AJ
 */
public class Sprite {
	/**
	 * The local path of the sprite sheet within the JAR file.
	 */
	public String path;

	/**
	 * Dimensions of the sprite sheet.
	 */
	public int width, height;

	/**
	 * One dimensional array containing all of the pixel data from the sprite
	 * sheet.
	 */
	public int[] pixels;

	/**
	 * Creates a {@link Sprite} object.
	 *
	 * @param path
	 *            {@link String} location of the file in the JAR.
	 */
	public Sprite(String path) {
		BufferedImage image = null;

		// try to load image (not found throws an exception).
		try {
			image = ImageIO.read(Sprite.class.getResourceAsStream(path));
		} catch (final Exception e) {
			e.printStackTrace();
			Debug.out(Out.SEVERE, "game.gfx.Sprite", "Failed to read " + path + "!");
		}

		if (image == null) {
			return;
		}

		// sets up path and vars.
		this.path = path;
		width = image.getWidth();
		height = image.getHeight();

		// makes the RGB image contain an RGB image
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
		Debug.out(Out.DEBUG, "game.gfx.Sprite", "Sprite map " + path + " loaded.");
	}

	/**
	 * Creates a {@link Sprite} object.
	 * 
	 * @param pixels
	 *            Array of pixels.
	 * @param width
	 *            Width of the object
	 * @param height
	 *            Height of the object.
	 */
	public Sprite(int[] pixels, int width, int height) {
		path = null;
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}
}
