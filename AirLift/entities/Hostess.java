package entities;

import genclass.GenericIO;
import sharedRegions.DepartureAirport;

/**
 *   Hostess thread.
 *
 *   It simulates the hostess life cycle.
 *   Static solution.
 */

public class Hostess extends Thread {
	/**
	 * Hostess identification.
	 */

	private int hostessId;

	/**
	 * Hostess state.
	 */

	private int hostessState;

	/**
	 * Reference to the departure airport.
	 */

	private final DepartureAirport airport;
	
	/**
	 * Control variable to know when to break the cycle.
	 */
	
	private Boolean endOfDay;

	/**
	 * Instantiation of a hostess thread.
	 *
	 * @param name       thread name
	 * @param hostessId  hostess id
	 * @param airport    reference to the departure airport
	 */
	

	public Hostess(String name, int hostessId, DepartureAirport airport) {
		super(name);
		this.hostessId = hostessId;
		hostessState = HostessStates.WAITFORFLIGHT;
		this.airport = airport;
		endOfDay = false;
	}

	/**
	 * Set hostess id.
	 *
	 * @param id hostess id
	 */

	public void setHostessId(int id) {
		hostessId = id;
	}

	/**
	 * Get hostess id.
	 *
	 * @return hostess id
	 */

	public int getHostessId() {
		return hostessId;
	}

	/**
	 * Set hostess state.
	 *
	 * @param state new hostess state
	 */

	public void setHostessState(int state) {
		hostessState = state;
	}

	/**
	 * Get hostess state.
	 *
	 * @return hostess state
	 */

	public int getHostessState() {
		return hostessState;
	}

	/**
	 * Life cycle of the hostess.
	 */

	@Override
	public void run() {
		GenericIO.writelnString("\nHostess RUN\n");
		while (!endOfDay) {
			//int passengerId; // passenger id
			boolean endOp;
			int i = 0;
			while (i == 0) {
				//
				endOp = airport.prepareForPassBoarding();
				if (endOp)
					break;

				// ---------------------------------------------------------------------------------------
				while (hostessState != HostessStates.READYTOFLY) {
					int waitPassengerId = airport.waitForNextPassenger();
					if (waitPassengerId >= 0)
						airport.checkDocuments(waitPassengerId);
					else if (waitPassengerId == -1) {
						airport.informPlaneReadyToTakeOff();
						//hostessState = HostessStates.READYTOFLY;
						// System.exit(0);
					} else {
						GenericIO.writelnString("ERROR");
						// System.exit(0);
					}
					i = 1;
				}
				airport.waitForNextFlight();
			}
			endOfDay = airport.CheckEndOfDay();
		}
		GenericIO.writelnString("\033[41m Hostess End Of Life \033[0m");

	}
	
}
