package mesquite.pdsim.PDAnovaCurator;

import mesquite.cont.lib.ContinuousHistory;
import mesquite.cont.lib.MContinuousDistribution;
import mesquite.lib.CommandChecker;
import mesquite.lib.ExtensibleDialog;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteTrunk;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.MesquiteMenuSpec;
import mesquite.lib.RadioButtons;
import mesquite.lib.SingleLineTextField;

import mesquite.lib.ObjectContainer;
import mesquite.lib.duties.CharMatrixSource;
import mesquite.lib.duties.WindowHolder;
import mesquite.lib.StringLister;
import mesquite.pdsim.lib.*;
import mesquite.pdsim.PDAnova.*;
import java.util.*;

public class PDAnovaCurator extends ResultsCurator{
	String name;
	pdanovaResults editing;
//	String[] groups; 
//	int numOfGroups;
	simResults simresults;
	boolean initialized=false;

	public MesquiteModule getModule(){
		return this;
	}
	public Class getResultClass(){
		return pdanovaResults.class;
	}
	/*.................................................................................................................*/
	public String getNameOfResultsClass() {
		return "Simulation_Results";
	}
	/*.....................getModelName............................................................................................*/
	public String getNEXUSNameOfResultsClass() {
		return "Simulation_Results";
	}
	
	public void initialize (simResults simresults){
		this.simresults=simresults;
		if (this.simresults!=null){
			//System.out.println(simresults.getColumnName(1));
			initialized=true;
		}
	}
	public String getName(){
		return "Statistical Analysis";
	}
 	public Results readResults(String name, MesquiteInteger stringPos, String description, int format) {
 		pdanovaResults results=null;
 		if (initialized)
 			results = new pdanovaResults("simResults", Results.class, simresults);
   		//System.out.println("help!");
   		//sim.fromString(description, stringPos, format);
   		return results;
 	}
 	public MesquiteModule editResultNonModal(Results result, ObjectContainer w){
		System.out.println("pdanovaResults editResultNonModal called.");
		if (result!=null && result instanceof pdanovaResults) {
			pdanovaResults resultsToEdit =  ((pdanovaResults)result);
			this.editing=resultsToEdit;
						
			MesquiteModule windowServer = hireNamedEmployee(WindowHolder.class, "#WindowBabysitter");
				if (windowServer == null)
			return null;
				PDSIMDataWindow cw=new PDSIMDataWindow(this, windowServer);
				cw.setDataSource(resultsToEdit);
				//StringLister h=new StringLister();
				//h.addElement(obj, false);
				//cw.ch
				//resultsToEdit
				//MesquiteMenuSpec menu;

				windowServer.makeMenu("Statistics");

				AnalysisNamesLister names = new AnalysisNamesLister(resultsToEdit);
				windowServer.addSubmenu(null, "View Summary Statistic", makeCommand("setStat", resultsToEdit), names);
				//.addMenuItem(this.getContainingMenuSpec(), "test Menu", makeCommand("saveStat", this));//addSubmenu(menu, "View Statistic...", makeCommand("showStat", this), names);
			
				//menu=windowServer.get

				windowServer.addMenuItem("Save Results to file ...",makeCommand("saveStat", this));
				windowServer.addMenuItem("Show Groups ...",makeCommand("showGroups", this));
				windowServer.addMenuItem("Get F and Value of Character Distribution ...",makeCommand("getP", this));	

				MesquiteWindow.centerWindow(cw);
				//cw.show();
				//cw.

			if (w!=null)
				w.setObject(cw);
			return windowServer;
		}
		return null;
   	}

 	public String getNameOfResultClass(){
 		return "boo";
 	}
   	public Results makeNewResults(String name) {
   		this.name=name;
		System.out.println("here we are");
		   		pdanovaResults results=null;
 		if (initialized){
 			System.out.println("Meh?");
 			results = new pdanovaResults(name, Results.class, simresults);
 		}
 		return results;
   	}
   	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
   		System.out.println("startingJob");
 		return true;
	}
