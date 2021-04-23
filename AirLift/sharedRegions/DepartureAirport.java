package sharedRegions;

import main.*;
import entities.*;
import commInfra.*;
import genclass.GenericIO;

public class DepartureAirport extends Thread {

	/**
	 * Control variable for the plane
	 */

	 boolean plane_ready_to_fly = false;
	 boolean next_fly = false;
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
	 * Number of people left to fly to destination
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
	 * Reference to call the passenger with ID
	 */
	 private int calledPassengerId = -1;

	/**
	 * Reference to the general repository.
	 */
	
	 private final GeneralRepos repos;
	
	 private int calledPassengerDocuments = -1;

	 public DepartureAirport(GeneralRepos repos)
	 {
		nLine = 0;
		passen = new Passenger[SimulPar.nPassengers];
		for (int i = 0; i < SimulPar.nPassengers; i++)
			passen[i] = null;
		try {
			waitingLine = new MemFIFO<>(new Integer[SimulPar.nPassengers]);
		} catch (MemException e) {
			GenericIO.writelnString("Instantiation of waiting FIFO failed: " + e.getMessage());
			waitingLine = null;
			System.exit(1);
		}
		this.repos = repos;
	 }

	 public synchronized void waitInQueue() {

		int passengerId; // passenger id
		Passenger passenger = ((Passenger) Thread.currentThread());
		passengerId = passenger.getPassengerId();
		passen[passengerId] = passenger;
				
		try {
			waitingLine.write(passengerId); // the customer sits down to wait for his turn
		} catch (MemException e) {
			GenericIO.writelnString("Insertion of customer id in waiting departure FIFO failed: " + e.getMessage());
			System.exit(1);
		}
		nLine++;
		
		GenericIO.writelnString(
				"\033[0;91m" + "Pasenger " + passengerId + " waiting in queue in " + nLine + " position" + "\u001B[0m");
		passen[passengerId].setPassengerState(PassengerStates.INQUEUE);
		repos.setQueue(1);
		repos.setPassengerState (passengerId, ((Passenger) Thread.currentThread ()).getPassengerState ());	
		notifyAll();

		while (passengerId != calledPassengerId) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	 }
 
