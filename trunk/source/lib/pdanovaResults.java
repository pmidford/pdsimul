package mesquite.pdsim.lib;

import java.awt.Checkbox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

import mesquite.cont.lib.ContinuousHistory;
import mesquite.lib.CommandChecker;
import mesquite.lib.ExtensibleDialog;
import mesquite.lib.MesquiteDouble;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteListener;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteTrunk;
import mesquite.lib.Notification;
import mesquite.lib.ParseUtil;
import mesquite.lib.Tree;
import mesquite.lib.StringLister;
import mesquite.pdsim.PDAnova.PDAnova;
import java.util.*;
//import 

public class pdanovaResults extends Results implements StringLister {
	//Checkbox mx[][];
	int numOfTaxa=3;
	public int numOfTraits=2;
	public int numOfRuns;
	Vector[] group;
	public int numOfGroups=0;
	int display=0;
	String[] modes;
	public int[] nodeVector;
	double AVE[][]; 
	double MED[][]; 
	double VAR[][]; 
	double MIM[][]; 
	//TODO: COR is too large, but I don't think I can specify something smaller.
	double COR[][][]; 
	double F[][];
	
	boolean initialized=false;
	//public simResults results;
	public boolean isEditable(){
		return false;
	}
	public boolean isEditableDiagonal(){
		return false;
	}
	
	public String toString () {
		System.out.println("gettingNesusSpecification");
		String str="";		
		str+="'"+getName()+"'"+" ("+"StatisticResult"+")\n";
		//str+="\tmodelName "+"'"+model.getName()+"'"+" treeName "+"'"+tree.getName()+"'\n";
		//str+="\tmodelNum 0 "+" treeNum 0\n";//CharModelCurator.getModelNumber(model);
		str+="\t"+"numOfRuns "+numOfRuns+"\n";
		str+="\t"+"numOfGroups "+numOfGroups+"\n";
		str+="\t"+"groupVectorSize "+group.length+"\n";
		for(int x=0;x<group.length;x++){
			str+="\t"+"group_"+x+" ";
			for (int y=0;y<group[x].size();y++)
				str+=group[x].get(y);
		}
		str+="\t"+"nodeVectorSize "+nodeVector.length+"\n\t";
		for(int x=0;x<nodeVector.length;x++)
			str+=nodeVector[x]+" ";

		str+="\tBEGINDATABLOCK\n";
		str+="\n\tAVE "+AVE.length+" "+AVE[0].length+"\n";
		for(int x=0;x<AVE.length;x++){
			for(int y=0;y<AVE[x].length;y++){
				str+="'"+AVE[x][y]+"' ";
			}
		}str+="\n\tMED "+MED.length+" "+MED[0].length+"\n";
		for(int x=0;x<MED.length;x++){
			for(int y=0;y<MED[x].length;y++){
				str+="'"+MED[x][y]+"' ";
			}
		}str+="\n\tVAR "+VAR.length+" "+VAR[0].length+"\n";
		for(int x=0;x<VAR.length;x++){
			for(int y=0;y<VAR[x].length;y++){
				str+="'"+VAR[x][y]+"' ";
			}
		}str+="\n\tMIM "+MIM.length+" "+MIM[0].length+"\n";
		for(int x=0;x<MIM.length;x++){
			for(int y=0;y<MIM[x].length;y++){
				str+="'"+MIM[x][y]+"' ";
			}
		}str+="\n\tF "+F.length+" "+F[0].length+"\n";
		for(int x=0;x<F.length;x++){
			for(int y=0;y<F[x].length;y++){
				str+="'"+F[x][y]+"' ";
			}
		}str+="\n\tCOR "+COR.length+" "+COR[0].length+" "+COR[0][0].length+"\n";
		for(int x=0;x<COR.length;x++){
			for(int y=0;y<COR[x].length;y++){
				for(int z=0;z<COR[x][y].length;z++){
					str+="'"+COR[x][y][z]+"' ";
				}
			}
		}
		str+="\tENDDATABLOCK\n";		
		return str;
	}
	public String getNEXUSCommand(){
		System.out.println("Tried to edit static data structure. Stop messin wit my code foo.");
		return "FIXTHIS";
	}	
	public pdanovaResults(String name, Class dataClass, CoVarMatrixModel model){
		super(name, dataClass);
		this.numOfTaxa=model.getTree().getTaxa().getNumTaxa();
		this.numOfTraits=model.num_of_traits;
		this.model=model;
		modes=new String[6];
		modes[0]="Mean";
		modes[1]="Median";
		modes[2]="Variance";
		modes[3]="Min/Max";
		modes[4]="Corelation";
		modes[5]="FStatistics";

		System.out.println("MEEP!");
		String[] RowTitle=new String[numOfTaxa];
		int[] nodeVector=new int[numOfTaxa];
		nodeVector=PDAnova.getTipNodeVector(nodeVector, model.getTree(), model.getTree().getRoot(), 0);
		for(int x=0; x<numOfTaxa;x++){
			System.out.println(nodeVector[x]);
		}
		for(int x=0; x<numOfTaxa;x++){
			RowTitle[x]=model.tree.getNodeLabel(nodeVector[x]);
		}
	}

