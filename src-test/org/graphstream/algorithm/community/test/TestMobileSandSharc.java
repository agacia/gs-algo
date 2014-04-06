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
package org.graphstream.algorithm.community.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import org.graphstream.algorithm.community.CongestionMeasure;
import org.graphstream.algorithm.community.Crowdz;
import org.graphstream.algorithm.community.DecentralizedCommunityAlgorithm;
import org.graphstream.algorithm.community.MobileMarker;
import org.graphstream.algorithm.community.MobileSandSharc;
import org.graphstream.algorithm.measure.CommunityDistribution;
import org.graphstream.algorithm.measure.MobilityMeasure;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSourceFactory;
import org.junit.Test;

public class TestMobileSandSharc {
	Graph g;
	FileSourceDGS dgs;
	CongestionMeasure congestionMeasure;
	String laneMarker;
	String speedHistoryMarker;
	String speedHistoryTimestampsMarker;
	String speedHistoryIndexMarker;
	String maxHistoryRecordsMarker;
	String speedMarker;
	String timeMeanSpeedMarker;
	String weightMarker;
	String angleMarker;
	int speedHistoryLength;
	String graphfilename;
	String marker;
	private MobileSandSharc algorithm;
	Dictionary<String, Object> params;
	Dictionary<String, Object> congestionParams;
	CommunityDistribution comDist;
	private CongestionMeasure congestionAlgorithm;
	
	public TestMobileSandSharc() {
		dgs = new FileSourceDGS();
		g = new AdjacencyListGraph("test");
		dgs.addSink(g);
		laneMarker = "vehicleLane";
		timeMeanSpeedMarker = "timeMeanSpeed";
		speedMarker = "vehicleSpeed";
		weightMarker = "weight";
		angleMarker = "vehicleAngle";
		marker = "community";
		
		String speedType = "instant";
//		String speedType = "timemean";
//		String speedType = "spacetimemean";
		speedHistoryLength = 10;
		Double congestionSpeedThreshold = 9.0;
//		graphfilename = "data/Kirchberg-accident-900-50-50-100PR.dgs";
		graphfilename= "data/TestMobileSandSharc.dgs";
		params = new Hashtable<String, Object>();
		params.put("weightMarker", weightMarker);
		params.put("speedMarker", speedMarker);
		params.put("timeMeanSpeedMarker", timeMeanSpeedMarker);
		params.put("angleMarker", angleMarker);
		params.put("congestionSpeedThreshold", "");
		params.put("congestionSpeedThreshold", congestionSpeedThreshold);
		
		congestionParams = new Hashtable<String, Object>();
		congestionParams.put("weightMarker", "");
		congestionParams.put("speedMarker", speedMarker);
		congestionParams.put("laneMarker", laneMarker);
		congestionParams.put("dynamismMarker", "dynamism");
		congestionParams.put("speedHistoryLength", speedHistoryLength);
		congestionParams.put("speedType", speedType);
		
	}

	protected InputStream getGraphStream(String filename) throws IOException {
		InputStream inStream = getClass().getResourceAsStream(filename);
		return inStream;
		
	}

	protected void begin(String filename) throws IOException {
		algorithm = new MobileSandSharc();
		algorithm.init(g);
		algorithm.staticMode();
		algorithm.setMarker(marker);
		algorithm.setParameters(params);
		marker = algorithm.getMarker();
		comDist = new CommunityDistribution(marker);
		comDist.init(g);
		congestionAlgorithm = new CongestionMeasure();
		congestionAlgorithm.setParameters(congestionParams);
		congestionAlgorithm.init(g);
		dgs.begin(getGraphStream(filename));
	}

	protected void end() throws IOException {
		dgs.end();
		g.clear();
	}

//	@Test
//	public void testEmergence() throws IOException {
//		begin(graphfilename);
//		
//		dgs.nextStep();
//		int communityNumber = comDist.number();
//		int previousCommunityNumber = 0;
//		
//		while (dgs.nextStep()) {
//						
//			congestionAlgorithm.compute();
//			algorithm.compute();
//			comDist.compute();
//
//			previousCommunityNumber = communityNumber;			
//			communityNumber = comDist.number();
//			
//			double step = g.getStep();
//			System.out.println("Step " + step + ", previousCommunityNumber " + previousCommunityNumber + ", communityNumber " + communityNumber);
//			if (step==5.0) {
//				assertTrue("Step " + step + ", previousCommunityNumber " + previousCommunityNumber + ", communityNumber " + communityNumber, (communityNumber) == (previousCommunityNumber+1));
//			}
//		}
//		assertTrue(true);
//		end();
//	}
	
//	@Test
//	public void testInstantSpeed() throws IOException {
//		String speedType = "instant";
//		params.put("speedType", speedType);
//		congestionParams.put("speedType", speedType);
//		begin("data/TestMobileSandSharc-timemean.dgs");
//		dgs.nextStep();
//		int communityNumber = comDist.number();
//		int previousCommunityNumber = 0;
//		System.out.println("Step " + g.getStep() + ", nodes: " + g.getNodeCount() + ", previousCommunityNumber " + previousCommunityNumber + ", communityNumber " + communityNumber);
//		while (dgs.nextStep()) {
//			congestionAlgorithm.compute();
//			algorithm.compute();
//			comDist.compute();
//			previousCommunityNumber = communityNumber;			
//			communityNumber = comDist.number();
//			System.out.println("Step " + g.getStep() + ", nodes: " + g.getNodeCount() + ", previousCommunityNumber " + previousCommunityNumber + ", communityNumber " + communityNumber);
//		}
//		assertTrue(true);
//		end();
//	}
	
	@Test
	public void testTimeMeanSpeed() throws IOException {
		String speedType = "timemean";
		params.put("speedType", speedType);
		congestionParams.put("speedType", speedType);
		begin("data/TestMobileSandSharc-timemean.dgs");
		dgs.nextStep();
		int communityNumber = comDist.number();
		int previousCommunityNumber = 0;
		while (dgs.nextStep()) {
			congestionAlgorithm.compute();
			algorithm.compute();
			comDist.compute();
			previousCommunityNumber = communityNumber;			
			communityNumber = comDist.number();
			double step = g.getStep();
//			System.out.println("Step " + step + ", previousCommunityNumber " + previousCommunityNumber + ", communityNumber " + communityNumber);
			
			if (step==6.0) {
				
				assertTrue("Step " + step + ", previousCommunityNumber " + previousCommunityNumber + ", communityNumber " + communityNumber, (communityNumber) == (previousCommunityNumber+1));
			}
		}
		assertTrue(true);
		end();
	}
	
//	@Test
//	public void testSpaceTimeMeanSpeed() throws IOException {
//		String speedType = "spacetimemean";
//		params.put("speedType", speedType);
//		congestionParams.put("speedType", speedType);
//		begin("data/TestMobileSandSharc-timemean.dgs");
//		dgs.nextStep();
//		int communityNumber = comDist.number();
//		int previousCommunityNumber = 0;
//		while (dgs.nextStep()) {
//			congestionAlgorithm.compute();
//			algorithm.compute();
//			comDist.compute();
//			previousCommunityNumber = communityNumber;			
//			communityNumber = comDist.number();
//			double step = g.getStep();
//			System.out.println("Step " + step + ", previousCommunityNumber " + previousCommunityNumber + ", communityNumber " + communityNumber);
//			if (step==5.0) {
//				assertTrue("Step " + step + ", previousCommunityNumber " + previousCommunityNumber + ", communityNumber " + communityNumber, (communityNumber) == (previousCommunityNumber));
//			}
//		}
//		assertTrue(true);
//		end();
//	}
}
