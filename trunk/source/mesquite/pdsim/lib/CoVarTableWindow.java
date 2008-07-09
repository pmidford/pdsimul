package mesquite.pdsim.lib;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import mesquite.lib.ColorDistribution;
import mesquite.lib.CommandChecker;
import mesquite.lib.Commandable;
import mesquite.lib.FileElement;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteListener;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.Notification;
import mesquite.lib.StringUtil;
import mesquite.lib.ToolPalette;
import mesquite.lib.characters.CharacterModel;
import mesquite.lib.duties.CharModelCurator;
import mesquite.lib.table.MesquiteTable;
import mesquite.lib.table.TableTool;
import mesquite.lib.table.TableWindow;
//import

/*=======================================================*/
/** A window that contains a table in which to edit the Covariance matrix.*/
public class CoVarTableWindow extends TableWindow implements MesquiteListener {
	CoVarTable table;
	int windowWidth = 360;
	int windowHeight =300;
	ToolPalette palette = null;
	CoVarMatrixCurator curatorModule;
	TableTool arrowTool,ibeamTool, fillTool, dropperTool;
	MesquiteNumber fillNumber = new MesquiteNumber(0);
	public CoVarTableWindow (CoVarMatrixCurator curatorModule, MesquiteModule ownerModule){
		super(ownerModule, true); 
		this.curatorModule = curatorModule;
		ownerModule.setModuleWindow(this);
		setWindowSize(windowWidth, windowHeight);
		table = new CoVarTable(curatorModule, this, 32, 32, getBounds().width - 32, getBounds().height-32, 30);
		getGraphicsArea().setLayout(new BorderLayout());
		addToWindow(table);
   	 	table.setSize(windowWidth, windowHeight);
		String selectExplanation = "This tool selects items in the matrix.  By holding down shift while clicking, the selection will be extended from the first to the last touched cell. ";
		selectExplanation += " A block of cells can be selected either by using shift-click to extend a previous selection, or by clicking on a cell and dragging with the mouse button still down";
		selectExplanation += " Discontinous selections are allowed, and can be obtained by a \"meta\"-click (right mouse button click, or command-click on a MacOS system). ";
		arrowTool = new TableTool(this, "arrow", MesquiteModule.getRootImageDirectoryPath(),"arrow.gif", 4, 2, "Select", selectExplanation, MesquiteModule.makeCommand("arrowTouchCell",  this) , MesquiteModule.makeCommand("arrowDragCell",  this), MesquiteModule.makeCommand("arrowDropCell",  this));
		arrowTool.setIsArrowTool(true);
		arrowTool.setUseTableTouchRules(true);
		addTool(arrowTool);
  	 	setCurrentTool(arrowTool);
 		if (arrowTool!=null)
 			arrowTool.setInUse(true);
		ibeamTool = new TableTool(this, "ibeam", MesquiteModule.getRootImageDirectoryPath(),"ibeam.gif", 7,7,"Edit", "This tool can be used to edit the contents of cells in the matrix.", MesquiteModule.makeCommand("editCell",  (Commandable)table) , null, null);
		addTool(ibeamTool);
		ibeamTool.setWorksOnRowNames(true);

		fillTool = new TableTool(this, "fill", MesquiteModule.getRootImageDirectoryPath(),"bucket.gif", 13,14,"Fill with 1", "This tool fills selected cells with text.  The text to be used can be determined by using the dropper tool.", MesquiteModule.makeCommand("fillTouchCell",  this) , null, null);
		fillTool.setOptionsCommand(MesquiteModule.makeCommand("touchTool",  this));
		dropperTool = new TableTool(this, "dropper", MesquiteModule.getRootImageDirectoryPath(),"dropper.gif", 1,14,"Copy value", "This tool fills the paint bucket with the text in the cell touched on", MesquiteModule.makeCommand("dropperTouchCell",  this) , null, null);
		addTool(fillTool);
		addTool(dropperTool);
   	 	setShowExplanation(true);
   	 	setShowAnnotation(true);
		resetTitle();
		//TODO I'd like to add some auto sizeinog here;
		int temp=table.getColumnWidth(0);		
		int temp2=table.getWidth();
		table.setColumnWidth(0, 20);
		table.setSize(temp2+(20-temp), getBounds().height-32);
		
		table.setVisible(true);
	}
	/*.................................................................................................................*/
	/** When called the window will determine its own title.  MesquiteWindows need
	to be self-titling so that when things change (names of files, tree blocks, etc.)
	they can reset their titles properly*/
	public void resetTitle(){
		setTitle("Edit " + curatorModule.getNameOfModelClass()); 
	}
	/*.................................................................................................................*/
    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {
    	 	if (checker.compare(this.getClass(), "Fills the touched cell with current paint", "[column][row]", commandName, "fillTouchCell")) {
   	 		if (table!=null && fillNumber !=null && table.getModel()!=null){
	   	 		MesquiteInteger io = new MesquiteInteger(0);
	   	 		String entry = fillNumber.toString();
	   			int column= MesquiteInteger.fromString(arguments, io);
	   			int row= MesquiteInteger.fromString(arguments, io);
	   			if (!table.rowLegal(row)|| !table.columnLegal(column))
	   				return null;
	   			if (table.anyCellSelected()) {
		   			if (table.isCellSelected(column, row)) {
						for (int i=0; i<table.getNumColumns(); i++)
							for (int j=0; j<table.getNumRows(); j++)
								if (table.isCellSelected(i,j)) {
									table.returnedMatrixTextNotify(i,j, entry,false);
								}
		 	   			table.repaintAll();
					}
				}
				else if (table.anyRowSelected()) {
		   			if (table.isRowSelected(row)) {
						for (int j=0; j<table.getNumRows(); j++) {
							if (table.isRowSelected(j))
								for (int i=0; i<table.getNumColumns(); i++)
									table.returnedMatrixTextNotify(i,j, entry,false);
						}
		 	   			table.repaintAll();
					}
				}
				else if (table.anyColumnSelected()) {
		   			if (table.isColumnSelected(column)) {
						for (int i=0; i<table.getNumColumns(); i++){
							if (table.isColumnSelected(i))
								for (int j=0; j<table.getNumRows(); j++) 
									table.returnedMatrixTextNotify(i,j, entry,false);
						}
		 	   			table.repaintAll();
					}
				}
				//else if (table.anyRowNameSelected()){

				//}
				else {
					table.returnedMatrixTextNotify(column, row, entry, false);
		 	   		table.repaintAll();
				}
				((CharacterModel)table.getModel()).notifyListeners(this, new Notification(MesquiteListener.UNKNOWN),CharacterModel.class, true);
				((CharacterModel)table.getModel()).notifyListeners(this, new Notification(MesquiteListener.UNKNOWN),CharacterModel.class, false);

			}
    	 	}
     	 	else if (checker.compare(this.getClass(), "Queries the user what paint to use", null, commandName, "touchTool")) {
   	 		if (table!=null){
				String fillString = MesquiteString.queryString(this, "Fill value", "Value with which to fill using paint bucket:", "");
		   		if (StringUtil.blank(fillString))
		   			return null;
			//	table.returnedRowTextModelNotify(0,i,entry,false);
				fillNumber.setValue(fillString);
	   			fillTool.setDescription("Fill with \"" + fillNumber.toString()+ " \"");
	   			dropperTool.setDescription("Copy value (current: " + fillNumber.toString() + ")");
				toolTextChanged();
			}
    	 	}
   	 	else if (checker.compare(this.getClass(), "Fills the paint bucket with the string of the selected cell", "[column][row]", commandName, "dropperTouchCell")) {
   	 		if (table!=null){
	   	 		MesquiteInteger io = new MesquiteInteger(0);
	   			int column= MesquiteInteger.fromString(arguments, io);
	   			int row= MesquiteInteger.fromString(arguments, io);
	   			if (!table.rowLegal(row)|| !table.columnLegal(column))
	   				return null;
	   			String fillString = table.getMatrixText(column, row);
		   		fillNumber.setValue(fillString);
	   			fillTool.setDescription("Fill with \"" + fillNumber.toString()+ " \"");
	   			dropperTool.setDescription("Copy value (current: " + fillNumber.toString() + ")");
				toolTextChanged();
			}
    	 	}

    	 	else
    	 		return  super.doCommand(commandName, arguments, checker);
	return null;
   	 }
    CorTraitModel model = null;
    public void dispose(){
		if (model != null && model instanceof FileElement && ((FileElement)model).getProject() != null)
			((FileElement)model).getProject().getCentralModelListener().removeListener(this);

    	super.dispose();
    }
	/*.................................................................................................................*/
	public void setModel(CorTraitModel other_model) {
		table.setModel(other_model);
		this.model = other_model;
		setTitle("Edit " + curatorModule.getNameOfModelClass()+ ": " + other_model.getName());

		if (other_model instanceof FileElement && ((FileElement)other_model).getProject() != null)
			((FileElement)other_model).getProject().getCentralModelListener().addListener(this);
		contentsChanged();
	}
	/** passes which object changed, along with optional Notification object with details (e.g., code number (type of change) and integers (e.g. which character))*/
	public void changed(Object caller, Object obj, Notification notification){
	
		if (obj == model)
			contentsChanged();
	}
	/** passes which object was disposed*/
	public void disposing(Object obj){
		if (obj == table.getModel() && ownerModule != null)
			ownerModule.windowGoAway(this);
	}
	/** Asks whether it's ok to delete the object as far as the listener is concerned (e.g., is it in use?)*/
	public boolean okToDispose(Object obj, int queryUser){
		return true;
		
	}

