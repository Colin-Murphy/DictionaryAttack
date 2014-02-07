/**
	PasswordCrack performs a dictionary attack on a provided database using a 
	provided dictionary file. It imlements multi threading and is an example
	of proper thread design.
	
	Designed for RIT Concepts of Paralel and Distributed Systems Project 1
	
	@author Colin L Murphy <clm3888@rit.edu>
	@version 2/7/14
*/
	

//Required to perform sha256 hashes
import java.security.MessageDigest;
//Must be caught to support sha-256 hashing
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
//Read files
import java.io.File;
//Arraylist for storing users and hashed passwords
import java.util.ArrayList;
//Buffered reader
import java.io.BufferedReader;
//File reader
import java.io.FileReader;
//Io exception
import java.io.IOException;
//File not found
import java.io.FileNotFoundException;


public class PasswordCrack {
	
	//Number of times to hash the given password
	private int numHashes = 100000;
	
	public static void main(String args[]) {
		//Insure proper amount of arguments
		if (args.length != 2) {
			System.err.println("Usage: java PasswordCrack <dictionaryFile> " + 
				"<databaseFile");
			//Were done here
			return;
		}
		
		File dictionaryFile = new File(args[0]);
		
		if (!dictionaryFile.exists()) {
			System.err.println("Error " + args[0] + " does not exist.");
			return;
		}
		
		File databaseFile = new File(args[1]);
		if (!dictionaryFile.exists()) {
			System.err.println("Error: " + args[1] + " does not exist.");
			return;
		}
		
		//If we got here then the input appears to be good
		new PasswordCrack(dictionaryFile, databaseFile);
				
	}//End of main
	
	
	/**
		Performs a dictionary attack on user/password database 
		using a dictionary provided.
		
		Implements multithreaded techniques and design patterns
		
		@param File dictionaryFile the the dictionary file
		@param String databaseFile the the database file
	*/
	public PasswordCrack(File dictionaryFile, File databaseFile) {
		
		ArrayList<String> passwords = new ArrayList<String>();
		ArrayList<User> users = new ArrayList<User>();
		
		BufferedReader reader;
		try {
			reader = new BufferedReader
				(new FileReader(dictionaryFile));
		}
		
		catch(FileNotFoundException e) {
			System.err.println("File " + dictionaryFile.getName() + 
				" does not exist");
			return;
		}
			
		String line = "";
		try {
			while ((line = reader.readLine()) !=null) {
					passwords.add(line.trim());
			}
		}
		
		catch(IOException e) {
			System.err.println("Error reading " + dictionaryFile.getName());
			return;
		}
		
		for (String password:passwords) {
			System.out.println(password);
		}

	}//End of PasswordCrack
	
	/**
		Converts a given string to a SHA256 hash as many times as specified by 
		numHashes
		@param A string of the password to hash
		@return String the returned 
	
	*/
	public String sha256(String password) {
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
					hash += Integer.toHexString(0xFF & b);
				}
				
				//Return a string of the hex value
				return hash;
			}
			
			//Handle exceptions
			catch(UnsupportedEncodingException e) {
				System.err.println(e);
				return "ERROR";
			}
			
			catch(NoSuchAlgorithmException e) {
				System.err.println(e);
				return "ERROR";
			}
	}//End of sha256
		
		
		
	


}