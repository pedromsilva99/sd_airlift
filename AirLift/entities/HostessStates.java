package entities;

/**
 *    Definition of the internal states of the hostess during his life cycle.
 */

public final class HostessStates {
	
	/**
     *  The hostess is checking the passenger in front of the queue.
     */
	
	public static final int CHECKPASSENGER = 0;
	
	/**
     *   The hostess waits for a passenger to get in to the queue.
     */
	
	public static final int WAITFORPASSENGER = 1;
	
	/**
     *   The hostess tells the pilot that the plane is ready to fly.
     */
	
	public static final int READYTOFLY = 2;
	
	/**
     *   The hostess waits for the flying back flight.
     */
	
	public static final int WAITFORFLIGHT = 3;
	
	/**
     *   It can not be instantiated.
     */
	
	private HostessStates ()
	   { }
	

}
