/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison.
Version 2.01, December 2007.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
 */

package mesquite.pdsim.lib;

import java.util.List;
import java.awt.Label;
import java.awt.Panel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.font.*;
import java.awt.Font;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Color;

//import java.awt.*;
//import java.io.*;
import mesquite.lib.*;

/* ======================================================================== */
public class CorTraitCurWindow extends MesquiteWindow {
	Label ModelName;
	
	boolean allowEstimation=false;
	static int insetFromWindow = 40;
	static int minWindowWidth = 200;
	String title="CorTraitWindow";
	Panel contents;
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();

	public CorTraitCurWindow(MesquiteModule module, String title, String name) {
		super(module, true);
		ModelName=new Label(name);
		setWindowSize(minWindowWidth, 100);
		this.title = title;
		contents = getGraphicsArea();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        contents.setLayout(gridbag);

        c.fill = GridBagConstraints.BOTH;
        //c.gridwidth=2;
        c.weightx = 1.0;

		resetTitle();
	} 
	/*.................................................................................................................*/
	/** When called the window will determine its own title.  MesquiteWindows need
	to be self-titling so that when things change (names of files, tree lists, etc.)
	they can reset their titles properly*/
	/*public  makeTextField(String name, String value, MesquiteCommand command){
		
	}*/
    protected void makebutton(String name, GridBagLayout gridbag, GridBagConstraints c) {	
    	Button button = new Button(name);
		gridbag.setConstraints(button, c);
		contents.add(button);
    }
    public TextField makeTextField(String name, String value){
    	return makeTextFieldwLabel(name,value, gridbag, c);
    }
    public TextField makeTextFieldln(String name, String value){
    	return makeTextFieldwLabelln(name,value, gridbag, c);
    }
    public WindowDoubleField makeCommandField(String name, String parameterName, ProbabilityCorContCharModel model, double value, MesquiteCommand command){
    	return makeCommandField(name, command, value, parameterName, model, gridbag, c, false);
    }

    public WindowDoubleField makeCommandFieldln(String name, String parameterName, ProbabilityCorContCharModel model,double value, MesquiteCommand command){
    	return makeCommandField(name, command, value, parameterName, model, gridbag, c, true);
    }    
    public void makeCommandButton(String name, MesquiteCommand command, String arg){
    	makeCommandButton(name, command, arg, false, gridbag, c);
    }
    public CommandCheckBox makeCommandCheckBox(String name, boolean b, MesquiteCommand command){
    	return makeCommandCheckBox(name,b,command, gridbag,c, false);
    }    
    public CommandCheckBox makeCommandCheckBoxln(String name, boolean b, MesquiteCommand command){
    	return makeCommandCheckBox(name,b,command, gridbag,c, true);
    }
    public CommandComboBox makeCommandComboBox(String name,String initState, List content, MesquiteCommand command){
    	return makeCommandComboBox(initState, content, command, name, gridbag,c,false);
    }
    public CommandComboBox makeCommandComboBoxln(String name,String initState, List content, MesquiteCommand command){
    	return makeCommandComboBox(initState, content, command, name, gridbag,c,true);
    }
    protected CommandComboBox makeCommandComboBox(String initState, List content, MesquiteCommand command, String name, GridBagLayout gridbag, GridBagConstraints c, boolean EndLine){
    	CommandComboBox df=new CommandComboBox(initState,content, command, name);
    	//TextField tf = new TextField(str);
    	Label lb = new Label(name);
	   	c.gridwidth = 1;
	   	c.weightx=0;
    	gridbag.setConstraints(lb,c);
    	if (EndLine)
   		   c.gridwidth = GridBagConstraints.REMAINDER; //Last;		
    	else
    		c.gridwidth = 1;
    	c.weightx=1;
    	gridbag.setConstraints(df,c);    	
    	contents.add(lb);
    	contents.add(df);
    	df.setVisible(true);
    	return df;
    }
    
    
    protected WindowDoubleField makeCommandField(String label, MesquiteCommand command, double value, String parameterName, ProbabilityCorContCharModel model, GridBagLayout gridbag, GridBagConstraints c, boolean EndLine){
    	WindowDoubleField df=new WindowDoubleField(command, parameterName, value, model);
    	//TextField tf = new TextField(str);
    	Label lb = new Label(label);
	   	c.gridwidth = 1;
	   	c.weightx=0;
    	gridbag.setConstraints(lb,c);
    	if (EndLine){
    		c.gridwidth = GridBagConstraints.REMAINDER; //Last;            	
    		c.weightx=1;//false
    	}
    	else{
    		c.gridwidth = 1;
    		c.weightx=0;//true
    	}
    	gridbag.setConstraints(df,c);    	
    	contents.add(lb);
    	contents.add(df);
    	df.setVisible(true);
    	return df;
    }
 
