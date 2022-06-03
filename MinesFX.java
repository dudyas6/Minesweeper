package mines;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MinesFX extends Application {
	// class implements Application provide us the ability to show things on screen
	@Override
	public void start(Stage stage) {
		//create main board and send it to the controller.
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("MinesFXML.fxml"));
			HBox box = loader.load();
			GameController controller = loader.getController();
			stage.setTitle("Minesweeper");
			Scene scene = new Scene(box);
			stage.setScene(scene);
			stage.getIcons().add(new Image("/mines/imgs/icon_bomb.png"));
			controller.setController(box, stage);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public static void main(String[] args) {
		launch(args);   	
	}
}
