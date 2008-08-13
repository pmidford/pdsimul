package mesquite.pdsim.lib;

/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison. 
Version 2.01, December 2007.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
 */
//package mesquite.parsimony.StepMatrixCurator;
/*~~  */


import mesquite.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.duties.*;
import mesquite.cont.lib.ContinuousHistory;
import mesquite.cont.lib.ContinuousState;
import mesquite.cont.lib.ContinuousStateTest;
import mesquite.stochchar.lib.ProbabilityContCharModel;
import mesquite.stochchar.lib.SimModelCompatInfo;
import mesquite.pdsim.PDAnova.*;
import mesquite.pdsim.SimCurator.*;
import mesquite.cont.lib.*;

//import java.io.IOException;
//import mesquite.stochchar.EvolveContinuous.*;

//import java.text.

//import mesquite.pdsim.CoVarMatrixModel.*;

/* ====================================================================== */
/*public class CoVarMatrixCurator {
	
}*/
public class CoVarMatrixCurator extends WholeCharModelCurator implements EditingCurator, DataCurator {
	//for (int x=0; x<3; x++) CorTraitTask[x]=null;
//	int ModelVector[]=null;
//	int CTTask_size=3;
//	int ModelVector_size=0;
	CoVarMatrixModel modelToEdit;
	
	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed
		
		 EmployeeNeed e = registerEmployeeNeed(WindowHolder.class, getName() + " need assistance to hold a window" ,
				 "This is arranged automatically");
		 EmployeeNeed e2 = registerEmployeeNeed(CTSubModelCurator.class, getName() + " need CorTrait Models.", "This should grab them");
	}
