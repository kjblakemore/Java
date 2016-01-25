package spelling;


/** A class for timing the Dictionary Implementations
 * 
 * @author UC San Diego Intermediate Programming MOOC team
 *
 */

public class DictionaryBenchmarking {

	
	public static void main(String [] args) {

	    // Run each test more than once to get bigger numbers and less noise.
	    // You can try playing around with this number.
	    int trials = 500;

	    // The text to test on
	    String dictFile = "data/dict.txt";
		
	    // The amount of words to increment each step
	    // You can play around with this
		int increment = 1000;

		// The number of steps to run.  
		// You can play around with this.
		int numSteps = 99;
		
		// The number of words to start with. 
		// You can play around with this.
		int start = 1;
		
		String notInDictionary = "notaword";
		int numToCheck = start;
		
		// TODO: Play around with the numbers above and graph the output to see trends in the data
		while(numToCheck <= 99170) {
			// Time the creation of finding a word that is not in the dictionary.
			DictionaryLL llDict = new DictionaryLL();
			DictionaryBST bstDict = new DictionaryBST();
			
			DictionaryLoader.loadDictionary(llDict, dictFile, numToCheck);
			DictionaryLoader.loadDictionary(bstDict, dictFile, numToCheck);
			
			long startTime = System.nanoTime();
			for (int i = 0; i < trials; i++) {
				llDict.isWord(notInDictionary);
			}
			long endTime = System.nanoTime();
			long timeLL = (endTime - startTime);  
			
			startTime = System.nanoTime();
			for (int i = 0; i < trials; i++) {
				bstDict.isWord(notInDictionary);
			}
			endTime = System.nanoTime();
			long timeBST = (endTime - startTime);
			
			System.out.println(numToCheck + "\t" + timeLL + "\t" + timeBST);
			
			numToCheck = numToCheck * 2;
			
		}
	
	}
	
}
