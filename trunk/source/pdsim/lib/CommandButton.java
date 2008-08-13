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
public class CommandButton extends Panel implements ActionListener {
	boolean adjustingSizes = true;
	String arg;
//	int heightSet, widthSet;
	Button button;
	MesquiteCommand command;
//	int x;
	
	public CommandButton (MesquiteModule module, MesquiteCommand command, String label, String Arg) {
		this.arg=Arg;
		button=new Button(label);
		button.setLabel(label);
	//	button.setVisible(true);
		button.addActionListener(this);
		add(button);
		this.command=command;
	}
	/*.................................................................................................................*/
	/*.................................................................................................................*/

	public String getLabel(){
		return button.getLabel();
	}
	/*.................................................................................................................*/
/*  	 public void setAllowEstimation(boolean allow) {
  	 	allowEstimation = allow;
  	 }*/
	/*.................................................................................................................*/
	public void setVisible(boolean b) {
		//if (b)
		super.setVisible(b);
		button.setVisible(b);
		repaint();
	}
	public void actionPerformed(ActionEvent e){
		//Event queue
			exec();
	}
	public void exec(){
		command.doItMainThread(arg, CommandChecker.getQueryModeString("CommandButton", command, this), this);
	}
}
