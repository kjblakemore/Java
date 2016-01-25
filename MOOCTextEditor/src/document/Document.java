package document;

/** 
 * A class that represents a text document
 * @author UC San Diego Intermediate Programming MOOC team
 */
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Document {

	private String text;
	
	/** Create a new document from the given text.
	 * Because this class is abstract, this is used only from subclasses.
	 * @param text The text of the document.
	 */
	protected Document(String text)
	{
		this.text = text;
	}
	
	/** Returns the tokens that match the regex pattern from the document 
	 * text string.
	 * @param pattern A regular expression string specifying the 
	 *   token pattern desired
	 * @return A List of tokens from the document text that match the regex 
	 *   pattern
	 */
	protected List<String> getTokens(String pattern)
	{
		ArrayList<String> tokens = new ArrayList<String>();
		Pattern tokSplitter = Pattern.compile(pattern);
		Matcher m = tokSplitter.matcher(text);
		
		while (m.find()) {
			tokens.add(m.group());
		}
		
		// System.out.println("tokens: " + tokens);
		return tokens;
	}
	
	// Return the number of syllables in a word. 
	// Syllables are defined as:
	// a contiguous sequence of vowels, except for a lone "e" at the 
	// end of a word if the word has another set of contiguous vowels, 
	// makes up one syllable.   y is considered a vowel.
	protected int countSyllables(String word)
	{
		char[] chars = word.toLowerCase().toCharArray();
		int count = 0;	// number of syllables
		
		// count the number of syllables, separated by vowels.
		for(int i = 0; i< chars.length; i++) {
			if("aeiouy".indexOf(chars[i]) >= 0) {	// first vowel in sequence
				count++;
				while(i+1 < chars.length && "aeiouy".indexOf(chars[i+1]) >= 0) i++; // skip subsequent vowels in sequence.
			}
		}
			
		// special case trailing e, with no preceding vowel.
		if(count > 1 
				&& word.charAt(word.length()-1) == 'e' 
				&& "aeiou".indexOf(word.charAt(word.length()-2))  < 0)
			count -= 1;	
		
		// System.out.println("Syllables: " + word + " " + count);
	    return count;
	}
	
	/** A method for testing
	 * 
	 * @param doc The Document object to test
	 * @param syllables The expected number of syllables
	 * @param words The expected number of words
	 * @param sentences The expected number of sentences
	 * @return true if the test case passed.  False otherwise.
	 */
	public static boolean testCase(Document doc, int syllables, int words, int sentences)
	{
		System.out.println("Testing text: ");
		System.out.print(doc.getText() + "\n....");
		boolean passed = true;
		int syllFound = doc.getNumSyllables();
		int wordsFound = doc.getNumWords();
		int sentFound = doc.getNumSentences();
		double fleschScore = doc.getFleschScore();
		
		if (syllFound != syllables) {
			System.out.println("\nIncorrect number of syllables.  Found " + syllFound 
					+ ", expected " + syllables);
			passed = false;
		}
		if (wordsFound != words) {
			System.out.println("\nIncorrect number of words.  Found " + wordsFound 
					+ ", expected " + words);
			passed = false;
		}
		if (sentFound != sentences) {
			System.out.println("\nIncorrect number of sentences.  Found " + sentFound 
					+ ", expected " + sentences);
			passed = false;
		}
		
		if (passed) {
			System.out.println(fleschScore + " passed.\n");
		}
		else {
			System.out.println(fleschScore + " FAILED.\n");
		}
		return passed;
	}
	
	
	/** Return the number of words in this document */
	public abstract int getNumWords();
	
	/** Return the number of sentences in this document */
	public abstract int getNumSentences();
	
	/** Return the number of syllables in this document */
	public abstract int getNumSyllables();
	
	/** Return the entire text of this document */
	public String getText()
	{
		return this.text;
	}
	
	/** return the Flesch readability score of this document */
	public double getFleschScore()
	{
		int numWords = getNumWords();
		int numSentences = getNumSentences();
		int numSyllables = getNumSyllables();
		
		// System.out.println("In getFleshScore: " + numWords + numSentences + numSyllables);
		
		if(numWords == 0 || numSentences == 0) return 0;		
		else return 206.835 - 1.015 * numWords/numSentences - 84.6 * numSyllables/numWords;
	}	
}
