package test.p5;

import processing.core.PApplet;

/*
 *  processing -> example -> Basics -> Transform
 */
public class RotateXYPApplet extends PApplet {

/**
 * Rotate 1. 
 * 
 * Rotating simultaneously in the X and Y axis. 
 * Transformation functions such as rotate() are additive.
 * Successively calling rotate(1.0) and rotate(2.0)
 * is equivalent to calling rotate(3.0). 
 */
 
float a = 0.0f;
float rSize;  // rectangle size

public void setup() {
  size(640, 360, P3D);
  rSize = width / 6;  
  noStroke();
  fill(204, 204);
}

public void draw() {  
  background(126);
  
  a += 0.005f;
  if(a > TWO_PI) { 
    a = 0.0f; 
  }
  
  translate(width/2, height/2);
  
  rotateX(a);
  rotateY(a * 2.0f);
  fill(255);
  rect(-rSize, -rSize, rSize*2, rSize*2);
  
  rotateX(a * 1.001f);
  rotateY(a * 2.002f);
  fill(0);
  rect(-rSize, -rSize, rSize*2, rSize*2);

}
}
