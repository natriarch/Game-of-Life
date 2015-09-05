package natriarch;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

//The game board. A grid of cells that are either alive or dead.
public class Board{

	private int height;
	private int width;
	private Cell[][] cells;	
	private boolean ready;
	private int simSpeed; //the rate at which the board advances generations

	//Constructor allows for different board sizes	
	public Board(int x, int y){
		simSpeed = 250;  
		width = x;
		height = y;
		cells = new Cell[width][height];
		ready = false;
		
		//must first populate board completely with 'dead' cells
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Cell(i, j, false);
			}
		}
	}
	
	public Cell getCell(int x, int y) {
		return cells[x][y]; 
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public boolean isReady(){
		return this.ready;
	}
	
	public void setReady(boolean b){
		this.ready = b;
	}
	
	public void setSimSpeed(int x) {
		simSpeed = x; 
	}
	
	public int getSimSpeed() {
		return simSpeed; 
	}
	
	//calculate the future board state by assessing dead and alive cells and their neighbors.
	//at the end, commit to the new board state.
	public void advanceGen(){
		//temp integers used to store for loop positions 
		//due to them being modified for edge conditions
		//(this allows for wrapping around the board)
		int tempX;
		int tempY;
		for (int i = 0; i < width; i ++){
			for (int j = 0; j < height; j++){
				int count = 0;
				for (int k = i-1; k <= i+1; k++){
					tempX = k;
					for (int l = j-1; l <= j+1; l++){
						tempY = l;
						//if the cell in question is off the board, we replace it with an opposite cell on the board
						//allows for wrapping of cells. This is done by altering the value of K and L. 
						//This value must be reset after placing the new cell to resume the loop from the correct location
						if (k < 0) {
							k = width-1;
						}
						if (k >= width) {
							k = 0;
						}
						if (l < 0) {
							l = height-1;	
						}
						if (l >= height) {
							l = 0;
						}
						if (cells[k][l].getIsAlive() == true){
							count++;
						}
						k = tempX;
						l = tempY;
					}
				}
				if (!cells[i][j].getIsAlive()){
					cells[i][j].zeroAge();
				}
				if (cells[i][j].getIsAlive()){
					count--;
				}
				if (cells[i][j].getIsAlive() && count < 2){
					cells[i][j].setFutureState(false);
				}
				else if(!cells[i][j].getIsAlive() && count == 3){
					cells[i][j].setFutureState(true);
				}
				else if (cells[i][j].getIsAlive() && count >= 4){
					cells[i][j].setFutureState(false); 
				}
				else {
					cells[i][j].setFutureState(cells[i][j].getIsAlive());
				}
				if(cells[i][j].getIsAlive() && cells[i][j].getIsAlive() == cells[i][j].getFutureState()){
					cells[i][j].tickAge();
				}
			}
		}

		commitToFuture(); //Switch all cells current values to their future values. 
	}
	
	public void commitToFuture(){
		for (int i = 0; i < width; i++){
			for (int j = 0; j < height; j++){
				cells[i][j].setIsAlive(cells[i][j].getFutureState());
			}
		}
	}
	
	//Clear the board
	public void reset(){
		ready = false;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Cell(i, j, false);
			}
		}
	}
	
	public void reset(int x, int y) {
		ready = false;
		width = x;
		height = y;
		cells = new Cell[width][height]; 
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Cell(i, j, false);
			}
		}
	}
	
	public static void main(String args[]) throws InterruptedException{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e){}
		catch (InstantiationException e){}
		catch (IllegalAccessException e){}
		catch (UnsupportedLookAndFeelException e){}
		
		
		Board gol = new Board(50,50);
		Gui gui = new Gui(gol);
	
		while (true) {
			gui.repaint();
			if (gol.isReady() == true){
				//do the game logic
				gol.advanceGen();
				gui.repaint();
				Thread.sleep(gol.getSimSpeed());
			}
			Thread.sleep(10);
		}
	}
}

	
	