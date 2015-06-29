package game.gfx;

import game.utils.Renderable;

/**
 * Used for showing the progress of actions.
 * 
 * @author AJ
 */
public class ProgressBar implements Renderable {

	private final int x, y, w, h;

	private double progress;

	private String text;

	/**
	 * Constructs a new progress bar given the following parameters.
	 *
	 * @param x
	 *            The X position on the screen
	 * @param y
	 *            The Y position on the screen
	 * @param w
	 *            The width IN PIXELS of the progress bar
	 * @param h
	 *            The height IN PIXELS of the progress bar
	 */
	public ProgressBar(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w / 3;
		this.h = h / 3;
	}

	@Override
	public void render(Screen screen) {
		for (int width = 0; width < w; width++) {
			for (int height = 0; height < h; height++) {
				if (width < (w * (progress / 99D))) {
					if (width == 0) {
						if (height == 0) {
							screen.render(x, y, "/progress.png", 0);
						} else if (height == (h - 1)) {
							screen.render(x, y + (height * 3), "/progress.png", 6);
						} else {
							screen.render(x, y + (height * 3), "/progress.png", 3);
						}
					} else if (width == (w - 1)) {
						if (height == 0) {
							screen.render(x + (width * 3), y, "/progress.png", 2);
						} else if (height == (h - 1)) {
							screen.render(x + (width * 3), y + (height * 3), "/progress.png", 8);
						} else {
							screen.render(x + (width * 3), y + (height * 3), "/progress.png", 5);
						}
					} else {
						if (height == 0) {
							screen.render(x + (width * 3), y, "/progress.png", 1);
						} else if (height == (h - 1)) {
							screen.render(x + (width * 3), y + (height * 3), "/progress.png", 7);
						} else {
							screen.render(x + (width * 3), y + (height * 3), "/progress.png", 4);
						}
					}
				} else {
					if (width == 0) {
						if (height == 0) {
							screen.render(x, y, "/noprogress.png", 0);
						} else if (height == (h - 1)) {
							screen.render(x, y + (height * 3), "/noprogress.png", 6);
						} else {
							screen.render(x, y + (height * 3), "/noprogress.png", 3);
						}
					} else if (width == (w - 1)) {
						if (height == 0) {
							screen.render(x + (width * 3), y, "/noprogress.png", 2);
						} else if (height == (h - 1)) {
							screen.render(x + (width * 3), y + (height * 3), "/noprogress.png", 8);
						} else {
							screen.render(x + (width * 3), y + (height * 3), "/noprogress.png", 5);
						}
					} else {
						if (height == 0) {
							screen.render(x + (width * 3), y, "/noprogress.png", 1);
						} else if (height == (h - 1)) {
							screen.render(x + (width * 3), y + (height * 3), "/noprogress.png", 7);
						} else {
							screen.render(x + (width * 3), y + (height * 3), "/noprogress.png", 4);
						}
					}
				}
			}
		}
		if (text != null) {
			Font.render(screen, text, (x + ((w * 3) / 2)) - (Font.getLength(text, false) / 2), (y + ((h * 3) / 2))
					- (Font.getHeight(false) / 2), 0xFF000000, false);
		}
	}

	/**
	 * Sets the progress of the progress bar (0.0 to 100.0)
	 * 
	 * @param progress
	 *            Progress of the bar.
	 */
	public void setProgress(double progress) {
		this.progress = progress;
	}

	/**
	 * Obtains the current progress.
	 * 
	 * @return Double 0.0-100.0
	 */
	public double getProgress() {
		return progress;
	}

	/**
	 * Sets the string of this progress bar.
	 * 
	 * @param str
	 *            Text.
	 */
	public void setString(String str) {
		text = str;
	}
}
