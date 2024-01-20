import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.util.Stack;

import javax.swing.JPanel;

public class GUI extends JPanel implements Runnable
{
	//Debug
	boolean printInfo = false;
	
	//If I want to add save/load features in the future, then here we go
	private static final long serialVersionUID = 1L;
	
	//Screen parameters
	public int screenWidth = 1280;
	public int screenHeight = 720;
	
	//User Input
	InputHandler input = new InputHandler();
	
	//Thread to control window
	Thread guiThread;
	
	//State machine
	public Stack<State> sm = new Stack<State>();
	
	public GUI()
	{
		Dimension screen = new Dimension(screenWidth, screenHeight);
		this.setSize(screen);
		this.setPreferredSize(screen);
		this.setMinimumSize(screen);
		this.setBackground(Color.BLACK);
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.setFocusTraversalKeysEnabled(false);
		
		//Basic Inputs
		this.addMouseListener(input);
		this.addMouseWheelListener(input);
		this.addKeyListener(input);
	}
	
	public void startThread()
	{
		sm.push(new MainMenuState());
		
		guiThread = new Thread(this);
		guiThread.start();
	}
	
	@Override
	public void run()
	{
		double drawInterval = 1000000000.0 / 60.0; //1 second in nano-sceonds, divided by target FPS of 60
		double delta = 0.00;
		long prevTime = System.nanoTime();
		long currTime;
		
		while(guiThread != null)
		{
			currTime = System.nanoTime();
			delta += (currTime - prevTime) / drawInterval; //60FPS
			prevTime = currTime;
			
			//When we reach a new frame, update and draw
			if(delta >= 1)
			{				
				update();
				
				repaint();
				
				delta--;
			}
		}
	}
	
	
	int testFrame = 0;
	int newFrame = 5 * 60;
	public void update()
	{
		//Update mouse position
		input.updateMousePosition();
		
		input.mousePos.x -= this.getLocationOnScreen().x;
		input.mousePos.y -= this.getLocationOnScreen().y;
		
		if(printInfo) testFrame++;
		if(testFrame >= newFrame)
		{
			testFrame -= newFrame;
			System.out.println("Screen: " + this.getLocation().toString());
			System.out.println("Mouse: " + input.mousePos.toString());
		}
		
		if(!sm.isEmpty())
		{
			sm.peek().update(input, sm);
		}
		else
		{
			System.err.println("Application is ending, no states found.");
			guiThread = null;
			
			this.dispatchEvent(new WindowEvent(Main.window, WindowEvent.WINDOW_CLOSING));
			
			System.exit(0);
		}
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		try
		{
			sm.peek().paint(g2);
		}
		catch(Exception e)
		{
			System.err.println("Tried to print state, but failed");
			System.exit(0);
		}
	}
}
