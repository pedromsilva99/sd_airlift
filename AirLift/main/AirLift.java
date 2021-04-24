package main;

import entities.*;
import genclass.FileOp;
import genclass.GenericIO;
import sharedRegions.*;
/**
 *   Simulation of the Problem of the Air Lift.
 *   
 */
public class AirLift {
	 /**
	   *    Main class for the Airport Problem. It launches the threads for all entities of the problem.
	   *
	   *    @param args runtime arguments
	   */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Pilot [] pilot = new Pilot [SimulPar.nPilots];				// array of pilot threads
		Hostess [] hostess = new Hostess [SimulPar.nHostess];          // array of hostess threads
	    Passenger [] passenger = new Passenger [SimulPar.nPassengers];    // array of passenger threads
	    DepartureAirport airport;                               // reference to the departure airport
	    Plane plane;											// reference to the plane
	    DestinationAirport destAirport;							// reference to the destination airport
	    GeneralRepos repos;                                     // reference to the general repository
        String fileName;                                        // logging file name
        char opt;                                               // selected option
        boolean success;                                        // end of operation flag
        
        GenericIO.writelnString ("\n" + "Problem of the AirLift\n");
        do
        { GenericIO.writeString ("Logging file name? ");
          fileName = "y";//args[0];//GenericIO.readlnString ();
          if (FileOp.exists (".", fileName))
             { do
               { GenericIO.writeString ("There is already a file with this name. Delete it (y - yes; n - no)? ");
                 opt = 'y';//GenericIO.readlnChar ();
               } while ((opt != 'y') && (opt != 'n'));
               if (opt == 'y')
                  success = true;
                  else success = false;
             }
             else success = true;
        } while (!success);

        repos = new GeneralRepos (fileName);
        airport = new DepartureAirport (repos);
        plane = new Plane (repos);
        
        
        destAirport = new DestinationAirport(repos);
        for (int i = 0; i < SimulPar.nPilots; i++)
        	pilot[i] = new Pilot ("Pilot_" + (i+1), i, airport, plane, destAirport);
        for (int i = 0; i < SimulPar.nHostess; i++)
        	hostess[i] = new Hostess ("Hostess_" + (i+1), i, airport,plane);
        for (int i = 0; i < SimulPar.nPassengers; i++)
        	passenger[i] = new Passenger ("Passenger_" + (i+1), i, airport, plane, destAirport);
        
        for (int i = 0; i < SimulPar.nPilots; i++)
        	pilot[i].start ();
        for (int i = 0; i < SimulPar.nHostess; i++)
        	hostess[i].start ();
        for (int i = 0; i < SimulPar.nPassengers; i++)
        	passenger[i].start ();
        
        /* waiting for the end of the simulation */

        GenericIO.writelnString ();
        for (int i = 0; i < SimulPar.nPassengers; i++)
        { try
          { passenger[i].join ();
          }
          catch (InterruptedException e) {}
          GenericIO.writelnString ("The passenger " + (i+1) + " has terminated.");
        }
        for (int i = 0; i < SimulPar.nHostess; i++)
        { try
          { hostess[i].join ();
          }
          catch (InterruptedException e) {}
          GenericIO.writelnString ("The hostess " + (i+1) + " has terminated.");
        }
        for (int i = 0; i < SimulPar.nPilots; i++)
        { try
          { pilot[i].join ();
          }
          catch (InterruptedException e) {}
          GenericIO.writelnString ("The pilot " + (i+1) + " has terminated.");
        }
        


	}

}
