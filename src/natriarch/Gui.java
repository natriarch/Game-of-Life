package natriarch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

public class Gui extends JPanel implements MouseInputListener, ActionListener, ChangeListener{
	private Board gameBoard;
	
	private InputMap im;
	private ActionMap am;
	
	private JFrame frame; 
	private JFrame optionFrame;
	
	private JTabbedPane tabbedPane;
	
	private JPanel dimensionPane;
	private JTextField widthEntry;
	private JTextField heightEntry;
	private JButton applyOptions;
	
	private JButton runButton;
	private JButton pauseButton;
	private JButton resetButton;
	
	private JPanel colorPane;
	private JColorChooser colorChooser;
	
	private JSlider slider; 
	
	private Color cellColor;
	

	
	public Gui(Board b) {
		gameBoard = b; 
		initControls();
		initGUI(gameBoard.getWidth(), gameBoard.getHeight()); 
		cellColor = new Color(51, 255, 153, 255);
	}
	
	public void initControls() {
		im = this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		am = this.getActionMap();
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "R");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "SPACE");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "S");
		im.put((KeyStroke.getKeyStroke(KeyEvent.VK_L, 0)), "L");
		
		am.put("R", new KeyAction("R"));
		am.put("SPACE", new KeyAction("SPACE"));
		am.put("S", new KeyAction("S"));
		am.put("L", new KeyAction("L"));
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	//Initialize GUI with the proper board dimensions as parameters (in units of cells)
	public void initGUI(int x, int y) {
		frame = new JFrame();
		this.setSize(x*10+4, y*10+67);
		this.setBackground(Color.BLACK);
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu mainMenu = new JMenu("Game of Life");
		
		runButton = new JButton("Run");
		runButton.setActionCommand("run simulation");
		runButton.addActionListener(this);
		
		pauseButton = new JButton("Pause");
		pauseButton.setActionCommand("pause simulation");
		pauseButton.addActionListener(this);
		
		resetButton = new JButton("Reset");
		resetButton.setActionCommand("reset board");
		resetButton.addActionListener(this);
		
		slider = new JSlider(1, 501, 250);
		slider.addChangeListener(this);
		
		JLabel sliderLabel = new JLabel("Speed");
		
		JMenuItem runSim = new JMenuItem("Start simulation");
		runSim.setActionCommand("run simulation");
		runSim.addActionListener(this);
		runSim.setMnemonic(KeyEvent.VK_S);
		
		JMenuItem pauseSim = new JMenuItem("Pause simulation");
		pauseSim.setActionCommand("pause simulation");
		pauseSim.addActionListener(this);
		pauseSim.setMnemonic(KeyEvent.VK_P);

		JMenuItem resetBoard = new JMenuItem("Reset the board");
		resetBoard.setActionCommand("reset board");
		resetBoard.addActionListener(this);
		resetBoard.setMnemonic(KeyEvent.VK_R);
		
		JMenuItem help = new JMenuItem("Help");
		help.setActionCommand("showHelp");
		help.addActionListener(this);
		help.setMnemonic(KeyEvent.VK_H);
		
		mainMenu.add(runSim);
		mainMenu.add(pauseSim);
		mainMenu.add(resetBoard);
		mainMenu.add(help);
				
		menuBar.add(mainMenu);
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		
		JPanel optionPane = new JPanel();
		optionPane.setLayout(new BoxLayout(optionPane, BoxLayout.PAGE_AXIS));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0,3));
		buttonPanel.add(runButton);
		buttonPanel.add(pauseButton);
		buttonPanel.add(resetButton);
		
		JPanel otherOptionPanel = new JPanel();
		otherOptionPanel.setLayout(new FlowLayout()); 
		otherOptionPanel.add(sliderLabel);
		otherOptionPanel.add(slider);
		
		optionPane.add(buttonPanel);
		optionPane.add(otherOptionPanel);
		
		frame = new JFrame("Game of Life");
		frame.setContentPane(content);
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(this);
		frame.getContentPane().add(optionPane, BorderLayout.SOUTH);
		frame.setSize(x*10+4, y*10+107);
		frame.setFocusable(true);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		optionFrame = new JFrame();
		
		tabbedPane = new JTabbedPane();
		
		dimensionPane = new JPanel();
		widthEntry = new JTextField("" + gameBoard.getWidth(), 3);
		heightEntry = new JTextField("" + gameBoard.getHeight(), 3);
		applyOptions = new JButton("Apply");
		applyOptions.setActionCommand("apply options");
		applyOptions.addActionListener(this);
		dimensionPane.add(widthEntry);
		dimensionPane.add(heightEntry);
		dimensionPane.add(applyOptions);
		
		tabbedPane.add("Board Dimensions", dimensionPane);
		
		colorPane = new JPanel();
		tabbedPane.add("Colors", colorPane);
		
		colorChooser = new JColorChooser();
		tabbedPane.add("TEST", colorChooser);
		optionFrame.getContentPane().add(tabbedPane);
		optionFrame.pack();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (!gameBoard.isReady()) {
			int x = (e.getX() / 10);
			int y = (e.getY() / 10);
			System.out.println(x + ", " + y);
			Cell c = gameBoard.getCell(e.getX() / 10, e.getY() / 10);
			c.printCoords();
			if (e.getButton() == e.BUTTON1 && c.getIsAlive() == false) {
				c.setIsAlive(true);
			}
			else if (e.getButton() == e.BUTTON3 && c.getIsAlive() == true) {
				c.setIsAlive(false);	
			}
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("what a drag...");
		if (!gameBoard.isReady()) {
			int x = (e.getX() / 10);
			int y = (e.getY() / 10);
			System.out.println(x + ", " + y);
			Cell c = gameBoard.getCell(e.getX() / 10, e.getY() / 10);
			c.printCoords();
			if (SwingUtilities.isLeftMouseButton(e) && c.getIsAlive() == false) {
				c.setIsAlive(true);
			}
			else if ((SwingUtilities.isRightMouseButton(e) && c.getIsAlive() == true)) {
				c.setIsAlive(false);	
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("pause simulation")) {
			gameBoard.setReady(false);
		}
		else if(e.getActionCommand().equals("run simulation")) {
			gameBoard.setReady(true);
		}
		else if(e.getActionCommand().equals("reset board")) {
			gameBoard.setReady(false);
			gameBoard.reset(); 
		}
		else if (e.getActionCommand().equals("showHelp")) {
			JFrame helpFrame = new JFrame("Game of Life - Help");
			JTextArea helpTextArea = new JTextArea("\n   Left-click to give life to a cell. Left-click dragging paints cells." + 
					"\n\n   Right-click to destroy living cells. Right-click dragging deletes cells" + 
					"\n\n   Press the 'R' key to run the simulation. Press it again to pause." + 
					"\n\n   Press the spacebar to reset the board.",
					10,40);
			helpTextArea.setFont(new Font(Font.SERIF, Font.PLAIN, 16));
			helpTextArea.setEditable(false);
			helpFrame.getContentPane().add(helpTextArea);
			helpFrame.pack();
			helpFrame.setVisible(true); 
		}
		else if(e.getActionCommand().equals("showOptions")) {
			optionFrame.setVisible(true);
		}
		
		else if(e.getActionCommand().equals("apply options")) {
			gameBoard.reset(Integer.parseInt(widthEntry.getText()), Integer.parseInt(heightEntry.getText()));
			frame.setSize(gameBoard.getWidth()*10+4, gameBoard.getHeight()*10+49);
			repaint();
			System.out.println("working!");
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		JSlider source = (JSlider)e.getSource();
		gameBoard.setSimSpeed(Math.abs((int)source.getValue() - 500));
		
	}
	
	public class KeyAction extends AbstractAction {
		private String command;
		public KeyAction(String cmd) {
			this.command = cmd;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			// TODO Auto-generated method stub
			if (command.equalsIgnoreCase("R") && !gameBoard.isReady()) {
				gameBoard.setReady(true);
				System.out.println("READY");
			}
			else if (command.equalsIgnoreCase("R") && gameBoard.isReady()) {
				gameBoard.setReady(false);
				System.out.println("Stopping");
			}
			else if (command.equalsIgnoreCase("SPACE")){
				//reset
				gameBoard.setReady(false);
				gameBoard.reset();
			}
			else if (command.equalsIgnoreCase("S")){
				gameBoard.setReady(false);
				gameBoard.advanceGen();
			}			
		}
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		super.paintComponent(g2d);
		for (int i = 0; i < gameBoard.getWidth(); i++){
			for (int j = 0; j < gameBoard.getHeight(); j++){
				if (gameBoard.getCell(i,j).getIsAlive()) {
					Color c = cellColor;
					if (gameBoard.getCell(i, j).getAge()*30 > 255){
						c = new Color(cellColor.getRed(), cellColor.getGreen(), cellColor.getBlue(), 50);
					}
					else {
						c = new Color(cellColor.getRed(),
								cellColor.getGreen()-gameBoard.getCell(i, j).getAge()*30,
								cellColor.getBlue(), 
								cellColor.getAlpha());
					}
					g2d.setColor(c);
					g2d.fillRect(gameBoard.getCell(i, j).getX() * 10, gameBoard.getCell(i, j).getY()*10, 8, 8);
				}
			}
		}
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void mouseEntered(MouseEvent e) {

	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
