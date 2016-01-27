/**
 * @author UCSD MOOC development team and Karen Blakemore
 * 
 * A class which represents a graph of geographic locations and corresponding node.
 *
 */
package roadgraph;


import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.PriorityQueue;

import geography.GeographicPoint;
import util.GraphLoader;

/**
 * @author UCSD MOOC development team and Karen Blakemore
 * 
 * A class which represents a geographic map.
 */
public class MapGraph {
	private HashMap <GeographicPoint, MapNode> map;	// location -> node mapping
	private int count;								// When searching for path, count is # nodes visited.
													// This is used to compare efficiency of weighted search methods.	
	/** 
	 * Create a new empty MapGraph 
	 */
	public MapGraph()
	{
		map = new HashMap<GeographicPoint, MapNode>();
		count = 0;
	}
	
	/**
	 * Get the number of vertices (road intersections) in the graph
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices()
	{
		return map.size();
	}
	
	/**
	 * Return the intersections, which are the vertices in this graph.
	 * @return The vertices in this graph as GeographicPoints
	 */
	public Set<GeographicPoint> getVertices()
	{
		return map.keySet();
	}
	
	/**
	 * Get the number of road segments in the graph
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges()
	{
		int edges = 0;
		
		for(MapNode node: map.values()) {
			edges += node.getNumNodeEdges();
		}
		return edges;
	}
	
	/**
	 * Reinitialize map for another search.  This will clear state stored in nodes.
	 */
	public void reInitialize() {
		this.count = 0;
		for(MapNode node: map.values()) {
			node.initialize();
		}
	}

