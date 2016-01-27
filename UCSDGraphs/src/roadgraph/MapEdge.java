package roadgraph;

/**
 * MapEdge.java
 * @author Karen Blakemore
 * 
 * A class that represents a directed road segment in a map.
 */

public class MapEdge {
	private MapNode end;		// ending node
	private String name;		// name of road.
	private String type;		// type of road (e.g., residential, city, connector).
	private double distance;	// length of road segment in kilometers.
	
	/** Create a MapEdge to represent a directed road segment */
	public MapEdge(MapNode start, MapNode end, String name, String type, double length) {
		this.end = end;
		this.name = name;
		this.type = type;
		this.distance = length;
	}
	
	/** Return the ending node for this edge */
	public MapNode getEndNode() {
		return this.end;
	}
	
	/** Return the distance associated with this edge */
	public double getDistance() {
		return this.distance;
	}
}