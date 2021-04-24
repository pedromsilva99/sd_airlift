package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

/**
 *    Departure airport.
 *
 *    It is responsible to keep a continuously updated account of the passengers inside the departure airport
 *    and is implemented as an implicit monitor.
 *    All public methods are executed in mutual exclusion.
 *    There are six internal synchronization points: four blocking points for the hostess, in one of them she waits for the flight,
 *    in another she waits for the plane to be ready for boarding, 
 *    in another one she wait for the passengers to get to the queue,
 *    and in the other she waits for the passenger to show his documents so that she can check the documents;
 *    and two arrays of blocking points, one of them where the passenger waits in the queue for his turn to show the documents
 *    and the other one where he waits for the hostess to check his documents and to let him enter the plane. 
 */

public class DepartureAirport extends Thread {

   /**
	* Control variable for the plane.
	*/

	boolean next_fly = false;
	
   /**
	* Control variable to board the plane.
	*/
	
	boolean plane_ready_boarding = false;

   /**
	* Number of the flight.
	*/

	private int flightNumber = 0;
	
   /**
	* Number of people in line.
	*/

	private int nLine = 0;

   /**
	* Number of people left to fly to destination.
	*/

	private int nLeft = SimulPar.nPassengers;

   /**
	* Reference to passengers on board.
	*/

	private int passengersOnBoard = 0;
   
   /**
	* Reference to passenger threads.
	*/

	private final Passenger[] passen;

   /**
	* Waiting seats occupation.
	*/

	private MemFIFO<Integer> waitingLine;

   /**
	* Reference to call the passenger with ID.
	*/
	
	private int calledPassengerId = -1;

   /**
	* Reference to the general repository.
	*/
	
	private final GeneralRepos repos;
	 
   /**
	* Reference to call the passenger.
	*/
	
	private int calledPassengerDocuments = -1;
	
   /**
	*  Departure airport instantiation.
	*
	*    @param repos reference to the general repository
	*/

	public DepartureAirport(GeneralRepos repos) {
		
		nLine = 0;
		passen = new Passenger[SimulPar.nPassengers];
		
		for (int i = 0; i < SimulPar.nPassengers; i++)
			passen[i] = null;
		
		try {
			waitingLine = new MemFIFO<>(new Integer[SimulPar.nPassengers]);
		} 
		catch (MemException e) {
			GenericIO.writelnString("Instantiation of waiting FIFO failed: " + e.getMessage());
			waitingLine = null;
			System.exit(1);
		}
		
		this.repos = repos;
		
	}

	/**
	*  Operation wait in queue.
	*
	*  It is called by the passenger when he arrives to the airport.
	*
	*/	
	
	public synchronized void waitInQueue() {

		int passengerId; // passenger id
		Passenger passenger = ((Passenger) Thread.currentThread());
		passengerId = passenger.getPassengerId();
		passen[passengerId] = passenger;
				
		try {
			waitingLine.write(passengerId);
		} 
		catch (MemException e) {
			GenericIO.writelnString("Insertion of customer id in waiting departure FIFO failed: " + e.getMessage());
			System.exit(1);
		}
		
		nLine++;
		
		GenericIO.writelnString("\033[0;91m" + "Pasenger " + passengerId + " waiting in queue in " + nLine + " position" + "\u001B[0m");
		
		passen[passengerId].setPassengerState(PassengerStates.INQUEUE);
		repos.setQueue(1);
		repos.setPassengerState (passengerId, ((Passenger) Thread.currentThread ()).getPassengerState ());	
		notifyAll();
	
		while (passengerId != calledPassengerId) {
			try {
				wait();
			} 
			catch (InterruptedException e) {}
		}
	}
 
   /**
	*  Operation show documents.
	*
	*  It is called by the passenger when the hostess wants to check his documents.
	*
	*/	
	
	public synchronized void showDocuments() {

		Passenger passenger = ((Passenger) Thread.currentThread());
		int passengerId = passenger.getPassengerId();
		passen[passengerId] = passenger;

		calledPassengerDocuments = passengerId;
		GenericIO.writelnString("\033[42m-Passenger " + passengerId + " giving his doccuments\033[0m");
		notifyAll();
		while (passenger.getPassengerState() != PassengerStates.INFLIGHT) { 
			try {
				wait();
			} 
			catch (InterruptedException e) {}
		}
	
	}
	 
   /**
	*  Operation wait for next passenger.
	*
	*  It is called by the hostess when the plane isn't ready to fly and she has to wait for passengers.
	*
	*  @return passengerId returns the Id from the passenger that is in front of the queue.
	*/	
	
