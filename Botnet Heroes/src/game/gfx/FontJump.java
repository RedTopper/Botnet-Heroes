package game.gfx;

import game.utils.Renderable;
import game.utils.Tickable;

/**
 * Utilities for making a font appear to "Jump" into the air.
 * 
 * @author AJ
 */
public class FontJump implements Tickable, Renderable {

	private double x;
	private double y;

	private final double sx;
	private final double sy;

	private double v0x;
	private double v0y;
	private double totalTime;
	private double dt;
	private final int steps;

	private final boolean size;
	private final String text;

	private static final double ACCELERATION = -9.81;

	private int tick = 0;

	private boolean done = false;

	/**
	 * Creates a font jump.
	 * 
	 * @param x
	 *            X position to render the font (At the start).
	 * @param y
	 *            Y position to render the font (At the start).
	 * @param text
	 *            Text to show for the font.
	 * @param direction
	 *            Direction (0-180) the font moves.
	 * @param vilocity
	 *            Speed the font moves.
	 * @param steps
	 *            Amount of steps the font should move.
	 * @param size
	 *            Size of the font.
	 */
	public FontJump(int x, int y, String text, int direction, double vilocity, int steps, boolean size) {
		this.x = x;
		this.y = y;
		sx = x;
		sy = y;
		this.size = size;
		this.text = text;
		this.steps = steps;
		table(vilocity, direction, steps);
		tick();
	}

	@Override
	public void tick() {
		if (tick <= (steps + 120)) {
			final double time = tick * dt;
			x = -(tick * v0x * dt) + sx;
			y = -(FontJump.displacement(v0y, time, FontJump.ACCELERATION)) + sy;
			tick++;
		} else {
			done = true;
		}
	}

	@Override
	public void render(Screen screen) {
		Font.render(screen, text, (int) x, (int) y, 0xFFFF0000, size);
	}

	private static double displacement(double v0, double t, double a) {
		return (v0 * t) + (0.5 * a * t * t);
	}

	private void table(double v0, double angle, int steps) {
		v0x = v0 * Math.cos(Math.toRadians(angle));
		v0y = v0 * Math.sin(Math.toRadians(angle));
		totalTime = (-2.0 * v0y) / FontJump.ACCELERATION;
		dt = totalTime / steps;
	}

	public boolean isDone() {
		return done;
	}
}
