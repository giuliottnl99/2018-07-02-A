package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class MF {
	
	// Questa funzione mi ordina gli edge, ma va sostituita ogni volta
	public static Set<DefaultWeightedEdge> orderEdges(SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo, Set<DefaultWeightedEdge> edges){
		// Per ogni nodo trovo il valore e lo metto in una treeMap
		TreeMap<Double, DefaultWeightedEdge> mappa = new TreeMap<Double, DefaultWeightedEdge>();
		Set<DefaultWeightedEdge> result = new HashSet<DefaultWeightedEdge>();
		for(DefaultWeightedEdge e: edges) {
			mappa.put(grafo.getEdgeWeight(e), e);
			
		}
		for(DefaultWeightedEdge e: mappa.values()) {
			result.add(e);
		}
		return result;
		
	}
	public static Set<DefaultWeightedEdge> reverseEdgeSet(SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo, Set<DefaultWeightedEdge> edges){
		ArrayList<DefaultWeightedEdge> vett = new ArrayList<DefaultWeightedEdge>(edges); 
		Collections.reverse(vett);
		Set<DefaultWeightedEdge> result = new HashSet<DefaultWeightedEdge>();
		for (DefaultWeightedEdge e: vett) {
			result.add(e);
		}
		return result;
	}
	
	public static List<Airport> successorList(SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo, Airport elemento){
		Set<DefaultWeightedEdge> edges = grafo.edgesOf(elemento);
		List<Airport> result = new ArrayList<Airport>();
		for(DefaultWeightedEdge e: edges) {
			Airport a = Graphs.getOppositeVertex(grafo, e, elemento);
			result.add(a);
		}
		return result;
	}
	
}
