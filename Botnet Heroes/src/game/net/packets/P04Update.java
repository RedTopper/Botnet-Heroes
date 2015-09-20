package game.net.packets;

import game.net.SocketClient;
import game.net.SocketServer;
import game.utils.Constants;

public class P04Update extends Packet {

	private final int[] id;
	private final double[] hp;
	private final boolean cleanAndFresh;
	private final int level;
	private final double gold;
	private final boolean ignore;

	public P04Update(String str) {
		final String[] dataArray = readData(str).split(",");
		final int[] id = new int[Constants.MOBS_PER_LANE * Constants.LANES];
		final double[] hp = new double[Constants.MOBS_PER_LANE * Constants.LANES];
		if (dataArray.length == (((Constants.MOBS_PER_LANE * Constants.LANES) * 2) + 5)) {
			username = dataArray[0];
			cleanAndFresh = dataArray[1].equals("1");
			level = Integer.parseInt(dataArray[2]);
			gold = Double.parseDouble(dataArray[3]);
			ignore = dataArray[4].equals("1");
			for (int i = 0; i < (Constants.MOBS_PER_LANE * Constants.LANES); i++) {
				id[i] = Integer.parseInt(dataArray[i + 5]);
			}
			for (int i = 0; i < (Constants.MOBS_PER_LANE * Constants.LANES); i++) {
				hp[i] = Double.parseDouble(dataArray[i + (Constants.MOBS_PER_LANE * Constants.LANES) + 5]);
			}
			this.id = id;
			this.hp = hp;
		} else {
			isValid = false;
			cleanAndFresh = false;
			ignore = false;
			this.id = null;
			this.hp = null;
			level = -1;
			gold = -1;
		}
	}

	public P04Update(String username, boolean cleanAndFresh, int level, double gold, boolean ignore, int[] id, double[] hp) {
		this.username = username;
		this.hp = hp;
		this.id = id;
		this.cleanAndFresh = cleanAndFresh;
		this.level = level;
		this.gold = gold;
		this.ignore = ignore;
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
	
	public double getGold() {
		return gold;
	}
	
	public boolean isIgnore() {
		return ignore;
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
		s += (cleanAndFresh ? "1" : "0") + ",";
		s += level + ",";
		s += gold + ",";
		s += (ignore ? "1" : "0") + ",";
		for (final Integer i : id) {
			s += (i + ",");
		}
		for (final Double d : hp) {
			s += (d + ",");
		}
		s = s.substring(0,s.length() - 2); //cuts off the ","
		return s;
	}
}
