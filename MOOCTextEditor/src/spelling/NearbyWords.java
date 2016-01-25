/**
 * 
 */
package spelling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


/**
 * @author UC San Diego Intermediate MOOC team and Karen Blakemore
 *
 */
public class NearbyWords implements SpellingSuggest {
	// THRESHOLD to determine how many words to look through when looking
	// for spelling suggestions (stops prohibitively long searching)
	// For use in the Optional Optimization in Part 2.
	private static final int THRESHOLD = 1000; 

	Dictionary dict;

	public NearbyWords (Dictionary dict) 
	{
		this.dict = dict;
	}

	/** Return the list of Strings that are one modification away
	 * from the input string.  
	 * @param s The original String
	 * @param wordsOnly controls whether to return only words or any String
	 * @return list of Strings which are nearby the original string
	 */
	public List<String> distanceOne(String s, boolean wordsOnly )  {
		   List<String> retList = new ArrayList<String>();
		   insertions(s, retList, wordsOnly);
		   subsitution(s, retList, wordsOnly);
		   deletions(s, retList, wordsOnly);
		   return retList;
	}

	
	/** Add to the currentList Strings that are one character mutation away
	 * from the input string.  
	 * @param s The original String
	 * @param currentList is the list of words to append modified words 
	 * @param wordsOnly controls whether to return only words or any String
	 * @return
	 */
	public void subsitution(String s, List<String> currentList, boolean wordsOnly) {
		// for each letter in the s and for all possible replacement characters
		for(int index = 0; index < s.length(); index++){
			for(int charCode = (int)'a'; charCode <= (int)'z'; charCode++) {
				// use StringBuffer for an easy interface to permuting the 
				// letters in the String
				StringBuffer sb = new StringBuffer(s);
				sb.setCharAt(index, (char)charCode);

				// if the item isn't in the list, isn't the original string, and
				// (if wordsOnly is true) is a real word, add to the list
				if(!currentList.contains(sb.toString()) && 
						(!wordsOnly||dict.isWord(sb.toString())) &&
						!s.equals(sb.toString())) {
					currentList.add(sb.toString());
				}
			}
		}
	}
	
	/** Add to the currentList Strings that are one character insertion away
	 * from the input string.  
	 * @param s The original String
	 * @param currentList is the list of words to append modified words 
	 * @param wordsOnly controls whether to return only words or any String
	 * @return
	 */
	public void insertions(String s, List<String> currentList, boolean wordsOnly ) {
		for(int index = 0; index <= s.length(); index++){
			for(int charCode = (int)'a'; charCode <= (int)'z'; charCode++) {
				// use StringBuffer for an easy interface to permuting the 
				// letters in the String
				StringBuffer sb = new StringBuffer(s);
				sb.insert(index, (char)charCode);

				// if the item isn't in the list, isn't the original string, and
				// (if wordsOnly is true) is a real word, add to the list
				if(!currentList.contains(sb.toString()) && 
						(!wordsOnly||dict.isWord(sb.toString())) &&
						!s.equals(sb.toString())) {
					currentList.add(sb.toString());
				}
			}
		}
	}

	/** Add to the currentList Strings that are one character deletion away
	 * from the input string.  
	 * @param s The original String
	 * @param currentList is the list of words to append modified words 
	 * @param wordsOnly controls whether to return only words or any String
	 * @return
	 */
	public void deletions(String s, List<String> currentList, boolean wordsOnly ) {
		for(int index = 0; index < s.length(); index++){
			StringBuffer sb = new StringBuffer(s);
			sb.deleteCharAt(index);

			// if the item isn't in the list, isn't the original string, and
			// (if wordsOnly is true) is a real word, add to the list
			if(!currentList.contains(sb.toString()) && (!wordsOnly||dict.isWord(sb.toString())) &&
					!s.equals(sb.toString())) {
				currentList.add(sb.toString());
			}
		}
	}

	/** Return suggested spellings for the input word, ordered starting at minimum number of 
	 * character changes. 
	 * @param word The misspelled word
	 * @param numSuggestions is the maximum number of suggestions to return 
	 * @return the list of spelling suggestions
	 */
	@Override
	public List<String> suggestions(String word, int numSuggestions) {

		// initial variables
		LinkedList<String> queue = new LinkedList<String>();     // Strings to explore
		HashSet<String> visited = new HashSet<String>();   // To avoid re-exploring strings 
		List<String> retList = new LinkedList<String>();   // Words to return
		List<String> mutations = new LinkedList<String>(); // Words that differ by single character
					
		// Level order search through mutations of the original word, 
		// with level number corresponding to number of mutations.
		String w = word;
		int count = 0;
		while(w != null && count < numSuggestions) {
			visited.add(w);
			
			if(dict.isWord(w)) {
				retList.add(w);
				count++;
			}
			
			// apply single character transformations
			subsitution(w, mutations, true);
			insertions(w, mutations, true);
			deletions(w, mutations, true);
			
			// add mutations to search queue, in not already determined to be a valid suggestion.
			for(String mutation: mutations) if(!retList.contains(mutation)) queue.add(mutation);
			
			// Get next valid spelling.
			w = queue.poll();		
		}
		return retList;
	}	

   public static void main(String[] args) {
	   /*
	   String word = "i";
	   // Pass NearbyWords any Dictionary implementation you prefer
	   Dictionary d = new DictionaryHashSet();
	   DictionaryLoader.loadDictionary(d, "data/dict.txt");
	   NearbyWords w = new NearbyWords(d);
	   List<String> l = w.distanceOne(word, true);
	   System.out.println("One away word Strings for for \""+word+"\" are:");
	   System.out.println(l+"\n");

	   word = "tailo";
	   List<String> suggest = w.suggestions(word, 10);
	   System.out.println("Spelling Suggestions for \""+word+"\" are:");
	   System.out.println(suggest);
	   */
	   	   
	   Dictionary d = new DictionaryHashSet();
       DictionaryLoader.loadDictionary(d, "data/dict2.txt");
       NearbyWords nw = new NearbyWords(d);
	   
       String word = "dag";
       List<String> suggest = nw.suggestions(word, 4);
       System.out.println("Spelling Suggestions for \""+word+"\" are:");
	   System.out.println(suggest);
	   
	   word = "fare";
       suggest = nw.suggestions(word, 3);
       System.out.println("Spelling Suggestions for \""+word+"\" are:");
	   System.out.println(suggest);
   }

}
