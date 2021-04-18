package entities;

import genclass.GenericIO;
import sharedRegions.DepartureAirport;

public class Hostess extends Thread{
	   /**
	   *  Hostess identification.
	   */

	   private int hostessId;

	  /**
	   *  Hostess state.
	   */

	   private int hostessState;

	  /**
	   *  Reference to the departure airport.
	   */

	   private final DepartureAirport airport;
	   
	   /**
	    *   Instantiation of a hostess thread.
	    *
	    *     @param name thread name
	    *     @param barberId barber id
	    *     @param bShop reference to the barber shop
	    */

	    public Hostess  (String name, int hostessId, DepartureAirport airport)
	    {
	       super (name);
	       this.hostessId = hostessId;
	       hostessState = HostessStates.WAITFORFLIGHT;
	       this.airport = airport;
	    }
	    
	    /**
	     *   Set hostess id.
	     *
	     *     @param id hostess id
	     */

	     public void setHostessId (int id)
	     {
	    	 hostessId = id;
	     }

	    /**
	     *   Get hostess id.
	     *
	     *     @return hostess id
	     */

	     public int getHostessId ()
	     {
	        return hostessId;
	     }

	    /**
	     *   Set hostess state.
	     *
	     *     @param state new hostess state
	     */

	     public void setHostessState (int state)
	     {
	    	 hostessState = state;
	     }

	    /**
	     *   Get hostess state.
	     *
	     *     @return hostess state
	     */

	     public int getHostessState ()
	     {
	        return hostessState;
	     }

	    /**
	     *   Life cycle of the hostess.
	     */

	     @Override
	     public void run ()
	     {
	    	 GenericIO.writelnString ("\nHostess RUN\n");
	    	 int passengerId;                                     // passenger id
	         boolean endOp;
	         while (true)
	         { 
	           endOp = airport.prepareForPassBoarding();
	           if (endOp) break;
	           
	           //---------------------------------------------------------------------------------------
	           while (hostessState != HostessStates.READYTOFLY)
	           {   
	        	   int waitPassenger = airport.waitForNextPassenger();
		           if ( waitPassenger == HostessStates.CHECKPASSENGER)                                   
		        	   passengerId = airport.checkDocuments ();
		           else if (waitPassenger == HostessStates.READYTOFLY) {
		        	   hostessState=HostessStates.READYTOFLY;
		        	   GenericIO.writelnString ("Hostess Ready To FLY \n");
		        	   System.exit(0);
		           }
		           else {
					//error
				}
	           
		           
	           }
	           //GenericIO.writelnString ("Hostess Ready To FLY \n");
	           //----------------------------------------------------------------------------------------
	           //informPlaneReadyToTakeOff
	           
	           //waitForNextFlight
	           
	           //notifyAll();

	         }
	     }

	    /**
	     *  
	     *
	     *  Internal operation?
	     */
	     
//	     private void checkDocuments() {
//	    	 try
//	         { sleep ((long) (1 + 50 * Math.random ()));
//	         }
//	         catch (InterruptedException e) {}
//	     }

}
