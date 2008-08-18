package mesquite.pdsim.SimCurator;

import java.util.List;

import mesquite.cont.lib.ContinuousHistory;
import mesquite.cont.lib.ContinuousState;
import mesquite.cont.lib.MContinuousDistribution;
import mesquite.lib.CommandChecker;
import mesquite.lib.EmployeeNeed;
import mesquite.lib.MesquiteDouble;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.ObjectContainer;
import mesquite.lib.ParseUtil;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.characters.CharacterModel;
import mesquite.lib.characters.MCharactersDistribution;
import mesquite.lib.duties.CharMatrixSource;
import mesquite.lib.duties.TreeSource;
import mesquite.lib.duties.WindowHolder;
import mesquite.pdsim.lib.*;
import mesquite.pdsim.*;
import mesquite.pdsim.PDAnovaCurator.*;
import mesquite.lib.*;
import mesquite.pdsim.lib.CoVarMatrixModel;
//import mesquite.pdsim.

public class SimCurator extends ResultsCurator{
	String name;
	CoVarMatrixModel model;
	Tree tree;
	int numOfRuns=0;
	ContinuousHistory[][] sim;
	public simResults editing;
	boolean initialized=false;
	public MesquiteModule getModule(){
		return this;
	}
	/*public String getNEXUSCommand(){
		reuturn "RESULTS";
	}*/
	public Class getResultClass(){
		return simResults.class;
	}
	public Results makeempty(String name){
		simResults r=new simResults(name, Results.class);
		editing=r;
		return r;
	}
	/*.................................................................................................................*/
	public String getNameOfResultsClass() {
		return "SimulationResult";
	}
	/*.....................getModelName............................................................................................*/
	public String getNEXUSNameOfResultClass() {
		return "SimulationResult";
	}
	public void initialize (CoVarMatrixModel model, Tree tree, int numOfRuns, ContinuousHistory[][] sim){
		this.sim=sim;
		this.model=model;
		this.tree=tree;
		this.numOfRuns=numOfRuns;
		if (sim!=null&&model!=null&&tree!=null&&numOfRuns!=0)
			initialized=true;
	}
	public String getName(){
		return "Simulation Results";
	}
	public Results subfinish(CoVarMatrixModel model,String name, String description, MesquiteInteger stringPos, int format){
		//simResults results=null;
  		System.out.println("finishing!");
		String value1, var1;
		var1 = ParseUtil.getToken(description, stringPos); // eating token
		value1 = ParseUtil.getToken(description, stringPos); // eating token
  		System.out.println("var/value"+var1+"  "+value1);
		if(var1.equalsIgnoreCase("numOfRuns")){
			numOfRuns=MesquiteInteger.fromString(value1);
		}
	
		simResults results=new simResults(name, Results.class, model, model.getTree(), numOfRuns);
		this.model=model;
		this.tree=model.getTree();
		initialized=true;

		editing=results;
		editing.fromString(description, stringPos, format);
 		return editing;
   		//System.out.println("help!");
   		//sim.fromString(description, stringPos, format);
   	}
 	public Results readResults(String name, MesquiteInteger stringPos, String description, int format) {
 		simResults results=null;
		String value1, var1, var2, value2;
		var1 = ParseUtil.getToken(description, stringPos); // eating token
		value1 = ParseUtil.getToken(description, stringPos); // eating token
		var2 = ParseUtil.getToken(description, stringPos); // eating token
		value2 = ParseUtil.getToken(description, stringPos); // eating token
		String treeSource=null, modelSource=null;
		
		if(var1.equalsIgnoreCase("modelname") && var2.equalsIgnoreCase("treename")){
			modelSource=value1;
			treeSource=value2;
		}
		
		int modelNum=0, treeNum=0;

		var1 = ParseUtil.getToken(description, stringPos); // eating token
		value1 = ParseUtil.getToken(description, stringPos); // eating token
		var2 = ParseUtil.getToken(description, stringPos); // eating token
		value2 = ParseUtil.getToken(description, stringPos); // eating token

		if(var1.equalsIgnoreCase("modelnum") && var2.equalsIgnoreCase("treenum")){
			modelNum=MesquiteInteger.fromString(value1);
			treeNum=MesquiteInteger.fromString(value2);
		}

		System.out.println("names:"+treeSource+","+modelSource+" numbers:"+treeNum+","+modelNum);
		// charData;
		if(treeSource!=null&&modelSource!=null){
			System.out.println("1");
		//TreeSource treeTask = (TreeSource)this.hireNamedEmployee(TreeSource.class, "\'"+treeSource+"\'");
		System.out.println("2");
		//CoVarMatrixCurator charTask = (CoVarMatrixCurator)this.hireNamedEmployee(CoVarMatrixCurator.class, "\'"+modelSource+ "\'"); //hireEmployee(CharMatrixSource.class, "Source of Character Data");
		;
		Taxa taxa=passtaxa;
		//if(treeTask==null)System.out.println("WTF!");
		
		System.out.println("1 "+taxa.toString());
		//tree=treeTask.getTree(taxa, treeNum); //(taxa, treeTask. //.queryUserChoose(taxa, "Please chose a tree"));
		System.out.println("2");
		CoVarMatrixModel firstData;
		//this.model=firstData;
 		var1 = ParseUtil.getToken(description, stringPos); // eating token
 		numOfRuns=MesquiteInteger.fromString(var1);
 		//this.tree=tree;
 		//this.numOfRuns=numOfRuns;
 		//this.sim=sim;
 		if (initialized)
 		results = new simResults("simResults", Results.class,model,tree,numOfRuns);
 		results.fromString(description, stringPos, format);
   		//System.out.println("help!");
   		//sim.fromString(description, stringPos, format);
   		return results;
		}
		return null;
 	}
 	public MesquiteModule editResultNonModal(Results result, ObjectContainer w){
		System.out.println("simResults editResultNonModal called.");
		if (result!=null && result instanceof simResults) {
			simResults resultsToEdit =  ((simResults)result);
			this.editing=resultsToEdit;
			System.out.println("Setting model: editResultsNonModal");
			MesquiteModule windowServer = hireNamedEmployee(WindowHolder.class, "#WindowBabysitter");
				if (windowServer == null)
			return null;
				
				PDSIMDataWindow cw=new PDSIMDataWindow(this, windowServer);
				cw.setDataSource(resultsToEdit);
				//cw.ch
				//resultsToEdit
				windowServer.makeMenu("Simulation Results");
				windowServer.addMenuItem("Do Statistical Analasys ...", makeCommand("runStats", this));
				windowServer.addMenuItem("Save Results to file ...", makeCommand("saveSim", this));
				
				MesquiteWindow.centerWindow(cw);
				//cw.show();
				//cw.

			if (w!=null)
				w.setObject(cw);
			return windowServer;
		}
		return null;
   	}
	public Object doCommand(String commandName, String arguments, CommandChecker checker) {
		if (checker.compare(this.getClass(), "Run a statistical analysis of these results", "[number of traits]", commandName, "runStats")) {
			System.out.println("Starting Statistical analysis.");
			PDAnovaCurator newStat;
			newStat=(PDAnovaCurator)hireEmployee(PDAnovaCurator.class, "Please Chose an analysis package:");
			newStat.initialize(this.editing);
			newStat.doCommand("newResults", "Untitled", CommandChecker.defaultChecker);
		}
		else if (checker.compare(this.getClass(), "Run a statistical analysis of these results", "[number of traits]", commandName, "saveSim")) {
			System.out.println("Saving results to external file");
			ExtensibleDialog fileDialog=new ExtensibleDialog (this, "Chose Format:");
			RadioButtons rb = fileDialog.addRadioButtons(new String[]{"Make Sim File","Make Csv File"},0);
			fileDialog.completeAndShowDialog();

			try {
				// TODO Auto-generated catch block
				if(rb.getValue()==0)
					SimFile.MakeSimFile(model, this.getModule(), tree, sim, numOfRuns);
				else
					DataFile.MakeDataFileWithDialog(editing, "defaultname",this.getModule());
			}
			catch (Exception em) {
				em.printStackTrace();
			}			
		}
		else
			return  super.doCommand(commandName, arguments, checker);
		return null;
	}

 	public String getNameOfResultClass(){
 		return "boo";
 	}
   	public Results makeNewResults(String name) {
   		this.name=name;
   		simResults results=null;
 		if (initialized)
 			results = new simResults(name, Results.class,model,tree,numOfRuns,sim);
		this.editing=results;
		return results;
   	}
   	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
   		System.out.println("startingJob");
 		return true;
	}
   	public String getNEXUSCommand(){
   		return "SIMDATA";
   	}
	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed
		 EmployeeNeed e = registerEmployeeNeed(WindowHolder.class, getName() + " needs assistance to hold a window" ,
				 "This is arranged automatically");
	}
}
