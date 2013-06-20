
public class Pathway {
	private Vector2f[] path;
	private Vector2f[] normals;
	private float[] lengths;
	
	private Vector2f segmentNormal;
	private Vector2f chosen;
	
	private float segmentLength;
	private float segmentProjection;
	private float total2DPathLength;
	private int pathLength;
	
	public Pathway(Vector2f[] p) {
		pathLength = p.length;
		path = new Vector2f[pathLength];
		normals = new Vector2f[pathLength];
		lengths = new float[pathLength];
		for(int i = 0; i < p.length; i++) {
			path[i] = p[i];
	        if (i > 0) {
	            normals[i] = Vmath.sub(path[i], path[i-1]);
	            lengths[i] = Vmath.len(normals[i]);
	            normals[i] = Vmath.mult(normals[i], 1/lengths[i]);  // find the normalized vector parallel to the segment
	            
	            total2DPathLength += lengths[i];
	        }
		}
	}
	
	public Vector2f mapPointToPath(Vector2f point) {
		float dist;
		float minDistance = Float.MAX_VALUE;
		Vector2f onPath = null;
		
		for(int i = 1; i < pathLength; i++) {
			segmentLength = lengths[i];
			segmentNormal = normals[i];
			dist = pointToSegmentDistance(point, path[i-1], path[i]); 
			if(dist < minDistance) {
				minDistance = dist;
				onPath = chosen;
			}
		}
		
		return onPath;
	}
	
	public float mapPointToPathDistance(Vector2f point) {
		float dist;
		float minDistance = Float.MAX_VALUE;
		float segmentLengthTotal = 0;
		float pathDistance = 0;
		
		for(int i = 1; i < pathLength; i++) {
			segmentLength = lengths[i];
			segmentNormal = normals[i];
			dist = pointToSegmentDistance(point, path[i-1], path[i]); 
			if(dist < minDistance) {
				minDistance = dist;
				pathDistance = segmentLengthTotal + segmentProjection;
			}
			
			segmentLengthTotal += segmentLength; 
		}
		
		return pathDistance;
	}
	
	public Vector2f mapDistanceToPoint(float pathDist) {
		float remaining = pathDist;
		
		if (pathDist < 0) return path[0];
        if (pathDist >= total2DPathLength) return path[pathLength-1];
        
        
        Vector2f result = null;
		for(int i = 1; i < pathLength; i++) {
			segmentLength = lengths[i];
			
			if(segmentLength < remaining) {
				remaining -= segmentLength;
			} else {
				float ratio = remaining / segmentLength;
				result = Vmath.interpolate(ratio, path[i-1], path[i]);
				break;
			}
		}
		
		return result;
	}
	
	private float pointToSegmentDistance(Vector2f point, Vector2f v0, Vector2f v1) {
		Vector2f local = Vmath.sub(point, v0);
		segmentProjection = Vmath.dot(segmentNormal, local); // using the dot product to project the vector
		
		if(segmentProjection < 0) {
			chosen = v0;
			segmentProjection = 0;
			return Vmath.distBetween(point, v0);
		}
		
		if(segmentProjection > segmentLength) {
			chosen = v1;
			segmentProjection = segmentLength;
			return Vmath.distBetween(point, v1);
		}
		
		chosen = Vmath.mult(segmentNormal, segmentProjection);
		chosen = Vmath.add(chosen, v0);
		
		return Vmath.distBetween(point, chosen);
	}
	
	public Vector2f getPathVectorAt(int idx) {
		if(idx < 0 || idx > pathLength) return null;
		return path[idx];
	}
	
	public int getPathSize() { 
		return pathLength;
	}
	
	public float getTotal2DPathLength() {
		return total2DPathLength;
	}
}
