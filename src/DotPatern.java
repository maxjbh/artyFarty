import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Random;

public class DotPatern {

	private int width;
	private int height;
	private int dotQuantity;
	private DoubleBuff screen;
	private Color color;
	Coord2D pivot;
	
	public DotPatern(int width, int height, int dotQuantity,Coord2D pivot, DoubleBuff screen, Color color) {
		this.width = width;
		this.height = height;
		this.dotQuantity = dotQuantity;
		this.screen = screen;
		this.pivot = pivot;
		this.color = color;
		initialise();
	}
	private void initialise() {
		int screenWidth = screen.getSizeX();
		int screenHeight = screen.getSizeY();
		
		ArrayList<DoubleBuff.Rec> recs = new ArrayList<DoubleBuff.Rec>();
		ArrayList<DoubleBuff.Line> lines = new ArrayList<DoubleBuff.Line>();
		ArrayList<DoubleBuff.Elipse> elipses = new ArrayList<DoubleBuff.Elipse>();
		
		Random rand = new Random();
		
		for(int i = 0; i<dotQuantity;i++) {
			int x = rand.nextInt(screenWidth);
			int y = rand.nextInt(screenHeight);
			Coord2D first = new Coord2D(x,y);
			Coord2D rotated = first.rotateAround(pivot, new Angle(20));
			elipses.add(new DoubleBuff.Elipse(x, y, 5, 5, true, color, true) );
			elipses.add(new DoubleBuff.Elipse(rotated.gX(), rotated.gY(), 5, 5, true, color, true) );
		}
		DoubleBuff.Entity  entity = new DoubleBuff.Entity(recs, lines, elipses, color);
		screen.entitys.add(entity);
	}
	
	
	
	public static void main(String[] args) {
		Dimension ScreenDimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int windowWidth = (int)ScreenDimension.getWidth() - 10;
		int windowHeight = (int)ScreenDimension.getHeight() - 90;
		DoubleBuff screen= new DoubleBuff(windowWidth, windowHeight, "ArtyFarty", Color.WHITE);
		
		DotPatern patern = new DotPatern(windowWidth, windowHeight, 4, new Coord2D(50, 50), screen, Color.BLACK);
		
		
		//make window visible.
		screen.setVisible(true);
		
		long oldTime = System.currentTimeMillis();
		long newTime;
		
		
		//Main loop
		while(true) {
			screen.run();
			newTime = System.currentTimeMillis();
			long cycleTime = newTime - oldTime;
			
			oldTime = newTime;
			
		}

	}

}