//	modelToEdit.setModelVector(CorTraitTask, ModelVector);

	public String getShortModelName(int x){
		String str = ""+x;
		return str;
	}
	public String getName() {
		return "Co-Variance matrix";
	}
	public String getExplanation() {
		return "Supplies editor for and manages CoVariance matrices.";
	}

	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
		return true;
	}

	/*.................................................................................................................*/
	public String getNameForMenuItem() {
		return "Co Variance Matrix";
	}

	public Object doCommand(String commandName, String arguments, CommandChecker checker) {
		//Vector str=checker.compare(classCommanded, explanation, parameters, command, targetCommand) //getAccumulatedCommands(this.getClass());
		//str.isEmpty()
		
//TODO this should probablly be moved to CoVarMatrix, and changed accordingly.	

		if (checker.compare(this.getClass(), "Sets the number of traits in the covariance matrix", "[number of traits]", commandName, "createModel")) {
			
			CTSubModelCurator newCorTraitTask;
			newCorTraitTask=(CTSubModelCurator)hireEmployee(CTSubModelCurator.class, "Please Chose a new submodel:");
			newCorTraitTask.doCommand("newModel", "Untitled", CommandChecker.defaultChecker);
			//System.out.println("lame!");
			System.out.print(newCorTraitTask.getName());
		}
		if (checker.compare(this.getClass(), "run a simulation", "[number of traits]", commandName, "newResults")) {
			
			SimCurator newsim;
			newsim=(SimCurator)hireEmployee(SimCurator.class, "Please Chose a simulation curator:");

			//MesquiteInteger buttonPressed = new MesquiteInteger(1);
			
			if(modelToEdit.isFullySpecified()){
				ExtensibleDialog makeFileDialog = new ExtensibleDialog(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "PDSIM: Simulation run");
				//SingleLineTextField fileNameField = makeFileDialog.addTextField("Name:", "Noname", 30);
				IntegerField numOfRunsField = makeFileDialog.addIntegerField("Number of Runs:", 10, 6);
				DoubleField stepSizeField = makeFileDialog.addDoubleField("Step_size (enter 0 when not using bounding):",0, 6);
				//RadioButtons rb = makeFileDialog.addRadioButtons(new String[]{"Make Sim File","Make Csv File"},0);
			
				makeFileDialog.completeAndShowDialog();
			
				int numOfRuns=numOfRunsField.getValue();
				ContinuousHistory[][] sim;
				sim=CoVarSimulations.run_sim(modelToEdit, numOfRuns, stepSizeField.getValue());
				//System.out.println("runDone");
				newsim.initialize(modelToEdit, 	modelToEdit.getTree(), numOfRuns, sim);
				newsim.doCommand("newResults", "Untitled", CommandChecker.defaultChecker);
			}
			else {
				ExtensibleDialog errorDialog = new ExtensibleDialog(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Error");
				errorDialog.addLabel("The Co Varaince matrix has unassigned models.");
				errorDialog.addLabel("Please assign models by click on row names");  
				errorDialog.completeAndShowDialog();

			}
		}
	return null;
	}
	/*.................................................................................................................*/
	/** passes which object changed, along with optional code number (type of change) and integers (e.g. which character)*/
	public void changed(Object caller, Object obj, Notification notification){
		if (obj instanceof CharacterModel){
			int i = getModelNumber((CharacterModel)obj);
			if (i>=0) {
				PDSIMDataWindow window = (PDSIMDataWindow)getWindow(i);
				if (window!=null)
					window.setDataSource((DataSource)obj);
			}
		}
		super.changed(caller, obj, notification);
	}
	public void TraitNumChanged(Object caller, Object obj, Notification notification){
		if (obj instanceof CharacterModel){
			int i = getModelNumber((CharacterModel)obj);
			if (i>=0) {
				PDSIMDataWindow window = (PDSIMDataWindow)getWindow(i);
				if (window!=null)
					window.setDataSource((CorTraitModel)obj);
			}
		}
	}

	/*.................................................................................................................*/
	public MesquiteModule editModelNonModal(CharacterModel model, ObjectContainer w){
		if (model!=null && model instanceof CoVarMatrixModel) {
			CoVarMatrixModel modelToEdit = (CoVarMatrixModel)model;
			
			MesquiteModule windowServer = hireNamedEmployee(WindowHolder.class, "#WindowBabysitter");
			if (windowServer == null)
				return null;
			MesquiteCommand hi=new MesquiteCommand("hi",this);

			PDSIMDataWindow window = new PDSIMDataWindow(this, windowServer);
			windowServer.makeMenu("Covariance Matrix");
			//windowServer.addM
			windowServer.addMenuItem("Run simulation...", makeCommand("newResults", this));
			windowServer.addMenuItem("Show colesky...", makeCommand("toggelCholesky", modelToEdit));
//			windowServer.addMenuItem("Run simulation...", makeCommand("runSim", modelToEdit));
			//TODO actually make these procedures!!
			windowServer.addMenuItem("Create new sub model...", makeCommand("createModel", this));
			windowServer.addMenuItem("Debug: Set Models to List...", makeCommand("setToList", modelToEdit));
			windowServer.addMenuItem("Debug: Set Number of traits...", makeCommand("setTraitNum", modelToEdit));
	//		windowServer.addMenuItem("Make Models From Matrix...", makeCommand("makeDefault", this));
	//		windowServer.addMenuItem("Run PDANOVA on model...", makeCommand("runAnova", this));
			
			
			modelToEdit.setWindow(window);

			window.setDefaultAnnotatable(model);
			window.setDataSource((CoVarMatrixModel)model);
			this.modelToEdit=modelToEdit;
			
			if (w!=null)
				w.setObject(window);
			return windowServer;
		}
		return this;
	}
	public MesquiteModule getModule (){
		return this;
	}
	public boolean curatesModelClass(Class modelClass){
		return CoVarMatrixModel.class.isAssignableFrom(modelClass);
	}
	/*.................................................................................................................*/
	public String getNameOfModelClass() {
		return "CoVarMatrix";
	}
	/*.................................................................................................................*/
	public String getNEXUSNameOfModelClass() {
		return "CoVarMatrix";
	}
	/*.................................................................................................................*/
	public Class getModelClass() {
		return CoVarMatrixModel.class;
	}
	public boolean isPrerelease(){
		return true;
	}
	/*.................................................................................................................*/
	public CharacterModel makeNewModel(String name) {
		Tree tree=null;
		Taxa taxa=null;
		MContinuousDistribution charData=null;
		TreeSource treeTask = (TreeSource)this.hireEmployee(TreeSource.class, "Source of trees");
		CharMatrixSource charTask = (CharMatrixSource)this.hireEmployee(CharMatrixSource.class, "Source of Character Data");
		taxa=getProject().getTaxa(0);
		if (treeTask!=null){
			if(treeTask.getNumberOfTrees(taxa)>1)
				tree=treeTask.getTree(taxa, treeTask.queryUserChoose(taxa, "Please chose a tree"));
			else tree=treeTask.getTree(taxa, 0);
		}
		if (charTask!=null){
			if(charTask.getNumberOfMatrices(taxa)>1)
				charData=(MContinuousDistribution)charTask.getMatrix(taxa, charTask.queryUserChoose(taxa, "Please chose character data"));
			else charData=(MContinuousDistribution)charTask.getMatrix(taxa, 0);
		}
		CoVarMatrixModel model = new CoVarMatrixModel(name, getProject(), this, tree, taxa, charData);

		//if (model.failed()){
			//MesquiteModule mod;
			//mod=getEmployer();
			//super.editCancel=true;
		//	return null; 
		//}
		return model;
	}
	/*.................................................................................................................*/
	public CharacterModel readCharacterModel(String name, MesquiteInteger stringPos, String description, int format) {
		//TODO this should hire trees and what not from description;
		Tree tree;
		Taxa taxa;
		MContinuousDistribution charData;
		TreeSource treeTask = (TreeSource)this.hireEmployee(TreeSource.class, "Source of trees");
		//TaxaSource taxaTask = (TaxaSource)this.hireEmployee(TaxaSource.class, "Source of taxa");
		CharMatrixSource charTask = (CharMatrixSource)this.hireEmployee(CharMatrixSource.class, "Source of Character Data");
		System.out.println("got employees");
		taxa=getProject().getTaxa(0);
		System.out.println("got taxa");
		tree=treeTask.getTree(taxa, treeTask.queryUserChoose(taxa, "Please chose a tree"));
		System.out.println("got tree");
		charData=(MContinuousDistribution)charTask.getMatrix(taxa, charTask.queryUserChoose(taxa, "Please chose character data")); //getCcharTask.queryUserChoose(taxa, "Please chose a character Data");
		System.out.println("got char");
		CoVarMatrixModel model = new CoVarMatrixModel(name, getProject(), this, tree, taxa, charData);
		model.fromString(description, stringPos, format);
		return model;
	}
	/*.................................................................................................................*/

	//public String getNexusCommands(MesquiteFile file, String blockName){
	//	return "Begin COVARMATRIX;";
	//}
}

