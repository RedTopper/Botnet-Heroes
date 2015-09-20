package game.utils;

import java.awt.Dimension;

public class Constants {

	/**
	 * The amount of commands (clicks) that can be sent in one packet.
	 */
	public static final int COMMANDS_PER_PACKET = 7;

	/**
	 * Max packets a second a server can resolve. It should be at least one
	 * above 6 (How many packets sent a second) and one for network overhead.
	 */
	public static final int PACKETS_PER_SECOND = 7;

	public static final Dimension SERVER_MIN_SIZE = new Dimension(640, (640 / 16) * 9);
	public static final Dimension SERVER_PREFERRED_SIZE = new Dimension(896, (896 / 16) * 9);
	public static final Dimension SERVER_MAX_SIZE = new Dimension(1280, (1280 / 16) * 9);
	public static final int STATS_HEIGHT = 11;
	public static final int STATS_WIDTH = 34;
	public static final int FONT_PT = 12;

	/**
	 * Lengths of things (in chars).
	 */
	public static final int USERNAME_LENGTH = 15, MESSAGE_LENGTH = 140, SERVER_LENGTH = 200;

	/*
	 * Units of time in ticks.
	 */
	public static final int ONE_SECOND = 60;
	public static final int ONE_SIXTH_SECOND = 10;
	public static final int MESSAGE_DISAPPEAR_RATE = 420;

	/**
	 * Game port.
	 */
	public static final int PORT = 63326;

	/**
	 * Mob lanes.
	 */
	public static final int LANES = 5;

	/**
	 * For your sanity, keep this at 3!
	 */
	public static final int MOBS_PER_LANE = 3;

	private Constants() {
		throw new AssertionError("You cannot create this class!");
	}

}
