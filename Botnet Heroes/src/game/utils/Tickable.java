package game.utils;

/**
 * Contains a single method declaring this object as tickable. This means the
 * object contains a method that will update its logic.
 * 
 * @author AJ
 *
 */
public interface Tickable {

	/**
	 * Updates the logic of this Object.
	 */
	public abstract void tick();
}
