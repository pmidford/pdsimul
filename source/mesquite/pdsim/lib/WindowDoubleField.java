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
public class WindowDoubleField extends Panel implements MiniControl, Explainable, ImageOwner, ActionListener, TextListener {
	double currentValue;
	boolean adjustingSizes = true;
	boolean allowEstimation=false;
	String currentText="";
	TextField tf;
	TextField dummy;
	EnterButton enterButton;
	private int textBoxWidth = 34;	
	private int textBoxHeight = 18;
	int widthSet = 0;
	public int totalWidth = 0;  
	public int totalHeight=0; 
	private int oldTextBoxWidth = 1;
	private int oldTextBoxHeight = 1;
	Color bg = Color.white;
	int edge = 4;
	int edgeRight = 8;
//	boolean enforceMin, enforceMax;

	MesquiteCommand command;
	int enterWidth = 8;
	// be passed max and min values  
	
	public WindowDoubleField (MesquiteCommand command, double currentValue) {
		this.currentValue = currentValue;
		this.command=command;
		initValues();
	}
	/*.................................................................................................................*/
	/*.................................................................................................................*/
  	 public void setText(String t) {
  	 	tf.setText(t);
  	 	currentText = t;
  	 }
	public void setWidth(int w){
		Point loc = getLocation();
		widthSet = w;
		calcTextBoxSize(getGraphics());
		setTotalSize();
		//slider.setLocation(10, textBoxHeight);
		//valueToPixel.setTotalPixels(slider.sliderRangeInPixels());
	//setLocation(loc);
	}
	/*.................................................................................................................*/
/*  	 public void setAllowEstimation(boolean allow) {
  	 	allowEstimation = allow;
  	 }*/
	/*.................................................................................................................*/
  	 public Dimension getPreferredSize() {
  	 	return new Dimension(totalWidth, totalHeight);
  	 }
	/*.................................................................................................................*/
  	 public EnterButton getEnterButton() {
  	 	return enterButton;
  	 }
	/*.................................................................................................................*/
  	 public void setTotalSize() {
		if (textBoxWidth  + EnterButton.MIN_DIMENSION+2 >widthSet)
			totalWidth = textBoxWidth + EnterButton.MIN_DIMENSION+2;
		else
			totalWidth = widthSet  + EnterButton.MIN_DIMENSION+2;
		totalHeight = textBoxHeight + 24;
		setSize(totalWidth, totalHeight);
  	 }
	/*.................................................................................................................*/
	private void initValues(){
		adjustingSizes = true;
		calcTextBoxSize(getGraphics());
		setTotalSize();
		setLayout(null);
		add(enterButton = new EnterButton(this, true));
		enterButton.setEnabled(false);
		currentText = MesquiteDouble.toString(currentValue);
		add(tf = new TextField("888888", 2));
			tf.setText(currentText);
		
		tf.addActionListener(this);
		tf.addTextListener(this);
		tf.setVisible(false);
		enterButton.setVisible(false);
		tf.setSize(1, 1);
		tf.setBackground(Color.white);
		recalcPositions(true);
		setBackground(bg);
		adjustingSizes = false;
	}
	private void calcTextBoxSize(Graphics g){
		if (g!=null) {
			Font f = g.getFont();
			FontMetrics fm = g.getFontMetrics(f);
			int sw1 = MesquiteInteger.maximum(fm.stringWidth("888888888888888888"), fm.stringWidth(tf.getText()));  // width of text in text field
			int sh = fm.getMaxAscent()+fm.getMaxDescent();
			
			textBoxWidth = sw1 + MesquiteModule.textEdgeCompensationWidth; //34
			textBoxHeight =  sh + MesquiteModule.textEdgeCompensationHeight; //18
			if (tf!= null) {
				if (Math.abs(tf.getBounds().width -  textBoxWidth)>2 || Math.abs(tf.getBounds().height - textBoxHeight)>0){
					tf.setBounds(0, 0, textBoxWidth, textBoxHeight);
				}
				if (enterButton != null) {
					if (Math.abs(enterButton.getLocation().x - ( tf.getBounds().x + tf.getBounds().width + 4))>2 || enterButton.getLocation().y != 2)
						enterButton.setLocation(tf.getBounds().x + tf.getBounds().width + 4, 2);
					enterButton.repaint();
				}
			}
		}
	}
	private boolean recalcPositions(boolean doRepaint){
		Graphics g = getGraphics();
		if (g != null){
			adjustingSizes = true;
			calcTextBoxSize(g);
			setTotalSize();
			if (doRepaint)
				repaint();
			if (getParent()!= null)
				getParent().repaint();
			adjustingSizes = false;
			g.dispose();
		}
		return true;
	}
	public void paint(Graphics g) { 
		//recalcPositions(g, false);
	}
	public void repaint() { 
		recalcPositions( false);
	}
	public void printAll(Graphics g) { 
	}
	public void paintComponents(Graphics g) { 
		if (g instanceof PrintGraphics)
			return;
		else
			super.paintComponents(g);
	}
	public void printComponents(Graphics g) { 
	}
	public void print(Graphics g) { 
	}
	private boolean checkBackground(){
		if (getParent() !=null && getBackground()!=null && !getBackground().equals(getParent().getBackground())) {
			bg =getParent().getBackground();
			setBackground(bg);
			if (tf!=null)
				tf.setBackground(Color.white);
			if (enterButton!=null)
				enterButton.setBackground(bg);
			return true;
		}
		else return false;
	}
	public void setVisible(boolean b) {
		if (b)
			checkBackground();
		super.setVisible(b);
		tf.setVisible(b);
		enterButton.setVisible(b);
		repaint();
	}
	public void setColor(Color c) {
		tf.setForeground(c);
	}
	public void setCurrentValue (double i) {  
		if (!MesquiteDouble.isCombinable(i))
			return;
		currentValue=i;
		tf.setText(MesquiteDouble.toString(currentValue));
		enterButton.setEnabled(false);
	}
	public MesquiteNumber getCurrentValue () {  
			return  new MesquiteNumber(currentValue);
	}
	public boolean textValid(String s){
		double d = MesquiteDouble.fromString(s);
		if (MesquiteString.explicitlyUnassigned(s))
			return true;
		if (!MesquiteDouble.isCombinable(d))
			return false;
		return true;
	}
	 public void textValueChanged(TextEvent e) {
	 	String s = tf.getText();
	 	double d = MesquiteDouble.fromString(s);
	 	if (d!=currentValue) {
	 		enterButton.setEnabled(textValid(s));
	 	}
 		enterButton.setEnabled(false);
	}
	public void acceptText(){
		if (MesquiteWindow.getQueryMode(this)) {
			MesquiteWindow.respondToQueryMode("WindowDoubleField", command, this);
			return;
		}
		String s = tf.getText();
		if (MesquiteString.explicitlyUnassigned(s) && allowEstimation){
			currentValue=MesquiteDouble.unassigned;
			command.doItMainThread(s, CommandChecker.getQueryModeString("WindowDoubleField", command, this), this);
			return;
		}
		if (!textValid(s))
			return;
		currentText=s;
		double d = MesquiteDouble.fromString(s);
		setCurrentValue(d);
		//enterButton.repaint();
		command.doItMainThread(MesquiteDouble.toString(d), CommandChecker.getQueryModeString("Mini slider", command, this), this);
		enterButton.setEnabled(false);
	}
	public void actionPerformed(ActionEvent e){
		//Event queue
			acceptText();
	}
	public String getExplanation(){ //TODO: this should use string passed in constructor
		return "This is a number control with text entry and slider";
	}
	public String getImagePath(){ //TODO: this should use path to standard image
		return null;
	}
	
}