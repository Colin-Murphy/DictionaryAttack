/**
	File: Hasher.java
	Designed for RIT Concepts of Paralel and Distributed Systems Project 1
	
	@author Colin L Murphy<clm3888@rit.edu>
	@version 2/7/14
*/

//Maps
import java.util.*;
//Required to perform sha256 hashes
import java.security.MessageDigest;
//Must be caught to support sha-256 hashing
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
//Locks
import java.util.concurrent.locks.Lock;


/**
	Hasher omputes the SHA265 hash of a provided password and inserts it into
	a shared map as the key value for the plain text password pair.
	
*/
public class Hasher implements Runnable {
	
	//The shared map to store pairs in
	private Map<String,String> map;
	
	//The plain text password to hash
	private String password;
	
	//Number of times to perform the hash
	private int numHashes = 100000;
	
	//Lock used to prevent concurrent writes
	private Lock writeLock;
	
	/**
		Create a new instance of hasher
		@param password The plain text password to hash
		@param The shared map to store hashed passwords in
		@param writeLock The lock to prevent reads during writes.
	*/
	public Hasher(String password, Map<String,String> map, Lock writeLock) {
		this.map = map;
		this.password = password;
		this.writeLock = writeLock;
	}
	
	/**
		Compute the hash and add it to the map with the hash as the key
		and the plaintext password as the value
	*/
	public void run() {
		try {
			//Convert the hash
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] data = password.getBytes("UTF-8");
			
			//Hash it as many times as defined by numHashes
			for (int i=0; i<numHashes; i++) {
				md.update(data);
				data = md.digest();
			}
			
			//String representation of the hex
			String hash = "";
			for (byte b : data) {
				//Convert that byte to a hex string, append it to the hash
				String hex = Integer.toHexString(0xFF & b);
				//Fix a bug where 0's might not get added
				if (hex.length() == 1) {
					hash += "0";
				}
				//Add this hex to the hash
				hash += hex;
			}
			
			
			try {
				//Lock to prevent reads
				writeLock.lock();
				//Add the data to the map
				map.put(hash, password);
			}
			finally {
				//Unlock
				writeLock.unlock();
			}
			//Let all the users know a new password is ready
			synchronized (map){ 
				map.notifyAll();
			}
		}
		
		//Handle exceptions
		catch(UnsupportedEncodingException e) {
			System.err.println(e);
		}
		
		catch(NoSuchAlgorithmException e) {
			System.err.println(e);
		}
	}

}