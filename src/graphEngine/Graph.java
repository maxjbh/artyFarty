package graphEngine;

import java.awt.Color;
import java.util.function.Function;
import geometryUtils.Coord2Ddouble;
import geometryUtils.Coord2D;
import max2DEngine.DoubleBuff;

public class Graph {
	
	private double maxY;
	private double minY;
	private double maxX;
	private double minX;
	private Function<Double, Double> getYfromX;
	private AxePositions axePosition; 
	private String xUnitName;
	private String yUnitName;

	
	private int screenWidth;
	private int screenHeight;

	
	private static final double screenXSideInUnits = 30.0;
	private static final double screenYSideInUnits = 20.0;
	private static final int axesStrokeWidth = 3;
	
	private static final int axeIndicatorHalfLength = 5;
	private static final int axeXIndicatorTextYOffset = 30;
	private static final int axeXIndicatorTextXOffset = -15;
	
	private static final int axeYIndicatorTextXOffset = -60;
	private static final int axeYIndicatorTextYOffset = 5;
	
	private static final int axeXLabelXOffset = -110;
	private static final int axeXLabelYOffset = -10;
	private static final int axeYLabelXOffset = 30;
	private static final int axeYLabelYOffset = 20;
	
	//Expressed in graph units for the bottom left axe placement
	private static final double bottomLeftOutOfBoundsToAxeDistance = 2.0;
	
	
	private double graphXUnit = 1.0;
	private double graphYUnit = 1.0;
	private double yAxisXvalue = 0.0;
	private double xAxisYvalue = 0.0;
	
	public Graph(
			double minX,
			double maxX,
			double minY,
			double maxY,
			Function<Double, Double> getYfromX,
			int screenWidth,
			int screenHeight,
			AxePositions axePosition
		) {
		this.maxY = maxY;
		this.minY = minY;
		this.maxX = maxX;
		this.minX = minX;
		this.getYfromX = getYfromX;
		
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		this.axePosition = axePosition;
		
		this.xUnitName = "";
		this.yUnitName = "";
	}
	public Graph(
			double minX,
			double maxX,
			double minY,
			double maxY,
			Function<Double, Double> getYfromX,
			int screenWidth,
			int screenHeight,
			AxePositions axePosition,
			String xUnitName,
			String yUnitName
		) {
		this.maxY = maxY;
		this.minY = minY;
		this.maxX = maxX;
		this.minX = minX;
		this.getYfromX = getYfromX;
		
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		this.axePosition = axePosition;
		
		this.xUnitName = xUnitName;
		this.yUnitName = yUnitName;
	}
	
	/**
	 * SIDE EFFECT : resets max and min values
	 * @return 2D element containing everything needed to draw axis
	 */
	public DoubleBuff.Entity calculateAxes(){
		double xOptimalUnit = calculateUnit(screenXSideInUnits, maxX, minX);
		double yOptimalUnit = calculateUnit(screenYSideInUnits, maxY, minY);
		graphXUnit = xOptimalUnit;
		graphYUnit = yOptimalUnit;
		//Recalabrate max and mins, if bottom left setting then we keep graph starting around min value
		double perSideExtraX = ((screenXSideInUnits*xOptimalUnit)-(maxX-minX))/2.0;
		double perSideExtraY = ((screenYSideInUnits*yOptimalUnit)-(maxY-minY))/2.0;
		switch(this.axePosition) {
		case bottomLeft:
			this.maxX = this.maxX + perSideExtraX*2.0 - bottomLeftOutOfBoundsToAxeDistance*graphXUnit;
			this.maxY = this.maxY + perSideExtraY*2.0 - bottomLeftOutOfBoundsToAxeDistance*graphYUnit;
			this.minX = this.minX - bottomLeftOutOfBoundsToAxeDistance*graphXUnit;
			this.minY = this.minY - bottomLeftOutOfBoundsToAxeDistance*graphYUnit;
			break;
		case central:
			this.minX = this.minX - perSideExtraX;
			this.maxX = this.maxX + perSideExtraX;
			this.minY = this.minY - perSideExtraY;
			this.maxY = this.maxY + perSideExtraY;
			break;
		}
		
		//Find Axes
		setAxeCoordinates();
		Coord2Ddouble left = new Coord2Ddouble(minX, xAxisYvalue);
		Coord2Ddouble right = new Coord2Ddouble(maxX, xAxisYvalue);
		Coord2Ddouble bottom = new Coord2Ddouble(yAxisXvalue, minY);
		Coord2Ddouble top = new Coord2Ddouble(yAxisXvalue, maxY);
		DoubleBuff.Line xLine = buildLineFromGraphCoordinates(left, right, Color.black, axesStrokeWidth);
		DoubleBuff.Line yLine = buildLineFromGraphCoordinates(bottom, top, Color.black, axesStrokeWidth);
		
		DoubleBuff.Entity result = new DoubleBuff.Entity();
		result.lines.add(xLine);
		result.lines.add(yLine);
		
		//BuildIndicators
		double firstX = ((minX/xOptimalUnit)+1)*xOptimalUnit;
		double firstY = ((minY/yOptimalUnit)+1)*yOptimalUnit;
		double currentX = firstX;
		double currentY = firstY;
		while(currentX<maxX) {
			Coord2Ddouble unitIndicatorCenter = new Coord2Ddouble(currentX, left.gY());
			result.lines.add(buildXIndicatorLineAt(unitIndicatorCenter));
			result.texts.add(buildXIndicatorTextAt(unitIndicatorCenter));
			currentX = currentX + xOptimalUnit;
		}
		while(currentY<maxY) {
			Coord2Ddouble unitIndicatorCenter = new Coord2Ddouble(bottom.gX(), currentY);
			result.lines.add(buildYIndicatorLineAt(unitIndicatorCenter));
			result.texts.add(buildYIndicatorTextAt(unitIndicatorCenter));
			currentY = currentY + yOptimalUnit;
		}
		
		//Build axe labels
		result.texts.add(buildXLabel(left.gY()));
		result.texts.add(buildYLabel(top.gX()));
		
		return result;
		
	}
	
