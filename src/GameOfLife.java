import java.util.ArrayList;
import java.util.Arrays;

public class GameOfLife 
{
	/*
	 * This function will view the current board and will apply the 4 rules for the game of life.
	 * Essentially there are 4 rules to consider, which is as follows
	 * 1. If the cell is active and has 2 or 3 living neighbors, it will continue existing
	 * 2. If the cell is active and has less than 2 or more than 3 living neighbors, then this cell will expire.
	 * 3. If the cell is not active, and it has 3 living neighbors then the cell will activate.
	 * 4. If the cell is not active, and it does not have exactly 3 living neighbors, then the cell will remain inactive.
	 * 
	 * Putting all of this together, we will update the board to reflect these rules and return the new state
	 * of the board back to the original call of the function.
	 * 
	 * The function will take in a 2D Boolean array which will then be modified and returned back to the calling function.
	 */
	public static boolean[][] createGeneration(boolean[][] board)
	{
		if(board == null) System.exit(0);
		
		int row = board.length;
		int col = board[0].length;
		
		board = testBoardSize(board, row, col);
		
		boolean[][] nextGeneration = new boolean[row][col];
		
		for(int i = 0; i < row; i++) nextGeneration[i] = Arrays.copyOf(board[i], col);
		
		for(int i = 0; i < row; i++)
		{
			for(int j = 0; j < col; j++)
			{
				int activeNeighbors = countActiveCells(board, i, j);
				
				//Left over debug comment to cross-examine if the counter is working.
				//println("Cell (" + i + ", " + j + ") -> Active neighbors: " + activeNeighbors);
				
				/*
				 * So rule 1 and 4 are basically useless here. 
				 * Rule 1: If it's already active, it's going to stay active, thus no change.
				 * Rule 4: If it's already inactive it's going to stay inactive, thus no change.
				 */
				
				if(board[i][j])
				{
					if(activeNeighbors < 2 || activeNeighbors > 3) nextGeneration[i][j] = false; //Rule 2
				}
				else
				{
					if(activeNeighbors == 3) nextGeneration[i][j] = true; //Rule 3
				}
			}
		}
		
		return nextGeneration;
	}
	
	/*
	 * Counts a 9x9 grid of the current cell, it will check if a neighbor is active (true) or inactive (false)
	 * 
	 * Function will take a 2D boolean array as the main board, and the current location of the cell.
	 * 
	 * Function is set to private, so we don't accidentally use this outside of the class and get confused.
	 * It will return an integer which is how many active neighbors are near the cell.
	 */
	private static int countActiveCells(boolean[][] board, int row, int col)
	{
		int rowMax = board.length;
		int colMax = board[0].length;
		
		int count = 0;
		
		boolean leftExist = row - 1 >= 0;
		boolean topExist = col - 1 >= 0;
		boolean rightExist = row + 1 < rowMax;
		boolean bottomExist = col + 1 < colMax;
		
		//Check top-row
		if(topExist)
		{
			if(leftExist) if(board[row - 1][col - 1]) count++;
			if(board[row][col - 1]) count++;
			if(rightExist) if(board[row + 1][col - 1]) count++;
		}
		
		//Check current-row | We will skip board[row][col] because we don't count itself.
		if(leftExist && board[row - 1][col]) count++;
		if(rightExist && board[row + 1][col]) count++;
		
		//Check bottom-row
		if(bottomExist)
		{
			if(leftExist) if(board[row - 1][col + 1]) count++;
			if(board[row][col + 1]) count++;
			if(rightExist) if(board[row + 1][col + 1]) count++;
		}
		
		return count;
	}
	
	/*
	 * This function will read the board from left-to-right, top-to-bottom and will fill in a pair of coordinates when it
	 * discovers an active cell.
	 * 
	 * The function will take in a 2D Boolean array which will be scanned. While scanning we will fill in a 2D Integer array
	 * which will contain a pair of coordinates of where the active cell will be located on the board.
	 * We will then return the array of coordinate pairs.
	 */
	public static int[][] findLivingCellLocations(boolean[][] gameOfLife)
	{
		if(gameOfLife == null) System.exit(0);
		
		int row = gameOfLife.length; //How many elements are in a single array, effectively how long a row is.
		int col = gameOfLife[0].length; //How many arrays are in the 2D array, effectively how many columns are present.
		
		gameOfLife = testBoardSize(gameOfLife, row, col);
		
		//We don't know how many actual coordinates are available, so we'll use an ArrayList for its dynamic memory
		ArrayList<int[]> coords = new ArrayList<int[]>();
		
		for(int i = 0; i < gameOfLife.length; i++) //Top to bottom
		{
			for(int j = 0; j < gameOfLife[0].length; j++) //Left to right
			{
				if(gameOfLife[i][j])
				{
					int[] place = {i, j};
					coords.add(place);
				}
			}
		}
		
		//Convert ArrayList to Integer array
		int[][] coordinates = new int[coords.size()][2];
		
		for(int i = 0; i < coords.size(); i++)
		{
			coordinates[i][0] = coords.get(i)[0];
			coordinates[i][1] = coords.get(i)[1];
		}
		
		return coordinates;
	}
	
	/*
	 * This is a utility function to test if our current board has a size. If it does not, or if it's null
	 * Then there's something wrong and we shouldn't risk crashing the program.
	 * 
	 * Returns the original board back if it's A-okay; otherwise we halt the program with a System exit.
	 */
	private static boolean[][] testBoardSize(boolean[][] board, int row, int col)
	{
		try
		{
			//Test if the array has a size.
			if(row == 0 || col == 0) throw new Exception("Sizeless Array");
			else if(board == null) throw new Exception("NULL Array");
		}
		catch(Exception e)
		{
			//Throw the error and return a null pointer.
			System.err.println("Error: The array has no size.");
			System.exit(0);
		}
		
		return board;
	}
	
	/*
	 * As the name implies, it prints the board. x := true | o := false
	 */
	public static void printBoard(boolean[][] board)
	{
		int row = board.length;
		int col = board[0].length;

		for(int i = 0; i < row; i++)
		{
			print("|");
			for(int j = 0; j < col - 1; j++)
			{
				print(board[i][j] ? "x " : "o ");
			}
			
			println(board[i][col - 1] ? "x|" : "o|");
		}

		println("");
	}
	
	/*
	 * This is just a helper function so I can be lazy and not have to write out "System.out"
	 * Essentially, when examining System.out.println it will always take an 'Object' and print it out
	 * So, I'll be taking advantage of that here to print a new line.
	 * 
	 * It's static because a class needs to be instantiated if we want to use a regular function, and that
	 * would defeat the purpose of not using "System.out"
	 */
	static void println(Object x)
	{
		System.out.println(x);
	}
	
	/*
	 * Same idea applies here, but instead of creating a new line afterwards, we'll just merely print and
	 * continue on with our day.
	 */
	static void print(Object x)
	{
		System.out.print(x);
	}
}
