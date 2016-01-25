package spelling;

import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/** 
 * An trie data structure that implements the Dictionary and the AutoComplete ADT
 * @author Karen Blakemore
 *
 */
public class AutoCompleteDictionaryTrie implements  Dictionary, AutoComplete {

    private TrieNode root;
    private int size;
    

    public AutoCompleteDictionaryTrie()
	{
		root = new TrieNode();
	}
    
	/** Insert a word into the trie, converting it to lower case, first.
	 * @return true if the word was added to the dictionary 
	 * (it wasn't already there).
	 */
	public boolean addWord(String word)
	{
		String lcWord = word.toLowerCase();
		TrieNode node = root;
		int index = 0;
		char c;
		
		// Search trie one char at a time, until char not found or end of word reached.
		while(index < lcWord.length()) {
			c = lcWord.charAt(index);
			
			// No more chars from word in trie.
			if(!node.getValidNextCharacters().contains(c)) break;
			
			node = node.getChild(c);
			index++;
		}
		
		if(index == word.length() && node.endsWord()) return false; 	// word already in dictionary
		
		// Insert remaining chars into trie
		for(int i=index; i < lcWord.length(); i++) {
			c = lcWord.charAt(i);
			node = node.insert(c);
		}
		node.setEndsWord(true);
		
		return true;
	}
	
	// recursive function to count words in subtrie.
	private int countSubTrie(TrieNode node) {
		int count = node.endsWord() ? 1 : 0;
		
		for(char c: node.getValidNextCharacters()) {
			count += countSubTrie(node.getChild(c));
		}
		return count;
	}
	
	/** 
	 * Return the number of words in the dictionary.
	 */
	public int size()
	{				
	    return countSubTrie(root);
	}
	
	
	/** Returns whether the string is a word in the trie */
	@Override
	public boolean isWord(String s) 
	{
		String word = s.toLowerCase();
		TrieNode node = root;
		int index = 0;
		char c;
		
		// Search trie one char at a time, until char not found or end of word reached.
		while(index < word.length()) {
			c = word.charAt(index);
			
			// No more chars from word in trie
			if(!node.getValidNextCharacters().contains(c)) break;
				
			node = node.getChild(c);
			index++;
		}
		
		if(index == word.length() && node.endsWord()) return true;
		else return false;
	}
	
	

	/** 
	 * Returns up to the n "best" predictions, including the word itself,
     * in terms of length
     * If this string is not in the trie, it returns null.
     * @param text The text to use at the word stem
     * @param n The maximum number of predictions desired.
     * @return A list containing the up to n best predictions
     */@Override
     public List<String> predictCompletions(String prefix, int numCompletions) 
     { 
    	String lcPrefix = prefix.toLowerCase();
 		TrieNode node = root;
 		int index = 0;
 		char c;
 		
 		// Search trie for prefix, until char not found or end of word reached.
 		while(index < lcPrefix.length()) {
 			c = lcPrefix.charAt(index);
 			
 			// Char not in trie
 			if(!node.getValidNextCharacters().contains(c)) break;				
 			
 			node = node.getChild(c);	
 			index++;
 		} 
 		
 		if(index != lcPrefix.length()) return new LinkedList<String>();
 		
 		// Found prefix, now create a list of possible words containing the prefix.
 		LinkedList<TrieNode> queue = new LinkedList<TrieNode>();	// queue for bfs search
 		List<String> words = new LinkedList<String>();				// list of completed words
 		int count = 0;
 		
 		while(node != null && count < numCompletions) {	// Traverse subtree in level-order to find completions
 			if(node.endsWord()) {
 				words.add(node.getText());
 				count++;
 			}
 			
			for(char ch: node.getValidNextCharacters()) {
				queue.add(node.getChild(ch));	
			}
			node = queue.poll();
 		}
		return words;
     }

 	// For debugging
 	public void printTree()
 	{
 		printNode(root);
 	}
 	
 	/** Do a pre-order traversal from this node down */
 	public void printNode(TrieNode curr)
 	{
 		if (curr == null) 
 			return;
 		
 		System.out.println(curr.getText());
 		
 		TrieNode next = null;
 		for (Character c : curr.getValidNextCharacters()) {
 			next = curr.getChild(c);
 			printNode(next);
 		}
 	}
 	

	
}