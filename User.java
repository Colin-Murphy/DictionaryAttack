/**
	File: User.java
	Designed for RIT Concepts of Parallel and Distributed Systems Project 1
	
	@author Colin L Murphy <clm3888@rit.edu>
	@version 2/7/14
*/

//Import map
import java.util.*;
//Locks
import java.util.concurrent.locks.Lock;
//Atomic Integer
import java.util.concurrent.atomic.AtomicInteger;


/**
	 User models the user/password fields for a user in the databse. 
	 It then creates athread that compares the users password from the 
	 dictionary to the map of hashed passwords
*/
public class User implements Runnable {
	//Username for the user
	String username = "";
	//Users password as represented by the SHA256 hash
	String hashedPassword = "";
	
	private Map<String, String> map;
	//The number of chances that the password can be broken (used to break out of the run.
	private int chances;
	//Last capacity of the map when it was checked
	private int progress = 0;
	//Lock for readers
	private Lock readLock;
	//Thread id for knowing when its turn to print has come up
	private int id;
	//Shared integer for tracking the print id for threads
	private AtomicInteger printID;

	
	/**
		Create a new instance of a user
		@param username String the username
		@param hashedPassword the sha256 password
		@param map Map<String, String> the shared map to lookup.
		@param readLock Lock A lock to prevent writing durind a read
		@param chances int The total number of passwords that will be hashed
		@param id int The threads id to track when it should print
		@param printID AtomicInteger the shared int to track which thread should print
		@return The newly created User object
	*/
	public User(String username, String hashedPassword, Map<String,String> map, 
		Lock readLock, int chances, int id, AtomicInteger printID) {
		
		//Store all variables
		this.username = username;
		this.hashedPassword = hashedPassword;
		this.map = map;
		this.chances = chances;
		this.readLock = readLock;
		this.id = id;
		this.printID = printID;
	}
	
	/**
		Looks up and prints the username and password in the order it 
		came in the database file
	*/
	public  void run() {
		//Wait until something is added to the map
		synchronized(map) {
			//Wait until every password has been hashed or our password has been found
			while (map.size() != chances && !keyFound()) {
				try {
					//Wait on the map again
					map.wait();
				}
				catch (InterruptedException e) {
				}
			}

		}
		
		//Print the key if its there after waiting for our turn
		synchronized(printID) {
			//Wait until its your turn to print
			while (printID.get() != id) {
				try {
					//Wait your turn
					printID.wait();
				}
				catch (InterruptedException e) {}
			}
			//Print the key if it was found
			if (keyFound()) {
				//Lock to prevent writes
				readLock.lock();
				System.out.println(username + " " + map.get(hashedPassword));
				//Allow writes again
				readLock.unlock();
			}
			//Incriment printID so the next user can print
			printID.addAndGet(1);
			//Notify any other threads that are waiting to print
			printID.notifyAll();
		}

	}
	/**
		Checks if the hashed password is in the dictionary
		No parameters are needed, this class knows its key
		@return boolean Whether of not the key is in the map
	*/
	public boolean keyFound() {
		boolean result = false;
		try {
			//Lock to prevent writes
			readLock.lock();
			result = map.containsKey(hashedPassword);
		}
		//Something wrong, just return false
		catch (Exception e) {
		}
		
		finally {
			readLock.unlock();
			return result;
		}
	}
	
}
	
	