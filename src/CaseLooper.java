
public class CaseLooper {
	
	private static Selenium seleniumInstance;

	public static void main(String[] args) throws Exception {
	   
    	// sets up Selenium 
		seleniumInstance = new Selenium();
		
		String page = "";
		
		// loop for each Medical CPB case; set to 1 to test for a single Medical CPB 
	    for (int i = 0; i < 1; i++) {
	    	seleniumInstance.setUp();
			page = seleniumInstance.execute(0, i);
			System.out.println(page);
	    }
	}
}