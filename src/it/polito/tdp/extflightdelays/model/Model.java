package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.management.relation.Relation;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.sun.javafx.geom.Edge;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
	List<Relations> relazioni;
	List<Airport> aereoporti;
	
	
	
	public List<Airport> creaGrafo(double min){
		aereoporti = dao.loadAllAirports();
		grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, aereoporti);
		relazioni = dao.loadAllRelations(min, aereoporti);
		// Aggiungo ogni relazione
		for (Relations r: relazioni) {
			DefaultWeightedEdge edge = grafo.addEdge(r.airport1, r.airport2);
			if(edge==null)
				continue;
			grafo.setEdgeWeight(edge, r.distance);
		}
		return aereoporti;
	}
	
	public class RelationComp implements Comparator{

		@Override
		public int compare(Object arg0, Object arg1) {
			// TODO Auto-generated method stub
			
			return 0;
		}
		
	}
	
	public String getResult(Airport airp) {
		Set<DefaultWeightedEdge> edges = grafo.edgesOf(airp);
		
		String result ="";
		edges = MF.orderEdges(grafo, edges);
		edges = MF.reverseEdgeSet(grafo, edges);
		
		for (DefaultWeightedEdge e: edges) {
			result += Graphs.getOppositeVertex(grafo, e, airp) + " " + grafo.getEdgeWeight(e) + "\n";
		}
		return result;
		
		
		
		
	}
	
	public Relations findRelation(Airport a1, Airport a2) {
		for(Relations r: this.relazioni) {
			if(r.airport1==a1 && r.airport2==a2) {
				return r;
			}
		}
		return null;
	}
	
	public List<Airport> itinerarioMigliore(List<Airport> scelti, double migliaPercorse, double migliaMax) throws Exception{
		
		if(migliaPercorse ==0) {
			// Da risolvere: problema di result
			List<Airport> result = new ArrayList<Airport>(scelti);
			double AttSize=0;
			for(Airport a: this.aereoporti) {
				List<Airport> sceltiCopy = new ArrayList<Airport>(scelti);
				sceltiCopy.add(a);
				
				// Ogni aereoporto ha la sua lista sceltiCopy
				
				try {
				List<Airport> vicini = Graphs.successorListOf(this.grafo, a);

				
				
				
				for(Airport a2: vicini) {
					DefaultWeightedEdge e = this.grafo.getEdge(a, a2);
					double peso = grafo.getEdgeWeight(e);
					double distAttuale = migliaPercorse+peso;
					if(distAttuale<migliaMax) {
//						List<Airport> sceltiCopy = new ArrayList<Airport>(scelti);
//						sceltiCopy.add(a2);
//						return this.itinerarioMigliore(sceltiCopy, distAttuale, migliaMax);
					// Altrimenti provo a vedere se aggiungendo questo migliora:
						
						List<Airport> listaTemp = new ArrayList<Airport>(sceltiCopy);
						listaTemp.add(a2);
						List<Airport> listVerifica = this.itinerarioMigliore(scelti, migliaPercorse, migliaMax);
						if( listVerifica.size() >= AttSize) {
							AttSize = listVerifica.size();
							result = listVerifica;
						}
						
						
						
					}
					
				}
				}
				catch(Exception e) {
					String stopDebug = "stop";
					List<Airport> vicini = Graphs.successorListOf(this.grafo, a);
				}
			}
			return result;
		}
		else {
			Airport a = scelti.get(scelti.size());
			List<Airport> vicini = Graphs.neighborListOf(this.grafo, a);
			
			// NON Devo usare il return ma trovare il minimo tra i vicini!
			List<Airport> result = scelti;
			double AttSize=scelti.size();

			for(Airport a2: vicini) {
				DefaultWeightedEdge e = this.grafo.getEdge(a, a2);
				double peso = grafo.getEdgeWeight(e);
				double distAttuale = migliaPercorse+peso;
				if(distAttuale<migliaMax) {
//					List<Airport> sceltiCopy = new ArrayList<Airport>(scelti);
//					sceltiCopy.add(a2);
//					return this.itinerarioMigliore(sceltiCopy, distAttuale, migliaMax);
				// Altrimenti provo a vedere se aggiungendo questo migliora:
					
					List<Airport> listaTemp = new ArrayList<Airport>(scelti);
					listaTemp.add(a2);
					List<Airport> listVerifica = this.itinerarioMigliore(scelti, migliaPercorse, migliaMax);
					if( listVerifica.size() >= AttSize) {
						AttSize = listVerifica.size();
						result = listVerifica;
					}
					
					
					
				}
				
			}
			return result;
		}
		
		
	}
	
	public String getItinerarioMigliore(double maxAccettato) throws Exception {
		List<Airport> listaNulla = new ArrayList<Airport>();
		String result = "";
		List<Airport> airpResult = this.itinerarioMigliore(listaNulla, 0, maxAccettato);
		result += "Dimensione totale: " + listaNulla.size() + "\n";
		for(Airport a: airpResult) {
			result += a.toString() + "\n"; 
		}
		return null;
		
		
	}
}
