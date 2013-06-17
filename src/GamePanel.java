import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements KeyListener {
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
}
