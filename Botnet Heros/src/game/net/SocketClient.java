package game.net;

import game.Game;
import game.net.packets.P00Login;
import game.utils.Debug;
import game.utils.Out;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SocketClient {

	private final InetAddress address;
	private final int port;
	private final SocketClientReader reader;
	private final Socket socket;
	private final DataOutputStream out;

	public SocketClient(Game game, String username, InetAddress address, int port) {
		this.address = address;
		this.port = port;
		Socket socket = null;
		DataOutputStream out = null;
		try {
			socket = new Socket(address, port);
			out = new DataOutputStream(socket.getOutputStream());
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.socket = socket;
		this.out = out;
		reader = new SocketClientReader(game, username, this.socket, false);
		new Thread(reader).start();
		Debug.out(Out.INFO, "game.net.SocketClient", "A game client has started!");
		final P00Login packet = new P00Login(username, true, Game.VERSION);
		packet.writeData(this);
	}

	public SocketClient(Game game, Socket socket) {
		address = socket.getInetAddress();
		port = socket.getPort();
		this.socket = socket;
		DataOutputStream out = null;
		SocketClientReader reader = null;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			reader = new SocketClientReader(game, "SERVER", socket, true);
			new Thread(reader).start();
			Debug.out(Out.INFO, "SC", "A server client has started!");
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.out = out;
		this.reader = reader;
	}

	public synchronized void writeData(String str) {
		try {
			out.writeBytes(str + "\n");
			out.flush();
		} catch (final IOException e) {
			shutdown(e.toString());
		}
	}

	public void shutdown(String reason) {
		try {
			out.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		reader.shutdown(reason);
	}

	/**
	 * @return the address of the client
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * @return the port of the client
	 */
	public int getPort() {
		return port;
	}

	public boolean isRunning() {
		return reader.isRunning();
	}

	public String getUsername() {
		return reader.getUsername();
	}

	public void resetPackets() {
		reader.resetPackets();
	}
	
	public int getLevel() {
		return reader.getLevel();
	}

	public double getGold() {
		return reader.getGold();
	}
	
	public void setGold(double gold) {
		reader.setGold(gold);
	}
	
	public int getLastClicked() {
		return reader.getLastClicked();
	}
}
