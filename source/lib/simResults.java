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
	int numOfRuns, numOfNodes, numOfTraits, numOfTaxa;
	public Tree tree;
	CoVarMatrixModel model;
	boolean simDone=false;
	/*public Class getClass(){
		return simResults.class;
	}*/
	/*public Results finish(String description, MesquiteInteger stringPos, int format) {
		
		sim=new ContinuousHistory[numOfRuns][numOfTraits];
		String name, value;
		value = ParseUtil.getToken(description, stringPos); // eating token
		System.out.println(value);
		int x=0, y=0, z=0;
		while (value != null&& x<numOfRuns && y<numOfTraits && z < numOfNodes) {
			value = ParseUtil.getToken(description, stringPos); // eating token
			if(!value.equalsIgnoreCase("ENDDATABLOCK"))
					if (MesquiteDouble.isCombinable(MesquiteDouble.fromString(value))) 
						sim[x][y].setState(z, 0, MesquiteDouble.fromString(value));
			z++;
			if(z==numOfNodes){
				z=0;
				y++;
			}
			if(y==numOfTraits){
				y=0;
				x++;
			}
		}
		return this;
	}*/

	public void fromString(String description, MesquiteInteger stringPos, int format) {
		
		sim=new ContinuousHistory[numOfRuns][numOfTraits];
		for(int x=0;x<numOfRuns;x++){
			for (int y=0;y<model.num_of_traits;y++){
				sim[x][y] =  new ContinuousHistory(tree.getTaxa(), tree.getNumNodeSpaces(), null);
			}
		}
		System.out.println("#nodes "+numOfNodes +" #taxa "+numOfTaxa + " #runs "+numOfRuns+" #numOfTraits");

		String value;
		value = ParseUtil.getToken(description, stringPos); // eating token
		System.out.println("yah!"+value);
		int x=0, y=0, z=0;
		while (value != null&& x<numOfRuns && y<numOfTraits && z < numOfNodes) {
			value = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println("yah!"+value);
			if(!value.equalsIgnoreCase("ENDDATABLOCK"))
					if (MesquiteDouble.isCombinable(MesquiteDouble.fromString(value))){ 
						System.out.println("x,y,z"+x+" "+y+" "+z);
						sim[x][y].setState(nodeNumVector[z], 0, MesquiteDouble.fromString(value));
					}
			z++;
			if(z==numOfNodes){
				z=0;
				y++;
			}
			if(y==numOfTraits){
				y=0;
				x++;
			}
		}
	}
	public boolean isEditable(){
		return false;
	}
	public boolean isEditableDiagonal(){
		return false;
	}
	public void rowTouched(int x){	
	}
	public String toString () {
			System.out.println("gettingNesusSpecification");
			String str="";		
			str+="'"+getName()+"'"+" ("+"SimulationResult"+")\n";
			//str+="\tmodelName "+"'"+model.getName()+"'"+" treeName "+"'"+tree.getName()+"'\n";
			//str+="\tmodelNum 0 "+" treeNum 0\n";//CharModelCurator.getModelNumber(model);
			str+="\t"+"numOfRuns "+numOfRuns+"\n";
			str+="\tBEGINDATABLOCK\n";
		for(int x=0;x<getNumRows();x++){
			for(int y=0;y<getNumColumns();y++){
				str+=getCellValue(x,y,null)+" ";
			}
		}
		str+="\tENDDATABLOCK\n";		
		return str;
	}
	public String getNexusSpecification() {
		return toString();
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
	public simResults (String name, Class dataClass) {
		super(name,dataClass);
		this.name=name;
	}
	
	public simResults (String name, Class dataClass, CoVarMatrixModel model, Tree tree, int numOfRuns) {
		super(name, dataClass);
		this.name=name;
		this.numOfTraits=model.num_of_traits;
		this.numOfRuns=numOfRuns;
		numOfNodes=tree.numberOfNodesInClade(tree.getRoot());
		nodeNumVector=new int[numOfNodes];
		nodeNameVector=new String[numOfNodes];
		numOfTaxa=tree.getNumTaxa();
		for(int x=0;x<numOfNodes;x++) nodeNumVector[x]=0;
		getnodevector(nodeNumVector,tree,tree.getRoot(), 0);
		for(int x=0;x<numOfNodes;x++) nodeNameVector[x]=tree.getNodeLabel(nodeNumVector[x]);
		traitNameVector=new String[numOfTraits];
		for(int x=0;x<numOfTraits;x++) traitNameVector[x]=model.getTraitName(x);
		simDone=true;
		this.tree=tree;
		this.model=model;
		System.out.println("#nodes "+numOfNodes +" #taxa "+numOfTaxa + " #runs "+numOfRuns);
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
		numOfTaxa=tree.getNumTaxa();
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
