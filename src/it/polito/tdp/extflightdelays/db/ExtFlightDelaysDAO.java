package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Flight;
import it.polito.tdp.extflightdelays.model.Relations;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				result.add(airport);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public Airport findAirport(int airportId, List<Airport> listaAereoporti) {
		for(Airport a: listaAereoporti) {
			if(a.getId()==airportId) {
				return a;
			}
			
		}
		return null;
	}
	
	public List<Relations> loadAllRelations(double min, List<Airport> listaAereoporti) {
		String sql = "SELECT * FROM (SELECT SUM(DISTANCE) AS S1, COUNT(*) AS C1, "
				+ "ORIGIN_AIRPORT_ID, DESTINATION_AIRPORT_ID "
				+ "FROM flights GROUP BY ORIGIN_AIRPORT_ID, DESTINATION_AIRPORT_ID) AS T1, "
				+ "(SELECT SUM(DISTANCE) AS S2, COUNT(*) AS C2, ORIGIN_AIRPORT_ID, "
				+ "DESTINATION_AIRPORT_ID FROM flights GROUP BY ORIGIN_AIRPORT_ID, DESTINATION_AIRPORT_ID) "
				+ "AS T2 WHERE T1.ORIGIN_AIRPORT_ID = T2.DESTINATION_AIRPORT_ID AND T2.ORIGIN_AIRPORT_ID = "
				+ "T1.DESTINATION_AIRPORT_ID GROUP BY T1.ORIGIN_AIRPORT_ID, T1.DESTINATION_AIRPORT_ID";
		List<Relations> result = new ArrayList<Relations>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int airP = rs.getInt("ORIGIN_AIRPORT_ID");
				int airT = rs.getInt("DESTINATION_AIRPORT_ID");
				double S1 = rs.getInt("S1");
				double S2 = rs.getInt("S2");
				double C1 = rs.getInt("C1");
				double C2 = rs.getInt("C2");
				Airport aereoportoP = this.findAirport(airP, listaAereoporti);
				Airport aereoportoT = this.findAirport(airT, listaAereoporti);
				double peso = (S1+S2)/(C1+C2);
				if(peso>=min) {
					Relations r = new Relations(aereoportoP, aereoportoT, peso);
					result.add(r);
					
				}
				
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	
	
}
