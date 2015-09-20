package game.gfx;

import game.utils.Debug;
import game.utils.Out;

import java.util.ArrayList;

/**
 * A screen contains an array of pixel ints that are used to draw onto a canvas.
 * It also imports all of the textures when the object is created to guarantee
 * availability during runtime.
 * 
 * @author AJ
 *
 */
public class Screen {

	/**
	 * A one dimensional array containing ints that represent pixels on the
	 * screen.
	 */
	public final int[] pixels;

	/**
	 * Width and height of this Screen.
	 */
	public int width, height;

	private final ArrayList<Sprite> sprites = new ArrayList<Sprite>();

	private final ArrayList<SpriteSheet> spritesheets = new ArrayList<SpriteSheet>();

	/**
	 * Creates a screen with some width and height in pixels.
	 * 
	 * @param width
	 *            Pixels wide.
	 * @param height
	 *            Pixels tall.
	 */
	public Screen(int width, int height) {
		// Sprites. Added here to guarantee their availability at runtime.
		sprites.add(new Sprite("/icon.png"));
		sprites.add(new Sprite("/background.png"));
		sprites.add(new Sprite("/game.png"));

		// Sprite sheets. Added here to guarantee their availability at runtime.
		spritesheets.add(new SpriteSheet("/font.png", 26, 4));
		spritesheets.add(new SpriteSheet("/smallfont.png", 26, 4));
		spritesheets.add(new SpriteSheet("/progress.png", 3, 3));
		spritesheets.add(new SpriteSheet("/noprogress.png", 3, 3));
		spritesheets.add(new SpriteSheet("/button_enabled.png", 3, 3));
		spritesheets.add(new SpriteSheet("/button_disabled.png", 3, 3));
		spritesheets.add(new SpriteSheet("/button_pressed.png", 3, 3));
		spritesheets.add(new SpriteSheet("/logo.png", 1, 4));
		spritesheets.add(new SpriteSheet("/Jerry_The_Icecream_Man.png", 2, 2));
		spritesheets.add(new SpriteSheet("/Jerry_The_Icecream_Man_Hurt.png", 1, 1));
		spritesheets.add(new SpriteSheet("/Jerry_The_Icecream_Man_Death.png", 2, 2));
		spritesheets.add(new SpriteSheet("/poof.png", 4, 2));
		spritesheets.add(new SpriteSheet("/click.png", 4, 2));
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}

	/**
	 * Renders some sprite onto the screen. The path of this method must match
	 * one of the paths in the "sprites" list.
	 * 
	 * @param xPos
	 *            X position to render.
	 * @param yPos
	 *            Y position to render.
	 * @param path
	 *            Path of the sprite in the "sprites" array list.
	 */
	public void render(int xPos, int yPos, String path) {
		final Sprite sprite = lookupSprite(path);
		for (int y = 0; y < sprite.height; y++) {
			for (int x = 0; x < sprite.width; x++) {
				final int pix = sprite.pixels[x + (y * sprite.width)];
				if ((pix != 0) && ((xPos + x) < width) && ((xPos + x) >= 0) && ((yPos + y) < height)
						&& ((yPos + y) >= 0)) {
					pixels[xPos + x + ((yPos + y) * width)] = pix;
				}
			}
		}
	}

	/**
	 * Renders some sprite onto the screen. The path of this method must match
	 * one of the paths in the "spritesheets" list. The number must be in range
	 * of the total number of sprites of that sprite sheet.
	 * 
	 * @param xPos
	 *            X position to render.
	 * @param yPos
	 *            Y position to render.
	 * @param path
	 *            Path of the sprite in the "spritesheets" array list.
	 * @param num
	 *            Index of the sprite.
	 */
	public void render(int xPos, int yPos, String path, int num) {
		final Sprite sprite = lookupSprite(path, num);
		for (int y = 0; y < sprite.height; y++) {
			for (int x = 0; x < sprite.width; x++) {
				final int pix = sprite.pixels[x + (y * sprite.width)];
				if ((pix != 0) && ((xPos + x) < width) && ((xPos + x) >= 0) && ((yPos + y) < height)
						&& ((yPos + y) >= 0)) {
					pixels[xPos + x + ((yPos + y) * width)] = pix;
				}
			}
		}
	}

	/**
	 * Renders some sprite onto the screen. The path of this method must match
	 * one of the paths in the "spritesheets" list. The number must be in range
	 * of the total number of sprites of that sprite sheet.
	 * 
	 * @param xPos
	 *            X position to render.
	 * @param yPos
	 *            Y position to render.
	 * @param path
	 *            Path of the sprite in the "spritesheets" array list.
	 * @param num
	 *            Index of the sprite.
	 * @param color
	 *            Color that replaces all non transparent pixels.
	 */
	public void render(int xPos, int yPos, String path, int num, int color) {
		final Sprite sprite = lookupSprite(path, num);
		for (int y = 0; y < sprite.height; y++) {
			for (int x = 0; x < sprite.width; x++) {
				final int pix = sprite.pixels[x + (y * sprite.width)];
				if ((pix != 0) && ((xPos + x) < width) && ((xPos + x) >= 0) && ((yPos + y) < height)
						&& ((yPos + y) >= 0)) {
					pixels[xPos + x + ((yPos + y) * width)] = color;
				}
			}
		}
	}

	/**
	 * Returns a single sprite looked up from a specific path.
	 * 
	 * @param path
	 *            Path of the sprite.
	 * @return Sprite representation of that path. Crashes otherwise.
	 */
	public Sprite lookupSprite(String path) {
		int index = 0;
		for (final Sprite s : sprites) {
			if (s.path.equals(path)) {
				return sprites.get(index);
			}
			index++;
		}
		Debug.out(Out.SEVERE, "game.gfx.Screen", "The index of the Sprite was not found"
				+ "Please bring this to the developers attention ASAP!");
		return null; // CAUSES PROBLEMS!!! DO NOT REACH!!!!
	}

	/**
	 * Returns a single sprite looked up from a specific path.
	 * 
	 * @param path
	 *            Path of the sprite.
	 * @param num
	 *            Number of the sprite on the sprite sheet.
	 * @return Sprite representation of that path. Crashes otherwise.
	 */
	public Sprite lookupSprite(String path, int num) {
		int index = 0;
		for (final SpriteSheet s : spritesheets) {
			if (s.path.equals(path)) {
				return spritesheets.get(index).get(num);
			}
			index++;
		}
		Debug.out(Out.SEVERE, "game.gfx.Screen", "The index of the Sprite was not found"
				+ "Please bring this to the developers attention ASAP!");
		return null;
	}
}
