package mesquite.pdsim.lib;

import java.awt.*;
import java.awt.event.*;

import mesquite.lib.duties.*;
import mesquite.lib.*;

/*  commands  */
/* includes commands,  buttons, miniscrolls

/* ======================================================================== */
/** A miniature slider used to set the value of a MesquiteNumber.  It provides
an incrementing and decrementing arrow, and a text field to show current value. */
public class CommandCheckBox extends Panel implements ItemListener {
	boolean adjustingSizes = true;
//	int heightSet, widthSet;
	Checkbox box;
	MesquiteCommand command;

	public CommandCheckBox (boolean initState, MesquiteCommand command, String label) {
		box=new Checkbox(label);
		box.setLabel(label);
	//	box.addActionListener(this);
		box.addItemListener(this); //(this);
		box.setState(initState);
		add(box);
		this.command=command;
	}
	/*.................................................................................................................*/
	/*.................................................................................................................*/

	public String getLabel(){
		return box.getLabel();
	}
	/*.................................................................................................................*/
/*  	 public void setAllowEstimation(boolean allow) {
  	 	allowEstimation = allow;
  	 }*/
	/*.................................................................................................................*/
	public void setVisible(boolean b) {
		//if (b)
		super.setVisible(b);
		box.setVisible(b);
		repaint();
	}
	public void actionPerformed(ActionEvent e){
		//Event queue
			exec();
	}
	public void itemStateChanged(ItemEvent e){
		exec();
	}
	public void exec(){
		if(box.getState()){
			command.doItMainThread("1", CommandChecker.getQueryModeString("CheckBox", command, this), this);
		}
		else {
			command.doItMainThread("0", CommandChecker.getQueryModeString("CheckBox", command, this), this);			
		}
	}
}
