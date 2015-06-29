package game.net.packets;

import game.net.SocketClient;
import game.net.SocketServer;

public class P02Chat extends Packet {

	private String message = "";
	private final int color;

	public P02Chat(String str) {
		final String[] dataArray = readData(str).split(",");
		if (dataArray.length > 2) {
			username = dataArray[0];
			color = Integer.parseInt(dataArray[1]);
			for (int i = 2; i < dataArray.length; i++) {
				message += dataArray[i];
			}
		} else {
			isValid = false;
			color = 0xFFFFFFFF;
		}
	}

	public P02Chat(String username, int color, String message) {
		this.username = username;
		setMessage(message);
		this.color = color;
	}

	@Override
	public void writeData(SocketClient client) {
		client.writeData(getData());
	}

	@Override
	public void writeData(SocketServer server) {
		server.writeData(getData());
	}

	/**
	 * @return Only the message if the username is "null" otherwise it returns a
	 *         formated message.
	 */
	public String getMessage() {
		if (username.equals("null")) {
			return message;
		} else {
			return username + ": " + message;
		}
	}

	/**
	 * @return Always returns the raw message.
	 */
	public String getRawMessage() {
		return message;
	}

	public int getColor() {
		return color;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message.replace(",", " ");
	}

	@Override
	public void writeData(SocketServer server, String username) {
		server.writeData(getData(), username);
	}

	@Override
	public String getData() {
		return "02" + username + "," + color + "," + message;
	}
}
