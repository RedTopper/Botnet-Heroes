package game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.sun.management.OperatingSystemMXBean;

import game.gfx.Button;
import game.gfx.ChatBox;
import game.gfx.Font;
import game.gfx.ProgressBar;
import game.gfx.Screen;
import game.gfx.TextBox;
import game.handlers.InputHandler;
import game.handlers.MouseHandler;
import game.handlers.WindowHandler;
import game.mobs.Mob;
import game.net.SocketClient;
import game.net.SocketServer;
import game.net.packets.P02Chat;
import game.net.packets.P03Damage;
import game.net.packets.P04Update;
import game.utils.ButtonList;
import game.utils.Constants;
import game.utils.Debug;
import game.utils.Out;
import game.utils.TextBoxList;

/**
 * The main class that instantiates all other variables.
 *
 * @author AJ
 */
@SuppressWarnings("restriction")
public class Game extends Canvas implements Runnable {

	/**
	 * I have no idea what this black magic is.
	 */
	private static final long serialVersionUID = -8332966340668015263L;

	/**
	 * The {@link String} of the game. Starts with a v. This is used when
	 * logging on to a server, so update it often to prevent out dated clients
	 * from connecting.
	 */
	public static final String VERSION = "v2.0.3";

	/**
	 * The {@link String} of the class. This is displayed in the title.
	 */
	public static final String NAME = "Botnet Heros";

	/**
	 * The minimum {@link Out} of debug that is output. Should be
	 * {@link Out#INFO} or higher on release. NEVER SET TO {@link Out#TRACE} ON
	 * RELEASE!
	 */
	public static final Out debugLevel = Out.INFO;

	/**
	 * Integer representing the dimensions of the JFrame.
	 */
	public static final int WIDTH = 480, HEIGHT = (Game.WIDTH / 16) * 9, SCALE = 2;

	/**
	 * The {@link JFrame} window the game is rendered to.
	 */
	public final JFrame frame;

	/**
	 * A list of all of the {@link Button}s (Enabled or hidden) in the game.
	 */
	public final ButtonList buttons = new ButtonList();

	/**
	 * A list of all of the {@link TextBox}s (Enabled or hidden) in the game.
	 */
	public final TextBoxList textboxes = new TextBoxList();

	/**
	 * Progress bar that appears when loading the game.
	 */
	public final ProgressBar loadingProgress = new ProgressBar((Game.WIDTH / 2) - ((100 * 3) / 2), (Game.HEIGHT / 2)
			- ((8 * 3) / 2), 100 * 3, 8 * 3);

	/**
	 * Box that chat appears in.
	 */
	public final ChatBox chat;

	/**
	 * Array of the mobs in the playing field.
	 */
	public final Mob[][] mobs = new Mob[Constants.LANES][3];

