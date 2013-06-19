import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.agopinath.lthelogutil.Fl;

public class GamePanel extends JPanel implements KeyListener, MouseListener {
	private Map map;
	
	public GamePanel() {
		map = new Map(new File("assets/maps/terrain3.txt"), new File("assets/tiles/grass/"));
		addKeyListener(this);
		addMouseListener(this);
		
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
			
		}
	}
	
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
		g.setColor(Color.red);
		for(int row = 0; row < map.getHeight(); row++) {
			g.drawLine(0, row*Terrain.IMG_HEIGHT, getWidth(), row*Terrain.IMG_HEIGHT);
		}
		
		for(int col = 0; col < map.getHeight(); col++) {
			g.drawLine(col*Terrain.IMG_WIDTH, 0, col*Terrain.IMG_WIDTH, getHeight());
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}

	int[] start, dest;
	@Override
	public void mouseClicked(MouseEvent e) {
		int[] rowcol = Map.screenToMap(e.getX(), e.getY(), map);
		Fl.og(rowcol[0] + " " + rowcol[1]);
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			start = rowcol;
		} else if(e.getButton() == MouseEvent.BUTTON3 && start != null) {
			dest = rowcol;
			PathFinder path = new PathFinder(this);
			ArrayList<Terrain> p = path.findPath(map.getTerrainAt(start[0], start[1]), map.getTerrainAt(dest[0], dest[1]), map);
			for(Terrain t : p) {
				GameUtil.changeBright(t, map, 1.4f);
				//Fl.og("[" + t.getRow() + ", " + t.getCol() + "]");
			}
			this.paintImmediately(0, 0, getWidth(), getHeight());
		}	
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}
