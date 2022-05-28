package mines;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MinesFX extends Application {

	@Override
	public void start(Stage stage) {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("MinesFXML.fxml"));
			HBox box = loader.load();
			GameController controller = loader.getController();
			stage.setTitle("Minesweeper");
			Scene scene = new Scene(box);
			stage.setScene(scene);
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
