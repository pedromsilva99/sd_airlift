package entities;

/**
 *    Definition of the internal states of the passenger during his life cycle.
 */

public final class PilotStates {
	
	/**
     *   The pilot is parks at transfer gate.
     */
	
	public static final int ATTRANSFERGATE = 0;
	
	/**
     *   The pilot tells the hostess that the plane is ready for boarding.
     */
	
	public static final int READYFORBOARDING = 1;
	
	/**
     *   The pilot for every passenger to enter the plane.
     */
	
	public static final int WAITINGFORBOARDING = 2;
	
	/**
     *   The pilot flies the plane to the destination airport.
     */
	
	public static final int FLYINGFORWARD = 3;
	
	/**
     *   The pilot waits for all passengers to leave the plane.
     */
	
	public static final int DEBOARDING = 4;
	
	/**
     *   The pilot flies the plane to the departure airport.
     */
	
	public static final int FLYINGBACK = 5;
	
	/**
     *   It can not be instantiated.
     */
	
	private PilotStates ()
	   { }

}
