package mines;

import java.util.Random;

public class Mines {
	// Class for the logical of the game
	private int height, width, numMines;
	private Mine[][] board;
	private boolean showAll = false;

	public Mines(int height, int width, int numMines) {
		// Constructor
		this.height = height;
		this.width = width;
		this.numMines = numMines;
		this.board = new Mine[height][width];
		initBoard();
	}

	public void initBoard() {
		/* 
		 * method to place a new mine object in each index of the matrix
		 *  then randomly add bombs to the board
		 */
		int i, j;
		Random r = new Random();
		for (i = 0; i < height; i++)
			for (j = 0; j < width; j++)
				board[i][j] = new Mine(i, j);
		for (int k = 0; k < numMines; k++) {
			do {
				i = r.nextInt(height);
				j = r.nextInt(width);
			} while (board[i][j].isMine);  	// as long as there is a mine in this spot -> random new place
			addMine(i, j); 					// finally add the mine
		}
	}

	// function to add mine, if there is a mine throw exception
	public boolean addMine(int i, int j) {
		/* 
		 * 	this method place a mine in specific position
		 *  because it is mainly for tests, we choose to throw an exception here
		 */
		if (board[i][j].isMine || !isValid(i, j))
			throw new IllegalArgumentException();
		board[i][j].setMine();
		incrementMines(i, j);
		return true;

	}


	private void incrementMines(int i, int j) {
		// method to add +1 to all neighbors of this mine
		int x, y;
		for (x = i - 1; x < i + 2; x++)
			for (y = j - 1; y < j + 2; y++)
				if (isValid(x, y))
					board[x][y].incerementMines();
	}

	
	public boolean open(int i, int j) {
		// a method to open all the required spots on the board when opening a
		// single spot, followed by the rules of minesweeper
		Mine temp = board[i][j];
		if (temp.isMine)
			return false;	//  -> mine here
		
		if (temp.isOpen)
			return true;  // nothing to see here
		temp.setOpen();  // not open & not mine
		
		// if there is no bombs around, check all neighbors if they can open too.
		if (temp.minesAround == 0) {
			for (int x = i - 1; x <= i + 1; x++)
				for (int y = j - 1; y <= j + 1; y++)
					if (isValid(x, y) && !board[i][j].isMine)
						open(x, y);
		}
		return true;
	}

	// toggle a flag on a certain spot.
	public void toggleFlag(int x, int y) { board[x][y].setFlag(); }

	// check if index is in matrix
	private boolean isValid(int i, int j) { return (i < height && j < width && i >= 0 && j >= 0); }

	// check if the game is won, if all bombs not revealed and all other is revealed
	public boolean isDone() {
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				if (!board[i][j].isOpen && !board[i][j].isMine)
					return false;
		return true;
	}

	// get current index item, flag bomb or number.
	public String get(int i, int j) { return board[i][j].toString(); }

	// show all hidden board spots.
	public void setShowAll(boolean showAll) { this.showAll = showAll; }

	// print all the board.
	public String toString() {
		int i, j;
		StringBuilder res = new StringBuilder();
		for (i = 0; i < height; i++) {
			for (j = 0; j < width; j++)
				res.append(board[i][j].toString());
			res.append("\n");
		}
		return res.toString();
	}

	
	private class Mine {
		// private mine inner class to save info, such as flag open or mine on certain
		// indexes to use in other functions.
		private int i, j, minesAround = 0;
		private boolean isOpen = false, isFlag = false, isMine = false;

		public Mine(int i, int j) {
			// Constructor
			this.i = i;
			this.j = j;
		}

		// We've created a setters 
		private void setOpen() { isOpen = true; }

		private void setFlag() { isFlag = !isFlag; }

		private void setMine() {  isMine = true; }

		private void incerementMines() { minesAround += 1; }

		@Override
		public String toString() {
			if (showAll) {
				if (isMine)
					return "X";
				else if (String.valueOf(minesAround).equals("0"))
					return " ";
				else
					return String.valueOf(minesAround);
			}
			if (!isOpen) {
				if (isFlag)
					return "F";
				return ".";
			}
			if (minesAround != 0)
				return String.valueOf(minesAround);
			return " ";
		}

	}

}