	 public synchronized boolean showDocuments() {

		int passengerId; // passenger id
		Passenger passenger = ((Passenger) Thread.currentThread());
		passengerId = passenger.getPassengerId();
		passen[passengerId] = passenger;
		//passen[passengerId].setPassengerState(PassengerStates.INQUEUE);
		//repos.setPassengerState (passengerId, ((Passenger) Thread.currentThread ()).getPassengerState ());

		calledPassengerDocuments = passengerId;
		GenericIO.writelnString("\033[42m-Passenger " + passengerId + " giving his doccuments\033[0m");
		notifyAll();
		while (
				passenger.getPassengerState() != PassengerStates.INFLIGHT) { 
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		return true;
	 }
	 
	 public synchronized void informPlaneReadyToTakeOff() {
		 try {
			sleep((long) (3 + 100 * Math.random()));
		 } catch (InterruptedException e) {
		 }

		 //repos.reportSpecificStatus("\nFlight " + flightNumber + " : departed with " +passengersOnBoard + " passengers.");
		 ((Hostess) Thread.currentThread()).setHostessState(HostessStates.READYTOFLY);
		 repos.setHostessState (((Hostess) Thread.currentThread ()).getHostessState ());
		 plane_ready_to_fly = true;
		 passengersOnBoard = 0;
		 notifyAll();
	 }

	 public synchronized int waitForNextPassenger() { // hostess function
		GenericIO.writelnString("\033[41mPassengers in line " + nLine + "\033[0m");
		((Hostess) Thread.currentThread()).setHostessState(HostessStates.WAITFORPASSENGER);
		repos.setHostessState (((Hostess) Thread.currentThread ()).getHostessState ());
		
		if ((passengersOnBoard >= SimulPar.minInPlane && nLine == 0) || passengersOnBoard == SimulPar.maxInPlane
				|| ( nLine == 0 && nLeft==0)) {
			plane_ready_to_fly = true;
			passengersOnBoard = 0;
			notifyAll();
			return -1;
		}
		while (nLine == 0) { // the hostess waits for a passenger to get in the queue
			try {wait();
			} catch (Exception e) {
				return -1; // the hostess wait has come to an end
			}
		}
		
		if (nLine > 0)
			nLine -= 1; // the hostess takes notice some one is in Line

		int passengerId;
		try {
			passengerId = waitingLine.read();
			if ((passengerId < 0) || (passengerId >= SimulPar.nPassengers))
				throw new MemException("illegal customer id!");
		} catch (MemException e) {
			GenericIO.writelnString("Retrieval of customer id from waiting FIFO failed: " + e.getMessage());
			passengerId = -1;
			System.exit(1);
		}
		calledPassengerId = passengerId;
		notifyAll();

		return passengerId;
	 }

	 public synchronized boolean prepareForPassBoarding() // hostess function
	 {
		
		while (!plane_ready_boarding) // the hostess waits for the plane to be ready
		{
			try {
				GenericIO.writelnString("\n\033[0;34mHostess Waiting for Plane\033[0m\n");
				wait();
			} catch (Exception e) {
				return true; // the hostess wait has come to an end
			}
		}
		int hostessId = ((Hostess) Thread.currentThread()).getHostessId();
		((Hostess) Thread.currentThread()).setHostessState(HostessStates.WAITFORPASSENGER);
		repos.setHostessState (((Hostess) Thread.currentThread ()).getHostessState ());
		next_fly = false;
		return false;
	 }

	 public synchronized int checkDocuments(int waitPassengerId) {
		GenericIO.writelnString("\n\033[42m----Enter Check Documents----\033[0m");

		while (waitPassengerId != calledPassengerDocuments) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		
		repos.reportSpecificStatus("\nFlight " + flightNumber + ": passenger " + waitPassengerId + " checked.");
		
		((Hostess) Thread.currentThread()).setHostessState(HostessStates.CHECKPASSENGER);
		repos.setHostessState (((Hostess) Thread.currentThread ()).getHostessState ());

		GenericIO.writelnString("Checking Doccuments of passenger " + waitPassengerId);
		passen[waitPassengerId].setPassengerState(PassengerStates.INFLIGHT);

		notifyAll();
		passengersOnBoard++;
		nLeft--;
		GenericIO.writelnString("Passengers on Board " + passengersOnBoard);
		return (waitPassengerId);
	 }

	 public synchronized void informPlaneReadyForBoarding() {
		 
		flightNumber++;
		repos.reportSpecificStatus("\nFlight " + flightNumber + ": boarding started."); 
		 
		((Pilot) Thread.currentThread()).setPilotState(PilotStates.READYFORBOARDING);
		repos.setPilotState (((Pilot) Thread.currentThread ()).getPilotState ());
		plane_ready_boarding = true;
		GenericIO.writelnString("Plane ready to flight");
		notifyAll();

	 }

	 public synchronized void waitForAllInBoard() {

		((Pilot) Thread.currentThread()).setPilotState(PilotStates.WAITINGFORBOARDING);
		repos.setPilotState (((Pilot) Thread.currentThread ()).getPilotState ());
		while (!plane_ready_to_fly) // the pilot waits for the plane to be ready
		{
			try {
				GenericIO.writelnString("\n\033[44mPilot Waiting for all Passengers\033[0m\n");
				wait();
			} catch (Exception e) {
				return; // the pilot wait has come to an end
			}
		}

		
		GenericIO.writelnString("Everybody on board");
		GenericIO.writelnString("Passengers left: " + nLeft);
	 }

	 public synchronized void parkAtTransferGate() {
		
		((Pilot) Thread.currentThread()).setPilotState(PilotStates.ATTRANSFERGATE);
		repos.setPilotState (((Pilot) Thread.currentThread ()).getPilotState ());
		GenericIO.writelnString("PLANE AT TRANSFER GATE");
		plane_ready_to_fly = false;
		next_fly = true;
		notifyAll();

	 }

	 public int checkPassengers() {
		return nLeft;
	 }

	 public Boolean CheckEndOfDay() {
		return nLeft == 0;
	 }

	 public synchronized void waitForNextFlight() { // hostess function
		((Hostess) Thread.currentThread()).setHostessState(HostessStates.WAITFORFLIGHT);
		repos.setHostessState (((Hostess) Thread.currentThread ()).getHostessState ()); 
		while (!next_fly) { // the hostess waits for the next flight
			try {
				wait();
			} catch (Exception e) {
				return; // the hostess wait has come to an end
			}
		}
	 }

}