/*=======================================================*/
/*class StepmatrixNexusCmdTest  extends NexusCommandTest{
	public boolean readsWritesCommand(String blockName, String commandName, String command){  //returns whether or not can deal with command
		return (blockName.equalsIgnoreCase("ASSUMPTIONS") && commandName.equalsIgnoreCase("stepmatrix"));
	}
}*/

/* ======================================================================== */


class ECorCoCCategoricalStateTest extends ContinuousStateTest{
	ModelCompatibilityInfo mci;
	public ECorCoCCategoricalStateTest (){
		super();
	}
	public  boolean isCompatible(Object obj, MesquiteProject project, EmployerEmployee prospectiveEmployer){
		if (project==null){
			return super.isCompatible(obj, project, prospectiveEmployer);
		}
		Listable[] models = project.getCharacterModels(mci, ContinuousState.class);
		if (models == null || models.length == 0)
			return false;
		boolean oneFound = false;
		for (int i=0; i<models.length; i++)
			if (((ProbabilityContCharModel)models[i]).isFullySpecified()) {
				oneFound = true;
				break;
			}
		if (oneFound){
			return super.isCompatible(obj, project, prospectiveEmployer);
		}
		return false;
	}
//	public getCompatible
}

class CoVarSimulations {
	
	public static ContinuousHistory[][] run_sim(CoVarMatrixModel model, int num_of_runs, double step_size){

		Tree tree;
		ProbabilityCorContCharModel CorTraitTask[];//=model.CorTraitTask;
		CorTraitTask=model.CorTraitTask;
		ContinuousHistory sim[][] = new ContinuousHistory[num_of_runs][model.num_of_traits];
		int start_node;

		//TreeSource treeTask=model.tree;
		Taxa taxa;
		/*MesquiteString treeTaskName;

		treeTaskName = new MesquiteString();

		treeTaskName.setValue(treeTask.getName());

		if (treeTask == null)  {
			System.out.println("Tree Task null (PDsim)!!");
		}*/

		taxa = model.taxa;

		if (taxa == null)  {
			System.out.println("Taxa null (PDsim)!!");
		}
		
		tree = model.tree;
		
		if (tree==null){
			System.out.println("Tree null (PDsim)!!");
		}

		//double startvector[] = new double[model.num_of_traits];

		System.out.println("Starting run . . .");
		
		for(int x=0; x<num_of_runs;x++){
			start_node=tree.getRoot();

			for (int y=0;y<model.num_of_traits;y++){

				sim[x][y] =  new ContinuousHistory(tree.getTaxa(), tree.getNumNodeSpaces(), null);
				for (int z=1;z<CorTraitTask[y].get_obj_num();z++){
//					System.out.println(""+CorTraitTask[y].getName()+" : "+z);
					sim[x][y].addItem(""+z);
					sim[x][y].setState(start_node, z, CorTraitTask[y].getRootValue(z));
				}
				sim[x][y].setState(start_node, CorTraitTask[y].getRootValue(0));
			}
		
			model.traits.setNodeNum(2);

			MesquiteLong seed=null;
			//double step_size=1;
			sim[x]=getCoVarMatrixHistory(sim[x], tree, model.traits, CorTraitTask, seed, step_size);
		}
		/*
		try {
			if(fileType==0)
				SimFile.MakeSimFile(model, fileName,tree, sim, num_of_runs);
			else CsvFile.MakeCsvFile(model, fileName, tree, sim, num_of_runs);
		} 
		catch (Exception em) {
			// TODO Auto-generated catch block
			em.printStackTrace();
		}*/
		return sim;
	}

