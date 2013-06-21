package org.cyanojay.rts.util.vector;

public class Vmath {
	public static float dot(Vector2f u, Vector2f v) {
		return (u.x * v.x + u.y * v.y);
	}
	
	public static float len(Vector2f v) {
		return (float) Math.sqrt(v.x * v.x + v.y * v.y);
	}
	
	public static float angle(Vector2f u, Vector2f v) {
		return (float) Math.acos(dot(normalize(u),normalize(v)));
	}
	
	public static Vector2f add(Vector2f u, Vector2f v) {
		return new Vector2f(u.x + v.x, u.y + v.y);
	}
	
	public static Vector2f sub(Vector2f u, Vector2f v) {
		return new Vector2f(u.x - v.x, u.y - v.y);
	}
	
	public static Vector2f mult(Vector2f v, float scale) {
		return new Vector2f(v.x * scale, v.y * scale);
	}
	
	public static Vector2f normalize(Vector2f v) {
		return mult(v, 1f/(len(v)));
	}
	
	public static Vector2f project(Vector2f vector, Vector2f onto) {
        return mult(onto, dot(vector, onto) / dot(onto, onto));
    }
	
	public static Vector2f truncate(Vector2f v, float maxLen) {
		float scale = maxLen / len(v);
		scale = (scale < 1.0f) ? scale : 1.0f;
		
		return mult(v, scale);
	}
	
	public static Vector2f setLength(Vector2f v, float len) {
		if(v.x == 0f && v.y == 0f) return v;
		return mult(normalize(v), len);
	}
	
	public static Vector2f interpolate(float ratio, Vector2f v0, Vector2f v1) {
		return Vmath.add(v0, Vmath.mult(Vmath.sub(v1, v0), ratio));
	}

	public static float distBetween(Vector2f pos1, Vector2f pos2) {
		return (float) Math.sqrt((pos2.x - pos1.x) * (pos2.x - pos1.x) + (pos2.y - pos1.y) * (pos2.y - pos1.y));
	}
}
