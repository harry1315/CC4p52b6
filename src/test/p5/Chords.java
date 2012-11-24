package test.p5;

import processing.core.*; 
import processing.data.*; 
import processing.opengl.*; 

import controlP5.*; 
import traer.physics.*; 
import toxi.geom.*; 

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector; 

import toxi.math.conversion.*; 
import toxi.geom.*; 
import toxi.math.*; 
//import processing.xml.*; 
import toxi.geom.mesh2d.*; 
import controlP5.*; 
import toxi.util.datatypes.*; 
import toxi.util.events.*; 
import traer.physics.*; 
import toxi.geom.mesh.subdiv.*; 
import toxi.math.waves.*; 
import toxi.geom.mesh.*; 
import toxi.util.*; 
import toxi.math.noise.*; 

public class Chords extends PApplet {

	/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/73697*@* */
	/* !do not delete the line above, required for linking your tweak if you re-upload */

	ParticleSystem physics;

	Automaton auto1;
	ChainManager mChain;

	int waveType = 3;
	boolean auto = false;
	boolean drawColorChords = false;

	float chordAmp = 0.62f;
	float step     = 0.57f;

	int radioChords = 260;

	float alphaBack = 10;
	float cBackground = 0;

	PGraphics offScreen;
	
	ControlP5 controlP5;
	public void setup() {
		size(1024, 768, P3D);

		menuSetup(); 

		offScreen = createGraphics(width, height, P2D);

		physics = new ParticleSystem( 0.0f, 0.05f );

		mChain = new ChainManager();
		auto1  = new Automaton(width/2, height/2, 255, 255, 4);
		mChain.pushCircle(physics, 200);
		mChain.setColors(color(0, 0, 0));

		offScreen.beginDraw();
		offScreen.smooth();
		offScreen.endDraw();

		waveType = 3;
		auto = true;
		drawColorChords = true;

		chordAmp = 0.62f;
		step     = 0.57f;
		alphaBack = 60;
	}

	public void draw() {
		offScreen.beginDraw();
		offScreen.noStroke();
		offScreen.fill(cBackground, alphaBack);
		offScreen.rect(0, 0, width, height);
		offScreen.endDraw();

		physics.tick();

		offScreen.beginDraw();
		mChain.draw();
		offScreen.endDraw();
		image(offScreen, 0, 0);

		auto1.generate();
		controlP5.draw();
	}

	class Automaton {
		float x;
		float y;
		float py;
		float px;
		float radioX;
		float radioY;
		float tStep;

		int posInitX;
		int posInitY;

		int type;
		float inc;

		Automaton(float radioX, float radioY, int type) {
			this.x = random(width);
			this.y = random(height);
			this.radioX = radioX;
			this.radioY = radioY;
			tStep = random(TWO_PI);
			this.type = type;
			inc = random(0.03f, 0.045f);
		}

		Automaton(int posInitX, int posInitY, float radioX, float radioY, int type) {
			this.posInitX = posInitX;
			this.posInitY = posInitY;
			this.x = random(width);
			this.y = random(height);
			this.radioX = radioX;
			this.radioY = radioY;
			tStep = random(TWO_PI);
			this.type = type;
			inc = random(0.02f, 0.029f);
		}

		public void generate() {
			px = x;
			py = y;

			switch(type) {
			case 1:
				x =  cos(tStep)*radioX + width/2.0f;
				y =  sin(tStep)*cos(tStep)*radioY + height/2.0f;
				break;
			case 2:
				x =  cos(tStep + tStep/2.0f)*radioX + width/2.0f;
				y =  sin(tStep)*sin(tStep)*radioY + height/2.0f;
				break;
			case 3:
				x =  sin(tStep + sin(tStep/2.0f))*radioX + width/2.0f;
				y =  sin(tStep + cos(tStep/2.0f))*radioY + height/2.0f;
				break;
			case 4:
				x = cos(tStep)*sin(tStep/6)*radioX + width/2.0f + noise(tStep)*60;
				y = sin(tStep/2 + cos(tStep/3))*cos(tStep/5)*radioY + height/2.0f + noise(tStep)*60;
				break;
			case 5:
				x = posInitX + 115*noise(tStep) + 50*cos(tStep);
				y = sin(tStep + cos(tStep/2))*radioY +  posInitY;
				break;
			}

			tStep += inc;
		}

	}

	class Chain {
		ParticleSystem physics;
		Particle end;
		Particle start;
		Vector p;
		Vector s;

		java.util.List vertices;

		PVector initP;
		PVector endP;

		float angle;
		float inc;
		float nVal;
		float rest;
		int   n = 15;
		float t = 0.0f;
		float amp = 100.0f;

		int chordColor;

