package mesquite.pdsim.lib;

import mesquite.cont.lib.ContinuousState;
import mesquite.lib.CommandChecker;
import mesquite.lib.DoubleField;
import mesquite.lib.ExtensibleDialog;
import mesquite.lib.IntegerField;
import mesquite.lib.MesquiteDouble;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteListener;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteProject;
import mesquite.lib.MesquiteThread;
import mesquite.lib.MesquiteTrunk;
import mesquite.lib.Notification;
import mesquite.lib.ParseUtil;
import mesquite.lib.RadioButtons;
import mesquite.lib.SingleLineTextField;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.lib.characters.CharacterModel;
import mesquite.lib.characters.ModelCompatibilityInfo;
import mesquite.lib.duties.TreeSource;
import mesquite.stochchar.lib.ProbabilityContCharModel;
import mesquite.stochchar.lib.SimModelCompatInfo;
import mesquite.cont.lib.ContinuousHistory;

public class CoVarMatrixModel  extends ProbabilityContCharModel implements CorTraitModel {
		ProbabilityCorContCharModel[] CorTraitTask={null,null,null};
		int[] states = new int[10];
		double rate;
		int times_called=0;
		int trait_num=0;
		int number_of_nodes;
		MesquiteModule module;
		CoVarTableWindow window = null;
		int num_of_traits, num_of_models;
		MesquiteNumber utilityNumber;
		MesquiteProject project;
		int numStates=0;
		public ContinuousHistory sim[][];
		public CorTrait traits;
		MesquiteInteger pos = new MesquiteInteger();

		TreeSource treeTask;
		Taxa taxa;

		public void attachtreesource(TreeSource treeTask){
			this.treeTask=treeTask;
		}	
		public void setTaxa (Taxa taxa){
			this.taxa=taxa;
		}
		public int getNumTaxa(){
			return taxa.getNumTaxa();
		}

		
		public double evolveState (double beginState, Tree tree, int node){
//			number of nodes should onlt be initialized once.
					if(times_called==0&&trait_num==0){
//						CoVarMatrixModel model=(CoVarMatrixModel)super.makeNewModel("two");
					//	this.traits.getClass();
						//CoVarMatrixModel bmm = new CoVarMatrixModel(name, getProject());
						//this.traits=bmm.traits;
						number_of_nodes=tree.numberOfNodesInClade(tree.getRoot())-1;
						traits.setNodeNum(number_of_nodes);
						//bmm.traits.newfunction(2);
						traits.make_traits();
					}
					if(times_called==number_of_nodes){
						times_called=0;
						trait_num++;
					}
					//double State=trait_num;
					if(trait_num<3){
					//	return 1;
						times_called++;
						return beginState + traits.get_num(times_called-1, trait_num); //Math.sqrt (tree.getBranchLength(node, 1.0));   //pre-1.05 this failed to take the sqrt!!!!
					}

					//else 
						return 0;
					//return State;
				}
			 	/*.................................................................................................................*/
				/** Returns (possibly by randomly generating) according to model an ancestral state for root of tree*/
				public double getRootState (Tree tree){
					return 0;  //todo: stochastic?
				}			
			 	/*.................................................................................................................*/
				public String getParameters() {
					return "rate " + MesquiteDouble.toString(rate);
				}
			 	/*.................................................................................................................*/
				public void setRate(double rate){
					this.rate = rate;
					notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
				}
			 	/*.................................................................................................................*/
				public double getRate(){
					return rate;
				}
			 	/*.................................................................................................................*/
			 	/** Performs command (for Commandable interface) */
				public CharacterModel cloneModelWithMotherLink(CharacterModel formerClone){
					CoVarMatrixModel bmm = new CoVarMatrixModel(name, getProject(), module);
			//		completeDaughterClone(formerClone, bmm);
					return bmm;
				}
			 	/* copy information from this to model passed (used in cloneModelWithMotherLink to ensure that superclass info is copied); should call super.copyToClone(pm) */
				/*public void copyToClone(CharacterModel md){
					if (md == null || !(md instanceof Gradual_OUModel))
						return;
					CoVarMatrixModel model = (CoVarMatrixModel)md;
					model.setRate(rate);
					super.copyToClone(md);
				}*/
				public boolean isFullySpecified(){
					return rate != MesquiteDouble.unassigned;
				}
				public void setSeed(long seed){
					//trait.setSeed(seed);
				}
				