	/** 
	 * Adds a node corresponding to an intersection at a Geographic Point
	 * If the location is already in the graph or null, this method does 
	 * not change the graph.
	 * @param location  The location of the intersection
	 * @return true if a node was added, false if it was not (the node
	 * was already in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location)
	{
		if(location == null || map.get(location) != null) return false;
	
		map.put(location,  new MapNode(location));		
		return true;
	}
	
	/**
	 * Adds a directed edge to the graph from pt1 to pt2.  
	 * Precondition: Both GeographicPoints have already been added to the graph
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road (e.g., residential, city, connector)
	 * @param length The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	public void addEdge(GeographicPoint from, GeographicPoint to, String roadName,
			String roadType, double length) throws IllegalArgumentException {
		
		// Check for invalid arguments.
		if(from==null || to==null || roadName==null || roadType==null || length < 0)
			throw new IllegalArgumentException();
		
		// Get nodes corresponding to the to and from locations.  If not in graph, throw exception.
		MapNode nodeFrom = map.get(from);
		if(nodeFrom == null) throw new IllegalArgumentException();
		
		MapNode nodeTo = map.get(to);
		if(nodeTo == null) throw new IllegalArgumentException();
		
		nodeFrom.addEdge(nodeTo, roadName, roadType, length);	
	}
	
	// Reconstruct shortest path from parent list, in terms of locations, starting with goal node.
	// Save shortest path at each node on the path. This information is used by the A* algorithm.
	private List<GeographicPoint> reconstructPath(HashMap<MapNode, MapNode> parentMap, MapNode startNode, MapNode goalNode, boolean aStar)
	{			
			List<GeographicPoint> path = new ArrayList<GeographicPoint>();
			
			MapNode node = goalNode;
			while(node != startNode) {
				path.add(0, node.getLoc());
				node = parentMap.get(node);
				if(aStar) node.addPath(path);			// add shortest path containing this node.
			} 
			path.add(0, startNode.getLoc());

			return path;
	}
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
	}
	
	/** Find the path from start to goal using breadth first search.
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched Locations saved for visualization of the path.
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, 
			 					     GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		if (start == null || goal == null) {
			System.out.println("Start or goal locations are null!  No path exists.");
			return null;
		}
		
		MapNode startNode = map.get(start);
		if(startNode == null) {
			System.out.println("Start node is null!  No path exists.");
			return null;
		}
		
		MapNode goalNode = map.get(goal);
		if(goalNode == null) {
			System.out.println("Goal node is null!  No path exists.");
			return null;
		}
		
		// queued keeps track of all nodes that have been queued, to avoid revisiting nodes.
		HashSet<MapNode> queued = new HashSet<MapNode>();
		
		// toVisit holds nodes to be visited, ordered in increasing layer depth from start node.
		Queue<MapNode> toVisit = new LinkedList<MapNode>();
		
		// parentMap maps child node -> parent node, and is used to reconstruct the discovered path.
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		
		// Traverse graph from startNode, one layer at a time, 
		// until graph is exhausted or goalNode is found.
		toVisit.add(startNode);
		queued.add(startNode);
		
		while(!toVisit.isEmpty()) {
			MapNode node = toVisit.remove();
			nodeSearched.accept(node.getLoc());	// save location for visualization of path.
			
			if(node == goalNode)  // found path!, return list of locations for path
				return(reconstructPath(parentMap, startNode, goalNode, false));
				
			// Add each adjacent node, that has not yet been seen, to the queue of nodes to be visited.	
			for(MapNode adjacentNode: node.getAdjacentNodes()) {
				
				if (!queued.contains(adjacentNode)) { // only queue nodes once
					queued.add(adjacentNode);
					parentMap.put(adjacentNode, node);
					toVisit.add(adjacentNode);
				}
			}			
		};
		
		// No path found, return null list.
		return null;
	}

	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, 
										  GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		if (start == null || goal == null) {
			System.out.println("Start or goal locations are null!  No path exists.");
			return new LinkedList<GeographicPoint>();
		}
		
		MapNode startNode = map.get(start);
		if(startNode == null) {
			System.out.println("Start node is null!  No path exists.");
			return new LinkedList<GeographicPoint>();
		}
		
		MapNode goalNode = map.get(goal);
		if(goalNode == null) {
			System.out.println("Goal node is null!  No path exists.");
			return new LinkedList<GeographicPoint>();
		}
		
		// When node is taken off the queue, add it to visited.
		// There is no need to visit nodes more than once, because subsequent visits would always be on longer paths.
		HashSet<MapNode> visited = new HashSet<MapNode>();
		
		// PQ holds nodes to be visited, prioritized by minimum distance from start node.
		// Nodes are added to the queue, as the graph is traversed in BFS, while minimizing distance from
		// start node.  A node can be on the queue more than once, if it can be reached from multiple paths.
		PriorityQueue<MapNode> PQ = new PriorityQueue<MapNode>();
		
		// parentMap maps child node -> parent node, and is used to reconstruct the discovered path.
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		
		// Traverse graph from startNode, in BFS fashion, giving priority to minimum distance from start node. 
		// Search ends when a goal node is reached (which will always be the shortest path, because of the priority queue),
		// or graph is exhausted.
		startNode.setBestDistance(0.0);
		PQ.add(startNode);
		
		while(!PQ.isEmpty()) {
			MapNode node = PQ.remove();
			nodeSearched.accept(node.getLoc());	// save location for visualization of path.
			
			// Goal found!  Return reconstructed path.
			if(node == goalNode) return reconstructPath(parentMap, startNode, goalNode, false);
			
			// If node has not been visited, visit it!
			if(!visited.contains(node)) {
				this.count++;
				visited.add(node);
			
				// Get list of adjacent nodes that have current best distance reachable from this node, 
				// updating best distances of all adjacent nodes. 
				List <MapNode> adjacentNodes =  node.bestDistanceAdjacentNodes();		
				for(MapNode adjacentNode: adjacentNodes) {
					// Update parent map & add to priority queue
					parentMap.put(adjacentNode, node);
					PQ.add(adjacentNode);
				}			
			}
		}
		
		// No path found, return null list.
		return null;		
	}

	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
	}
	
	/** Find the path from start to goal using and optimized A-Star search.  The optimization involves using
	 *  previously found shortest paths.
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, 
											 GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		if (start == null || goal == null) {
			System.out.println("Start or goal locations are null!  No path exists.");
			return new LinkedList<GeographicPoint>();
		}
		
		MapNode startNode = map.get(start);
		if(startNode == null) {
			System.out.println("Start node is null!  No path exists.");
			return new LinkedList<GeographicPoint>();
		}
		
		MapNode goalNode = map.get(goal);
		if(goalNode == null) {
			System.out.println("Goal node is null!  No path exists.");
			return new LinkedList<GeographicPoint>();
		}
		
		// When node is taken off the queue, add it to visited.
		// There is no need to visit nodes more than once, because subsequent visits would always be on longer paths.
		HashSet<MapNode> visited = new HashSet<MapNode>();
		
		// PQ holds nodes to be visited, prioritized by minimum distance from start node.
		// Nodes are added to the queue, as the graph is traversed in BFS, while minimizing distance from
		// start node.  A node can be on the queue more than once, if it can be reached from multiple paths.
		PriorityQueue<MapNode> PQ = new PriorityQueue<MapNode>();
		
		// parentMap maps child node -> parent node, and is used to reconstruct the discovered path.
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		
		// Traverse graph from startNode, in BFS fashion, giving priority to minimum distance from start node. 
		// Search ends when a goal node is reached (which will always be the shortest path, because of the priority queue),
		// or graph is exhausted.
		startNode.setBestDistance(0.0);
		PQ.add(startNode);
		
		while(!PQ.isEmpty()) {
			MapNode node = PQ.remove();
			nodeSearched.accept(node.getLoc());	// save location for visualization of path.
			
			List<GeographicPoint> path = node.getPath(goal);	// Retrieve previously calculated shortest path 
																// from this node to goal.
			
			if(node == goalNode || path != null) {				// Path found!  
				// Reconstruct path up to current node and append with saved shortest path from this node to goal.
				List<GeographicPoint> path0 = reconstructPath(parentMap, startNode, node, true);
				if(path != null) path0.addAll(path);	
				return path0;	
			}
			
			// If node has not been visited, visit it!
			if(!visited.contains(node)) {
				this.count++;
				visited.add(node);
			
				// Get list of adjacent nodes that have current best distance reachable from this node, 
				// updating best distances of all adjacent nodes. 
				List <MapNode> adjacentNodes = node.bestDistanceAdjacentNodes(goalNode);		
				for(MapNode adjacentNode: adjacentNodes) {
					// Update parent map & add to priority queue
					parentMap.put(adjacentNode, node);
					PQ.add(adjacentNode);
				}			
			}
		}
		
		// No path found, return null list.
		return null;
	}
	
	public static void main(String[] args)
	{
		/*
		System.out.print("Making a new map...");
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", theMap);
		System.out.println("DONE.");
	
		// Test bfs on simpletest.map
		GeographicPoint start = new GeographicPoint(1.0, 1.0);
		GeographicPoint end = new GeographicPoint(8.0, -1.0);		
		List<GeographicPoint> route = theMap.bfs(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(route);
		
		start = new GeographicPoint(8.0, -1.0);
		end = new GeographicPoint(1.0, 1.0);		
		route = theMap.bfs(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(route);
		
		start = new GeographicPoint(4.0, 1.0);
		end = new GeographicPoint(4.0, 1.0);		
		route = theMap.bfs(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(route);
		
		start = new GeographicPoint(4.0, 1.0);
		end = new GeographicPoint(8.0, -1.0);		
		route = theMap.bfs(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(route);
		
		System.out.print("Making a new map...");
		theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/graders/mod2/map2.txt", theMap);
		System.out.println("DONE.");
		
		start = new GeographicPoint(6.0, 6.0);
		end = new GeographicPoint(0.0, 0.0);		
		route = theMap.bfs(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(route);
			
		// Test dijkstra on simpletest.map
		System.out.print("Making a new map...");
		theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", theMap);
		System.out.println("DONE.");
		
		start = new GeographicPoint(1.0, 1.0);
		end = new GeographicPoint(8.0, -1.0);	
		route = theMap.dijkstra(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(theMap.count + " nodes visited: " + route);
		
		// Test A* on simpletest.map
		System.out.print("Making a new map...");
		theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", theMap);
		System.out.println("DONE.");
		route = theMap.aStarSearch(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(theMap.count + " nodes visited: " + route);
		
		// Test diskstra on utc.map.
		theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		start = new GeographicPoint(32.8648772, -117.2254046);
		end = new GeographicPoint(32.8660691, -117.217393);
			
		route = theMap.dijkstra(start,end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(theMap.count + " nodes visited: " + route);
	
		// Test A* on utc.map.
		theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		start = new GeographicPoint(32.8648772, -117.2254046);
		end = new GeographicPoint(32.8660691, -117.217393);
					
		route = theMap.aStarSearch(start,end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(theMap.count + " nodes visited: " + route);	
		*/
		
