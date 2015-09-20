package game.mobs;

import game.gfx.FontJump;
import game.gfx.ProgressBar;
import game.gfx.Screen;
import game.utils.Renderable;
import game.utils.Tickable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Mob implements Renderable, Tickable{

	private final String idle;
	private final String clicked;
	private final String dead;
	private final int idleFrames;
	private final int clickedFrames;
	private final int deadFrames;
	private final int framerate;

	private double hp;
	private final double init_hp;

	private final ProgressBar healthBar;

	private int frame;
	private int animation;

	private final int x;
	private final int y;
	private final int w;
	private final int h;

	private boolean pressed;

	private int ticks;

	private final CopyOnWriteArrayList<FontJump> jumping = new CopyOnWriteArrayList<>();
	
	private boolean justDied = false;

	public Mob(String idle, String clicked, String dead, int idleFrames, int clickedFrames, int deadFrames,
			int framerate, int id, double hp) {
		this.idle = idle;
		this.clicked = clicked;
		this.dead = dead;
		this.idleFrames = idleFrames;
		this.clickedFrames = clickedFrames;
		this.deadFrames = deadFrames;
		this.framerate = framerate;
		w = 96;
		h = 96;
		this.hp = hp;
		init_hp = hp;
		if (id == 0) {
			x = 188;
			y = 105;
		} else if (id == 1) {
			x = 286;
			y = 40;
		} else if (id == 2) {
			x = 384;
			y = 105;
		} else {
			x = 0;
			y = 0;
		}
		healthBar = new ProgressBar(x, y - (8 * 3), w, 8 * 3);
		if (hp > 999D) {
			final NumberFormat formatter = new DecimalFormat("0E0");
			formatter.setMinimumFractionDigits(2);
			healthBar.setString(formatter.format(hp));
		} else {
			healthBar.setString((hp + "").substring(0, (hp + "").indexOf(".")));
		}
		healthBar.setProgress((int) ((hp / init_hp) * 100));
	}

	public void render(Screen screen) {
		if (animation == 0) { // Render idle
			if (frame >= idleFrames) {
				frame = 0;
			}
			screen.render(getX(), getY(), idle, frame);
		}
		if (animation == 1) { // Render idle
			if (frame >= clickedFrames) {
				frame = 0;
				changeToIdle();
			}
			screen.render(getX(), getY(), clicked, frame);
		}
		if (animation == 2) { // Render idle
			if (frame >= deadFrames) {
				frame = 0;
				animation = 3;
			}
			if (animation == 2) {
				screen.render(getX(), getY(), dead, frame);
			}
		}
		healthBar.render(screen);
		for (final FontJump j : jumping) {
			j.render(screen);
		}
		for (int i = 0; i < jumping.size(); i++) {
			if (jumping.get(i).isDone()) {
				jumping.remove(i);
				i--;
			}
		}
	}

	public void tick() {
		ticks++;
		if (ticks >= framerate) {
			frame++;
			ticks = 0;
		}
		for (final FontJump j : jumping) {
			j.tick();
		}
		if (hp <= 0D) {
			healthBar.setString("Rekt");
		}
	}

	/**
	 * Gets the type of this monster as an integer. Make sure to update this
	 * method with the Mob class!
	 * 
	 * @return Int of the type of monster that object is.
	 */
	public abstract int getType();

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	private void changeToIdle() {
		animation = 0;
		frame = 0;
	}

	public void changeToClicked() {
		animation = 1;
		frame = 0;
		jumping.add(new FontJump(x + (96 / 2), y + (96 / 2), "1", (int) (Math.random() * 80D) + 50, 45, 60, false));
	}

	private void changeToDead() {
		animation = 2;
		frame = 0;
		justDied = true;
	}

	public boolean getPressed() {
		final boolean temp = pressed;
		pressed = false;
		return temp;
	}

	public double getHealth() {
		return hp;
	}

	public double getOriginalHp() {
		return init_hp;
	}

	public void setHealth(double hp) {
		if (this.hp > 0D) {
			this.hp = hp;
			if (!(this.hp > 0D)) {
				this.hp = 0D;
				changeToDead();
			}
			if (hp > 999D) {
				final NumberFormat formatter = new DecimalFormat("0E0");
				formatter.setMinimumFractionDigits(2);
				healthBar.setString(formatter.format(hp));
			} else {
				healthBar.setString((hp + "").substring(0, (hp + "").indexOf(".")));
			}
			healthBar.setProgress((int) ((hp / init_hp) * 100));
		}
	}
	
	public boolean justDied() {
		boolean dead = justDied;
		justDied = false;
		return dead;
	}

	/**
	 * Creates and returns a mob based on a passed int.
	 *
	 * 1: Jerry The Ice Cream Man
	 *
	 * @param idToMake
	 *            The Id (See above) to make.
	 * @param id
	 *            The position the mob will be in.
	 * @param health
	 *            The max health of the mob.
	 * @return A newly created mob based on an id.
	 */
	public static Mob createBasedOnId(int idToMake, int id, double health) {
		if (idToMake == 1) {
			return new JerryTheIceCreamMan(id, health);
		}
		return null;
	}
}
