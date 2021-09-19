import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class MandlebrotCircleApp {

	private static MandlebrotCircle chacha;
	
	public static void main(String[] args) {
		
		Dimension ScreenDimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int windowWidth = (int)ScreenDimension.getWidth() - 10;
		int windowHeight = (int)ScreenDimension.getHeight() - 90;
		DoubleBuff screen= new DoubleBuff(windowWidth, windowHeight, "ArtyFarty", Color.BLACK);
		
		//Sound:
		Sound song = new Sound("Assets\\Civilization 4 - Intro Video.wav");
		//song.play();
		
		
		int cercleDiam = 650;
		Coord2D center = new Coord2D(windowWidth/2, windowHeight/2);
		chacha = new MandlebrotCircle(cercleDiam, center, 100, 2, screen, Color.PINK);
		
		
		//make window visible.
		screen.setVisible(true);
		
		long oldTime = System.currentTimeMillis();
		long newTime;
		
		
		//Main loop
		while(true) {
			screen.run();
			newTime = System.currentTimeMillis();
			long cycleTime = newTime - oldTime;
			
			checkTypedKeys(screen);
			
			chacha.update(cycleTime);
			oldTime = newTime;
			
		}
		

	}
	private static void checkTypedKeys(DoubleBuff screen) {
		if(screen.leftPressed) {
			chacha.slowDownPatern();
			screen.leftPressed = false;
		}
		if(screen.rightPressed) {
			chacha.speedUpPatern();
			screen.rightPressed = false;
		}
		if(screen.pPressed) {
			chacha.pausePatern();
			screen.pPressed = false;
		}
		if(screen.rPressed) {
			chacha.reversePatern();
			screen.rPressed = false;
		}
	}

}
