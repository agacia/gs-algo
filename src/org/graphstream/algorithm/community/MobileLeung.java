
/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */

/**
 * 	Agata Grzybek     <agata.grzybek@uni.lu>
 */

package org.graphstream.algorithm.community;

import java.io.BufferedWriter;
import java.util.Dictionary;
import java.util.HashMap;

import org.graphstream.algorithm.measure.MobilityMeasure;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;


/**
 * This class implements an improved community detection algorithm based on the
 * epidemic label propagation paradigm the was presented by Leung <i>et al</i>.
 * 
 * @reference I. X. Y. Leung, P. Hui, P. Lio`, and J. Crowcroft, “Towards Real-
 *            Time Community Detection in Large Networks,” Physical Review E
 *            (Statistical, Nonlinear, and Soft Matter Physics), vol. 79, no. 6,
 *            pp. 066 107+, 2009.
 * 
 * @author Agata Grzybek
 * 
 */
public class MobileLeung extends EpidemicCommunityAlgorithm {

	/**
	 * Name of the marker that is used to store weight of links on the graph
	 * that this algorithm is applied to.
	 */
	protected String weightMarker = "weight";

	protected MobilityMeasure mobilityMeasure;
	
	HashMap<MobileMarker, String> mobilityMarkers;
	protected String mobilityMetric = null;
	protected BufferedWriter outLog = null;
	
	/**
	 * Comparable node characteristic preference exponent
	 */
	protected double m = 0.1;

	/**
	 * Hop attenuation factor
	 */
	protected double delta = 0.05;

	public MobileLeung() {
		super();
	}

	public MobileLeung(Graph graph) {
		super(graph);
	}

	public MobileLeung(Graph graph, String marker) {
		super(graph, marker);
	}

	/**
	 * Create a new Leung algorithm instance, attached to the specified graph,
	 * using the specified marker to store the community attribute, and the
	 * specified weightMarker to retrieve the weight attribute of graph edges.
	 * 
	 * @param graph
	 *            graph to which the algorithm will be applied
	 * @param marker
	 *            community attribute marker
	 * @param weightMarker
	 *            edge weight marker
	 */
	public MobileLeung(Graph graph, String marker, String weightMarker) {
		super(graph, marker);
		this.weightMarker = weightMarker;
	}

	/**
	 * Create a new Leung algorithm instance, attached to the specified graph,
	 * using the default markers for the node community and edge weight
	 * attributes. Sets the preference exponent and hop attenuation factor to
	 * the given values.
	 * 
	 * @param graph
	 *            graph to which the algorithm will be applied
	 * @param m
	 *            comparable function preference exponent value
	 * @param delta
	 *            hop attenuation factor value
	 */
	public MobileLeung(Graph graph, double m, double delta) {
		super(graph);
		setParameters(m, delta);
	}

	/**
	 * Create a new Leung algorithm instance, attached to the specified graph,
	 * using the specified marker to store the community attribute, and the
	 * default marker to retrieve the weight attribute of graph edges. Sets the
	 * preference exponent and hop attenuation factor to the given values.
	 * 
	 * @param graph
	 *            graph to which the algorithm will be applied
	 * @param marker
	 *            community attribute marker
	 * @param m
	 *            comparable function preference exponent value
	 * @param delta
	 *            hop attenuation factor value
	 */
	public MobileLeung(Graph graph, String marker, double m, double delta) {
		super(graph, marker);
		setParameters(m, delta);
	}

	/**
	 * Create a new Leung algorithm instance, attached to the specified graph,
	 * using the specified marker to store the community attribute, and the
	 * specified weightMarker to retrieve the weight attribute of graph edges.
	 * Sets the preference exponent and hop attenuation factor to the given
	 * values.
	 * 
	 * @param graph
	 *            graph to which the algorithm will be applied
	 * @param marker
	 *            community attribute marker
	 * @param weightMarker
	 *            edge weight marker
	 * @param m
	 *            comparable function preference exponent value
	 * @param delta
	 *            hop attenuation factor value
	 */
	public MobileLeung(Graph graph, String marker, String weightMarker, double m,
			double delta) {
		super(graph, marker);
		this.weightMarker = weightMarker;
		setParameters(m, delta);
	}