	/**
	 * The {@link BufferedImage} that is rendered to the JFrame.
	 */
	private static final BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);

	/**
	 * Error stream for the game.
	 */
	private final ChatBox err;

	/**
	 * Used for updating and pressing the mouse.
	 */
	private final MouseHandler mouse;

	/**
	 * Screen that is being rendered.
	 */
	private final Screen screen;

	/**
	 * An array of ints that is used to draw the picture on the JFrame.
	 */
	private final int[] pixels = ((DataBufferInt) Game.image.getRaster().getDataBuffer()).getData();

	/**
	 * State of the running game
	 */
	public Stage stage = Stage.LOADING;

	/**
	 * Lane we are in.
	 */
	public int lane;
	private int ticksPassed = 0;
	private volatile SocketServer server;
	private volatile SocketClient client;

	/**
	 * Creates the Game class
	 */
	public Game() {
		Debug.out(Out.INFO, getClass().getName(), "You are running " + Game.VERSION);

		setMinimumSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
		setMaximumSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
		setPreferredSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
		frame = new JFrame(Game.NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.setResizable(false);
		final ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/smallIcon.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
		frame.setIconImages(icons);
		frame.pack();
		frame.setLocationRelativeTo(null);
		screen = new Screen(Game.WIDTH, Game.HEIGHT);
		chat = new ChatBox(0, 24, 30, 10);
		err = new ChatBox(0, 200, 50, 3);
		mouse = new MouseHandler(this);
	}

	/**
	 * Starts up the game thread.
	 */
	private void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime(); // long time in nanoseconds.
		final double nsPerTick = 1000000000D / 60D; // limits updates to 60 ups
		int frames = 0; // frames drawn
		long lastTimer = System.currentTimeMillis();
		double wait = 0; // unprocessed nanoseconds.
		int ticks = 0;

		init();

		while (true) {
			final long now = System.nanoTime();
			wait += (now - lastTime) / nsPerTick;
			lastTime = now;
			while (wait >= 1D) {
				ticks++; // Update drawn ticks.
				tick();
				wait = wait - 1D;
			}
			frames++;
			render();
			try {
				Thread.sleep(6);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			if ((System.currentTimeMillis() - lastTimer) > 1000) {
				lastTimer += 1000;
				frame.setTitle(Game.NAME + " - " + ticks + " ticks, " + frames + " frames.");
				frames = 0;
				ticks = 0;
			}
		}
	}

	/**
	 * Code that is run to draw objects to the frame.
	 */
	private void render() {
		final BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3); // Triple buffering!
			return;
		}

		switch (stage) {
		default:
		case LOADING:
			screen.render(0, 0, "/background.png");
			loadingProgress.render(screen);
			err.render(screen);
			break;
		case MENU:
			screen.render(0, 0, "/background.png");
			screen.render((Game.WIDTH / 2) - (256 / 2), 0, "/logo.png", ((int) (Math.random() * 20) > 0 ? 3
					: (int) (Math.random() * 4)));
			err.render(screen);
			break;
		case CONNECT:
			screen.render(0, 0, "/background.png");
			break;
		case CONNECT_SETUP:
			screen.render(0, 0, "/background.png");
			err.render(screen);
			break;
		case PLAY:
			screen.render(0, 0, "/background.png");
			break;
		case PLAY_SETUP:
			screen.render(0, 0, "/background.png");
			break;
		case GENERAL_SETUP:
			screen.render(0, 0, "/background.png");
			break;
		case RUNNING:
			screen.render(0, 0, "/game.png");
			for (final Mob m : mobs[lane]) {
				if (m != null) {
					m.render(screen);
				}
			}
			if (chat != null) {
				chat.render(screen);
			}
			Font.render(screen, (lane + 1) + "", 75, 26, 0xFFFFFFFF, false);
			Font.render(screen, "Level: " + client.getLevel(), 5, 45, 0xFFFFFFFF, false);
			if (client.getGold() > 9999999D) {
				final NumberFormat formatter = new DecimalFormat("0E0");
				formatter.setMinimumFractionDigits(6);
				Font.render(screen, formatter.format(client.getGold()), 30, 4, 0xFFE5AF00, false);
			} else {
				Font.render(screen, (client.getGold() + "").substring(0, (client.getGold()  + "").indexOf(".")), 30, 4, 0xFFE5AF00, false);
			}
			break;
		}
		for (final Button b : buttons.getAll()) {
			b.render(screen);
		}
		for (final TextBox t : textboxes.getAll()) {
			t.render();
		}
		for (int pix = 0; pix < screen.pixels.length; pix++) {
			pixels[pix] = screen.pixels[pix];
		}
		final Graphics g = bs.getDrawGraphics();
		g.drawImage(Game.image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}

	/**
	 * Code that is updated once every nsPerTick nanoseconds.
	 *
	 * @see Game#run()
	 */
	private void tick() {
		ticksPassed++;
		switch (stage) {
		default:
		case LOADING:
			loadingProgress.setProgress(loadingProgress.getProgress() + 1);
			loadingProgress.setString("Now Loading...");
			hideAll();
			if (loadingProgress.getProgress() > 100) {
				buttons.get(BN.MENU_PLAY).state = Button.States.ENABLED;
				buttons.get(BN.MENU_CONNECT).state = Button.States.ENABLED;
				buttons.get(BN.MENU_QUIT).state = Button.States.ENABLED;
				buttons.get(BN.MENU_QUIT).text = "Quit";
				stage = Stage.MENU;
			}
			break;
		case MENU:
			for (final Button b : buttons.getAll()) {
				if (b.isClicked()) {
					if (b.text.equals("PLAY!")) {
						hideAll();
						buttons.get(BN.MENU_QUIT).state = Button.States.ENABLED;
						buttons.get(BN.MENU_QUIT).text = "Back";
						buttons.get(BN.CONNECT_CONNECT).state = Button.States.DISABLED;
						buttons.get(BN.CONNECT_CONNECT).text = "Host";
						textboxes.get(TN.CONNECT_USERNAME).state = TextBox.States.SELECTED;
						stage = Stage.PLAY;
					}
					if (b.text.equals("JOIN!")) {
						hideAll();
						buttons.get(BN.MENU_QUIT).state = Button.States.ENABLED;
						buttons.get(BN.MENU_QUIT).text = "Back";
						buttons.get(BN.CONNECT_CONNECT).state = Button.States.DISABLED;
						buttons.get(BN.CONNECT_CONNECT).text = "Connect";
						textboxes.get(TN.CONNECT_USERNAME).state = TextBox.States.SELECTED;
						textboxes.get(TN.CONNECT_IP).state = TextBox.States.ENABLED;
						stage = Stage.CONNECT;
					}
					if (b.text.equals("Quit")) {
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					}
				}
			}
			break;
		case CONNECT:
			for (final Button b : buttons.getAll()) {
				if (b.isClicked()) {
					if (b.text.equals("Back")) {
						hideAll();
						buttons.get(BN.MENU_PLAY).state = Button.States.ENABLED;
						buttons.get(BN.MENU_CONNECT).state = Button.States.ENABLED;
						buttons.get(BN.MENU_QUIT).state = Button.States.ENABLED;
						buttons.get(BN.MENU_QUIT).text = "Quit";
						stage = Stage.MENU;
					}
					if (b.text.equals("Connect")) {
						hideAll();
						err.addString("Attempting to connect to the server...", 0xFFFF0000);
						stage = Stage.CONNECT_SETUP;
					}
				}
				if (stage == Stage.CONNECT) {
					if ((textboxes.get(TN.CONNECT_USERNAME).text != null)
							&& !textboxes.get(TN.CONNECT_USERNAME).text.equals("")
							&& (textboxes.get(TN.CONNECT_IP).text != null)
							&& !textboxes.get(TN.CONNECT_IP).text.equals("")) {
						buttons.get(BN.CONNECT_CONNECT).state = Button.States.ENABLED;
					} else {
						buttons.get(BN.CONNECT_CONNECT).state = Button.States.DISABLED;
					}
				}
			}
			break;
		case CONNECT_SETUP:
			server = null;
			InetAddress host = null;
			String username = getValidUsername(textboxes.get(TN.CONNECT_USERNAME).text);
			if (username.equals("INVALID")) {
				restart("That username was invalid.");
				break;
			}
			try {
				host = InetAddress.getByName(textboxes.get(TN.CONNECT_IP).text);
			} catch (final UnknownHostException e) {
				restart(e.toString());
			}
			client = new SocketClient(this, username, host, 63326);
			stage = Stage.GENERAL_SETUP;
			break;
		case PLAY:
			for (final Button b : buttons.getAll()) {
				if (b.isClicked()) {
					if (b.text.equals("Back")) {
						hideAll();
						buttons.get(BN.MENU_PLAY).state = Button.States.ENABLED;
						buttons.get(BN.MENU_CONNECT).state = Button.States.ENABLED;
						buttons.get(BN.MENU_QUIT).state = Button.States.ENABLED;
						buttons.get(BN.MENU_QUIT).text = "Quit";
						stage = Stage.MENU;
					}
					if (b.text.equals("Host")) {
						hideAll();
						stage = Stage.PLAY_SETUP;
					}
				}
				if (stage == Stage.PLAY) {
					if ((textboxes.get(TN.CONNECT_USERNAME).text != null)
							&& !textboxes.get(TN.CONNECT_USERNAME).text.equals("")) {
						buttons.get(BN.CONNECT_CONNECT).state = Button.States.ENABLED;
					} else {
						buttons.get(BN.CONNECT_CONNECT).state = Button.States.DISABLED;
					}
				}
			}
			break;
		case PLAY_SETUP:
			username = getValidUsername(textboxes.get(TN.CONNECT_USERNAME).text);
			if (username.equals("INVALID")) {
				restart("That username was invalid!");
				break;
			}
			server = new SocketServer(this);
			server.start();
			InetAddress localhost = null;
			localhost = InetAddress.getLoopbackAddress();
			client = new SocketClient(this, username, localhost, Constants.PORT);
			stage = Stage.GENERAL_SETUP;
			server.generateNewLevel();
			break;
		case GENERAL_SETUP:
			textboxes.get(TN.MESSAGE_BOX).state = TextBox.States.ENABLED;
			buttons.get(BN.MESSAGE_SEND).state = Button.States.ENABLED;
			buttons.get(BN.PREV_LANE).state = Button.States.ENABLED;
			buttons.get(BN.NEXT_LANE).state = Button.States.ENABLED;
			stage = Stage.RUNNING;
			break;
		case RUNNING:
			if ((textboxes.get(TN.MESSAGE_BOX).text == null) || textboxes.get(TN.MESSAGE_BOX).text.equals("")) {
				buttons.get(BN.MESSAGE_SEND).state = Button.States.DISABLED;
			} else {
				buttons.get(BN.MESSAGE_SEND).state = Button.States.ENABLED;
			}
			for (final Button b : buttons.getAll()) {
				if (b.isClicked()) {
					if (b.text.equals("Send")) {
						final P02Chat packet = new P02Chat(client.getUsername(), 0xFFFFFFFF,
								textboxes.get(TN.MESSAGE_BOX).text);
						textboxes.get(TN.MESSAGE_BOX).text = "";
						packet.writeData(client);
					}
					if (b.text.equals("<")) {
						if (--lane < 0) {
							lane = Constants.LANES - 1;
						}
					}
					if (b.text.equals(">")) {
						if (++lane > (Constants.LANES - 1)) {
							lane = 0;
						}
					}
				}
			}
			if ((ticksPassed % Constants.ONE_SIXTH_SECOND) == 0) {
				if (getServer() != null) {
					boolean isKill = true;
					for(int lane = 0; lane < mobs.length; lane++) {
						for(int mob = 0; mob < mobs[0].length; mob++) {
							if(mobs[lane][mob].getHealth() > 0) {
								isKill = false;
							}
							if(mobs[lane][mob].justDied()) {
								for (final SocketClient c : getServer().clients) {
									if((c.getLastClicked() / Constants.MOBS_PER_LANE) == lane) {
										c.setGold(c.getGold() + 10);
									}
								}
							}
						}
					}
					P04Update packet;
					if(isKill) {
						server.generateNewLevel();
						for (final SocketClient c : getServer().clients) {
							packet = getServer().parseDataFromGameToPacket(true, c.getGold(), false);
							c.writeData(packet.getData());
						}
					} else {
						for (final SocketClient c : getServer().clients) {
							if(c.getUsername().equals(client.getUsername())) {
								//Don't update the host's stuff. Host is always right.
								packet = getServer().parseDataFromGameToPacket(false, c.getGold(), true);
							} else {
								packet = getServer().parseDataFromGameToPacket(false, c.getGold(), false);
							}
							c.writeData(packet.getData());
						}
					}
				}
				final P03Damage damage = new P03Damage(client.getUsername(), mouse.getActions(), mouse.getLastClicked());
				int t = 0;
				for (final int i : damage.getClicked()) {
					t += i;
				}
				if (t != 0) {
					damage.writeData(client);
				}
				mouse.resetActions();
			}
			if ((ticksPassed % Constants.ONE_SECOND) == 0) {
				if (server != null) {
					server.resetPackets();
					final Runtime r = Runtime.getRuntime();
					final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory
							.getOperatingSystemMXBean();
					final double load = (osBean.getProcessCpuLoad() * 100D);
					BigDecimal bd = new BigDecimal(load);
					final double load2 = (osBean.getSystemCpuLoad() * 100D);
					BigDecimal bd2 = new BigDecimal(load2);
					bd = bd.setScale(2, RoundingMode.HALF_UP);
					bd2 = bd2.setScale(2, RoundingMode.HALF_UP);
					final long MB = 1024 * 1024;
					server.writeStatus("Version: " + Game.VERSION + "\n" + "Ticks: " + ticksPassed + "\n" + "Clients: "
							+ server.clients.size() + "\n" + "Threads: About " + Thread.activeCount() + "\n"
							+ "Free memory: " + (r.freeMemory() / MB) + "MiB\n" + "Used memory: "
							+ ((r.totalMemory() - r.freeMemory()) / MB) + "MiB\n" + "Allocated memory: "
							+ (r.totalMemory() / MB) + "MiB\n" + "Max memory: " + (r.maxMemory() / MB) + "MiB\n"
							+ "Total Free memory: " + ((r.freeMemory() + (r.maxMemory() - r.totalMemory())) / MB)
							+ "MiB\n" + "Server Load: " + bd + "%\n" + "Computer Load: " + bd2 + "%");
				}
			}
			break;
		}
		chat.tick();
		err.tick();
		for (final Mob[] lanes : mobs) {
			for (final Mob m : lanes) {
				if (m != null) {
					m.tick();
				}
			}
		}
		for (final Button b : buttons.getAll()) {
			if ((b.state == Button.States.ENABLED) || (b.state == Button.States.PRESSED)
					|| (b.state == Button.States.OUT)) {
				final boolean within = (mouse.getX() >= b.getX())
						&& (mouse.getX() <= ((b.getW() * b.getTileWidth()) + b.getX())) && (mouse.getY() >= b.getY())
						&& (mouse.getY() <= ((b.getH() * b.getTileHeight()) + b.getY()));
				if (within && (mouse.getX() >= 0) && (mouse.getY() >= 0)) {
					b.state = Button.States.PRESSED;
				} else if (!within && (mouse.getX() >= 0) && (mouse.getY() >= 0)) {
					b.state = Button.States.OUT;
				} else {
					b.state = Button.States.ENABLED;
				}
			}
		}
		for (final TextBox t : textboxes.getAll()) {
			if (((mouse.getX() >= 0) && (mouse.getY() >= 0))
					&& ((t.state == TextBox.States.ENABLED) || (t.state == TextBox.States.SELECTED))) {
				final boolean within = (mouse.getX() >= t.getX()) && (mouse.getX() <= (t.getW() + t.getX()))
						&& (mouse.getY() >= t.getY()) && (mouse.getY() <= (t.getH() + t.getY()));
				if (within) {
					for (final TextBox sett : textboxes.getAll()) {
						if (sett.state == TextBox.States.SELECTED) {
							sett.state = TextBox.States.ENABLED;
						}
					}
					t.state = TextBox.States.SELECTED;
				} else {
					t.state = TextBox.States.ENABLED;
				}
			}
		}

		if (server != null) {
			server.attemptTrash();
		}
	}

	private void hideAll() {
		for (final Button b : buttons.getAll()) {
			b.state = Button.States.HIDDEN;
		}
		for (final TextBox t : textboxes.getAll()) {
			t.state = TextBox.States.HIDDEN;
		}
	}

	private String getValidUsername(String username) {
		if (username.equals("INVALID")) { // Just to make sure.
			return "INVALID";
		}
		if (username.equals("SERVER")) {
			return "INVALID";
		}
		if (username.equals("null")) {
			return "INVALID";
		}
		if (username.contains(",")) {
			username = username.substring(0, username.indexOf(","));
		}
		if (username.length() > Constants.USERNAME_LENGTH) {
			username = username.substring(0, Constants.USERNAME_LENGTH);
		}
		username = username.replace(" ", "");
		return username;
	}

	/**
	 * Completely restarts the game from the bottom up, showing an error message to the user.
	 * @param reason Reason given for the sudden restart.
	 */
	public void restart(String reason) {
		stage = Stage.LOADING;
		hideAll();
		loadingProgress.setProgress(0);
		Debug.out(Out.WARNING, "game.Game", "Error: " + reason);
		err.addString(reason, 0xFFFF0000);
	}

	/**
	 * Gets the currently selected text box.
	 * @return The selected text box.
	 */
	public TextBox getSelectedTextBox() {
		for (final TextBox t : textboxes.getAll()) {
			if (t.state == TextBox.States.SELECTED) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Gets the client one time per thread.
	 * @return The client.
	 */
	public synchronized SocketClient getClient() {
		return client;
	}

	/**
	 * Gets the server one time per thread.
	 * @return The server.
	 */
	public synchronized SocketServer getServer() {
		return server;
	}

	/**
	 * @param args
	 *            Arguments used to start the game with.
	 */
	public static void main(String[] args) {
		for (final String s : args) {
			System.out.println(s);
		}
		new Game().start();
	}

	/**
	 * Code that runs ONE TIME when the game is started up.
	 */
	private void init() {
		new InputHandler(this);
		new WindowHandler(this);
		frame.setVisible(true); // After init, show the frame.
		final int BUTTON_WIDTH = 10;
		final int BUTTON_HEIGHT = 4;
		final int BUTTON_PIX_WIDTH = 9;
		final int BUTTON_PIX_HEIGHT = 9;

		buttons.add(new Button(screen, (Game.WIDTH / 2) - (((BUTTON_WIDTH + 1) * BUTTON_PIX_WIDTH)), (Game.HEIGHT / 2)
				- (((BUTTON_HEIGHT + 1) * BUTTON_PIX_HEIGHT) / 2), BUTTON_WIDTH, BUTTON_HEIGHT, "/button_enabled.png",
				"/button_disabled.png", "/button_pressed.png"), BN.MENU_PLAY);
		buttons.get(BN.MENU_PLAY).text = "PLAY!";

		buttons.add(new Button(screen, (Game.WIDTH / 2) + 10, (Game.HEIGHT / 2)
				- (((BUTTON_HEIGHT + 1) * BUTTON_PIX_HEIGHT) / 2), BUTTON_WIDTH, BUTTON_HEIGHT, "/button_enabled.png",
				"/button_disabled.png", "/button_pressed.png"), BN.MENU_CONNECT);
		buttons.get(BN.MENU_CONNECT).text = "JOIN!";

		buttons.add(new Button(screen, (Game.WIDTH / 2) - ((BUTTON_WIDTH * BUTTON_PIX_WIDTH) / 2), (Game.HEIGHT / 2)
				- ((((BUTTON_HEIGHT + 1) * BUTTON_PIX_HEIGHT) / 2) - 50), BUTTON_WIDTH, BUTTON_HEIGHT,
				"/button_enabled.png", "/button_disabled.png", "/button_pressed.png"), BN.MENU_QUIT);
		buttons.get(BN.MENU_QUIT).text = "Quit";

		buttons.add(new Button(screen, (Game.WIDTH / 2) - ((BUTTON_WIDTH * BUTTON_PIX_WIDTH) / 2), (Game.HEIGHT / 2)
				- (((BUTTON_HEIGHT + 1) * BUTTON_PIX_HEIGHT) / 2), BUTTON_WIDTH, BUTTON_HEIGHT, "/button_enabled.png",
				"/button_disabled.png", "/button_pressed.png"), BN.CONNECT_CONNECT);
		buttons.get(BN.CONNECT_CONNECT).text = "Connect";

		buttons.add(new Button(screen, 239, 224, 7, 2, "/button_enabled.png", "/button_disabled.png",
				"/button_pressed.png"), BN.MESSAGE_SEND);
		buttons.get(BN.MESSAGE_SEND).text = "Send";

		buttons.add(new Button(screen, 0, 24, 7, 2, "/button_enabled.png", "/button_disabled.png",
				"/button_pressed.png"), BN.PREV_LANE);
		buttons.get(BN.PREV_LANE).text = "<";

		buttons.add(new Button(screen, 92, 24, 7, 2, "/button_enabled.png", "/button_disabled.png",
				"/button_pressed.png"), BN.NEXT_LANE);
		buttons.get(BN.NEXT_LANE).text = ">";

		textboxes.add(new TextBox(screen, (Game.WIDTH / 2) - (256 / 2), 30, 256, false, "Username"),
				TN.CONNECT_USERNAME);
		textboxes.add(new TextBox(screen, (Game.WIDTH / 2) - (256 / 2), 70, 256, false, "IP Address"), TN.CONNECT_IP);
		textboxes.add(new TextBox(screen, 0, 223, 240, false, "Message"), TN.MESSAGE_BOX);
	}

	/**
	 * State of the game.
	 * @author AJ
	 *
	 */
	public enum Stage {
		/**
		 * The game is loading and just restarted.
		 */
		LOADING, 
		
		/**
		 * The game is at the main menu.
		 */
		MENU, 
		
		/**
		 * The game is showing the connect screen.
		 */
		CONNECT, 
		
		/**
		 * The game is running one-time code to set up a connection to a server.
		 */
		CONNECT_SETUP, 
		
		/**
		 * The game is showing the play screen.
		 */
		PLAY, 
		
		/**
		 * The game is running one-time code to set up the game as a host.
		 */
		PLAY_SETUP,
		
		/**
		 * The game is running one-time code to set up both the game and the server connections.
		 */
		GENERAL_SETUP, 
		
		/**
		 * The game is running.
		 */
		RUNNING
	}

	/**
	 * Available button names (Unique)(Self explanatory).
	 * @author AJ
	 *
	 */
	public enum BN {
		MENU_PLAY, MENU_CONNECT, MENU_QUIT, CONNECT_CONNECT, MESSAGE_SEND, NEXT_LANE, PREV_LANE
	}

	/**
	 * Available text box names (Unique)(Self explanatory).
	 * @author AJ
	 *
	 */
	public enum TN {
		CONNECT_USERNAME, CONNECT_IP, MESSAGE_BOX
	}
}
