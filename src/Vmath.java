
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

	public static Vector2f projectOnPath(Vector2f point, Vector2f[] path) {
		if(path.length < 2) return null;
		Vector2f closest = null;
		double minDist = 0;
		for(int n = 1; n < path.length; n++) {
			Vector2f projection = projectSegment(point, path[n-1], path[n]);
			if(projection == null)
				continue;
			double distance = Vmath.len(Vmath.sub(projection, point));
			if(closest == null || distance < minDist) {
				minDist = distance;
				closest = projection;
			}
		}
		
		return closest;
	}
	
	public static Vector2f projectSegment(Vector2f point, Vector2f vertex0, Vector2f vertex1) {
		Vector2f line = Vmath.sub(vertex1, vertex0);
		Vector2f transPoint = Vmath.sub(point, vertex0);
		Vector2f projection = Vmath.add(vertex0, project(transPoint, line));

		if(!between(projection.x, vertex1.x, vertex0.x) || !between(projection.y, vertex1.y, vertex0.y)) 
			return null;

		return projection;
	}

	public static boolean between(double p, double v1, double v2) {
		double diff = v2 - v1;
		double diffHigh = v2 - p;
		double diffLow = p - v1;

		return (diff == 0 && p == v1) || 
				(diff > 0 && diffHigh >= 0 && diffLow >= 0) || 
				(diff < 0 && diffHigh <= 0 && diffLow <= 0);
	}
}
