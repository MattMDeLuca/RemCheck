package remCheckUI;


import java.io.IOException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;
import remCheckProcessing.AlfrescoConnector;
import remCheckProcessing.ExportParser;

public class RemCheckMainScreenController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private TextField exportURLTextField;
    
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button mainScreenOKButton;
    
    public static AlfrescoConnector alfrescoConnection;
    
    
    
    @FXML
    void mainScreenOKButtonPress(ActionEvent event) throws IOException {
    	
    	
    		if (exportURLTextField.getText().isEmpty() == false) { //don't forget to check here for malformed url
    			
        		Dialog<Pair<String, String>> dialog = new Dialog<>();
        		dialog.setTitle("Log Into Alfresco");
        		
        		ButtonType okLoginButton = new ButtonType("OK", ButtonData.OK_DONE);
        		
        		dialog.getDialogPane().getButtonTypes().addAll(okLoginButton, ButtonType.CANCEL);
        		GridPane textFieldGridPane = new GridPane();
        		textFieldGridPane.setHgap(10);
        		textFieldGridPane.setVgap(10);
        		
        		Label dialogLabel = new Label();
        		dialogLabel.setText("Please enter your Alfresco login information.");
        		
        		TextField userName = new TextField();
        		userName.setPromptText("Enter U42");
        		PasswordField password = new PasswordField();
        		password.setPromptText("Enter password");
        		
        		textFieldGridPane.add(dialogLabel, 0, 0);
        		textFieldGridPane.add(userName, 0, 1);
        		textFieldGridPane.add(password, 0, 2);
        		
        		dialog.getDialogPane().setContent(textFieldGridPane);
        		
        		dialog.setResultConverter(dialogButton -> {
        			if (dialogButton == okLoginButton) {
        				return new Pair<> (userName.getText(), password.getText());
        				
        			}
        			
        			return null;
        		});
        		
        		Optional<Pair<String, String>> result = dialog.showAndWait();
        		
        		if (result != null) {
        			
        			alfrescoConnection  = new AlfrescoConnector();
        			alfrescoConnection.setAuthInformation(result.get());
        			alfrescoConnection.retrieveExportCSV(exportURLTextField.getText());
        			
        			
        			if (alfrescoConnection.getExportURL() == null) {
            			Alert retrievingExportInformationDialog = new Alert(AlertType.INFORMATION);
            			retrievingExportInformationDialog.setTitle("Cannot Retrieve Export");
            			retrievingExportInformationDialog.setHeaderText(null);
            			retrievingExportInformationDialog.setContentText("There is an issue retrieving your export. Please check your URL or U42 and try again.");
            			retrievingExportInformationDialog.showAndWait();
        			}

        			else {
        				
            			Parent root = FXMLLoader.load(getClass().getResource("exportResults.fxml"));
        				Scene scene = new Scene(root);
        				Stage primaryStage = RemCheckMainProg.getMainStage();
        				primaryStage.setScene(scene);
        				primaryStage.centerOnScreen();
        				primaryStage.show();
        				
        			}

       
        			
        			//ExportParser exportParser = new ExportParser(exportURLTextField.getText(), result.get().getKey(), result.get().getValue());
        			
        			//Alert retrievingExportInformationDialog = new Alert(AlertType.INFORMATION);
        			//retrievingExportInformationDialog.setTitle("Retrieving Export Information");
        			//retrievingExportInformationDialog.setHeaderText(null);
        			//retrievingExportInformationDialog.setContentText("Export information is being retrieved from Alfresco.");
        			//retrievingExportInformationDialog.showAndWait();
       
        		}
        		
        		System.out.println(result); //returns a key-value pair with the u/n as the key.
    			
    		}
    		
    		
    		else {
    			Alert emptyUrlDialog = new Alert(AlertType.INFORMATION);
    			emptyUrlDialog.setTitle("Alfresco Export URL Missing");
    			emptyUrlDialog.setHeaderText(null);
    			emptyUrlDialog.setContentText("Please enter an Alfresco export URL.");
    			emptyUrlDialog.showAndWait();
    		}   		

    }
    
}




