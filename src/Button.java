import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Button 
{
	private Rectangle body;
	
	//Colors
	Color buttonIdle;
	Color buttonHover;
	Color buttonActive;
	Color textIdle;
	Color textHover;
	Color textActive;
	
	//Main body text
	String text;
	int textX;
	int textY;
	boolean textChange = false;
	
	//Font for use
	Font font;
	
	//Boolean setText pos
	boolean setTextPos = false;
	
	//Special boolean to check if user unclicks
	boolean debounce = true; //It is not being clicked = true, it needs to be debounced = false
	boolean debounceR = true;
	
	state currentState;
	public static enum state
	{
		active,
		hover,
		idle
	};
	
	public Button(int x, int y, int width, int height, 
	int fontSize, String text, String fontType,
	Color textIdle, Color textHover, Color textActive,
	Color buttonIdle, Color buttonHover, Color buttonActive)
	{
		currentState = state.idle;
		
		body = new Rectangle(x, y, width, height);
		
		this.buttonIdle = buttonIdle;
		this.buttonHover = buttonHover;
		this.buttonActive = buttonActive;
		this.textIdle = textIdle;
		this.textHover = textHover;
		this.textActive = textActive;
		
		this.text = text;
		
		this.font = new Font(fontType, Font.PLAIN, fontSize);
		
		this.textX = x + (width / 2);
		this.textY = y + (height / 2);
	}
	
	public Button(int x, int y, int width, int height, 
	int fontSize, String fontSpecial, String text, String fontType,
	Color textIdle, Color textHover, Color textActive,
	Color buttonIdle, Color buttonHover, Color buttonActive)
	{
		this(x, y, width, height, fontSize, text, fontType, textIdle, textHover, textActive, buttonIdle, buttonHover, buttonActive);
	
		//Special font parsing
		
	}
	
	public void setTextPos(Graphics2D g2)
	{
		this.textX = body.x + (body.width / 2) - (g2.getFontMetrics().stringWidth(text	) / 2);
		this.textY = body.y + (body.height / 2) + (g2.getFontMetrics().getHeight() / 3);
		
		setTextPos = true;
	}
	
	int debugCount = 0;
	public void update(InputHandler input)
	{	
		currentState = state.idle;
		
		if(body.contains(input.getMousePosition()))
		{
			currentState = state.hover;
			
			if(input.leftClick)
			{
				currentState = state.active;
				
				debounce = false;
			}
			
			if(input.rightClick)
			{
				currentState = state.active;
				debounceR = false;
			}
		}
	}
	
	public void paint(Graphics2D g2)
	{
		g2.setFont(this.font);
		
		if(!setTextPos) setTextPos(g2);
						
		switch(currentState)
		{
		case active:
			g2.setColor(buttonActive);
			g2.fillRect(body.x, body.y, body.width, body.height);
			g2.setColor(textActive);
			g2.drawString(text, textX, textY);
			break;
		case hover:
			g2.setColor(buttonHover);
			g2.fillRect(body.x, body.y, body.width, body.height);
			g2.setColor(textHover);
			g2.drawString(text, textX, textY);
			break;
		case idle:
			g2.setColor(buttonIdle);
			g2.fillRect(body.x, body.y, body.width, body.height);
			g2.setColor(textIdle);
			g2.drawString(text, textX, textY);
			break;
		}
	}
	
	public String toString()
	{
		return "Button \"" + text + "\" -> [x=" + body.x + ", y=" + body.y + ", width=" + body.width + ", height=" + body.height + 
				"]\n Text Position [x=" + textX + ", y=" + textY + "]";
	}
}
