package entities;

import genclass.GenericIO;
import sharedRegions.*;

/**
 *   Pilot thread.
 *
 *   It simulates the passenger life cycle.
 *   Static solution.
 */

public class Pilot extends Thread{
	/**
	 *  Pilot identification.
	 */

	private int pilotId;

	/**
	 *  Pilot state.
	 */

	private int pilotState;

	/**
	 *  Reference to the departure airport.
	 */

	private final DepartureAirport airport;

	/**
	 *  Reference to the plane.
	 */

	private final Plane plane;

	/**
	 *  Reference to the destination airport.
	 */

	private final DestinationAirport destAirport;

	/**
	 * Control variable to know when to break the cycle.
	 */

	private Boolean endOfDay;

	/**
	 *   Instantiation of a Pilot thread.
	 *
	 *     @param name         thread name
	 *     @param pilotId      pilot id
	 *     @param airport      reference to the departure airport
	 *     @param plane 	      reference to the plane
	 *     @param destAirport  reference to the destination airport
	 */

	public Pilot  (String name, int pilotId, DepartureAirport airport, Plane plane, DestinationAirport destAirport){
		super (name);
		this.pilotId = pilotId;
		this.airport = airport;
		this.plane = plane;
		this.destAirport = destAirport;
		endOfDay=false;
	}

	/**
	 *   Set Pilot id.
	 *     @param id Pilot id
	 */

	public void setPilotId (int id){pilotId = id;}
	/**
	 *   Get Pilot id.
	 *     @return Pilot id
	 */

	public int getPilotId (){return pilotId;}

	/**
	 *   Set Pilot state.
	 *
	 *     @param state new Pilot state
	 */

	public void setPilotState (int state){pilotState = state;}

	/**
	 *   Get Pilot state.
	 *
	 *     @return Pilot state
	 */

	public int getPilotState (){return pilotState;}

	/**
	 *   Life cycle of the pilot.
	 */

	@Override
	public void run (){
		GenericIO.writelnString ("\nPILOT Run \n");
		while(!endOfDay) {
			airport.informPlaneReadyForBoarding();
			plane.waitForAllInBoard();
			plane.flyToDestinationPoint();
			plane.anounceArrival();
			destAirport.flyToDeparturePoint();
			airport.parkAtTransferGate();
			endOfDay = airport.CheckEndOfDay();
		}
		plane.lastPrint();
		GenericIO.writelnString("\033[41m Pilot End Of Life \033[0m");
	}
}
