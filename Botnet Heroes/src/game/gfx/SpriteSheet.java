package game.gfx;

import game.utils.Debug;
import game.utils.Out;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * Creates a one dimensional array of {@link Sprite}s.
 * 
 * @author AJ
 */
public class SpriteSheet {
	/**
	 * The local path of the sprite sheet within the JAR file.
	 */
	public String path;

	private final ArrayList<Sprite> sprites = new ArrayList<Sprite>();

	/**
	 * Creates a {@link SpriteSheet} object.
	 *
	 * @param path
	 *            {@link String} location of the file in the JAR.
	 * @param widthTiles
	 *            Width of the SpriteSheet in tiles.
	 * @param heightTiles
	 *            Height of the SpriteSheet in tiles.
	 */
	public SpriteSheet(String path, int widthTiles, int heightTiles) {
		BufferedImage image = null;

		// try to load image (not found throws an exception).
		try {
			image = ImageIO.read(Sprite.class.getResourceAsStream(path));
		} catch (final Exception e) {
			e.printStackTrace();
			Debug.out(Out.SEVERE, "game.gfx.Sprite", "Failed to load " + path + "!");
		}

		if (image == null) {
			return;
		}

		// sets up path and vars.
		this.path = path;
		final int fullWidth = image.getWidth();
		final int fullHeight = image.getHeight();
		final int width = fullWidth / widthTiles;
		final int height = fullHeight / heightTiles;

		final int[] pixels = image.getRGB(0, 0, fullWidth, fullHeight, null, 0, fullWidth);

		for (int ya = 0; ya < heightTiles; ya++) {
			for (int xa = 0; xa < widthTiles; xa++) {
				final int[] pix = new int[width * height];
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						pix[x + (y * width)] = pixels[((xa * width) + x) + ((((ya * height) + y) * fullWidth))];
					}
				}
				sprites.add(new Sprite(pix, width, height));
			}
		}
		Debug.out(Out.DEBUG, "game.gfx.SpriteSheet", "SpriteSheet " + path + " loaded.");
	}

	/**
	 * Gets a {@link Sprite} from this SpriteSheet.
	 * 
	 * @param i
	 *            Index.
	 * @return Sprite.
	 */
	public Sprite get(int i) {
		return sprites.get(i);
	}
}
