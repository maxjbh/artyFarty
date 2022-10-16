package graphEngine;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import max2DEngine.DoubleBuff;
import geometryUtils.Coord2Ddouble;
//import graphEngine.Graph;

public class GraphDrawer {

	private DoubleBuff.Entity entity;
	
	public static void main(String[] args) {
		
		Dimension ScreenDimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int windowWidth = (int)ScreenDimension.getWidth() - 60;
		int windowHeight = (int)ScreenDimension.getHeight() - 90;
		DoubleBuff screen= new DoubleBuff(windowWidth, windowHeight, "GraphDrawer", Color.GRAY);
		
		Graph testGraph = new Graph(0.0, 50.0, 0.0, 1000.0, x -> Math.pow(x, 2.0), windowWidth, windowHeight, Graph.AxePositions.bottomLeft, "Turns", "Techs");
		DoubleBuff.Entity axes = testGraph.calculateAxes();
		DoubleBuff.Entity content = testGraph.drawFullGraphContent();
		
		//Coord2Ddouble testPoint = new Coord2Ddouble(50.0, 50.0);
		//Coord2Ddouble testPoint2 = new Coord2Ddouble(450.0, 50.0);
		
		
		//DoubleBuff.Line testLine = DoubleBuff.Line.fromCoord2D(testGraph.translateGraphCoordinates(testPoint), testGraph.translateGraphCoordinates(testPoint2), Color.red);
	
		screen.entitys.add(axes);
		screen.entitys.add(content);
		
		//Main loop
		while(true) {
			screen.run();
			
		}

	}

}
