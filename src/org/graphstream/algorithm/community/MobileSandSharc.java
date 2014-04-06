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
	protected int emergencePeriod;
	
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
		emergencePeriod = 2;
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

	public void setEmergenceMode(Node u) {
		// perform emergence 
//		if (u.getId().equals("0.64")) {
//			System.out.println("step " + graph.getStep() + "emergence? " + u.hasAttribute("emergence") + ", emergence.done " + u.hasAttribute("emergence.done"));
////			if (u.hasAttribute("emergence.to") && !u.hasAttribute("emergence.done")) {
////				System.out.println("originate " + (Community)u.getAttribute("emergence.to"));
////			}
//		}
//		System.out.println("step " + graph.getStep());
//		for (Node n : graph.getNodeSet()) {
//			System.out.print("\t node:"+n.getId()+","+n.getAttribute(marker));
//		}
//		System.out.println();
		if (u.hasAttribute("emergence.to") && !u.hasAttribute("emergence.done")) {
			originateCommunity(u, (Community)u.getAttribute("emergence.to"));
			u.addAttribute("emergence.done", true);
//			System.out.println("Node " + u.getId() + " emerges a new community at step " + graph.getStep() + " from " + u.getAttribute("emergence.from") + ", to: " + u.getAttribute("emergence.to") + " " + u.getAttribute(marker));
//			System.out.println("step " + graph.getStep());
//			for (Node n : graph.getNodeSet()) {
//				System.out.print("\t node:"+n.getId()+","+n.getAttribute(marker));
//			}
		}
//			
		if (!u.hasAttribute("emergence.done")) {
			// check if emergence of a new community is needed 
			// if the node has similarities with other nodes of his community equal zero, then the emergence is needed
			DecimalFormat df = new DecimalFormat("##.##");
	//		System.out.print("Step " + graph.getStep() + ", node " + u.getId() + ", com: " + u.getAttribute(marker) + ", checks neighbors: ");
			boolean needForEmergence = false;
			boolean isEmergenceNeighbor = false;
			Community emergenceNeighborCom = null;
			if (u.getEnteringEdgeSet().size() > 1) {
				for (Edge e : u.getEnteringEdgeSet()) {
					Node v = e.getOpposite(u);
					if (v.hasAttribute(marker)) {
						Double sim = similarity(u, v);
//						System.out.println("step " + graph.getStep() + " " + u.getId() + "," + u.getAttribute(marker) + " with " + v.getId() + "," + v.getAttribute(marker) + ",sim: " +  df.format(sim) + ", v.emergence? " + v.hasAttribute("emergence"));
						if (v.getAttribute(marker).equals(u.getAttribute(marker)) && sim.equals(new Double(0.0))) {
							needForEmergence = true;
	//						System.out.println("node recognizes emergence " + u.getId() + " with " + v.getId() + ",com:" + v.getAttribute(marker) + ",sim: " +  df.format(sim));
						}
						// if the node has a neighbor that is about to emerge from the same community 
						if (v.hasAttribute("emergence") && v.hasAttribute("emergence.from")) {
							Community neighbourPreviousCommunity = (Community)v.getAttribute("emergence.from");
							if (neighbourPreviousCommunity.equals((Community)u.getAttribute(marker))) {
								isEmergenceNeighbor = true;
								if (!sim.equals(new Double(0.0))) {
									emergenceNeighborCom = v.getAttribute("emergence.to");
	//								System.out.println("Node " + u.getId() + " found a node to join at step" + graph.getStep() + " from " + u.getAttribute(marker) + ", to: " + emergenceNeighborCom);
									
									if (needForEmergence) {
										break;
									}
								}
							}
						}
					}
				}
			}
			if (u.getId().equals("0.64")) {
				System.out.println("step " + graph.getStep() + ", needForEmergence? " + needForEmergence + ", speed " + u.getAttribute(timeMeanSpeedMarker));
//				if (u.hasAttribute("emergence.to") && !u.hasAttribute("emergence.done")) {
//					System.out.println("originate " + (Community)u.getAttribute("emergence.to"));
//				}
			}
			if (needForEmergence) {
				// check if other node is marked as emergence
				if (!isEmergenceNeighbor){
					// if there is no neighbor that is marked as emergence, originate a new community
					u.addAttribute("emergence.to", new Community());
//					System.out.println("Node " + u.getId() + " will create a new community ast step" + graph.getStep() + " from " + u.getAttribute(marker) + " from " + u.getAttribute("emergence.to") + ", isEmergenceNeighbor: " + isEmergenceNeighbor);
					
				} else if (emergenceNeighborCom != null) {
					u.addAttribute("emergence.to", emergenceNeighborCom);
				}
				// do nothing if there is a emergence node but in different class (sim==0)
				if (u.hasAttribute("emergence.to")) {
					Community previousCom = (Community)u.getAttribute(marker);
					u.addAttribute("emergence", emergencePeriod);
					u.addAttribute("emergence.from",  (Community)u.getAttribute(marker));
//					System.out.println("Node " + u.getId() + " will emerge a new community ast step" + graph.getStep() + " " + u.getAttribute(marker) + " from " + previousCom.getId() + ", to: " + u.getAttribute("emergence.to"));
				}
			}
			if (u.getId().equals("0.64")) {
				if (u.hasAttribute("emergence.to") && !u.hasAttribute("emergence.done")) {
					System.out.println("originate " + (Community)u.getAttribute("emergence.to") + " from neighbor? " + (emergenceNeighborCom!=null) + " link: " + (String)u.getAttribute("vehicleLane"));
				}
//				System.out.println();
			}
		}
		if (u.hasAttribute("emergence")) {
			int remaining = (Integer)u.getAttribute("emergence");
	
			// Decrease break mode lifetime
			if (remaining > 0) {
				u.setAttribute("emergence", remaining - 1);
			}
			// clear all emergence markers every time a vehicle changes it's mobility class
			else if (remaining == 0) {
				u.removeAttribute("emergence");
				u.removeAttribute("emergence.from");
				u.removeAttribute("emergence.to");
				u.removeAttribute("emergence.done");
//				System.out.println("Node " + u.getId() + " removes emergence markers at step" + graph.getStep());
			}
		}
	}

//	protected boolean restartEmergence(Node n) {
//		boolean changeMobilityClass = false;
//		if (n.hasAttribute("))
//		return changeMobilityClass;
//	}
	
	protected void originateCommunity(Node node, Community c) {
		node.addAttribute(marker, c);
		node.setAttribute(marker + ".score", 0.0);
		node.setAttribute(marker + ".originator", true);
		node.setAttribute(marker + ".new_originator", true);
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