		// Test A* on simpletest.map
		System.out.print("Making a new map...");
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", theMap);
		System.out.println("DONE.");
		
		GeographicPoint start = new GeographicPoint(1.0, 1.0);
		GeographicPoint end = new GeographicPoint(8.0, -1.0);
		List <GeographicPoint>route = theMap.aStarSearch(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(theMap.count + " nodes visited: " + route);
		
		// Clear search state in map, then test path reuse with different source, same dest.
		theMap.reInitialize();
		start = new GeographicPoint(4.0, 2.0);
		end = new GeographicPoint(8.0, -1.0);
		route = theMap.aStarSearch(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(theMap.count + " nodes visited: " + route);
		
		// Clear search state in map, then test path reuse with same source, different dest.
		theMap.reInitialize();
		start = new GeographicPoint(1.0, 1.0);
		end = new GeographicPoint(6.5, 0.0);
		route = theMap.aStarSearch(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(theMap.count + " nodes visited: " + route);
		
		// Clear search state in map, then test path reuse with different source, different dest.
		theMap.reInitialize();
		start = new GeographicPoint(4.0, 1.0);
		end = new GeographicPoint(6.5, 0.0);
		route = theMap.aStarSearch(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(theMap.count + " nodes visited: " + route);
		
		// Clear search state in map, then test path reuse with two node, unsearched path.
		theMap.reInitialize();
		start = new GeographicPoint(4.0, 1.0);
		end = new GeographicPoint(7.0, 3.0);
		route = theMap.aStarSearch(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(theMap.count + " nodes visited: " + route);
		
		// Clear search state in map, then test path reuse with single node path.
		theMap.reInitialize();
		start = new GeographicPoint(4.0, 1.0);
		end = new GeographicPoint(4.0, 1.0);
		route = theMap.aStarSearch(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(theMap.count + " nodes visited: " + route);
		
		// Clear search state in map, then test path with nodes not in graph.
		theMap.reInitialize();
		start = new GeographicPoint(1.0, 1.0);
		end = new GeographicPoint(0.0, 0.0);
		route = theMap.aStarSearch(start, end);
		System.out.println("Path from " + start + " to " + end + ": ");
		System.out.println(theMap.count + " nodes visited: " + route);
	}
}
