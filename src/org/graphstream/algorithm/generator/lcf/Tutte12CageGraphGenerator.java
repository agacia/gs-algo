/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
package org.graphstream.algorithm.generator.lcf;

import org.graphstream.algorithm.generator.LCFGenerator;

/**
 * Build a Tutte 12-cage graph.
 * 
 * <dl>
 * <dt>Nodes</dt>
 * <dd>126</dd>
 * <dt>LCF</dt>
 * <dd>[17, 27, -13, -59, -35, 35, -11, 13, -53, 53, -27, 21, 57, 11, -21, -57,
 * 59, -17]^7</dd>
 * </dl>
 * 
 * @reference Brouwer, A. E.; Cohen, A. M.; and Neumaier, A. Distance Regular
 *            Graphs. New York: Springer-Verlag, 1989.
 * 
 */
public class Tutte12CageGraphGenerator extends LCFGenerator {
	/**
	 * LCF notation of a Tutte 12-cage graph.
	 */
	public static final LCF TUTTE_12CAGE_GRAPH_LCF = new LCF(7, 17, 27, -13,
			-59, -35, 35, -11, 13, -53, 53, -27, 21, 57, 11, -21, -57, 59, -17);

	public Tutte12CageGraphGenerator() {
		super(TUTTE_12CAGE_GRAPH_LCF, 126, false);
	}
}
