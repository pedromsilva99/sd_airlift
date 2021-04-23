package sharedRegions;

import java.util.Objects;

import entities.*;
import genclass.GenericIO;
import genclass.TextFile;
import main.SimulPar;

public class GeneralRepos {
	  /**
	   *  Name of the logging file.
	   */

	   private final String logFileName;

	  /**
	   *  Number of iterations of the customer life cycle.
	   */

	   private final int nIter;

	  /**
	   *  State of the pilot.
	   */

	   private int pilotState;

	  /**
	   *  State of the passengers.
	   */

	   private final int [] passengerState;
	   
	  /**
	   *  State of the hostess.
	   */

	   private int hostessState;
	   
	  /**
	   *  Number of passengers presently forming a queue to board the plane
	   */

	   private int inQueue = 0;
	   
	  /**
	   *  Number of passengers in the plane
	   */

	   private int inFlight = 0;
	   
	  /**
	   *  Number of passengers that have already arrived at their destination
	   */

	   private int inDestination = 0;

	  /**
	   *   Instantiation of a general repository object.
	   *
	   *     @param logFileName name of the logging file
	   *     @param nIter number of iterations of the customer life cycle
	   */
	   
	   public GeneralRepos (String logFileName, int nIter)
	   {
	      if ((logFileName == null) || Objects.equals (logFileName, ""))
	         this.logFileName = "logger";
	         else this.logFileName = logFileName;
	      this.nIter = nIter;
	      pilotState = PilotStates.ATTRANSFERGATE;
	      passengerState = new int [SimulPar.nPassengers];
	      for (int i = 0; i < SimulPar.nPassengers; i++)
	    	  passengerState[i] = PassengerStates.GOINGTOAIRPORT;
	      hostessState = HostessStates.WAITFORFLIGHT;
	      reportInitialStatus ();
	   }
	   
	   /**
	    *   Set pilot state.
	    *
	    *     @param state pilot state
	    */

	    public synchronized void setPilotState (int state)
	    {
	    	pilotState = state;
	       reportStatus ();
	    }

	   /**
	    *   Set passenger state.
	    *
	    *     @param id passenger id
	    *     @param state passenger state
	    */

	    public synchronized void setPassengerState (int id, int state)
	    {
	       passengerState[id] = state;
	       reportStatus ();
	    }

	   /**
	    *   Set hostess state.
	    *
	    *     @param state hostess state
	    */

	    public synchronized void setHostessState (int state)
	    {
	       hostessState = state;
	       reportStatus ();
	    }
	    
	   /**
	    *   Set queue.
	    *
	    *     @param number number to add
	    */

	    public synchronized void setQueue (int number)
	    {
	       inQueue += number;
	       //reportStatus ();
	    }
	    
	   /**
	    *   Set flight.
	    *
	    *     @param number number to add
	    */

	    public synchronized void setFlight (int number)
	    {
	       inFlight += number;
	       //reportStatus ();
	    }
	    
	   /**
	    *   Set destination.
	    *
	    *     @param number number to add
	    */

	    public synchronized void setDestisnation (int number)
	    {
	       inDestination += number;
	       //reportStatus ();
	    }
	    
	   /**
	    *  Write the header to the logging file.
	    *
	    *  The pilot is flying back, the passengers are going to the airport and the hostess is waiting for the flight
	    *  Internal operation.
	    */
	    private void reportInitialStatus ()
	    {
	       TextFile log = new TextFile ();                      // instantiation of a text file handler

	       if (!log.openForWriting (".", logFileName))
	          { GenericIO.writelnString ("The operation of creating the file " + logFileName + " failed!");
	            System.exit (1);
	          }
	       log.writelnString (" 			Airlift - Description of the internal state\n");
	       //log.writelnString ("\nNumber of iterations = " + nIter + "\n");
	       log.writelnString ("  PT   HT    P00   P01   P02   P03   P04   P05   P06   P07   P08   P09   P10"
	       		+ "   P11   P12   P13   P14   P15   P16   P17   P18   P19   P20  InQ  InF  PTAL");

	       if (!log.close ())
	          { GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
	            System.exit (1);
	          }
	       reportStatus ();
	    }
	    
	    /**
	     *  Write a state line at the end of the logging file.
	     *
	     *  The current state of the pilot, hostess and passengers is organized in a line to be printed.
	     *  Internal operation.
	     */

	     private void reportStatus ()
	     {
	        TextFile log = new TextFile ();                      // instantiation of a text file handler

	        String lineStatus = "";                              // state line to be printed

	        if (!log.openForAppending (".", logFileName))
	           { GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
	             System.exit (1);
	           }
	        
	        switch (pilotState)
	        { case PilotStates.ATTRANSFERGATE:     lineStatus += "ATRG ";
	                                        	   break;
	          case PilotStates.READYFORBOARDING:   lineStatus += "RDFB ";
	                                        	   break;
	          case PilotStates.WAITINGFORBOARDING: lineStatus += "WTFB ";
              									   break;
	          case PilotStates.FLYINGFORWARD:      lineStatus += "FLFW ";
	          									   break;
	          case PilotStates.DEBOARDING:         lineStatus += "DRPP ";
												   break;
	          case PilotStates.FLYINGBACK:         lineStatus += "FLBK ";
              									   break;
	        }
	        switch (hostessState)
	        { case HostessStates.CHECKPASSENGER:     lineStatus += " CKPS ";
	                                        	     break;
	          case HostessStates.WAITFORPASSENGER:   lineStatus += " WTPS ";
	                                        	     break;
	          case HostessStates.READYTOFLY:         lineStatus += " RDTF ";
              									     break;
	          case HostessStates.WAITFORFLIGHT:      lineStatus += " WTFL ";
	          									     break;
    	    }
	        for (int i = 0; i < SimulPar.nPassengers; i++)
	          switch (passengerState[i])
	          { case PassengerStates.GOINGTOAIRPORT:  lineStatus += " GTAP ";
	                                               	  break;
	            case PassengerStates.INQUEUE:         lineStatus += " INQE ";
	                                               	  break;
	            case PassengerStates.INFLIGHT:        lineStatus += " INFL ";
	                                               	  break;
	            case PassengerStates.ATDESTINATION:   lineStatus += " ATDS ";
	                                               	  break;
	          }
	        
	        lineStatus += String.format(" %2s  %2s  %2s ", inQueue, inFlight, inDestination);//" " + inQueue + "    " + inFlight + "    " + inDestination;
	        log.writelnString (lineStatus);
	        if (!log.close ())
	           { GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
	             System.exit (1);
	           }
	     }
	     
	     public synchronized void reportSpecificStatus (String message)
	     {
	        TextFile log = new TextFile ();                      // instantiation of a text file handler

	        String lineStatus = "";                              // state line to be printed

	        if (!log.openForAppending (".", logFileName))
	           { GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
	             System.exit (1);
	           }
	        
	        log.writelnString (message);
	        if (!log.close ())
	           { GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
	             System.exit (1);
	           }
	        
	     }
	   
}
