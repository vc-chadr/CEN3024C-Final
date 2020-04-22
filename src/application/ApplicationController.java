package application;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Application Controller for all GUI events and actions
 */
public class ApplicationController {
	/**
	 * Default number of top words to count
	 */
	private final Integer NUMBER_OF_TOP_WORDS = 20;
	
	/**
	 * Use database instead of HashMap to count word occurrences
	 */	
	private final Boolean USE_DATABASE = true;
	
	/**
	 * Default input filename
	 */	
	private final String DEFAULT_FILENAME = "macbeth.txt";
	
	public TextField textFilename;
	public ListView<String> wordListSummary;
	public ObservableList<String> wordListView = FXCollections.observableArrayList();
	public BarChart<Number, String> wordBarChart;
	public HBox wordDisplayContainer;
	
	private WordOccurrences analyzer;
	private WordOccurrencesDB analyzerDB;
	private HashMap<String, Integer> wordMap;

	/**
	 * Initialize GUI and core variables 
	 */
    @FXML
    void initialize() {
    	analyzer = new WordOccurrences();
    	analyzerDB = new WordOccurrencesDB();
    	wordMap = new HashMap<>();
    	
    	textFilename.setText(DEFAULT_FILENAME);
    	clearWordCount();
    	
    	// Construct and add the BarChart
    	NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        xAxis.setTickLabelRotation(90);
        yAxis.setAnimated(false);	// Workaround for animation not displaying yAxis labels when first calculated
        wordBarChart = new BarChart<Number,String>(xAxis, yAxis);
        wordBarChart.setBarGap(0.5);
        wordBarChart.setCategoryGap(1.0);        
        wordDisplayContainer.getChildren().add(wordBarChart);
    }

	/**
	 * Clear the word count GUI list 
	 */
    public void clearWordCount() {
    	wordListView.clear();
    	wordListView.add("");
    	wordListSummary.setItems(wordListView); 
    }
    
	/**
	 * Open a file browser and select a file
	 * @param actionEvent JavaFX action event (not currently utilized) 
	 */    
    public void clickBrowse(ActionEvent actionEvent) {
    	Stage stage = (Stage) wordListSummary.getScene().getWindow();    	
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open input File");
    	File file = fileChooser.showOpenDialog(stage);
    	if (file != null) {    	
    		textFilename.setText(file.getAbsolutePath());
    	}
    }

	/**
	 * Load and calculate the word occurrences
	 * @param actionEvent JavaFX action event (not currently utilized) 
	 */ 
    public void clickGo(ActionEvent actionEvent) {      	
		long startTime = System.currentTimeMillis();
	   	   
    	if (USE_DATABASE) {    		
    		if (analyzerDB.connect()) {
    			System.out.println("Connected to MySQL server");
    			
    			if (analyzerDB.readFile(textFilename.getText())) {
    				// Successfully read a file, calculate and display the output
    				Map<String, Integer> sortedMap = analyzerDB.getTopOccurrences(NUMBER_OF_TOP_WORDS);
    	    		System.out.println("\nTop " + NUMBER_OF_TOP_WORDS + " Words From Database\n------------");
    	    		displayWords(sortedMap);    				
    			} else {
    				popupErrorMessage("Error opening file", "Error opening input file: " + textFilename.getText());
    			}
    			analyzerDB.close();				
    		} else {
    			popupErrorMessage("Database error", "Error connecting to MySQL server");
    		}    		
    	} else {
    		wordMap.clear();
	    	if (analyzer.readFile(textFilename.getText(), wordMap)) {
	    		// Successfully read a file, calculate and display the output
	    		Map<String, Integer> sortedMap = analyzer.getTopOccurrences(wordMap, NUMBER_OF_TOP_WORDS);		
	    		System.out.println("\nTop " + NUMBER_OF_TOP_WORDS + " Words\n------------");
	    		displayWords(sortedMap);
			} else {
				popupErrorMessage("Error opening file", "Error opening input file: " + textFilename.getText());
			}
    	}
    	
    	System.out.printf("\nElapsed time = %.2f seconds\n", (System.currentTimeMillis() - startTime) / 1000.0f);    	
    }
    
	/**
	 * Display the wordmap in the word count list and bar chart
	 * @param map wordmap that will be displayed 
	 */     
	public void displayWords(Map<String, Integer> map) {
        // Clear previous display
		wordBarChart.getData().clear();
		wordListView.clear();		
		XYChart.Series<Number, String> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Word Count");
        
		for (Entry<String, Integer> word : map.entrySet()) {
			String output = word.getValue() + " - " + word.getKey();
			System.out.println(output);
			
			// Update List View
	        wordListView.add(output);        
	        wordListSummary.setItems(wordListView);
	        
	        // Update bar chart
	        dataSeries.getData().add(new XYChart.Data<Number, String>(word.getValue(), word.getKey()));
		}
		
		wordBarChart.getData().add(dataSeries);
	}
	

	/**
	 * Popup a dialog window with an error message
	 * @param title text to be displayed in the dialog's title
	 * @param content text to be displayed in the body of the dialog window 
	 */ 	
	public void popupErrorMessage(String title, String content) {
    	Alert popup = new Alert(AlertType.ERROR);
    	popup.setTitle(title);
    	popup.setHeaderText(null);
    	popup.setContentText(content);
    	popup.showAndWait();   
	}
}
