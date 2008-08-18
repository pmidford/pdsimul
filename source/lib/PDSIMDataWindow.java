package mesquite.pdsim.lib;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import mesquite.lib.ColorDistribution;
import mesquite.charMatrices.BasicDataWindowMaker.*;
import mesquite.lib.CommandChecker;
import mesquite.lib.Commandable;
import mesquite.lib.FileElement;
import mesquite.lib.MesquiteBoolean;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteListener;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.MesquiteSubmenuSpec;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.Notification;
import mesquite.lib.StringUtil;
import mesquite.lib.ToolPalette;
import mesquite.lib.duties.*;
import mesquite.lib.table.*;

//import

/*=======================================================*/
/** A window that contains a table in which to edit the Covariance matrix.*/
public class PDSIMDataWindow extends TableWindow implements MesquiteListener {

	DataTable table;
	int windowWidth = 360;
	int windowHeight =300;
	ToolPalette palette = null;
	DataCurator curatorModule;
	TableTool arrowTool,ibeamTool, fillTool, dropperTool;
	MesquiteNumber fillNumber = new MesquiteNumber(0);
	public PDSIMDataWindow (DataCurator curatorModule, MesquiteModule ownerModule){
		super(ownerModule, true); 
		this.curatorModule = curatorModule;
		ownerModule.setModuleWindow(this);
		setWindowSize(windowWidth, windowHeight);
		table = new DataTable(curatorModule, this, 32, 32, getBounds().width - 32, getBounds().height-32, 30);
		table.setOwenerModule(ownerModule);
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
		//addTool(fillTool);
		//addTool(dropperTool);
   	 	//setShowExplanation(true);
   	 	//setShowAnnotation(true);
		resetTitle();
		//TODO I'd like to add some auto sizeinog here;
//		int temp2=table.getWidth();
		//table.setColumnWidth(0, 20);
//		table.setSize(temp2+(20-temp), getBounds().height-32);
		table.setColumnNamesDiagonal(true);
		table.showColumnGrabbers=true;
		table.showRowGrabbers=true;
		
		
		//((ColumnNamesPanel)table.getColumnNamesPanel()).extraRowTop(0);
		//((ColumnNamesPanel)table.getColumnNamesPanel()).decrementInfoStrips();
		//((ColumnNamesPanel)table.getColumnNamesPanel()).setNumInfoStrips(0);
		//System.out.println(((ColumnNamesPanel)table.getColumnNamesPanel()).nameRowHeight());
		table.showRowNumbers = true;
		table.showColumnNumbers = true;

		//table.frameColumnNames(true);
		//table.setConstrainMaxAutoColumn(constrainedCW.getValue());
//		table.setUserMove(true, true);
		//table.set
		table.setVisible(true);
	}
	/*.................................................................................................................*/
	/** When called the window will determine its own title.  MesquiteWindows need
	to be self-titling so that when things change (names of files, tree blocks, etc.)
	they can reset their titles properly*/
	public void resetTitle(){
		setTitle("Edit " + curatorModule.getName()); 
	}
	/*.................................................................................................................*/
    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {
    	 	if (checker.compare(this.getClass(), "Fills the touched cell with current paint", "[column][row]", commandName, "fillTouchCell")) {
   	 		if (table!=null && fillNumber !=null && table.getDataSource()!=null){
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
	   			//TODO, unbreak this.
				//((DataSource)table.getDataSource()).notifyListeners(this, new Notification(MesquiteListener.UNKNOWN),CharacterModel.class, true);
				//((DataSource)table.getDataSource()).notifyListeners(this, new Notification(MesquiteListener.UNKNOWN),CharacterModel.class, false);

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
    DataSource matrix = null;
    public void dispose(){
		if (matrix != null && matrix instanceof FileElement && ((FileElement)matrix).getProject() != null)
			((FileElement)matrix).getProject().getCentralModelListener().removeListener(this);

    	super.dispose();
    }
	/*.................................................................................................................*/
    /** This should be added in an extention*/
   public void setDataSource(DataSource newSource) {
		table.setDataSource(newSource);
		this.matrix = newSource;
		matrix.addListener(this);
		System.out.println("set Listening");
		setTitle("Edit " + curatorModule.getName()+ ": " + newSource.getName());

		if (newSource instanceof FileElement && ((FileElement)newSource).getProject() != null)
			((FileElement)newSource).getProject().getCentralModelListener().addListener(this);
		contentsChanged();
	}
	/** passes which object changed, along with optional Notification object with details (e.g., code number (type of change) and integers (e.g. which character))*/
	public void changed(Object caller, Object obj, Notification notification){
		System.out.println("change detected");
		if (obj == matrix){
			System.out.println("change detected");
			table.updateDataSource();
			repaint();
			contentsChanged();
		}
		//else if(obj == )
	}
	/** passes which object was disposed*/
	public void disposing(Object obj){
		if (obj == table.getDataSource() && ownerModule != null)
			ownerModule.windowGoAway(this);
	}
	/** Asks whether it's ok to delete the object as far as the listener is concerned (e.g., is it in use?)*/
	public boolean okToDispose(Object obj, int queryUser){
		return true;
		
	}
	//TODO extention;
   	/*public void setTraitNum(int num_of_traits){
   		table.setTraitNum(num_of_traits);
   	}*/
	
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
class DataTable extends MesquiteTable {

	DataCurator curatorModule;
	DataSource matrix;
	TableWindow window;
	boolean symetric=true;

	boolean diagonalEditable = false;
	
	public DataTable (DataCurator curatorModule, TableWindow window, int numRowsTotal, int numColumnsTotal, int totalWidth, int totalHeight, int taxonNamesWidth) {
		super(numRowsTotal, numColumnsTotal, totalWidth, totalHeight, taxonNamesWidth, ColorDistribution.getColorScheme(curatorModule.getModule()), true, true);
		this.window= window;
		this.curatorModule=curatorModule;
		frameColumnNames=true;
		frameRowNames=true;

		setEditable(true, false, false, false);
		setSelectable(true, true, true, false, false, false);

		setColumnWidthsUniform(50);
		
		}
	
   	public void setDiagnonalEditable(boolean editable){
   		diagonalEditable = editable;
   	}
   	public boolean getDiagnonalEditable(){
   		return diagonalEditable;
   	}
   	/*
   	public void setTraitNum(int num_of_traits){
   		matrix.setTraitNum(num_of_traits);
   		setNumColumns(num_of_traits);
   		setNumRows(num_of_traits);
   		repaintAll();
   	}*/
   	//
	/*public boolean autoSizeColumns (Graphics g) {  // this is EXTREMELY slow for large matrices.
		FontMetrics fm=g.getFontMetrics(g.getFont());
		int h = fm.getMaxAscent()+ fm.getMaxDescent() + MesquiteModule.textEdgeCompensationHeight; //2 + MesquiteString.riseOffset;
		setRowHeightsUniform(h);
		if (!columnNames.isDiagonal())
			setColumnNamesRowHeight(h);
		String s;
		int tableWIDTHpart =getTableWidth()/3 ;
		boolean changed = false;
		int def = fm.stringWidth("G"); //WPMMAT 12
		if (columnNames.isDiagonal()){
			int def2 = fm.getAscent() + fm.getDescent();
			if (def< def2)
				def = def2;
		}
		int max = def;
		for (int ic = 0; ic<matrix.getNumColumns(); ic++) {
			if (!columnAdjusted(ic)){
				max = def;
				for (int it = 0; it<matrix.getNumRows(); it++) {
					s=getMatrixTextForDisplay(ic, it);
					int lengthString = fm.stringWidth(s);
						if (lengthString>max)
							max = lengthString;
				}
				
				if (getConstrainMaxAutoColumn() && max>tableWIDTHpart)
					max = tableWIDTHpart;
				int newCW = 0;
				int current = getColumnWidth(ic);
				newCW = max + 2 + MesquiteModule.textEdgeCompensationHeight;
				if (newCW != current) {
					setColumnWidth(ic, newCW);
					changed = true;
				}
			}
		}
		return changed;

	}*/
   	public DataSource getDataSource() {
		return matrix;
	}
	public void setDataSource(DataSource source) {
		this.matrix = source;
		if (getNumRows() != source.getNumRows())
			setNumRows(source.getNumRows());
		if (getNumColumns() != source.getNumColumns())
			setNumColumns(source.getNumColumns());
		setEditable(matrix.isEditable(), false, false, false);
		setSelectable(true, true, true, false, false, false);

		repaint();
	}
	public void updateDataSource(){
		if (getNumRows() != matrix.getNumRows())
			setNumRows(matrix.getNumRows());
		if (getNumColumns() != matrix.getNumColumns())
			setNumColumns(matrix.getNumColumns());
		setEditable(matrix.isEditable(), false, false, false);
		setSelectable(true, true, true, false, false, false);		
	}
	public void returnedRowTextModelNotify(int column, int row, String s, boolean notify){
		String names[];
		int x=0;
		System.out.println("returnRowTextModelNotify");		
	}
	/** Called after editing a cell, passing the String resulting. 
	Can be overridden in subclasses to respond to editing.*/
	public void returnedMatrixTextNotify(int column, int row, String s,  boolean notify){ 
		if (column==row && !matrix.isEditableDiagonal())
			return;
		if (!matrix.isEditable())
			return;//		if (StringUtil.blank(s))
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
				matrix.setCellValue(row,column, i, notify);
				if(symetric)
					matrix.setCellValue(column,row, i, notify);
				repaint();
			}
//		}
			
//}
	public void returnedMatrixTextModelNotify(int column, int row, String s,  boolean notify){
		
	}
	/** Called after editing a cell, passing the String resulting. 
	Can be overridden in subclasses to respond to editing.*/
	public void returnedMatrixText(int column, int row, String s){ 

		if (column==row && !matrix.isEditableDiagonal())
			return;
		if (!matrix.isEditable())
			return;
		//if (StringUtil.blank(s))
		//	return;
		// boolean explicitlyUnassigned = ("unassigned".equalsIgnoreCase(s) || "estimate".equalsIgnoreCase(s) || "?".equalsIgnoreCase(s));
		
		MesquiteNumber i = new MesquiteNumber();
		MesquiteNumber c = new MesquiteNumber();
		i.setValue(s);
		//if (i.isCombinable() || i.isInfinite() || explicitlyUnassigned){
		//	if (!i.equals(traits.getTransitionValue(row,column, c))){
				matrix.setCellValue(row,column, i, true);
				if(symetric)
					matrix.setCellValue(column,row, i, true);
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
			if (selected)
				bgColor = Color.black;
			else {
				if (!matrix.isEditableDiagonal() || !matrix.isEditable())
					bgColor = ColorDistribution.veryLightGray;
				else 
					bgColor = Color.white;
			}
			g.setColor(bgColor);
			g.fillRect(x+1,y+1,w-2,h-2);
			g.setColor(fontColor);
			g.drawString(matrix.getCellValue(row,column, null).toString(), x+2, y+h-2);
			autoSizeColumns(g);
		}
		else {
			if (selected)
				bgColor = Color.black;
			else {
				if(!matrix.isEditable())
					bgColor = ColorDistribution.veryLightGray;
				else bgColor = Color.white;
			}
			g.setColor(bgColor);
			g.fillRect(x+1,y+1,w-2,h-2);
			g.setColor(fontColor);
			g.drawString(matrix.getCellValue(row,column, null).toString(), x+2, y+h-2);
			autoSizeColumns(g);
		}
	}
	
	/*...............................................................................................................*/
	public void drawCornerCell(Graphics g, int x, int y,  int w, int h){
		FontMetrics fm=g.getFontMetrics(g.getFont());
		int lineHeight = fm.getAscent() + fm.getDescent() + 4;
		g.drawString(matrix.getRowHeader(), x+3, y+h-fm.getDescent());
		g.drawString(matrix.getColumnHeader(), x+w -fm.stringWidth("Trait Name"), y+lineHeight);
	}
	public String getMatrixText(int column, int row){
		if (column==row) {
			return matrix.getCellValue(row,column, null).toString();
		}
		else  {
			return matrix.getCellValue(row,column, null).toString();
		}
	}
	
	public void drawColumnNameCell(Graphics g, int x, int y, int w, int h, int column){
		autoSizeColumns(g);

		if (matrix == null)
			return;
		boolean annotationAvailable = isAttachedNoteAvailable(column, -1);
		Color fillColor = null;
		boolean selected = isColumnNameSelectedAnyWay(column);
		fillColor = getColumnNameFillColor(column, Color.white, false, selected, false, true);

		if (fillColor != null){
			Color c = g.getColor();
			g.setColor(fillColor);
			columnNames.fillCell(g, x, y, w, h, selected);
			/*	g.fillRect(x+1,y+1,w-1,h-1);
			if (selected)
				GraphicsUtil.fillTransparentSelectionRectangle(g,x+1,y+1,w-1,h-1);*/
			if (c!=null) g.setColor(c);

		}
		float[] hsb = new float[3];

		hsb[0]=hsb[1]=hsb[2]= 1;
		Color.RGBtoHSB(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), hsb);
		Color oldColor = null;
		if (annotationAvailable){
			oldColor = g.getColor();
			g.setColor(getContrasting(selected, hsb, Color.white, Color.black));
			g.drawLine(x+w-2,y+1, x+w-2,y+2); //left
			g.drawLine(x+w-2,y+2, x+w,y+2); //bottom
			g.drawLine(x+w,y+1, x+w,y+2); //right
			g.drawLine(x+w-2,y, x+w,y); //top
			if (!selected){
				g.setColor(Color.white);
				g.drawLine(x+w-1,y+1, x+w-1,y+1);
			}
			g.setColor(oldColor);
		}
		if (selected){
			g.setColor(Color.white);
		}

		oldColor = g.getColor();


		Color textColor = null;
		textColor = getContrasting(selected, hsb, Color.white, Color.black);
		g.setColor(textColor);
//		((ColumnNamesPanel)table.getColumnNamesPanel()).setDiagonalHeight(h);

		String name = matrix.getColumnName(column);
		//String s = matrix.getAnnotation(column);
		//if (!StringUtil.blank(s)) 
		//	name = "*" + name;

		g.drawString(name, 10+getNameStartOffset(), StringUtil.getStringVertPosition(g,y,h, null));

		g.setColor(oldColor);
	}
	public void drawColumnNamesPanelExtras(Graphics g, int left, int top, int width, int height) {
		Color oldColor = g.getColor();
		for (int extraRow = 0; extraRow<this.numDataColumnNamesAssistants(); extraRow++) {
			DataColumnNamesAssistant assistant = this.getDataColumnNamesAssistant(extraRow);
			for (int c=getFirstColumnVisible(); (c<numColumnsTotal) && (c<=getLastColumnVisible()); c++) { 
				int leftSide = columnNames.startOfColumn(c);
				int topSide = columnNames.extraRowTop(extraRow);

				if (assistant!=null)
					assistant.drawInCell(c,g,leftSide,topSide, columnNames.columnWidth(c), columnNames.rowHeight(-1), false);
				g.setColor(Color.gray); 
				g.drawRect(leftSide, topSide, columnNames.columnWidth(c), columnNames.rowHeight(-1));
			}
		}
		g.setColor(oldColor);
	}
	public String getColumnNameText(int column){
		return matrix.getColumnName(column);
	}

	public DataColumnNamesAssistant getDataColumnNamesAssistant(int num){
		int count = 0;
		if (ownerModule==null) return null;
		for (int i = 0; i<ownerModule.getNumberOfEmployees(); i++) { 
			MesquiteModule e=(MesquiteModule)ownerModule.getEmployeeVector().elementAt(i);
			if (e instanceof DataColumnNamesAssistant) {
				if (count>=num)
					return (DataColumnNamesAssistant)e;
				count++;
			}
		}
		return null;
	}
	/*...*/
	MesquiteModule ownerModule;
	public void setOwenerModule(MesquiteModule owenerModule){
		this.ownerModule=owenerModule;
	}
	public int numDataColumnNamesAssistants(){
		int num=0;
		if (ownerModule == null)
			return 0;
		for (int i = 0; i<ownerModule.getNumberOfEmployees(); i++) { 
			MesquiteModule e=(MesquiteModule)ownerModule.getEmployeeVector().elementAt(i);
			if (e instanceof DataColumnNamesAssistant) {
				num++;
			}
		}
		return num;
	}
	Color getContrasting(boolean selected, float[] hsb, Color light, Color dark){
		if (selected)
			return light;
		else if (hsb[2]>0.5)
			return dark;
		else
			return light;
	}
	public void drawRowNameCell(Graphics g, int x, int y,  int w, int h, int row){
		autoSizeColumns(g);
		g.drawString(matrix.getRowName(row), x+2, y+h-2);
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
	public void rowNameTouched(int row, int regionInCellH, int regionInCellV, int modifiers, int clickCount) {
		returnedRowTextModelNotify(row, 0,"s", false);
		//System.out.println("");
		//System.out.println("touched" + row);
		
		matrix.rowTouched(row);
		//traits.ModelNamePopup(column);
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
	