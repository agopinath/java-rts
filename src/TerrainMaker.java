import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TerrainMaker {
	private TerrainType[][] currLevel;
	private char[][] origLevel;
	private int BLOCK_SIZE, xAlign, yAlign;
	private String levelName = "terrain1.txt";
	private int levelWidth, levelHeight;
	private TerrainType currentBlock;
	private JFrame frame;
	private Rectangle borderRect, levelRect;
	private DrawPanel dPanel;
	private TilePanel tPanel;
	boolean saved = true;
	private List<Image> tileImgs;
	private JComponent parent;
	private Color BROWN = new Color(156, 93, 82);

	public static void main(String args[]) {
		new TerrainMaker(null);

	}

	public TerrainMaker(JComponent parent) {
		this.parent = parent;
		levelName = "assets/maps/" + JOptionPane.showInputDialog("Enter Full Name of Level File to Edit:");
		
		frame = new JFrame("Level Maker");
		loadAssets(new File[] { (new File("assets/tiles/grass")),
				(new File("assets/tiles/dirt")) });
		dPanel = new DrawPanel(92);
		tPanel = new TilePanel(8);
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.getContentPane().add(dPanel);
		frame.getContentPane().add(tPanel);
		currLevel = new TerrainType[100][100];

		currentBlock = TerrainType.DIRT;

		BLOCK_SIZE = 10;

		loadLevel();
		borderRect = new Rectangle(0, 0, BLOCK_SIZE, BLOCK_SIZE);
		levelRect = new Rectangle(0, 0, levelWidth * BLOCK_SIZE, levelHeight * BLOCK_SIZE);
		frame.setResizable(false);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void loadLevel() {
		try {
			origLevel = new char[100][100];
			int colLength = currLevel[0].length;

			Scanner levelReader = new Scanner(new FileReader(levelName));
			int row = 0;
			int col = 0;
			while (levelReader.hasNext()) {
				char block = (char) levelReader.next().codePointAt(0);
				if (block != ' ') {
					origLevel[row][col] = block;
					col++;
				}
				if (col == colLength) {
					col = 0;
					row++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int row = 0; row < origLevel.length; row++) {
			for (int col = 0; col < origLevel[row].length; col++) {

				switch (origLevel[row][col]) {
				case '0':
					currLevel[row][col] = TerrainType.GRASS;
					break;
				case '1':
					currLevel[row][col] = TerrainType.DIRT;
					break;
				}
			}
		}
		levelWidth = currLevel[0].length;
		levelHeight = currLevel.length;
	}

	private void loadAssets(File[] tilesDirs) {
		tileImgs = new ArrayList<Image>();

		for (File assetsDir : tilesDirs) {
			File[] assets = assetsDir.listFiles();
			try {
				tileImgs.add(ImageIO.read(assets[0]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class DrawPanel extends JPanel implements MouseListener,
			MouseMotionListener, KeyListener {
		int percent;

		public DrawPanel(int percent) {
			this.percent = percent;
			setFocusable(true);
			addKeyListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);
			setBackground(Color.BLACK);
		}

		public void paintComponent(Graphics gr) {
			super.paintComponent(gr);
			Graphics2D g = (Graphics2D) gr;
			requestFocusInWindow();

			g.setColor(Color.BLACK);
			for (int row = 0; row < currLevel.length; row++) {
				for (int col = 0; col < currLevel[row].length; col++) {
					switch (currLevel[row][col]) {
					case GRASS:
						g.drawImage(tileImgs.get(0), col * BLOCK_SIZE - xAlign, row * BLOCK_SIZE+ yAlign, BLOCK_SIZE, BLOCK_SIZE, null);
						break;
					case DIRT:
						g.drawImage(tileImgs.get(1), col * BLOCK_SIZE - xAlign, row * BLOCK_SIZE+ yAlign, BLOCK_SIZE, BLOCK_SIZE, null);
						break;
					}
				}
			}
			g.setColor(Color.WHITE);
			g.draw(borderRect);
			g.setColor(Color.GREEN);
			g.draw(levelRect);

		}

		public Dimension getPreferredSize() {
			Dimension d = getParent().getSize();
			int w = d.width * percent / 100;
			int h = d.height * percent / 100;
			return new Dimension(w, h);
		}

		@Override
		public void mouseDragged(MouseEvent evt) {
			saved = false;
			int x = ((evt.getX() + BLOCK_SIZE / 2) / BLOCK_SIZE) * BLOCK_SIZE;
			int y = ((evt.getY() + BLOCK_SIZE / 2) / BLOCK_SIZE) * BLOCK_SIZE;
			int width = BLOCK_SIZE;
			int height = BLOCK_SIZE;
			int arrayRow, arrayCol;
			borderRect.x = x;
			borderRect.y = y;

			arrayRow = ((y + height - yAlign) / BLOCK_SIZE) - 1;
			arrayCol = ((x + width + xAlign) / BLOCK_SIZE) - 1;
			System.out.println(arrayRow + " " + arrayCol);
			currLevel[arrayRow][arrayCol] = currentBlock;

			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent evt) {
			int x = ((evt.getX() + BLOCK_SIZE / 2) / BLOCK_SIZE) * BLOCK_SIZE;
			int y = ((evt.getY() + BLOCK_SIZE / 2) / BLOCK_SIZE) * BLOCK_SIZE;
			borderRect.x = x;
			borderRect.y = y;

			repaint();
		}

		@Override
		public void keyPressed(KeyEvent evt) {
			switch (evt.getKeyCode()) {
			case KeyEvent.VK_0:
				currentBlock = TerrainType.DIRT;
				break;
			case KeyEvent.VK_1:
				currentBlock = TerrainType.GRASS;
				break;
			case KeyEvent.VK_RIGHT:
				xAlign += BLOCK_SIZE;
				break;
			case KeyEvent.VK_LEFT:
				xAlign -= BLOCK_SIZE;
				break;
			case KeyEvent.VK_UP:
				yAlign += BLOCK_SIZE;
				break;
			case KeyEvent.VK_DOWN:
				yAlign -= BLOCK_SIZE;
				break;
			case KeyEvent.VK_ESCAPE:
				if (!saved) {
					switch (JOptionPane.showConfirmDialog(null,
							"You haven't saved yet. "
									+ "Would you like to return and save?")) {
					case JOptionPane.YES_OPTION:
						// Do nothing and return to level maker
						break;
					case JOptionPane.NO_OPTION:
						if (parent != null) {
							frame.setVisible(false);
							frame = null;
							dPanel = null;
							tPanel = null;
							currLevel = null;
							origLevel = null;
							System.gc();
						} else
							System.exit(0);
						break;
					case JOptionPane.CANCEL_OPTION:
						// Do nothing and return to level maker
						break;
					}
				} else {
					if (parent != null) {
						frame.setVisible(false);
						frame = null;
						dPanel = null;
						tPanel = null;
						currLevel = null;
						origLevel = null;
						System.gc();
					} else
						System.exit(0);
				}
				break;
			case KeyEvent.VK_F12:
				for (int row = 0; row < currLevel.length; row++) {
					for (int col = 0; col < currLevel[row].length; col++) {
						try {
							currLevel[row][col] = TerrainType.DIRT;
						} catch (Exception e) {
						}
					}
				}
				break;
			}

			levelRect.setBounds(0 - xAlign, 0 + yAlign,
					levelWidth * BLOCK_SIZE, levelHeight * BLOCK_SIZE);

			repaint();
		}

		@Override
		public void keyReleased(KeyEvent evt) {
		}

		@Override
		public void keyTyped(KeyEvent evt) {
		}

		@Override
		public void mouseClicked(MouseEvent evt) {
		}

		@Override
		public void mouseEntered(MouseEvent evt) {
		}

		@Override
		public void mouseExited(MouseEvent evt) {
		}

		@Override
		public void mousePressed(MouseEvent evt) {
			saved = false;
			int x = ((evt.getX() + BLOCK_SIZE / 2) / BLOCK_SIZE) * BLOCK_SIZE;
			int y = ((evt.getY() + BLOCK_SIZE / 2) / BLOCK_SIZE) * BLOCK_SIZE;
			int width = BLOCK_SIZE;
			int height = BLOCK_SIZE;
			int arrayRow, arrayCol;
			borderRect.setBounds(x, y, BLOCK_SIZE, BLOCK_SIZE);
			arrayRow = ((y + height - yAlign) / BLOCK_SIZE) - 1;
			arrayCol = ((x + width + xAlign) / BLOCK_SIZE) - 1;
			System.out.println(arrayRow + " " + arrayCol);
			currLevel[arrayRow][arrayCol] = currentBlock;

			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

	class TilePanel extends JPanel implements ActionListener {
		int percent;
		JButton saveButton;
		TerrainType tiles[];

		public TilePanel(int percent) {
			this.percent = percent;
			tiles = TerrainType.values();
			saveButton = new JButton("SAVE");
			saveButton.addActionListener(this);

			add(saveButton, BorderLayout.EAST);
			setBackground(Color.green);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			for (int i = 0; i < tiles.length; i++) {
				g.drawString("" + tiles[i].toString(), 70 * i + 20, 25);
				g.drawRect(70 * i + 20, 30, 25, 25);
				g.drawString(tiles[i].toString(), 70 * i + BLOCK_SIZE / 2, 80);
			}
		}

		public Dimension getPreferredSize() {
			Dimension d = getParent().getSize();
			int w = d.width * percent / 100;
			int h = d.height * percent / 100;
			return new Dimension(w, h);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {

			String dateName;
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH~mm~ss");
			Date date = new Date();
			dateName = dateFormat.format(date);
			System.out.println(dateName);
			File outputFile = new File(levelName);
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(outputFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			char tileID = 'R';
			for (int row = 0; row < currLevel.length; row++) {
				for (int col = 0; col < currLevel[row].length; col++) {
					switch (currLevel[row][col]) {
					case GRASS:
						tileID = '0';
						break;
					case DIRT:
						tileID = '1';
						break;
					}
					if (col == currLevel[row].length - 1) {
						writer.append(tileID);
						writer.println();
					} else {
						writer.append(tileID + " ");
					}
					writer.flush();
				}
			}
			writer.close();
			dPanel.requestFocusInWindow();
			saved = true;
		}
	}

}