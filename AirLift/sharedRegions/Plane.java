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
	    private int nPassengersLeft = 0;
	    
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
	    
	    /**
	     *  Variable that sinalizes the flight arrival
	     */
	 	private boolean arrived = false;
	   
	   
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
		      { GenericIO.writelnString ("Insertion of customer id in plane FIFO failed: " + e.getMessage ());
		          System.exit (1);
		      }
		      nPassengers++;
		      //
		   }
		
		
		
		public synchronized void flyToDestinationPoint ()  //hostess function
		   {
	        try
	        { 
	        sleep ((long) (3 + 100 * Math.random ()));
	        }
	        catch (InterruptedException e) {}
	        GenericIO.writelnString ("NPassengers = "+nPassengers);
	        
	        ((Pilot) Thread.currentThread ()).setPilotState (PilotStates.FLYINGFORWARD);
	        GenericIO.writelnString ("\u001B[45mPLANE FLYING TO DESTINATION AIRPORT \u001B[0m");
		   }
		
		
		
		public synchronized void anounceArrival ()
	    {
	       try
	       { sleep ((long) (1 + 100 * Math.random ()));
	       }
	       catch (InterruptedException e) {}
	       ((Pilot) Thread.currentThread ()).setPilotState (PilotStates.DEBOARDING);
	       GenericIO.writelnString ("PLANE ARRIVED");
	       arrived = true;
	       notifyAll();
	       while (nPassengersLeft!=nPassengers)                                 // the hostess waits for the plane to be ready
		      { try
		        { 
		    	  GenericIO.writelnString ("\n\033[0;34mPassenger waiting for passengers to leave the plane\033[0m\n");
		    	  wait();        
		        }
		        catch (Exception e)
		        { 	
		        	return ;                                // the hostess wait has come to an end
		        }
		      }
	        while(nPassengers >0) {
	        	try
			      { planeSeats.read ();                    // the customer sits down to wait for his turn
			      }
			      catch (MemException e)
			      { GenericIO.writelnString ("Removal of customer id in plane FIFO failed: " + e.getMessage ());
			          System.exit (1);
			      }
	        	nPassengers--;
	        }
	        nPassengers=0;
	        nPassengersLeft=0;
	       arrived=false;
	       
	       
	    }
		
		public synchronized boolean leaveThePlane ()  //hostess function
		   {   
		      while (!arrived)                                 // the hostess waits for the plane to be ready
		      { try
		        { 
		    	  GenericIO.writelnString ("\n\033[0;34mPassenger waiting for plane arrival\033[0m\n");
		    	  wait();        
		        }
		        catch (Exception e)
		        { 	
		        	return true;                                     // the hostess wait has come to an end
		        }
		      }
		      nPassengersLeft++;
		      notifyAll();
		      int passengerId;                                      // passenger id
	      	  Passenger passenger = ((Passenger) Thread.currentThread ()); 	
		      passengerId = passenger.getPassengerId ();
		      passen[passengerId] = passenger;
		      passen[passengerId].setPassengerState (PassengerStates.ATDESTINATION);
		      
		      GenericIO.writelnString ("\n\033[0;34mPassenger " + passengerId +" is on the destination\033[0m\n");
		      
		      return false;
		   }
		
		
		
		
		
}
