import java.awt.Color;
import java.awt.event.KeyEvent;

public class MandelbrotVisualizer {
	
	//Visualizer information given in complex plane coordinates:
	private double visualizerWidth;
	private double visualizerHeight;
	private ComplexNumber visualizerCenter;
	//image information in pixels:
	//Note: these pixels aren't nessesaraly screen pixels, they can be bigger to
	//have a lower quality but faster loading image.
	private int width;
	private int height;
	//Color table allowing us to draw the result:
	private Color[][] colorTable;
	//Color scheme stored in the enum Option
	private MandelbrotVisualizer.Option option;
	//The limit at which we assume the passed complex number is part of the
	//mandelbrot set, increase for better presision but worse performance.
	private int escapeSpeedMaximum;
	//The screen that holds this mandelbrot visualizer.
	private DoubleBuff screen;
	//Top left point of where to draw the visualizer from
	private Coord2D topLeft;
	//How many screen pixels a visualizer pixel takes (pixelFactor*pixelFactor)
	private int pixelFactor;
	//Amount the center moves by in function of zoom
	private double currentMoveValue;
	private final double moveValueAt8 = 0.2;
	//Amout zoom changes by in function of zoom
	private double currentZoomValue;
	private final double zoomValueAt8 = 1;
	
	public MandelbrotVisualizer(int width, int height, MandelbrotVisualizer.Option option, DoubleBuff screen, int pixelFactor) {
		this.visualizerWidth = 4.0;
		this.visualizerHeight = 4.0;
		this.visualizerCenter = new ComplexNumber();
		this.width = width;
		this.height = height;
		this.option = option;
		this.screen = screen;
		this.pixelFactor = pixelFactor;
		colorTable = new Color[width][height];
		updateMoveValue();
		updateZoomValue();
		
		screen.makeKeyHoldAvailable(KeyEvent.VK_I);
		screen.makeKeyHoldAvailable(KeyEvent.VK_O);
		screen.makeKeyHoldAvailable(KeyEvent.VK_LEFT);
		screen.makeKeyHoldAvailable(KeyEvent.VK_RIGHT);
		screen.makeKeyHoldAvailable(KeyEvent.VK_UP);
		screen.makeKeyHoldAvailable(KeyEvent.VK_DOWN);
		
		//Automated attributes in this constrctor:
		escapeSpeedMaximum = 100;
		topLeft = new Coord2D(0,0);
		
		updateColorTable();
		updateRectangles();
	}
	
	//updates move value
	private void updateMoveValue() {
		currentMoveValue = (visualizerWidth*moveValueAt8)/8.0;
	}
	//updates move value
	private void updateZoomValue() {
		currentZoomValue = (visualizerWidth*zoomValueAt8)/8.0;
	}
	/*
	 * general update (for zoom and moving about functionalitys.
	 */
	public void update() {
		boolean changed = false;
		if(screen.isKeyHeld(KeyEvent.VK_UP)) {
			visualizerCenter.imgPart += currentMoveValue;
			changed = true;
		}
		if(screen.isKeyHeld(KeyEvent.VK_DOWN)) {
			visualizerCenter.imgPart -= currentMoveValue;
			changed = true;
		}
		if(screen.isKeyHeld(KeyEvent.VK_LEFT)) {
			visualizerCenter.realPart -= currentMoveValue;
			changed = true;
		}
		if(screen.isKeyHeld(KeyEvent.VK_RIGHT)) {
			visualizerCenter.realPart += currentMoveValue;
			changed = true;
		}
		if(screen.isKeyHeld(KeyEvent.VK_I)) {
			this.visualizerWidth -= currentZoomValue;
			this.visualizerHeight -= currentZoomValue;
			updateMoveValue();
			updateZoomValue();
			changed = true;
		}
		if(screen.isKeyHeld(KeyEvent.VK_O)) {
			this.visualizerWidth += currentZoomValue;
			this.visualizerHeight += currentZoomValue;
			updateMoveValue();
			updateZoomValue();
			changed = true;
		}
		if(changed) {
			updateColorTable();
			updateRectangles();
		}
	}
	/*
	 * updates the color table.
	 */
	private void updateColorTable() {
		for(int j=0; j<height; j++) {
			for(int i=0; i<width; i++) {
				ComplexNumber current = pixelToComplexNumber(new Coord2D(i, j));
				int escapeSpeed = fetchEscapeSpeed(current);
				int colorQuantity = (escapeSpeed*255)/escapeSpeedMaximum;
				//int colorQuantity = 200;
				switch(option) {
				case BLUE:
					colorTable[i][j] = new Color(0,0, colorQuantity);
					break;
				case RED:
					colorTable[i][j] = new Color(colorQuantity,0, 0);
					break;
				case GREEN:
					colorTable[i][j] = new Color(0,colorQuantity, 0);
					break;
				}
				
			}
		}
	}
	/*
	 * updates the visualizer rectangles to the screen starting from screen pixel topLeft, and with each visulizer pixel
	 * taking up pixelFactor*pixelFactor screen pixels.
	 */
	private void updateRectangles() {
		Coord2D current = new Coord2D(topLeft.gX(), topLeft.gY());
		for(int j=0; j<height; j++) {
			for(int i=0; i<width; i++) {
				screen.recs.add(new DoubleBuff.Rec(current.gX(), current.gY(), pixelFactor, pixelFactor, true, colorTable[i][j]));
				current.setX(current.gX()+pixelFactor);
			}
			current.setY(current.gY()+pixelFactor);
			current.setX(topLeft.gX());
		}
	}
	//sais how many steps it takes to escape the dark side of the mandelbrot set:
	// (z*z + c) -> z 
	private int fetchEscapeSpeed(ComplexNumber c) {
		ComplexNumber z = new ComplexNumber();
		int counter = 0;
		z = z.times(z).add(c);
		while(z.getModule()<=2 && counter<escapeSpeedMaximum) {
			counter++;
			z = z.times(z).add(c);
		}
		return counter;
	}
	/*
	 * returns the complex number assosiated with the passed pixel
	 */
	private ComplexNumber pixelToComplexNumber(Coord2D pixel) {
		double real = ( ((double)pixel.gX())  * visualizerWidth) / ((double)width);
		real -= visualizerWidth/2;
		real += visualizerCenter.realPart;
		double img =-1.0 * ( ( ((double)pixel.gY())  * visualizerHeight) / ((double)height) );
		img += visualizerHeight/2;
		img += visualizerCenter.imgPart;
		return new ComplexNumber(real, img);
	}
	
	public static enum Option{
		BLUE, GREEN, RED;
	}
	
	public static void main(String[] args) {
		DoubleBuff testScreen = new DoubleBuff(300, 300, "Mandelstrumen", Color.WHITE);
		MandelbrotVisualizer test = new MandelbrotVisualizer(150, 150, MandelbrotVisualizer.Option.RED, testScreen, 2);
		while(true) {
			test.update();
			testScreen.run();
		}
	}
	

}
