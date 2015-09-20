package game.utils;

import game.Game;

import javax.swing.JOptionPane;

public class Debug {

	/**
	 * System output.
	 *
	 * @param level
	 *            Enum Type level
	 * @param name
	 *            The name of the class (Variable should be called CLASS!)
	 * @param msg
	 *            Message to send to the log.
	 * @return Formatted string.
	 */
	public static String out(Out level, String name, String msg) {
		switch (Game.debugLevel) {
		default:
		case TRACE:
			if (level == Out.TRACE) {
				System.out.println("[TRACE][" + name + "] " + msg);
				return "[TRACE][" + name + "] " + msg;
			}
		case DEBUG:
			if (level == Out.DEBUG) {
				System.out.println("[DEBUG][" + name + "] " + msg);
				return "[DEBUG][" + name + "] " + msg;
			}
		case INFO:
			if (level == Out.INFO) {
				System.out.println("[INFO][" + name + "] " + msg);
				return "[INFO][" + name + "] " + msg;
			}
		case WARNING:
			if (level == Out.WARNING) {
				System.out.println("[WARNING][" + name + "] " + msg);
				return "[WARNING][" + name + "] " + msg;
			}
		case SEVERE:
			if (level == Out.SEVERE) {
				System.err.println();
				System.err.println("Oh no! The game has crashed. Here is some"
						+ " debug information to send to the devs.");
				System.err.println("[SEVERE][" + name + "] " + msg);
				JOptionPane.showMessageDialog(null, "[SEVERE][" + name + "] " + msg, "ERROR: " + "Crash!",
						JOptionPane.INFORMATION_MESSAGE);
				Runtime.getRuntime().halt(0); // CRASH IT
			}
		}
		return "";
	}
}
