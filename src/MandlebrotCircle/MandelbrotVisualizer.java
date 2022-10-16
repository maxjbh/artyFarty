package MandlebrotCircle;
import java.awt.Color;
import java.awt.event.KeyEvent;

import geometryUtils.ComplexNumber;
import geometryUtils.Coord2D;
import max2DEngine.DoubleBuff;

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
	private MandelbrotVisualizer.ColorOption colorOption;
	private MandelbrotVisualizer.ColorOption colorOption2;
	//The limit at which we assume the passed complex number is part of the
	//mandelbrot set, increase for better presision but worse performance.
	private int escapeSpeedMaximum;
	private int secondHighestEscapeSpeed = 0;
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
	
	public MandelbrotVisualizer(MandelbrotVisualizer.ColorOption colorOption, MandelbrotVisualizer.ColorOption colorOption2, DoubleBuff screen, int pixelFactor) {
		this.visualizerWidth = 4.0;
		this.visualizerHeight = 4.0;
		this.visualizerCenter = new ComplexNumber();
		this.width = screen.getSizeX()/pixelFactor;
		this.height = screen.getSizeY()/pixelFactor;
		this.colorOption = colorOption;
		this.colorOption2 = colorOption2;
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
		escapeSpeedMaximum = 1000;
		topLeft = new Coord2D(0,0);
		
		updateColorTable();
		updateRectangles();
	}
	
	//updates move value, produit en croix VERIFIED on paper
	private void updateMoveValue() {
		currentMoveValue = (visualizerWidth*moveValueAt8)/8.0;
	}
	//updates zoom value , Same proof as for movement (draw a grid)
	private void updateZoomValue() {
		System.out.println("width : " + visualizerWidth);
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
			System.out.println("nextCycle (change) ...");
			
			secondHighestEscapeSpeed = 0;
			//TODO ? add a table of complex numbers, then make a new function that does half of updateColorTable's current job, then change updateColorTable's function 
			updateColorTable();
			updateRectangles();
		}
	}
	/*
	 * updates the color table.
	 * 
	 * in function of escape speed, @see fetchEscapeSpeed() , at maximum escape speed will be fullt colored
	 * 
	 * TODO ? Make it in function of , fully colored for visible pixel with highest or second highest escape speed (replace escapeSpeedMaximum with visibleEscapeSpeedMaximum) 
	 */
	private void updateColorTable() {
		for(int j=0; j<height; j++) {
			for(int i=0; i<width; i++) {
				ComplexNumber current = pixelToComplexNumber(new Coord2D(i, j));
				int escapeSpeed = fetchEscapeSpeed(current);
				//int colorQuantity = (escapeSpeed*255)/escapeSpeedMaximum;
				//if 2 colors:
				int totalColorQuantity = (escapeSpeed*510)/escapeSpeedMaximum;
				int mainColorQuantity = 0;
				int secondaryColorQuantity = 255;
				if(totalColorQuantity >255) {
					mainColorQuantity = totalColorQuantity - 255;
				}else {
					secondaryColorQuantity = totalColorQuantity;
				}
				
				if(colorOption == MandelbrotVisualizer.ColorOption.RED && colorOption2 == MandelbrotVisualizer.ColorOption.BLUE) {
					colorTable[i][j] = new Color(mainColorQuantity,0, secondaryColorQuantity);
				}else {
					System.out.println("Color scheme not handled.");
				}
				/* when there was one color only :
				switch(colorOption) {
				case BLUE:
					colorTable[i][j] = new Color(0,0, colorQuantity);
					break;
				case RED:
					colorTable[i][j] = new Color(colorQuantity,0, 0);
					break;
				case GREEN:
					colorTable[i][j] = new Color(0,colorQuantity, 0);
					break;
				}*/
				
			}
		}
	}
	/*
	 * updates the visualizer pixels to the screen starting from screen pixel topLeft, and with each visulizer pixel
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
	
	public static enum ColorOption{
		BLUE, GREEN, RED;
	}
	
	public static void main(String[] args) {
		//DoubleBuff testScreen = new DoubleBuff(300, 300, "Mandelstrumen", Color.WHITE);
		//MandelbrotVisualizer test = new MandelbrotVisualizer(150, 150, MandelbrotVisualizer.Option.RED, testScreen, 2);
		DoubleBuff testScreen = new DoubleBuff(900, 900, "Mandelstrumen", Color.WHITE);
		MandelbrotVisualizer test = new MandelbrotVisualizer(MandelbrotVisualizer.ColorOption.RED, MandelbrotVisualizer.ColorOption.BLUE, testScreen, 1);
		while(true) {
			test.update();
			testScreen.run();
		}
	}
	

}
