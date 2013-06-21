package org.cyanojay.rts.world.units;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.Collections;

import org.cyanojay.rts.ai.steering.Pathway;
import org.cyanojay.rts.ai.steering.SteeringManager;
import org.cyanojay.rts.util.GameUtil;
import org.cyanojay.rts.util.vector.Vector2f;
import org.cyanojay.rts.util.vector.Vmath;
import org.cyanojay.rts.world.Drawable;
import org.cyanojay.rts.world.map.Terrain;


public class Soldier implements Drawable {
	public final static float MOVE_SPEED = 4f;
	public final static float MAX_STEER = 2f;
	public final static float SIZE = 12f;
	//public final static float SLOWING_RAD = 3f*Terrain.IMG_HEIGHT;
	public final static float STOPPING_RAD = 0.5f*Terrain.IMG_HEIGHT;
	
	private Vector2f position;
	private Vector2f velocity;
	private Color color;
	private Ellipse2D.Float body;
	private long uid;
	private UnitState state;
	private Pathway currPath;
	
	public Soldier(Vector2f pos, Color c) {
		position = pos;
		color = c;
		body = new Ellipse2D.Float(position.x, position.y, SIZE, SIZE);
		velocity = new Vector2f();
		uid = GameUtil.getUID();
	}
	
	public void draw(Graphics2D g, int xOff, int yOff) {
		g.setColor(color);
		g.fill(body);
		Vector2f velocityScale = Vmath.mult(velocity, 4f);
		g.setColor(Color.GREEN);
		g.drawLine((int)(xOff+position.x), (int)(yOff+position.y), (int)(xOff+position.x+velocity.x+velocityScale.x), (int)(yOff+position.y+velocity.y+velocityScale.y));
	}
	
	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
		body.x = position.x-SIZE/2;
		body.y = position.y-SIZE/2;
	}
	
	public Vector2f getVelocity() {
		return velocity;
	}
	
	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}
	
	/*public void update(Vector2f weightedSteerForce) {
		applySteeringForces(weightedSteerForce);
	}
	
	private void applySteeringForces(Vector2f steering) {
		velocity = Vmath.setLength(Vmath.add(velocity, steering), MOVE_SPEED);
		position = Vmath.add(position, velocity);
		
		setPosition(position);
	}*/

	public UnitState getState() {
		return state;
	}

	public void setState(UnitState state) {
		this.state = state;
	}

	public Pathway getCurrPath() {
		return currPath;
	}

	public void setCurrPath(Pathway currPath) {
		this.currPath = currPath;
	}
	
	/*public boolean nearingEndOfPath() {
		if(currPath == null) return false;
		return Vmath.distBetween(position, currPath.getPathVectorAt(currPath.getPathSize()-1)) < SLOWING_RAD;
	}*/
	
	public boolean atEndOfPath() {
		if(currPath == null) return false;
		return Vmath.distBetween(position, currPath.getPathVectorAt(currPath.getPathSize()-1)) < STOPPING_RAD;
	}
	
	public boolean equals(Object other) {
		return (this.uid == ((Soldier) other).uid);
	}
	
	public int hashCode() {
		return (int) uid;
	}
}
