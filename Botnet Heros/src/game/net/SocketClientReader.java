package game.net;

import game.Game;
import game.mobs.Mob;
import game.net.packets.P00Login;
import game.net.packets.P01Quit;
import game.net.packets.P02Chat;
import game.net.packets.P03Damage;
import game.net.packets.P04Update;
import game.net.packets.Packet;
import game.net.packets.Packet.PacketTypes;
import game.utils.Constants;
import game.utils.Debug;
import game.utils.Out;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class SocketClientReader extends Thread {

	private final Game game;
	private final InetAddress address;
	private final int port;
	private final InputStream inp;
	private final BufferedReader brinp;
	private final boolean isServer;

	private boolean running = true;
	private boolean loggedIn = false;
	private String username;
	private String fullName = null;
	private int level = 0;
	private double gold = 0D;
	private int lastMob = 0;

	private int packetsGot = 0;

	public SocketClientReader(Game game, String username, Socket socket, boolean isServer) {
		this.game = game;
		this.isServer = isServer;
		this.username = username;
		address = socket.getInetAddress();
		port = socket.getPort();
		if (isServer) {
			game.getServer().getPlayers().addElement(getUsername());
		}
		InputStream inp = null;
		BufferedReader brinp = null;
		try {
			inp = socket.getInputStream();
			brinp = new BufferedReader(new InputStreamReader(inp));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.inp = inp;
		this.brinp = brinp;
	}

	@Override
	public void run() {
		String line = null;
		while (running) {
			try {
				try {
					line = brinp.readLine();
				} catch (final SocketException e) {
					shutdown(e.toString());
					return;
				}
				if (!running) {
					shutdown("The thread was halted before the data could be parsed.");
					return;
				} else {
					if ((line != null) && !line.equals("")) {
						if (isServer) {
							handlePacketAsServer(line);
						} else {
							handlePacketAsClient(line);
						}
					}
				}
			} catch (final IOException e) {
				shutdown(e.toString());
				return;
			}
		}
	}

	private void handlePacketAsClient(String line) { 
		Debug.out(Out.DEBUG, "game.net.SocketClientReader", "[" + address + ":" + port + "] Client got: " + line);
		final String message = line.trim();
		PacketTypes type;
		try {
			type = Packet.lookupPacket(message.substring(0, 2));
		} catch (final IndexOutOfBoundsException e) {
			type = Packet.PacketTypes.INVALID;
		}
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			shutdown("Received a malformed packet."); // RED ALERT!
			break;
		case LOGIN:
			packet = new P00Login(line);
			clientHandleLogin((P00Login) packet);
			break;
		case QUIT:
			break;
		case CHAT:
			packet = new P02Chat(line);
			clientHandleChat((P02Chat) packet);
			break;
		case DAMAGE:
			break;
		case UPDATE:
			packet = new P04Update(line);
			clientHandleUpdate((P04Update) packet);
			break;
		}
	}

	private void clientHandleLogin(P00Login packet) {
		if (!loggedIn) {
			if (packet.getUsername().equals(username) && packet.isSucessful()) {
				loggedIn = true;
			} else if (packet.getUsername().equals(username) && !packet.isSucessful() && packet.getVersion().equals(Game.VERSION)) {
				shutdown("The server refused the connection. The username might be taken.");
			} else if (packet.getUsername().equals(username) && !packet.isSucessful() && !packet.getVersion().equals(Game.VERSION)) {
				shutdown("The server refused the connection. Your version: " + Game.VERSION + ". Server's version: " + packet.getVersion());
			}
		}
	}

	private void clientHandleChat(P02Chat packet) {
		game.chat.addString(packet.getMessage(), packet.getColor());
	}

	private void clientHandleUpdate(P04Update packet) {
		if (packet.isCleanAndFresh()) {
			for (int lane = 0; lane < game.mobs.length; lane++) {
				for (int id = 0; id < game.mobs[0].length; id++) {
					game.mobs[lane][id] = Mob.createBasedOnId(packet.getId()[(lane * 3) + id], id,
							packet.getHp()[(lane * Constants.MOBS_PER_LANE) + id]);
				}
			}
		} else {
			if(!packet.isIgnore()) {
				for (int lane = 0; lane < game.mobs.length; lane++) {
					for (int id = 0; id < game.mobs[0].length; id++) {
						if (game.mobs[lane][id] != null) {
							game.mobs[lane][id].setHealth(packet.getHp()[(lane * Constants.MOBS_PER_LANE) + id]);
						}
					}
				}
			}
		}
		level = packet.getLevel();
		gold = packet.getGold();
	}

	private void handlePacketAsServer(String line) {
		game.getServer().writeLog(Debug.out(Out.DEBUG, "SCR", "[" + address + ":" + port + "] Server got: " + line));
		if (packetsGot > Constants.PACKETS_PER_SECOND) {
			return;
		}
		packetsGot++;
		final String message = line.trim();
		PacketTypes type;
		try {
			type = Packet.lookupPacket(message.substring(0, 2));
		} catch (final IndexOutOfBoundsException e) {
			type = Packet.PacketTypes.INVALID;
		}
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			shutdown("Received a malformed packet."); // RED ALERT
			break;
		case LOGIN:
			packet = new P00Login(line);
			if (!packet.isValid()) {
				break;
			}
			serverHandleLogin((P00Login) packet);
			break;
		case QUIT:
			packet = new P01Quit(line);
			if (!validate(packet)) {
				break;
			}
			serverHandleQuit((P01Quit) packet);
			break;
		case CHAT:
			packet = new P02Chat(line);
			if (!validate(packet)) {
				break;
			}
			serverHandleChat((P02Chat) packet);
			break;
		case DAMAGE:
			packet = new P03Damage(line);
			if (!validate(packet)) {
				break;
			}
			serverHandleDamage((P03Damage) packet);
			break;
		}
	}

	private void serverHandleLogin(P00Login packet) {
		if(!packet.getVersion().equals(Game.VERSION)) {
			packet = new P00Login(packet.getUsername(), false, Game.VERSION);
		}
		for (final SocketClient client : game.getServer().clients) {
			int num = 0;
			if (client.getUsername().equals(packet.getUsername())) {
				num++;
			}
			if (num > 0) {
				packet = new P00Login(packet.getUsername(), false, Game.VERSION);
				break;
			}
		}
		P02Chat cpacket = null;
		if (username.equals("SERVER") && packet.isSucessful()) {
			username = packet.getUsername(); // This thread now belongs to that
			// user.
			game.getServer().getPlayers().removeElement("SERVER");
			fullName = "[" + address + ":" + port + "] " + getUsername();
			game.getServer().getPlayers().addElement(fullName);
			cpacket = new P02Chat("null", 0xFFFFFF00, packet.getUsername() + " joined the game!");
			game.getServer().writeLog(Debug.out(Out.INFO, "SCR", packet.getUsername() + " joined the game!"));
		}
		packet.writeData(game.getServer());
		if (cpacket != null) {
			cpacket.writeData(game.getServer());
			final P04Update upacket = game.getServer().parseDataFromGameToPacket(true, 0.0, false);
			upacket.writeData(game.getServer(), packet.getUsername());
		}
	}

	private void serverHandleQuit(P01Quit packet) {
		game.getServer().writeLog(Debug.out(Out.INFO, "SCR", packet.getUsername() + " quit the game!"));
		shutdown("Quit");
	}

	private void serverHandleChat(P02Chat packet) {
		game.getServer().writeLog(Debug.out(Out.INFO, "SCR", packet.getMessage()));
		game.getServer().handleCommand(packet.getUsername(), packet.getRawMessage(), false);
	}

	private void serverHandleDamage(P03Damage packet) {
		int total = 0;
		for (final int i : packet.getClicked()) {
			total += i;
			if (total > Constants.COMMANDS_PER_PACKET) {
				return;
			}
		}
		for (int lane = 0; lane < game.mobs.length; lane++) {
			for (int id = 0; id < game.mobs[0].length; id++) {
				if (game.mobs[lane][id] != null) {
					game.mobs[lane][id].setHealth(game.mobs[lane][id].getHealth()
							- packet.getClicked()[(lane * Constants.MOBS_PER_LANE) + id]);
				}
			}
		}
		lastMob = packet.getLastClicked();
	}

	/**
	 * Validates a packet
	 *
	 * @param p
	 *            Packet to validate
	 * @return True if it is valid, false otherwise.
	 */
	private boolean validate(Packet p) {
		return p.isValid() && !username.equals("SERVER") && (p.getUsername() != null)
				&& p.getUsername().equals(username);
	}

	/**
	 * If the thread is running, attempts to permanently close all of the
	 * connections to this thread.
	 *
	 * @param reason
	 *            The reason to display to the user when closed.
	 */
	public void shutdown(String reason) {
		if (running) {
			running = false;
			closeQuietly(inp);
			closeQuietly(brinp);
			if ((game.getServer() != null) && game.getServer().isRunning()) {
				if(!reason.equals("Quit")) {
					game.getServer().writeLog(
							Debug.out(Out.WARNING, "SCR", username + "'s reader shut down (" + reason + ")"));
				}
				game.getServer().getPlayers().removeElement(fullName != null ? fullName : "SERVER");
				if (!username.equals("SERVER")) {
					if ((reason.indexOf(":") > 0) && (reason.length() > 2)) {
						reason = reason.substring(reason.indexOf(":") + 2);
					}
					final P02Chat packet = new P02Chat("null", reason.equals("Quit") ? 0xFFFFFF00 : 0xFFFF0000,
							username + " disconnected! (" + reason + ")");
					packet.writeData(game.getServer());
				}
			} else {
				Debug.out(Out.WARNING, "game.net.SocketClientReader", username + "'s reader shut down (" + reason + ")");
				game.restart(reason);
			}
		}
	}

	/**
	 * Attempts to close a Closeable
	 *
	 * @param c
	 *            Closeable to close.
	 */
	private void closeQuietly(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (final IOException ignored) {

			}
		}
	}

	/**
	 * @return True if this thread is running on the server object.
	 */
	public boolean isServer() {
		return isServer;
	}

	/**
	 * @return True if this thread is running.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Gets the username of this reader.
	 *
	 * @return The username
	 */
	public String getUsername() {
		return username;
	}

	public void resetPackets() {
		packetsGot = 0;
	}

	public int getLevel() {
		return level;
	}

	public double getGold() {
		return gold;
	}
	
	public void setGold(double gold) {
		this.gold = gold;
	}
	
	public int getLastClicked() {
		return lastMob;
	}
	
}
