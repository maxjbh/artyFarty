import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class DoubleBuff extends Canvas{
	// create a frame to contain our game
	protected JFrame container;
			
	protected JPanel panel;
	
	public BufferStrategy strategy;
	
	public ArrayList<Rec> recs;
	public ArrayList<Line> lines;
	public ArrayList<Elipse> elipses;
	public ArrayList<Entity> entitys;
	
	private HashMap<Integer,Boolean> keyActivityMap;
	
	protected int size_x;
	protected int size_y;
	
	private Color backColor;
	
	public DoubleBuff(int size_x, int size_y, String name, Color back) {
		container = new JFrame(name);
		panel = (JPanel) container.getContentPane();
		this.size_x = size_x;
		this.size_y = size_y;
		this.backColor = back;
		panel.setPreferredSize(new Dimension(size_x, size_y));
		panel.setLayout(null);
		setBounds(0,0,size_x,size_y);
		container.setSize(this.size_x, this.size_y);
		panel.add(this);
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);
		// finally make the window visible 
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		// create the buffering strategy which will allow AWT
		// to manage our accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		addKeyListener(new KeyInputHandler());
		setBackground(backColor);
		
		keyActivityMap = new HashMap<Integer,Boolean>();
		
		recs = new ArrayList<Rec>();
		lines = new ArrayList<Line>();
		elipses = new ArrayList<Elipse>();
		entitys = new ArrayList<Entity>();
		
		recs.add(new Rec(0, 0, size_x, size_y, true, backColor));
	}
	public void setVisible(boolean vis) {
		container.setVisible(vis);
	}
	
	//put this inside of a while loop in the main.
	public void run() {
		
		//setOpaque(true);
		//setBackground(backColor);
		draw();
		strategy.show();
		try { Thread.sleep(10); } catch (Exception e) {}
		
	}
	//Draw stuff here
	protected void draw() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		
		g.setBackground(backColor);
		
		//WARNING: always do recs first because i don't know how to properly clear the fucking screen!
		g.setColor(Color.black);
		for(Rec r:recs) {
			g.setColor(r.color);
			//g.setPaintMode();
			if(r.filled)
				g.fillRect(r.x, r.y, r.width, r.height);
			else
				g.drawRect(r.x, r.y, r.width, r.height);
		}
		g.setColor(Color.black);
		for(Elipse e:elipses) {
			g.setColor(e.color);
			//g.setPaintMode();
			if(e.filled)
				g.fillOval(e.x, e.y, e.width, e.height);
			else
				g.drawOval(e.x, e.y, e.width, e.height);
		}
		g.setColor(Color.black);
		for(Line l:lines) {
			g.setColor(l.color);
			//g.setPaintMode();
			g.drawLine(l.x, l.y, l.x2, l.y2);
		}
		
		for(Entity entity:entitys) {
			g.setColor(entity.color);
			for(Rec r:entity.recs) {
				if(r.filled)
					g.fillRect(r.x, r.y, r.width, r.height);
				else
					g.drawRect(r.x, r.y, r.width, r.height);
			}
			for(Elipse e:entity.elipses) {
				if(e.filled)
					g.fillOval(e.x, e.y, e.width, e.height);
				else
					g.drawOval(e.x, e.y, e.width, e.height);
			}
			for(Line l:entity.lines) {
				g.drawLine(l.x, l.y, l.x2, l.y2);
			}
		}
		g.dispose();
	}
	
	public int getSizeX() {
		return this.size_x;
	}
	public int getSizeY() {
		return this.size_y;
	}
	//Drawable objects:
	public static class Entity{
		public ArrayList<Rec> recs;
		public ArrayList<Line> lines;
		public ArrayList<Elipse> elipses;
		public Color color;
		public Entity(ArrayList<Rec> recs, ArrayList<Line> lines, ArrayList<Elipse> elipses, Color color) {
			this.recs = recs;
			this.lines = lines;
			this.elipses = elipses;
			this.color = color;
		}
		public void update(ArrayList<Rec> recs, ArrayList<Line> lines, ArrayList<Elipse> elipses, Color color) {
			this.recs = recs;
			this.lines = lines;
			this.elipses = elipses;
			this.color = color;
		}
	}
	public static class Rec {
		public int x;
		public int y;
		public int width;
		public int height;
		public Color color;
		public boolean filled;
		
		public Rec(int x, int y, int width, int height, boolean filled, Color color) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.color = color;
			this.filled = filled;
		}
		
	}
	public static class Elipse {
		//x and y are coordinates of top left
		public int x;
		public int y;
		public int width;
		public int height;
		public Color color;
		public boolean filled;
		
		public Elipse(int topLeftX, int topLeftY, int width, int height, boolean filled, Color color) {
			this.x = topLeftX;
			this.y = topLeftY;
			this.width = width;
			this.height = height;
			this.color = color;
			this.filled = filled;
		}
		public Elipse(int x, int y, int width, int height, boolean filled, Color color, boolean fromCenter) {
			this.width = width;
			this.height = height;
			this.color = color;
			this.filled = filled;
			if(!fromCenter) {
				this.x = x;
				this.y = y;
			}
			else {
				this.x = x - width/2;
				this.y = y - height/2;
			}
		}
		
	}
	public static class Line {
		public int x;
		public int y;
		public int x2;
		public int y2;
		public Color color;
		
		public Line(int x, int y, int x2, int y2, Color color) {
			this.x = x;
			this.y = y;
			this.x2 = x2;
			this.y2 = y2;
			this.color = color;
		}
		
	}
	//Keyboard crap
	boolean leftPressed = false;
	boolean rightPressed = false;
	boolean pPressed = false;
	boolean rPressed = false;
	//make this key available for holding, WARNING isKeyHeld will not work if this is not done.
	public void makeKeyHoldAvailable(Integer i) {
		keyActivityMap.put(i, false);
	}
	//sais if the key assosiated with the keycode i is being held down.
	public boolean isKeyHeld(Integer i) {
		if(keyActivityMap.containsKey(i))
			return keyActivityMap.get(i);
		else
			return false;
	}
	private class KeyInputHandler extends KeyAdapter {
		
		public void keyPressed(KeyEvent e) {
			
			/*
			if (e.getKeyCode() == KeyEvent.VK_UP {
				leftPressed = true;
			}
			*/
			
			for(Integer i : keyActivityMap.keySet()) {
				if(e.getKeyCode() == i) {
					keyActivityMap.put(i, true);
				}
			}
		} 
			
			
		public void keyReleased(KeyEvent e) {			
			/*example:
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			}
			*/
			for(Integer i : keyActivityMap.keySet()) {
				if(e.getKeyCode() == i) {
					keyActivityMap.put(i, false);
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_P) {
				pPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_R) {
				rPressed = true;
			}
		}
		public void keyTyped(KeyEvent e) {
			// if we hit escape, then quit the game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			}
			 
		}
	}
	

}

