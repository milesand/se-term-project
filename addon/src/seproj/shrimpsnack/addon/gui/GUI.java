package seproj.shrimpsnack.addon.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import seproj.shrimpsnack.addon.addon.AddOn;
import seproj.shrimpsnack.addon.map.MapView;
import seproj.shrimpsnack.addon.sim.SIMConnection;
import seproj.shrimpsnack.addon.sim.SocketSIMConnection;
import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.OptionalBool;
import seproj.shrimpsnack.addon.utility.Pair;

public class GUI extends JFrame {
	
	public static final int SCREEN_WIDTH = 880;
	public static final int SCREEN_HEIGHT = 1000;
	
	public static final Pair START_POSITION = new Pair(0, 0); 
	
	private AddOn addon;
	private SIMConnection sim;
	
	private Image screenImage;
	private Graphics screenGraphic;

	private Image background = new ImageIcon(GUI.class.getResource("/image/background.png")).getImage();

	private Image robot;
	private Image robotUp = new ImageIcon(GUI.class.getResource("/image/robotUp.png")).getImage();
	private Image robotDown = new ImageIcon(GUI.class.getResource("/image/robotDown.png")).getImage();
	private Image robotLeft = new ImageIcon(GUI.class.getResource("/image/robotLeft.png")).getImage();
	private Image robotRight = new ImageIcon(GUI.class.getResource("/image/robotRight.png")).getImage();
	
	private Image hazardImage = new ImageIcon(GUI.class.getResource("/image/hazard.png")).getImage();
	
	private Image blobImage = new ImageIcon(GUI.class.getResource("/image/blob.png")).getImage();
	
	private Image pathImage = new ImageIcon(GUI.class.getResource("/image/path.png")).getImage();
	
	private Image destinationImage = new ImageIcon(GUI.class.getResource("/image/destination.png")).getImage();
	
	private Image finishImage = new ImageIcon(GUI.class.getResource("/image/finish.png")).getImage();
	
	private JLabel menuBar = new JLabel(new ImageIcon(GUI.class.getResource("/image/bar.png")));

	//Buttons	
	//Exit Button
	private ImageIcon exitButtonImage = new ImageIcon(GUI.class.getResource("/image/exitButton.png"));
	private JButton exitButton = new JButton(exitButtonImage); //basic default button is exitButtonBasicImage
	
	//Move Button
	private ImageIcon moveButtonImage = new ImageIcon(GUI.class.getResource("/image/moveButton.png"));
	private JButton moveButton = new JButton(moveButtonImage);
	
	//Make the screen move when we drag menu bar
	private int mouseX, mouseY;
	private boolean isFinish = false;
	
	private List<Pair> pathList = new ArrayList<>();
	private List<Pair> destinationList = new ArrayList<>();
	
