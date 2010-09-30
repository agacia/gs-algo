/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */
package org.graphstream.algorithm.measure;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.ElementSink;

/**
 * <p>
 * The <i>element nervousness</i> in the context of dynamic graphs defines how
 * often elements in that graph appear and disappear during the evolution of the
 * graph.
 * </p>
 * <p>
 * This metric may be applied to any of the nodes or edges and an average value
 * for the all graph or a set of elements on the graph like a path or a clique
 * for instance can be made.
 * </p>
 * <p>
 * Since a dynamic graph here has as discrete evolution (step by step) it is
 * possible to define the age of a graph element in considering the number of
 * steps where it exists. We may define the <i>age</i> of an existing element as
 * the number of steps between its last appearing (add event) and the current
 * step. The <i>accumulated age</i> of an element may be defined as the number
 * of steps where the considered element is present since the begin of the
 * evolution of the graph.
 * </p>
 * <p>
 * For a given element in a dynamic graph (identified with its id), the
 * <i>element nervousness</i> is the number of times this element appears
 * (number of <i>add</i> events) divided by its <i>accumulated age</i>.
 * </p>
 * <p>
 * the <i>average element nervousness</i> may also be defined for a set of
 * elements in the graph or for the all graph. It is the average nervousness of
 * the elements that compose the set or the whole graph.
 * </p>
 */
