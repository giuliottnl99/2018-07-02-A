package it.polito.tdp.extflightdelays.model;

public class Relations {
	Airport airport1;
	Airport airport2;
	double distance;
	
	public Relations(Airport airport1, Airport airport2, double peso) {
		super();
		this.airport1 = airport1;
		this.airport2 = airport2;
		this.distance = peso;
	}
	
	public int compareTo(Relations r) {
		return (int) (this.distance - r.distance);
	}
	
	
}
