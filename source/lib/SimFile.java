package mesquite.pdsim.lib;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import mesquite.cont.lib.ContinuousHistory;
import mesquite.lib.*;

public class SimFile {
	public static void MakeSimFile(CoVarMatrixModel model, MesquiteModule module, Tree tree, ContinuousHistory sim[][], int num_of_runs) throws Exception {
		System.out.println("Saving results to external file");
		String dir;
		
		dir=MesquiteFile.saveFileAsDialog("please enter a file name"); //chooseDirectory("please chose a directory to save you file", module.getPath());
		
		System.out.println(dir);
		
		ExtensibleDialog fileDialog=new ExtensibleDialog (module, "Save File:");
		//SingleLineTextField nameField;
		//nameField=fileDialog.addTextField(defaultName);
		RadioButtons rb = fileDialog.addRadioButtons(new String[]{"use Windows/Dos LF? (\\r\\n)","use *n?x LF? (\\n)"},0);

		//SingleLineTextField FieldDel;
		//FieldDel=fileDialog.addTextField("Field Delineator",",", 1);
			
		fileDialog.completeAndShowDialog();
		String name; 
		//String FieldDelineator;
		String LF;
		if (rb.getValue()==0) LF="\r\n";
		else LF="\n";
		
		//FieldDelineator=FieldDel.getText();
		name=dir+".csv";
		
        File file = new File(dir);
		PrintWriter pw = new PrintWriter(new FileWriter(file));

	    MesquiteNumber toss=null;
		pw.print("Output from Mesquite.pdsim: continuous characters simulated on a phylogenetic tree."+LF);
//		plnw.println();
		pw.print("Data from ");
		//pw.print(str);
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
		int num_of_tips=tree.getNumTaxa();
		int num_int_nodes;
		num_int_nodes=num_of_nodes-num_of_tips;
		int nodevector[]=new int[num_of_nodes];
		int tipvector[]=new int[num_of_tips];
		int intnodevector[]=new int[num_int_nodes];
		
		//String nameVector[]=new String[num_of_nodes];
		for(int x=0;x<num_of_nodes;x++) nodevector[x]=0;
		for(int x=0;x<num_of_tips;x++) tipvector[x]=0;
		for(int x=0;x<num_int_nodes;x++) intnodevector[x]=0;
		getnodevector(nodevector,tree,tree.getRoot(), 0);
		gettipvector(tipvector,tree,tree.getRoot(), 0);
		getintnodevector(intnodevector,tree,tree.getRoot(), 0);
		
		//for(int x=0;x<num_of_nodes;x++) nameVector[x]=tree.getNodeLabel(nodevector[x]);
	
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
		pw.print("Initial Values:\t\t"+Format(sim[0][0].getState(tree.getRoot()))+"\t"+Format(sim[0][1].getState(tree.getRoot()))+"\t"+Format((model.getCellValue(0,1,toss)).getDoubleValue())+LF);
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
			while (z<num_of_tips||z<num_int_nodes){

				if(z<num_of_tips){
					pw.print(SimNodeNameFormat(tree, tipvector[z]));
					for (int y=0;y<2;y++){
						pw.print("\t"+Format(sim[x][y].getState(tipvector[z],0)));
					}
				}
				else pw.print("**"+"\t"+Format(0)+"\t"+Format(0));
				if(z<num_int_nodes){
					pw.print("\t"+SimNodeNameFormat(tree, intnodevector[z]));
					for (int y=0;y<2;y++){
						pw.print("\t"+Format(sim[x][y].getState(intnodevector[z],0)));
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
		String str;
		str=tree.getNodeLabel(node);
		//if (str==null){
		if(str.length()!=2){
			return Format2(node);
		}
		return str;//Format2(node);
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
		System.out.println("node: "+tree.getNodeLabel(node));
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
	static public int[] gettipvector(int nodevector[],Tree tree,int node, int count){
		System.out.println("tip node: "+tree.getNodeLabel(node));
		if(tree.numberOfDaughtersOfNode(node)==0){
			while (nodevector[count]!=0) count++;
			nodevector[count]=node;
		}
		int daughters[], num_of_daughters;
		daughters=tree.daughtersOfNode(node);
		num_of_daughters=tree.numberOfDaughtersOfNode(node);
		for (int x=0;x<num_of_daughters;x++){
			gettipvector(nodevector, tree, daughters[x], count);
		}	
		//nodevector[count]=node;
		//tree.
		//nodevector=getnodevector(nodevector, tree, node, count);
		return nodevector;
	}
	static public int[] getintnodevector(int nodevector[],Tree tree,int node, int count){
		System.out.println("int node: "+tree.getNodeLabel(node));
		if(tree.numberOfDaughtersOfNode(node)!=0){
			
			while (nodevector[count]!=0) count++;
			nodevector[count]=node;
		}
		int daughters[], num_of_daughters;
		daughters=tree.daughtersOfNode(node);
		num_of_daughters=tree.numberOfDaughtersOfNode(node);
		for (int x=0;x<num_of_daughters;x++){
			getintnodevector(nodevector, tree, daughters[x], count);
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

    String[] nameArray = null;
    HashSet nameUsed = new HashSet();

    /*..............................................................*/
    /**
     * This protects the two character names of non-conflicting and first found conficting
     * tips.
     */
    /*
    private void namePass1(Tree tree, int node){
        if (tree.nodeIsInternal(node)){
            for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter))
                namePass1(tree,daughter);  
        }
        else {
            String rawName = tree.getNodeLabel(node);
            if (rawName != null){
                String shortName = unProcessName(rawName);
                if (!nameUsed.contains(shortName)){
                    nameUsed.add(shortName);
                    nameArray[node] = shortName;
                }
            }
        }
    }

    
    /**
     * This fills in names for conflicting tips and internal nodes.
     * @param tree the Tree to save
     * @param node int specifying the node in the tree that needs a name
     */
    /*
    private void namePass2(Tree tree, int node){
        if (tree.nodeIsInternal(node)){
            for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter))
                namePass2(tree,daughter);  
        }
        if (nameArray[node] == null){ // name not already assigned
            int idx = node;
            String newName = genName(idx);
            while(nameUsed.contains(newName))
                newName = genName(idx++);
            nameUsed.add(newName);
            nameArray[node]=newName;
        }   
    }
    /**
     * This walks through the tree and assigns each node, internal and external
     * a two-character identifier for the duration of this file.  Doing this once
     * avoids collisions
     */
    /*
    private void setNames(Tree tree, int root){
        if (nameUsed == null)
            nameUsed = new HashSet();
        else
            nameUsed.clear();
        if (nameArray == null || nameArray.length != tree.getNumNodeSpaces())
            nameArray = new String[tree.getNumNodeSpaces()];
        for (int i= 0;i<nameArray.length;i++)
            nameArray[i]= null;
        namePass1(tree,root);
        namePass2(tree,root);            
    }

/*...............................................................*/
// This writes the node, its ancestor and the length of the branch.
    private void exportNode(Tree tree, StringBuffer outputBuffer, int node, String LN) {
    	String myName = nameArray[node];
    	if (node != tree.getRoot()) {
            String mothersName = nameArray[tree.motherOfNode(node)];
            outputBuffer.append(myName + " " + mothersName + "  ");
            outputBuffer.append(MesquiteDouble.toFixedWidthString(tree.getBranchLength(node,1.0),15));
            outputBuffer.append(LN);
    	}
    	else {
			outputBuffer.append(myName + " $$  " + MesquiteDouble.toFixedWidthString(0.0,15)); // PDI root BL's always 0
			outputBuffer.append(LN);
    	}
    	for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter))
    		exportNode(tree, outputBuffer, daughter, LN);
	}
}
