package application;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

/**
 * JUnit Test: Read File
 * 
 * Using a pre-defined text file, the WordOccurrences class will be populated from the contents 
 * of the file and ensure that the expected word occurrences count is returned.   	 
 */
public class TestReadFile {

	@Test
	public void test() {
		WordOccurrences test = new WordOccurrences();		
		
		LinkedHashMap<String, Integer> expectedMap = new LinkedHashMap<>();				
		expectedMap.put("witch", 4);
		expectedMap.put("when", 3);
		expectedMap.put("the", 3);		
		expectedMap.put("thunder", 2);
		expectedMap.put("three", 2);

		// Take our the keys from the LinkedHashMap (which maintains order) and save it to an array that we can iterate by index
		ArrayList<String> expectedKeys = new ArrayList<>();
		for (Entry<String, Integer> word : expectedMap.entrySet()) {			
			expectedKeys.add(word.getKey());			
		}			
		
		HashMap<String, Integer> map = new HashMap<>();		
		assertTrue(test.readFile("tests/filetest.txt", map));
		
		Map<String, Integer> sortedMap = test.getTopOccurrences(map, map.size());
		
		int i = 0;
		for (Entry<String, Integer> word : sortedMap.entrySet()) {
			assertEquals(word.getKey(), expectedKeys.get(i));
			assertEquals(word.getValue(), expectedMap.get(expectedKeys.get(i)));
			if (++i > 4) {
				// Just check the first 5 elements
				break;
			}
		}
	}

}