	public synchronized int waitForNextPassenger() { 
		
		GenericIO.writelnString("\033[41mPassengers in line " + nLine + "\033[0m");
		((Hostess) Thread.currentThread()).setHostessState(HostessStates.WAITFORPASSENGER);
		repos.setHostessState (((Hostess) Thread.currentThread ()).getHostessState ());
		GenericIO.writelnString("\033[41mPassengers in nleft " + nLeft + "\033[0m");
		GenericIO.writelnString("\033[41mPassengers in passengersOnBoard " + passengersOnBoard + "\033[0m");
		
		if ((passengersOnBoard >= SimulPar.minInPlane && nLine == 0) || passengersOnBoard == SimulPar.maxInPlane
				|| ( nLine == 0 && nLeft==0)) {
			notifyAll();
			return -passengersOnBoard;
		}
		while (nLine == 0) {
			try {
				wait();
			} 
			catch (Exception e) {
				return -SimulPar.nPassengers-1;
			}
		}
		
		if (nLine > 0)
			nLine -= 1;

		int passengerId;
		
		try {
			passengerId = waitingLine.read();
			if ((passengerId < 0) || (passengerId >= SimulPar.nPassengers))
				throw new MemException("illegal customer id!");
		} 
		catch (MemException e) {
			GenericIO.writelnString("Retrieval of customer id from waiting FIFO failed: " + e.getMessage());
			passengerId = -SimulPar.nPassengers-1;
			System.exit(1);
		}
		
		calledPassengerId = passengerId;
		notifyAll();

		return passengerId;
		
	 }

   /**
	*  Operation prepare for pass boarding.
	*
	*  It is called by the hostess when she is waiting for the plane to arrive to the transfer gate.
	*
	*/
	
	public synchronized void prepareForPassBoarding() {
		
		while (!plane_ready_boarding){
			
			try {
				GenericIO.writelnString("\n\033[0;34mHostess Waiting for Plane\033[0m\n");
				wait();
			} 
			catch (Exception e) {}
			
		}
		
		next_fly = false;
		plane_ready_boarding = false;
		passengersOnBoard = 0;
		
	}
	
   /**
	*  Operation check documents.
	*
	*  It is called by the hostess to check the documents of the first passenger of the queue.
	*
	*  @param waitPassengerId receives the id of the passenger that is having his documents checked
	*/

	public synchronized void checkDocuments(int waitPassengerId) {
		
		GenericIO.writelnString("\n\033[42m----Enter Check Documents----\033[0m");

		while (waitPassengerId != calledPassengerDocuments) {
			try {
				wait();
			} 
			catch (InterruptedException e) {}
		}	
		
		((Hostess) Thread.currentThread()).setHostessState(HostessStates.CHECKPASSENGER);
		repos.setHostessState (((Hostess) Thread.currentThread ()).getHostessState (),waitPassengerId );

		GenericIO.writelnString("Checking Doccuments of passenger " + waitPassengerId);
		passen[waitPassengerId].setPassengerState(PassengerStates.INFLIGHT);
		passengersOnBoard++;
		nLeft--;
		notifyAll();
				
		GenericIO.writelnString("Passengers on Board " + passengersOnBoard);

	}

   /**
	*  Operation inform plane ready for boarding.
	*
	*  It is called by the pilot after parking the plane at the transfer gate.
	*
	*/
	
	public synchronized void informPlaneReadyForBoarding() {
		 
		flightNumber++;
		repos.reportSpecificStatus("\nFlight " + flightNumber + ": boarding started."); 
		 
		((Pilot) Thread.currentThread()).setPilotState(PilotStates.READYFORBOARDING);
		repos.setPilotState (((Pilot) Thread.currentThread ()).getPilotState ());
		plane_ready_boarding = true;
		GenericIO.writelnString("Plane ready to flight");
		notifyAll();

	}

   /**
	*  Operation park at transfer gate.
	*
	*  It is called by the pilot after the flight back to park the plane at the transfer gate.
	*
	*/
	
	public synchronized void parkAtTransferGate() {
		
		((Pilot) Thread.currentThread()).setPilotState(PilotStates.ATTRANSFERGATE);
		repos.setPilotState (((Pilot) Thread.currentThread ()).getPilotState ());
		GenericIO.writelnString("PLANE AT TRANSFER GATE");
		next_fly = true;
		notifyAll();

	}
	
   /**
	*  Operation check end of the day.
	*
	*  Checks if all the passengers have traveled to the destination airport.
	*
	*  @return true if all the passengers have traveled to the destination airport, false otherwise.
	*
	*/

	public Boolean CheckEndOfDay() {
		return nLeft == 0;
	}
	
   /**
	*  Operation wait for next flight.
	*
	*  It is called by the hostess when the plane left the departure airport and 
	*  she has to wait for the next flight to arrive.
	*
	*/

	public synchronized void waitForNextFlight() {
		((Hostess) Thread.currentThread()).setHostessState(HostessStates.WAITFORFLIGHT);
		repos.setHostessState (((Hostess) Thread.currentThread ()).getHostessState ()); 
		while (!next_fly) {
			
			try {
				wait();
			} 
			catch (Exception e) {
				return;
			}
			
		}
		
	}

}
