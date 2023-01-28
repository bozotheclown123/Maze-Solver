package graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.w3c.dom.Node;

/**
 * <P>This class represents a general "directed graph", which could 
 * be used for any purpose.  The graph is viewed as a collection 
 * of vertices, which are sometimes connected by weighted, directed
 * edges.</P> 
 * 
 * <P>This graph will never store duplicate vertices.</P>
 * 
 * <P>The weights will always be non-negative integers.</P>
 * 
 * <P>The WeightedGraph will be capable of performing three algorithms:
 * Depth-First-Search, Breadth-First-Search, and Djikatra's.</P>
 * 
 * <P>The Weighted Graph will maintain a collection of 
 * "GraphAlgorithmObservers", which will be notified during the
 * performance of the graph algorithms to update the observers
 * on how the algorithms are progressing.</P>
 */

/* This class is a weighted directed graph that can perform basic functions of a graph along with some 
 * useful algorithms.
 */

public class WeightedGraph<V> {


	private HashMap<V, HashMap<V, Integer>> weightedGraph;
	Set<V> vertices;
	V smallestVertex;
	V ending;
	Integer smallestCost;

	/* STUDENTS:  You decide what data structure(s) to use to
	 * implement this class.
	 * 
	 * You may use any data structures you like, and any Java 
	 * collections that we learned about this semester.  Remember 
	 * that you are implementing a weighted, directed graph.
	 */






	/* Collection of observers.  Be sure to initialize this list
	 * in the constructor.  The method "addObserver" will be
	 * called to populate this collection.  Your graph algorithms 
	 * (DFS, BFS, and Dijkstra) will notify these observers to let 
	 * them know how the algorithms are progressing. 
	 */
	private Collection<GraphAlgorithmObserver<V>> observerList;


	/** Initialize the data structures to "empty", including
	 * the collection of GraphAlgorithmObservers (observerList).
	 */
	public WeightedGraph() {
		weightedGraph = new HashMap<V, HashMap<V, Integer>>();
		vertices = new HashSet<>(); // A set of all vertices
		observerList = new ArrayList<GraphAlgorithmObserver<V>>();

	}

	/** Add a GraphAlgorithmObserver to the collection maintained
	 * by this graph (observerList).
	 * 
	 * @param observer
	 */
	public void addObserver(GraphAlgorithmObserver<V> observer) {
		observerList.add(observer);
	}

	/** Add a vertex to the graph.  If the vertex is already in the
	 * graph, throw an IllegalArgumentException.
	 * 
	 * @param vertex vertex to be added to the graph
	 * @throws IllegalArgumentException if the vertex is already in
	 * the graph
	 */
	public void addVertex(V vertex) {
		// If weightedGraph tries to add a duplicate, it throws an exception
		if (weightedGraph.containsKey(vertex)) {
			throw new IllegalArgumentException();
		} else {
			weightedGraph.put(vertex, new HashMap<V, Integer>());
			vertices.add(vertex);

		}
	}

	/** Searches for a given vertex.
	 * 
	 * @param vertex the vertex we are looking for
	 * @return true if the vertex is in the graph, false otherwise.
	 */
	public boolean containsVertex(V vertex) {
		if (weightedGraph.containsKey(vertex)) {
			return true;
		}
		return false;
	}

	/** 
	 * <P>Add an edge from one vertex of the graph to another, with
	 * the weight specified.</P>
	 * 
	 * <P>The two vertices must already be present in the graph.</P>
	 * 
	 * <P>This method throws an IllegalArgumentExeption in three
	 * cases:</P>
	 * <P>1. The "from" vertex is not already in the graph.</P>
	 * <P>2. The "to" vertex is not already in the graph.</P>
	 * <P>3. The weight is less than 0.</P>
	 * 
	 * @param from the vertex the edge leads from
	 * @param to the vertex the edge leads to
	 * @param weight the (non-negative) weight of this edge
	 * @throws IllegalArgumentException when either vertex
	 * is not in the graph, or the weight is negative.
	 */


	public void addEdge(V from, V to, Integer weight) {
		if (weightedGraph.containsKey(from) == false) {
			throw new IllegalArgumentException();
		}
		if (weightedGraph.containsKey(to) == false) {
			throw new IllegalArgumentException();
		}
		if (weight < 0) {
			throw new IllegalArgumentException();
		}
		weightedGraph.get(from).put(to, weight); // Creates an edge between two vertices of some weight
	}

