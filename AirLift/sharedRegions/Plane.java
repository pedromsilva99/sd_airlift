package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *    Plane.
 *
 *    It is responsible to keep a continuously updated account of the passengers inside the plane
 *    and is implemented as an implicit monitor.
 *    All public methods are executed in mutual exclusion.
 *    There are four internal synchronization points: two single blocking point for the pilot, one where he waits for the signal
 *    that all passengers have entered the plane and another where he waits for all passengers to leave the plane;
 *    one an array of blocking points, one per each passenger, where he waits for the plane to arrive at the destination airport;
 *    and one single blocking point for the hostess, where she waits for all the passengers to enter the plane to tell the pilot.
 *    where cutting chair while having his hair cut.
 */

public class Plane extends Thread
{
	
   /**
	*  Number of passengers in the plane.
	*/
	
	private int nPassengers = 0;
	
   /**
	*  Vector for saving the number of passengers per flight.
	*/
	
	private int [] nPassForFlight;
	
   /**
	*  Number of passengers that left the flight.
	*/
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
	* Number of the flight.
	*/
	
	private int flightNumber = 0;
	 
   /**
	*   Reference to the general repository.
	*/
	
	private final GeneralRepos repos;
	
   /**
	*  Variable that signals the flight arrival
	*/
	private boolean allOnBoard = false;
	
   /**
	*  Variable that signals the flight arrival
	*/
	private boolean arrived = false;
	   
   /**
	*  Barber shop instantiation.
	*
	*    @param repos reference to the general repository
	*/
 	
	public Plane (GeneralRepos repos) {
		
		passen = new Passenger [SimulPar.nPassengers];
		nPassForFlight = new int [5];
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
		this.repos = repos;
    }
		
	/**
	 *  Operation board the plane.
	 *
	 *  It is called by a passenger when he has permission to enter the plane.
	 *
	 */
	
	public synchronized void boardThePlane () {   
		
		nPassengers++;										// the passenger sits on the plane
		int passengerId = ((Passenger) Thread.currentThread ()).getPassengerId ();
		((Passenger) Thread.currentThread ()).setPassengerState (PassengerStates.INFLIGHT);
	    repos.setFlight(1);									
	    repos.setQueue(-1);
	    repos.setPassengerState (passengerId, ((Passenger) Thread.currentThread ()).getPassengerState ());
	    GenericIO.writelnString ("\u001B[45mPASSENGER IN FLIGHT " + passengerId + "\u001B[0m");
	    try { 
	    	planeSeats.write (passengerId);                 // the passenger sits on the plane and waits for the end of the flight
	    }
	    catch (MemException e) {
	    	GenericIO.writelnString ("Insertion of customer id in plane FIFO failed: " + e.getMessage ());
	        System.exit (1);
	    }
	    notifyAll();   										// the passenger lets his presence be known
	 }
		
   /**
	*  Operation wait for all in board.
	*
	*  It is called by the pilot after he signals that the plane is ready for boarding.
	*  The pilot waits for all the passengers to enter the plane.
	*
	*/
	
	public synchronized void waitForAllInBoard() {

		((Pilot) Thread.currentThread()).setPilotState(PilotStates.WAITINGFORBOARDING);
		repos.setPilotState (((Pilot) Thread.currentThread ()).getPilotState ());
		while (!allOnBoard) 
		{
			try {
				GenericIO.writelnString("\n\033[44mPilot Waiting for all Passengers\033[0m\n");
				wait();
			} 
			catch (Exception e) {
				return;
			}
		}	
		
		GenericIO.writelnString("Everybody on board");
		GenericIO.writelnString("Passengers left: " + nPassengersLeft);
	}
	
   /**
	*  Operation inform plane ready to take off.
	*
	*  It is called by the hostess after every passenger entering the plane.
	*
	*  @param nboarded number of people that boarded the plane
	*/
	
	public synchronized void informPlaneReadyToTakeOff(int nboarded) {
		
		while (nboarded!= nPassengers) {
			try {
				GenericIO.writelnString("\n\033[44mPilot Waiting for all Passengers\033[0m\n");
				wait();
			}
			catch (Exception e) {
				return;
			}
		}

		((Hostess) Thread.currentThread()).setHostessState(HostessStates.READYTOFLY);
		repos.setHostessState (((Hostess) Thread.currentThread ()).getHostessState ());
		allOnBoard = true;
		notifyAll();
		
	 }
		
