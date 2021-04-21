package entities;

import genclass.GenericIO;
import sharedRegions.*;

public class Passenger extends Thread {

	/**
	 * Passenger identification.
	 */

	private int passengerId;

	/**
	 * Passenger state.
	 */

	private int passengerState;

	/**
	 * Reference to the departure airport.
	 */

	private final DepartureAirport airport;

	/**
	 * Reference to the plane.
	 */

	private final Plane plane;

	/**
	 * Reference to the destination airport.
	 */

	private final DestinationAirport destAirport;

	/**
	 * Instantiation of a Passenger thread.
	 *
	 * @param name        thread name
	 * @param passengerId Passenger id
	 * @param airport     reference to the departure airport
	 */

	public Passenger(String name, int passengerId, DepartureAirport airport, Plane plane,
			DestinationAirport destAirport) {
		super(name);
		this.passengerId = passengerId;
		passengerState = PassengerStates.GOINGTOAIRPORT;
		this.airport = airport;
		this.plane = plane;
		this.destAirport = destAirport;
	}

	/**
	 * Set Passenger id.
	 *
	 * @param id Passenger id
	 */

	public void setPassengerId(int id) {
		passengerId = id;
	}

	/**
	 * Get Passenger id.
	 *
	 * @return Passenger id
	 */

	public int getPassengerId() {
		return passengerId;
	}

	/**
	 * Set Passenger state.
	 *
	 * @param state new Passenger state
	 */

	public void setPassengerState(int state) {
		passengerState = state;
	}

	/**
	 * Get Passenger state.
	 *
	 * @return Passenger state
	 */

	public int getPassengerState() {
		return passengerState;
	}

	/**
	 * Life cycle of the passenger.
	 */

	@Override
	public void run() {
		GenericIO.writelnString("Run Passenger " + passengerId + "\n");
		travelToAirport();
		airport.waitInQueue();
		airport.showDocuments();
		plane.boardThePlane();
		plane.leaveThePlane();

		// airport.waitForEndOfFlight();
		// leave the plane

	}

	/**
	 * 
	 *
	 * Internal operation Going to Airport
	 */

	private void travelToAirport() {
		try {
			sleep((long) (3 + 100 * Math.random()));
		} catch (InterruptedException e) {
		}
	}

}
