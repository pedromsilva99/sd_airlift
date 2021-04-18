package entities;

import genclass.GenericIO;
import sharedRegions.DepartureAirport;

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
	    *   Instantiation of a Pilot thread.
	    *
	    *     @param name thread name
	    *     @param barberId Pilot id
	    *     @param airport reference to the departure airport
	    */

	    public Pilot  (String name, int hostessId, DepartureAirport airport)
	    {
	       super (name);
	       this.pilotId = pilotId;
	       pilotState = PilotStates.ATTRANSFERGATE;
	       this.airport = airport;
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
	    	 GenericIO.writelnString ("\nPILOT Run \n");
	    	 parkAtTransferGate();
	    	 airport.informPlaneReadyForBoarding();
	     }

	    /**
	     *  
	     *
	     *  Internal operation?
	     */
	     private void parkAtTransferGate ()
	     {
	        try
	        { sleep ((long) (5 + 100 * Math.random ()));
	        }
	        catch (InterruptedException e) {}
	     }
	
}
