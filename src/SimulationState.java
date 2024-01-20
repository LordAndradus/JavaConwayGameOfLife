import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Stack;

public class SimulationState extends State
{
	//Mouse position for paint
	Point mousePos;
	Point cellPos;
	
	//Run simulation
	boolean running = false;
	
	//For every 5 seconds that pass, we do one generation of the simulation
	Rectangle simSpace;
	float secondsToGenerate = 0.5f;
	int framesToGenerate = (int) (secondsToGenerate * 60.0);
	int simulationCount = 0;
	
	//Size of cells
	private final int cellSize = 20;
	
	//Screen stuff
	private final int activeAreaX = 1280;
	private final int activeAreaY = 620;
	private final int cellRows = activeAreaY / cellSize;
	private final int cellCols = activeAreaX / cellSize;
	
	
	//Main Board
	boolean[][] board;
	boolean[][] savedBoard;
	
	ClickableCells[] guiBoard;
	
	public SimulationState()
	{
		simSpace = new Rectangle(0, 100, activeAreaX, activeAreaY);
		
		//activeAreaX
		board = new boolean[cellRows][cellCols]; //Each square is 20 by 20
		savedBoard = new boolean[cellRows][cellCols];
		guiBoard = new ClickableCells[board.length * board[0].length];
		
		mousePos = new Point();
		cellPos = new Point();
		
		setupButtons();
		setupGrid();
	}
	
	public void setupButtons()
	{
		buttons.put("run", defaultButton(0, 0, 100, 100, 20, "Run"));
		buttons.get("run").textIdle = new Color(0, 204, 0, 120);
		buttons.put("save", defaultButton(100, 0, 100, 100, 20, "Save"));
		buttons.put("load", defaultButton(200, 0, 100, 100, 20, "Load"));
		buttons.put("reset", defaultButton(300, 0, 100, 100, 20, "Reset"));
		buttons.put("timer", defaultButton(400, 0, 100, 100, 20, "Timer " + secondsToGenerate + "s"));
		buttons.put("gun", defaultButton(500, 0, 100, 100, 20, "Gun"));
		buttons.put("color", defaultButton(600, 0, 100, 100, 20, "Color"));
		buttons.get("color").textIdle = new Color(255, 0, 255, 255);
		buttons.put("quit", defaultButton(700, 0, 100, 100, 20, "Quit"));
		buttons.get("quit").textIdle = new Color(255, 0, 0, 255);
	}
	
	public void setupGrid()
	{
		int row = board.length;
		int col = board[row - 1].length;
		int cell = 0;
		
		System.out.println("Rows: " + row + ", Col: " + col);
		
		for(int y = 0; y < row; y++)
		{
			for(int x = 0; x < col; x++, cell++)
			{
				guiBoard[cell] = new ClickableCells((x * cellSize), (y * cellSize) + 100, cellSize, cellSize);
			}
		}
	}

	public void paint(Graphics2D g2) 
	{
		
		g2.setColor(Color.white);
		g2.fill(simSpace);
		
		if(buttons.get("run").textChange)
		{
			buttons.get("run").textChange = false;
			g2.setFont(buttons.get("run").font);
			buttons.get("run").setTextPos(g2);
		}
		
		if(buttons.get("timer").textChange)
		{
			buttons.get("timer").textChange = false;
			g2.setFont(buttons.get("run").font);
			buttons.get("timer").setTextPos(g2);
		}
		
		for(String key : buttons.keySet())
		{
			buttons.get(key).paint(g2);
		}
		
		for(ClickableCells c : guiBoard)
		{
			c.paint(g2);
		}
		
		g2.setColor(Color.white);
		if(mousePos.y >= 100)
			g2.drawString("Cell: (" + cellPos.x + ", " + cellPos.y + ")", 800, 58);
		else
			g2.drawString("(0,0)", 800, 55);
	}

	public void update(InputHandler input, Stack<State> sm) 
	{
		//Update mouse position
		mousePos = input.getMousePosition();
		
		cellPos.x = mousePos.x / cellSize;
		cellPos.y = (mousePos.y - 100) / cellSize;
		
		//Simulation stuff
		if(running) simulationCount++;
		
		if(simulationCount >= framesToGenerate)
		{
			simulationCount -= framesToGenerate;
			
			System.out.println("board generated");
			
			board = GameOfLife.createGeneration(board);
			
			//Convert board to the GUI
			copyGUI();
		}
		
		//Clickable cells
		for(int i = 0; i < guiBoard.length; i++)
		{
			ClickableCells c = guiBoard[i];
			
			c.update(input);
			
			if(!(c.currentState == ClickableCells.state.active) && !c.debounce)
			{				
				c.isActive = !c.isActive;
				
				//Translate to board
				int row = i / (cellCols);
				int col = i % (cellCols);
				
				System.out.println("(" + row + ", " + col + ")");
				
				board[row][col] = c.isActive;
								
				c.debounce = true;
			}
		}
		
		//Buttons
		for(String key : buttons.keySet())
		{
			buttons.get(key).update(input);
		}
		
		buttons.get("color").textIdle = ClickableCells.grid;
		
		checkButtons(input);
		
		if(quit) endState(sm);
	}
	
