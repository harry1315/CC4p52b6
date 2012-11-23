package test.p5;

import processing.core.*; 
import processing.data.*; 
import processing.opengl.*; 

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

public class Distance1D extends PApplet {

/**
 * Distance 1D. 
 * 
 * Move the mouse left and right to control the 
 * speed and direction of the moving shapes. 
 */
 
float xpos1;
float xpos2;
float xpos3;
float xpos4;
int thin = 8;
int thick = 36;

public void setup() 
{
  size(640, 480);
  noStroke();
  xpos1 = width/2;
  xpos2 = width/2;
  xpos3 = width/2;
  xpos4 = width/2;
}

public void draw() 
{
  background(0);
  
  float mx = mouseX * 0.4f - width/5.0f;
  
  fill(102);
  rect(xpos2, 0, thick, height/2);
  fill(204);
  rect(xpos1, 0, thin, height/2);
  fill(102);
  rect(xpos4, height/2, thick, height/2);
  fill(204);
  rect(xpos3, height/2, thin, height/2);
	
  xpos1 += mx/16;
  xpos2 += mx/64;
  xpos3 -= mx/16;
  xpos4 -= mx/64;
  
  if(xpos1 < -thin)  { xpos1 =  width; }
  if(xpos1 >  width) { xpos1 = -thin; }
  if(xpos2 < -thick) { xpos2 =  width; }
  if(xpos2 >  width) { xpos2 = -thick; }
  if(xpos3 < -thin)  { xpos3 =  width; }
  if(xpos3 >  width) { xpos3 = -thin; }
  if(xpos4 < -thick) { xpos4 =  width; }
  if(xpos4 >  width) { xpos4 = -thick; }
}

//  static public void main(String[] passedArgs) {
//    String[] appletArgs = new String[] { "Distance1D" };
//    if (passedArgs != null) {
//      PApplet.main(concat(appletArgs, passedArgs));
//    } else {
//      PApplet.main(appletArgs);
//    }
//  }
}
