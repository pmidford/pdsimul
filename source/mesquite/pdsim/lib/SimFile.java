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

public class SimFile {
	public static void MakeSimFile(CoVarMatrixModel model, String str, Tree tree, ContinuousHistory sim[][], int num_of_runs) throws Exception {
	    PrintWriter pw = new PrintWriter(new FileWriter(str+".sim"));
	    MesquiteNumber toss=null;
	    String LF="\r\n"; //end line char.
		pw.print("Output from Mesquite.pdsim: continuous characters simulated on a phylogenetic tree."+LF);
//		plnw.println();
		pw.print("Data from ");
		pw.print(str);
	    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//	    DateFormat time = new SimpleDateFormat("HH:mm:ss");
	    Date date = new Date();
	    dateFormat.format(date);
		pw.print(" "+date+LF);
		pw.print(LF);
//		dateFormat.format(str2);
//		pw.println(" at "+str2);
		pw.print("    Number of Tips  =  "+tree.getNumTaxa()+LF);
		pw.print("Phylogenetic tree from input file: "+tree.getName()+LF);
		pw.print("(Node name, \t Left child, \t Right child, \t Ancestor: name, \t branch-length, \t number descendants.)"+LF);

		int num_of_nodes=tree.numberOfNodesInClade(tree.getRoot());
		int nodevector[]=new int[num_of_nodes];
		for(int x=0;x<num_of_nodes;x++) nodevector[x]=0;
		getnodevector(nodevector,tree,tree.getRoot(), 0);
		//for(int x=0;x<num_of_nodes;x++) System.out.println(nodevector[x]);

		for(int x=0;x<num_of_nodes;x++){
			if(!tree.nodeIsTerminal(nodevector[x])) pw.print("  "+SimNodeNameFormat(tree,nodevector[x])+"\t"+SimNodeNameFormat(tree,tree.firstDaughterOfNode(nodevector[x]))+"\t"+Format(tree.getBranchLength(tree.firstDaughterOfNode(nodevector[x]), 1.0))+"\t"+tree.numberOfNodesInClade(tree.firstDaughterOfNode(nodevector[x]))+"\t"+SimNodeNameFormat(tree,tree.lastDaughterOfNode(nodevector[x]))+"\t"+Format(tree.getBranchLength(tree.lastDaughterOfNode(nodevector[x]), 1.0))+"\t"+tree.numberOfNodesInClade(tree.lastDaughterOfNode(nodevector[x]))+"\t"+SimNodeNameFormat(tree,tree.branchingAncestor(nodevector[x]))+"\t"+Format(tree.getBranchLength(tree.branchingAncestor(nodevector[x]), 1.0))+LF);
		}

		pw.print("SIMULATION PARAMETERS: \t"); //for loop?

		for(int x=0;x<model.num_of_traits;x++){
			if (x!=0) pw.print("\t\t\t"); 
		//	pw.print("\t" + model.getLongModelName(x) + " was used for trait: " + model.getShortModelName(x)+LF); 
		}
		pw.print("\t\t\t\tTrait 1\t\t\tTrait 2\t\t\tCorrelation"+LF);

		//#.##########E(+/-)## 
		pw.print("Initial Values:\t\t"+Format(sim[0][0].getState(tree.getRoot()))+"\t"+Format(sim[0][1].getState(tree.getRoot()))+"\t"+Format((model.getTransitionValue(0,1,toss)).getDoubleValue())+LF);
		pw.print("Selected Tip Variance:"+LF);
		if(model.getName()=="OU"){
			pw.print("OU Decay Constants:         1.0000000000E-02  1.0000000000E-02"+LF);
			pw.print("OU starting peak value:     0.0000000000E+00  0.0000000000E+00"+LF);
		}
		//model.traits.initialized;
				
		pw.print("Speed of change of peak:"+LF);
		pw.print("Variance of peak change:"+LF);
//		if(modle.
		pw.print("No bounds are used."+LF);
		pw.print(LF);
		pw.print(LF);
		pw.print("The Pseudo-random Number Generator Seed = " + model.getSeed() + "."+LF);
		pw.print("Trait values from "+num_of_runs+" simulations follow."+LF);
		pw.print("\t\tTips\t\t\t\tNodes\t\t\t\tSimulation"+LF);
		pw.print("____________________________________________________________________________________"+LF);
	
	//	for (int x=0;x<num_of_nodes;x++) nodevector[x]=x+2;
		for (int x=0;x<num_of_runs;x++){
			int z=0;
			while (z<num_of_nodes){

				if(z<num_of_nodes){
					pw.print(SimNodeNameFormat(tree, nodevector[z]));
					for (int y=0;y<2;y++){
						pw.print("\t"+Format(sim[x][y].getState(nodevector[z],0)));
					}
				}
				else pw.print("**"+"\t"+Format(0)+"\t"+Format(0));
				z++;
				if(z<num_of_nodes){
					pw.print("\t"+SimNodeNameFormat(tree, nodevector[z]));
					for (int y=0;y<2;y++){
						pw.print("\t"+Format(sim[x][y].getState(nodevector[z],0)));
					}
				}
				else pw.print("\t**"+"\t"+Format(0)+"\t"+Format(0));				
				z++;
				pw.print(" "+(x+1)+LF);
			}
	    }
	    pw.print(LF);
	    pw.close();

	}
	static public String Format (double x){
		String str;
		NumberFormat formatter = new DecimalFormat("0.0000000000E00");
		str=formatter.format(x);
		if (x>=0){
			if (x>=1 || x==0){
				return " "+str.substring(0, 13) + '+' + str.substring(13,15);
//			return str1+"+"+str2;
			}
			else { 
				return " "+str;
			}
		}
		else {
			if (x<=-1){
				return str.substring(0, 14) + '+' + str.substring(14,16);
			}
			else { 
				return str;
			}
		}
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
		//System.out.println("node:"+node+" count: "+count);
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
	static public ContinuousHistory[][] ReadSimFile(String str){
		ContinuousHistory sim[][]=null;
		return sim;
	}
}
