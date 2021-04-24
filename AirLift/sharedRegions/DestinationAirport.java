package sharedRegions;

import main.*;
import entities.*;
import genclass.GenericIO;

/**
 * Destination Airport shared Region
 */

public class DestinationAirport extends Thread{

	/**
	 *  Reference to passenger threads.
	 */
	private final Passenger [] passen;

	/**
	 * Reference to the general repository.
	 */
	private final GeneralRepos repos;

	/**
	 *   Instantiation of DestinationAirport object.
	 *
	 *     @param repos repository object for logging
	 */
	public DestinationAirport (GeneralRepos repos){
		passen = new Passenger [SimulPar.nPassengers];
		for (int i = 0; i < SimulPar.nPassengers; i++)
			passen[i] = null;

		this.repos = repos;
	}
	
	/**
	 *  Sleep a certain amount of time to simulate the plane flying to the departure airport.
	 *	After the Sleep, the Pilot changes his state to FLYINGBACK	
	 *     
	 */
	public synchronized void flyToDeparturePoint () {  //hostess function
		try{ 
			sleep ((long) (3 + 100 * Math.random ()));
		}
		catch (InterruptedException e) {}

		((Pilot) Thread.currentThread ()).setPilotState (PilotStates.FLYINGBACK);
		repos.setPilotState (((Pilot) Thread.currentThread ()).getPilotState ());
		GenericIO.writelnString ("\u001B[45mPLANE FLYING TO DEPARTURE AIRPORT \u001B[0m");

	}
}