    protected void makeCommandButton(String label, MesquiteCommand command, String arg, boolean initState, GridBagLayout gridbag, GridBagConstraints c){
    	CommandButton b=new CommandButton(getOwnerModule(), command, label, arg);
	   	c.gridwidth = 1;
	   	c.weightx=0;
    	gridbag.setConstraints(b,c);
    	contents.add(b);
    	b.setVisible(true);
    }
    
	protected CommandCheckBox makeCommandCheckBox(String label, boolean initState, MesquiteCommand command,GridBagLayout gridbag,GridBagConstraints c, boolean EndLine){
		CommandCheckBox b=new CommandCheckBox(initState,command,label);
		if(EndLine)
			c.gridwidth = GridBagConstraints.REMAINDER; //Last;            	
		else
			c.gridwidth = 1;
	   	c.weightx=0;
    	gridbag.setConstraints(b,c);
    	contents.add(b);
    	b.setVisible(true);	
    	return b;
	}

	
	
    protected TextField makeTextFieldwLabel(String label, String str, GridBagLayout gridbag, GridBagConstraints c){
    	TextField tf = new TextField(str);
    	Label lb = new Label(label);
	   	c.gridwidth = 1;
	   	c.weightx=0;
    	gridbag.setConstraints(lb,c);
	   	c.gridwidth = 1;
    	c.weightx=1;
    	gridbag.setConstraints(tf,c);    	
    	contents.add(lb);
    	contents.add(tf);
    	return tf;
    }
    
    protected TextField makeTextFieldwLabelln(String label, String value, GridBagLayout gridbag, GridBagConstraints c){
    	TextField tf=new TextField(value);
    	Label lb = new Label(label);
        c.gridwidth = 1;//GridBagConstraints.RELATIVE; //next-to-last in row            	
        c.weightx=0;
        gridbag.setConstraints(lb,c);
        c.gridwidth = GridBagConstraints.REMAINDER; //Last;            	
	   	c.weightx=1;
        gridbag.setConstraints(tf,c);
        contents.add(lb);
        contents.add(tf);
        
        /*c.weighty = 0;
    	c.weightx = 0;*/
    	return tf;
    }
	public void resetTitle(){
		setTitle(title);
	}

	/*.................................................................................................................*/
	public void windowResized(){
		super.windowResized();
//		if (sliderWithText!= null)
//			sliderWithText.setWidth(getBounds().width-insetFromWindow);
		if (contents != null){
			contents.invalidate();
			contents.validate();
		}
	}
	/*.................................................................................................................*/
	/*.................................................................................................................*/
	public Dimension getMinimumSize() {
		return new Dimension(minWindowWidth,getWindowHeight());
	}
	/*.................................................................................................................*/
	public Dimension getMaximumSize() {
		return new Dimension(super.getMaximumSize().width, getWindowHeight());  // forces height to not change
	}
	/*.................................................................................................................*/
	/*.................................................................................................................*/
	public Snapshot getSnapshot(MesquiteFile file) {
		Snapshot temp = new Snapshot();
//		temp.addLine("setSweetMin " + sliderWithText.getMinimumSweetValue());
//		temp.addLine("setSweetMax " + sliderWithText.getMaximumSweetValue());
		temp.incorporate(super.getSnapshot(file), false);
		return temp;
	}
	/*.................................................................................................................*/
	public Object doCommand(String commandName, String arguments, CommandChecker checker) {
		//else
		//	return  super.doCommand(commandName, arguments, checker);
		return null;
	}
	public void setColor(Color c){
		setBackground(c);
		ModelName.setBackground(c);
		Panel contents = getGraphicsArea();
		contents.setBackground(c);
		repaintAll();
	}
	public void setText(String s) {
		ModelName.setText(s);
		ModelName.repaint();
		repaint();
	}
	public String getText() {
		return ModelName.getText();
	}

	public void dispose(){
//		getOwnerModule().deleteMenuItem(minItem);
//		getOwnerModule().deleteMenuItem(maxItem);
		super.dispose();
	}
}