	/*
	 * Precision will be unit / 10
	 */
	public DoubleBuff.Entity drawFullGraphContent(){
		DoubleBuff.Entity result = new DoubleBuff.Entity();
		boolean firstPoint = true;
		Coord2Ddouble lastPoint = new Coord2Ddouble();
		for(double currentX = minX; currentX<=maxX; currentX += graphXUnit/10.0) {
			Coord2Ddouble nextPoint = graphPointAfterFunctionApply(currentX);
			if(!firstPoint) {
				result.lines.add(buildLineFromGraphCoordinates(lastPoint, nextPoint, Color.red, 1));
			}
			firstPoint = false;
			lastPoint = nextPoint;
		}
		return result;
	}
	
	private void setAxeCoordinates() {
		switch(axePosition) {
		case bottomLeft:
			yAxisXvalue = minX + bottomLeftOutOfBoundsToAxeDistance*graphXUnit;
			xAxisYvalue = minY + bottomLeftOutOfBoundsToAxeDistance*graphYUnit;
			break;
		case central:
			yAxisXvalue = minX + ((maxX-minX)/2.0);
			xAxisYvalue = minY + ((maxY-minY)/2.0);
			break;
		}
	
	}
	
	private double calculateUnit(double screenSideInUnits, double maxValue, double minValue) {
		double result =0.0;
		double clostestMatch = (maxValue-minValue)/screenSideInUnits;
		String clostestMatchString = ""+clostestMatch;
		char currentDigit = clostestMatchString.charAt(0);
		int digitIndex = 0;
		boolean dotPassed = false;
		while(currentDigit=='0' || currentDigit=='.') {
			digitIndex++;
			currentDigit = clostestMatchString.charAt(digitIndex);
			if(currentDigit=='.') {
				dotPassed = true;
			}
			//digetIndex and current digit finish set on first digit that isn't 0 or .
		}
		int startingDigit = Integer.parseInt(""+currentDigit);
		if(startingDigit>=5) {
			startingDigit = 10;
		}else {
			startingDigit++;
		}
		if(dotPassed) {
			result = Math.pow(10.0, (-1.0)*((double)(digitIndex-1))*(double)startingDigit);
		}else {
			//Get index of dot
			while(currentDigit!='.') {
				digitIndex++;
				currentDigit = clostestMatchString.charAt(digitIndex);
				//finishes set on .
			}
			result = Math.pow(10.0, (double)(digitIndex-1))*(double)startingDigit;
		}
		
		
		return result;
	}

	
	public Coord2D translateGraphCoordinates(Coord2Ddouble graphCoordinates ) {
		return new Coord2D(translateX(graphCoordinates.gX()), translateY(graphCoordinates.gY()));
	}
	
	public Coord2Ddouble graphPointAfterFunctionApply(Double x) {
		Double y = getYfromX.apply(x);
		return new Coord2Ddouble(x, y);
	}
	
	
	private DoubleBuff.TextOnScreen buildTextFromGraphCoordinates(String text, Coord2Ddouble positionInGraph){
		return new DoubleBuff.TextOnScreen(text, translateGraphCoordinates(positionInGraph));
	}
	
