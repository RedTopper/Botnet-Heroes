package game.utils;

/**
 * Static enum used for debugging.
 */
public enum Out {
	/**
	 * Will display info about movement and server pings. Produces lots of
	 * output. Also outputs any levels higher than trace.
	 */
	TRACE,

	/**
	 * Will display detailed info about the current running game. Only intended
	 * for developers to figure out what broke. Also outputs any levels higher
	 * than debug.
	 */
	DEBUG,

	/**
	 * Will display standard events that happen within the game. Intended for
	 * release of buggy builds. Also outputs any levels higher than info.
	 */
	INFO,

	/**
	 * Limited output for errors that allow the program to continue. Also
	 * outputs any levels higher than warning.
	 */
	WARNING,

	/**
	 * The highest debug level. Using this warning will cause the system to
	 * crash the game.
	 */
	SEVERE;
}