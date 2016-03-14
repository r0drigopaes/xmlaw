import java.awt.*;
import javax.swing.*;

class T {

public static void main (String [] args) {

	JLabel picture = new JLabel (new ImageIcon ("jsimDBSchema.gif"));
	JPanel p = new JPanel (new BorderLayout ());
	p.add (picture, BorderLayout.CENTER);
	JFrame f = new JFrame ("ImageIcon Test");
	f.setContentPane (p);
	f.pack ();
	f.setVisible (true);
}}