	private MapView mv = null;
	
	
	public GUI() {	
		setUndecorated(true); // when first executed, menubar doesn't show
		setTitle("ADD-ON"); // the name of our game becomes "Dynamic Beat"
		setSize(GUI.SCREEN_WIDTH, GUI.SCREEN_HEIGHT);
		setResizable(false); // user cannot redefine the screen size
		setLocationRelativeTo(null); // when you run the project, the screen will appear right on the centre of the screen
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // need to declare this; otherwise the program continues to run in computer even after we close screen
		setVisible(true); // make the screen visible
		setBackground(new Color(0, 0, 0, 0)); // paintcomponent changes to white
		setLayout(null); // button and layout gets located right on spot we declared
		
		robot = robotUp;
		
//		 AddOn
		try {
			sim = new SocketSIMConnection.Builder("localhost", 3000)
					.mapSize(new Pair(8, 8))
					.robotPosition(START_POSITION)
					.robotDirection(Direction.N)
					.addHazard(new Pair(3, 2))
					.addHazard(new Pair(6, 7))
					.addHazard(new Pair(0, 5))
					.addHazard(new Pair(1, 4))
					.addBlob(new Pair(1, 1))
					.addBlob(new Pair(3, 4))
					.addBlob(new Pair(5, 4))
					.build();
			addon = new AddOn(sim);
		} catch(UnknownHostException uhe) {
			uhe.printStackTrace();
		} catch(IOException ie) {
			ie.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		// Buttons
		exitButton.setBounds(840, 0, 40, 40); //put exit button on the rightmost side of the menu bar
		exitButton.setBorderPainted(false); //need to set JButton so that it fits our button image
		exitButton.setContentAreaFilled(false);
		exitButton.setFocusPainted(false);
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		add(exitButton);
		
		moveButton.setBounds(400, 920, 80, 40);
		moveButton.setBorderPainted(false);
		moveButton.setContentAreaFilled(false);
		moveButton.setFocusPainted(false);
		moveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {	
				try {
					pathList = addon.navigate();
					if(pathList.isEmpty()) {
						isFinish = true;
					}
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});
		add(moveButton);
		
		// bar
		menuBar.setBounds(0, 0, 880, 40); // declares position and size of menubar
		menuBar.addMouseListener(new MouseAdapter() {
//			@Override
			public void mousePressed(MouseEvent e) { //when a mouse event occurs, retrieve the x and y coordinates of the mouse
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
		
		menuBar.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) { // whenever mouse is dragged, get x,y position of the mouse and move the screen accordingly
				int x = e.getXOnScreen(); 
				int y = e.getYOnScreen();
				setLocation(x-mouseX, y-mouseY);
			}
		});
		add(menuBar); // adds menubar to jframe
		
		// set destinations
		addon.addDestination(0, new Pair(1, 2));
		addon.addDestination(1, new Pair(2, 5));
		addon.addDestination(2, new Pair(7, 7));
		destinationList = addon.destinationsView();
		
		// set hazards
		addon.addHazard(new Pair(3, 2));
		addon.addHazard(new Pair(6, 7));
		addon.addHazard(new Pair(0, 5));
		
		mv = addon.mapView();
	}
	
	
	public void paint(Graphics g) {
		screenImage = createImage(GUI.SCREEN_WIDTH, GUI.SCREEN_HEIGHT); // create image by the screen size
		screenGraphic = screenImage.getGraphics();
		try {
			screenDraw((Graphics2D) screenGraphic); // Graphics2D is needed to printString nicely on the screen (otherwise it cracks)
		} catch(Exception e) {
			e.printStackTrace();
		}
		g.drawImage(screenImage, 0, 0, null); // draw screenImage at point (0,0)
	}
	
	// double buffering
	public void screenDraw(Graphics2D g) throws Exception{
		g.drawImage(background, 0, 40, null); //generally, we use drawImage to draw moving images
		if(sim != null)  {
			turn();
			g.drawImage(robot, getCoord(sim.getPosition()).x, getCoord(sim.getPosition()).y, null);
		}
		
		for (Pair destination : destinationList) {
			Pair p = getCoord(destination);
			g.drawImage(destinationImage, p.x, p.y, null);
		}
		

		if (mv != null) {
			for (int i = 0; i < mv.getSize().y; i++) {
				for (int j = 0; j < mv.getSize().x; j++) {
					Pair p = new Pair(i, j);
					// draw hazard
					if (mv.get(p).isHazard().equals(OptionalBool.True)) {
						g.drawImage(hazardImage, getCoord(p).x, getCoord(p).y, null);
					}
					
					//draw blob
					if (mv.get(p).isBlob().equals(OptionalBool.True)) {
						g.drawImage(blobImage, getCoord(p).x, getCoord(p).y, null);
					}
				}
			}
		}
		
		if (pathList != null) {
			for (Pair p : pathList) {
				Pair tp = getCoord(p);
				g.drawImage(pathImage, tp.x, tp.y, null);
			}
		}
		
		if(isFinish) {
			g.drawImage(finishImage, 130, 400, null);
		}
		
		paintComponents(g); // draws the images in the screen image (menubar stays constant; doesn't change. therefore use paintcomponent not drawimage), draws all the "add()" components
		try {
			Thread.sleep(5);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		this.repaint(); 
	}
	
	private void turn() throws Exception {
		switch(sim.getDirection()) {
		case N:
			robot = robotUp;
			break;
		case E :
			robot = robotRight;
			break;
		case W :
			robot = robotLeft;
			break;
		case S :
			robot = robotDown;
			break;
		}
	}
	
	private Pair getCoord(Pair pair) {
		int x = 42 + pair.x*99;
		int y = 777 - pair.y*99;
		return new Pair(x, y);
	}

	// main
	public static void main(String[] args) throws Exception{	
		new GUI();
		
	}
}

