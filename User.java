/**
	A thread that compares the users password from the dictionary to the map of hashed passwords
	
	Designed for RIT Concepts of Parallel and Distributed Systems Project 1
	
	@author Colin L Murphy <clm3888@rit.edu>
	@version 2/7/14
*/

//Import map
import java.util.*;
//Locks
import java.util.concurrent.locks.Lock;

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

	
	public User(String username, String hashedPassword, Map<String,String> map, 
		Lock readLock, int chances) {
		
		this.username = username;
		this.hashedPassword = hashedPassword;
		this.map = map;
		this.chances = chances;
		this.readLock = readLock;
	}
	
	
	public  void run() {
		synchronized(map) {
			//Wait until every password has been hashed or our password has been found
			while (map.size() < chances && !keyFound()) {
				try {
					//System.out.println("Wait");
					map.wait();
				}
				catch (InterruptedException e) {
					System.err.println("Interrupted");
				}
			}
			//Print the key if its there
			if (keyFound()) {
				readLock.lock();
				System.out.println(username + " " + map.get(hashedPassword));
				readLock.unlock();
			}

		}
	}
	/**
		Checks if the hashed password is in the dictionary
		This is done because reimplementing the entire hashmap is somewhat overkill.
		No parameters are needed, this class knows its key
		@return boolean Whether of not the key is in the map
	*/
	public boolean keyFound() {
		try {
			readLock.lock();
			return map.containsKey(hashedPassword);
		}
		//Something wrong, just return false
		catch (Exception e) {
			System.out.println("ERROR");
			return false;
		}
		
		finally {
			readLock.unlock();
		}
	}
	
}
	
	