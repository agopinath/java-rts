import javax.swing.JFrame;

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
		frame.addKeyListener(gamePanel);
		frame.addMouseListener(gamePanel);
		
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	public static void main(String[] argv) {
		Main app = new Main();
		app.start();
	}
}
