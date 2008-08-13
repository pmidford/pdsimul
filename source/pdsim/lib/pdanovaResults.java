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
	public simResults results;
	public boolean isEditable(){
		return false;
	}
	public boolean isEditableDiagonal(){
		return false;
	}
	
	public String getNEXUSCommand(){
		System.out.println("Tried to edit static data structure. Stop messin wit my code foo.");
		return "FIXTHIS";
	}	

		public pdanovaResults(String name, Class dataClass, simResults Meep){
			super(name, dataClass);

			this.results=Meep;
			this.numOfRuns=Meep.numOfRuns;
			this.numOfTaxa=results.model.getNumTaxa();
			this.numOfTraits=results.numOfTraits;
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
			nodeVector=PDAnova.getTipNodeVector(nodeVector, results.model.getTree(), results.model.getTree().getRoot(), 0);
			for(int x=0; x<numOfTaxa;x++){
				System.out.println(nodeVector[x]);
			}
			for(int x=0; x<numOfTaxa;x++){
				RowTitle[x]=Meep.tree.getNodeLabel(nodeVector[x]);
			}
			PDAnova run=new PDAnova(results.model, results.sim, results.numOfRuns, RowTitle);
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
				return "Group: "+temp+", trait:"+results.getTraitName(column-numOfTraits*temp);
			}
			else if(display==1){
				temp=column/(numOfTraits);
				return "Group: "+temp+", trait:"+results.getTraitName(column-numOfTraits*temp);
			}
			else if(display==2){
				temp=column/(numOfTraits);
				return "Group: "+temp+", trait:"+results.getTraitName(column-numOfTraits*temp);
			}
			else if(display==3){
				temp=column/(numOfTraits*2);
				if (MesquiteInteger.isDivisibleBy(column,2))
					return "Min of Group: "+temp+", trait:"+results.getTraitName((int)(column/2)-numOfTraits*temp*2);
				else return  "Max of Group: "+temp+", trait:"+results.getTraitName((int)(column/2)-numOfTraits*temp*2);
					
			}
			else if(display==4){
				int localGroup,trait2,trait1;
				localGroup=column/(numOfTraits*numOfTraits);
				trait2=(column-(localGroup*numOfTraits*numOfTraits))/numOfTraits;
				trait1=column-(localGroup*numOfTraits*numOfTraits)-trait2*numOfTraits;
				return "Group: "+localGroup+", trait:"+results.getTraitName(trait1)+"to"+results.getTraitName(trait2);
			}
			else if(display==5){
				//column/numOfTraits;
				return ":"+results.getTraitName(column);
			}
			return "Attempting to display invalid statistic";
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
				if(display==0||display==1||display==2)
					return numOfGroups*numOfTraits;
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
