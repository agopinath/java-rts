import java.awt.Color;
import java.awt.Font;
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
	private int[] start, dest;
	
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
	Font font = new Font("Arial", Font.PLAIN, 8);
	for(int row = 0; row < 100; row++) {
			for(int col = 0; col < 25; col++) {
				Terrain t = map.getTerrainAt(row, col);
				int[] coords = GameUtil.squarify(row, t.getX(), t.getY());
				
				g.setColor(Color.red);
				//g.fillRect(coords[0], coords[1], 2, 2);
				g.setFont(font);
				g.drawString(""+(col), (coords[0]), (coords[1]));
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {

		int[] rowcol = Map.screenToMap(e.getX(), e.getY(), map);
		int row = rowcol[0];
		int col = rowcol[1];
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			start = rowcol;
			if (row > 0 && col > 0) {
				GameUtil.changeBright(map.getTerrainAt(row, col), map, 1.4f);
			}
		} else if(e.getButton() == MouseEvent.BUTTON3) {
			dest = rowcol;
			if (row > 0 && col > 0) {
				GameUtil.changeBright(map.getTerrainAt(row, col), map, 0f);
				paintImmediately(0, 0, getWidth(), getHeight());
			}
			
			if(start != null) {
				PathFinder finder = new PathFinder(this);
				ArrayList<Terrain> path = 
						finder.findPath(map.getTerrainAt(start[0], start[1]), map.getTerrainAt(dest[0], dest[1]), map);
				//Terrain[] path = GameUtil.calcSurroundings(new int[] {row, col}, map);
				
				for(Terrain t : path) {
					GameUtil.changeBright(t, map, 2f);
					Fl.og("[" + t.getX() + ", " + t.getY() + "]");
				}
				
				paintImmediately(0, 0, getWidth(), getHeight());
			}
		}
		
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
}
