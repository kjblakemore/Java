/**
 * @author Karen Blakemore
 * 
 * A class which defines a shortest path in a road graph.
 */

package roadgraph;

import java.util.List;
import geography.GeographicPoint;

public class MapPath {
	private List <GeographicPoint> path;	
	
	/**
	 * Create a new shortest path of 
	 * @param locs
	 */
	public MapPath(List<GeographicPoint> locs) {
		path = locs;
	}
	
	/**
	 * Check for shortest path between
	 * @param source and
	 * @param dest
	 * if found,
	 * @return path.
	 */
	public List<GeographicPoint> getPath(GeographicPoint source, GeographicPoint dest) {
		int sourceIndex = path.indexOf(source);
		int destIndex = path.indexOf(dest);
		
		if(sourceIndex != -1 && destIndex != -1) return path.subList(sourceIndex + 1, destIndex + 1);
		else return null;
	}
}
