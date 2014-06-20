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
package org.graphstream.algorithm.measure.test;


import java.io.IOException;
import java.io.InputStream;

import org.graphstream.algorithm.community.CongestionMeasure;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.file.FileSourceDGS;

public class TestCongestionMeasure {
	Graph g;
	FileSourceDGS dgs;
	CongestionMeasure congestionMeasure;
	String laneMarker;
	String speedHistoryMarker;
	String speedHistoryTimestampsMarker;
	String speedHistoryIndexMarker;
	String maxHistoryRecordsMarker;
	String timeMeanSpeedMarker;
	
	int speedHistoryLength;
			
	public TestCongestionMeasure() {
		dgs = new FileSourceDGS();
		g = new AdjacencyListGraph("test");
		dgs.addSink(g);
		laneMarker = "vehicleLane";
		speedHistoryMarker = "speedHistory";
		speedHistoryTimestampsMarker = "speedHistoryTimestamps";
		speedHistoryIndexMarker = "speedHistoryIndex";
		maxHistoryRecordsMarker = "maxHistoryRecords";
		timeMeanSpeedMarker = "timeMeanSpeed";
		congestionMeasure = new CongestionMeasure();
		speedHistoryLength = 10;
		
	}

	protected InputStream getGraphStream(String filename) throws IOException {
		congestionMeasure.init(g);
		return getClass().getResourceAsStream(filename);
		
	}

	protected void begin(String filename) throws IOException {
		dgs.begin(getGraphStream(filename));
	}

