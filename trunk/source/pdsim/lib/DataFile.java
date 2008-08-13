package mesquite.pdsim.lib;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import mesquite.cont.lib.ContinuousHistory;
import mesquite.lib.Tree;

public class DataFile {
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

