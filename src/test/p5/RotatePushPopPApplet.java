package test.p5;

import processing.core.PApplet;

/*
 *  processing -> example -> Basics -> Transform
 */

public class RotatePushPopPApplet extends PApplet {

/**
 * Rotate Push Pop. 
 * 
 * The push() and pop() functions allow for more control over transformations.
 * The push function saves the current coordinate system to the stack 
 * and pop() restores the prior coordinate system. 
 */
 
float a;                          // Angle of rotation
float offset = PI/24.0f;             // Angle offset between boxes
int num = 12;                     // Number of boxes
int[] colors = new int[num];  // Colors of each box
int safecolor;

boolean pink = true;

public void setup() 
{ 
  size(360, 360, P3D);
  noStroke();  
  for(int i=0; i<num; i++) {
    colors[i] = color(255 * (i+1)/num);
  }
  lights();
} 
 

public void draw() 
{     
  background(0, 0, 26);
  translate(width/2, height/2);
  a += 0.01f;   
  
  for(int i = 0; i < num; i++) {
    pushMatrix();
    fill(colors[i]);
    rotateY(a + offset*i);
    rotateX(a/2 + offset*i);
    box(200);
    popMatrix();
  }
} 
}