	/** 
	 * <P>Returns weight of the edge connecting one vertex
	 * to another.  Returns null if the edge does not
	 * exist.</P>
	 * 
	 * <P>Throws an IllegalArgumentException if either
	 * of the vertices specified are not in the graph.</P>
	 * 
	 * @param from vertex where edge begins
	 * @param to vertex where edge terminates
	 * @return weight of the edge, or null if there is
	 * no edge connecting these vertices
	 * @throws IllegalArgumentException if either of
	 * the vertices specified are not in the graph.
	 */
	public Integer getWeight(V from, V to) {
		if (weightedGraph.containsKey(from) == false) {
			throw new IllegalArgumentException();
		}
		if (weightedGraph.containsKey(to) == false) {
			throw new IllegalArgumentException();
		}
		return weightedGraph.get(from).get(to); // Returns the weight between two vertices
	}

	/** 
	 * <P>This method will perform a Breadth-First-Search on the graph.
	 * The search will begin at the "start" vertex and conclude once
	 * the "end" vertex has been reached.</P>
	 * 
	 * <P>Before the search begins, this method will go through the
	 * collection of Observers, calling notifyBFSHasBegun on each
	 * one.</P>
	 * 
	 * <P>Just after a particular vertex is visited, this method will
	 * go through the collection of observers calling notifyVisit
	 * on each one (passing in the vertex being visited as the
	 * argument.)</P>
	 * 
	 * <P>After the "end" vertex has been visited, this method will
	 * go through the collection of observers calling 
	 * notifySearchIsOver on each one, after which the method 
	 * should terminate immediately, without processing further 
	 * vertices.</P> 
	 * 
	 * @param start vertex where search begins
	 * @param end the algorithm terminates just after this vertex
	 * is visited
	 */
	public void DoBFS(V start, V end) {
		for (GraphAlgorithmObserver<V> a : observerList) {
			a.notifyBFSHasBegun();
		}
		ArrayList<V> visitedSet = new ArrayList<V>();
		Queue<V> myQueue = new LinkedList<V>();
		myQueue.add(start); // Add 1st vertex to the Queue
		while (myQueue.isEmpty() == false) {
			V x = myQueue.remove(); // Take X out of the Queue
			if (visitedSet.contains(x) == false) {
				// VISIT X
				for (GraphAlgorithmObserver<V> b : observerList) {
					b.notifyVisit(x);;
				}
				visitedSet.add(x);
				if (end.equals(x)) {
					for (GraphAlgorithmObserver<V> c : observerList) {
						c.notifySearchIsOver();
					}
					break; 
					// Once the end has been reached, we call notifySearchIsOver()  
					// and break out of the while loop
				}
				// adds all the neighbors of X into the Queue if it's not in the visitedSet
				for (V y : weightedGraph.get(x).keySet()) {
					if (visitedSet.contains(y) == false) {
						myQueue.add(y);
					}
				}
			}
		}
	}

	/** 
	 * <P>This method will perform a Depth-First-Search on the graph.
	 * The search will begin at the "start" vertex and conclude once
	 * the "end" vertex has been reached.</P>
	 * 
	 * <P>Before the search begins, this method will go through the
	 * collection of Observers, calling notifyDFSHasBegun on each
	 * one.</P>
	 * 
	 * <P>Just after a particular vertex is visited, this method will
	 * go through the collection of observers calling notifyVisit
	 * on each one (passing in the vertex being visited as the
	 * argument.)</P>
	 * 
	 * <P>After the "end" vertex has been visited, this method will
	 * go through the collection of observers calling 
	 * notifySearchIsOver on each one, after which the method 
	 * should terminate immediately, without visiting further 
	 * vertices.</P> 
	 * 
	 * @param start vertex where search begins
	 * @param end the algorithm terminates just after this vertex
	 * is visited
	 */
	public void DoDFS(V start, V end) {
		for (GraphAlgorithmObserver<V> a : observerList) {
			a.notifyDFSHasBegun();
		}
		ArrayList<V> visitedSet = new ArrayList<V>();
		Stack<V> myStack = new Stack<V>();
		myStack.add(start); // Adds 1st vertex onto the Stack
		while(myStack.isEmpty() == false) {
			V x = myStack.pop(); // Removes the vertex x from the Stack
			if (visitedSet.contains(x) == false) {
				// VISIT X
				for (GraphAlgorithmObserver<V> b : observerList) {
					b.notifyVisit(x);;
				}
				visitedSet.add(x);
				if (end.equals(x)) {
					for (GraphAlgorithmObserver<V> c : observerList) {
						c.notifySearchIsOver();
					}
					break;
					// Once the end has been reached, we call notifySearchIsOver()  
					// and break out of the while loop
				}
			}
			// adds all the neighbors of X into the Stack if it's not in the visitedSet
			for ( V y : weightedGraph.get(x).keySet()) {
				if (visitedSet.contains(y) == false) {
					myStack.push(y);
				}
			}
		}
	}

