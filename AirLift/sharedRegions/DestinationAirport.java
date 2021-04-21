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
    
   
	
	public DestinationAirport ()//GeneralRepos repos)
	   {
		  
		  passen = new Passenger [SimulPar.nPassengers];
	      for (int i = 0; i < SimulPar.nPassengers; i++)
	    	  passen[i] = null;
	      
	      //this.repos = repos;
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