	public void copyGUI()
	{
		for(int i = 0; i < guiBoard.length; i++)
		{
			ClickableCells c = guiBoard[i];
			
			int row = i / (cellCols);
			int col = i % (cellCols);
			
			if((board[row][col] && !c.isActive) || (!board[row][col] && c.isActive))
			{
				c.isActive = !c.isActive;
			}
		}
	}
	
	public void saveBoard()
	{
		for(int i = 0; i < board.length; i++)
		{
			savedBoard[i] = board[i].clone();
		}
	}
	
	public void loadBoard()
	{
		for(int i = 0; i < savedBoard.length; i++)
		{
			board[i] = savedBoard[i].clone();
		}
		
		copyGUI();
	}

	public void checkButtons(InputHandler input)
	{
		String key = "quit";
		if(buttonDebounce(key))
		{	
			quit = true;
			
			resetDebounce(key);
		}
		
		key = "save";
		if(buttonDebounce(key))
		{			
			saveBoard();
			
			System.out.println("Board saved successfully!");
			
			resetDebounce(key);
		}
		
		if(buttonDebounce("load"))
		{
			loadBoard();
			
			simulationCount = 0;
			
			System.out.println("Board was loaded.");
			
			resetDebounce("load");
		}
		
		if(buttonDebounce("run"))
		{
			running = !running;
			
			if(!running)
			{
				buttons.get("run").text = "Run";
				buttons.get("run").textIdle = new Color(0, 204, 0, 120);
				simulationCount = 0;
			}
			else
			{
				buttons.get("run").text = "Stop";
				buttons.get("run").textIdle = new Color(225, 0, 0, 120);
			}
			
			buttons.get("run").textChange = true;
			
			resetDebounce("run");
		}
		
		if(buttonDebounce("reset"))
		{
			for(int i = 0; i < board.length; i++)
			{
				for(int j = 0 ; j < board[i].length; j++)
				{
					board[i][j] = false;
				}
			}
			
			copyGUI();
			
			running = false;
			buttons.get("run").text = "Run";
			buttons.get("run").textIdle = new Color(0, 204, 0, 120);
			buttons.get("run").textChange = true;
			
			secondsToGenerate = 0.5f;
			framesToGenerate = (int) (secondsToGenerate * 60.0);
			
			String newTimer = String.format("Timer %.1fs", secondsToGenerate);
			buttons.get("timer").text = newTimer;
			buttons.get("timer").textChange = true;
			
			resetDebounce("reset");
		}
		
		if(buttonDebounce("timer"))
		{
			simulationCount = 0;
			secondsToGenerate += 0.10f;
			if(secondsToGenerate >= 1.1f) secondsToGenerate = 0.1f;
			framesToGenerate = (int) (secondsToGenerate * 60.0);
			
			String newTimer = String.format("Timer %.1fs", secondsToGenerate);
			buttons.get("timer").text = newTimer;
			buttons.get("timer").textChange = true;
			
			resetDebounce("timer");
		}
		else if(buttonDebounceR("timer"))
		{
			simulationCount = 0;
			secondsToGenerate -= 0.10f;
			if(secondsToGenerate <= 0.005f) secondsToGenerate = 1.0f;
			framesToGenerate = (int) (secondsToGenerate * 60.0);
			
			String newTimer = String.format("Timer %.1fs", secondsToGenerate);
			buttons.get("timer").text = newTimer;
			buttons.get("timer").textChange = true;
			
			resetDebounceR("timer");
		}
		
		if(buttonDebounce("gun"))
		{
			setGliderGun();
			resetDebounce("gun");
		}
		
		if(buttonDebounce("color"))
		{
			Color magenta = new Color(255, 0, 255, 255);
			Color red = new Color(255, 0, 0, 255);
			Color green = new Color(0, 102, 0, 255);
			Color blue = new Color(0, 0, 255, 255);
			Color brown = new Color(153, 76, 0, 255);
			Color darkCyan = new Color(0, 204, 204, 255);
			
			Color magentaH = new Color(255, 0, 255, 120);
			Color redH = new Color(255, 0, 0, 120);
			Color greenH = new Color(0, 102, 0, 120);
			Color blueH = new Color(0, 0, 255, 120);
			Color brownH = new Color(153, 76, 0, 120);
			Color darkCyanH = new Color(0, 204, 204, 120);
			
			//We START with Magenta
			if(ClickableCells.grid.getRGB() == magenta.getRGB()) //Magenta -> Green
			{
				ClickableCells.grid = green;
				ClickableCells.gridH = greenH;
			}
			else if(ClickableCells.grid.getRGB() == green.getRGB()) //Green -> Red
			{
				ClickableCells.grid = red;
				ClickableCells.gridH = redH;
			}
			else if(ClickableCells.grid.getRGB() == red.getRGB()) //Red -> Blue
			{
				ClickableCells.grid = blue;
				ClickableCells.gridH = blueH;
			}
			else if(ClickableCells.grid.getRGB() == blue.getRGB()) //Blue -> Brown
			{
				ClickableCells.grid = brown;
				ClickableCells.gridH = brownH;
			}
			else if(ClickableCells.grid.getRGB() == brown.getRGB()) //Brown -> Dark Cyan
			{
				ClickableCells.grid = darkCyan;
				ClickableCells.gridH = darkCyanH;
			}
			else if(ClickableCells.grid.getRGB() == darkCyan.getRGB()) //Dark Cyan -> Magenta
			{
				ClickableCells.grid = magenta;
				ClickableCells.gridH = magentaH;
			}
			
			resetDebounce("color");
		}
		else if(buttonDebounceR("color"))
		{
			Color magenta = new Color(255, 0, 255, 255);
			Color red = new Color(255, 0, 0, 255);
			Color green = new Color(0, 102, 0, 255);
			Color blue = new Color(0, 0, 255, 255);
			Color brown = new Color(153, 76, 0, 255);
			Color darkCyan = new Color(0, 204, 204, 255);
			
			Color magentaH = new Color(255, 0, 255, 120);
			Color redH = new Color(255, 0, 0, 120);
			Color greenH = new Color(0, 102, 0, 120);
			Color blueH = new Color(0, 0, 255, 120);
			Color brownH = new Color(153, 76, 0, 120);
			Color darkCyanH = new Color(0, 204, 204, 120);
			
			//We START with Magenta
			if(ClickableCells.grid.getRGB() == magenta.getRGB()) //Magenta -> Dark Cyan
			{
				ClickableCells.grid = darkCyan;
				ClickableCells.gridH = darkCyanH;
			}
			else if(ClickableCells.grid.getRGB() == darkCyan.getRGB()) //Dark Cyan -> Brown
			{
				ClickableCells.grid = brown;
				ClickableCells.gridH = brownH;
			}
			else if(ClickableCells.grid.getRGB() == brown.getRGB()) //Brown -> Blue
			{
				ClickableCells.grid = blue;
				ClickableCells.gridH = blueH;
			}
			else if(ClickableCells.grid.getRGB() == blue.getRGB()) //Blue -> Red
			{
				ClickableCells.grid = red;
				ClickableCells.gridH = redH;
			}
			else if(ClickableCells.grid.getRGB() == red.getRGB()) //Red -> Green
			{
				ClickableCells.grid = green;
				ClickableCells.gridH = greenH;
			}
			else if(ClickableCells.grid.getRGB() == green.getRGB()) //Green -> Magenta
			{
				ClickableCells.grid = magenta;
				ClickableCells.gridH = magentaH;
			}
			
			resetDebounceR("color");
		}
	}
	
