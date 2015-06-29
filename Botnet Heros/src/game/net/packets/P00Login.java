package game.net.packets;

import game.net.SocketClient;
import game.net.SocketServer;

public class P00Login extends Packet {

	private final boolean loginSucessful;

	public P00Login(String str) {
		final String[] dataArray = readData(str).split(",");
		if (dataArray.length == 2) {
			username = dataArray[0];
			loginSucessful = dataArray[1].equals("1");
		} else {
			isValid = false;
			loginSucessful = false;
		}
	}

	public P00Login(String username, boolean isValid) {
		this.username = username;
		loginSucessful = isValid;
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
		return "00" + username + "," + (loginSucessful ? "1" : "0");
	}

	public boolean isSucessful() {
		return loginSucessful;
	}
}