public Object doCommand(String commandName, String arguments, CommandChecker checker) {
	if (checker.compare(this.getClass(), "Save Displayed Statistic", "[number of traits]", commandName, "saveStat")) {
//		getOwnerModule().getFileCoordinator();
		//this.getFileCoordinator().getProject().getHomeFile();
		//this.
		
		System.out.println("Saving results to external file");
		ExtensibleDialog fileDialog=new ExtensibleDialog (this, "Save File:");
		SingleLineTextField nameField;
		
		nameField=fileDialog.addTextField("File Name",""+this.getName()+"-"+editing.getStatName(editing.getDisplay()), 10);
		RadioButtons rb = fileDialog.addRadioButtons(new String[]{"use Windows/Dos LF? (\r\n)","use *n?x LF? (\n)"},0);

		SingleLineTextField FieldDel;
		FieldDel=fileDialog.addTextField("Field Delineator",",", 1);
			
		fileDialog.completeAndShowDialog();

		String str=nameField.getText();
		try {
			// TODO Auto-generated catch block
			if(rb.getValue()==0)
				DataFile.MakeDataFile(editing, str,FieldDel.getText(),"\r\n");
			else
				DataFile.MakeDataFile(editing, str,FieldDel.getText(),"\n");
				}
		catch (Exception em) {
			em.printStackTrace();
		}
	}
	else if (checker.compare(this.getClass(), "Run a statistical analysis of these results", "[number of traits]", commandName, "getP")) {
		String[] str=new String[editing.numOfTraits];
		for(int x=0;x<editing.numOfTraits;x++){
			str[x]=editing.results.getTraitName(x); //(x);
		}

		ExtensibleDialog first=new ExtensibleDialog(this,"Choose a character");
		RadioButtons rb;
		rb=first.addRadioButtons(str, 0); //addLargeOrSmallTextLabel("The P value is"+(1.0-(double)(a)/(double)(editing.numOfRuns)));
		first.completeAndShowDialog();

		MContinuousDistribution testChar;
		Vector[] testGroup;
		double testStat;
		double[][] TotalFstat;
		double[] groupFstat;
		CharMatrixSource charTask = (CharMatrixSource)this.hireEmployee(CharMatrixSource.class, "Matrix to test");
		testChar=(MContinuousDistribution)charTask.getMatrix(getProject().getTaxa(0), charTask.queryUserChoose(getProject().getTaxa(0), "Please chose matrix to test")); //getCcharTask.queryUserChoose(taxa, "Please chose a character Data");
		int ic=rb.getValue();
		//int ig=0;
		//System.out.println("156");			
			if(testChar.getNumTaxa()==editing.getNumTaxa()){
		//	System.out.println("157");							
			testGroup=PDAnova.charDatatoGroupData(testChar,editing.getGroupVector(),editing.getNumGroups(),ic);
		//	System.out.println("158");										
			testStat=PDAnova.getFStat(testGroup, editing.getNumGroups());
		//	System.out.println("160");			
			TotalFstat=editing.getFStat();
			groupFstat=new double[editing.numOfRuns];
		//	System.out.println("163");			
			for(int x=0;x<editing.numOfRuns;x++){
				groupFstat[x]=TotalFstat[ic][x];
			}
			Arrays.sort(groupFstat);
		//	System.out.println("168");			
			int a=-1;
		//	System.out.println("170:Start");			
			for(int x=0;x<editing.numOfRuns;x++){
		//		System.out.println(groupFstat[x]);
				if(x==0){
					if(groupFstat[x]>=testStat){
						a=x;
						x=editing.numOfRuns;
					}
				}
				else if(groupFstat[x]>=testStat&&groupFstat[x-1]<=testStat){
					a=x;
					x=editing.numOfRuns;
				}
			}
			//System.out.println("178:End");
			if(a==-1) 
				a=editing.numOfRuns;
			//System.out.println("180");
			ExtensibleDialog hey=new ExtensibleDialog(this,"P value");
			hey.addLargeOrSmallTextLabel("The P value is "+((double)(a)/(double)(editing.numOfRuns)));
			//hey.addLargeOrSmallTextLabel("Debug:"+a);
			hey.addLargeOrSmallTextLabel("The F value is "+testStat);
			hey.completeAndShowDialog();
			
			/*
			boolean search=true;
			int guess=Math.round((float)((testStat/groupFstat[editing.numOfRuns-1])*(double)(editing.numOfRuns)));
			int step=-1;
			int top=editing.numOfRuns-1;
			int bottom=0;
			while(search){
				if (guess>0){
					if (guess!=editing.numOfRuns-1){	
						if(groupFstat[guess]>=testStat&&groupFstat[guess-1]<=testStat){
							search=false;
						}
						else if(groupFstat[guess]>testStat){
							if(step==-1){
								step=guess/2;
							}
							else step=step/2;
							top=guess;
							guess-=step;
						}
						else{
							if(step==-1){
								step=guess/2;
							}
							else step=step/2;
							bottom=guess;
							guess+=step;							
						}
					}
					else if(groupFstat[guess]<=testStat){
						search=false;
					}
				}
				else if(groupFstat[guess]>=testStat){
					search=false;
				}
				*/
			
			//Fstat[+ic][run]
		}
		else{
			System.out.println("An error has occured. number of taxa do not match");
		}
	}
	else if (checker.compare(this.getClass(), "Shows groups used", "[null]", commandName, "showGroups")) {
		ExtensibleDialog popup=new ExtensibleDialog(this,"Group Members");
		String[] groupNames;
		Vector[] groups;
		groupNames=new String[editing.numOfGroups];
		for(int x=0;x<editing.numOfGroups;x++){
			groupNames[x]="Group "+(x+1);
		}
		//taxaNames=editing;
		groups=editing.getGroupVector();
		String[] nodeNameVector=new String[editing.nodeVector.length];
		nodeNameVector=PDAnova.getTipNameVector(nodeNameVector, editing.results.tree, editing.results.tree.getRoot(), 0);

		for(int x=0;x<groups.length;x++){
			String str="";
			for(int y=0;y<groups[x].size();y++){
				if(y%10==0&&y!=0) str+="\n";
				str+=""+nodeNameVector[((MesquiteInteger)groups[x].get(y)).getValue()]+", ";
				//System.out.println("value"+((MesquiteInteger)groups[x].get(y)).getValue());
				//System.out.println("Name"+editing.results.nodeNameVector[((MesquiteInteger)groups[x].get(y)).getValue()]);
				//System.out.println("Num"+editing.nodeVector[((MesquiteInteger)groups[x].get(y)).getValue()]);
				//System.out.println("Num-name"+editing.results.nodeNameVector[editing.results.nodeNumVector[((MesquiteInteger)groups[x].get(y)).getValue()]]);
				
			}
			popup.addLabel(groupNames[x]+":"+str);
			System.out.println(groupNames[x]+":"+str);			
		}
		popup.completeAndShowDialog();
	}
	else
		return  super.doCommand(commandName, arguments, checker);
	return null;
}
}
class AnalysisNamesLister implements StringLister{
	pdanovaResults resultss;
	//Class subclass;
	public AnalysisNamesLister(pdanovaResults resultss){
		this.resultss = resultss;
	}
	public String[] getStrings() {
		return resultss.getStrings();
	}
}

