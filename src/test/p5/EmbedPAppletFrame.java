package test.p5;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import processing.core.PApplet;

public class EmbedPAppletFrame extends JFrame {
 
      public EmbedPAppletFrame() {
          super("Embedded PApplet");
 
          setLayout(new BorderLayout());
          PApplet embed = new Bounce();
          add(embed, BorderLayout.CENTER);
          
          // important to call this whenever embedding a PApplet.
          // It ensures that the animation thread is started and
          // that other internal variables are properly set.
          embed.init();          
          embed.registerMethod("pre", this);
      }
      
      public void pre(){
    	  System.out.println("pre");
      }
      
      public static void main(String[] args){
    	  EmbedPAppletFrame frame = new EmbedPAppletFrame();
    	  frame.setSize(640, 200);
    	  frame.setVisible(true);
      }
 }