	public pdanovaResults(String name, Class dataClass, simResults Meep){
		super(name, dataClass);

		//this.results=Meep;
		this.numOfRuns=Meep.numOfRuns;
		this.numOfTaxa=Meep.numOfTaxa;
		this.numOfTraits=Meep.numOfTraits;
		this.model=Meep.model;
		modes=new String[6];
		modes[0]="Mean";
		modes[1]="Median";
		modes[2]="Variance";
		modes[3]="Min/Max";
		modes[4]="Corelation";
		modes[5]="FStatistics";

		//int numOfRuns=results.numOfRuns;
		System.out.println("MEEP!");
		String[] RowTitle=new String[numOfTaxa];
		int[] nodeVector=new int[numOfTaxa];
		nodeVector=PDAnova.getTipNodeVector(nodeVector, Meep.model.getTree(), Meep.model.getTree().getRoot(), 0);
		for(int x=0; x<numOfTaxa;x++){
			System.out.println(nodeVector[x]);
		}
		for(int x=0; x<numOfTaxa;x++){
			RowTitle[x]=Meep.tree.getNodeLabel(nodeVector[x]);
		}
		PDAnova run=new PDAnova(Meep.model, Meep.sim, Meep.numOfRuns, RowTitle);
		run.runAnova();	

		this.AVE=run.getAVE();
		this.MED=run.getMED();
		this.VAR=run.getVAR();
		this.MIM=run.getMIM();
		this.F=run.getF();	//TODO: COR is too large, but I don't think I can specify something smaller.
		this.COR=run.getCOR();
		this.numOfGroups=run.getNumOfGroups();
		this.group=run.getGroups();
		this.nodeVector=run.nodeVector;

		run.dispose();
		initialized=true;
	}
		public void rowTouched(int x){	
		}
		public void setCellValue(int row, int column, MesquiteNumber i, boolean notify){
			System.out.println("Tried to edit static data structure. Stop messin wit my code foo.");
		}
		public String getRowName(int row){
			return ""+(row+1);
		}
		public String getColumnName(int column){
			int temp;
			if(display==0){
				temp=column/(numOfTraits);
				return "Group: "+temp+", trait:"+model.getTraitName(column-numOfTraits*temp); //results.getTraitName(column-numOfTraits*temp);
			}
			else if(display==1){
				temp=column/(numOfTraits);
				return "Group: "+temp+", trait:"+model.getTraitName(column-numOfTraits*temp); //results.getTraitName(column-numOfTraits*temp);
			}
			else if(display==2){
				temp=column/(numOfTraits);
				return "Group: "+temp+", trait:"+model.getTraitName(column-numOfTraits*temp);
			}
			else if(display==3){
				temp=column/(numOfTraits*2);
				if (MesquiteInteger.isDivisibleBy(column,2))
					return "Min of Group: "+temp+", trait:"+model.getTraitName((int)(column/2)-numOfTraits*temp*2);
				else return  "Max of Group: "+temp+", trait:"+model.getTraitName((int)(column/2)-numOfTraits*temp*2);
					
			}
			else if(display==4){
				int localGroup,trait2,trait1;
				localGroup=column/(numOfTraits*numOfTraits);
				trait2=(column-(localGroup*numOfTraits*numOfTraits))/numOfTraits;
				trait1=column-(localGroup*numOfTraits*numOfTraits)-trait2*numOfTraits;
				return "Group: "+localGroup+", trait:"+model.getTraitName(trait1)+"to"+model.getTraitName(trait2);
			}
			else if(display==5){
				//column/numOfTraits;
				return ":"+model.getTraitName(column);
			}
			return "Attempting to display invalid statistic";
		}		
		public void fromString(String description, MesquiteInteger stringPos,
				int format) {
			System.out.println("DE DE gettingNesusSpecification");
		
			String var1, val1, val2, val3;		

			var1 = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(var1);	
			var1 = ParseUtil.getToken(description, stringPos); // eating token
			val1 = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(val1);	
			System.out.println("numOfRuns="+var1);	
			numOfRuns=MesquiteInteger.fromString(var1);
			var1 = ParseUtil.getToken(description, stringPos); // eating token
			val1 = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(var1);	
			System.out.println("numOfGroups"+var1);	
			numOfGroups=MesquiteInteger.fromString(var1);
			var1 = ParseUtil.getToken(description, stringPos); // eating token
			val1 = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(var1);	
			System.out.println("Vector["+var1+"]");	
			
			group = new Vector[MesquiteInteger.fromString(var1)];
			for(int x=0;x<group.length;x++){
				System.out.println("allocating vector");
		 		group[x]=new Vector();
			}
			val1 = ParseUtil.getToken(description, stringPos); // eating token
			for(int x=0;x<group.length;){
				val1 = ParseUtil.getToken(description, stringPos); // eating token
				if(val1.equalsIgnoreCase("group "+(x+1))||val1.equalsIgnoreCase("nodeVectorSize"))
					x++;
				else{
					System.out.println("pusshing "+val1);
					group[x].add(new MesquiteInteger(MesquiteInteger.fromString(val1)));
		 		}
				
			}
 			val1 = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(var1);	
			System.out.println("new node vector "+val1);	
			nodeVector=new int[MesquiteInteger.fromString(val1)];
			for(int x=0;x<nodeVector.length;x++){
				val1=ParseUtil.getToken(description, stringPos);
				System.out.println("setting "+val1);	
				nodeVector[x]=MesquiteInteger.fromString(val1);
			}
			initialized=true;
			
			var1 = ParseUtil.getToken(description, stringPos); // eating token
			var1 = ParseUtil.getToken(description, stringPos); // eating token
			val1 = ParseUtil.getToken(description, stringPos); // eating token
			val2 = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(var1);	
			System.out.println(val1);	
			System.out.println(val2);	

			AVE=new double[MesquiteInteger.fromString(val1)][MesquiteInteger.fromString(val2)];
			for(int x=0;x<AVE.length;x++){
				for(int y=0;y<AVE[x].length;y++){
					AVE[x][y]=MesquiteDouble.fromString(ParseUtil.getToken(description, stringPos));
				}
			}
			var1 = ParseUtil.getToken(description, stringPos); // eating token
			val1 = ParseUtil.getToken(description, stringPos); // eating token
			val2 = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(var1);	
			System.out.println(val1);	
			System.out.println(val2);	

			MED=new double[MesquiteInteger.fromString(val1)][MesquiteInteger.fromString(val2)];
			for(int x=0;x<MED.length;x++){
				for(int y=0;y<MED[x].length;y++){
					MED[x][y]=MesquiteDouble.fromString(ParseUtil.getToken(description, stringPos));
				}
			}
			var1 = ParseUtil.getToken(description, stringPos); // eating token
			val1 = ParseUtil.getToken(description, stringPos); // eating token
			val2 = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(var1);	
			System.out.println(val1);	
			System.out.println(val2);	

			VAR=new double[MesquiteInteger.fromString(val1)][MesquiteInteger.fromString(val2)];
			for(int x=0;x<VAR.length;x++){
				for(int y=0;y<VAR[x].length;y++){					
					var1 = ParseUtil.getToken(description, stringPos); // eating token
					System.out.println("x,y:val"+x+","+y+","+var1);
					if(var1.equalsIgnoreCase("-")){
						
					}
					VAR[x][y]=MesquiteDouble.fromString(var1);
				}
			}
			var1 = ParseUtil.getToken(description, stringPos); // eating token
			val1 = ParseUtil.getToken(description, stringPos); // eating token
			val2 = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(var1);	
			System.out.println(val1);	
			System.out.println(val2);	

			MIM=new double[MesquiteInteger.fromString(val1)][MesquiteInteger.fromString(val2)];
			for(int x=0;x<MIM.length;x++){
				for(int y=0;y<MIM[x].length;y++){
					MIM[x][y]=MesquiteDouble.fromString(ParseUtil.getToken(description, stringPos));
				}
			}
			var1 = ParseUtil.getToken(description, stringPos); // eating token
			val1 = ParseUtil.getToken(description, stringPos); // eating token
			val2 = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(var1);	
			System.out.println(val1);	
			System.out.println(val2);	

			F=new double[MesquiteInteger.fromString(val1)][MesquiteInteger.fromString(val2)];
			for(int x=0;x<F.length;x++){
				for(int y=0;y<F[x].length;y++){
					F[x][y]=MesquiteDouble.fromString(ParseUtil.getToken(description, stringPos));
				}
			}
			var1 = ParseUtil.getToken(description, stringPos); // eating token
			val1 = ParseUtil.getToken(description, stringPos); // eating token
			val2 = ParseUtil.getToken(description, stringPos); // eating token
			val3 = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(var1);	
			System.out.println(val1);	
			System.out.println(val2);	
			System.out.println(val3);	

			COR=new double[MesquiteInteger.fromString(val1)][MesquiteInteger.fromString(val2)][MesquiteInteger.fromString(val3)];
			for(int x=0;x<COR.length;x++){
				for(int y=0;y<COR[x].length;y++){
					for(int z=0;z<COR[x][y].length;z++){
						COR[x][y][z]=MesquiteDouble.fromString(ParseUtil.getToken(description, stringPos));
					}
				}
			}
		}		
		public String getNexusSpecification(){
			return toString();
		}
		public MesquiteNumber getCellValue(int row, int column, MesquiteNumber i){

			if(initialized){
				if(display==0)
					return new MesquiteNumber(AVE[column][row]);
				else if(display==1)
					return new MesquiteNumber(MED[column][row]);
				else if(display==2)
					return new MesquiteNumber(VAR[column][row]);
				else if(display==3)
					return new MesquiteNumber(MIM[column][row]);
				else if(display==5)
					return new MesquiteNumber (F[column][row]);
				else if(display==4){
					int groupy,trait2,trait1;
					groupy=column/(numOfTraits*numOfTraits);
					trait2=(column-(groupy*numOfTraits*numOfTraits))/numOfTraits;
					trait1=column-(groupy*numOfTraits*numOfTraits)-trait2*numOfTraits;
					return new MesquiteNumber(PDAnova.getCor(COR,numOfTraits,trait1,trait2,groupy,row));
				}
				else return new MesquiteNumber(-1);
			}
			else {
				MesquiteNumber x = new MesquiteNumber(-1);
				//x.setToUnassigned();
				return x;
			}
		}
		public int getNumRows(){
			if(initialized){
				return numOfRuns;
			}
			else{
				return 0;
			}
		}
		public int getNumColumns(){

			if(initialized){
				if(display==0||display==1||display==2){
					System.out.println("HERE!"+numOfGroups+","+numOfTraits);
					return numOfGroups*numOfTraits;
				}
				else if(display==3)
					return numOfGroups*numOfTraits*2;
				else if(display==4)
					return numOfGroups*numOfTraits*numOfTraits;
				else if(display==5)
					return numOfTraits;
				else return 0;
			}
			return 0;
		}
		public String getRowHeader(){
			return "Run number";
		}
		public String getColumnHeader(){
			if(display==0)
				return "Mean";
			else if(display==1)
				return "Median";
			else if(display==2)
				return "Variance";
			else if(display==3)
				return "Min/Max";
			else if (display==4)
				return "Correlation";
			else if (display==5)
				return "F-Statistic";
			else return "WTF";
		}