				public long getSeed(){
					return traits.getSeed();
				}
		
		public CoVarMatrixModel (String name, Class dataClass) {
			super(name, dataClass);
		}
		

		public int getTraitNum(){
			return traits.getTraitNum();
		}
		
		public void setTraitNum(int newvar){
			int oldnum_of_traits=num_of_traits;
			num_of_traits=newvar;
			ProbabilityCorContCharModel[] newCorTraitTask=new ProbabilityCorContCharModel[num_of_traits];
			if (oldnum_of_traits!=num_of_traits){
				if (oldnum_of_traits<num_of_traits){
					for(int x=0;x<oldnum_of_traits;x++){
						newCorTraitTask[x]=CorTraitTask[x];
					}
					for(int x=oldnum_of_traits;x<num_of_traits;x++){
						newCorTraitTask[x]=null;
					}
					CorTraitTask=new ProbabilityCorContCharModel[num_of_traits];
					for(int x=0;x<num_of_traits;x++){
						CorTraitTask[x]=newCorTraitTask[x];
					}
				}
				else if (oldnum_of_traits>num_of_traits){
					for(int x=0;x<num_of_traits;x++){
						newCorTraitTask[x]=CorTraitTask[x];
					}
					CorTraitTask=new ProbabilityCorContCharModel[num_of_traits];
					for(int x=0;x<num_of_traits;x++){
						CorTraitTask[x]=newCorTraitTask[x];
					}
				}
			}
			traits.setTraitNum(num_of_traits);
			notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
		}
		public void get_cholesky (){
			traits.get_cholesky();
		}
//		public String getParadigm(){
//			return "Correlated Tvraits";
//		}
		public String getModelTypeName(){
			return "Correlated Traits";
		}
		String ModelList= "";
		public void setToList(){
			MesquiteInteger stringPos=new MesquiteInteger(0);
			String name;
			for(int x=0;x<num_of_traits;x++){
				name=ParseUtil.getToken(ModelList, stringPos);
				CorTraitTask[x]=(ProbabilityCorContCharModel)getProject().getCharacterModel(name);
				System.out.println("setto:"+name);
			}
			notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
		}
		public void fromString (String description, MesquiteInteger stringPos, int format) {
			String str;
			System.out.println("hi");
			str=ParseUtil.getToken(description, stringPos); //eating token
			System.out.println(str);
			str=ParseUtil.getToken(description, stringPos); //eating token
			System.out.println(str);
			//num_of_traits=MesquiteDouble.fromString(str);
			int temp2=MesquiteInteger.fromString(str);
			System.out.println(temp2);
				setTraitNum(temp2);
			System.out.println("hi2");
			
			str=ParseUtil.getToken(description, stringPos); //eating token
			System.out.println("Heading:"+str);
			for(int x=0;x<num_of_traits;x++){
				str=ParseUtil.getToken(description, stringPos); //eating token
				System.out.println(x+":"+str);
				//ProbabilityCorContCharModel temp=null;
				//MesquiteTrunk.mesquiteTrunk.getProject().getCharacterModel("Ornstein_Uhlenbeck default");
				System.out.println("yup.");
				//System.out.println(temp.getName());
				ModelList+=" "+"'"+str+"'";
			}
			System.out.println("hi3");
			//str=ParseUtil.getToken(description, stringPos); //eating token
			for(int x=0;x<num_of_traits;x++){
				for(int y=0;y<num_of_traits;y++){
					str=ParseUtil.getToken(description, stringPos); //eating token
					//z.setValue(str);
					traits.setTransitionValue(x,y,MesquiteDouble.fromString(str),false);			
				}
			}
		//	if(str.equalsIgnoreCase("))		
		}
		public String getNexusSpecification(){
			String specs;
			System.out.println("hi");
			specs="numoftraits ";
			specs+=num_of_traits;
			System.out.println("set 1");
			specs+=" CorTraitTasks ";
			for (int x=0;x<num_of_traits;x++){
				specs+="'"+CorTraitTask[x].getName()+"'"+" ";
				//getProject().getCharacterModel(new SimModelCompatInfo(ProbabilityCorContCharModel.class, ContinuousState.class),0);
			}
			System.out.println("set 2");
			for(int x=0;x<num_of_traits;x++){
				for(int y=0;y<num_of_traits;y++){
					specs+=traits.getTransitionValue(x, y)+" ";				
				}
			}
			System.out.println("set 3");
			return specs;
		}
		public CoVarMatrixModel (String name, MesquiteProject project, MesquiteModule module) {
			super(name, ContinuousState.class);
			this.project = project;
			this.module = module;
			//TODO: num of traits and num of models are not initialized yet!!!!!!
			num_of_traits=3;
			num_of_models=1;
			traits = new CorTrait (num_of_traits);

			traits.setTransitionValue(0, 1, .6, false);
			traits.setTransitionValue(0, 2, .3, false);
			traits.setTransitionValue(1, 2, .5, false);

			traits.setTransitionValue(1, 0, .6, false);
			traits.setTransitionValue(2, 0, .3, false);
			traits.setTransitionValue(2, 1, .5, false);

			for (int x=0; x < num_of_traits; x++) {
					traits.setTransitionValue(x, x, 1, false);
			}

			//		traits.setTransitionValue(1, 3, .7, false);
			if(traits.valid_CoVar()){
				traits.get_cholesky();
			}
			
			utilityNumber = new MesquiteNumber();
		}

