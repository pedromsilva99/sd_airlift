package main;

import entities.*;
import genclass.GenericIO;
import sharedRegions.*;
public class AirLift {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Pilot [] pilot = new Pilot [SimulPar.P];				// array of pilot threads
		Hostess [] hostess = new Hostess [SimulPar.M];          // array of hostess threads
	    Passenger [] passenger = new Passenger [SimulPar.N];    // array of passenger threads
	    DepartureAirport airport;                               // reference to the departure airport
	    GeneralRepos repos;                                     // reference to the general repository
        int nIter;                                              // number of iterations of the customer life cycle
        String fileName;                                        // logging file name
        char opt;                                               // selected option
        boolean success;                                        // end of operation flag
        
        GenericIO.writelnString ("\n" + "Problem of the AirLift\n");
        GenericIO.writeString ("Number of iterations of the customer life cycle? ");
        
        airport = new DepartureAirport ();//repos);
        for (int i = 0; i < SimulPar.P; i++)
        	pilot[i] = new Pilot ("Pilot_" + (i+1), i, airport);
        for (int i = 0; i < SimulPar.M; i++)
        	hostess[i] = new Hostess ("Hostess_" + (i+1), i, airport);
        for (int i = 0; i < SimulPar.N; i++)
        	passenger[i] = new Passenger ("Passenger_" + (i+1), i, airport);
        
        for (int i = 0; i < SimulPar.P; i++)
        	pilot[i].start ();
        for (int i = 0; i < SimulPar.M; i++)
        	hostess[i].start ();
        for (int i = 0; i < SimulPar.N; i++)
        	passenger[i].start ();

	}

}