	public void setGliderGun()
	{
		for(int row = 0; row < board.length; row++)
		{
			for(int col = 0; col < board[row].length; col++)
			{
				board[row][col] = false;
			}
		}
		
		board[8][8] = true;
		board[8][9] = true;
		board[9][9] = true;
		board[9][8] = true;
		
		board[8][18] = true;
		board[9][18] = true;
		board[10][18] = true;
		board[11][19] = true;
		board[12][20] = true;
		board[12][21] = true;
		
		board[7][19] = true;
		board[6][20] = true;
		board[6][21] = true;
		board[9][22] = true;
		board[7][23] = true;
		board[11][23] = true;
		board[8][24] = true;
		board[9][24] = true;
		board[10][24] = true;
		board[9][25] = true;
		board[8][28] = true;
		board[7][28] = true;
		board[6][28] = true;
		board[6][29] = true;
		board[7][29] = true;
		board[8][29] = true;
		board[9][30] = true;
		board[5][30] = true;
		board[5][32] = true;
		board[4][32] = true;
		board[9][32] = true;
		board[10][32] = true;
		board[6][42] = true;
		board[6][43] = true;
		board[7][43] = true;
		board[7][42] = true;
		
		copyGUI();
	}
	
	public void endState(Stack<State> sm) 
	{
		System.err.println("Simulation State is ending.");
		sm.pop();
	}

}
