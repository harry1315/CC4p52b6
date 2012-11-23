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

public class Mirror extends PApplet {
/**
 * Mirror 
 * by Daniel Shiffman.  
 *
 * Each pixel from the video source is drawn as a rectangle with rotation based on brightness.   
 */ 
// Size of each cell in the grid
int cellSize = 20;
// Number of columns and rows in our system
int cols, rows;
// Variable for capture device
Capture video;


public void setup() {
  size(640, 480, P2D);
  frameRate(30);
  cols = width / cellSize;
  rows = height / cellSize;
  colorMode(RGB, 255, 255, 255, 100);

  // Uses the default video input, see the reference if this causes an error
  video = new Capture(this, width, height);
  video.start();
  
  background(0);
}

public void draw() { 
  if (video.available()) {
    video.read();
    video.loadPixels();
    
    // Not bothering to clear background
    // background(0);
  
    // Begin loop for columns
    for (int i = 0; i < cols; i++) {
      // Begin loop for rows
      for (int j = 0; j < rows; j++) {
      
        // Where are we, pixel-wise?
        int x = i*cellSize;
        int y = j*cellSize;
        int loc = (video.width - x - 1) + y*video.width; // Reversing x to mirror the image
      
        float r = red(video.pixels[loc]);
        float g = green(video.pixels[loc]);
        float b = blue(video.pixels[loc]);
        // Make a new color with an alpha component
        int c = color(r, g, b, 75);
      
        // Code for drawing a single rect
        // Using translate in order for rotation to work properly
        pushMatrix();
        translate(x+cellSize/2, y+cellSize/2);
        // Rotation formula based on brightness
        rotate((2 * PI * brightness(c) / 255.0f));
        rectMode(CENTER);
        fill(c);
        noStroke();
        // Rects are larger than the cell for some overlap
        rect(0, 0, cellSize+6, cellSize+6);
        popMatrix();
      }
    }
  }
}
}
