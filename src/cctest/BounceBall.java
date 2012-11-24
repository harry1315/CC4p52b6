package cctest;

import processing.core.PApplet;


public class BounceBall extends PApplet {

int size = 60;       // Width of the shape
float xpos, ypos;    // Starting position of shape    

float xspeed = 2.8f;  // Speed of the shape
float yspeed = 2.2f;  // Speed of the shape

int xdirection = 1;  // Left or Right
int ydirection = 1;  // Top to Bottom

public void setup() {
  size(640, 200);
  frameRate(30);
  smooth();
  xpos = width/2;
  ypos = height/2;
}

public void draw() {
  background(100);

  xpos = xpos + ( xspeed * xdirection );
  ypos = ypos + ( yspeed * ydirection );

  if (xpos > width-size || xpos < 0) {
    xdirection *= -1;
  }
  if (ypos > height-size || ypos < 0) {
    ydirection *= -1;
  }

  ellipse(xpos+size/2, ypos+size/2, size, size);
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "BounceBall" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