		Chain(ParticleSystem physics, PVector initP, PVector endP) {
			amp = 50 + random(-30, 30);
			inc = random(0.022f, 0.029f);
			nVal = random(0.2f, 0.3f);
			// t = random(-PI, PI);
			this.physics = physics;

			this.initP = initP;//new PVector(initP.x, initP.y);
			this.endP  = endP;//new PVector(endP.x, endP.y);
			inc = random(0.2f, 0.3f);

			end = physics.makeParticle(1.0f, initP.x, initP.y, 0);
			end.makeFixed();
			start = physics.makeParticle(1.0f, endP.x, endP.y, 0);
			start.makeFixed();

			p = new Vector();
			s = new Vector();

			p.add(start);
			for (int i=1; i < (n-1); i++) {
				p.add( physics.makeParticle(1.0f, lerp(initP.x, endP.x, i/(float)(n-1) ), lerp(initP.y, endP.y, i/(float)(n-1) ), 0.0f ));
			}
			p.add(end);

			float d = dist(initP.x, initP.y, endP.x, endP.x)/(float)(n-1);

			for (int i=0; i < (n-1); i++) {
				Particle p1 = (Particle)p.get(i);
				Particle p2 = (Particle)p.get(i+1);
				s.add( physics.makeSpring(p1, p2, 1.0f, 0.05f, d));
			}
		}

		public void draw() {

			angle = atan2(endP.y - initP.y, endP.x - initP.y );

			Vec2D[] handles=new Vec2D[n];
			for (int i=0; i<n; i++) {
				Particle p1=(Particle)p.get(i);
				handles[i]=new Vec2D(p1.position().x(), p1.position().y());
			}

			Spline2D spline = new Spline2D(handles);
			vertices = spline.computeVertices(8);

			setDraw();
		}

		public void setDraw() {
			angle = PI + atan2(endP.y - initP.y, endP.x - initP.y );

			if (!drawColorChords) {
				offScreen.stroke( chordColor, 25 + noise(nVal)*50);
			}
			else {
				float mA = map(angle, 0, TWO_PI, 25, 75);
				float mG = mag(endP.x - initP.y, endP.y - initP.y)*0.2f;
				offScreen.stroke(0, mA + mG + noise(nVal)*50, mG + mA + noise(nVal)*30, mA);
			}

			drawWave();
		}

		public void drawWave() {
			offScreen.strokeWeight(1); 
			offScreen.noFill();
			offScreen.beginShape();
			for (int i=0; i < vertices.size();i++ ) {
				Vec2D v=(Vec2D)vertices.get(i);
				offScreen.vertex(v.x, v.y);
			}
			offScreen.endShape();
		}

		public void update() {
			t += 1/24.0f +noise(nVal)/8;

			angle = PI + atan2(endP.y - initP.y, endP.x - initP.y );

			switch(waveType) {
			case 1:
				for (int i = 1; i < (n-1); i++) {
					Particle p1 = (Particle)p.get(i);
					p1.position().add(amp*chordAmp*cos(i * step + t)*sin(angle), amp*chordAmp*sin(i *step + t)*cos(angle), 0.0f);
				}
				break;
			case 2:
				for (int i=1; i < (n-1); i++) {
					Particle p1 = (Particle)p.get(i);
					p1.position().add(chordAmp*log(i*step +1.0f)*sin(angle), chordAmp*log(i*step +1.0f)*cos(angle), 0.0f);
				}
				break;
			case 3:
				for (int i=1; i < (n-1); i++) {
					Particle p1 = (Particle)p.get(i);
					p1.position().add(chordAmp*log(i*step +1.0f)*sin(i*step +t), chordAmp*log(i*step +1.0f)*cos(i*step +t), 0.0f);
				}
				break;
			}

			nVal += inc;
		}

		public void moveStart(float x, float y) {
			end.position().set(x, y, 0.0f);
		}

		public void moveEnd(float x, float y) {
			start.position().set(x, y, 0.0f);
		}

		public void setColor(int c) {
			chordColor = c;
		}

		public void setColorR(float a) {
			int c = color(a, chordColor >> 8 & 0xFF, chordColor & 0xFF);
			chordColor = c;
		}

		public void setColorG(float a) {
			int c = color( chordColor >> 16 & 0xFF, a, chordColor & 0xFF);
			chordColor = c;
		}

		public void setColorB(float a) {
			int c = color(chordColor >> 16 & 0xFF, chordColor >> 8 & 0xFF, a);
			chordColor = c;
		}
	}

	class ChainManager {
		ArrayList<Chain> chain;

		ChainManager() {
			chain = new ArrayList<Chain>();
		}

		public void draw() {
			Iterator<Chain> it = chain.iterator();
			while (it.hasNext ()) {
				Chain p = it.next();
				p.draw();
				p.update();
				if (auto)
					p.moveEnd(auto1.x, auto1.y);
				else
					p.moveEnd(mouseX, mouseY);
			}
		}

