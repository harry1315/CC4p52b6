package test.p5;

import processing.core.PApplet;
import processing.core.PImage;

public class MikuFlipPApplet extends PApplet{
	int numFrames = 12;  // The number of frames in the animation
	int frame = 0;
	String imageDirName;	
	PImage[] images; 
	
	public MikuFlipPApplet(){
		//this(250, "miku-front");
		this(250, "miku-back");
	}
	public MikuFlipPApplet(int numFrames, String imageDirName){
		this.imageDirName = imageDirName;
		this.numFrames = numFrames;
		this.images = new PImage[numFrames];
	}
	public void setup(){
		  size(100, 180);
		  frameRate(30);
		  for(int i=0; i<numFrames; i++){
			  if(i<10)
				  this.images[i] = loadImage(imageDirName + "/000"+i+".png");
			  else if(i<100)
				  this.images[i] = loadImage(imageDirName + "/00"+i+".png");
			  else 
				  this.images[i] = loadImage(imageDirName + "/0"+i+".png");
		  }		  
	}

	public void draw() { 
		background(0);
		frame = (frame+1) % numFrames;
		image(images[frame], 0, 0);
	}
	
	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#c0c0c0", "MikuFlipPApplet" });
	}	
}
