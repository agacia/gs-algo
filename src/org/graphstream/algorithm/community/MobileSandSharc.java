/**
 * 
 */
package org.graphstream.algorithm.community;

import java.io.BufferedWriter;
import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.HashMap;

import org.graphstream.algorithm.measure.MobilityMeasure;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * @author Guillaume-Jean Herbiet
 * 
 */
public class MobileSandSharc extends DynSharc {

	/**
	 * 
	 */
	public MobileSandSharc() {
		super();
	}

	/**
	 * @param graph
	 * @param marker
	 */
	public MobileSandSharc(Graph graph, String marker) {
		super(graph, marker);
	}

	public MobileSandSharc(Graph graph, String marker, String weightMarker) {
		super(graph, marker, weightMarker);
	}

	public MobileSandSharc(Graph graph, String marker, String weightMarker, Double congestionSpeedThreshold) {
		super(graph, marker, weightMarker);
		MobilityMeasure.setCongestionSpeedThreshold(congestionSpeedThreshold);
	}
	
	/**
	 * @param graph
	 * @param marker
	 * @param stallingThreshold
	 * @param breakPeriod
	 */
	public MobileSandSharc(Graph graph, String marker, int stallingThreshold,
			int breakPeriod) {
		super(graph, marker, stallingThreshold, breakPeriod);
	}
	
	/**
	 * @param graph
	 * @param marker
	 * @param stallingThreshold
	 * @param breakPeriod
	 */
	public MobileSandSharc(Graph graph, String marker, int stallingThreshold, int breakPeriod, double congestionSpeedThreshold) {
		super(graph, marker, stallingThreshold, breakPeriod);
		MobilityMeasure.setCongestionSpeedThreshold(congestionSpeedThreshold);
	}

	/**
	 * Markers used to calculate mobility similarity. 
	 */
	protected String speedMarker = "speed";
	protected String angleMarker = "angle";
	protected String dynamismMarker = "dynamism";
	protected String timeMeanSpeedMarker = "timeMeanSpeed";

	protected String speedType = "timemean"; // or 'instant' , 'spacetimemean'
	
	/**
	 * @param graph
	 */
	public MobileSandSharc(Graph graph) {
		super(graph);
	}

	/**
	 * @param graph
	 * @param stallingThreshold
	 * @param breakPeriod
	 */
	public MobileSandSharc(Graph graph, int stallingThreshold, int breakPeriod) {
		super(graph, stallingThreshold, breakPeriod);
	}
	
