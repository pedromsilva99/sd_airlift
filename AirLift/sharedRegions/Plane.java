package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

public class Plane {
	
		/**
	    *  Number of pasengers in the plane.
	    */
	
	    private int nPassengers = 0;
	    
	    /**
	    *  Reference to passenger threads.
	    */
    
	    private final Passenger [] passen;

	    /**
	    *   Plane seats occupation.
	    */

	    private MemFIFO<Integer> planeSeats;

	    /**
	    *   Reference to the general repository.
	    */

	    //private final GeneralRepos repos;
	   
	   
		public Plane ()//GeneralRepos repos)
		   {
			  passen = new Passenger [SimulPar.nPassengers];
		      for (int i = 0; i < SimulPar.nPassengers; i++)
		    	  passen[i] = null;
		      try
		      { planeSeats = new MemFIFO<> (new Integer [SimulPar.maxInPlane]);
		      }
		      catch (MemException e)
		      { GenericIO.writelnString ("Instantiation of waiting FIFO failed: " + e.getMessage ());
		        planeSeats = null;
		        System.exit (1);
		      }
		      //this.repos = repos;
		   }
		
		public synchronized void boardThePlane ()  //hostess function
		   {   
		      int passengerId = ((Passenger) Thread.currentThread ()).getPassengerId ();
		      ((Passenger) Thread.currentThread ()).setPassengerState (PassengerStates.INFLIGHT);
		      GenericIO.writelnString ("\u001B[45mPASSENGER IN FLIGHT " + passengerId + "\u001B[0m");
		      
		   }
}
