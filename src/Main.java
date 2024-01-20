/*
 * CS 3443-003 | Applications Programming
 * Spring 2024, Assignment 1
 * Written by: Jo-michael Paul Wallace, cfx628
 */

import javax.swing.JFrame;

public class Main 
{
	public static JFrame window;
	public static GUI gui;
	
	public static void main(String[] args)
	{
		
		
		while(true)
		{
			window = new JFrame();
			gui = new GUI();
			
			window.getContentPane();
			window.setResizable(false);
			window.setTitle("Assignment 1 - cfx628");
			
			window.add(gui);
			window.pack();
			
			window.setLocationRelativeTo(null);
			window.setVisible(true);
			
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			gui.startThread();
			
			break;
		}
	}
	
	/*
	 * Again, as the name implies, it prints out the coordinate pairs of active cells.
	 */
	private static void printActiveCells(int[][] arr)
	{
		for(int i = 0; i < arr.length; i++)
		{
			println("(" + arr[i][0] + ", " + arr[i][1] + ")");
		}
		
		println("");
	}
	
	/*
	 * Helper functions so I can be lazy and not put "System.out" every-single time.
	 * Please read a more complete the comments inside the "GameOfLife.java" class for a more in-depth explanation.
	 */
	private static void println(Object x)
	{
		System.out.println(x);
	}
	
	private static void print(Object x)
	{
		System.out.print(x);
	}
}
