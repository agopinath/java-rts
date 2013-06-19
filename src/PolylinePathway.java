
public class PolylinePathway {
	private Vector2f[] path;
	private Vector2f[] normals;
	private float[] lengths;
	
	private Vector2f segmentNormal;
	private Vector2f chosen;
	
	private float segmentLength;
	private float segmentProjection;
	private float totalPathLength;
	private int pointCount;
	
	public PolylinePathway(Vector2f[] p) {
		pointCount = p.length;
		path = new Vector2f[pointCount];
		normals = new Vector2f[pointCount];
		lengths = new float[pointCount];
		for(int i = 0; i < p.length; i++) {
			path[i] = p[i];
	        if (i > 0) {
	            // compute the segment length
	            normals[i] = Vmath.sub(path[i], path[i-1]);
	            lengths[i] = Vmath.len(normals[i]);

	            // find the normalized vector parallel to the segment
	            normals[i] = Vmath.mult(normals[i], 1/lengths[i]);

	            // keep running total of segment lengths
	            totalPathLength += lengths[i];
	        }
		}
	}
	
	public Vector2f mapPointToPath(Vector2f point) {
		float d;
		float minDistance = Float.MAX_VALUE;
		Vector2f onPath = null;
		
		for(int i = 1; i < pointCount; i++) {
			segmentLength = lengths[i];
			segmentNormal = normals[i];
			d = pointToSegmentDistance(point, path[i-1], path[i]); 
			if(d < minDistance) {
				minDistance = d;
				onPath = chosen;
			}
		}
		
		return onPath;
	}
	
	public float mapPointToPathDistance(Vector2f point) {
		float d;
		float minDistance = Float.MAX_VALUE;
		float segmentLengthTotal = 0;
		float pathDistance = 0;
		
		for(int i = 1; i < pointCount; i++) {
			segmentLength = lengths[i];
			segmentNormal = normals[i];
			d = pointToSegmentDistance(point, path[i-1], path[i]); 
			if(d < minDistance) {
				minDistance = d;
				pathDistance = segmentLengthTotal + segmentProjection;
			}
			
			segmentLengthTotal += segmentLength; 
		}
		
		return pathDistance;
	}
	
	public Vector2f mapDistanceToPoint(float pathDist) {
		float remaining = pathDist;
		
		if (pathDist < 0) return path[0];
        if (pathDist >= totalPathLength) return path[pointCount-1];
        
        
        Vector2f result = null;
		for(int i = 1; i < pointCount; i++) {
			segmentLength = lengths[i];
			
			if(segmentLength < remaining) {
				remaining -= segmentLength;
			} else {
				float ratio = remaining / segmentLength;
				result = interpolate(ratio, path[i-1], path[i]);
				break;
			}
		}
		
		return result;
	}
	
	private float pointToSegmentDistance(Vector2f point, Vector2f ep0, Vector2f ep1) {
		Vector2f local = Vmath.sub(point, ep0);
		segmentProjection = Vmath.dot(segmentNormal, local);
		
		if(segmentProjection < 0) {
			chosen = ep0;
			segmentProjection = 0;
			return distBetween(point, ep0);
		}
		
		if(segmentProjection > segmentLength) {
			chosen = ep1;
			segmentProjection = segmentLength;
			return distBetween(point, ep1);
		}
		
		chosen = Vmath.mult(segmentNormal, segmentProjection);
		chosen = Vmath.add(chosen, ep0);
		
		return distBetween(point, chosen);
	}
	
	/*private static float pointToSegmentDistance(Vector2f p, Vector2f v, Vector2f w) {
		float l2 = (float) Math.pow(w.sub(v).len(), 2);
		if(l2 == 0.0f) return distBetween(p, v);
		
		float t = Vmath.dot(p.sub(v), w.sub(v)) / l2;
		if(t < 0.0f) return distBetween(p, v); // if it's before the first vertex
		else if(t > 1.0) return distBetween(p, w); // if it's after the second vertex
		
		Vector2f fallsOn = v.add(Vmath.mult(w.sub(v), t)); // v + t * (w - v), find where it falls on the segment
		
		return distBetween(p, fallsOn); // return the distance between the point and where it falls on the segment
	}
	
	private static Vector2f getFallsOn(Vector2f p, Vector2f v, Vector2f w) {
		float l2 = (float) Math.pow(w.sub(v).len(), 2);
		float t = Vmath.dot(p.sub(v), w.sub(v)) / l2;
		
		return v.add(Vmath.mult(w.sub(v), t)); // v + t * (w - v), find where it falls on the segment
	}*/
	
	
	
	private static Vector2f interpolate(float ratio, Vector2f v0, Vector2f v1) {
		return Vmath.add(v0, Vmath.mult(Vmath.sub(v1, v0), ratio));
	}
	
	public Vector2f projectOnPath(Vector2f point) {
		if(path.length < 2) return null;
		Vector2f closest = null;
		float minDist = 0;
		for(int n = 1; n < path.length; n++) {
			Vector2f projection = projectSegment(point, path[n-1], path[n]);
			if(projection == null) continue;
			float distance = Vmath.len(Vmath.sub(projection, point));
			if(closest == null || distance < minDist) {
				minDist = distance;
				closest = projection;
			}
		}
		
		return closest;
	}
	
	public Vector2f projectSegment(Vector2f point, Vector2f vertex0, Vector2f vertex1) {
		Vector2f line = Vmath.sub(vertex1, vertex0);
		Vector2f transPoint = Vmath.sub(point, vertex0);
		Vector2f projection = Vmath.sub(vertex0, Vmath.project(transPoint, line));

		if(!between(projection.x, vertex1.x, vertex0.x) || !between(projection.y, vertex1.y, vertex0.y)) 
			return null;

		return projection;
	}
	
	public Vector2f getPathPositionAt(int idx) {
		if(idx < 0 || idx > pointCount) return null;
		return path[idx];
	}
	
	public int getPathLength() {
		return pointCount;
	}
	
	public static boolean between(float p, float v1, float v2) {
		float diff = v2 - v1;
		float diffHigh = v2 - p;
		float diffLow = p - v1;

		return (diff == 0 && p == v1) || 
				(diff > 0 && diffHigh >= 0 && diffLow >= 0) || 
				(diff < 0 && diffHigh <= 0 && diffLow <= 0);
	}
	
	public static float distBetween(Vector2f pos1, Vector2f pos2) {
		return (float) Math.sqrt((pos2.x - pos1.x) * (pos2.x - pos1.x) + (pos2.y - pos1.y) * (pos2.y - pos1.y));
	}
}