   	public void setTraitNum(int num_of_traits){
   		table.setTraitNum(num_of_traits);
   	}
   	public void setDiagnonalEditable(boolean editable){
   		table.setDiagnonalEditable(editable);
   	}
   	public boolean getDiagnonalEditable(){
   		return table.getDiagnonalEditable();
   	}
	public void windowResized() {
		super.windowResized();
	   	if (MesquiteWindow.checkDoomed(this))
	   		return;
   	 	if (table!=null && ((getHeight()!=windowHeight) || (getWidth()!=windowWidth))) {
   	 		windowHeight =getHeight();
   	 		windowWidth = getWidth();
   	 		table.setSize(windowWidth, windowHeight);
   	 	}
		MesquiteWindow.uncheckDoomed(this);
	}
	public MesquiteTable getTable(){
		return table;
	}
}

	/*=======================================================*/
class CoVarTable extends MesquiteTable {
	CoVarMatrixCurator curatorModule;
	CorTraitModel traits;
	MesquiteWindow window;

	boolean diagonalEditable = false;
	
	public CoVarTable (CoVarMatrixCurator curatorModule, MesquiteWindow window, int numRowsTotal, int numColumnsTotal, int totalWidth, int totalHeight, int taxonNamesWidth) {
		super(numRowsTotal, numColumnsTotal, totalWidth, totalHeight, taxonNamesWidth, ColorDistribution.getColorScheme(curatorModule), true, true);
		this.window= window;
		this.curatorModule=curatorModule;
		frameColumnNames=false;
		frameRowNames=false;

		setEditable(true, false, false, false);
		setSelectable(true, false, false, false, false, false);
		setColumnWidthsUniform(48);
	}
   	public void setDiagnonalEditable(boolean editable){
   		diagonalEditable = editable;
   	}
   	public boolean getDiagnonalEditable(){
   		return diagonalEditable;
   	}
   	public void setTraitNum(int num_of_traits){
   		traits.setTraitNum(num_of_traits);
   		setNumColumns(num_of_traits);
   		setNumRows(num_of_traits);
   		repaintAll();
   	}
	public CorTraitModel getModel() {
		return traits;
	}
	public void setModel(CorTraitModel traits) {
		this.traits = traits;
		if (getNumRows() != traits.getTraitNum())
			setNumRows(traits.getTraitNum());
		if (getNumColumns() != traits.getTraitNum())
			setNumColumns(traits.getTraitNum());
		repaint();
	}
	public void returnedRowTextModelNotify(int column, int row, String s, boolean notify){
		String names[];
	//	names=curatorModule.getModelVectorNames();
		int x=0;
		System.out.println("returnRowTextModelNotify");		
		/*while(names[x]!=null){
			System.out.println(names[x]);
			x++;
		}*/
		
	}
	/** Called after editing a cell, passing the String resulting. 
	Can be overridden in subclasses to respond to editing.*/
	public void returnedMatrixTextNotify(int column, int row, String s,  boolean notify){ 
//		if (column==row && !diagonalEditable)
//			return;
//		if (StringUtil.blank(s))
//			return;
//		boolean explicitlyUnassigned = ("unassigned".equalsIgnoreCase(s) || "estimate".equalsIgnoreCase(s) || "?".equalsIgnoreCase(s));
/*		if (column==0){
			String names[];
			names=curatorModule.getModelVectorNames();
			int x=0;
			System.out.println("returnRowTextModelNotify");		
			while(names[x]!=null){
				System.out.println(names[x]);
			}
		}
		else {
			System.out.println(column+row);*/
			MesquiteNumber i = new MesquiteNumber();
			MesquiteNumber c = new MesquiteNumber();
			i.setValue(s);
//		if (i.isCombinable() || i.isInfinite() || explicitlyUnassigned){
//			if (!i.equals(traits.getTransitionValue(row,column, c))){
				traits.setTransitionValue(row,column, i, notify);
				traits.setTransitionValue(column,row, i, notify);
				repaint();
			}
//		}
			
//}
	public void returnedMatrixTextModelNotify(int column, int row, String s,  boolean notify){
		
	}
	/** Called after editing a cell, passing the String resulting. 
	Can be overridden in subclasses to respond to editing.*/
	public void returnedMatrixText(int column, int row, String s){ 

		//if (column==row && !diagonalEditable)
		//	return;
		//if (StringUtil.blank(s))
		//	return;
		// boolean explicitlyUnassigned = ("unassigned".equalsIgnoreCase(s) || "estimate".equalsIgnoreCase(s) || "?".equalsIgnoreCase(s));
		
		MesquiteNumber i = new MesquiteNumber();
		MesquiteNumber c = new MesquiteNumber();
		i.setValue(s);
		//if (i.isCombinable() || i.isInfinite() || explicitlyUnassigned){
		//	if (!i.equals(traits.getTransitionValue(row,column, c))){
				traits.setTransitionValue(row,column, i, true);
				traits.setTransitionValue(column,row, i, true);
				repaint();
		//	}
		//}
			
	}
	public boolean useString(int column, int row){
		return false;
	}
	
