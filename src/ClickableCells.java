import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ClickableCells 
{
	private Rectangle body;
	private Rectangle full;
	//private Rectangle border;
	
	//Check if cell is active
	boolean isActive = false;
	
	//Special boolean to check if user unclicks
	boolean debounce = true; //It is not being clicked = true, it needs to be debounced = false
	
	//Colors
	public static Color grid = new Color(255, 0, 255, 255);
	public static Color gridH = new Color(255, 0, 255, 120);
	
	state currentState = state.idle;
	public enum state
	{
		active,
		hover,
		idle
	}
	
	public ClickableCells(int x, int y, int width, int height)
	{
		body = new Rectangle(x + 1, y + 1, width - 2, height - 2);
		full = new Rectangle(x, y, width, height);
		//border = new Rectangle(x, y, width, height);
	}
	
	public void update(InputHandler input)
	{
		currentState = state.idle;
		
		if(full.contains(input.getMousePosition()))
		{
			currentState = state.hover;
			
			if(input.leftClick)
			{
				currentState = state.active;
				
				debounce = false;
			}
		}
	}
	
	public void paint(Graphics2D g2)
	{	
		g2.setColor(grid);
		//g2.drawRect(border.x, border.y, border.width, border.height);
		
		switch(currentState)
		{
		case state.active, state.idle:
			g2.setColor(grid);
			if(isActive) g2.fill(body);
			else g2.drawRect(body.x, body.y, body.width, body.height);
			if(!debounce)
			{
				g2.setColor(gridH);
				g2.fill(body);
			}
			break;
			
		case state.hover:
			g2.setColor(gridH);
			g2.fill(body);
			break;
		}
	}
	
	public String toString()
	{
		return "Cell [x=" + body.x + ", y=" + body.y + ", width=" + body.width + ", height=" + body.height;
	}
}
