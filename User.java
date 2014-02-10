/**
	Models a user from the database file to make storing and accessing easier.
	
	Designed for RIT Concepts of Paralel and Distributed Systems Project 1
	
	@author Colin L Murphy <clm3888@rit.edu>
	@version 2/7/14
*/

public class User {
	//Username for the user… wow this comment is useless
	String username = "";
	//Users password as represented by the SHA256 hash
	String hashedPassword = "";
	//Users password determined by the dictionary attack
	String textPassword = "";
	
	public User(String username, String hashedPassword) {
		this.username = username;
		this.hashedPassword = hashedPassword;
	}
	
	/**
		Set the plain text password of a user.
		@param String the plain text password to store
	*/
	public void setTextPassword(String textPassword) {
		this.textPassword = textPassword;
	}
	
	/**
		Getter for the hashed password
		@return String SHA256 representation of the users password
	*/
	public String getHashedPassword() {
		return hashedPassword;
	}
	/**
		Getter for the username
		@return String username
	*/
	public String getUsername() {
		return username;
	}
	
}
	
	