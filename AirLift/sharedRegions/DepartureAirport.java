package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

public class DepartureAirport {
	
	   /**
	   *  Control variable for the plane
	   */
	
	   boolean plane_at_transfer_gate= false;
	   boolean plane_ready_to_fly = false;
	
	   /**
	   *  Number of people in line.
	   */

	   private int nLine = 0;
	   
	   /**
	   *  Reference to passengers on board.
	   */
	   
	   private int passengersOnBoard = 0;
	   /**
	   *  Reference to passenger threads.
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
		  passen = new Passenger [SimulPar.nPassengers];
	      for (int i = 0; i < SimulPar.nPassengers; i++)
	    	  passen[i] = null;
	      try
	      { waitingLine = new MemFIFO<> (new Integer [SimulPar.nPassengers]);
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
		      System.out.println ("\033[0;91m"+"Pasenger "+passengerId+" waiting in queue in " + nLine+" position"+"\u001B[0m");

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
		      GenericIO.writelnString ("\033[44mPassenger "+ passengerId +" INFLIGHT\033[0m");	
		      return true;
		    	  
	   }
	   
	   public synchronized int waitForNextPassenger ()  //hostess function
	   {   
	      while (nLine == 0)                                 // the hostess waits for a passenger to get in the queue
	      { try
	        { 
	    	  GenericIO.writelnString ("\033[41mPassengers in line " + nLine+"\033[0m");
	    	  if (passengersOnBoard >= SimulPar.minInPlane) {
	    		  plane_ready_to_fly = true;  
	    		  return HostessStates.READYTOFLY;
	    		}
			
	    	  wait();        
	        }
	        catch (Exception e)
	        { 	GenericIO.writelnString ("\n" + "UI\n");
	        	return -1;                                     // the hostess wait has come to an end
	        }
	      }

	      if (nLine > 0) nLine -= 1;                       // the hostess takes notice some one is in Line

	      return HostessStates.CHECKPASSENGER;
	   }
	   
	   public synchronized boolean prepareForPassBoarding ()  //hostess function
	   {   
	      while (!plane_at_transfer_gate)                                 // the hostess waits for the plane to be ready
	      { try
	        { 
	    	  GenericIO.writelnString ("\n\033[0;34mHostess Waiting for Plane\033[0m\n");
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
	   
	   public synchronized void showDocuments() 
	   {
		   
	   }
	   
	   public synchronized int checkDocuments ()
	   {
		  GenericIO.writelnString ("\n\033[42m----Enter Check Documents----\033[0m");
		  int passengerId;
		  
		  try
	      { passengerId = waitingLine.read ();                            // the barber calls the customer
	        if ((passengerId < 0) || (passengerId >= SimulPar.nPassengers))
	           throw new MemException ("illegal customer id!");
	      }
	      catch (MemException e)
	      { GenericIO.writelnString ("Retrieval of customer id from waiting FIFO failed: " + e.getMessage ());
	      passengerId = -1;
	        System.exit (1);
	      }
		  GenericIO.writelnString ("Checking Doccuments of passenger "+ passengerId);
		  passen[passengerId].setPassengerState (PassengerStates.INFLIGHT);
		  notifyAll();
		  passengersOnBoard++;
	      return (passengerId);

	   }
	   
	   public synchronized void informPlaneReadyForBoarding () {
		   
		   int pilotId;
		   pilotId = ((Pilot) Thread.currentThread ()).getPilotId ();
		   ((Pilot) Thread.currentThread ()).setPilotState (PilotStates.READYFORBOARDING);
		   plane_at_transfer_gate = true;
		   GenericIO.writelnString ("Plane ready to flight");
		   notifyAll();
		   
	   }

	public synchronized void waitForAllInBoard() {
		
		while (!plane_ready_to_fly)                                 // the pilot waits for the plane to be ready
	      { try
	        { 
	    	  GenericIO.writelnString ("\n\033[44mPilot Waiting for all Passengers\033[0m\n");
	    	  wait();        
	        }
	        catch (Exception e)
	        { 	GenericIO.writelnString ("\n" + "UI\n");
	        	return ;                                     // the pilot wait has come to an end
	        }
	      }
		
	}

}
