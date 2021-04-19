package main;

import entities.*;
import genclass.GenericIO;
import sharedRegions.*;
/**
 *   Simulation of the Problem of the Sleeping Barbers.
 *   
 */
public class AirLift {
	 /**
	   *    Main class for the Airport Rhapsody Problem. It launches the threads for all entities of the problem.
	   *
	   *    @param args runtime arguments
	   */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Pilot [] pilot = new Pilot [SimulPar.nPilots];				// array of pilot threads
		Hostess [] hostess = new Hostess [SimulPar.nHostess];          // array of hostess threads
	    Passenger [] passenger = new Passenger [SimulPar.nPassengers];    // array of passenger threads
	    DepartureAirport airport;                               // reference to the departure airport
	    Plane plane;											// reference to the plane
	    DestinationAirport destAirport;							// reference to the destination airport
	    GeneralRepos repos;                                     // reference to the general repository
        int nIter;                                              // number of iterations of the customer life cycle
        String fileName;                                        // logging file name
        char opt;                                               // selected option
        boolean success;                                        // end of operation flag
        
        GenericIO.writelnString ("\n" + "Problem of the AirLift\n");
        GenericIO.writeString ("Number of iterations of the customer life cycle? ");
        
        airport = new DepartureAirport ();//repos);
        plane = new Plane ();
        destAirport = new DestinationAirport();
        for (int i = 0; i < SimulPar.nPilots; i++)
        	pilot[i] = new Pilot ("Pilot_" + (i+1), i, airport, plane, destAirport);
        for (int i = 0; i < SimulPar.nHostess; i++)
        	hostess[i] = new Hostess ("Hostess_" + (i+1), i, airport);
        for (int i = 0; i < SimulPar.nPassengers; i++)
        	passenger[i] = new Passenger ("Passenger_" + (i+1), i, airport, plane, destAirport);
        
        for (int i = 0; i < SimulPar.nPilots; i++)
        	pilot[i].start ();
        for (int i = 0; i < SimulPar.nHostess; i++)
        	hostess[i].start ();
        for (int i = 0; i < SimulPar.nPassengers; i++)
        	passenger[i].start ();

	}

}
