package roadgraph;

import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;

import geography.GeographicPoint;

/**
 * MapNode.java
 * 
 * @author Karen Blakemore
 * 
 * A class to represent a road intersection in a geographic map.  
 * Each intersection is a MapNode which consists of:
 * 		loc - the geographic location (latitude, longitude) of the intersection.
 * 		edges - a list of directed road segments, emanating from the intersection.
 * 		paths - a list of shortest paths from this node.
 * 		bestDistance - used by search algorithm to store current best (i.e., minimum) distance from start node.
 * 	    estimatedDistance - best distance + estimate of remaining distance to goal node.
 */
public class MapNode implements Comparable <MapNode>{
	private GeographicPoint loc;
	private HashSet<MapEdge> edges;
	private HashSet<MapPath> paths;
	private Double bestDistance;
	private Double estimatedDistance;
	
	/** Create a new MapNode at
	 * @param loc - geographic location for new node
	 */
	public MapNode(GeographicPoint loc) {
		this.loc = loc;
		this.edges = new HashSet<MapEdge>();
		this.paths = new HashSet<MapPath>();
		initialize();
	}
	
	/**
	 * Initialize the distance values that are used and updated by the search algorithms. This method is
	 * called prior to a search.
	 */
	public void initialize() {
		this.bestDistance = Double.POSITIVE_INFINITY;		// current best distance from start node
		this.estimatedDistance = Double.POSITIVE_INFINITY;	// bestDistance + estimated distance to goal node
	}
	
	/** Return the number of road segments connected to the intersection */
	public int getNumNodeEdges() {
		return edges.size();
	}
	
	/** Return the geographic location for the intersection */
	public GeographicPoint getLoc() {
		return this.loc;
	}
	
	/** Add a directed road segment from this node, described by:
	 * 
	 * @param toNode - destination node. 
	 * @param name - road name.
	 * @param type - type of road.
	 * @param length - length of the road segment.
	 */
	public void addEdge(MapNode toNode, String name, String type, double length) {
		MapEdge edge = new MapEdge(this, toNode, name, type, length);
		this.edges.add(edge);
	}
	
	/** Return a list of nodes directly reachable from this node, without traveling through additional nodes. */
	public List<MapNode> getAdjacentNodes() {
		List<MapNode> nodes = new LinkedList<MapNode>();
		
		for(MapEdge edge: edges) {
			nodes.add(edge.getEndNode());
		}	
		return nodes;
	}
	
	/** Update the best & estimated distance for each adjacent node and return list of nodes 
	 *  with current shortest path reachable from this node.  For this method, the distance to
	 *  the goal node is not considered, so best and estimated distances are equal. */
	public List<MapNode> bestDistanceAdjacentNodes(){
		List<MapNode> nodes = new LinkedList<MapNode>();
		
		for(MapEdge edge: edges) {
			double newDistance = bestDistance + edge.getDistance();
			
			MapNode adjacentNode = edge.getEndNode();
			if(newDistance < adjacentNode.bestDistance) {
					adjacentNode.bestDistance = adjacentNode.estimatedDistance = newDistance;
					nodes.add(adjacentNode);
			}
		}
		return nodes;
	}
	
	/** Update the best and estimated distances for each adjacent node and return list of nodes 
	 *  with estimated shortest path reachable from this node. The estimated distance is the actual
	 *  distance from the start to this node, plus the estimated additional distance to the goal node. */
	public List<MapNode> bestDistanceAdjacentNodes(MapNode goalNode){
		List<MapNode> nodes = new LinkedList<MapNode>();
		
		for(MapEdge edge: edges) {
			double newDistance = bestDistance + edge.getDistance();
			
			MapNode adjacentNode = edge.getEndNode();			
			if(newDistance < adjacentNode.bestDistance) {
					adjacentNode.bestDistance = newDistance;
					adjacentNode.estimatedDistance = newDistance + adjacentNode.loc.distance(goalNode.loc);
					nodes.add(adjacentNode);
			}
		}
		return nodes;
	}
	
	/** 
	 * Add shortest path from this node.
	 */
	public void addPath(List<GeographicPoint> locs) {
		MapPath path = new MapPath(locs);
		this.paths.add(path);
	}
	
	/**
	 * Get the shortest path from this node to dest, if one was previously found.
	 * @param dest - end point of path.
	 * @return path, if one exists.
	 */
	public List<GeographicPoint> getPath(GeographicPoint dest) {
		for(MapPath path: this.paths) {
			List<GeographicPoint> locs = path.getPath(this.getLoc(), dest);
			if(locs != null) return locs;
		}
		return null;
	}
	
	// Order by increasing bestDistance.
	public int compareTo(MapNode other) {
		return this.estimatedDistance.compareTo(other.estimatedDistance);
	}
	
	// Set bestDistance 
	public void setBestDistance(Double distance) {
		this.bestDistance = distance;
	}
}