package mesquite.pdsim.lib;

import java.util.List;
import java.awt.Choice;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import mesquite.lib.CommandChecker;
import mesquite.lib.MesquiteCommand;

public class CommandComboBox extends Panel implements ItemListener {
	boolean adjustingSizes = true;
//	int heightSet, widthSet;
	Choice box;
	MesquiteCommand command;

	public CommandComboBox (String initState, List options, MesquiteCommand command, String label) {
		box=new Choice();
		for(int x=0;x<options.size();x++)
			box.add(options.get(x).toString());
	//	box.addActionListener(this);
		box.addItemListener(this); //(this);
		box.select(initState);
		add(box);
		this.command=command;
	}
	/*.................................................................................................................*/
	/*.................................................................................................................*/

	public String getLabel(){
		return box.getName();
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
		command.doItMainThread(box.getSelectedItem(), CommandChecker.getQueryModeString("CheckBox", command, this), this);
	}
}