		public String getName(){
			return name;
		}
		public boolean getEditCancel(){
			return false;
		}
	   	public Object doCommand(String commandName, String arguments, CommandChecker checker){
	   		MesquiteInteger pos=new MesquiteInteger(0);
		 	if (checker.compare(this.getClass(), "Sets statistic to be displayed", "[trait_var of change; must be > 0]", commandName, "setStat")) {
		 		pos.setValue(0);
		 		String str;
		 		//eat tokken;
		 		System.out.println(arguments);
		 		str=ParseUtil.getToken(arguments, pos); //eating token
		 		str=ParseUtil.getToken(arguments, pos);
		 		arguments=str;
		 		System.out.println(arguments);
		 		//display
		 		pos.setValue(0);
		 		int var = MesquiteInteger.fromString(arguments, pos);
		 		int a = display;
		 		if (!MesquiteInteger.isCombinable(var)) {
		 			var= MesquiteInteger.queryInteger(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Set statistic to be displayed", "display:", a);
		 		}
		 		if ( var!=a && MesquiteInteger.isCombinable(var)) {
		 			display=var;
		 			System.out.println("changed display");
		 			notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
		 		}
		 	}
	    	else
	    		return  super.doCommand(commandName, arguments, checker);
			return null;
	   	}
		public String[] getStrings(){
			return modes;
		}
		public String getStatName (int x){
			return modes[x];
		}
		public int getDisplay(){
			return display;
		}
		public int getNumTaxa(){
			return numOfTaxa;
		}
		public int getNumGroups(){
			return numOfGroups;
		}
		public Vector[] getGroupVector(){
			return group;
		}
		public double[][] getFStat(){
			return F;
		}
}
