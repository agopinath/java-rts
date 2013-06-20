package org.cyanojay.rts;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.cyanojay.rts.ai.PathFinder;
import org.cyanojay.rts.util.GameUtil;
import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.world.map.Map;
import org.cyanojay.rts.world.map.Terrain;
import org.cyanojay.rts.world.map.Viewport;
import org.cyanojay.rts.world.units.Soldier;

import com.agopinath.lthelogutil.Fl;

public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
	private Map map;
	private Viewport vp;
	private List<Soldier> sols;
	private Vector2f[] path;
	private boolean drawPath;
	
	public GamePanel() {
		setPreferredSize(new Dimension(800, 600));
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		File[] mapAssets = new File[] {
			new File("assets/tiles/grass/"), new File("assets/tiles/dirt/")
		};
		map = new Map(new File("assets/maps/terrain3.txt"), mapAssets);
		
		initEntities();
		
		Thread gameLoop = new Thread(new GameLoop());
		gameLoop.start();
	}
	
	public void initPostAdd() { // to be called by parent container after it has been added
		initViewport();
		map.setViewport(vp);
	}
	
	public void initViewport() {
		Dimension viewArea = getPreferredSize();
		int[] rowcol = map.screenToMap(0 + viewArea.width, 0 + viewArea.height, map); // get row/col of bottom right corner of viewport
		vp = new Viewport(map, 0, 0, rowcol[0], rowcol[1]);
		
		Fl.og("Viewarea edges: " + viewArea.width + ", " + viewArea.height);
	}
	
	public void initEntities() {
		sols = new ArrayList<Soldier>();
		sols.add(new Soldier(new Vector2f(128, 128), Color.RED));
		//sols.add(new Soldier(new Vector2f(256, 512), Color.RED));
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
					while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
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
			updateEntities();
		}

		private void updateEntities() {
			for(Soldier sol : sols) {
				sol.update();
			}
		}
	}
	
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		
		Graphics2D g = (Graphics2D) gr;
		drawMap(g);
		//drawPath(g);
		drawEntities(g);
	}
	
	private void drawMap(Graphics2D g) {
		map.draw(g);
	}
	
	private void drawEntities(Graphics2D g) {
		for(Soldier sol : sols) {
			sol.draw(g, vp.getOffsetX(), vp.getOffsetY());
		}
	}
	
	/*private void drawPath(Graphics2D g) {
		if(path == null || !drawPath) return;
		float pathRad = SteeringManager.PATH_RADIUS;
		for(int i = 1; i < path.length; i++) {
			g.setColor(Color.LIGHT_GRAY);
			g.drawOval((int)(path[i-1].x-pathRad), (int)(path[i-1].y-pathRad), (int)(pathRad*2), (int)(pathRad*2));
			g.setColor(Color.CYAN);
			g.drawLine((int)path[i-1].x, (int)path[i-1].y, (int)path[i].x, (int)path[i].y);
		}
	}*/
	
	@Override
	public void keyPressed(final KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_RIGHT:
				vp.shiftHorizontally(1);
				break;
			case KeyEvent.VK_LEFT:
				vp.shiftHorizontally(-1);
				break;
			case KeyEvent.VK_UP:
				vp.shiftVertically(-1);
				break;
			case KeyEvent.VK_DOWN:
				vp.shiftVertically(1);
				break;
			case KeyEvent.VK_F12:
				map.resetMap();
				drawPath = false;
				break;
		}
		
		Fl.og(vp.getOffsetX() + " " + vp.getOffsetY());
	}
	
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}

	
	@Override
	public void mouseClicked(final MouseEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(e.getButton() == MouseEvent.BUTTON3) {
					int[] start = map.screenToMap((int)sols.get(0).getPosition().x+vp.getOffsetX(), (int)sols.get(0).getPosition().y+vp.getOffsetY(), map);
					int[] dest = map.screenToMap(e.getX(), e.getY(), map);
					
					PathFinder finder = new PathFinder();
					ArrayList<Terrain> p = finder.findPath(map.getTerrainAt(start[0], start[1]), map.getTerrainAt(dest[0], dest[1]), map);
					path = new Vector2f[p.size()];
					for(int i = 0; i < p.size(); i++) {
						Terrain t = p.get(i);
						//path[i] = new Vector2f(t.getX(), t.getY());
						path[i] = new Vector2f(t.getX()+Terrain.IMG_WIDTH/2, t.getY()+Terrain.IMG_HEIGHT/2);
						GameUtil.changeBright(t, map, 1.4f);
					}
					
					sols.get(0).setPath(path);
					paintImmediately(0, 0, getWidth(), getHeight());
					
					drawPath = true;
				} else {
					int[] d = map.screenToMap(e.getX(), e.getY(), map);
					Fl.og(d[0] + " " + d[1]);
				}
			}
		});
	}
	
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}
}