	/**
	 * Create a new Leung algorithm instance, attached to the specified graph,
	 * using the specified marker to store the community attribute, and the
	 * specified mobilityMarkers to retrieve the mobility-based weight attributes of graph edges.
	 * Sets the preference exponent and hop attenuation factor to the given
	 * values.
	 * 
	 * @param graph
	 *            graph to which the algorithm will be applied
	 * @param marker
	 *            community attribute marker
	 * @param mobilitytMarkers
	 *            edges containing mobility parameters
	 * @param mobilitytMetric
	 *            mobility metric
	 * @param m
	 *            comparable function preference exponent value
	 * @param delta
	 *            hop attenuation factor value
	 */
	public MobileLeung(Graph graph, String marker, HashMap<MobileMarker, String> mobilityMarkers, String mobilitytMetric, double m,
			double delta) {
		super(graph, marker);
		this.mobilityMarkers = mobilityMarkers;
		this.mobilityMetric = mobilitytMetric;
		setParameters(m, delta);
	}
	
	/**
	 * Create a new Leung algorithm instance, attached to the specified graph,
	 * using the specified marker to store the community attribute, and the
	 * specified weightMarker to retrieve the weight attribute of graph edges.
	 * Additional mobilityMarkers specify the mobility-based weight attributes of graph edges.
	 * Sets the preference exponent and hop attenuation factor to the given
	 * values.
	 * 
	 * @param graph
	 *            graph to which the algorithm will be applied
	 * @param marker
	 *            community attribute marker
	 * @param weightMarker
	 *            edge weight marker
	 * @param mobilitytMarkers
	 *            edges containing mobility parameters
	 * @param mobilityMetric
	 *            mobility metric
	 * @param m
	 *            comparable function preference exponent value
	 * @param delta
	 *            hop attenuation factor value
	 */
	public MobileLeung(Graph graph, String marker, String weightMarker, HashMap<MobileMarker, String> mobilityMarkers, String mobilityMetric, double m,
			double delta) {
		super(graph, marker);
		this.weightMarker = weightMarker;
		this.mobilityMarkers = mobilityMarkers;
		this.mobilityMetric = mobilityMetric;
		setParameters(m, delta);
	}
	
	/**
	 * Sets the preference exponent and hop attenuation factor to the given
	 * values.
	 * 
	 * @param m
	 *            comparable function preference exponent value
	 * @param delta
	 *            hop attenuation factor value
	 */
	public void setParameters(double m, double delta) {
		this.m = m;
		this.delta = delta;
	}

	/**
	 * Sets the preference exponent and hop attenuation factor to the given
	 * values.
	 * 
	 * @param m
	 *            comparable function preference exponent value
	 * @param delta
	 *            hop attenuation factor value
	 * @param weightMarker
	 *            edge weight marker
	 * @param mobilitytMarkers
	 *            edges containing mobility parameters
	 */
	@Override
	public void setParameters(Dictionary<String, Object> params) {
		this.m = (Double) params.get("m");
		this.delta = (Double) params.get("delta");
		this.weightMarker = (String) params.get("weightMarker");
		this.mobilityMarkers = (HashMap<MobileMarker, String>) params.get("mobilityMarkers");
		this.mobilityMetric = (String) params.get("mobilityMetric");
		this.outLog = (BufferedWriter) params.get("outLog");
	}
	
