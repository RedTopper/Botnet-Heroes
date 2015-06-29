package game.net;

import game.Game;
import game.handlers.ServerInputHandler;
import game.mobs.JerryTheIceCreamMan;
import game.mobs.Mob;
import game.net.packets.P02Chat;
import game.net.packets.P04Update;
import game.utils.Constants;
import game.utils.Debug;
import game.utils.Out;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class SocketServer extends Thread {

	private final Game game;
	private boolean running = true;

	private volatile DefaultListModel<String> listOfPlayers = new DefaultListModel<>();
	public volatile CopyOnWriteArrayList<SocketClient> clients = new CopyOnWriteArrayList<>();

	private volatile JTextArea text;
	private volatile JTextArea console;
	private volatile JScrollPane loggerScroller;

	private final ServerSocket serverSocket;
	private Socket clientSocket;

	private JFrame frame;

	private static final String[] help = { "Help document: ", "*: Server only.", "/help  - Shows this",
			"/stop* - Close the server" };

	public SocketServer(Game game) {
		this.game = game;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(Constants.PORT);
			init();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
		while (true) {
			try {
				clientSocket = serverSocket.accept();
			} catch (final IOException e) {
				shutdown("The server could not accept a connection.");
				return;
			}
			clients.add(new SocketClient(game, clientSocket));
			writeLog(Debug.out(Out.INFO, "SS",
					"[" + clients.get(clients.size() - 1).getAddress() + ":"
							+ clients.get(clients.size() - 1).getPort() + "] has connected!"));
			if (!running) {
				try {
					serverSocket.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				shutdown("The server is no longer running.");
				return;
			}
		}
	}

	public void shutdown(String reason) {
		if (running) {
			writeLog(Debug.out(Out.WARNING, "game.net.SocketServer", "The game server is shutting down!"));
			for (final SocketClient client : clients) {
				client.shutdown(reason);
			}
			closeQuietly(serverSocket);
			frame.dispose();
			running = false;
		}
	}

	/**
	 * @return the model of the text box for the server window.
	 */
	public synchronized DefaultListModel<String> getPlayers() {
		return listOfPlayers;
	}

	/**
	 * Writes a string to all of the clients
	 *
	 * @param line
	 *            String to send.
	 */
	public void writeData(String line) {
		for (final SocketClient client : clients) {
			if (client.isRunning()) {
				client.writeData(line);
			}
		}
	}

	/**
	 * Writes a string to a specific user.
	 *
	 * @param line
	 *            String to send.
	 * @param username
	 *            Username to send to.
	 */
	public void writeData(String line, String username) {
		for (final SocketClient client : clients) {
			if (client.isRunning() && client.getUsername().equals(username)) {
				client.writeData(line);
			}
		}
	}

	/**
	 * Attempts to get rid of old clients.
	 */
	public synchronized void attemptTrash() {
		for (int i = 0; i < clients.size(); i++) {
			if (!clients.get(i).isRunning()) {
				clients.remove(i);
				writeLog(Debug.out(Out.INFO, "SS", "Trashed an old client. " + clients.size()
						+ (clients.size() == 1 ? " client is " : " clients are ") + "connected to the server."));
				i--;
			}
		}
	}

	/**
	 * Writes data to the log.
	 *
	 * @param str
	 *            Message to write. A new line will be inserted automatically.
	 */
	public synchronized void writeLog(String str) {
		if ((str != null) && !str.equals("")) {
			console.append(str + "\n");
			final int max = loggerScroller.getVerticalScrollBar().getMaximum();
			loggerScroller.getVerticalScrollBar().setValue(max);
		}
	}

	/**
	 * Writes the status of the server. New lines are manual.
	 *
	 * @param str
	 *            Long string (containing line breaks) to set the status.
	 */
	public synchronized void writeStatus(String str) {
		text.setText(str);
	}

	/**
	 * If the server is running, this will return true.
	 *
	 * @return True if running, false if not.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Handles a command. If no command is sent, (no preceding "/"), the server
	 * will write the data to all clients (send a message).
	 *
	 * @param username
	 *            Username of the sender
	 * @param message
	 *            Message of the sender
	 * @param isServer
	 *            If the handle should consider this message as a server.
	 */
	public synchronized void handleCommand(String username, String message, boolean isServer) {
		if ((message == null) || message.equals("")) {
			return;
		}
		P02Chat packet;
		if (!message.substring(0, 1).equals("/")) {
			packet = new P02Chat(username.equals("null") ? "Host" : username, username.equals("null") ? 0xFF00FF00
					: 0xFFFFFFFF, message);
			packet.writeData(game.getServer());
		}
		if (message.substring(0, 1).equals("/") && isServer) {
			if (message.substring(1).equals("stop")) {
				shutdown("The host shut down the server");
			} else if (message.substring(1).equals("help")) {
				for (final String s : SocketServer.help) {
					writeLog(Debug.out(Out.INFO, "SS", s));
				}
			} else {
				writeLog(Debug.out(Out.INFO, "SS", "Unknown command, try /help."));
			}
		}
		if (message.substring(0, 1).equals("/") && !isServer) {
			if (message.substring(1).equals("help")) {
				for (final String s : SocketServer.help) {
					packet = new P02Chat("null", 0xFF0088FF, s);
					packet.writeData(this, username);
				}
			} else {
				packet = new P02Chat("null", 0xFFFF0000, "Unknown command - try /help.");
				packet.writeData(this, username);
			}
		}
	}

	/**
	 * Attempts to close a data stream.
	 *
	 * @param c
	 *            The closeable to close.
	 */
	private void closeQuietly(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (final IOException ignored) {

			}
		}
	}

	private class FixedSizeDocument extends PlainDocument {
		private static final long serialVersionUID = 7228078518369455442L;
		private int max = 10;

		private FixedSizeDocument(int max) {
			this.max = max;
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if ((getLength() + str.length()) > max) {
				str = str.substring(0, max - getLength());
				Toolkit.getDefaultToolkit().beep();
			}
			super.insertString(offs, str, a);
		}
	}

	public P04Update parseDataFromGameToPacket(boolean cleanAndFresh) {
		final int[] id = new int[Constants.MOBS_PER_LANE * Constants.LANES];
		final double[] hp = new double[Constants.MOBS_PER_LANE * Constants.LANES];
		int index = 0;
		for (final Mob[] lane : game.mobs) {
			for (final Mob m : lane) {
				if (m == null) {
					id[index] = 0;
					hp[index] = 0.0;
				} else {
					if (!cleanAndFresh) {
						id[index] = m.getType();
						hp[index] = m.getHealth();
					} else {
						id[index] = m.getType();
						hp[index] = m.getOriginalHp();
					}
				}
				index++;
			}
		}
		return new P04Update("SERVER", id, hp, cleanAndFresh);
	}

	public void resetPackets() {
		for (final SocketClient c : clients) {
			c.resetPackets();
		}
	}

	private void init() {
		boolean windows = false;
		if (!windows) { // Redundant, but it helps readability.
			try {
				for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if ("Windows".equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						windows = true;
						break;
					}
				}
			} catch (final Exception e) {
			}
		}
		if (!windows) { // No windows? Let's try this.
			try {
				for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			} catch (final Exception e2) {
				System.out.print("You have literally no themes. Using fallback");
			}
		}
		/**
		 * I'm sorry to all of the maintainers of this code (mostly just me,
		 * AJ), but to all who look at this next selection, I apologize. This
		 * magic code sets up the servers JFrame and JPanels. There isn't much
		 * else here.
		 */
		final JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.X_AXIS));
		final JPanel stats = new JPanel();
		stats.setLayout(new BoxLayout(stats, BoxLayout.Y_AXIS));
		stats.setBorder(new TitledBorder("Stats"));
		text = new JTextArea(Constants.STATS_HEIGHT, Constants.STATS_WIDTH);
		text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, Constants.FONT_PT));
		text.setEditable(false);
		text.setMaximumSize(text.getPreferredSize());
		text.setMinimumSize(text.getPreferredSize());
		stats.add(text);
		final JList<String> list = new JList<>(getPlayers());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setVisibleRowCount(6);
		list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, Constants.FONT_PT));
		final JScrollPane listScroller = new JScrollPane(list);
		listScroller.setBorder(new TitledBorder("Players"));
		listScroller.setMaximumSize(new Dimension(text.getPreferredSize().width, Integer.MAX_VALUE));
		stats.add(listScroller);
		main.add(stats);
		final JPanel log = new JPanel();
		log.setLayout(new BoxLayout(log, BoxLayout.Y_AXIS));
		log.setBorder(new TitledBorder("Log and Chat"));
		console = new JTextArea();
		console.setEditable(false);
		console.setFont(new Font(Font.MONOSPACED, Font.PLAIN, Constants.FONT_PT));
		loggerScroller = new JScrollPane(console);
		log.add(loggerScroller, BorderLayout.PAGE_START);
		final JTextField in = new JTextField(25);
		in.addKeyListener(new ServerInputHandler(in, this));
		in.setMaximumSize(new Dimension(Integer.MAX_VALUE, in.getPreferredSize().height));
		in.setDocument(new FixedSizeDocument(Constants.SERVER_LENGTH));
		log.add(in);
		main.add(log);
		frame = new JFrame("Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(main);
		frame.setMinimumSize(Constants.SERVER_MIN_SIZE);
		frame.setPreferredSize(Constants.SERVER_PREFERRED_SIZE);
		frame.setMaximumSize(Constants.SERVER_MAX_SIZE);
		frame.setLocationRelativeTo(game.frame);
		final ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/smallIcon.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
		frame.setIconImages(icons);
		frame.pack();
		frame.setAutoRequestFocus(false);
		game.frame.toFront();
		frame.setVisible(true);
		game.frame.toFront();
		writeLog("[INFO][SS] Started the server.");
		writeLog("[INFO][SS] For debug purposes, the class names have been shortened to");
		writeLog("[INFO][SS] a more reasonable length. The class names are as follows:");
		writeLog("[INFO][SS] [SS] = game.net.SocketServer, [SCR] = game.net.SocketClientReader");
		writeLog("[INFO][SS] [SC] = game.net.SocketClient, [SIR] = game.utils.ServerInputHandler.");
		writeLog(" ");

		game.mobs[0][0] = new JerryTheIceCreamMan(0, (double)((int)(Math.random()*1000)));
		game.mobs[0][1] = new JerryTheIceCreamMan(1, (double)((int)(Math.random()*1000)));
		game.mobs[0][2] = new JerryTheIceCreamMan(2, (double)((int)(Math.random()*1000)));
		game.mobs[1][0] = new JerryTheIceCreamMan(0, (double)((int)(Math.random()*1000)));
		game.mobs[2][1] = new JerryTheIceCreamMan(1, (double)((int)(Math.random()*1000)));
		game.mobs[3][2] = new JerryTheIceCreamMan(2, (double)((int)(Math.random()*1000)));
		game.mobs[4][2] = new JerryTheIceCreamMan(2, (double)((int)(Math.random()*1000)));
	}
}
