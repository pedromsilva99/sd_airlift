package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

public class DepartureAirport {
	
	   /**
	   *  Control variable for the plane
	   */
	
	   boolean planeReady = false;
	
	   /**
	   *  Number of people in line.
	   */

	   private int nLine = 0;
	   
	   /**
	   *  Reference to customer threads.
	   */

	   private final Passenger [] passen;

	  /**
	   *   Waiting seats occupation.
	   */

	   private MemFIFO<Integer> waitingLine;

	  /**
	   *   Reference to the general repository.
	   */

	   //private final GeneralRepos repos;
	   
	   public DepartureAirport ()//GeneralRepos repos)
	   {
		  nLine = 0;
		  passen = new Passenger [SimulPar.N];
	      for (int i = 0; i < SimulPar.N; i++)
	    	  passen[i] = null;
	      try
	      { waitingLine = new MemFIFO<> (new Integer [SimulPar.K]);
	      }
	      catch (MemException e)
	      { GenericIO.writelnString ("Instantiation of waiting FIFO failed: " + e.getMessage ());
	        waitingLine = null;
	        System.exit (1);
	      }
	      //this.repos = repos;
	   }
	   
	   public synchronized boolean waitInQueue () {
		      int passengerId;                                      // passenger id

		      passengerId = ((Passenger) Thread.currentThread ()).getPassengerId ();
		      passen[passengerId] = (Passenger) Thread.currentThread ();
		      passen[passengerId].setPassengerState (PassengerStates.INQUEUE);
		      
		      nLine++;
		      GenericIO.writelnString ("Pasenger "+passengerId+" waiting in queue in " + nLine+" position");
		      try
		      { waitingLine.write (passengerId);                    // the customer sits down to wait for his turn
		      }
		      catch (MemException e)
		      { GenericIO.writelnString ("Insertion of customer id in waiting FIFO failed: " + e.getMessage ());
		          System.exit (1);
		      }
		      notifyAll ();
		      
		      while (((Passenger) Thread.currentThread ()).getPassengerState()!= PassengerStates.INFLIGHT)
		      { /* the customer waits for the service to be executed */
		        try
		        { wait ();
		        }
		        catch (InterruptedException e) {}
		      }

		      return true;
		    	  
	   }
	   
	   public synchronized boolean waitForNextPassenger ()  //hostess function
	   {   
	      while (nLine == 0)                                 // the hostess waits for a passenger to get in the queue
	      { try
	        { 
	    	  GenericIO.writelnString ("Hostest passengers in line " + nLine);
	    	  wait();        
	        }
	        catch (Exception e)
	        { 	GenericIO.writelnString ("\n" + "UI\n");
	        	return true;                                     // the hostess wait has come to an end
	        }
	      }

	      if (nLine > 0) nLine -= 1;                       // the hostess takes notice some one is in Line

	      return false;
	   }
	   
	   public synchronized boolean prepareForPassBoarding ()  //hostess function
	   {   
	      while (!planeReady)                                 // the hostess waits for the plane to be ready
	      { try
	        { 
	    	  GenericIO.writelnString ("\nHostess Waiting for Plane\n");
	    	  wait();        
	        }
	        catch (Exception e)
	        { 	GenericIO.writelnString ("\n" + "UI\n");
	        	return true;                                     // the hostess wait has come to an end
	        }
	      }
	      int hostessId = ((Hostess) Thread.currentThread ()).getHostessId ();
	      ((Hostess) Thread.currentThread ()).setHostessState (HostessStates.WAITFORPASSENGER);
	      
	      return false;
	   }
	   
	   public synchronized int checkDocuments ()
	   {
		  GenericIO.writelnString ("\n----Enter Check Documents----");
		  int passengerId;
		  
		  try
	      { passengerId = waitingLine.read ();                            // the barber calls the customer
	        if ((passengerId < 0) || (passengerId >= SimulPar.N))
	           throw new MemException ("illegal customer id!");
	      }
	      catch (MemException e)
	      { GenericIO.writelnString ("Retrieval of customer id from waiting FIFO failed: " + e.getMessage ());
	      passengerId = -1;
	        System.exit (1);
	      }
		  GenericIO.writelnString ("Checking Doccuments of passenger "+ passengerId);
		  passen[passengerId].setPassengerState (PassengerStates.INFLIGHT);
		  GenericIO.writelnString ("Passenger "+ passengerId +" INFLIGHT");
	      return (passengerId);

	   }
	   
	   public synchronized void informPlaneReadyForBoarding () {
		   
		   int pilotId;
		   pilotId = ((Pilot) Thread.currentThread ()).getPilotId ();
		   ((Pilot) Thread.currentThread ()).setPilotState (PilotStates.READYFORBOARDING);
		   planeReady = true;
		   GenericIO.writelnString ("Plane ready to flight");
		   notifyAll();
		   
	   }

}
