package mesquite.pdsim.lib;

import mesquite.cont.lib.ContinuousHistory;
import mesquite.lib.*;
import mesquite.pdsim.lib.*;

public class simResults extends Results {
	ContinuousHistory sim[][];
	public int[] nodeNumVector=null;
	public String[] nodeNameVector=null;
	String[] traitNameVector=null;
	String name = "simResults";
	int numOfRuns, numOfNodes, numOfTraits;
	public Tree tree;
	CoVarMatrixModel model;
	boolean simDone=false;
	/*public Class getClass(){
		return simResults.class;
	}*/
	public boolean isEditable(){
		return false;
	}
	public boolean isEditableDiagonal(){
		return false;
	}
	public void rowTouched(int x){	
	}
	public String getNEXUSCommand(){
		System.out.println("Tried to edit static data structure. Stop messin wit my code foo.");
		return "FIXTHIS";
	}	
	public void setCellValue(int row, int column, MesquiteNumber i, boolean notify){
		System.out.println("Tried to edit static data structure. Stop messin wit my code foo.");
	}
	public String getRowName(int row){
		return ""+(row+1);
	}
	public String getColumnName(int column){
		int trait=column/numOfNodes;
		int node=column-trait*numOfNodes;
		if(trait<numOfTraits && node<numOfNodes)
			return "("+nodeNameVector[node]+")"+traitNameVector[trait];
		else return "Matt Messed Up (TM)";
	}
	
	
	public MesquiteNumber getCellValue(int row, int column, MesquiteNumber i){
		if(simDone){
			int trait=column/numOfNodes;
			int node=column-trait*numOfNodes;
			if(row<numOfRuns && node < numOfNodes && trait < numOfTraits);
				return new MesquiteNumber (sim[row][trait].getState(nodeNumVector[node]));
		}
		else {
			MesquiteNumber x = new MesquiteNumber(-1);
			//x.setToUnassigned();
			return x;
		}
	}
	public int getNumRows(){
		if(simDone){
			return numOfRuns;
		}
		else{
			return 0;
		}
	}
	public int getNumColumns(){
		if(simDone){
			return numOfNodes*numOfTraits;
		}
		else{
			return 0;
		}
	}
	public String getRowHeader(){
		return "Run number";
	}
	public String getColumnHeader(){
		return "(Trait) Node";
	}

	public String getName(){
		return name;
	}
	public boolean getEditCancel(){
		return false;
	}
	public simResults (String name, Class dataClass, CoVarMatrixModel model, Tree tree, int numOfRuns, ContinuousHistory[][] sim) {
		super(name, dataClass);
		this.name=name;
		this.sim=sim;
		this.numOfTraits=model.num_of_traits;
		this.numOfRuns=numOfRuns;
		numOfNodes=tree.numberOfNodesInClade(tree.getRoot());
		nodeNumVector=new int[numOfNodes];
		nodeNameVector=new String[numOfNodes];
		for(int x=0;x<numOfNodes;x++) nodeNumVector[x]=0;
		getnodevector(nodeNumVector,tree,tree.getRoot(), 0);
		for(int x=0;x<numOfNodes;x++) nodeNameVector[x]=tree.getNodeLabel(nodeNumVector[x]);
		traitNameVector=new String[numOfTraits];
		for(int x=0;x<numOfTraits;x++) traitNameVector[x]=model.getTraitName(x);
		simDone=true;
		this.tree=tree;
		this.model=model;
		
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
	public String getTraitName(int x){
		if (x<numOfTraits&&x>=0){
			return traitNameVector[x];
		}
		else return "Out of Bounds:"+x;
	}
	/*
	int[] nodeNumVector=null;
	String[] nodeNameVector=null;
	String[] traitNameVector=null*/
}
