
public class Vector2f {
	public float x, y;
	
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2f() {
		this(0f, 0f);
	}
	
	public String toString() {
		return String.format("[%.3f, %.3f]; len = %.3f", x, y, Vmath.len(this));
	}
	
	public void normalize() {
		Vector2f v = Vmath.normalize(this);
		x = v.x;
		y = v.y;
	}
	
	public void mult(float scale) {
		Vector2f v = Vmath.mult(this, scale);
		x = v.x;
		y = v.y;
	}
	
	public float len() {
		return Vmath.len(this);
	}
}
