package game.net.packets;

import game.net.SocketClient;
import game.net.SocketServer;
import game.utils.Constants;

public class P03Damage extends Packet {

	private final int[] clicked;
	private final int lastId;

	public P03Damage(String str) {
		final String[] dataArray = readData(str).split(",");
		final int[] clicked = new int[Constants.MOBS_PER_LANE * Constants.LANES];
		if (dataArray.length == ((Constants.MOBS_PER_LANE * Constants.LANES) + 2)) {
			username = dataArray[0];
			for (int i = 1; i < (dataArray.length - 1); i++) {
				clicked[i - 1] = Integer.parseInt(dataArray[i]);
			}
			lastId = Integer.parseInt(dataArray[dataArray.length - 1]);
			this.clicked = clicked;
		} else {
			isValid = false;
			lastId = 0;
			this.clicked = null;
		}
	}

	public P03Damage(String username, int[] clicked, int lastId) {
		this.username = username;
		this.clicked = clicked;
		this.lastId = lastId;
	}

	public int[] getClicked() {
		return clicked;
	}

	@Override
	public void writeData(SocketClient client) {
		client.writeData(getData());
	}

	@Override
	public void writeData(SocketServer server) {
		server.writeData(getData());
	}

	@Override
	public void writeData(SocketServer server, String username) {
		server.writeData(getData(), username);
	}

	@Override
	public String getData() {
		String s = "";
		s += "03";
		s += username + ",";
		for (final Integer i : clicked) {
			s += (i + ",");
		}
		s += lastId + "";
		return s;
	}

	public int getLastClicked() {
		return lastId;
	}
}