public class ElementNervousness
	implements Algorithm, ElementSink
{
	/**
	 * The graph we listen at.
	 */
	protected Graph graph;
	
	/**
	 * Decides if nodes are listened to for the construction of the nervousness.
	 */
	protected boolean watchNodes = true;

	/**
	 * Decides if edges are listened to for the computation of the nervousness
	 */
	protected boolean watchEdges = true;

	/**
	 * The optional structure to listen to
	 */
	protected HashSet<String> structure = null;

	/**
	 * Gather ids whose appearance's count has change during the step.
	 */
	private HashSet<String> pending = null;

	/**
	 * Map of listened elements and their accumulated ages
	 */
	private HashMap<String, Integer> accumulatedAges;

	/**
	 * Map of listened elements and the number of time they appeared.
	 */
	private HashMap<String, Integer> appearance;

	
	/**
	 * Default constructor. Listens to all the elements in the graph. Any
	 * nervousness query can be made with any element, or group of elements with
	 * the <code>getElementNervousness(...)</code> methods.
	 */
	public ElementNervousness()
	{
	}

	/**
	 * Constructor that only listens to one kind of element, Nodes or Edges.
	 * 
	 * @param className
	 *            The classname of the kind of element to be listened to
	 */
	public ElementNervousness( Class<?> className )
	{
		watchEdges = watchNodes = false;
		
		if (className.equals(Edge.class))
		{
			watchEdges = true;
		}
		else if (className.equals(Node.class))
		{
			watchNodes = true;
		}
		else
		{
			for (Class<?> c : className.getClasses())
			{
				if (c.equals(Edge.class))
				{
					watchEdges = true;
				}
				else if (c.equals(Node.class))
				{
					watchNodes = true;
				}
			}
		}
		
		if (!watchEdges && !watchNodes)
		{
			throw new ClassCastException(
					"the given class in the constructor of Nervousness does not inherite from Edge nor from Node"
							+ watchEdges + " " + watchNodes);
		}

	}

	/**
	 * Constructor with an array of identifiers (strings) to listen to; These ids may refer to nodes
	 * or edges.
	 * 
	 * @param ids
	 *            Array of elements identifiers to listen to for the computation of the nervousness.
	 */
	public ElementNervousness( Collection<? extends String> ids )
	{
		watchEdges = false;
		watchNodes = false;
		structure = new HashSet<String>();
		structure.addAll(ids);
	}

	private void reinit()
	{
		accumulatedAges = new HashMap<String, Integer>();
		appearance = new HashMap<String, Integer>();
		pending = new HashSet<String>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.algorithm.Algorithm#init(org.graphstream.graph.Graph)
	 */
	public void init( Graph graph )
	{
		if( this.graph != null )
			this.graph.removeElementSink(this);
		
		reinit();
		
		this.graph = graph;
		this.graph.addElementSink(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.graphstream.algorithm.Algorithm#compute()
	 */
	public void compute()
	{

	}

	/**
	 * With no argument this method returns the <b>average element
	 * nervousness</b> of the listened structure. This structure depends on what
	 * was initialized in the constructor. It can be a given structure, the set
	 * of all the edges or nodes, or the whole graph.
	 * 
	 * @return the <b>average element nervousness</b> of the listened structure.
	 *         If no structure and no element type was defined, then returns the
	 *         <b>average element nervousness</b> of the elements on whole
	 *         graph.
	 */
	public double getElementNervousness()
	{
		double N = 0.0;
		
		for (String id : accumulatedAges.keySet())
		{
			N += getElementNervousness(id);
		}
		
		return N / (double) accumulatedAges.size();
	}

	/**
	 * <p>
	 * Returns the element nervousness of the given element.It is assumed that
	 * the given id correspond to an element that was listened to by the
	 * algorithm.
	 * </p>
	 * <h2>Warning</h2>
	 * <p>
	 * If this method is called with a non listened element it will throw any
	 * exception and return zero. You just have to know what you do.
	 * </p>
	 * 
	 * @param id
	 * @return The nervousness.
	 */
	public double getElementNervousness(String id)
	{
		if (appearance.get(id) == null)
		{
			return 0;
		}
		else
		{
			return appearance.get(id).doubleValue() / accumulatedAges.get(id).doubleValue();
		}
	}

	/**
	 * Give the nervousness of the given set of elements (array of ids)
	 * 
	 * @param ids
	 * @return The nervousness.
	 */
	public double getElementNervousness(String...  ids)
	{	
		double N = 0.0;
		
		for (String id : ids)
		{
			N += getElementNervousness(id);
		}
		
		return N / (double) ids.length;
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.stream.ElementSink#edgeAdded(java.lang.String, long, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void edgeAdded( String graphId, long timeId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		Edge edge = graph.getEdge( edgeId );
		
		if( edge != null )
		{
			if (structure != null)
			{
				if (structure.contains(edge.getId()))
				{
						pending.add(edge.getId());
				}
			} else
			{
				if (watchEdges)
				{
					pending.add(edge.getId());
				}
			}
		}
    }

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.stream.ElementSink#edgeRemoved(java.lang.String, long, java.lang.String)
	 */
	public void edgeRemoved( String graphId, long timeId, String edgeId )
    {
    }

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.stream.ElementSink#nodeAdded(java.lang.String, long, java.lang.String)
	 */
	public void nodeAdded( String graphId, long timeId, String nodeId )
    {
		Node node = graph.getNode( nodeId );
		
		if( node != null )
		{
			if (structure != null)
			{
				if (structure.contains(node.getId()))
				{
					pending.add(node.getId());
				}
			} else
			{
				if (watchEdges)
				{
					pending.add(node.getId());
				}
			}
		}
    }

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.stream.ElementSink#nodeRemoved(java.lang.String, long, java.lang.String)
	 */
	public void nodeRemoved( String graphId, long timeId, String nodeId )
    {
    }
	
	/*
	 * (non-Javadoc)
	 * @see org.graphstream.stream.ElementSink#graphCleared(java.lang.String, long)
	 */
	public void graphCleared( String graphId, long timeId )
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.graphstream.stream.ElementSink#stepBegins(java.lang.String, long, double)
	 */
	public void stepBegins( String graphId, long timeId, double time )
    {
		if (structure != null)
		{
			for (String id : structure)
			{
				if (graph.getNode(id) != null)
				{
					Integer i = accumulatedAges.get(id);
					if (i == null)
					{
						accumulatedAges.put(id, 1);
					} else
					{
						accumulatedAges.put(id, i + 1);
					}
				}
			}
		} else
		{
			if (watchEdges)
			{
				Iterator<? extends Edge> edgesIterator = graph.getEdgeIterator();
				while (edgesIterator.hasNext())
				{
					Edge edge = edgesIterator.next();
					String id = edge.getId();
					Integer i = accumulatedAges.get(id);
					if (i == null)
					{
						accumulatedAges.put(id, 1);
					} else
					{
						accumulatedAges.put(id, i + 1);
					}
				}
			}
			if (watchNodes)
			{
				Iterator<? extends Node> nodesIterator = graph.getNodeIterator();
				while (nodesIterator.hasNext())
				{
					Node node = nodesIterator.next();
					String id = node.getId();
					Integer i = accumulatedAges.get(id);
					if (i == null)
					{
						accumulatedAges.put(id, 1);
					} else
					{
						accumulatedAges.put(id, i + 1);
					}
				}
			}
		}

		for (String id : pending)
		{
			Integer i = appearance.get(id);
			if (i == null)
			{
				appearance.put(id, 1);
			} else
			{
				appearance.put(id, i + 1);
			}
		}
		pending.clear();
    }
}