	private DoubleBuff.TextOnScreen buildXLabel(double graphYofXAxis){
		Coord2Ddouble point = new Coord2Ddouble(maxX, graphYofXAxis);
		Coord2D translatedPoint = translateGraphCoordinates(point);
		Coord2D textCenterPoint = new Coord2D(translatedPoint.gX() + axeXLabelXOffset, translatedPoint.gY() + axeXLabelYOffset);
		return new DoubleBuff.TextOnScreen(this.xUnitName, textCenterPoint);
	}
	private DoubleBuff.TextOnScreen buildYLabel(double graphXofYAxis){
		Coord2Ddouble point = new Coord2Ddouble(graphXofYAxis, maxY);
		Coord2D translatedPoint = translateGraphCoordinates(point);
		Coord2D textCenterPoint = new Coord2D(translatedPoint.gX() + axeYLabelXOffset, translatedPoint.gY() + axeYLabelYOffset);
		return new DoubleBuff.TextOnScreen(this.yUnitName, textCenterPoint);
	}
	
	private DoubleBuff.TextOnScreen buildXIndicatorTextAt(Coord2Ddouble point){
		Coord2D translatedPoint = translateGraphCoordinates(point);
		Coord2D textCenterPoint = new Coord2D(translatedPoint.gX() + axeXIndicatorTextXOffset, translatedPoint.gY() + axeXIndicatorTextYOffset);
		return new DoubleBuff.TextOnScreen(""+point.gX(), textCenterPoint);
	}
	private DoubleBuff.TextOnScreen buildYIndicatorTextAt(Coord2Ddouble point){
		Coord2D translatedPoint = translateGraphCoordinates(point);
		Coord2D textCenterPoint = new Coord2D(translatedPoint.gX() + axeYIndicatorTextXOffset, translatedPoint.gY() + axeYIndicatorTextYOffset);
		return new DoubleBuff.TextOnScreen(""+point.gY(), textCenterPoint);
	}
	
	private DoubleBuff.Line buildYIndicatorLineAt(Coord2Ddouble point){
		Coord2D translatedPoint = translateGraphCoordinates(point);
		Coord2D startPoint = new Coord2D(translatedPoint.gX() - axeIndicatorHalfLength, translatedPoint.gY());
		Coord2D endPoint = new Coord2D(translatedPoint.gX() + axeIndicatorHalfLength, translatedPoint.gY());
		return DoubleBuff.Line.fromCoord2D(startPoint, endPoint, Color.black, axesStrokeWidth);
	}
	
	private DoubleBuff.Line buildXIndicatorLineAt(Coord2Ddouble point){
		Coord2D translatedPoint = translateGraphCoordinates(point);
		Coord2D startPoint = new Coord2D(translatedPoint.gX(), translatedPoint.gY() + axeIndicatorHalfLength);
		Coord2D endPoint = new Coord2D(translatedPoint.gX(), translatedPoint.gY() - axeIndicatorHalfLength);
		return DoubleBuff.Line.fromCoord2D(startPoint, endPoint, Color.black, axesStrokeWidth);
	}
	private DoubleBuff.Line buildLineFromGraphCoordinates(Coord2Ddouble firstPoint, Coord2Ddouble secondPoint, Color color, int strokeWidth){
		return DoubleBuff.Line.fromCoord2D(translateGraphCoordinates(firstPoint), translateGraphCoordinates(secondPoint), Color.black, strokeWidth);
	}
	
	
	private int translateY(double y) {
		double coef = 0.0;
		if(y>maxY) {
			coef = ((y - maxY)/(maxY-minY))*-1.0;
		}else {
			if(y<minY) {
				coef = ((minY - y)/(maxY-minY))+1.0;
			}else {
				coef = ((maxY - y)/(maxY-minY));
			}
		}
		return ((int)(coef*((double)screenHeight)));
	}
	
	private int translateX(double x) {
		double coef = 0.0;
		if(x>maxX) {
			coef = ((x - maxY)/(maxX-minX))+1.0;
		}else {
			if(x<minX) {
				coef = ((minX - x)/(maxX-minX))*-1.0;
			}else {
				coef = ((x - minX)/(maxX-minX));
			}
		}
		return ((int)(coef*((double)screenWidth)));
	}
	
	public enum AxePositions{
		bottomLeft,
		central
	}
	
	
}
