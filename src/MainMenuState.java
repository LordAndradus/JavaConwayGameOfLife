import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class MainMenuState extends State
{	
	//TODO: Remove this later
	//DEBUG
	//Point mousePos;
	
	//Timer
	private float secondCounter = 0.5f;
	private int generateFrameLimit = (int) (secondCounter * 60.0f);
	private int demoCounter = 0;
	
	//Demo area
	Rectangle demoArea;
	int demoCells = 9;
	int demoWidth = 540;
	int demoHeight = 540;
	int demoCellSize = demoWidth/demoCells;
	int demoRow = demoWidth / demoCellSize;
	int demoCol = demoHeight / demoCellSize;
	
	//Demo board
	boolean[][] demo;
	Rectangle[] demoGrid;
	
	public MainMenuState()
	{
		//mousePos = new Point();
		setupButtons();
		setupDemo();
	}
	
	public void setupDemo()
	{
		demoArea = new Rectangle(550, 70, demoWidth, demoHeight);
		demo = new boolean[demoRow][demoCol];
		demoGrid = new Rectangle[demoRow * demoCol];
		
		generateRandomDemo();
		
		int cell = 0;
		for(int y = 0; y < demoRow; y++)
		{
			for(int x = 0; x < demoCol; x++, cell++)
			{
				demoGrid[cell] = new Rectangle((x * demoCellSize) + 552, (y * demoCellSize) + 72, demoCellSize - 4, demoCellSize - 4);
			}
		}
	}
	
	public void generateRandomDemo()
	{
		for(int row = 0; row < demo.length; row++)
		{
			for(int col = 0; col < demo[row].length; col++)
			{
				demo[row][col] = false;
			}
		}
		
		
		//Generate number from [0, 2)
		int preset = ThreadLocalRandom.current().nextInt(0, 6);
		
		System.out.println("Chosen preset: " + preset);
		
		if(preset == 0)
		{
			//Make explosion
			demo[4][4] = true;
			demo[4][5] = true;
			demo[5][4] = true;
			demo[4][3] = true;
			demo[3][4] = true;
		}
		else if(preset == 1)
		{
			//Make the glider
			demo[0][0] = true;
			demo[2][1] = true;
			demo[0][2] = true;
			demo[1][2] = true;
			demo[2][2] = true;
		}
		else if(preset == 2)
		{
			//Make frog trap
			demo[5][3] = true;
			demo[5][4] = true;
			demo[5][5] = true;
			demo[4][4] = true;
			demo[4][5] = true;
			demo[4][6] = true;
		}
		else if(preset >= 3)
		{
			//Truly random pattern
			//Generate number from [10, 71)
			int cells = ThreadLocalRandom.current().nextInt(0, 71);
			
			for(int i = 0; i < cells; i++)
			{
				int row = ThreadLocalRandom.current().nextInt(0, demoCells);
				int col = ThreadLocalRandom.current().nextInt(0, demoCells);
				
				if(demo[row][col]) //Skip the generated cell if it already exists
				{
					i--;
					continue;
				}
				
				demo[row][col] = true;
			}
		}
	}
	
	public void setupButtons()
	{
		buttons.put("start", defaultButton(100, 100, 300, 100, 36, "Start"));
		buttons.put("quit", defaultButton(100, 400, 300, 100, 36, "Quit"));
	}
	
	public void paint(Graphics2D g2) 
	{		
		for(String key : buttons.keySet())
		{
			buttons.get(key).paint(g2);
		}
		
		paintDemo(g2);
		
		//TODO: Remove this later
		//g2.setFont(new Font("Arial", Font.ITALIC, 10));
		//g2.setColor(Color.YELLOW);
		//g2.drawString("(" + mousePos.x + ", " + mousePos.y + ")", mousePos.x, mousePos.y);
	}
	
	public void paintDemo(Graphics2D g2)
	{	
		g2.setColor(Color.WHITE);
		g2.fill(demoArea);
		
		g2.setFont(new Font("Arial", Font.ITALIC, 36));
		g2.drawString("LIVE DEMO", 725, 50);
		
		g2.setColor(Color.BLUE);
		
		for(int row = 0; row < demo.length; row++)
		{
			for(int col = 0; col < demo[row].length; col++)
			{
				int index = (row * demo[row].length) + col;
				
				Rectangle r = demoGrid[index];
												
				if(demo[row][col]) g2.fill(r);
				else g2.drawRect(r.x, r.y, r.width, r.height);
			}
		}
	}
	
	public void update(InputHandler input, Stack<State> sm) 
	{
		//mousePos = input.getMousePosition();
		
		for(String key : buttons.keySet())
		{
			buttons.get(key).update(input);
		}
		
		if(buttonDebounce("start"))
		{
			//Add to state machine in GUI
			System.out.println("You clicked start, wow");
			sm.push(new SimulationState());
			
			generateRandomDemo();
			
			resetDebounce("start");
		}
		
		if(buttonActive("quit"))
		{
			//TODO: Generate window that asks if you are sure.
			System.err.println("Initiating quit protocol for main program");
			quit = true;
		}
		
		updateDemo();
		
		if(quit) endState(sm);
	}
	
	public void updateDemo()
	{
		demoCounter++;
		if(demoCounter >= generateFrameLimit)
		{
			demoCounter -= generateFrameLimit;
			
			demo = GameOfLife.createGeneration(demo);
		}
	}
	
	public void endState(Stack<State> sm)
	{
		System.err.println("Main Menu State is ending.");
		sm.pop();
	}
}
