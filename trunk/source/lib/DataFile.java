package mesquite.pdsim.lib;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import mesquite.cont.lib.ContinuousHistory;
import mesquite.lib.ExtensibleDialog;
import mesquite.lib.RadioButtons;
import mesquite.lib.SingleLineTextField;
import mesquite.lib.Tree;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteFile;

public class DataFile {
	public static void MakeDataFileWithDialog(DataSource data, String defaultName, MesquiteModule module) throws Exception{
		System.out.println("Saving results to external file");
		String dir;
		
		dir=MesquiteFile.saveFileAsDialog("please enter a file name"); //chooseDirectory("please chose a directory to save you file", module.getPath());
		
		System.out.println(dir);
		
		ExtensibleDialog fileDialog=new ExtensibleDialog (module, "Save File:");
		//SingleLineTextField nameField;
		//nameField=fileDialog.addTextField(defaultName);
		RadioButtons rb = fileDialog.addRadioButtons(new String[]{"use Windows/Dos LF? (\\r\\n)","use *n?x LF? (\\n)"},0);

		SingleLineTextField FieldDel;
		FieldDel=fileDialog.addTextField("Field Delineator",",", 1);
			
		fileDialog.completeAndShowDialog();
		String name; 
		String FieldDelineator;
		String LF;
		if (rb.getValue()==0) LF="\r\n";
		else LF="\n";
		
		FieldDelineator=FieldDel.getText();
		name=dir+".csv";
		
        File file = new File(dir);
		PrintWriter pw = new PrintWriter(new FileWriter(file));
	    for (int x=0;x<data.getNumColumns();x++){
			pw.print("\""+data.getColumnName(x)+"\""+FieldDelineator);//(nodevector[z])));
	    }
	    pw.print(LF);
		for (int x=0;x<data.getNumRows();x++){
			pw.print(data.getRowName(x)+FieldDelineator);//(nodevector[z])));
			for(int y=0;y<data.getNumColumns();y++){
				pw.print(Format(data.getCellValue(x, y, null).getDoubleValue())+FieldDelineator);//(nodevector[z])));
			}
			pw.print(LF);
		}
		pw.close();
	}
	public static void MakeDataFile(DataSource data, String name, String FieldDelineator,String LF) throws Exception {
		PrintWriter pw = new PrintWriter(new FileWriter(name+".csv"));
	    for (int x=0;x<data.getNumColumns();x++){
			pw.print("\""+data.getColumnName(x)+"\""+FieldDelineator);//(nodevector[z])));
	    }
		for (int x=0;x<data.getNumRows();x++){
			pw.print(data.getRowName(x)+FieldDelineator);//(nodevector[z])));
			for(int y=0;y<data.getNumColumns();y++){
				pw.print(Format(data.getCellValue(x, y, null).getDoubleValue())+FieldDelineator);//(nodevector[z])));
			}
			pw.print(LF);
		}
		pw.close();
	}
	static public String Format (double x){
		NumberFormat formatter = new DecimalFormat("0.0000000000E00");
		return formatter.format(x);
	}
}