	public void drawMatrixCell(Graphics g, int x, int y,  int w, int h, int column, int row, boolean selected){  
		Color fontColor, bgColor;
		if (selected)
			fontColor = Color.white;
		else 
			fontColor = Color.black;
		if (column==row) {
			if (diagonalEditable)
				bgColor = ColorDistribution.veryLightGray;
			else 
				bgColor = Color.gray;
			g.setColor(bgColor);
			g.fillRect(x+1,y+1,w-2,h-2);
			g.setColor(fontColor);
			g.drawString(traits.getTransitionValue(row,column, null).toString(), x+2, y+h-2);
			
		}
		else {
			if (selected)
				bgColor = Color.black;
			else 
				bgColor = Color.white;
			g.setColor(bgColor);
			g.fillRect(x+1,y+1,w-2,h-2);
			g.setColor(fontColor);
			g.drawString(traits.getTransitionValue(row,column, null).toString(), x+2, y+h-2);
		}
	}
	
	/*...............................................................................................................*/
	public void drawCornerCell(Graphics g, int x, int y,  int w, int h){
		FontMetrics fm=g.getFontMetrics(g.getFont());
		int lineHeight = fm.getAscent() + fm.getDescent() + 4;
		g.drawString("Model Name", x+3, y+h-fm.getDescent());
		g.drawString("Model Name", x+w -fm.stringWidth("Trait Name"), y+lineHeight);
	}
	public String getMatrixText(int column, int row){
		if (column==row) {
			return traits.getTransitionValue(row,column, null).toString();
		}
		else  {
			return traits.getTransitionValue(row,column, null).toString();
		}
	}
	
