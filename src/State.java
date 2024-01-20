import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class State 
{
	Map<String, Button> buttons = new HashMap<String, Button>();
	protected boolean quit = false;
	
	//Default Colors
	Color dIdleButton = new Color(70, 70, 70, 0);
	Color dHoverButton = new Color(150, 150, 150, 0);
	Color dActiveButton = new Color(70, 70, 70, 0);
	Color dIdleText = new Color(70, 70, 70, 200);
	Color dHoverText	= new Color(250, 250, 250, 250);
	Color dActiveText = new Color(20, 20, 20, 50);
	
	public boolean getQuit()
	{
		return quit;
	}
	
	public Button defaultButton(int x, int y, int width, int height, int fontSize, String text)
	{
		return new Button(x, y, width, height, fontSize, text, "Arial", dIdleText, dHoverText, dActiveText, dIdleButton, dHoverButton, dActiveButton);
	}
	
	public boolean buttonActive(String key)
	{
		return buttons.get(key).currentState == Button.state.active;
	}
	
	public boolean buttonDebounce(String key)
	{
		return !(buttons.get(key).currentState == Button.state.active) && !buttons.get(key).debounce;
	}
	
	public boolean buttonDebounceR(String key)
	{
		return !(buttons.get(key).currentState == Button.state.active)&& !buttons.get(key).debounceR; 
	}
	
	public void resetDebounce(String key)
	{
		buttons.get(key).debounce = true;
	}
	
	public void resetDebounceR(String key)
	{
		buttons.get(key).debounceR = true;
	}
	
	public abstract void setupButtons();
	public abstract void paint(Graphics2D g2);
	public abstract void update(InputHandler input, Stack<State> sm);
	public abstract void endState(Stack<State> sm);
}
