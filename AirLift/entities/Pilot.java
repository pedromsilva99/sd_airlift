package entities;

import genclass.GenericIO;
import sharedRegions.*;

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
	    *   Instantiation of a Pilot thread.
	    *
	    *     @param name thread name
	    *     @param barberId Pilot id
	    *     @param airport reference to the departure airport
	    */

	    public Pilot  (String name, int pilotId, DepartureAirport airport, Plane plane, DestinationAirport destAirport)
	    {
	       super (name);
	       this.pilotId = pilotId;
	       this.airport = airport;
	       this.plane = plane;
	       this.destAirport = destAirport;
	    }
	    
	    /**
	     *   Set Pilot id.
	     *
	     *     @param id Pilot id
	     */

	     public void setPilotId (int id)
	     {
	    	 pilotId = id;
	     }

	    /**
	     *   Get Pilot id.
	     *
	     *     @return Pilot id
	     */

	     public int getPilotId ()
	     {
	        return pilotId;
	     }

	    /**
	     *   Set Pilot state.
	     *
	     *     @param state new Pilot state
	     */

	     public void setPilotState (int state)
	     {
	    	 pilotState = state;
	     }

	    /**
	     *   Get Pilot state.
	     *
	     *     @return Pilot state
	     */

	     public int getPilotState ()
	     {
	        return pilotState;
	     }

	    /**
	     *   Life cycle of the pilot.
	     */

	     @Override
	     public void run ()
	     {
	    	 int interaction = 0;
	    	 GenericIO.writelnString ("\nPILOT Run \n");
	    	 while(interaction == 0) {
	    		 
		    	 airport.parkAtTransferGate();
		    	 airport.informPlaneReadyForBoarding();
		    	 airport.waitForAllInBoard();
		    	 plane.flyToDestinationPoint();
		    	 destAirport.anounceArrival();
		    	 plane.flyToDeparturePoint();
		    	 //interaction = airport.checkPassengers();
		    	 interaction = 1;
	    	 }
	    	 
	     }
	
}
