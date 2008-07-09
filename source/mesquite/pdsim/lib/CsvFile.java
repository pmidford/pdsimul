package mesquite.pdsim.lib;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import mesquite.cont.lib.ContinuousHistory;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.Tree;

public class CsvFile {
	public static void MakeCsvFile(CoVarMatrixModel model, String str, Tree tree, ContinuousHistory sim[][], int num_of_runs) throws Exception {
		PrintWriter pw = new PrintWriter(new FileWriter(str+".csv"));
		String LF="\n";
		    
		int num_of_nodes=tree.numberOfNodesInClade(tree.getRoot());
		int num_of_traits=model.num_of_traits;
		int nodevector[]=new int[num_of_nodes];
		for(int x=0;x<num_of_nodes;x++) nodevector[x]=0;
		getnodevector(nodevector,tree,tree.getRoot(), 0);


		for (int y=0;y<num_of_traits;y++){
			pw.print("Trait:"+y+LF);
			pw.print("Run Number ,");
			for(int z=0;z<num_of_nodes;z++){
				pw.print("Node:"+SimNodeNameFormat(tree, nodevector[z])+", ");
			}
			pw.print(""+LF);
			for (int x=0;x<num_of_runs;x++){
				pw.print(x+", ");
				for(int z=0;z<num_of_nodes;z++){
					pw.print(Format(sim[x][y].getState(nodevector[z])));
						if (z<num_of_nodes-1){
							pw.print(", ");
						}
						else pw.print(""+LF);
				}
		    }
		    pw.print(""+LF);
		    pw.print(""+LF);
		}
		pw.close();
	}
	static public String Format (double x){
		//String str;
		NumberFormat formatter = new DecimalFormat("0.0000000000E00");
		return formatter.format(x);
		}
		static public String SimNodeNameFormat(Tree tree, int node){
			//String str;
			//str=tree.getNodeLabel(thisnode);
			//if (str==null){
				return Format2(node);
			//}
			//else return	str;
		}
		static public String Format2(int z){
			String str;
			NumberFormat formater = new DecimalFormat("00");
			str=formater.format(z);
			return str;
		}
		static public int[] getnodevector(int nodevector[],Tree tree,int node, int count){
			System.out.println("node:"+node+" count: "+count);
			while (nodevector[count]!=0) count++;
			nodevector[count]=node;
			int daughters[], num_of_daughters;
			daughters=tree.daughtersOfNode(node);
			num_of_daughters=tree.numberOfDaughtersOfNode(node);
			for (int x=0;x<num_of_daughters;x++){
				getnodevector(nodevector, tree, daughters[x], count);
			}	
			//nodevector[count]=node;
			//tree.
			//nodevector=getnodevector(nodevector, tree, node, count);
			return nodevector;
		}
}
