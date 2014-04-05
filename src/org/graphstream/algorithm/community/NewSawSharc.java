/*
 * Copyright (C) 2010 Guillaume-Jean Herbiet
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.graphstream.algorithm.community;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * Re-implementation of the SAw-SHARC community detection algorithm (Stability
 * Aware Sharper Heuristic for Assignment of Robust Communities).
 * 
 * @reference TO BE PROVIDED
 * @author Guillaume-Jean Herbiet
 * 
 */
public class NewSawSharc extends Sharc {

	/**
	 * Name of the marker that is used to store weight of links on the graph
	 * that this algorithm is applied to.
	 */
	protected String weightMarker = "weightM";

	/**
	 * Maximum weight on all incoming links
	 */
	protected Double maxWeight = Double.NEGATIVE_INFINITY;

	/**
	 * New instance of the SAw-SHARC community detection algorithm, not attached
	 * to a graph and using the default community marker.
	 */
	public NewSawSharc() {
		super();
	}

	/**
	 * New instance of the SAw-SHARC community detection algorithm, attached to
	 * the specified graph and using the specified marker for the community
	 * attribute.
	 * 
	 * @param graph
	 *            the graph to which the algorithm will be applied
	 * @param marker
	 *            String used as marker for the community attribute
	 */
	public NewSawSharc(Graph graph, String marker) {
		super(graph, marker);
	}

	/**
	 * Create a new SAw-SHARC algorithm instance, attached to the specified
	 * graph, using the specified marker to store the community attribute, and
	 * the specified weightMarker to retrieve the weight attribute of graph
	 * edges.
	 * 
	 * @param graph
	 *            graph to which the algorithm will be applied
	 * @param marker
	 *            community attribute marker
	 * @param weightMarker
	 *            edge weight marker
	 */
	public NewSawSharc(Graph graph, String marker, String weightMarker) {
		super(graph, marker);
		this.weightMarker = weightMarker;
	}

	/**
	 * New instance of the SAw-SHARC community detection algorithm, attached to
	 * the specified graph and using the default community marker.
	 * 
	 * @param graph
	 *            the graph to which the algorithm will be applied
	 */
	public NewSawSharc(Graph graph) {
		super(graph);
	}

	@Override
	public void computeNode(Node u) {
		/*
		 * First construct the cdf based on link weights
		 */
		setMaxWeight(u);

		/*
		 * Then perform the assignment
		 */
		super.computeNode(u);
	}

	/**
	 * Neighborhood weighted similarity between two nodes.
	 * 
	 * @param a
	 *            The first node
	 * @param b
	 *            The second node
	 * @return The similarity value between the two nodes
	 * @complexity O(DELTA) where DELTA is the average node degree in the
	 *             network
	 */
	@Override
	protected Double similarity(Node a, Node b) {
		Double sim;
		// no neighbors or no weight - process normal neighorhood similarity (0 if no common edges, 1 if one neighbor only) 
		if (maxWeight == Double.NEGATIVE_INFINITY) {
			sim = super.similarity(a, b);
		}
		// if there is no neighbor with weight > 0
		else if (maxWeight == 0.0) {
			sim = 0.0;
//			System.out.println(a.getId() + " " + b.getId() + " " + "sim: "+ sim + " weight: " + getWeightInLinkFrom(a, b) + ", maxWeigt: " + maxWeight);	
		}
		else {
			sim = super.similarity(a, b) * (getWeightInLinkFrom(a, b) / maxWeight);
//			System.out.println(a.getId() + " " + b.getId() + " " + "sim: "+ super.similarity(a, b) + " weight: " + getWeightInLinkFrom(a, b) + ", maxWeigt: " + maxWeight);	
		}
		return sim;
	}

	protected void setMaxWeight(Node u) {

		maxWeight = Double.NEGATIVE_INFINITY;
		for (Edge e : u.getEnteringEdgeSet()) {
			Double weight = getWeightInLinkFrom(u, e.getOpposite(u));
			if (weight > maxWeight) {
				maxWeight = weight;
//				System.out.println("Setting maxweight: " + maxWeight);
			}
		}
	}

	protected Double getWeightInLinkFrom(Node a, Node b) {
		Double weight = 0.0;
		if (a.hasEdgeFrom(b.getId())
				&& a.<Edge>getEdgeFrom(b.getId()).hasAttribute(weightMarker)) {
			weight = (Double) a.<Edge>getEdgeFrom(b.getId()).getAttribute(
					weightMarker);
		}
//		System.out.println("weightmarker " + weightMarker + " weight of node " + a.getId() + " and " + b.getId() + " = " + weight);
		return weight;

	}
}
