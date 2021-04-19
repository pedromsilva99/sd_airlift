package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

public class Plane extends Thread{
	
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
		      try
		      { planeSeats.write (passengerId);                    // the customer sits down to wait for his turn
		      }
		      catch (MemException e)
		      { GenericIO.writelnString ("Insertion of customer id in waiting FIFO failed: " + e.getMessage ());
		          System.exit (1);
		      }
		      nPassengers++;
		      //É mesmo necessário colocar o id dos passageiros numa estrutura de dados??
		   }
		
		
		
		public synchronized void flyToDestinationPoint ()  //hostess function
		   {
	        try
	        { 
	        sleep ((long) (3 + 100 * Math.random ()));
	        }
	        catch (InterruptedException e) {}
	        ((Pilot) Thread.currentThread ()).setPilotState (PilotStates.FLYINGFORWARD);
	        GenericIO.writelnString ("\u001B[45mPLANE FLYING TO DESTINATION AIRPORT \u001B[0m");
		   }
		
		public synchronized void flyToDeparturePoint ()  //hostess function
		   {
	        try
	        { 
	        sleep ((long) (3 + 100 * Math.random ()));
	        }
	        catch (InterruptedException e) {}
	        ((Pilot) Thread.currentThread ()).setPilotState (PilotStates.FLYINGBACK);
	        GenericIO.writelnString ("\u001B[45mPLANE FLYING TO DEPARTURE AIRPORT \u001B[0m");
		   }
		
		
		
		
		
}
