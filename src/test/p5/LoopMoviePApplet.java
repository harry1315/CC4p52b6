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

public class LoopMoviePApplet extends PApplet {

/**
 * Loop. 
 * 
 * Move the cursor across the screen to draw. 
 * Shows how to load and play a QuickTime movie file.  
 *
 */

Movie movie;

public void setup() {
  size(640, 360);
  background(0);
  // Load and play the video in a loop
  movie = new Movie(this, "transit.mov");
  movie.loop();
}

public void movieEvent(Movie m) {
  m.read();
}

public void draw() {
  if (movie.available() == true) {
    movie.read();
  }
  image(movie, 0, 0, width, height);
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Loop" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
