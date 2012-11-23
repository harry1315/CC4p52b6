package test.p5;

import processing.core.PApplet;

public class Rotate2PApplet extends PApplet{	 
	float a = 0.0f;
	float rSize;  // rectangle size
	float initPhase;
	
	public Rotate2PApplet(){
		this(0.5f);
	}
	public Rotate2PApplet(float _initPhase){
		initPhase = _initPhase;
	}

	//void setup() {
	public void setup(){ 
	  size(160, 160, P3D);
	  frameRate(15);
	  rSize = width / 3;  
	  noStroke();
	  fill(204, 204);
	}

	//void draw() {
	public void draw(){ 
	  background(31);
	  
	  a += 0.02;
	  if(a > TWO_PI) { 
	    //a = 0.0;
	    a = 0.0f;
	  }
	  
	  translate(width/2, height/2);
	  
	  rotateX(a + initPhase);
	  //rotateY(a * 2.0);
	  rotateY((a +initPhase) * 2.0f);
	  rect(-rSize, -rSize, rSize*2, rSize*2);
	  
	  //rotateX(a * 1.001);
	  //rotateY(a * 2.002);
	  rotateX((a +initPhase) * 1.001f);
	  rotateY((a + initPhase) * 2.002f);
	  rect(-rSize, -rSize, rSize*2, rSize*2);
	}
}

