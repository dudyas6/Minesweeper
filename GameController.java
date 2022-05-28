package mines;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GameController {
	private Mines boardInfo;
	private infoButton[][] buttonsInfo;
	private int height, width, mines;
	private HBox box;
	private Stage stage;
	boolean firstRun = true, labelsVisible = true;

	public void setController(HBox box, Stage stage) {
		this.box = box;
		this.stage = stage;
		setBox();
		setDefaultValues();
		initBoard();
	}

	@FXML
	private ComboBox<String> choiceBox;


    @FXML
    private Label heightText;

    @FXML
    private TextField heightLabel;

    @FXML
    private Label minesText;

    @FXML
    private TextField minesLabel;

    @FXML
    private Button resetButton;

    @FXML
    private Label widthText;

    @FXML
    private TextField widthLabel;

	@FXML
	void setBoard(MouseEvent event) {
		if(labelsVisible)
			getValues();
		initBoard();
	}

	@FXML
	void changeMode(ActionEvent event) {
		Object selectedItem = choiceBox.getSelectionModel().getSelectedItem();
		if (selectedItem.toString().equals("Custom")) {
			changeLabels(true);

		} else if (selectedItem.toString().equals("Easy")) {
			changeLabels(false);
			height = 8; width = 10; mines = 13;
			
		} else if (selectedItem.toString().equals("Moderate")) {
			changeLabels(false);
			height = 13; width = 15; mines = 33;

		} else {
			changeLabels(false);
			height = 18; width = 20; mines = 50;
		}
	}

	private void changeLabels(Boolean visible) {
		heightText.setVisible(visible);
		widthText.setVisible(visible);
		minesText.setVisible(visible);
		heightLabel.setVisible(visible);
		widthLabel.setVisible(visible);
		minesLabel.setVisible(visible);
		labelsVisible = visible;

	}

	private void initBoard() {
		GridPane grid = new GridPane();

		addConstrains(grid);
		setGrid(grid);

		if (!firstRun)
			box.getChildren().remove(box.getChildren().size() - 1);
		box.getChildren().add(grid);
		box.autosize();
		stage.sizeToScene();
		firstRun = false;
	}

	private void addConstrains(GridPane grid) {
		for (int i = 0; i < width; i++)
			grid.getColumnConstraints().add(new ColumnConstraints(40));
		for (int j = 0; j < height; j++)
			grid.getRowConstraints().add(new RowConstraints(40));
	}

	private void setGrid(GridPane grid) {
		boardInfo = new Mines(height, width, mines);
		buttonsInfo = new infoButton[height][width];
		int i, j;
		for (i = 0; i < height; i++)
			for (j = 0; j < width; j++) {
				buttonsInfo[i][j] = new infoButton(i, j);
				buttonsInfo[i][j].setText(boardInfo.get(i, j));
				buttonsInfo[i][j].setFont(Font.font(null, FontWeight.BOLD, 14));
				// buttonsInfo[i][j].setStyle(); add style if required to buttons fresh buttons
				buttonsInfo[i][j].setOnMouseClicked(new Reveal());
				buttonsInfo[i][j].setMaxHeight(Double.MAX_VALUE);
				buttonsInfo[i][j].setMaxWidth(Double.MAX_VALUE);
				grid.add(buttonsInfo[i][j], j, i);
			}
	}

	class Reveal implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			int i = ((infoButton) event.getSource()).getX();
			int j = ((infoButton) event.getSource()).getY();
			if (event.getButton() == MouseButton.PRIMARY) {
				boolean isntMine;
				isntMine = boardInfo.open(i, j);
				updateBoard();
				if (!isntMine || boardInfo.isDone()) {
					boardInfo.setShowAll(true);
					updateBoard();
					endMsg(!isntMine);
				}
			}

			else if (event.getButton() == MouseButton.SECONDARY) {
				boardInfo.toggleFlag(i, j);
				updateBoard();
			}
			return;
		}

	}

	private void setBox() {
		ObservableList<String> availableChoices = FXCollections.observableArrayList("Custom", "Easy", "Moderate",
				"Hard");
		choiceBox.setItems(availableChoices);
		choiceBox.getSelectionModel().select("Custom");
	}

	private void endMsg(boolean clickedMine) {
		HBox endBox = new HBox();
		Label endLabel = new Label("Some Text");
		endLabel.setFont(Font.font(null, FontWeight.BOLD, 20));
		if (clickedMine) {
			endLabel.setText("You Lose! ¯\\_( ╥ ₃ ╥ )_/¯ ");
		} else {
			endLabel.setText("You Win! (👍 ͡🔥 ͜ʖ ͡🔥)👍 ");
		}
		endBox.setAlignment(Pos.CENTER);
		endBox.getChildren().add(endLabel);
		Scene endScene = new Scene(endBox, 300, 100);
		Stage endStage = new Stage();
		endStage.setTitle("Its over");
		endStage.setScene(endScene);
		endStage.show();
	}

	private void updateBoard() {
		int i, j;
		for (i = 0; i < height; i++) {
			for (j = 0; j < width; j++) {

				infoButton curButton = buttonsInfo[i][j];
				curButton.setText(boardInfo.get(i, j));
				if (curButton.isFlag) {
					curButton.setGraphic(null);
				}
				if (curButton.getText() == "F") {
					curButton.setText("");
					Image img = new Image("/mines/imgs/flag.png");
					ImageView view = new ImageView(img);
					view.setFitHeight(25);
					view.setPreserveRatio(true);
					curButton.setGraphic(view);
					curButton.isFlag = true;
				}

				if (curButton.getText() == "X") {
					curButton.setText("");
					curButton.setStyle("-fx-background-color: red");
					Image img = new Image("/mines/imgs/bomb.png");
					ImageView view = new ImageView(img);
					view.setFitHeight(25);
					view.setPreserveRatio(true);
					curButton.setGraphic(view);
				}

			}
		}
	}

	private void setDefaultValues() {
		height = 10;
		width = 10;
		mines = 5;
	}

	private void getValues() {
		height = Integer.parseInt(heightLabel.getText());
		width = Integer.parseInt(widthLabel.getText());
		mines = Integer.parseInt(minesLabel.getText());
		return;
	}

	private class infoButton extends Button {
		private int x, y;
		private boolean isFlag;

		public infoButton(int x, int y) {
			this.x = x;
			this.y = y;
			this.isFlag = false;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

}
