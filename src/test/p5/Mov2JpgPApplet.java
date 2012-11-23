package test.p5;

import processing.core.PApplet;
import processing.video.Movie;

public class Mov2JpgPApplet extends PApplet {
	Movie myMovie;
	int framenumber = 0;
	// if you get images from hoge.mov, movTitle = "hoge";
	String movTitle = "transit"; 

	public void setup() {
		size(640, 360, P2D);
		myMovie = new Movie(this, movTitle+".mov");
		myMovie.play();
	}

	public void draw() { 	 
		image(myMovie, 0, 0);
		// then you get images at /hoge/{0,1,...100, 101,...,lastFrameNum}.jpg
		save("./"+movTitle+"images/"+ framenumber + ".jpg");
		framenumber++;
	}

	// Called every time a new frame is available to read
	public void movieEvent(Movie m) {
		m.read();
	}
	
	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "Mov2JpgPApplet" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
}

