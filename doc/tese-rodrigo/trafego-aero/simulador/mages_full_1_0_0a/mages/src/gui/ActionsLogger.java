/*
GNU Mages, version 1.00 alpha
Multi-Agents Environment Simulator
Copyright (C) 2001-2002 João Ricardo Bittencourt <jrbitt@uol.com.br>
 
This program is free software; you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version. 

This program is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
GNU General Public License for more details. 

You should have received a copy of the GNU General Public License 
along with this program; if not, write to the Free Software 
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package mages.gui;

import mages.simulation.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;

public class ActionsLogger extends JInternalFrame{
  final String[] names = {"ID","Action"};
  
  JScrollPane desk = new JScrollPane();
  JTable table = null;
  Vector data = new Vector(); //id + actionName
  Vector turns = new Vector();
  
  public ActionsLogger(){
    setTitle("Actions Logger");
    setIconifiable(true);
    setMaximizable(false);
    setResizable(true);
    setVisible(true);
        
    TableModel dataModel = new AbstractTableModel() {
      public int getColumnCount() { return names.length; }
      public int getRowCount() { return data.size();}
      public Object getValueAt(int row, int col) {return ((Vector)data.elementAt(row)).elementAt(col);}
      public String getColumnName(int column) {return names[column];}
      public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
      public boolean isCellEditable(int row, int col) {return false;}
      public void setValueAt(Object aValue, int row, int column) { ((Vector)data.elementAt(row)).add(column,aValue); }
    };    
    
    table = new JTable(dataModel);
    table.setShowHorizontalLines(true);
    table.setShowVerticalLines(true);    
    table.setRowSelectionAllowed(true);
    table.setBackground(Color.white);
    table.setOpaque(true);
    setBounds(0,0,200,200);
    setOpaque(true);
    desk.setOpaque(true);
    setContentPane(desk);
    desk.getViewport().add(table);    
    
    addInternalFrameListener(new InternalFrameAdapter() {
      public void internalFrameActivated(InternalFrameEvent e){
        repaint();
      }                       
    });    
  }
  
  public void addTurn(Turn turn){
    Vector tmp = new Vector(2);
    tmp.addElement(new Long(turn.getId()));
    tmp.addElement(turn.getAction().getName());
    data.addElement(tmp);
    turns.addElement(turn.getString());
    table.repaint();
  }
  
  public String[] getTurns(){
    String[] tmp = new String[turns.size()];
    for(int i=0; i<turns.size(); i++){
      tmp[i] = (String)turns.elementAt(i);
    }
    return tmp;
  }
}