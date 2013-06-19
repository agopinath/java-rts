import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import com.agopinath.lthelogutil.Fl;


public class Soldier {
	private Vector2f position;
	private Vector2f velocity;
	private final static float MAX_SPEED = 2f;
	private final static float MAX_STEER = 0.2f;
	private Color color;
	private Ellipse2D.Float body;
	
	public Soldier(Vector2f pos, Color c) {
		position = pos;
		color = c;
		body = new Ellipse2D.Float(position.x, position.y, 12, 12);
		
		velocity = new Vector2f();
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
	
	public void update(float mx, float my) {
		Vector2f target = new Vector2f(mx - position.x, my - position.y);
		Vector2f steering = Vmath.truncate(Vmath.sub(target, velocity), MAX_STEER);
		
		velocity = Vmath.truncate(Vmath.add(velocity, steering), MAX_SPEED);
		
		position = Vmath.add(position, velocity);
		
		setPosition(position);
	}
}
