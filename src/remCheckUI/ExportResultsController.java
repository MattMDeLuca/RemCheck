package remCheckUI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import remCheckProcessing.ExportParser;

public class ExportResultsController {

    @FXML
    private CheckBox selectAllCheckBox;


    @FXML
    private Button exportResultsOKButton;
    
    @FXML
    private Pane topPane;
    
    
    @FXML
    private AnchorPane exportResultsAnchorPane;
    
    private ArrayList<CheckBox> assessmentTestCheckBoxList = new ArrayList();

    @FXML
    void exportResultsOKButtonClick(ActionEvent event) {
    	//This is prompt the program to check the QTI question data. When it's finished, it should result in a popup
    	//that allows the user to save the file containing the remediation information OR if everything is OK, it should
    	//result in a popup that says so. Once the user has responded to those popups, add another that asks if they want
    	//to save the information about each question in the assessments.
    	
    	Map<String, Map<String, String>> finalAssessment = ExportParser.getAssessmentObjsWithRem();
    	
    	int cbFalseCount = 0;
    	int cbTrueCount = 0;
    	
    	for (CheckBox c: assessmentTestCheckBoxList) {
    		
    		if (c.selectedProperty().getValue() == true) {
    			cbTrueCount +=1;
    			
    		}
    		
    		if (c.selectedProperty().getValue() == false) {
    			cbFalseCount +=1;
    			finalAssessment.remove(c.getId(),finalAssessment.get(c.getId()));
    			
    		}
    		
    	}
    	
    	System.out.println(cbTrueCount);
    	System.out.println(finalAssessment.size());
    	System.out.println(cbFalseCount);
    	
    	Alert lookingForAssessmentAlert = new Alert(AlertType.INFORMATION);
    	lookingForAssessmentAlert.setTitle("Parsing QTI Packages");
    	lookingForAssessmentAlert.setHeaderText(null);
    	lookingForAssessmentAlert.setContentText("Finding and reviewing assessment");
    	lookingForAssessmentAlert.showAndWait();
    	

    }
    
    @FXML
    void selectAllCheckBoxAction(ActionEvent event) {
    	if (selectAllCheckBox.selectedProperty().getValue() == true) {
    		for (CheckBox c: assessmentTestCheckBoxList) {
    			c.setSelected(true);
    		}
    				
    	}
    	
    	if (selectAllCheckBox.selectedProperty().getValue() == false) {
    		for (CheckBox c: assessmentTestCheckBoxList) {
    			c.setSelected(false);
    		}
    	}

    }

    
    
    @FXML
    public void initialize() throws FileNotFoundException, IOException {
    	
    	
    	GridPane assessmentTestGridPane = new GridPane();
    	//assessmentTestGridPane.setBackground(new Background(new BackgroundFill(Color.BEIGE, CornerRadii.EMPTY, Insets.EMPTY)));
    	assessmentTestGridPane.setHgap(10);
    	assessmentTestGridPane.setVgap(10);
    	assessmentTestGridPane.maxWidth(Double.MAX_VALUE);
    	assessmentTestGridPane.maxHeight(Double.MAX_VALUE);
    	exportResultsAnchorPane.setTopAnchor(assessmentTestGridPane, 50.0);
    	exportResultsAnchorPane.setLeftAnchor(assessmentTestGridPane, 5.0);
    	exportResultsAnchorPane.setRightAnchor(assessmentTestGridPane, 5.0);
    	exportResultsAnchorPane.setBottomAnchor(assessmentTestGridPane, 5.0);
    	exportResultsAnchorPane.getChildren().add(assessmentTestGridPane);

    	
    	
    	
    	ExportParser exportParser = new ExportParser(RemCheckMainScreenController.alfrescoConnection.retrieveExportInputStream());
    	
    	int assessmentCount = 0; 
    	int columnCount = 0;
    	int secondColumnCount = 1;
    	int assessmentRowCount = 0;
    	
    	System.out.println(exportParser.getAssessmentObjsWithRem().size());
    	
    	for (Map<String, Pair<Integer, Integer>> assessment: exportParser.assessmentHierarchy) {//use the hierarchy created in the export parser to display the assessment somewhat sequentially.
    		
    		Map<String, String> assessmentEntry = exportParser.getAssessmentObjsWithRem().get(assessment.keySet().toString().replace("[", "").replace("]", ""));
    		
    		Label assessmentTestLabel = new Label();
    		assessmentTestLabel.setText(assessmentEntry.get("Title In Sequence"));
    		
    		CheckBox assessmentTestCheckBox = new CheckBox();
    		assessmentTestCheckBox.setId(assessmentEntry.get("External ID"));
    		assessmentTestCheckBoxList.add(assessmentTestCheckBox);
    		
    		if (assessmentCount % 25 == 0 && assessmentCount > 0) {
    			columnCount +=2;
    			secondColumnCount +=2;
    			assessmentRowCount = 0;
    		}
    		
    		
        	assessmentTestGridPane.add(assessmentTestCheckBox, columnCount, assessmentRowCount);
        	assessmentTestGridPane.setHgrow(assessmentTestCheckBox, Priority.ALWAYS);
        	assessmentTestGridPane.setVgrow(assessmentTestCheckBox, Priority.ALWAYS);
        	
        	assessmentTestGridPane.add(assessmentTestLabel, secondColumnCount, assessmentRowCount);
        	assessmentTestGridPane.setHgrow(assessmentTestLabel, Priority.ALWAYS);
        	assessmentTestGridPane.setVgrow(assessmentTestLabel, Priority.ALWAYS);
        	
        	assessmentCount+=1;
        	assessmentRowCount +=1;
    		 		
    	}
    	
    	selectAllCheckBox.setOnAction(this::selectAllCheckBoxAction);

    	
    }
   

}
