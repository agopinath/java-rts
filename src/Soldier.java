import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;


public class Soldier {
	private Vector2f position;
	private Vector2f velocity;
	private Color color;
	private Ellipse2D.Float body;
	
	public Soldier(Vector2f pos, Color c) {
		position = pos;
		color = c;
		body = new Ellipse2D.Float(position.x, position.y, 12, 12);
	}
	
	public void draw(Graphics2D g) {
		g.setColor(color);
		g.fill(body);
	}
	
	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
		body.x = position.x;
		body.y = position.y;
	}
	
	public void update() {
		
	}
	
	private void seek() {
		
	}
}
