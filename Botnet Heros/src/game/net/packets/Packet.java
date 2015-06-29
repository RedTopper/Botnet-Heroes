package game.net.packets;

import game.net.SocketClient;
import game.net.SocketServer;
import game.utils.Debug;
import game.utils.Out;

public abstract class Packet {
	protected boolean isValid = true;
	protected String username;

	public static enum PacketTypes {
		INVALID(-1), LOGIN(0), QUIT(1), CHAT(2), DAMAGE(3), UPDATE(4);

		private final int packetId;

		private PacketTypes(int packetId) {
			this.packetId = packetId;
		}

		public int getId() {
			return packetId;
		}
	}

	public String getUsername() {
		if ((username == null) || username.equals("")) {
			Debug.out(Out.WARNING, "game.net.Packet", "Username was never set on this packet!");
		}
		return username;
	}

	public abstract void writeData(SocketClient client);

	public abstract void writeData(SocketServer server);

	public abstract void writeData(SocketServer server, String username);

	public String readData(String str) {
		final String message = str.trim();
		return message.substring(2); // Drop the packet type.
	}

	public abstract String getData();

	public static PacketTypes lookupPacket(String packetId) {
		try {
			return Packet.lookupPacket(Integer.parseInt(packetId));
		} catch (final Exception e) {
			return PacketTypes.INVALID;
		}
	}

	private static PacketTypes lookupPacket(int id) {
		for (final PacketTypes p : PacketTypes.values()) {
			if (p.getId() == id) {
				return p;
			}
		}
		return PacketTypes.INVALID;
	}

	public boolean isValid() {
		return isValid;
	}
}
