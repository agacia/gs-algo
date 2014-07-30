/**
 * 
 */
package org.graphstream.algorithm.community;

import java.util.Dictionary;
import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * @author Guillaume-Jean Herbiet
 * 
 */
public class SandSharc_ag extends SandSharc_oryg {

	/**
	 * 
	 */
	public SandSharc_ag() {
		super();
	}

	/**
	 * @param graph
	 * @param marker
	 */
	public SandSharc_ag(Graph graph, String marker) {
		super(graph, marker);
	}

	public SandSharc_ag(Graph graph, String marker, String weightMarker) {
		super(graph, marker, weightMarker);
	}

	/**
	 * @param graph
	 * @param marker
	 * @param stallingThreshold
	 * @param breakPeriod
	 */
	public SandSharc_ag(Graph graph, String marker, int stallingThreshold,
			int breakPeriod) {
		super(graph, marker, stallingThreshold, breakPeriod);
	}

	/**
	 * @param graph
	 */
	public SandSharc_ag(Graph graph) {
		super(graph);
	}

	/**
	 * @param graph
	 * @param stallingThreshold
	 * @param breakPeriod
	 */
	public SandSharc_ag(Graph graph, int stallingThreshold, int breakPeriod) {
		super(graph, stallingThreshold, breakPeriod);
	}

	
	@Override
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		/*
		 * Current node has originator token
		 */
		Node u = graph.getNode(nodeId);
		if (u.hasAttribute(marker + ".originator") && !u.hasAttribute(marker + ".new_originator")) {
			/* Pass the originator
			 * token to the neighbor of its community with the highest score 
			 * ! ensure if another neighbor is not already an originator
			 */
			Node newOriginator = findNewOriginatorNode(u);
			System.out.println(graph.getStep() + "\tORIGINATOR_REMOVED\tnodeId" + nodeId + "\tcomId\t" + u.getAttribute(marker) + "\tdegree\t" + u.getDegree() + " " + u.getEnteringEdgeSet().size());
			/*
			 * A neighbor having the same community is found -> becomes a new originator
			 */
			if (newOriginator != null) {
				newOriginator.setAttribute(marker + ".originator", true);
				newOriginator.setAttribute(marker + ".new_originator", true);
				System.out.println(graph.getStep() + "\tORIGINATOR_PASSED\tnodeId" + u.getId() + "\tcomId\t" + u.getAttribute(marker) + "\tdegree\t" + u.getDegree() + "\tnewOriginator\t" + (newOriginator.getId()));
				
			}
		}

	}
	
	private Node findNewOriginatorNode(Node u) {
		Community community = (Community) u.getAttribute(marker);
		double score = Double.NEGATIVE_INFINITY;
		Node newOriginator = null;
		for (Edge e : u.getEnteringEdgeSet()) {
			Node v = e.getOpposite(u);
			if (v.hasAttribute(marker) && v.<Object> getAttribute(marker).equals(community)
					&& v.hasAttribute(marker + ".score") && v.getNumber(marker + ".score") > score) {
				score = v.getNumber(marker + ".score");
				newOriginator = v;
			}
		}
		if (newOriginator != null) {
//			System.out.println("ORIGINATOR_PASSED\tnodeId" + u.getId() + "\tcomId\t" + u.getAttribute(marker) + "\tdegree\t" + u.getDegree() + "\tnewOriginator\t" + (newOriginator.getId()));
		}
		return newOriginator;
	}
	
	@Override
	public void computeNode(Node u) {
		// perform SandSharc community detection
		super.computeNode(u);
		
		/*
		 * 
		 * If SandSharc ended up with no preferred community 
		 * then it means than the current node is not similar anymore to other nodes from its community
		 * The current node should:
		 * -  if is no an originator:
		 * 		- and can pass the originator role:
		 * 			pass the originator role and originate a new community
		 *		- if is a single-node community:
		 *			- do nothing 
		 *	- if is not an originator: 
		 * 		- originate a new community
		 */
		if (((Double) u.getAttribute(marker + ".score")) == 0.0) {
			Boolean isOriginator = u.hasAttribute(marker + ".originator") && (Boolean)u.getAttribute(marker + ".originator");			
			if (isOriginator) {
				// check if the node has neighbors with the same community
				Node newOriginator = findNewOriginatorNode(u);
				if (newOriginator != null) { // there is at least one neighbor belonging to the same community 
					// pass an originator role to one of it's neighbors
					newOriginator.setAttribute(marker + ".originator", true);
					newOriginator.setAttribute(marker + ".new_originator", true);
					// originate a new community
					Community oldCommunity = (Community)u.getAttribute(marker);
					originateCommunity(u);
//					System.out.println("NO_PREFFERRED_COM IS_ORIGINATOR PASS_ORIGINATOR_ROLE ORIGINATE\tnodeId\t" + u.getId() + "\toldComId\t" + oldCommunity + "\tdegree\t" + u.getDegree() + "\tnewComId\t" + u.getAttribute(marker) + "\tnewOriginator\t" +newOriginator);
				}					
				else { // is an originator with no preferred new community and no neighbors with the same community
					// do nothing, stay within the same community
//					System.out.println("NO_PREFFERRED_COM IS_ORIGINATOR DO_NOTHING\tnodeId\t" + u.getId() + "\tcomId\t" + u.getAttribute(marker) + "\tdegree\t" + u.getDegree() + "\tisOriginator\t" + isOriginator);
				}
			} else { // not an originator 
				// originate a new community
				Community oldCommunity = (Community)u.getAttribute(marker);
				originateCommunity(u);
//				System.out.println("NO_PREFFERRED_COM NOT_ORIGINATOR ORIGINATE \tnodeId\t" + u.getId() + "\toldComId\t" +oldCommunity + "\tdegree\t" + u.getDegree() + "\tnewComId\t" + u.getAttribute(marker) );
				
			}
		}

	}
	
	/**
	 * Allows to set generic parameters as a key,value 
	 * @param params
	 */
	@Override
	public void setParameters(Dictionary<String, Object> params) {
		super.setParameters(params);
		if (params.get("weightMarker") != null) {
			this.weightMarker = (String) params.get("weightMarker");
		}
		System.out.println("SandSharc_ag " + this.weightMarker);
	}

}
