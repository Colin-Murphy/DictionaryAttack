/**
	A thread that compares the users password from the dictionary to the map of hashed passwords
	
	Designed for RIT Concepts of Paralel and Distributed Systems Project 1
	
	@author Colin L Murphy <clm3888@rit.edu>
	@version 2/7/14
*/

//Import map
import java.util.*;

public class User implements Runnable {
	//Username for the user
	String username = "";
	//Users password as represented by the SHA256 hash
	String hashedPassword = "";
	
	Map<String, String> map;

	
	public User(String username, String hashedPassword, Map<String,String> map) {
		this.username = username;
		this.hashedPassword = hashedPassword;
		this.map = map;
	}
	
	
	public synchronized void run() {
		while (!map.containsKey(hashedPassword)) {
			try {
				System.out.println("Wait");
				wait();
			}
			catch (InterruptedException e) {
				System.err.println("Interrupted");
			}
		}
		
		System.out.println(username + " " + map.get(hashedPassword));
	
	}
	
}
	
	