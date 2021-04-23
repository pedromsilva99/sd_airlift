package entities;

/**
 *    Definition of the internal states of the passenger during his life cycle.
 */

public final class PassengerStates {
	
	/**
     *   The passenger is traveling to the airport.
     */
	
	public static final int GOINGTOAIRPORT = 0;
	
	/**
     *   The passenger enters the waiting queue.
     */
	
	public static final int INQUEUE = 1;
	
	/**
     *   The passenger enters the plane.
     */
	
	public static final int INFLIGHT = 2;
	
	/**
     *   The passenger arrives at the destination.
     */
	
	public static final int ATDESTINATION = 3;
	
	/**
     *   It can not be instantiated.
     */
	
	private PassengerStates ()
	   { }

}
