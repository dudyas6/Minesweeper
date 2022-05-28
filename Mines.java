package mines;

import java.util.Random;

public class Mines {
	private int height, width, numMines;
	private Mine[][] board;
	private boolean showAll;

	public Mines(int height, int width, int numMines) {
		this.height = height;
		this.width = width;
		this.numMines = numMines;
		this.showAll = false;
		this.board = new Mine[height][width];
		initBoard();
	}

	public void initBoard() {
		int i, j;
		Random r = new Random();
		for (i = 0; i < height; i++)
			for (j = 0; j < width; j++)
				board[i][j] = new Mine(i, j);
		for (int k = 0; k < numMines; k++) {
			do {
				i = r.nextInt(height);
				j = r.nextInt(width);
			} while (board[i][j].isMine);
			addMine(i, j);
		}
	}

	public boolean addMine(int i, int j) {
		if (board[i][j].isMine || !isValid(i, j))
			throw new IllegalArgumentException();
		board[i][j].setMine();
		incrementMines(i, j);
		return true;

	}

	private void incrementMines(int i, int j) {
		int x, y;
		for (x = i - 1; x < i + 2; x++)
			for (y = j - 1; y < j + 2; y++)
				if (isValid(x, y))
					board[x][y].incerementMines();
	}

	public boolean open(int i, int j) {
		Mine temp = board[i][j];
		if (temp.isMine)
			return false;

		if (temp.isOpen)
			return true;

		temp.setOpen();

		if (temp.minesAround == 0) {
			for (int x = i - 1; x <= i + 1; x++)
				for (int y = j - 1; y <= j + 1; y++)
					if (isValid(x, y) && !board[i][j].isMine)
						open(x, y);
		}
		return true;
	}

	public void toggleFlag(int x, int y) {
		board[x][y].setFlag();
	}

	public boolean isValid(int i, int j) {
		return (i < height && j < width && i >= 0 && j >= 0);
	}

	public boolean isDone() {
		boolean res = true;
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				if (!board[i][j].isOpen && !board[i][j].isMine)
					res = false;
		return res;
	}

	public String get(int i, int j) {
		return board[i][j].toString();
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	public String toString() {
		int i, j;
		StringBuilder res = new StringBuilder();
		for (i = 0; i < height; i++) {
			res.append("\n");
			for (j = 0; j < width; j++)
				res.append(board[i][j].toString());
		}
		return res.toString();
	}

	private class Mine {
		private int i, j, minesAround;
		private boolean isOpen, isFlag, isMine;

		public Mine(int i, int j) {
			this.i = i;
			this.j = j;
			this.minesAround = 0;
			this.isOpen = false;
			this.isFlag = false;
			this.isMine = false;
		}

		public void setOpen() {
			this.isOpen = true;
		}

		public void setFlag() {
			this.isFlag = !this.isFlag;
		}

		public void setMine() {
			this.isMine = true;
		}

		public void incerementMines() {
			this.minesAround += 1;
		}

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

	public static void main(String[] args) {

		Mines m = new Mines(5, 5, 0);
		m.addMine(2, 2);
		m.open(1, 0);

		System.out.println(m);
	}
}
