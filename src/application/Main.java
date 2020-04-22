/**
 *  Assignment 9 adding JavaDoc to the word occurrences GUI application 
 */
package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 *  Main JavaFX application
 */
public class Main extends Application {
	
	/**
	 * Start the JavaFX GUI
	 * @param primaryStage JavaFX initial stage
	 */
	@Override
	public void start(Stage primaryStage) {
		try {			
	        Parent root = FXMLLoader.load(getClass().getResource("application.fxml"));
	        primaryStage.setTitle("Word Occurrences Calculator");
	        primaryStage.setScene(new Scene(root));	        
	        primaryStage.setResizable(false);  
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main entry point for our application
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