   /**
	*  Operation fly to destination point.
	*
	*  It is called by the pilot to fly to the destination airport.
	*
	*/	
	
	public synchronized void flyToDestinationPoint () {
        
		try { 
        	sleep ((long) (3 + 100 * Math.random ()));
        }
        catch (InterruptedException e) {}
        GenericIO.writelnString ("NPassengers = "+nPassengers);
        
        flightNumber++;
        nPassForFlight[flightNumber-1] = nPassengers;
        ((Pilot) Thread.currentThread ()).setPilotState (PilotStates.FLYINGFORWARD);
        repos.setPilotState (((Pilot) Thread.currentThread ()).getPilotState ());
        GenericIO.writelnString ("\u001B[45mPLANE FLYING TO DESTINATION AIRPORT \u001B[0m");
        
   	}
   /**
	*  Operation announce arrival.
	*
	*  It is called by the pilot when he arrives at the destination airport.
	*
	*/		
		
	public synchronized void announceArrival () {
	   try {
		   sleep ((long) (1 + 100 * Math.random ()));
	   }
	   catch (InterruptedException e) {}
	   
	   repos.reportSpecificStatus("\nFlight " + flightNumber +": arrived.");
	   ((Pilot) Thread.currentThread ()).setPilotState (PilotStates.DEBOARDING);
	   repos.setPilotState (((Pilot) Thread.currentThread ()).getPilotState ());
	   GenericIO.writelnString ("PLANE ARRIVED");
	   arrived = true;
	   notifyAll();
	   while (nPassengersLeft!=nPassengers) {
		   try { 
	    	  GenericIO.writelnString ("\n\033[0;34mPILOT waiting for passengers to leave the plane\033[0m\n");
	    	  wait();        
	        }
	        catch (Exception e) { 	
	        	return ;
	        }
	      }
	    while(nPassengers >0) {
	    	try{
	    		planeSeats.read ();                   
		      }
		      catch (MemException e) {
		    	  GenericIO.writelnString ("Removal of customer id in plane FIFO failed: " + e.getMessage ());
		          System.exit (1);
		      }
	    	nPassengers--;
	    }
	    
	    nPassengers=0;
	    nPassengersLeft=0;
	    allOnBoard = false;
	    arrived=false; 
	    
	}
		
   /**
	*  Operation leave the plane.
	*
	*  It is called by the passenger to leave the plane.
	*
	*/	
	
	public synchronized void leaveThePlane () {   
	    while (!arrived) {
	    	try { 
	    		GenericIO.writelnString ("\n\033[0;34mPassenger waiting for plane arrival\033[0m\n");
		    	wait();        
		    }
		    catch (Exception e) { 	
		    	System.exit(0);                 
		    }
	    }
	      
	    repos.setFlight(-1);
	    repos.setDestisnation(1);
	    nPassengersLeft++;
	    notifyAll();
	    int passengerId;                                      // passenger id
	  	Passenger passenger = ((Passenger) Thread.currentThread ()); 	
	    passengerId = passenger.getPassengerId ();
	    passen[passengerId] = passenger;
	    passen[passengerId].setPassengerState (PassengerStates.ATDESTINATION);
	    repos.setPassengerState (passengerId, ((Passenger) Thread.currentThread ()).getPassengerState ());
	    
	    GenericIO.writelnString ("\n\033[0;34mPassenger " + passengerId +" is on the destination\033[0m\n");
	    
	}
	
   /**
	*  Operation last print.
	*
	*  It is called by the pilot in the end to print the last information lines of the logger file.
	*
	*/
	
	public void lastPrint() {
		
		repos.reportSpecificStatus("\nAirlift sum up:");
		
		for (int i=1; i<=flightNumber; i++) {
			repos.reportSpecificStatus("Flight " + i + " transported " + nPassForFlight[i-1] + " passengers");
		}
		
	}
	
}