	public static ContinuousHistory[] getCoVarMatrixHistory(ContinuousHistory sim[], Tree tree, CorTrait rng, ProbabilityCorContCharModel model[], MesquiteLong seed, double step_size){
		// TODO Insert error checking
		rng.get_cholesky();
		rng.make_traits();
		for(int x=0;x<rng.getTraitNum();x++)
			if(!model[x].isInitialized()) model[x].initialize(tree);
		evolveCor(tree, sim, tree.getRoot(), rng, model, step_size);
/*		*/
		return sim; 
	}

	private static void evolveCor(Tree tree, ContinuousHistory states[], int node, CorTrait rng, ProbabilityCorContCharModel model[], double step_size){
		
		if (node!=tree.getRoot()) {
			double time;
		
			rng.make_traits();
			for (int x=0; x<rng.num_of_traits;x++){
				double statesAtAncestor[] = new double[model[x].get_obj_num()];
				double statesAtNodes[] = new double[model[x].get_obj_num()];
				
				if(model[x].isSpeciational()){
					time=1;
				}
				else time=tree.getBranchLength(node, 1);
				
				double round;
				//TODO this doesn't really work right.
				//int mark=0;
				//System.out.println("a"+mark);
				//mark++;
				if(step_size!=0){

					round=Math.floor(time/step_size);
					round=step_size+(((time/step_size)-round)/round);
				}
				else round=time;

				//System.out.println("b"+mark);
				//mark++;

				for(int y=0;y<model[x].get_obj_num();y++)
					statesAtAncestor[y]=states[x].getState(tree.motherOfNode(node), y);
				//System.out.println("c"+mark);
				//mark++;
				
				if (tree.getNodeLabel(node)!=tree.getNodeLabel(tree.motherOfNode(node)) || !model[x].isPunctuational()){
					double y=round;

					//System.out.println("d"+mark);
					//mark++;
					
					while(y<=time){
						
						//System.out.println("e"+mark);
						//mark++;
						
						//System.out.println("time"+time);
						//System.out.println("y"+y);
						
						if (time==0||round==0){
							y=time+1;
						}
						statesAtNodes = model[x].evolveMultiState(statesAtAncestor, rng, round, node, x, model[x].isBound());
						statesAtAncestor=statesAtNodes;
						y=y+round;
					}
				}
				else statesAtNodes=statesAtAncestor;

				//System.out.println("f"+mark);
				//mark++;
				
				for(int y=0;y<model[x].get_obj_num();y++){
					states[x].setState(node, y, statesAtNodes[y]);
				}
				
				//System.out.println("g"+mark);
				//mark++;
	
			}
		}
		
		for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter))
			evolveCor(tree, states, daughter, rng, model,step_size);
	}		
	//MesquiteFileDialog
}