		public void setColors(int c) {
			Iterator<Chain> it = chain.iterator();
			while (it.hasNext ()) {
				Chain p = it.next();
				p.setColor( setGoodColor() );
			}
		}

		public void pushPos(ParticleSystem physics, int n) { 
			for (int  i =0; i < n; i++)
				push(physics, new PVector(50, height/2), new PVector(width - 100, height/2));
		}

		public void push(ParticleSystem physics, PVector init, PVector end) {
			chain.add(new Chain(physics, init, end));
		}

		public void pushCircle(ParticleSystem physics, int n) {
			for (int i =0; i < n; i++) {
				addCircle(physics, n);
			}
		}

		public void addCircle(ParticleSystem physics, int n) {
			float step = (TWO_PI/n)* (chain.size() - 1);
			chain.add(new Chain(physics, new PVector(width/2 + cos(step)*(radioChords + random(-5, 5) ), height/2 + sin(step)*(radioChords + random(-5, 5)) ), new PVector(width/2, height/2)));
		}

		public void pop() {
			chain.remove( chain.size() -1 );
		}
	}


	ControlWindow controlWindow;

	public void menuSetup() {
		controlP5 = new ControlP5(this);
		controlP5.setAutoDraw(true);
		//Korg nano-kontrol automatic gui creation
		int x1 = 10;
		int y1 = 10;

		controlP5.addSlider("alphaBack", 0, 255, 80, x1, y1, 15, 100);
		controlP5.addSlider("cBackground", 0, 255, 0, x1 + 55, y1, 15, 100);
		controlP5.addSlider("stepV", 0, 1, 0.57f, x1, y1 + 120, 15, 100);
		controlP5.addSlider("chordAmpV", 0, 2, 0.62f, x1 + 55, y1 + 120, 15, 100);

		controlP5.addSlider("waveType", 1, 4, 50, x1 + 15, y1 + 240, 15, 100).setNumberOfTickMarks(4);

		controlP5.addToggle("autoV", true, x1+ 130, y1, 30, 30);
		controlP5.addToggle("drawColorChords", true, x1+ 130, y1 + 50, 30, 30); 
	}

	public void autoV(){
		auto = !auto; 
	}
	public void drawColorChords(){
		drawColorChords = !drawColorChords;
	}

	public void waveType(int a){
		waveType = a;
	}

	public void alphaBack(float a) {
		alphaBack = a;
	}

	public void cBackground(float a) {
		cBackground = a;
	}

	public void stepV(float a) {
		step = a;
	}

	public void chordAmpV(float a) {
		chordAmp = a;
	}

	int[] colors ={
			0xff126886, 0xff107CA2, 0xff1F88AD, 0xff047C5F, 0xff36B99A, 0xff199D7D, 0xff088969,
			0xff16896D, 0xff125D98, 0xff2779B9, 0xff1EBCB8, 0xff0FAAA6, 0xff18B9B5, 0xff0E3995,
			0xff11BCB2, 0xff0F7E78, 0xff55C9C2, 0xff247672, 0xff0D6593, 0xff024A6F, 0xff085176,
			0xff0AAA9B, 0xff19B4A6, 0xff09746A, 0xff1D7E95, 0xff05645F, 0xff2CA099, 0xff2E8972,
			0xff108E5A, 0xff126B93, 0xff116379, 0xff095367, 0xff096766, 0xff1D9D6D, 0xff097C22,
			0xff118986, 0xff048683, 0xff1D8986, 0xff088380, 0xff1B817E, 0xff0CA7A4, 0xff25A086,
			0xff1CBCB8, 0xff0AC4BF, 0xff0C7EAA, 0xff177AA0, 0xff02AF5A, 0xff179EBF, 0xff00D8B2,
			0xff17A593, 0xff0FB3B7, 0xff07A1A5, 0xff13CCD1, 0xff30BBBF, 0xff05A7E5, 0xff139ACE,
			0xff1182AD, 0xff1180B9, 0xff038D93, 0xff014043, 0xff066367, 0xff1E7D81, 0xff05575A,
			0xff118398, 0xff0BB27E, 0xff15BC88, 0xff007F9D, 0xff197389, 0xff034555, 0xff086176,
			0xff0B5998, 0xff114B79, 0xff078180, 0xff0B4FA0, 0xff107383, 0xffDEFAFF, 0xffDAE8EA,
			0xffF7F7F7, 0xffFFFFFF, 0xff48CBE0, 0xff478A95, 0xff479579, 0xffA2FFDE, 0xff4B9D7F,
			0xffC1C1C1, 0xff41BAC9, 0xff2D75AA, 0xff327C77, 0xff097E77, 0xff035550, 0xff077C75
	};



	public int setGoodColor(){
		return colors[(int)random(0, colors.length - 1)];
	}
	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "Chords" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
}
