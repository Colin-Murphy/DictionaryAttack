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
//Maps
import java.util.*;
//Semaphore used to block in critical sections
import java.util.concurrent.Semaphore;
//Locks
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
//Atomic Integer
import java.util.concurrent.atomic.AtomicInteger;


public class PasswordCrack {
	
	//Number of times to hash the given password
	private int numHashes = 100000;
	//Locks for the threads
	private static ReadWriteLock base = new ReentrantReadWriteLock();
	private static Lock readLock = base.readLock();
	private static Lock writeLock = base.writeLock();
	
	public static void main(String args[]) throws Throwable {
		//Insure proper amount of arguments
		if (args.length != 2) {
			System.err.println("Usage: java PasswordCrack <dictionaryFile> " + 
				"<databaseFile>");
			//Were done here
			return;
		}
		
		File dictionaryFile = new File(args[0]);
		File databaseFile = new File(args[1]);
		
		//If we got here then the input appears to be good
		new PasswordCrack(dictionaryFile, databaseFile);
				
	}//End of main
	
	
	/**
		Performs a dictionary attack on user/password database 
		using a dictionary provided.
		
		Implements multithreaded techniques and design patterns
		
		@param File dictionaryFile the the dictionary file
		@param File databaseFile the the database file
	*/
	public PasswordCrack(File dictionaryFile, File databaseFile) throws Throwable {
		//Reader for reading the input files
		BufferedReader reader;
		//Store the passwords
		ArrayList<String> passwords = new ArrayList<String>();
		
		//Read the dictionary first 
		try {
			reader = new BufferedReader
				(new FileReader(dictionaryFile));
		}
		catch(FileNotFoundException e) {
			System.err.println("File " + dictionaryFile.getName() + 
				" does not exist");
			System.exit (1);
			return;
		}
		
		//Go line by line and put the password into the passwords array
		String line = "";
		try {
			while ((line = reader.readLine()) !=null) {
					passwords.add(line.trim());
			}
		}
		
		//Something went wrong reading the file, tell the user and give up
		catch(IOException e) {
			System.err.println("Error reading " + dictionaryFile.getName());
			System.exit (1);
		}
		
		//Read the database next
		try {
			reader = new BufferedReader
				(new FileReader(databaseFile));
		}
		
		//File not found, tell the user and give up
		catch(FileNotFoundException e) {
			System.err.println("File " + databaseFile.getName() + 
				" does not exist");
			System.exit (1);
			return;
		}
		
		//Shared map to store the hashed passwords
		Map<String, String> map = new HashMap<String, String>();
		//Arraylist of user threads
		ArrayList<Thread> users = new ArrayList<Thread>();
		
		//Locks for the threads
		base = new ReentrantReadWriteLock();
		readLock = base.readLock();
		writeLock = base.writeLock();
		
		AtomicInteger printID = new AtomicInteger();
		
		//Parse input and start thread group 2 (users)
		try {
			int i = 0;
			while((line = reader.readLine()) !=null) {
				String[] tokens = line.split("\\s+");
				Thread t = new Thread(new User(tokens[0],tokens[1], map, readLock, passwords.size(),
				i, printID));
				t.start();
				users.add(t);
				i++;
				
			}
		}
		//Something went wrong reading the file, tell the user and give up
		catch(IOException e) {
			System.err.println("Error reading " + databaseFile.getName());
			System.exit (1);
		}
		
		//Arraylist of the hashers threads
		ArrayList<Thread> hashers = new ArrayList<Thread>();
		for (String password:passwords) {
			Thread t = new Thread(new Hasher(password, map, writeLock));
			t.start();
			hashers.add(t);
		}
		
		//Join all the threads, no further actions may be done in this thread per requirements
		for (Thread t: users) {
			t.join();
		}
		
		for (Thread t: hashers) {
			t.join();
		}
		
		

	}//End of PasswordCrack constructor
	
}