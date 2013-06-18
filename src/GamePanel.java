import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.swing.JPanel;

import com.agopinath.lthelogutil.Fl;

public class GamePanel extends JPanel implements KeyListener, MouseListener {
	private Map map;
	//private boolean moving = false;
	
	public GamePanel() {
		map = new Map(new File("assets/maps/terrain3.txt"), new File("assets/tiles/"));
		addKeyListener(this);
		
		Thread gameLoop = new Thread(new GameLoop());
		gameLoop.start();
	}

	private class GameLoop implements Runnable {
		private boolean gameRunning = true;
		private boolean gamePaused = false;
		private static final float GAME_FPS = 30f;
		private static final float TIME_BETWEEN_UPDATES = 1000000000 / GAME_FPS;
		private static final int MAX_UPDATES_BEFORE_RENDER = 1;
		private static final float TARGET_FPS = 60;
		private static final float TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;
		
		@Override
		public void run() {
			double lastUpdateTime = System.nanoTime();
			double lastRenderTime = System.nanoTime();
			
			int lastSecondTime = (int) (lastUpdateTime / 1000000000);

			while (gameRunning) {
				double now = System.nanoTime();
				int updateCount = 0;

				if (!gamePaused) {
					while (now - lastUpdateTime > TIME_BETWEEN_UPDATES&& updateCount < MAX_UPDATES_BEFORE_RENDER) {
						updateGame();
						lastUpdateTime += TIME_BETWEEN_UPDATES;
						updateCount++;
					}

					if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
						lastUpdateTime = now - TIME_BETWEEN_UPDATES;
					}

					repaint();
					
					lastRenderTime = now;

					int thisSecond = (int) (lastUpdateTime / 1000000000);
					if (thisSecond > lastSecondTime) {
						lastSecondTime = thisSecond;
					}

					while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
						Thread.yield();
						
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {}

						now = System.nanoTime();
					}
				}
			}
		}
		
		private void updateGame() {
			/*if(moving) {
				r.x++;
				r.y++;
			}*/
		}
	}
	
	private Rectangle r = new Rectangle(50, 50);
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		
		Graphics2D g = (Graphics2D) gr;
		drawMap(g);
		drawEntities(g);
	}
	
	private void drawMap(Graphics2D g) {
		map.draw(g);
	}
	
	private void drawEntities(Graphics2D g) {
		//g.clearRect(0, 0, getWidth(), getHeight());
		//g.fill(r);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		//moving = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//moving = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {
		int[] rowcol = getMapRowCol(e.getX(), e.getY());
		Fl.og("Map Row/col: [" + rowcol[0] + ", " + rowcol[1] + "]");
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}

	Polygon topL = new Polygon(new int[] { 0, Terrain.IMG_WIDTH / 2, 0 },
			new int[] { 0, 0, Terrain.IMG_HEIGHT / 2 }, 3);
	Polygon topR = new Polygon(new int[] { Terrain.IMG_WIDTH / 2, Terrain.IMG_WIDTH, Terrain.IMG_WIDTH }, 
			new int[] { 0, Terrain.IMG_HEIGHT / 2, 0 }, 3);
	Polygon botL = new Polygon(new int[] { 0, Terrain.IMG_WIDTH / 2, 0 },
			new int[] { Terrain.IMG_HEIGHT / 2, Terrain.IMG_HEIGHT,Terrain.IMG_HEIGHT }, 3);
	Polygon botR = new Polygon(new int[] { Terrain.IMG_WIDTH / 2,Terrain.IMG_WIDTH, Terrain.IMG_WIDTH },
			new int[] { Terrain.IMG_HEIGHT, Terrain.IMG_HEIGHT / 2,Terrain.IMG_HEIGHT }, 3);

	private int[] getMapRowCol(int x, int y) {
		int row = -1;
		int col = -1;

		int regionX = (int) (x / Terrain.IMG_WIDTH);
		int regionY = (int) (y * 2 / Terrain.IMG_HEIGHT);

		int mouseMapX = x % Terrain.IMG_WIDTH;
		int mouseMapY = y % Terrain.IMG_HEIGHT;

		int regionDX = 0;
		int regionDY = 0;

		if (topL.contains(mouseMapX, mouseMapY)) {
			Fl.og("topL");
			regionDX = -1;
			regionDY = -1;
		} else if (topR.contains(mouseMapX, mouseMapY)) {
			Fl.og("topR");
			regionDX = 0;
			regionDY = -1;
		} else if (botL.contains(mouseMapX, mouseMapY)) {
			Fl.og("botL");
			regionDX = -1;
			regionDY = 1;
		} else if (botR.contains(mouseMapX, mouseMapY)) {
			Fl.og("botR");
			regionDX = 0;
			regionDY = 1;
		}

		col = regionX + regionDX;
		row = regionY + regionDY - 4;

		if (row > 0 && col > 0) {
			BufferedImage img = (BufferedImage) map.mapArray[(int) row][col].getImage();
			BufferedImage dest = deepCopy(img);
			RescaleOp rescaleOp = new RescaleOp(1.4f, 15, null);

			rescaleOp.filter(img, dest);

			map.mapArray[(int) row][col].setImage(dest);
		}
		
		return new int[] {row, col};
	}
	
	public static BufferedImage deepCopy(BufferedImage img) {
		 ColorModel cm = img.getColorModel();
		 return new BufferedImage(cm, img.copyData(null), cm.isAlphaPremultiplied(), null);
	}
	
	public static boolean pntInTriangle(double px, double py, double x1, double x2, double x3, double y1, double y2,  double y3) {

	    double o1 = getOrientationResult(x1, y1, x2, y2, px, py);
	    double o2 = getOrientationResult(x2, y2, x3, y3, px, py);
	    double o3 = getOrientationResult(x3, y3, x1, y1, px, py);

	    return (o1 == o2) && (o2 == o3);
	}

	private static int getOrientationResult(double x1, double y1, double x2, double y2, double px, double py) {
	    double orientation = ((x2 - x1) * (py - y1)) - ((px - x1) * (y2 - y1));
	    if (orientation > 0) {
	        return 1;
	    }
	    else if (orientation < 0) {
	        return -1;
	    }
	    else {
	        return 0;
	    }
	}
}
