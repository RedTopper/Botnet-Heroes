package game.utils;

import game.gfx.Screen;

/**
 * Contains a single method used to declare an object as renderable to a screen.
 * 
 * @author AJ
 */
public interface Renderable {

	/**
	 * This Object can be rendered to a screen.
	 * 
	 * @param screen
	 *            {@link Screen} to render the object to.
	 */
	public abstract void render(Screen screen);
}
