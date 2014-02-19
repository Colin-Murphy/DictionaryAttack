public class Hasher implements Runnable {
	
	//The shared map to store pairs in
	private Map<K,V> map;
	
	//The plain text password to hash
	private String pass;
	
	//Number of times to perform the hash
	private int numHashes = 100000;
	
	public Hasher(String pass, Map<K,V> map) {
		this.map = map;
		this.pass = pass;
	}
	
	public void run() {
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
				
				
				//Add the data to the map
				map.put(hash, pass);
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
	}
	
	
	
	
	
}