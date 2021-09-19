import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Sound {
	
	private Clip clip;
	private long lastStartTime;
	//music line stuff
	private byte[] arrFile;
	long length;
	/////
	public Sound(String file) {
		try {
   			File soundFile = new File(file);
   			
   			//music line stuff
   			DataInputStream fis = new DataInputStream(new FileInputStream(soundFile));
   			arrFile = new byte[(int) soundFile.length()];
   			fis.readFully(arrFile);
   			/////
   			
   			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
   			// Get a clip resource.
   			clip = AudioSystem.getClip();
   			// Open audio clip and load samples from the audio input stream.
   			clip.open(audioIn);
   			length = clip.getMicrosecondLength();
      } catch (UnsupportedAudioFileException e) {
    	  e.printStackTrace();
      } catch (IOException e) {
    	  e.printStackTrace();
      } catch (LineUnavailableException e) {
    	  e.printStackTrace();
      }
	}
	public int getAmplitude() {
		
		long timeElapsed = System.currentTimeMillis() - lastStartTime;
		int ourByteIndex = (int)(((double)(timeElapsed)/(double)(length))*arrFile.length);
		if(ourByteIndex%2==1)
			ourByteIndex++;
		return (int)arrFile[ourByteIndex]+(int)arrFile[ourByteIndex+1]*16*16;
		//System.out.println((int)arrFile[i]+(int)arrFile[i+1]*16*16);
	}
	 public void play() {
         if (clip.isRunning())
            clip.stop();   // Stop the player if it is still running
         clip.setFramePosition(0); // rewind to the beginning
         clip.start();     // Start playing
         lastStartTime = System.currentTimeMillis();
	   }
	

}
