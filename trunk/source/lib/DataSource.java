package mesquite.pdsim.lib;

import mesquite.lib.duties.*;
import mesquite.lib.*;

public abstract interface DataSource extends Listenable {

	public abstract int getNumRows();
	public abstract int getNumColumns();
	public abstract String getColumnName(int column);
	public abstract String getRowName(int row);
	public abstract String getRowHeader();
	public abstract String getColumnHeader();
	public abstract String getName();
	public abstract boolean isEditable();
	public abstract boolean isEditableDiagonal();
	
	public void rowTouched(int row);
	
	
	public abstract void setCellValue (int row, int column, MesquiteNumber i, boolean notify);
	public abstract MesquiteNumber getCellValue (int row, int column, MesquiteNumber i);
	
	/*public Class getDutyClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}*/
}
