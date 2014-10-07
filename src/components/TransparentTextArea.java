package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JTextArea;
/**
 * 
 * The code for this class comes from:
 * http://stackoverflow.com/questions/14177340/remove-jtextpanes-white-background-without-setopaque-over-a-translucent-jfram
 *
 */
public class TransparentTextArea extends JTextArea {
	
	 public TransparentTextArea() {
         setOpaque(false);
     }
	 
	 public TransparentTextArea(String text, int width, int height) {
		 super(text, width, height);
         setOpaque(false);
     }

     @Override
     protected void paintComponent(Graphics g) {
         g.setColor(new Color(255, 255, 255, 64));
         Insets insets = getInsets();
         int x = insets.left;
         int y = insets.top;
         int width = getWidth() - (insets.left + insets.right);
         int height = getHeight() - (insets.top + insets.bottom);
         g.fillRect(x, y, width, height);
         super.paintComponent(g);
     }
}
