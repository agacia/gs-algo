/**
 * 
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
	 * A weight for mobility similarity.
	 * Indicates how important is mobility similarity in calculation of the final similarity.
	 * The final similarity is computes as: mobilityWeight * mobilitySimilarity + (1-mobilityWeight) * neighborhoodSimilarity
	 */
	private Double mobilityWeight = 0.5;

	/**
	 * Markers used to calculate mobility similarity. 
	 */
	protected String speedMarker = "speed";
	protected String avgSpeedMarker = "vehicleAvgSpeed";
	protected String angleMarker = "angle";
	protected String dynamismMarker = "dynamism";
	protected String timeMeanSpeedMarker = "timeMeanSpeed";
	/**
	 * A threshold value for mobility similarity between 0 and 1.
	 * If mobility similarity is lower than threshold, the final similarity is set to 0 
	 */
	private Double mobilitySimilarityThreshold = 0.5; 
	
	/**
	 * Maximum speed
	 */
	private Double maxSpeed = 90.0; 
	
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
		this.mobilityWeight = (Double) params.get("mobilityWeight");
		this.weightMarker = (String) params.get("weightMarker");
		this.speedMarker = (String) params.get("speedMarker");
		this.avgSpeedMarker = (String) params.get("avgSpeedMarker");
		this.timeMeanSpeedMarker = (String) params.get("timeMeanSpeedMarker");
		this.angleMarker = (String) params.get("angleMarker");
		this.mobilitySimilarityThreshold = (Double) params.get("mobilitySimilarityThreshold");
		this.maxSpeed = (Double) params.get("maxSpeed");
		MobilityMeasure.setCongestionSpeedThreshold( (Double) params.get("congestionSpeedThreshold"));
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

	/**
	 * Compute the scores for all relevant communities for the selected node
	 * using the SHARC algorithm
	 * 
	 * @param u
	 *            Node for which the computation is performed
	 * @complexity O(DELTA^2) where DELTA is the average node degree in the
	 *             network
	 */
	@Override
	protected void communityScores(Node u) {
		/*
		 * Compute the "simple" count of received messages for each community.
		 * This will be used as a fallback metric if the maximum "Sharc" score
		 * is 0, meaning there is no preferred community.
		 */
		super.communityScores(u);
		communityCounts = communityScores;
		System.out.println("in MobileSandSharc community scores ");
		/*
		 * Reset the scores for each communities
		 */
		communityScores = new HashMap<Object, Double>();

		/*
		 * Iterate over the nodes that this node "hears"
		 */
		for (Edge e : u.getEnteringEdgeSet()) {
			Node v = e.getOpposite(u);
			/*
			 * Update the count for this community
			 */
			if (v.hasAttribute(marker)) {
				// Update score
				if (communityScores.get(v.getAttribute(marker)) == null)
					communityScores.put(v.getAttribute(marker),
							similarity(u, v));
				else
					communityScores.put(v.getAttribute(marker),
							communityScores.get(v.getAttribute(marker))
									+ similarity(u, v));
			}
		}
	}
	
	@Override
	public void computeNode(Node node) {
//		if (isValidForAssignment(node, this.avgSpeedMarker, 0.0)) {
		super.computeNode(node);
//		}
	}
	
	protected boolean isValidForAssignment(Node node, String marker, Double threshold) {
		if (!node.hasAttribute(marker)) {
			return true;
		}
		Double avgSpeed = (Double) node.getAttribute(marker);
		return !(avgSpeed < 0);
	}
	
	protected Double dynamicSimilarity(Node a, Node b) {
		double dynSim = 0;
		// TODO
		return dynSim;		
	}
	
	
	protected Double mobilitySimilarity(Node a, Node b) {
		
		// get average speed
//		Double speedA = MobilityMeasure.getAvgSpeed(a, speedMarker, avgSpeedMarker);
//		Double speedB = MobilityMeasure.getAvgSpeed(b, speedMarker, avgSpeedMarker);

		// get instantaneouos speed
		Double speedA = MobilityMeasure.getAvgSpeed(a, speedMarker, null);
		Double speedB = MobilityMeasure.getAvgSpeed(b, speedMarker, null);	
//		System.out.println("speed " + speedA);
//		 get time mean speed
//		if (a.hasAttribute(timeMeanSpeedMarker)) {
//			speedA = (Double)a.getAttribute(timeMeanSpeedMarker);
////			System.out.println("timeMeanSpeedMarker " + speedA);	
//		}
//		if (b.hasAttribute(timeMeanSpeedMarker)) {
//			speedB = (Double)b.getAttribute(timeMeanSpeedMarker);	
//		}
//		if (a.getId().equals("41427")) {
//			System.out.println("inst speed " + MobilityMeasure.getAvgSpeed(a, speedMarker, null) + ", meanSPeed " + speedA );
//		}
		// get angle
		Double angleA = MobilityMeasure.getValue(a, angleMarker);
		Double angleB = MobilityMeasure.getValue(b, angleMarker);
		
	//	Double dynamismA = MobilityMeasure.getValue(a, dynamismMarker);
	//	Double dynamismB = MobilityMeasure.getValue(b, dynamismMarker);
	//	Double speedRatio = MobilityMeasure.calculateSpeedRatio(speedA, speedB);
	//	Double cos = MobilityMeasure.calculateCos(angleA, angleB);
	//	Double dsd = MobilityMeasure.calculateDegreeOfSpatialDependence(speedA, speedB, angleA, angleB);
		
		Double mobSim = MobilityMeasure.calculateDegreeOfCongestionDependence(speedA, speedB, angleA, angleB);
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
//		System.out.println("weightmarker " + weightMarker + " weight of node " + a.getId() + " and " + b.getId() + " = " + weight);
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
//		sim = nSim;

			
//		if (mobSim == 0.0) { // should never happen 
//			System.err.println("mobsim is 0");
//		}
//		if (mobSim < mobilitySimilarityThreshold) {
////			System.out.println(a.getId() + " reseting sim " + mobSim + " (mobilitySimilarityThreshold " + mobilitySimilarityThreshold + ")");
//			return 0.0;
//		}
		//
//		sim = nSim * mobSim;
//		sim = nSim;
//		sim = 0.5*nSim + 0.5*mobSim; 
//		else if (mobSim < mobilitySimilarityThreshold) {
//			sim = 0.0;
//			System.out.println(a.getId() + " reseting mobSim " + mobSim);
//		}
//		else {
//			// dynamicly compute mobilityWeigh dependent on mobility dynamics
//			Double dynamicMobilityWeight = getDynamicMobilityWeight(a);
//			sim = (1-dynamicMobilityWeight) * nSim + dynamicMobilityWeight * mobSim;	
////			System.out.println(a.getId() + "dynamic mob weight " + dynamicMobilityWeight + " " + maxSpeed);
//	//		sim = nSim * mobSim;
//	//		sim = nSim;
//		}
//		System.out.println(a.getId() + " " + b.getId() + " " + "sim: " + sim + "nSim: " + nSim + " mSim: " + mobSim + ", mobilityWeight:" + mobilityWeight + ", weightMarker: " + weightMarker + ", speedMarker: " + speedMarker + ", angleMarker:"+angleMarker);
		return sim;
	}

	
	protected Double getDynamicMobilityWeight(Node a) {
		Double dynamicMobilityWeight = 0.0;
		String speedMarker = "vehicleAvgSpeed";
		if (a.hasAttribute(speedMarker)) {
			Double speed = (Double) a.getAttribute(speedMarker);
			dynamicMobilityWeight = speed / maxSpeed;
			if (dynamicMobilityWeight > 1) {
				dynamicMobilityWeight = 1.0;
			}
		}
		return dynamicMobilityWeight;
	}
}
