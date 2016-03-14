
package mages.controller.ctf;

import java.awt.*;
import java.awt.event.*;

public class CtFScore extends Dialog{
  Button btnOk = new Button("Ok");

  public CtFScore(Frame f, int br, int bb, int fr, int fb){
    super(f,"Score");

    setLayout(new GridLayout(5,2));      
    add(new Label("Bots in Red Team (live): ")); add(new Label(Integer.toString(br)));                  
    add(new Label("Bots in Blue Team (live): ")); add(new Label(Integer.toString(bb)));                        
    add(new Label("Flags captured (Red): ")); add(new Label(Integer.toString(fr)));                        
    add(new Label("Flags captured (Blue): ")); add(new Label(Integer.toString(fb)));                                        
    
    Panel pnlButtons = new Panel();
    pnlButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
    pnlButtons.add(btnOk);
    add(pnlButtons);
    initEvents();
    pack();
    show();
  }
  
  public CtFScore(Frame f, String email, String id, double br, double bb, double fr, double fb){
    super(f,"Score"); 

    setLayout(new GridLayout(7,2));      
    add(new Label("Admin E-mail: ")); add(new Label(email));                      
    add(new Label("# Simulation: ")); add(new Label(id));                          
    add(new Label("Avr. Bots in Red Team (live): ")); add(new Label(Double.toString(br)));                  
    add(new Label("Avr. Bots in Blue Team (live): ")); add(new Label(Double.toString(bb)));                        
    add(new Label("Avr. Flags captured (Red): ")); add(new Label(Double.toString(fr)));                        
    add(new Label("Avr. Flags captured (Blue): ")); add(new Label(Double.toString(fb)));                                        
    
    Panel pnlButtons = new Panel();
    pnlButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
    pnlButtons.add(btnOk);
    add(pnlButtons);     
    initEvents();
    pack();
    show();    
  }
    
  private void initEvents(){
    btnOk.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         dispose();
      }
    });	         
  }
}