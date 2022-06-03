package mines;

import java.util.Random;

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
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GameController {
	// class that Controls the whole game 
	private Mines boardInfo;
	private infoButton[][] buttonsInfo;
	private int height = 10, width = 10, mines = 5;  // default values
	private HBox box;
	private Stage stage;
	boolean labelsVisible = true;

	public void setController(HBox box, Stage stage) {
		// send box and stage from MinesFX to this class for future use
		this.box = box;
		this.stage = stage;

		setBox(); 				// set difficulty box
		initBoard(); 			// initialize first run board
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
		// method for setting the board 
		if (labelsVisible)  getValues();  	// if the player chose "Custom"
		initBoard(); 						// initialize
	}

	@FXML
	void changeMode(ActionEvent event) {
		// a function to set difficulty according to choice, custom for text boxes.
		Object selectedItem = choiceBox.getSelectionModel().getSelectedItem();
		if (selectedItem.toString().equals("Custom")) {
			changeLabels(true);
			return;

		} else if (selectedItem.toString().equals("Easy")) {
			height = 8;
			width = 10;
			mines = 13;
			return;

		} else if (selectedItem.toString().equals("Moderate")) {
			height = 13;
			width = 15;
			mines = 33;

		} else {  // hard
			height = 18;
			width = 20;
			mines = 50;
		}
		changeLabels(false);

	}

	// initiate board after setting values
	private void initBoard() {
		GridPane grid = new GridPane();		// new grid
		if (!(boardInfo == null)) // not the first game
			box.getChildren().remove(box.getChildren().size() - 1);
		// add constrains, set the grid buttons and add it to main stage.
		addConstrains(grid);
		setGrid(grid);
		box.getChildren().add(grid);
		box.autosize();
		stage.sizeToScene();
	}

	private void addConstrains(GridPane grid) {
		// add constrains, the percentage the buttons take from column/row
		for (int i = 0; i < width; i++)
			grid.getColumnConstraints().add(new ColumnConstraints(40));
		for (int j = 0; j < height; j++)
			grid.getRowConstraints().add(new RowConstraints(40));
	}

	private void setGrid(GridPane grid) {
		boardInfo = new Mines(height, width, mines);	// creates new Mines board for all information on minesweeper
		buttonsInfo = new infoButton[height][width]; 	// creates buttons accordingly to mines board
		int i, j;
		infoButton btn;
		for (i = 0; i < height; i++)
		{
			for (j = 0; j < width; j++) {
				// set all buttons (i,j) indexes and text info according to boardInfo

				buttonsInfo[i][j] = btn = new infoButton(i, j);
				btn.setText(boardInfo.get(i, j));
				btn.setFont(Font.font(null, FontWeight.BOLD, 14));
				btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
				btn.setOnMouseClicked(new Reveal()); 	// set on click functions and add to grid.
			
				
				grid.add(buttonsInfo[i][j], j, i);		// add button to grid
			}
		}
	}

	
	// function for button clicks.
	class Reveal implements EventHandler<MouseEvent> {
		Random r = new Random();  // new random object to get random sound from above list

		@Override
		public void handle(MouseEvent event) {
			
			int i = ((infoButton) event.getSource()).getX();
			int j = ((infoButton) event.getSource()).getY();
			boolean isntMine;
			if (event.getButton() == MouseButton.PRIMARY) {
				
				// if left click, check the current button info
				// if its mine, show all and deploy lost msg
				// if its not mine open all the required spots on board.
				isntMine = boardInfo.open(i, j);
				if (!isntMine || boardInfo.isDone()) 
					endGame(!isntMine);
				
				else {
					String[] box_sound = new String[] {"/mines/sounds/box_sound1.m4a", "/mines/sounds/box_sound2.m4a", "/mines/sounds/box_sound3.m4a"};
					AudioClip buzzer = new AudioClip(getClass().getResource(box_sound[r.nextInt(3)]).toExternalForm());
					buzzer.play();
				}
			}
			// if its secondary click, toggle flag.
			else if (event.getButton() == MouseButton.SECONDARY) {
				String[] flag_sound = new String[] {"/mines/sounds/flag1_sound.mp3", "/mines/sounds/flag2_sound.mp3", "/mines/sounds/flag3_sound.mp3"};
				AudioClip buzzer = new AudioClip(getClass().getResource(flag_sound[r.nextInt(3)]).toExternalForm());
				buzzer.setVolume(120);
				buzzer.play();
				boardInfo.toggleFlag(i, j);
			}
			// update board after its changes.
			updateBoard();
			return;
		}

	}

	// add choices to the dropdown choices box.
	private void setBox() {
		ObservableList<String> availableChoices = FXCollections.observableArrayList("Custom", "Easy", "Moderate",
				"Hard");
		choiceBox.setItems(availableChoices);
		choiceBox.getSelectionModel().select("Custom");
	}


	private void endGame(boolean clickedMine) {
		// method to handle the game over case
		HBox endBox = new HBox();
		Label endLabel = new Label();
		String condition = "";
		boardInfo.setShowAll(true);		// show all board 
		
		// disabled all board after ending the game
		for (int i = 0; i < height; i++) 
			for (int j = 0; j < width; j++) 
				buttonsInfo[i][j].setDisable(true);
			
		
		if (clickedMine) 
			condition = "/mines/sounds/explode_sound.mp3";
		else
			condition = "/mines/sounds/winning_sound.m4a";
		
		AudioClip buzzer = new AudioClip(getClass().getResource(condition).toExternalForm());
		buzzer.play(); 
		endLabel.setFont(Font.font(null, FontWeight.BOLD, 20));
		endLabel.setText("Game Over\n" + 
				(clickedMine ?  "     Loser!"  : "   Winner!"));  // set text based on end condition
		endBox.setAlignment(Pos.CENTER);
		endBox.getChildren().add(endLabel);
		Scene endScene = new Scene(endBox, 300, 100);
		Stage endStage = new Stage();
		endStage.setTitle("Its over");
		endStage.getIcons().add(new Image("/mines/imgs/icon_bomb.png"));

		endStage.setScene(endScene);
		endStage.show();
	}

	private void updateBoard() {
		// method for update for after each action
		
		int i, j;
		String[] colors = new String[] {"blue", "navy", "green", "purple", "orange", "red"};

		for (i = 0; i < height; i++) {
			for (j = 0; j < width; j++) {
				// for each button:
				infoButton curButton = buttonsInfo[i][j];  
				curButton.setText(boardInfo.get(i, j)); 	// set text on button
				boolean isNumeric = curButton.getText().chars().allMatch( Character::isDigit );  // checks if num or not

				if(isNumeric)
					curButton.setStyle("-fx-text-fill: " + colors[Integer.parseInt(buttonsInfo[i][j].getText()) % 6]);
			
				if (curButton.getText().equals("X") || curButton.getText().equals("F")) 
					imgLoad(curButton.getText(), curButton);
				
				else
					curButton.setGraphic(null);

			}
		}
		
	}
	private void imgLoad(String s, infoButton btn)
	{
		// the method sets an image on specific button, corresponding to Flag or Bomb
		Image img;
		ImageView view;
		btn.setText("");
		if(s.equals("X")) {
			btn.setStyle("-fx-background-color: red");
			img = new Image("/mines/imgs/bomb.png");
		}
		else 
			img = new Image("/mines/imgs/flag.png");
		
		view = new ImageView(img);
		view.setFitHeight(20);
		view.setPreserveRatio(true);
		btn.setGraphic(view);
	}
	// show/hide all labels when not Custom
	
	private void changeLabels(Boolean visible) {
		
		heightText.setVisible(visible);
		widthText.setVisible(visible);
		minesText.setVisible(visible);
		heightLabel.setVisible(visible);
		widthLabel.setVisible(visible);
		minesLabel.setVisible(visible);
		labelsVisible = visible;

	}

	

	private void getValues() {
		// get values from labels
		height = Integer.parseInt(heightLabel.getText());
		width = Integer.parseInt(widthLabel.getText());
		mines = Integer.parseInt(minesLabel.getText());
	}

	private class infoButton extends Button {
		// inner method to save the button real location on table
		private int x, y;
		public infoButton(int x, int y) {
			this.x = x;
			this.y = y;

		}

		public int getX() { return x; }

		public int getY() { return y; }

		
	}

}
