package MandlebrotCircle;
import java.awt.Color;
import java.util.ArrayList;

import geometryUtils.Angle;
import geometryUtils.Coord2D;
import geometryUtils.Angle.Unit;
import max2DEngine.DoubleBuff;
import max2DEngine.DoubleBuff.Elipse;
import max2DEngine.DoubleBuff.Entity;
import max2DEngine.DoubleBuff.Line;
import max2DEngine.DoubleBuff.Rec;



public class MandlebrotCircle {
	
	/*
	 * Class allowing creation and drawing of the multiplication circle thing.
	 * Has nothing to do with Mandlebrot zoom but named it in honor of that as they are highly linked mathmatically.
	 */
	
	int diameter;
	Coord2D center;
	int segments;
	double factor;
	Coord2D[] coords;
	DoubleBuff screen;
	Color color;
	private DoubleBuff.Entity entity;
	
	
	private long startTime;
	
	private boolean automode;
	private boolean firstChange = true;
	private boolean secondChange = true;
	private boolean thirdChange = true;
	private boolean fourthChange = true;
	
	private boolean pausePattern;
	private double paternSpeed;
	
	private ArrayList<DoubleBuff.Line> lines;
	private ArrayList<DoubleBuff.Elipse> outline;
	
	public MandlebrotCircle(int diameter, Coord2D center, int segmentsAtStart, double factorAtStart, DoubleBuff screen, Color color) {
		startTime = System.currentTimeMillis();
		automode = true;
		paternSpeed = 0.0045;
		pausePattern = false;
		anim1 = false;
		futorColor = null;
		this.diameter = diameter;
		this.center = center;
		this.segments = segmentsAtStart;
		this.factor = factorAtStart;
		this.screen = screen;
		this.color = color;
		lines = new ArrayList<DoubleBuff.Line>();
		outline = new ArrayList<DoubleBuff.Elipse>();
		entity = new DoubleBuff.Entity(new ArrayList<DoubleBuff.Rec>(), lines, outline, color);
		screen.entitys.add(entity);
		update(0);
	}
	//Updates the entity thats stored in the screen.
	public void update(long cycleTime) {
		
		lines = new ArrayList<DoubleBuff.Line>();
		outline = new ArrayList<DoubleBuff.Elipse>();
		
		//Anims
		if(anim1)
			advanceAnim1(cycleTime, futorColor);
		if(automode)
			colorCycle();
		if(!pausePattern) {
			double temp = factor + paternSpeed;
			if(temp>0)
				factor += paternSpeed;
		}
		//Put a cercle in the center.
		outline.add(new DoubleBuff.Elipse(center.gX(), center.gY(), diameter, diameter, false, color, true));
				
		//Calculate all the needed points.
		coords = new Coord2D[segments];
		Angle alpha = new Angle(Angle.Unit.DEG, 180);
		
		for(int i = 0; i<segments; i++) {
			coords[i] = center.coordsAt(alpha, diameter/2);
			alpha.setSize(alpha.gSize() - (360/segments));
		}
		//Add the lines.
		for(int i = 0; i<segments; i++) {
			if(i>0) {
				lines.add(new DoubleBuff.Line(coords[i].gX(), coords[i].gY(),
													coords[(int)(i*factor)%segments].gX(), coords[(int)(i*factor)%segments].gY(), color));
			}
		}
		entity.update(new ArrayList<DoubleBuff.Rec>(), lines, outline, color);
		
	}
	public void reversePatern() {
		paternSpeed *= -1;
	}
	public void speedUpPatern() {
		if(paternSpeed>=0)
			paternSpeed += 0.0005;
		else
			paternSpeed -= 0.0005;
	}
	public void slowDownPatern() {
		double temp;
		if(paternSpeed>=0) {
			temp = paternSpeed - 0.0005;
			if(temp>=0)
				paternSpeed = temp;
		}
		else {
			temp = paternSpeed + 0.0005;
			if(temp<0)
				paternSpeed = temp;
		}
		
	}
	public void pausePatern() {
		pausePattern = !pausePattern;
	}
	//Color automode
	private void colorCycle() {
		long newTime = System.currentTimeMillis();
		if(firstChange&&(newTime-startTime > 20000 )) {
			firstChange = false;
			initiateAnim1(Color.BLUE);
		}
		if(secondChange&&(newTime-startTime > 40000 )) {
			secondChange = false;
			initiateAnim1(Color.YELLOW);
		}
		if(thirdChange&&(newTime-startTime > 60000 )) {
			thirdChange = false;
			initiateAnim1(Color.MAGENTA);
		}
		if(fourthChange&&(newTime-startTime > 80000 )) {
			fourthChange = false;
			initiateAnim1(Color.RED);
		}
		if(newTime-startTime > 100000 ) {
			startTime = newTime;
			firstChange=true;secondChange=true;thirdChange=true;fourthChange=true;
			initiateAnim1(Color.PINK);
		}
	}
	//Animation stuff
	private Color futorColor;
	private boolean anim1;
	private boolean phase1;
	private int oldDiameter;
	private void advanceAnim1(long cycleTime, Color newColor) {
		int diamChange = (int)((cycleTime*650)/1000);
		if(phase1) {
			this.diameter -= diamChange;
			if(diameter<=0) {
				phase1 = false;
				this.color = newColor;
			}
		}
		else {
			this.diameter += diamChange;
			if(diameter>=oldDiameter) {
				anim1 = false;
				diameter = oldDiameter;
			}
		}
	}
	public void initiateAnim1(Color newColor) {
		futorColor = newColor;
		anim1 = true;
		oldDiameter = this.diameter;
		phase1=true;
	}
	

}
