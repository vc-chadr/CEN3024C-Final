package application;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;


/**
 * WordOccurrencesDB class to populate a database with words and their occurrence count.
 * 
 * Database Table created with the following command
 * <pre>
 * CREATE TABLE `word_occurrences`.`word_count` (
 * `word` VARCHAR(32) NOT NULL,
 * `count` INT NULL,
 *  PRIMARY KEY (`word`));
 * </pre>
 */
public class WordOccurrencesDB {

	/**
	 * Default database driver 
	 */		
	private static final String dbDriver = "jdbc:mysql";
	
	/**
	 * Default database path with host information
	 */	
	private static final String dbPath = "//localhost:3306";
	
	/**
	 * Default database user name
	 */	
	private static final String dbUser = "USER-SAMPLE";
	
	/**
	 * Default database user password
	 */		
	private static final String dbPassword = "PASSWORD-SAMPLE";

	/**
	 * Database URL initialized in constructor
	 */
	private String url = null;
	
	/**
	 * Database connection
	 */	
	private Connection conn = null;

	/*
	 * Initializes the WordOccurrencesDB class
	 */
	public WordOccurrencesDB() {
		url = dbDriver + ":" + dbPath;
	}

	/*
	 * Connect connect's to the database and will create it if it does not exist.
	 * @return returns true if a successfully connection was made
	 */
	public Boolean connect() {
		System.out.println("Connecting to database");
		try {
			conn = DriverManager.getConnection(url, dbUser, dbPassword);
			if (conn != null) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Database Error: " + e.getMessage());
		}

		return false;
	}

	/*
	 * Close closes our database connection
	 */
	public void close() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}	
	
	/**
	 * Counts the Word Occurrences in a the database and returns a sorted map with the top numElements.
	 * @param numElements top x number of elements to return in our map
	 * @return returns a sorted map
	 */
    public Map<String, Integer> getTopOccurrences(Integer numElements) {
    	Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
    			
		try {									
			if (conn != null) {								
				String sql = "SELECT word, count FROM word_occurrences.word_count ORDER BY count DESC LIMIT ?";
				PreparedStatement pstatement = conn.prepareStatement(sql);
				pstatement.setInt(1, numElements);
				ResultSet rs = pstatement.executeQuery();		
				
				while (rs.next() ) {
					sortedMap.put(rs.getString("word"), rs.getInt("count"));
				}
				
				rs.close();
				pstatement.close();								
			} else {
				System.out.println("Database connection is invalid");
			}					
		} catch (SQLException e) {
			System.out.println("Database Error: " + e.getMessage());
		}			
		
		
		return sortedMap;
	}

	/**
	 * Reads a text file and populates a database. The records are comprised of words as the key and the occurrences 
	 * of the word count as the value. 
	 * @param filename text file to process
	 * @return returns true if the file read operation was successful
	 */
	public boolean readFile(String filename) {
        // Open input file and process words until done
		System.out.println("Reading from file and adding words to database");
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
            	
        		try {									
        			if (conn != null) {								
        				String sql = "SELECT word, count FROM word_occurrences.word_count WHERE word = ?";
        				PreparedStatement pstatement = conn.prepareStatement(sql);
        				pstatement.setString(1, word);
        				ResultSet rs = pstatement.executeQuery();
        				
        				if (rs.next() == false) {
        					// the word was not found in database, add it
        					String sql2 = "INSERT INTO word_occurrences.word_count(word, count) VALUES(?, 1)";
        					PreparedStatement pstatement2 = conn.prepareStatement(sql2);
        					pstatement2.setString(1, word);
        					pstatement2.execute();
        					pstatement2.close();
        				} else {
        					// the word was not found in database, increment count
        					int count = rs.getInt("count");
        					String sql2 = "UPDATE word_occurrences.word_count SET count=? WHERE word=?";
        					PreparedStatement pstatement2 = conn.prepareStatement(sql2);
        					pstatement2.setInt(1, count + 1);
        					pstatement2.setString(2, word);        					
        					pstatement2.execute();
        					pstatement2.close();
        				}
        								
        				pstatement.close();
        				
        			} else {
        				System.out.println("Database connection is invalid");
        			}					
        		} catch (SQLException e) {
        			System.out.println("Database Error: " + e.getMessage());
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
