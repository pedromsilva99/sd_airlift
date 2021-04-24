package entities;

import genclass.GenericIO;
import main.SimulPar;
import sharedRegions.DepartureAirport;
import sharedRegions.Plane;

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
	 * Reference to the plane.
	 */
	private final Plane plane;

	/**
	 * Control variable to know if there is no passengers to fly
	 */
	private Boolean endOfDay;

	/**
	 * Instantiation of a hostess thread.
	 *
	 * @param name       thread name
	 * @param hostessId  hostess id
	 * @param airport    reference to the departure airport
	 * @param plane    	 reference to the plane
	 */
	public Hostess(String name, int hostessId, DepartureAirport airport, Plane plane) {
		super(name);
		this.hostessId = hostessId;
		hostessState = HostessStates.WAITFORFLIGHT;
		this.airport = airport;
		this.plane = plane;
		endOfDay = false;
	}

	/**
	 * Set hostess id.
	 *
	 * @param id hostess id
	 */
	public void setHostessId(int id) {hostessId = id;}

	/**
	 * Get hostess id.
	 *
	 * @return hostess id
	 */
	public int getHostessId() {return hostessId;}

	/**
	 * Set hostess state.
	 *
	 * @param state new hostess state
	 */
	public void setHostessState(int state) {hostessState = state;}

	/**
	 * Get hostess state.
	 *
	 * @return hostess state
	 */
	public int getHostessState() {return hostessState;}

	/**
	 * Life cycle of the hostess.
	 * When the hostess is created, while there is passengers to fly she follows a routine:
	 * <p>waits for the pilot to arrive at the Transfer Gate
	 * <p>After the plane is ready for boarding the Hostess Waits for a passenger to Call
	 * <p>Then ask the passenger to check his documents
	 * <p>If the plane is full or there is no more passengers to fly, she informs the pilot to take off
	 * <p>otherwise she calls for the next passenger
	 * 
	 */

	@Override
	public void run() {
		GenericIO.writelnString("\nHostess RUN\n");
		while (!endOfDay) {
			airport.prepareForPassBoarding();

			while (hostessState != HostessStates.READYTOFLY) {
				int waitPassengerId = airport.waitForNextPassenger();
				if (waitPassengerId >= 0)
					airport.checkDocuments(waitPassengerId);
				else if (waitPassengerId == -SimulPar.nPassengers-1) {
					GenericIO.writelnString("ERROR");
					System.exit(0);
				} else {
					plane.informPlaneReadyToTakeOff(waitPassengerId*-1);
				}
			}
			airport.waitForNextFlight();
			endOfDay = airport.CheckEndOfDay();
		}
		GenericIO.writelnString("\033[41m Hostess End Of Life \033[0m");
	}
}