	protected void end() throws IOException {
		dgs.end();
		g.clear();
	}

//	@Test
//	public void testCalculateSpaceMeanSpeed() throws IOException {
//		begin("data/TestCongestionMeasure.dgs");
//
//		/*
//		 * Step 1
//		 */
//		dgs.nextStep();
//		String nodeId = "1";
//		Node node = g.getNode(nodeId);
//		assertTrue(node.getId().equals(nodeId));
//		Double spaceMeanSpeed = congestionMeasure.calculateSpaceMeanSpeed(node);
//		assertTrue("Space mean speed should equal 10", spaceMeanSpeed.equals(10.0));
//
//		/*
//		 * Step 2
//		 */
//		dgs.nextStep();
//		node = g.getNode(nodeId);
//		spaceMeanSpeed = congestionMeasure.calculateSpaceMeanSpeed(node);
//		assertTrue(spaceMeanSpeed.equals(9.0));
//
//		/*
//		 * Step 3
//		 */
//		dgs.nextStep();
//		node = g.getNode(nodeId);
//		spaceMeanSpeed = congestionMeasure.calculateSpaceMeanSpeed(node);
//		assertTrue(spaceMeanSpeed.equals(9.5));
//		/*
//		 * Step 4
//		 */
//		dgs.nextStep();
//		node = g.getNode(nodeId);
//		spaceMeanSpeed = congestionMeasure.calculateSpaceMeanSpeed(node);
//		assertTrue(spaceMeanSpeed.equals(1.0));
//		
//		/*
//		 * Step 5
//		 */
//		dgs.nextStep();
//		node = g.getNode(nodeId);
//		spaceMeanSpeed = congestionMeasure.calculateSpaceMeanSpeed(node);
//		assertTrue(spaceMeanSpeed.equals(10.0));
//		
//		end();
//	}
//
//	@Test
//	public void testAddSpeedToSpeedHistory() throws IOException {
//		begin("data/TestCongestionMeasure.dgs");
//
//		/*
//		 * Step 1, graph should be 4-edge-connected.
//		 */
//		dgs.nextStep();
//		String nodeId = "1";
//		Node node = g.getNode(nodeId);
//		String previousLane = (String) node.getAttribute(this.laneMarker).toString();
//		String currentLane = previousLane;
//		node.setAttribute(this.laneMarker + ".previous", currentLane);
//		
//		Double spaceMeanSpeed = 10.0;
//		congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//
//		Object[] history = (Object[])node.getAttribute(speedHistoryMarker);
//		Object[] timestamps = (Object[])node.getAttribute(speedHistoryTimestampsMarker);
//		Integer speedHistoryIndex = (Integer)node.getAttribute(speedHistoryIndexMarker);	
//		Integer maxHistoryRecords = (Integer)node.getAttribute("maxHistoryRecords");	
//		assertTrue(speedHistoryIndex.equals(1));
//		assertTrue(maxHistoryRecords.equals(0));
//		assertTrue(history[speedHistoryIndex-1].equals(10.0));
//		
//		/*
//		 * Step 2, graph should be 1-edge-connected.
//		 */
//		dgs.nextStep();
//		node = g.getNode(nodeId);
//		spaceMeanSpeed = 10.0;
//		congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//		history = (Object[])node.getAttribute(speedHistoryMarker);
//		timestamps = (Object[])node.getAttribute(speedHistoryTimestampsMarker);
//		speedHistoryIndex = (Integer)node.getAttribute(speedHistoryIndexMarker);	
//		maxHistoryRecords = (Integer)node.getAttribute("maxHistoryRecords");	
//		assertTrue(speedHistoryIndex.equals(2));
//		assertTrue(maxHistoryRecords.equals(-1));
//		assertTrue(history[speedHistoryIndex-1].equals(10.0));
//		assertTrue(history[speedHistoryIndex-2].equals(10.0));
//		assertTrue(history[speedHistoryIndex].equals(0.0));
//		assertTrue(timestamps[speedHistoryIndex-1].equals(2.0));
//		assertTrue(timestamps[speedHistoryIndex-2].equals(1.0));
//		assertTrue(timestamps[speedHistoryIndex].equals(0.0));
//		
//		end();
//	}
//	
//	@Test
//	public void testAddSpeedToSpeedHistoryCopyFromNeighbor() throws IOException {
//		begin("data/TestCongestionMeasure.dgs");
//		System.out.println("testAddSpeedToSpeedHistoryCopyFromNeighbor " + g.getStep());
//		/*
//		 * Step 1.
//		 */
//		dgs.nextStep();
//		
//		String nodeId = "1";
//		Node node = g.getNode(nodeId);
//		String previousLane = (String) node.getAttribute(this.laneMarker).toString();
//		String currentLane = previousLane;
//		node.setAttribute(this.laneMarker + ".previous", currentLane);
//		Double spaceMeanSpeed = 1.0;
//		congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//		
//		nodeId = "3";
//		node = g.getNode(nodeId);
//		node.setAttribute(this.laneMarker + ".previous", currentLane);
//		spaceMeanSpeed = 1.0;
//		congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//
//		/*
//		 * Step 2.
//		 */
//		dgs.nextStep();
//		node = g.getNode(nodeId);
//		spaceMeanSpeed = 2.0;
//		congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//		
//		/*
//		 * Step 2.
//		 */
//		dgs.nextStep();
//		node = g.getNode("2");
//		node.setAttribute(this.laneMarker + ".previous", currentLane);
//		spaceMeanSpeed = 10.0;
//		congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//		Object[] history = (Object[])node.getAttribute(speedHistoryMarker);
//		Object[] timestamps = (Object[])node.getAttribute(speedHistoryTimestampsMarker);
//		Integer speedHistoryIndex = (Integer)node.getAttribute(speedHistoryIndexMarker);	
//		Integer maxHistoryRecords = (Integer)node.getAttribute("maxHistoryRecords");	
//		assertTrue("history index should be 3 not " + speedHistoryIndex, speedHistoryIndex.equals(3));
//		assertTrue("maxHistoryRecords got " + maxHistoryRecords + " expected: " + 2, maxHistoryRecords.equals(2));
//		assertTrue(history[0].equals(1.0));
//		assertTrue(timestamps[0].equals(1.0));
//		assertTrue(history[1].equals(2.0));
//		assertTrue(timestamps[1].equals(2.0));
//		assertTrue(history[2].equals(10.0));
//		assertTrue(timestamps[2].equals(3.0));
//		
//		end();
//	}
//	
//	@Test
//	public void testAddSpeedToSpeedHistoryCopyFromNeighborNotAll() throws IOException {
//		begin("data/TestCongestionMeasure_long.dgs");
//		System.out.println("testAddSpeedToSpeedHistoryCopyFromNeighborNotAll " + g.getStep() + ", items: " + 3);
//		
//		String nodeId = "3";
//		for (Integer i = 1; i <= 3; ++i) {
//			dgs.nextStep();
//			Node node = g.getNode(nodeId);
//			String previousLane = (String) node.getAttribute(this.laneMarker).toString();
//			String currentLane = previousLane;
//			node.setAttribute(this.laneMarker + ".previous", currentLane);
//			Double spaceMeanSpeed = i.doubleValue();
//			congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//		}
//		
//
//		dgs.nextStep();
//		Node node = g.getNode("2");
//		String previousLane = (String) node.getAttribute(this.laneMarker).toString();
//		String currentLane = previousLane;
//		node.setAttribute(this.laneMarker + ".previous", currentLane);
//		Double spaceMeanSpeed = 10.0;
//		congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//	
//		Object[] history = (Object[])node.getAttribute(speedHistoryMarker);
//		Object[] timestamps = (Object[])node.getAttribute(speedHistoryTimestampsMarker);
//		Integer speedHistoryIndex = (Integer)node.getAttribute(speedHistoryIndexMarker)%speedHistoryLength;	
//		Integer maxHistoryRecords = (Integer)node.getAttribute("maxHistoryRecords");	
//		assertTrue("maxHistoryRecords should be " + maxHistoryRecords,  maxHistoryRecords.equals(3));
//		assertTrue("history index should be " + speedHistoryIndex, speedHistoryIndex.equals(4));
//		Integer i = 0;
//		for (i = 0; i < 3; ++i) {
//			assertTrue("got" + history[i] + " expected: " + i.doubleValue()+1, history[i].equals(i.doubleValue()+1));
//			assertTrue("got" + timestamps[i] + " expected: " + i.doubleValue()+1, timestamps[i].equals(i.doubleValue()+1));
//		}
//		assertTrue(history[i].equals(10.0));
//		assertTrue(timestamps[i].equals(4.0));
//
//		end();
//	}
//	
//	@Test
//	public void testAddSpeedToSpeedHistoryCopyFromNeighborAll() throws IOException {
//		begin("data/TestCongestionMeasure_long.dgs");
//		System.out.println("testAddSpeedToSpeedHistoryCopyFromNeighborAll " + g.getStep() + ", speedHistoryLength: " + speedHistoryLength);
//		
//		String nodeId = "3";
//		for (Integer i = 1; i <= speedHistoryLength+3; ++i) {
//			dgs.nextStep();
//			Node node = g.getNode(nodeId);
//			String previousLane = (String) node.getAttribute(this.laneMarker).toString();
//			String currentLane = previousLane;
//			node.setAttribute(this.laneMarker + ".previous", currentLane);
//			Double spaceMeanSpeed = i.doubleValue();
//			congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//		}
//		
//		dgs.nextStep();
//		Node node = g.getNode("2");
//		String previousLane = (String) node.getAttribute(this.laneMarker).toString();
//		String currentLane = previousLane;
//		node.setAttribute(this.laneMarker + ".previous", currentLane);
//		Double spaceMeanSpeed = 10.0;
//		congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//	
//		Object[] history = (Object[])node.getAttribute(speedHistoryMarker);
//		Object[] timestamps = (Object[])node.getAttribute(speedHistoryTimestampsMarker);
//		Integer speedHistoryIndex = (Integer)node.getAttribute(speedHistoryIndexMarker)%speedHistoryLength;	
//		Integer maxHistoryRecords = (Integer)node.getAttribute("maxHistoryRecords");	
//		assertTrue("maxHistoryRecords should be " + maxHistoryRecords,  maxHistoryRecords.equals(speedHistoryLength-1));
//		assertTrue("history index should be " + speedHistoryLength + " not " + speedHistoryIndex, (speedHistoryIndex).equals(0));
//		Integer i = 0;
//		for (i = 0; i < speedHistoryLength; ++i) {
//			System.out.println("i " + i + " history: " + history[i] + ", timestamps: " + timestamps[i]);
//		}
//		for (i = 0; i < 9; ++i) {
//			assertTrue("history got" + history[i] + " expected: " + (i.doubleValue()+5), history[i].equals(i.doubleValue()+5));
//			assertTrue("timestamps got" + timestamps[i] + " expected: " + (i.doubleValue()+5), timestamps[i].equals(i.doubleValue()+5));
//		}
//		assertTrue("h " + history[speedHistoryLength-1] + " expected: " + 10, history[speedHistoryLength-1].equals(10.0));
//		assertTrue("timestamps got" + timestamps[i] + " expected: " + g.getStep(), timestamps[i].equals(14.0));
//
//		end();
//	}
//	
//	@Test
//	public void testCalculateTimeMeanSpeed() throws IOException {
//		begin("data/TestCongestionMeasure_long.dgs");
//		System.out.println("testCalculateTimeMeanSpeed " + g.getStep() + ", speedHistoryLength: " + speedHistoryLength);
//		
//		String nodeId = "3";
//		for (Integer i = 1; i <= speedHistoryLength; ++i) {
//			dgs.nextStep();
//			Node node = g.getNode(nodeId);
//			String previousLane = (String) node.getAttribute(this.laneMarker).toString();
//			String currentLane = previousLane;
//			node.setAttribute(this.laneMarker + ".previous", currentLane);
//			Double spaceMeanSpeed = 10.0;
//			congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//		}
//		Node node = g.getNode(nodeId);
//		/*
//		 * Step 1, graph should be 4-edge-connected.
//		 */
//		dgs.nextStep();
//		
//		Object[] history = (Object[])node.getAttribute(speedHistoryMarker);
//		Object[] timestamps = (Object[])node.getAttribute(speedHistoryTimestampsMarker);
//		Integer i = 0;
//		for (i = 0; i < 10; ++i) {
//			System.out.println("i " + i + " history: " + history[i] + ", timestamps: " + timestamps[i]);
//		}
//
//		congestionMeasure.calculateAndSetTimeMeanSpeed(node);
//		Double timeMeanSpeed = (Double)node.getAttribute(timeMeanSpeedMarker);
//		Integer sumNonZeroCount = (Integer)node.getAttribute(timeMeanSpeedMarker+".count");
//		assertTrue(timeMeanSpeed.equals(10.0));
//		assertTrue(sumNonZeroCount.equals(speedHistoryLength));
//	} 
//	
//	@Test
//	public void testCalculateTimeMeanSpeed2() throws IOException {
//		begin("data/TestCongestionMeasure_long.dgs");
//		System.out.println("testCalculateTimeMeanSpeed2 " + g.getStep() + ", speedHistoryLength: " + speedHistoryLength);
//		
//		String nodeId = "3";
//		for (Integer i = 1; i <= 5; ++i) {
//			dgs.nextStep();
//			Node node = g.getNode(nodeId);
//			String previousLane = (String) node.getAttribute(this.laneMarker).toString();
//			String currentLane = previousLane;
//			node.setAttribute(this.laneMarker + ".previous", currentLane);
//			Double spaceMeanSpeed = 10.0;
//			congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//		}
//		Node node = g.getNode(nodeId);
//		/*
//		 * Step 1, graph should be 4-edge-connected.
//		 */
//		dgs.nextStep();
//		
//		Object[] history = (Object[])node.getAttribute(speedHistoryMarker);
//		Object[] timestamps = (Object[])node.getAttribute(speedHistoryTimestampsMarker);
//		Integer i = 0;
//		for (i = 0; i < 10; ++i) {
//			System.out.println("i " + i + " history: " + history[i] + ", timestamps: " + timestamps[i]);
//		}
//
//		congestionMeasure.calculateAndSetTimeMeanSpeed(node);
//		Double timeMeanSpeed = (Double)node.getAttribute(timeMeanSpeedMarker);
//		Integer sumNonZeroCount = (Integer)node.getAttribute(timeMeanSpeedMarker+".count");
//		System.out.println("timeMeanSpeed " + timeMeanSpeed + ", sumNonZeroCount: " + sumNonZeroCount);
//		assertTrue(timeMeanSpeed.equals(10.0));
//		assertTrue(sumNonZeroCount.equals(5));
//	}
//	
//	@Test
//	public void testCalculateTimeMeanSpeedOutdated() throws IOException {
//		begin("data/TestCongestionMeasure_long.dgs");
//		System.out.println("testCalculateTimeMeanSpeedOutdated " + g.getStep() + ", speedHistoryLength: " + speedHistoryLength);
//		
//		String nodeId = "3";
//		for (Integer i = 1; i <= speedHistoryLength; ++i) {
//			dgs.nextStep();
//			Node node = g.getNode(nodeId);
//			String previousLane = (String) node.getAttribute(this.laneMarker).toString();
//			String currentLane = previousLane;
//			node.setAttribute(this.laneMarker + ".previous", currentLane);
//			Double spaceMeanSpeed = 10.0;
//			congestionMeasure.addSpeedToSpeedHistory(node, spaceMeanSpeed);
//		}
//		for (Integer i = 1; i <= 5; ++i) {
//			dgs.nextStep();
//		}
//		Node node = g.getNode(nodeId);
//		Object[] history = (Object[])node.getAttribute(speedHistoryMarker);
//		Object[] timestamps = (Object[])node.getAttribute(speedHistoryTimestampsMarker);
//		Integer i = 0;
//		for (i = 0; i < 10; ++i) {
//			System.out.println("i " + i + " history: " + history[i] + ", timestamps: " + timestamps[i]);
//		}
//
//		congestionMeasure.calculateAndSetTimeMeanSpeed(node);
//		Double timeMeanSpeed = (Double)node.getAttribute(timeMeanSpeedMarker);
//		Integer sumNonZeroCount = (Integer)node.getAttribute(timeMeanSpeedMarker+".count");
//		System.out.println(g.getStep() +  ", timeMeanSpeed " + timeMeanSpeed + ", sumNonZeroCount: " + sumNonZeroCount);
//		assertTrue(timeMeanSpeed.equals(10.0));
//		assertTrue("sumNonZeroCount " + sumNonZeroCount, sumNonZeroCount.equals(speedHistoryLength-4));
//	}
}
