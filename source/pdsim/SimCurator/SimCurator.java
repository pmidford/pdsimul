package mesquite.pdsim.SimCurator;

import java.util.List;

import mesquite.cont.lib.ContinuousHistory;
import mesquite.cont.lib.ContinuousState;
import mesquite.lib.CommandChecker;
import mesquite.lib.MesquiteDouble;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.ObjectContainer;
import mesquite.lib.Tree;
import mesquite.lib.characters.CharacterModel;
import mesquite.lib.duties.WindowHolder;
import mesquite.pdsim.lib.*;
import mesquite.pdsim.*;
import mesquite.pdsim.PDAnovaCurator.*;
import mesquite.lib.ExtensibleDialog;
import mesquite.lib.*;
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
	public Class getResultClass(){
		return simResults.class;
	}
	/*.................................................................................................................*/
	public String getNameOfResultsClass() {
		return "Statistical_Results";
	}
	/*.....................getModelName............................................................................................*/
	public String getNEXUSNameOfResultsClass() {
		return "Statistical_Results";
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
 	public Results readResults(String name, MesquiteInteger stringPos, String description, int format) {
 		simResults results=null;
 		if (initialized)
 			results = new simResults("simResults", Results.class,model,tree,numOfRuns,sim);
   		//System.out.println("help!");
   		//sim.fromString(description, stringPos, format);
   		return results;
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
			ExtensibleDialog fileDialog=new ExtensibleDialog (this, "Save File:");
			SingleLineTextField nameField;
			
			nameField=fileDialog.addTextField(""+editing.getName(), 10);
			RadioButtons rb = fileDialog.addRadioButtons(new String[]{"Make Sim File","Make Csv File"},0);
			
			fileDialog.completeAndShowDialog();

			String str=nameField.getText();
			try {
				// TODO Auto-generated catch block
				//if(rb.getValue()==0)
				//	SimFile.MakeSimFile(model, str, tree, sim, numOfRuns);
				//else
				//	CsvFile.MakeCsvFile(model, str, tree, sim, numOfRuns);
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
}
