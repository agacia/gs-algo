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
	 * 
	 */
	package org.graphstream.algorithm.community;

	import java.util.Dictionary;
	import java.util.HashMap;

	import org.graphstream.algorithm.measure.MobilityMeasure;
	import org.graphstream.graph.Edge;
	import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * This class implements a mobile community detection algorithm for VANETs <i>et al</i>.
 * 
 * author Agata Grzybek
 * 
 */
public class StableCrowdz extends DynSharc_my {

	
		protected String mobilitySimilarityMarker = "mobilitySimilarity";
		
		/**
		 * 
		 */
		public StableCrowdz() {
			super();
		}

		/**
		 * @param graph
		 * @param marker
		 */
		public StableCrowdz(Graph graph, String marker) {
			super(graph, marker);
		}

		public StableCrowdz(Graph graph, String marker, String weightMarker) {
			super(graph, marker, weightMarker);
		}
		
		/**
		 * @param graph
		 * @param marker
		 * @param stallingThreshold
		 * @param breakPeriod
		 */
		public StableCrowdz(Graph graph, String marker, int stallingThreshold,
				int breakPeriod) {
			super(graph, marker, stallingThreshold, breakPeriod);
		}
		
		/**
		 * @param graph
		 */
		public StableCrowdz(Graph graph) {
			super(graph);
		}

		/**
		 * @param graph
		 * @param stallingThreshold
		 * @param breakPeriod
		 */
		public StableCrowdz(Graph graph, int stallingThreshold, int breakPeriod) {
			super(graph, stallingThreshold, breakPeriod);
		}
		
		@Override
		public void setParameters(Dictionary<String, Object> params) {
			super.setParameters(params);
			this.weightMarker = (String) params.get("weightMarker");
			this.mobilitySimilarityMarker = (String) params.get("mobilitySimilarityMarker");
		}

		@Override
		public Double similarity(Node a, Node b) {
			Double sim = 0.0;
			Double mob_sim = 0.0;
			Double weight = 0.0;
			Double weighted_sim = super.similarity(a, b);
			
			mob_sim = getMobSimLinkFrom(a, b);
			sim = weighted_sim * mob_sim;
//			System.out.println(graph.getStep() + " computing sim for " + a.getId() + ", weighted_sim " + weighted_sim + ", mob sim: " + mob_sim  + ", sim: " + sim);
			
			return sim;
		}
		

		protected Double getMobSimLinkFrom(Node a, Node b) {
			Double weight = 0.0;
			if (a.hasEdgeFrom(b.getId())
					&& a.<Edge>getEdgeFrom(b.getId()).hasAttribute(mobilitySimilarityMarker)) {
				weight = (Double) a.<Edge>getEdgeFrom(b.getId()).getAttribute(
						mobilitySimilarityMarker);
			}
			return weight;
		}
		
		protected void updateOriginator(Node u, Object previousCommunity) {
			/*
			 * Current node has originator token
			 */
			if (u.hasAttribute(marker + ".originator")
					&& !u.hasAttribute(marker + ".new_originator")) {
				/*
				 * Originator stayed in the same community: Make the originator
				 * token wander using a "local optimum favored" weighted random
				 * walk.
				 */
				if (previousCommunity != null
						&& previousCommunity.equals(u.getAttribute(marker))) {

					double score = u.getNumber(marker + ".score");
					double max = Double.NEGATIVE_INFINITY;
					HashMap<Node, Double> scores = new HashMap<Node, Double>();
					double total = 0;

					/*
					 * Search for the maximum neighboring score in the same
					 * community update total at the same time
					 */
					for (Edge e : u.getEnteringEdgeSet()) {
						Node v = e.getOpposite(u);
						if (v.hasAttribute(marker)
								&& v.<Object> getAttribute(marker).equals(
										u.<Object> getAttribute(marker))
								&& v.getId() != u.getAttribute(marker + ".originator_from")) {
							scores.put(v, v.getNumber(marker + ".score"));
							total += v.getNumber(marker + ".score");
							if (v.getNumber(marker + ".score") > max) // not consistent with thesis - in thesis not score (similarity*weight) but community membeship (sum scores)
								max = v.getNumber(marker + ".score");
						}
					}

					/*
					 * Current node is the local optimum: Originator token will pass
					 * only with a given probability. Otherwise token is passed
					 * using weighted random walk
					 */
					if (max > score || rng.nextDouble() < (max / score)) {

						double random = rng.nextDouble() * total;
						Node originator = null;
						for (Node v : scores.keySet()) {
							if (random <= scores.get(v) &&
								v.getId() != u.getAttribute(marker + ".originator_from")) {
									originator = v;
							} else {
								random -= scores.get(v);
							}
						}

						if (originator != null) {
							u.removeAttribute(marker + ".originator");
							u.removeAttribute(marker + ".originator_from");
							
							originator.setAttribute(marker + ".originator", true);
							originator.setAttribute(marker + ".new_originator",
									true);
							originator.setAttribute(marker + ".originator_from", u.getId());
						}
					}
				}

				/*
				 * Originator node changed community: Simply pass the originator
				 * token to the neighbor of previous community with the highest
				 * score
				 */
				else {
					u.removeAttribute(marker + ".originator");
					u.removeAttribute(marker + ".originator_from");

					double score = Double.NEGATIVE_INFINITY;
					Node originator = null;
					for (Edge e : u.getEnteringEdgeSet()) {
						Node v = e.getOpposite(u);
						if (v.hasAttribute(marker)
								&& v.<Object> getAttribute(marker).equals(
										previousCommunity)
								&& v.hasAttribute(marker + ".score")
								&& v.getNumber(marker + ".score") > score) {
							score = v.getNumber(marker + ".score");
							originator = v;
						}
					}

					/*
					 * A neighbor is found
					 */
					if (originator != null) {
						originator.setAttribute(marker + ".originator", true);
						originator.setAttribute(marker + ".new_originator", true);
					}
				}
			}

			/*
			 * The node has been processed, so it can't be a new originator
			 */
			if (u.hasAttribute(marker + ".new_originator"))
				u.removeAttribute(marker + ".new_originator");
		}

		protected void originateCommunity(Node node, Community c) {
			node.addAttribute(marker, c);
			node.setAttribute(marker + ".score", 0.0);
			node.setAttribute(marker + ".originator", true);
			node.setAttribute(marker + ".new_originator", true);
		}
		
		
	}
