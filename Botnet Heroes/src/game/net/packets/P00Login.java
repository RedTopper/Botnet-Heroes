package game.net.packets;

import game.net.SocketClient;
import game.net.SocketServer;

public class P00Login extends Packet {

	private final boolean loginSucessful;
	private final String version;

	public P00Login(String str) {
		final String[] dataArray = readData(str).split(",");
		if (dataArray.length == 3) {
			username = dataArray[0];
			loginSucessful = dataArray[1].equals("1");
			version = dataArray[2];
		} else {
			isValid = false;
			loginSucessful = false;
			version = "";
		}
	}

	public P00Login(String username, boolean isValid, String version) {
		this.username = username;
		loginSucessful = isValid;
		this.version = version;
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
		return "00" + username + "," + ((loginSucessful ? "1" : "0") + "," + version);
	}

	public boolean isSucessful() {
		return loginSucessful;
	}
	
	public String getVersion() {
		return version;
	}
}