	@Override
	public void setParameters(Dictionary<String, Object> params) {
		this.weightMarker = (String) params.get("weightMarker");
		this.speedMarker = (String) params.get("speedMarker");
		this.timeMeanSpeedMarker = (String) params.get("timeMeanSpeedMarker");
		this.angleMarker = (String) params.get("angleMarker");
		MobilityMeasure.setCongestionSpeedThreshold( (Double) params.get("congestionSpeedThreshold"));
		if (params.get("speedType") != null) {
			this.speedType = (String) params.get("speedType");
		}
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

//	/**
//	 * Compute the scores for all relevant communities for the selected node
//	 * using the SHARC algorithm
//	 * 
//	 * @param u
//	 *            Node for which the computation is performed
//	 * @complexity O(DELTA^2) where DELTA is the average node degree in the
//	 *             network
//	 */
//	@Override
//	protected void communityScores(Node u) {
//		/*
//		 * Compute the "simple" count of received messages for each community.
//		 * This will be used as a fallback metric if the maximum "Sharc" score
//		 * is 0, meaning there is no preferred community.
//		 */
//		super.communityScores(u);
//		communityCounts = communityScores;
//		System.out.println("in MobileSandSharc community scores ");
//		/*
//		 * Reset the scores for each communities
//		 */
//		communityScores = new HashMap<Object, Double>();
//
//		/*
//		 * Iterate over the nodes that this node "hears"
//		 */
//		System.out.print("node " + u.getId() + ", neighbors: ");
//		for (Edge e : u.getEnteringEdgeSet()) {
//			Node v = e.getOpposite(u);
//			/*
//			 * Update the count for this community
//			 */
//			if (v.hasAttribute(marker)) {
//				System.out.print("\t" + v.getId() + " " + v.getAttribute(marker));
//				// Update score
//				Double sim = similarity(u, v);
//				System.out.println("calculate  scores " + ", sim: " + sim);
//				String vComId = v.getAttribute(marker);
//				if (communityScores.get(vComId) == null)
//					communityScores.put(vComId, sim);
//				else {
//					Double currentScore = communityScores.get(vComId);
//					// check if a node u has sim with some neighbors of the same community zero and non-zero -> then this community should be devided  
//					if (currentScore != 0 && sim == 0) {
//						
//					}
//					communityScores.put(vComId, currentScore + sim);
//				}
//			}
//		}
//		System.out.println();
//	}
	
	public void setEmergenceMode(Node u) {
		// perform emergence 
//		if (u.hasAttribute(marker + ".emergence")) {
//			
//		}
		// check if emergence of a new community is needed 
		// if the node has similarities with other nodes of his community equal zero, then he should originate new community 

		DecimalFormat df = new DecimalFormat("##.##");
//		System.out.print("Step " + graph.getStep() + ", node " + u.getId() + ", com: " + u.getAttribute(marker) + ", checks neighbors: ");
		for (Edge e : u.getEnteringEdgeSet()) {
			Node v = e.getOpposite(u);
			if (v.hasAttribute(marker) && v.getAttribute(marker)==u.getAttribute(marker)) {
				Double sim = similarity(u, v);
//				System.out.print("\t" + v.getId() + ",com:" + v.getAttribute(marker) + ",sim: " +  df.format(sim));
				if (sim.equals(new Double(0.0))) {
					Community previousCom = (Community)u.getAttribute(marker);
					originateCommunity(u);
					System.out.println("Node " + u.getId() + " emerges a new community " + u.getAttribute(marker) + " from " + previousCom.getId());
					break;
				}
			}
		}
//		System.out.println();
	}
	
	@Override
	public void computeNode(Node node) {
		super.computeNode(node);

		setEmergenceMode(node);
	}

	
	protected Double mobilitySimilarity(Node a, Node b) {
		Double speedA = 0.0;
		Double speedB = 0.0;
		// get instantaneouos speed
		if (this.speedType.equals("instant")) {
			speedA = MobilityMeasure.getAvgSpeed(a, speedMarker, null);
			speedB = MobilityMeasure.getAvgSpeed(b, speedMarker, null);
//			System.out.println("use instant speed "  + a.getId() + " " + speedMarker + " " + speedA);	
		}
		// get mean speed
		else if (speedType.equals("timemean") || speedType.equals("spacetimemean")) {
			if (a.hasAttribute(timeMeanSpeedMarker)) {
				speedA = (Double)a.getAttribute(timeMeanSpeedMarker);
//				System.out.println("use timeMeanSpeedMarker "  + a.getId() + " " +  timeMeanSpeedMarker + " "  + speedA);	
			}
			if (b.hasAttribute(timeMeanSpeedMarker)) {
				speedB = (Double)b.getAttribute(timeMeanSpeedMarker);	
			}
		}
		// get angle
		Double angleA = MobilityMeasure.getValue(a, angleMarker);
		Double angleB = MobilityMeasure.getValue(b, angleMarker);
		Double mobSim = MobilityMeasure.calculateDegreeOfCongestionDependence(speedA, speedB, angleA, angleB);
//		System.out.println("mob sim " + a.getId() + "-" + b.getId() + "=" + mobSim);	
		return mobSim;
	}
	
	protected void setStabilityWeight(Node a, Node b) {
		Double mobSim = mobilitySimilarity(a, b);
		setWeightInLinkFrom(a, b, mobSim);
	}
	
	protected void setWeightInLinkFrom(Node a, Node b, Double weight) {
		if (!a.hasEdgeFrom(b.getId())) {
			return;
		}
		a.<Edge>getEdgeFrom(b.getId()).setAttribute(weightMarker, weight);
	}
	
	/**
	 * Neighborhood weighted similarity between two nodes.
	 * + mobility similarity 
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
		setStabilityWeight(a, b);
		Double sim = super.similarity(a, b);
//		System.out.println("nei sim " + a.getId() + "-" + b.getId() + "=" + sim);	
		return sim;
	}

	
}
