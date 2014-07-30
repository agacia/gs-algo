package org.graphstream.algorithm.community.test;

import static org.junit.Assert.*;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import org.graphstream.algorithm.community.CongestionMeasure;
import org.graphstream.algorithm.community.MobileMarker;
import org.graphstream.algorithm.community.MobileSandSharc;
import org.graphstream.algorithm.community.SandSharc_ag;
import org.graphstream.algorithm.community.Sharc_oryg;
import org.graphstream.algorithm.measure.CommunityDistribution;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSourceDGS;
import org.junit.Before;
import org.junit.Test;

public class TestSandSharc_ag {

	Graph g;
	
	public void readDynamicGraph() {
		
		
	}
	
	public String[] getArgs() {
		String[] args = new String[]{ "--inputFile", "/Users/agata/workspace/Jean/Agata/vanet_probeData_v15-30+300_17032014.dgs", // "/Users/agata/Documents/PhD/sumo_scenarios/twolanes_long_120kmph_600/vanet.dgs",
				"--communityAlgorithmName", "SandSharc_ag", //"NewSawSharc_oryg", // "SandSharc_hybrid", // SandSharc_mobility SandSharc_link_duration   SandSharc_oryg StableCrowdz "MobileSandSharc_oryg", // "Crowdz", //"SandSharc_oryg",
				"--congestionAlgorithmName", "CongestionMeasure",
				"--goal", "communities",
				"--startStep", "0",
				"--endStep", "100",
				"--outputDir", "/Users/agata/workspace/crowds/output/eclipse/",
				"--speedHistoryLength", "90",
				"--speedType", "timemean",
				"--numberOfIterations", "1"};
		return args;
	}
	
	public HashMap<String, String> parseArgs(String[] args) {
		HashMap<String, String> programArgs = new HashMap<String, String>();

		if (args.length > 1) {
			for (int i = 0; i < args.length; ++i) {
				String argName = args[i];
				String argValue = args[++i];				
				if (argName.equals("--communityAlgorithmName")) {
					programArgs.put("communityAlgorithmName", argValue.trim());
				}
				if (argName.equals("--congestionAlgorithmName")) {
					programArgs.put("congestionAlgorithmName", argValue.trim());
				}
				if (argName.equals("--goal")) {
					programArgs.put("goal", argValue.trim());
				}
				if (argName.equals("--inputFile")) {
					programArgs.put("filePath", argValue.trim());
				}
				if (argName.equals("--startStep")) {
					programArgs.put("startStep", argValue.trim());
				}
				if (argName.equals("--endStep")) {
					programArgs.put("endStep", argValue.trim());
				}
				if (argName.equals("--outputDir")) {
					programArgs.put("outputDir", argValue.trim());
				}
				if (argName.equals("--numberOfIterations")) {
					programArgs.put("numberOfIterations", argValue.trim());
				}
				if (argName.equals("--speedHistoryLength")) {
					programArgs.put("speedHistoryLength", argValue.trim());
				}
				if (argName.equals("--speedType")) {
					programArgs.put("speedType", argValue.trim());;
				}
			}
		}
		return programArgs;
	}
	
	public static Hashtable<String, Object> getCongestionParams(HashMap<String, String> programArgs, HashMap<MobileMarker, String> markers) {
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		String algorithmName = programArgs.get("congestionAlgorithmName");
		if (algorithmName.equals("CongestionCrowdz") || algorithmName.equals("CongestionMeasure")) {	
			params.put("weightMarker", "weight");
			params.put("speedMarker", markers.get(MobileMarker.SPEED));
			params.put("angleMarker", markers.get(MobileMarker.ANGLE));
			params.put("mobilitySimilarityMarker", markers.get(MobileMarker.MOBILITY_SIMILARITY));
			params.put("laneMarker", markers.get(MobileMarker.LANE));
			params.put("dynamismMarker", markers.get(MobileMarker.DYNAMISM));
			params.put("linkDurationMarker", markers.get(MobileMarker.LINK_DURATION));
			params.put("hybridMarker", "hybrid");
			params.put("speedHistoryLength", Integer.parseInt(programArgs.get("speedHistoryLength")));
			params.put("speedType", programArgs.get("speedType"));
		}
		return params;
	}
	
	public Graph createSimpleGraph() {
		Graph graph = new SingleGraph("Tutorial 1");
		graph.addNode("A");
		graph.addNode("B");
		return graph;
	}
	
	@Before
	public void setUp() throws Exception {
		String[] args = getArgs();
		HashMap<String, String> programArgs = parseArgs(args);
	}

	

	@Test
	public void test_complete_graph_four_nodes() {
//		graph = createCompleteGraph();
//		SandSharc_ag sharc = new SandSharc_ag(graph);
//		sharc.setParameters(params);
		
		assertTrue(true);
	}
	
}
