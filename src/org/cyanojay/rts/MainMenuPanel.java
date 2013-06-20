package org.cyanojay.rts;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class MainMenuPanel extends JPanel {
	private JButton start;
	private JButton quit;
	
	public MainMenuPanel() {
		start = new JButton("Start");
		quit = new JButton("Quit");
		Box box = new Box(BoxLayout.Y_AXIS);
		
        box.add(Box.createVerticalGlue());
        setUpComponents(box, new JComponent[] {start, quit});
        box.add(Box.createVerticalGlue());
        
        add(box);
	}
	
	private void setUpComponents(Box container, JComponent[] comps) {
		for(JComponent comp : comps) {
			comp.setAlignmentX(JComponent.CENTER_ALIGNMENT);
			container.add(comp);
		}
	}
}