		/** returns whether or not size can be changed externally*/
		public boolean canChangeSize() {
			return true;
		}
		/** returns whether or not model is default; if so, then doesn't need to be written to file*/
		public boolean isBuiltIn() {
			return false;
		}
		//untitled.nex
		/** returns name of model class (e.g. "CoVarMatrix")*/
		public String getNEXUSClassName() {
			return "CoVarMatrix";
		}
		
		public Object doCommand(String commandName, String arguments, CommandChecker checker) {
			if (checker.compare(this.getClass(), "Displays the cholesky for the current covariance matrix", "[number of traits]", commandName, "get_cholesky")) {
				traits.get_cholesky();
				window.getTable().redrawMatrix();
					
			}
			else if (checker.compare(this.getClass(), "Sets the number of traits in the covariance matrix", "[number of traits]", commandName, "setTraitNum")) {
				MesquiteInteger io = new MesquiteInteger(0);
				int num_of_traits = MesquiteInteger.fromString(arguments, io);
				int previous = traits.getTraitNum();
				int new_trait_num = 0; 
				
				if (!MesquiteInteger.isCombinable(num_of_traits)){ 
					new_trait_num = MesquiteInteger.queryInteger(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Set number of taits", "Number of traits in the model", traits.getTraitNum());
				}
				if (new_trait_num != previous && !(!MesquiteInteger.isCombinable(new_trait_num))) {
					window.setTraitNum(new_trait_num);
				}
			}
			else if (checker.compare(this.getClass(), "Sets the number of traits in the covariance matrix", "[number of traits]", commandName, "setToList")) {
				setToList();
			}
			if (checker.compare(this.getClass(), "Sets model to control trait", "[number of traits]", commandName, "setModel")) {
				ModelCompatibilityInfo mci = new ModelCompatibilityInfo(ProbabilityCorContCharModel.class, ContinuousState.class);
				CTSubModelCurator newCorTraitTask;
				//(dutyClass, explanation)
				//newCorTraitTask=(CT)ProbabilityContCharModel.chooseExistingCharacterModel(this.show(), mci, "please chose a model.");
				newCorTraitTask=(CTSubModelCurator)MesquiteTrunk.mesquiteTrunk.hireEmployee(CTSubModelCurator.class, "Please Chose a new submodel:");
				newCorTraitTask.doCommand("newModel", "Untitled", CommandChecker.defaultChecker);
				System.out.println("lame!");
				//System.out.print(newCorTraitTask.getName());
			}
			else if (checker.compare(this.getClass(), "Run a simulation and show statistical summery", "[number of traits]", commandName, "runSim")) {
				System.out.println("detected menu option.");

				MesquiteInteger buttonPressed = new MesquiteInteger(1);
				
				ExtensibleDialog makeFileDialog = new ExtensibleDialog(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "PDSIM: Simulation run",buttonPressed);
				SingleLineTextField fileNameField = makeFileDialog.addTextField("File Name:", "Noname", 30);
				IntegerField numOfRunsField = makeFileDialog.addIntegerField("Number of Runs:", 10, 6);
				DoubleField stepSizeField = makeFileDialog.addDoubleField("Step_size (enter 0 when not using bounding):", .1, 6);
				RadioButtons rb = makeFileDialog.addRadioButtons(new String[]{"Make Sim File","Make Csv File"},0);
				
				makeFileDialog.completeAndShowDialog("OK",null,null,"OK");
				
				int numOfRuns=numOfRunsField.getValue();
				String fileName=fileNameField.getText();
				sim=CoVarSimulations.run_sim(this, numOfRuns, fileName, rb.getValue(), stepSizeField.getValue());}
			//else if (checker.compare(this.getClass(), "Add new CorTrait Sub Model", "?", commandName, "addModel")){
			//}*/
			else
				return  super.doCommand(commandName, arguments, checker);
			return null;
			
		}

		public String getStateSymbol(int state){
			return Integer.toString(state);
		}

		/** Returns correlatio between two state. */
		public MesquiteNumber getTransitionValue (int x, int y, MesquiteNumber result){
			if (result ==null)
				result = new MesquiteNumber();
//			if (beginState>=numStates || endState>=numStates)
//				result.setToInfinite();
//			else
				traits.getTransitionValue(x, y, result, false);
			return result;
		}
		/** Currently simply returns true. */
		public boolean isSymmetrical(){
//			for (int i=0; i< numStates; i++){
//				for (int j=i+1; j< numStates; j++)
//					if (!costs.equal(i*maxNumStates+j, j*maxNumStates+i))
//						return false;
//			}
			return true;
		}
		
		/** Sets correlatio between two state.*/
		public void setTransitionValue (int beginState, int endState, MesquiteNumber result, boolean notify){
			//if (beginState>=numStates || endState>=numStates)
			//	return;
			traits.setTransitionValue(beginState, endState, result, false);
			if (notify){
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN),CharacterModel.class, true);
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN),CharacterModel.class, false);
			}
		}
		
		public void setWindow(CoVarTableWindow window){
			this.window = window;
		}

		public String getExplanation(){	
			return "Covariance between coloum trait and row trait";
		}

		public String getLongModelName( int x){
			//String str = "";
			if (x<num_of_traits){
				if (CorTraitTask[x]!=null){
					return ""+(CorTraitTask[x].getName()); //getmodelName()); //.getName(); //getNameForMenuItem(); //getName(); //.getModelName();
				}
				else return "unassiagned";
			}
			else return "Programer Error";
			//return str;
		}
		public void ModelNamePopup(int x){
			System.out.println("here");
			ProbabilityCorContCharModel charSimulatorTask = chooseModel();
			setModel(charSimulatorTask, x);		
		}
		public void setModel(ProbabilityCorContCharModel CorTraitTask, int x){
			this.CorTraitTask[x]=CorTraitTask;
		}

		private ProbabilityCorContCharModel chooseModel(){
			System.out.println("we");
			ProbabilityCorContCharModel m = null;
			System.out.println("are");
			//System.out.println(;
			System.out.println("boys.");
			//getProject().
			//ProbabilityCorContCharModel.findCurators(this.getProject(), ProbabilityCorContCharModel.class);
			if (MesquiteThread.isScripting())
				return (ProbabilityCorContCharModel)getProject().getCharacterModel(new SimModelCompatInfo(ProbabilityCorContCharModel.class, ContinuousState.class),0);
			else
				return (ProbabilityCorContCharModel)CharacterModel.chooseExistingCharacterModel(module, new SimModelCompatInfo(ProbabilityCorContCharModel.class, ContinuousState.class), "Choose probability model for simulations of continuous character evolution");
			}
			public String[] getModelVectorNames(){
				String[] names=new String[num_of_traits+1];
				for(int x=0;x<num_of_traits;x++){
					names[x]=CorTraitTask[x].getName();
				}
				names[num_of_traits]=null;
				return names;
			}

		/*public void pushModel(ProbabilityCorContCharModel model){
			ProbabilityCorContCharModel tempCorTraitTask[]=new ProbabilityCorContCharModel[CTTask_size+1];
			for (int x=0;x<num_of_traits;x++){
				tempCorTraitTask[x]=CorTraitTask[x];
			}
			tempCorTraitTask[CTTask_size]=model;
			CTTask_size++;
			CorTraitTask=new ProbabilityCorContCharModel[CTTask_size];
			for (int x=0;x<CTTask_size;x++){
				CorTraitTask[x]=tempCorTraitTask[x];
			}
		}*/
}

