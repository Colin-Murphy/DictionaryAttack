import java.security.MessageDigest;
import java.awt.*;

//Must be caught to support sha-256 hashing
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


public class PasswordCrack {
	
	private int numHashes = 100000;
	
	public static void main(String args[]) {
		new PasswordCrack();
				
	}
	
	public PasswordCrack() {
		String hash = sha256("12345");
		System.out.println(hash);
	}
	
	
	public String sha256(String password) {
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				byte[] data = password.getBytes("UTF-8");
				
				for (int i=0; i<numHashes; i++) {
					md.update(data);
					data = md.digest();
				}
				
				StringBuffer hexString = new StringBuffer();
				for (byte b : data) {
					hexString.append(Integer.toHexString(0xFF & b));
				}
				
				return hexString.toString();
			}
			catch(UnsupportedEncodingException e) {
				System.out.println(e);
				return "ERROR";
			}
			
			catch(NoSuchAlgorithmException e) {
				System.out.println(e);
				return "ERROR";
			}
	}
		
		
		
	


}