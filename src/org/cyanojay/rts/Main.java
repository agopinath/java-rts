package org.cyanojay.rts;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import javax.swing.JFrame;

import com.agopinath.lthelogutil.Fl;

public class Main {
	private JFrame frame;
	private GamePanel gamePanel; //, menuPanel;
	
	public void start() {
		frame = new JFrame("Java RTS");
		gamePanel = new GamePanel();
		//menuPanel = new MainMenuPanel();
		//frame.setLayout(new GridBagLayout());
		//frame.add(menuPanel, new GridBagConstraints());
		frame.add(gamePanel);
		
		gamePanel.initPostAdd(); // called after gamePanel is added to a GUI component in case of
								 // any pending events dependent on its being added
		frame.addKeyListener(gamePanel);
		
		setUpGame(frame);
	}
	
	private void setUpGame(JFrame gameFrame) {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice d = e.getDefaultScreenDevice();
		
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if(d.isFullScreenSupported()) {
			Fl.og("Fullscreen supported!");
			d.setFullScreenWindow(frame);
		} else {
			Fl.og("Fullscreen not supported, going with maximized screen size.");
			frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		}
		
		frame.setVisible(true);
	}

	public static void main(String[] argv) {
		Main app = new Main();
		app.start();
	}
}
