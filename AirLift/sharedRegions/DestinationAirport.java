package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

public class DestinationAirport extends Thread{
	
	/**
    *  Number of people in the destination airport.
    */
	private int nPassengers = 0;
	
	/**
    *  Reference to passenger threads.
    */
   
    private final Passenger [] passen;
    
    /**
     *  Variable that sinalizes the flight arrival
     */
 	private boolean arrived = false;
	
	public DestinationAirport ()//GeneralRepos repos)
	   {
		  
		  passen = new Passenger [SimulPar.nPassengers];
	      for (int i = 0; i < SimulPar.nPassengers; i++)
	    	  passen[i] = null;
	      
	      //this.repos = repos;
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
	      
	      int passengerId;                                      // passenger id
      	  Passenger passenger = ((Passenger) Thread.currentThread ()); 	
	      passengerId = passenger.getPassengerId ();
	      passen[passengerId] = passenger;
	      passen[passengerId].setPassengerState (PassengerStates.ATDESTINATION);
	      
	      GenericIO.writelnString ("\n\033[0;34mPassenger " + passengerId +" is on the destination\033[0m\n");
	      
	      return false;
	   }
	
}
