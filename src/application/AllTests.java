package application;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * JUnit test suite to run all tests
 */
@RunWith(Suite.class)
@SuiteClasses({ TestHashMap.class, TestReadFile.class, TestDatabase.class })
public class AllTests {

}