	/** 
	 * <P>Perform Dijkstra's algorithm, beginning at the "start"
	 * vertex.</P>
	 * 
	 * <P>The algorithm DOES NOT terminate when the "end" vertex
	 * is reached.  It will continue until EVERY vertex in the
	 * graph has been added to the finished set.</P>
	 * 
	 * <P>Before the algorithm begins, this method goes through 
	 * the collection of Observers, calling notifyDijkstraHasBegun 
	 * on each Observer.</P>
	 * 
	 * <P>Each time a vertex is added to the "finished set", this 
	 * method goes through the collection of Observers, calling 
	 * notifyDijkstraVertexFinished on each one (passing the vertex
	 * that was just added to the finished set as the first argument,
	 * and the optimal "cost" of the path leading to that vertex as
	 * the second argument.)</P>
	 * 
	 * <P>After all of the vertices have been added to the finished
	 * set, the algorithm will calculate the "least cost" path
	 * of vertices leading from the starting vertex to the ending
	 * vertex.  Next, it will go through the collection 
	 * of observers, calling notifyDijkstraIsOver on each one, 
	 * passing in as the argument the "lowest cost" sequence of 
	 * vertices that leads from start to end (I.e. the first vertex
	 * in the list will be the "start" vertex, and the last vertex
	 * in the list will be the "end" vertex.)</P>
	 * 
	 * @param start vertex where algorithm will start
	 * @param end special vertex used as the end of the path 
	 * reported to observers via the notifyDijkstraIsOver method.
	 */
	public void DoDijsktra(V start, V end) {

		for (GraphAlgorithmObserver<V> a : observerList) {
			a.notifyDijkstraHasBegun();
		}
		ArrayList<V> finishedSet = new ArrayList<V>();

		// Setting "null" for all vertices
		HashMap<V, V> pred = new HashMap<>();
		for (V vertex : vertices) {
			pred.put(vertex, null);
		}

		// Setting start = 0 and infinity (Integer.MAX_VALUE) for all other vertices
		HashMap<V, Integer> cost = new HashMap<>();
		for (V vertex : vertices) {
			cost.put(vertex, Integer.MAX_VALUE);
		}
		cost.put(start, 0);
		// The while loop holds true if all the vertices are not in the finishedSet
		while (finishedSet.size() != vertices.size()) {
			smallestCost = Integer.MAX_VALUE;
			// This for loop finds the smallest vertex not in the FinishedSet with the smallest cost
			for (V vertex : cost.keySet()) {
				if (smallestCost > cost.get(vertex) && finishedSet.contains(vertex) == false) {
					smallestCost = cost.get(vertex);
					smallestVertex = vertex;
				}
			}
			
			finishedSet.add(smallestVertex);
			for (GraphAlgorithmObserver<V> b : observerList) {
				b.notifyDijkstraVertexFinished(smallestVertex, smallestCost);
			}
			// For each neighbor of smallestVertex that is not in FinishedSet
			for ( V y : weightedGraph.get(smallestVertex).keySet()) {
				if (finishedSet.contains(y) == false) {
					if (cost.get(smallestVertex) + getWeight(smallestVertex, y) < cost.get(y)) {
						cost.put(y, cost.get(smallestVertex) + getWeight(smallestVertex, y));
						pred.put(y, smallestVertex);
					}
				}
			}
		}
		// A list that consists of vertices that takes the smallest cost to get
		// from the start to the end (given by the parameters)
		List<V> shortestPath = new ArrayList<V>(); 
		ending = end;
		// This while loop will start at the ending vertex and get it's predecessor and
		// continue to do so up until the last two vertices right before the end
		while (pred.get(ending) != pred.get(start)) {
			V newEnding = pred.get(ending);
			shortestPath.add(pred.get(newEnding));
			ending = newEnding;
		}
		
		// for some reason, null gets added to the shortestPath arrayList, IDK why
		// This is also why I have to manually add the last two vertices 
		shortestPath.remove(null); 
		// Since the list is from end to start and we want it from start to end, we just call reverse on it
		Collections.reverse(shortestPath); 
		
		ending = end;
		shortestPath.add(shortestPath.size()  , pred.get(ending)); // vertex right before the end
		shortestPath.add(shortestPath.size()  , ending); // end vertex
		for (GraphAlgorithmObserver<V> c : observerList) {
			c.notifyDijkstraIsOver(shortestPath);
		}
		
		
		
	}
}

