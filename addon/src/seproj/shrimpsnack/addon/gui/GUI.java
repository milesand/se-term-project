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
import seproj.shrimpsnack.addon.sim.SocketSIMConnection;
import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.Pair;

public class GUI extends JFrame {
	
	public static final int SCREEN_WIDTH = 880;
	public static final int SCREEN_HEIGHT = 1000;
	
	private AddOn addon;
	
	private Image screenImage;
	private Graphics screenGraphic;

	private Image background = new ImageIcon(GUI.class.getResource("/image/background.png")).getImage();

	private Image robot;
	private Image robotUp = new ImageIcon(GUI.class.getResource("/image/robotUp.png")).getImage();
	private Image robotDown = new ImageIcon(GUI.class.getResource("/image/robotDown.png")).getImage();
	private Image robotLeft = new ImageIcon(GUI.class.getResource("/image/robotLeft.png")).getImage();
	private Image robotRight = new ImageIcon(GUI.class.getResource("/image/robotRight.png")).getImage();
	private int robotWidth = 100;
	
	private Image hazardImage = new ImageIcon(GUI.class.getResource("/image/hazard.png")).getImage();
	private int hazardWidth = 80;
	
	private Image blobImage = new ImageIcon(GUI.class.getResource("/image/blob.png")).getImage();
	private int blobWidth = 80;
	
	private Image pathImage = new ImageIcon(GUI.class.getResource("/image/path.png")).getImage();
	private int pathWidth = 120;
	
	private Image destinationImage = new ImageIcon(GUI.class.getResource("/image/destination.png")).getImage();
	private int destinationWidth = 80;
	
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
	private int robotX = 46, robotY = 775;
	
	private List<Pair> hazardList = new ArrayList<>();
	private List<Pair> blobList = new ArrayList<>();
	private List<Pair> pathList = new ArrayList<>();
	private List<Pair> destinationList;
	
	
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
		
		robot = robotRight;
		
		// AddOn
		try {
		addon = new AddOn(new SocketSIMConnection("host", 1));
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
				// navigate
				moveRight();
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
		
		// temp function
		// get cell position
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				System.out.println("x :"+x+", y :"+y);
			}
		});
		
		
		hazardList.add(new Pair(20, 60));
		blobList.add(new Pair(135, 60));
		pathList.add(new Pair(135, 175));
		destinationList.add(new Pair(250, 175));
		
		// set map size
		// sim -> mapsize : AddOn.MAP_SIZE
		
		// set start position
		// start position : AddOn.START_POSITION
		robotX = AddOn.START_POSITION.x;
		robotY = AddOn.START_POSITION.y;
		
		// set destinations
		// addon.addDestination(idx, pos), idx : 0 ~ 2
		addon.addDestination(0, new Pair(20, 60));
		addon.addDestination(1, new Pair(120, 160));
		addon.addDestination(2, new Pair(220, 260));
		destinationList = addon.destinationsView();
		
		// set hazards
		// addon.addHazard(pos) // num : 3
		addon.addHazard(new Pair(520, 360));
		hazardList = addon.hazardView();
		
		// map initialize
		// sim이 하는 것? set unknown hazard and blobs
		
		try {
			// while : visit all destinations
			while (addon.navigate() != null) {
				// addon.navigate : plan path + detect blobs + detect hazard + turn + 1 move
				// path를 얻어와 pathList에 add
				// direction을 얻오와 turn(dirction) 
				// moveforward실행
				// detect된 blob을 blobList에 add
				// detect된 hazard를 addon.addHazard() 후 hazardList = addon.hazardView() 수행
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void paint(Graphics g) {
		screenImage = createImage(GUI.SCREEN_WIDTH, GUI.SCREEN_HEIGHT); // create image by the screen size
		screenGraphic = screenImage.getGraphics();
		screenDraw((Graphics2D) screenGraphic); // Graphics2D is needed to printString nicely on the screen (otherwise it cracks)
		g.drawImage(screenImage, 0, 0, null); // draw screenImage at point (0,0)
	}
	
	// double buffering
	public void screenDraw(Graphics2D g) {
		g.drawImage(background, 0, 40, null); //generally, we use drawImage to draw moving images
		g.drawImage(robot,robotX, robotY, null);
		
		for (Pair destination : destinationList) {
			g.drawImage(destinationImage, destination.x, destination.y, null);
		}
		
		for (Pair hazard : hazardList) {
			g.drawImage(hazardImage, hazard.x, hazard.y, null);
		}
		
		for (Pair blob : blobList) {
			g.drawImage(blobImage, blob.x, blob.y, null);
		}		
		
		for (Pair path : pathList) {
			g.drawImage(pathImage, path.x, path.y, null);
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
	
	// move robot
	private void moveLeft() { 
		if(robotX > 110) {
			robotX -= 99;
			robot = robotLeft;
		}
	}
	private void moveRight() { 
		if(robotX < 770) {
			robotX += 99;
			robot = robotRight;
		}
	}
	private void moveUp() { 
		if(robotY > 120) {
			robotY -= 99;
			robot = robotUp;
		}
	}
	private void moveDown() {
		if(robotY < 730) {
			robotY += 99;
			robot = robotDown;
		}
	}
	
	private void turn(Direction d) {
		switch(d) {
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
	
	private void moveForward() {
		if(robot.equals(robotUp)) {
			moveUp();
		}
		else if(robot.equals(robotDown)) {
			moveDown();
		}
		else if(robot.equals(robotLeft)) {
			moveLeft();
		}
		else if(robot.equals(robotRight)) {
			moveRight();
		}
	}
	

	// main
	public static void main(String[] args) {
		new GUI();
	}
}

