//Maps
import java.util.*;
//Required to perform sha256 hashes
import java.security.MessageDigest;
//Must be caught to support sha-256 hashing
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
//Semaphore used to block in critical sections
import java.util.concurrent.Semaphore;

public class Hasher implements Runnable {
	
	//The shared map to store pairs in
	private Map<String,String> map;
	
	//The plain text password to hash
	private String password;
	
	//Number of times to perform the hash
	private int numHashes = 100000;
	
	//Semaphore used to block in critical sections
	private Semaphore semaphore;
	
	public Hasher(String password, Map<String,String> map, Semaphore semaphore) {
		this.map = map;
		this.password = password;
		this.semaphore = semaphore;
	}
	
	public synchronized void run() {
		try {
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
					hash += Integer.toHexString(0xFF & b);
				}
				
				
				//acquire the semaphore or block (beginning of critical section)
				semaphore.acquireUninterruptibly();
				//Add the data to the map
				map.put(hash, password);
				notifyAll();
				//release the semaphore (end of critical section)
				semaphore.release();
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