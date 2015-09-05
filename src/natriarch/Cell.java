package natriarch;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

//Cell class represents the individual square on the game board.
//They can be alive or dead
public class Cell extends JComponent  {
	private int x;
	private int y;
	private boolean currentState;
	private boolean futureState; //This variable stores the future state of this cell, determined by adjacent cells.
	private int age;
	
	public Cell(int w, int h, boolean b){
		x = w;
		y = h;
		currentState = b;
		age = 0;
	}
	
	public int getAge(){
		return age; 
	}
	
	public void tickAge() {
		age++; 
	}
	
	public void zeroAge() {
		age = 0; 
	}
	
	public int getX(){
		return x; 
	}
	
	public int getY(){
		return y; 
	}
	
	public void setIsAlive(boolean b){
		currentState = b; 
	}
	
	public boolean getIsAlive(){
		return currentState;
	}
	
	public void setFutureState(boolean b){
		futureState = b; 
	}
	
	public boolean getFutureState(){
		return futureState; 
	}
	
	public void printCoords(){
		System.out.println("cell coordinates: "+ x + ", " + y);
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g);
		if (this.currentState == true){
			g2d.setColor(Color.CYAN);
			g2d.fillRect(x * 10, y * 10, 8, 8);
			
		}
	}
	
	
	

}
