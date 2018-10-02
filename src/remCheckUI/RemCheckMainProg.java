

package remCheckUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RemCheckMainProg extends Application {
	
	private static Stage mainStage;

	public static void main(String[] args) {
		launch(args);

	}
	
	
	@Override
	public void start (Stage primaryStage) throws Exception {
		
		setMainStage(primaryStage);

		Parent root = FXMLLoader.load(getClass().getResource("remCheckMainScreen.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Rem Thing");
		Platform.setImplicitExit(true);
		primaryStage.setOnCloseRequest((ae) -> {
			Platform.exit();
			System.exit(0);
		});
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

	
	private void setMainStage (Stage mainStage) {
		this.mainStage = mainStage;
	}
	
	public static Stage getMainStage() {
		return mainStage;
	}
}


