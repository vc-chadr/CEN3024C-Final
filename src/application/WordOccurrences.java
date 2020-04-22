package application;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * WordOccurrences class to populate word maps from a file and sort the words
 */
public class WordOccurrences {
	
	/**
	 * Gets the top Word Occurrences in a supplied HashMap and returns a sorted map with the top numElements.
	 * @param map input word map to process
	 * @param numElements top x number of elements to return in our map
	 * @return returns a sorted map
	 */
    public Map<String, Integer> getTopOccurrences(HashMap<String, Integer> map, Integer numElements) {
		// Reverse sort the map by value and limit the output to the desired number of elements
		Map<String, Integer> sortedMap = map.entrySet().stream()
			    .sorted(Entry.<String, Integer>comparingByValue().reversed())
			    .limit(numElements)
			    .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		return sortedMap;
	}

	/**
	 * Reads a text file and populates a HashMap. The HashMap is comprised of words as the key and the occurrences 
	 * of the word count as the value. 
	 * @param filename text file to process
	 * @param map HashMap to store the words and word counts
	 * @return returns true if the file read operation was successful
	 */
	public boolean readFile(String filename, HashMap<String, Integer> map) {
        // Open input file and process words until done
		System.out.println("Reading from file and adding words to HashMap");
        try {
            Scanner sc = new Scanner(new File(filename));
            
            // Grab one string at a time
            while (sc.hasNext()) {
            	String word = sc.next();
            	word = word.replaceAll("\\p{P}", "");  	// Use RegEx to Replace all punctuation characters
            	word = word.toLowerCase();				// force all words to lower case

            	if (word.length() == 0) {				// skip blank words
            		continue;
            	}
            	
            	if (map.containsKey(word)) {
            		map.put(word, map.get(word) + 1);	// Increment word hash by one
            	} else {
            		map.put(word, 1);					// Add a newly discovered word
            	}            	      
            }
            sc.close();

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("ERROR: Unable to open file '" + filename + "', please try another filename");
            return false;
        }
        
        return true;
	}   
}
