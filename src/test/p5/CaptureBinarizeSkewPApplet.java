package test.p5;
import processing.core.*; 
import processing.data.*; 
import processing.opengl.*; 

import processing.video.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class CaptureBinarizeSkewPApplet extends PApplet {
	public boolean realDeployment = false;	
	Capture video;
	int   selected = -1;  
	int   pos[][] = {{0,0},{400,0},{400,300},{0,300}}; 

    PImage binaryImage;
    boolean[] binaryData;
    
	public void setup(){
		size(400,300, P2D);
		
		binaryImage	= new PImage(width, height);
		binaryData	= new boolean[width * height];
		if(realDeployment == true){
			video = new Capture(this, width, height);
			video.start();
		}
	}

	public	void setCapture(Capture capture){
		this.video = capture;
	}
	
	//  gray = 0.114*b + 0.587*g + 0.299*r; 
	float r_ratio = 0.299f;
	float g_ratio = 0.587f;
	float b_ratio = 0.114f;
	int threshold = 128;

	public void draw(){
		if (video.available()) {
			video.read();
			video.loadPixels();
        
			for(int i = 0; i < width * height; i++){
				int pix = video.pixels[i];
				float gray = (int)(r_ratio*red(pix) + g_ratio*green(pix) + b_ratio*blue(pix));
				//float med = (int)(red(pix) + green(pix) + blue(pix));
				if(gray > threshold){
					binaryData[i] = false;
					binaryImage.pixels[i] = color(255);
				}
				else{
					binaryData[i] = true;
					binaryImage.pixels[i] = color(0);
				}
			}
			binaryImage.updatePixels();
			//image(binaryImage, 0, 0);
     
			background(0);    
			beginShape();
				texture(binaryImage);  
				vertex(pos[0][0], pos[0][1], 0, 0);
				vertex(pos[1][0], pos[1][1], video.width, 0);
				vertex(pos[2][0], pos[2][1], video.width, video.height);
				vertex(pos[3][0], pos[3][1], 0, video.height);
			endShape(CLOSE);
    
			if ( mousePressed && selected >= 0 ) {
				pos[selected][0] = mouseX;
				pos[selected][1] = mouseY;
			}
			else {
				float min_d = 20;
				selected = -1;
				for (int i=0; i<4; i++) {
					float d = dist( mouseX, mouseY, pos[i][0], pos[i][1] );
					if ( d < min_d ) {
						min_d = d;
						selected = i;
					}      
				}
			}
			if ( selected >= 0 ) {
				ellipse( mouseX, mouseY, 20, 20 );
			}    
		}//end of draw
	}
}
