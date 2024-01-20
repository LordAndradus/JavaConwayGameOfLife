import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

public class InputHandler implements KeyListener, MouseListener, MouseWheelListener 
{
	//Supported Keys
	boolean esc = false;
	
	//Mouse position
	public Point mousePos;
	
	//Mouse semaphore flags
	public boolean leftClick, rightClick;
	
	//Mouse wheel scroll units
	public boolean mwScrolled;
	public int mwScrollUnits;
	
	public InputHandler()
	{
		mousePos = new Point();
		updateMousePosition();
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		mwScrolled = true;
		mwScrollUnits += e.getWheelRotation();
	}
	
	public void resetMouseWheel()
	{
		mwScrolled = false;
		mwScrollUnits = 0;
	}

	public void mouseClicked(MouseEvent e) 
	{
		if(SwingUtilities.isLeftMouseButton(e))
		{
			leftClick = false;
		}
		
		if(SwingUtilities.isRightMouseButton(e))
		{
			rightClick = false;
		}
	}

	public void mousePressed(MouseEvent e) 
	{
		if(SwingUtilities.isLeftMouseButton(e))
		{
			leftClick = true;
		}
		
		if(SwingUtilities.isRightMouseButton(e))
		{
			rightClick = true;
		}
	}

	public void mouseReleased(MouseEvent e) 
	{
		if(SwingUtilities.isLeftMouseButton(e))
		{
			leftClick = false;
		}
		
		if(SwingUtilities.isRightMouseButton(e))
		{
			rightClick = false;
		}
	}

	public void mouseEntered(MouseEvent e) 
	{
		
	}

	public void mouseExited(MouseEvent e) 
	{
		
		
	}

	public void keyTyped(KeyEvent e) 
	{
		
		
	}

	public void keyPressed(KeyEvent e) 
	{
		
		
	}

	public void keyReleased(KeyEvent e) 
	{
		
		
	}
	
	
	public void updateMousePosition()
	{
		try
		{
			mousePos.x = MouseInfo.getPointerInfo().getLocation().x;
			mousePos.y = MouseInfo.getPointerInfo().getLocation().y;
		}
		catch(Exception e)
		{
			System.err.println("Mouse untraceable, defaulting to (0, 0)");
			
			mousePos.x = 0;
			mousePos.y = 0;
		}
	}
	
	public Point getMousePosition()
	{
		return mousePos;
	}
}
