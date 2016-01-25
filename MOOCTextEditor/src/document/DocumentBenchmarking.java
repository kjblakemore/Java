package document;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/** A class for timing the EfficientDocument and BasicDocument classes
 * 
 * @author UC San Diego Intermediate Programming MOOC team
 * @author Karen Blakemore
 */

public class DocumentBenchmarking {

	
	public static void main(String [] args) {
		String textfile = "data/warAndPeace.txt";
		int start = 50000;		// initial size of test case
	    int trials = 400;		// number of iterations for each test case.
		int increment = 20000;	// number of characters to increment for test case.
		int numSteps = 30;		// number of test cases.
		
		long t0;
		long t1;
		long totalBasic;
		long totalEfficient; 
		
		// TODO: Fill in the rest of this method so that it runs two loops
		// and prints out timing results as described in the assignment 
		// instructions.
		for (int numToCheck = start; numToCheck < numSteps*increment + start; 
				numToCheck += increment) {	
			
			String text = getStringFromFile(textfile, numToCheck);
			
			t0 = System.nanoTime();
			for(int i=0; i<trials; i++) {
				BasicDocument doc = new BasicDocument(text);
				doc.getFleschScore();	
			}
			t1 = System.nanoTime();
			totalBasic = t1-t0;
			
			t0 = System.nanoTime();
			for(int i=0; i<trials; i++) {
				EfficientDocument doc = new EfficientDocument(text);
				doc.getFleschScore();
			}
			t1 = System.nanoTime();
			totalEfficient = t1-t0;
			
			System.out.println(numToCheck + "\t" + totalBasic + "\t" + totalEfficient);
		}	
	}
	
	/** Get a specified number of characters from a text file
	 * 
	 * @param filename The file to read from
	 * @param numChars The number of characters to read
	 * @return The text string from the file with the appropriate number of characters
	 */
	public static String getStringFromFile(String filename, int numChars) {
		
		StringBuffer s = new StringBuffer();
		try {
			FileInputStream inputFile= new FileInputStream(filename);
			InputStreamReader inputStream = new InputStreamReader(inputFile);
			BufferedReader bis = new BufferedReader(inputStream);
			int val;
			int count = 0;
			while ((val = bis.read()) != -1 && count < numChars) {
				s.append((char)val);
				count++;
			}
			if (count < numChars) {
				System.out.println("Warning: End of file reached at " + count + " characters.");
			}
			bis.close();
		}
		catch(Exception e)
		{
		  System.out.println(e);
		  System.exit(0);
		}
		
		
		return s.toString();
	}
	
}
