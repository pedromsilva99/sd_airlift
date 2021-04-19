package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

public class DepartureAirport extends Thread{
	
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
	   *  Number of people left to fly to destination
	   */

	   private int nLeft = SimulPar.nPassengers;
	   
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
	   *   Reference to call the passenger with ID
	   */
	   private int calledPassengerId =-1;
	   
	   /**
	   *   Reference to the general repository.
	   */
	   private int calledPassengerDocuments =-1;
	   
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
	   
	   public synchronized void waitInQueue () {
		  
		      int passengerId;                                      // passenger id
	      	  Passenger passenger = ((Passenger) Thread.currentThread ()); 	
		      passengerId = passenger.getPassengerId ();
		      passen[passengerId] = passenger;
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
		      
		      while(passengerId!=calledPassengerId) {
		    	  try{ 
		        	wait ();
		    	  }
		    	  catch (InterruptedException e) {}
		      }

	   }
	   public synchronized boolean showDocuments () {
			  
		      int passengerId;                                      // passenger id
	      	  Passenger passenger = ((Passenger) Thread.currentThread ()); 	
		      passengerId = passenger.getPassengerId ();
		      passen[passengerId] = passenger;
		      passen[passengerId].setPassengerState (PassengerStates.INQUEUE);
		      
		      calledPassengerDocuments = passengerId;
		      GenericIO.writelnString ("\033[42m-Passenger "+ passengerId +" giving his doccuments\033[0m");
		      notifyAll();
		      while (passenger.getPassengerState()!= PassengerStates.INFLIGHT)
		      { /* the customer waits for the service to be executed */
		        try{ 
		        	wait ();
		        }
		        catch (InterruptedException e) {}
		      }
		      return true;
	   }
	   
	   
	   public synchronized int waitForNextPassenger () {  //hostess function
	      
	      while (nLine == 0){                                 // the hostess waits for a passenger to get in the queue
	       try{ 
	    	  GenericIO.writelnString ("\033[41mPassengers in line " + nLine+"\033[0m");
	    	  if (passengersOnBoard >= SimulPar.minInPlane) {
	    		  plane_ready_to_fly = true;  
	    		  return -1;
	    		}
	    	  wait();        
	        }
	        catch (Exception e){
	        	return -1;                                     // the hostess wait has come to an end
	        }
	      }

	      if (nLine > 0) nLine -= 1;                       // the hostess takes notice some one is in Line
	      
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
		  calledPassengerId=passengerId;
		  notifyAll();
		  
	      return passengerId;
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
	        { 	
	        	return true;                                     // the hostess wait has come to an end
	        }
	      }
	      int hostessId = ((Hostess) Thread.currentThread ()).getHostessId ();
	      ((Hostess) Thread.currentThread ()).setHostessState (HostessStates.WAITFORPASSENGER);
	      
	      return false;
	   }
	   
	   
	   public synchronized int checkDocuments (int waitPassengerId)
	   {
		  GenericIO.writelnString ("\n\033[42m----Enter Check Documents----\033[0m");
		  nLeft--;
		  while(waitPassengerId!=calledPassengerDocuments) {
	    	  try{ 
	        	wait ();
	    	  }
	    	  catch (InterruptedException e) {}
	      }
		  
		  GenericIO.writelnString ("Checking Doccuments of passenger "+ waitPassengerId);
		  passen[waitPassengerId].setPassengerState (PassengerStates.INFLIGHT);
		  notifyAll();
		  passengersOnBoard++;
	      return (waitPassengerId);

	   }
	   
	   public synchronized void informPlaneReadyForBoarding () {
		   
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
	        { 	
	        	return ;                                     // the pilot wait has come to an end
	        }
	      }
		
		((Pilot) Thread.currentThread ()).setPilotState (PilotStates.WAITINGFORBOARDING);
		GenericIO.writelnString ("Everybody on board");
		GenericIO.writelnString ("Passengers left: " + nLeft);
	}
	
	public synchronized void parkAtTransferGate ()
    {
       try
       { sleep ((long) (5 + 100 * Math.random ()));
       }
       catch (InterruptedException e) {}
       ((Pilot) Thread.currentThread ()).setPilotState (PilotStates.ATTRANSFERGATE);
       GenericIO.writelnString ("PLANE AT TRANSFER GATE");
       plane_ready_to_fly = false;
    }
	
	
	public int checkPassengers() {
		return nLeft;
	}
	
	public synchronized void waitForNextFlight () {  //hostess function
	      
	      while (!plane_ready_to_fly){                                 // the hostess waits for a passenger to get in the queue
	       try{ 
	    	   //Incompleto
	    	  
	    	  wait();        
	        }
	        catch (Exception e){
	        	return;                                  // the hostess wait has come to an end
	        }
	      }
	   }

}
