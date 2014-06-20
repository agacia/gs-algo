package org.graphstream.algorithm.community.test;

import static org.junit.Assert.*;

import org.graphstream.algorithm.community.Sharc_my;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Before;
import org.junit.Test;

public class TestSharc_my {

	private Graph graph;
	
	public Graph createGraph() {
		Graph graph = new SingleGraph("Tutorial 1");
		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		return graph;
	}
	
	@Before
	public void setUp() throws Exception {
		graph = createGraph();
	}

	@Test
	public void test_two_nodes_no_edge() {
		Sharc_my sharc = new Sharc_my(graph);
		Node a = graph.getNode("A");
		Node b = graph.getNode("B");
		Double sim = sharc.getSimilarity(a, b);
		assertEquals(0.0, sim);
	}
	
	@Test
	public void test_two_nodes_one_edge() {
		Sharc_my sharc = new Sharc_my(graph);
		graph.addEdge("AB", "A", "B");
		Node a = graph.getNode("A");
		Node b = graph.getNode("B");
		Double sim = sharc.getSimilarity(a, b);
		assertEquals(1.0, sim);
	}

	@Test
	public void test_three_nodes_one_edge() {
		Sharc_my sharc = new Sharc_my(graph);
		graph.addEdge("AB", "A", "B");
		graph.addEdge("AC", "A", "C");
		Node a = graph.getNode("A");
		Node b = graph.getNode("B");
		Double sim = sharc.getSimilarity(a, b);
		System.out.println("sim "+ sim);
	}
	
	@Test
	public void test_three_nodes_two_edges() {
		Sharc_my sharc = new Sharc_my(graph);
		graph.addEdge("AB", "A", "B");
		graph.addEdge("AC", "A", "C");
		graph.addEdge("BC", "B", "C");
		Node a = graph.getNode("A");
		Node b = graph.getNode("B");
		Double sim = sharc.getSimilarity(a, b);
		System.out.println("sim "+ sim);
		
	}
}