	@Override
	public void computeNode(Node node) {
		/*
		 * Recall and update the node current community and previous score
		 */
		Object previousCommunity = node.getAttribute(marker);
		Double previousScore = (Double) node.getAttribute(marker + ".score");
		
		super.computeNode(node);

		/*
		 * Update the node label score
		 */

		// Handle first iteration // originate new community
		if (previousCommunity == null) {
			previousCommunity = node.getAttribute(marker);
			previousScore = (Double) node.getAttribute(marker + ".score");
		} 

		/*
		 * The node is the originator of the community and hasn't changed
		 * community at this iteration (or we are at the first simulation step):
		 * keep the maximum label score
		 */
		if ((node.getAttribute(marker).equals(previousCommunity))
				&& (previousScore.equals(1.0)))
			node.setAttribute(marker + ".score", 1.0);

		
		/*
		 * Otherwise search for the highest score amongst neighbors and reduce
		 * it by decreasing factor
		 */
		else {
			Double maxLabelScore = Double.NEGATIVE_INFINITY;
			for (Edge e : node.getEnteringEdgeSet()) {
				Node v = e.getOpposite(node);
				if (v.hasAttribute(marker)
						&& v.getAttribute(marker).equals(
								node.getAttribute(marker))) {
					if ((Double) v.getAttribute(marker + ".score") > maxLabelScore)
						maxLabelScore = (Double) v.getAttribute(marker + ".score");
				}
			}
			
			/*
			 *  If node disconnected from its neighbors, it has already originated community (in super.computeNode(node);
			 *  The case when the node continues traveling without neighbors was handled earlier
			 */
			if (!maxLabelScore.equals(Double.NEGATIVE_INFINITY)) { 
				node.setAttribute(marker + ".score", maxLabelScore - delta); // score indicates the distance from the originator, 
				// if the originator of the community is the neighbour of the node, then the maxLabelScore is 1, so the node gets 1-delta
			}
		}
	}

	/**
	 * Compute the scores for all relevant communities for the selected node
	 * using Leung algorithm.
	 * 
	 * @param u
	 *            The node for which the scores computation is performed
	 * @complexity O(DELTA) where DELTA is is the average node degree in the
	 *             network
	 */
	@Override
	protected void communityScores(Node u) {
		/*
		 * Reset the scores for each communities
		 */
		communityScores = new HashMap<Object, Double>();

		/*
		 * Iterate over the nodes that this node "hears"
		 */
		Double mobilitySimilarity = 1.0;
		
		String speedMarker = this.mobilityMarkers.get(MobileMarker.SPEED);
		String angleMarker = this.mobilityMarkers.get(MobileMarker.ANGLE);
		
		for (Edge e : u.getEnteringEdgeSet()) {
			Node v = e.getOpposite(u);

			/*
			 * Update the count for this community
			 */
			if (v.hasAttribute(marker)) {
				Double nodeScore = (Double) v.getAttribute(marker + ".score");
				
				Double topologySimilarity = Math.pow(v.getInDegree(), m);
				mobilitySimilarity = MobilityMeasure.computeRelativeMobility(u, v, speedMarker, angleMarker);
				if (mobilitySimilarity < 0.0) {
					mobilitySimilarity = 0.0;
				}
//				System.out.println("topologySimilarity " + topologySimilarity + ", mobilitySimilarity " + mobilitySimilarity);
				// Compute the neighbor node current score
				Double score = nodeScore * topologySimilarity * mobilitySimilarity;

				// Update the score of the nodes from according community
				if (communityScores.get(v.getAttribute(marker)) == null) {
					communityScores.put(v.getAttribute(marker), score);
				}
				else {
					Double newScore = communityScores.get(v.getAttribute(marker)) + score;
					communityScores.put(v.getAttribute(marker), newScore);
				}

//				try {
//					outLog.write("u: " + u.getId() + "\tv: " + v.getId() + 
//							"\tv.score: " + v.getAttribute(marker + ".score") + 
//							"\tv.degree: " + v.getInDegree() + "\ttopologySimilarity: " + topologySimilarity +
//							"\tmobilitySimilarity: " + mobilitySimilarity + 
//							"\tcommunityScores[" + v.getAttribute(marker) + "]: " + communityScores.get(v.getAttribute(marker)));
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}

			}
		}
	}
	
	@Override
	protected void originateCommunity(Node node) {
		super.originateCommunity(node);

		// Correct the original community score for the Leung algorithm
		node.setAttribute(marker + ".score", 1.0);
	}
}
