package org.cyanojay.rts;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import javax.swing.JFrame;

import com.agopinath.lthelogutil.Fl;

public class Main {
	private GameFrame gameFrame;
	
	public void start() {
		gameFrame = new GameFrame();
		setUpGame();
	}
	
	private void setUpGame() {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice d = e.getDefaultScreenDevice();
		
		gameFrame.setUndecorated(true);
		gameFrame.setResizable(false);
		gameFrame.setLocationRelativeTo(null);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if(d.isFullScreenSupported()) {
			Fl.og("Fullscreen supported!");
			d.setFullScreenWindow(gameFrame);
		} else {
			Fl.og("Fullscreen not supported, going with maximized screen size.");
			gameFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		}
		
		gameFrame.initPostAdd(); // called after gamePanel is added to a GUI component in case of
		 // any pending events dependent on its being added
		
		gameFrame.setVisible(true);
	}

	public static void main(String[] argv) {
		Main app = new Main();
		app.start();
	}
}