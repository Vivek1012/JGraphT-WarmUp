package GSOC_JGraphT.WarmUp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.io.DOTImporter;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.GraphImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;

import GSOC_JGraphT.WarmUp.App;

/**
 * Hello world!
 *
 */
public class App<V, E> {

	private Graph<V, E> graph;

	public App(Graph<V, E> graph) {
		// TODO Auto-generated constructor stub
		this.graph = GraphTests.requireDirected(graph, "Graph must be directed");
	}

	@SuppressWarnings("deprecation")
	public static <V, E> void main(String[] args) {

		FileReader in = null;
		// File fle = new
		// File("../WarmUp/src/main/java/GSOC_JGraphT/WarmUp/MyGraph.dot");

		Scanner s = new Scanner(System.in);
		System.out.println("Enter the full path of .dot file along with its extension :");
		String sfile = s.next();
		File fle = new File(sfile);

		System.out.println("Enter the name of First Child :");
		String Child1 = s.next();

		System.out.println("Enter the name of Second Child :");
		String Child2 = s.next();

		try {
			in = new FileReader(fle);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		VertexProvider<String> vp = (a, b) -> a;
		EdgeProvider<String, DefaultEdge> ep = (f, t, l, a) -> new DefaultEdge();

		GraphImporter<String, DefaultEdge> importer = new DOTImporter<String, DefaultEdge>(vp, ep);

		SimpleDirectedGraph<String, DefaultEdge> result = new SimpleDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		try {
			importer.importGraph(result, in);
		} catch (ImportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		App<String, DefaultEdge> finder = new App(result);

		@SuppressWarnings("unchecked")
		Set<V> lcaSet = (Set<V>) finder.MyfindLcas(Child1, Child2);
		if (lcaSet.isEmpty())
			System.out.println("There is/are no common Ancestor's of " + Child1 + " and " + Child2);
		else {
			System.out.print("The Closest Common Ancestor's is/are : ");
			for (V node : lcaSet) {
				System.out.print(node + "\t");
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Set<V> MyfindLcas(String a, String b) {
		Set<V>[] visitedSets = new Set[2];
		// set of nodes visited from a
		visitedSets[0] = new LinkedHashSet<>();
		// set of nodes visited from b
		visitedSets[1] = new LinkedHashSet<>();

		doubleBfs(a, b, visitedSets);
		// all common ancestors of both a and b
		Set<V> intersection;

		// optimization trick: save the intersection using the smaller set
		if (visitedSets[0].size() < visitedSets[1].size()) {
			visitedSets[0].retainAll(visitedSets[1]);
			intersection = visitedSets[0];
		} else {
			visitedSets[1].retainAll(visitedSets[0]);
			intersection = visitedSets[1];
		}

		/*
		 * Find the set of all non-leaves by iterating through the set of common
		 * ancestors. When we encounter a node which is still part of the SLCA(a, b) we
		 * remove its parent(s).
		 */
		Set<V> nonLeaves = new LinkedHashSet<>();
		for (V node : intersection) {
			for (E edge : graph.incomingEdgesOf(node)) {
				if (graph.getEdgeTarget(edge).equals(node)) {
					V source = graph.getEdgeSource(edge);

					if (intersection.contains(source))
						nonLeaves.add(source);
				}
			}
		}

		// perform the actual removal of non-leaves
		// intersection.removeAll(nonLeaves);
		return intersection;
	}

	/**
	 * Perform a simultaneous bottom-up bfs from a and b, saving all visited nodes.
	 * Once a a node has been visited from both a and b, it is no longer expanded in
	 * our search (we know that its ancestors won't be part of the SLCA(x, y) set).
	 */

	private void doubleBfs(String a, String b, Set[] visitedSets) {
		Queue<V>[] queues = new Queue[2];
		queues[0] = new ArrayDeque<>();
		queues[1] = new ArrayDeque<>();

		queues[0].add((V) a);
		queues[1].add((V) b);

		visitedSets[0].add(a);
		visitedSets[1].add(b);

		for (int ind = 0; !queues[0].isEmpty() || !queues[1].isEmpty(); ind ^= 1) {
			if (!queues[ind].isEmpty()) {
				V node = queues[ind].poll();
				try {
				if (!visitedSets[0].contains(node) || !visitedSets[1].contains(node))
					for (E edge : graph.incomingEdgesOf(node)) {
						if (graph.getEdgeTarget(edge).equals(node)) {
							V source = graph.getEdgeSource(edge);

							if (!visitedSets[ind].contains(source)) {
								queues[ind].add(source);
								visitedSets[ind].add(source);
							}
						}
					}
				}
				catch(IllegalArgumentException e)
				{
					System.out.println("No such Node exist in Graph !");
				}
			}
		}
	}

}