	public void drawColumnNameCell(Graphics g, int x, int y, int w, int h, int column){
		g.drawString(curatorModule.getShortModelName(column), x+2, y+h-2);
	}
	public void drawRowNameCell(Graphics g, int x, int y,  int w, int h, int row){
		g.drawString(traits.getLongModelName(row), x+2, y+h-2);
	}
	/*...............................................................................................................*/
	public void cellTouched(int column, int row, int regionInCellH, int regionInCellV, int modifiers, int clickCount) {
		if (column==row && !diagonalEditable)
			return;
		if (window.getCurrentTool().isArrowTool())
			super.cellTouched(column, row, regionInCellH, regionInCellV, modifiers, clickCount);
		else
			((TableTool)window.getCurrentTool()).cellTouched(column, row, regionInCellH, regionInCellV, modifiers);
		repaintAll();
	}
	public void rowNameTouched(int column, int regionInCellH, int regionInCellV, int modifiers, int clickCount) {
		returnedRowTextModelNotify(column, 0,"s", false);
		System.out.println("");
		System.out.println("touched" + column);
		
		traits.ModelNamePopup(column);
		//window.
		
		repaintAll();
	}
	/*...............................................................................................................*/
	public void cellDrag(int column, int row, int regionInCellH, int regionInCellV, int modifiers) {
		if (window.getCurrentTool().isArrowTool()) 
			super.cellDrag(column, row, regionInCellH,  regionInCellV, modifiers);
		else
		((TableTool)window.getCurrentTool()).cellDrag(column, row, regionInCellH,  regionInCellV, modifiers);
	}
	/*...............................................................................................................*/
	public void cellDropped(int column, int row, int regionInCellH, int regionInCellV, int modifiers) {
		if (column==row && !diagonalEditable)
			return;
		if (window.getCurrentTool().isArrowTool()) 
			super.cellDropped(column, row,  regionInCellH,  regionInCellV, modifiers);
		else
		  ((TableTool)window.getCurrentTool()).cellDropped(column, row, regionInCellH, regionInCellV, modifiers);
	}
}