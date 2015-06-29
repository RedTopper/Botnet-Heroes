package game.net.packets;

import game.net.SocketClient;
import game.net.SocketServer;
import game.utils.Constants;

public class P04Update extends Packet {

	private final int[] id;
	private final double[] hp;
	private final boolean cleanAndFresh;
	private final int level;

	public P04Update(String str) {
		final String[] dataArray = readData(str).split(",");
		final int[] id = new int[Constants.MOBS_PER_LANE * Constants.LANES];
		final double[] hp = new double[Constants.MOBS_PER_LANE * Constants.LANES];
		if (dataArray.length == (((Constants.MOBS_PER_LANE * Constants.LANES) * 2) + 3)) {
			username = dataArray[0];
			for (int i = 0; i < (Constants.MOBS_PER_LANE * Constants.LANES); i++) {
				id[i] = Integer.parseInt(dataArray[i + 1]);
			}
			for (int i = 0; i < (Constants.MOBS_PER_LANE * Constants.LANES); i++) {
				hp[i] = Double.parseDouble(dataArray[i + (Constants.MOBS_PER_LANE * Constants.LANES) + 1]);
			}
			cleanAndFresh = dataArray[dataArray.length - 2].equals("1");
			level = Integer.parseInt(dataArray[dataArray.length - 1]);
			this.id = id;
			this.hp = hp;
		} else {
			isValid = false;
			cleanAndFresh = false;
			this.id = null;
			this.hp = null;
			level = -1;
		}
	}

	public P04Update(String username, int[] id, double[] hp, boolean cleanAndFresh, int level) {
		this.username = username;
		this.hp = hp;
		this.id = id;
		this.cleanAndFresh = cleanAndFresh;
		this.level = level;
	}

	public double[] getHp() {
		return hp;
	}

	public int[] getId() {
		return id;
	}

	public boolean isCleanAndFresh() {
		return cleanAndFresh;
	}
	
	public int getLevel() {
		return level;
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
		s += "04";
		s += username + ",";
		for (final Integer i : id) {
			s += (i + ",");
		}
		for (final Double d : hp) {
			s += (d + ",");
		}
		s += (cleanAndFresh ? "1" : "0") + ",";
		s += level;
		return s;
	}
}
