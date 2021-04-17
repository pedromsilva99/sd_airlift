package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

public class DepartureAirport {
	
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
		      GenericIO.writelnString ("\n" + passen[passengerId].getPassengerState());
		      passen[passengerId].setPassengerState (PassengerStates.INQUEUE);
		      
		      nLine++;
		      GenericIO.writelnString ("nLine " + nLine);
		      try
		      { waitingLine.write (passengerId);                    // the customer sits down to wait for his turn
		      }
		      catch (MemException e)
		      { GenericIO.writelnString ("Insertion of customer id in waiting FIFO failed: " + e.getMessage ());
		          System.exit (1);
		      }
		      
		      return true;
		    	  
	   }
	   
	   public synchronized boolean waitForNextPassenger ()  //hostess function
	   {   
		  int f=0; 
	      while (nLine == 0)                                 // the barber waits for a service request
	      { try
	        { 
	    	  //GenericIO.writelnString ("nLine " + nLine);
	    	  
//	    	  if(f==0) {
//	    		  for(int i = 0; i<101;i++) {
//		    		  GenericIO.writelnString ("----" + i);
//		    	  }
//	    	  }
	    	  f=1;
	    	  wait();
	    	  
	    	  
	        
	        }
	        catch (Exception e)
	        { 	GenericIO.writelnString ("\n" + "UI\n");
	        	return true;                                     // the barber life cycle has come to an end
	        }
	      }

	      if (nLine > 0) nLine -= 1;                       // the barber takes notice some one has requested his service

	      return false;
	   }
	   
	   public synchronized int checkDocuments ()
	   {
		  GenericIO.writelnString ("\n" + "entra");
		  return 10;
//	      int passengerId;                                                // passenger id
//
//	      passengerId = ((Passenger) Thread.currentThread ()).getpassengerId ();
//	      ((Barber) Thread.currentThread ()).setBarberState (BarberStates.INACTIVITY);
//
//	      try
//	      { customerId = sitCustomer.read ();                            // the barber calls the customer
//	        if ((customerId < 0) || (customerId >= SimulPar.N))
//	           throw new MemException ("illegal customer id!");
//	      }
//	      catch (MemException e)
//	      { GenericIO.writelnString ("Retrieval of customer id from waiting FIFO failed: " + e.getMessage ());
//	        customerId = -1;
//	        System.exit (1);
//	      }
//
//	      cust[customerId].setCustomerState (CustomerStates.CUTTHEHAIR);
//
//	      return (customerId);
	   }

}
