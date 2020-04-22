package application;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * JUnit Test: Database 
 * 
 * Verifies that our program is able to connect to the MySQL database.
 */
public class TestDatabase {

	@Test
	public void test() {
		WordOccurrencesDB db = new WordOccurrencesDB();
		assertTrue(db.connect());
		db.close();				
	}

}
