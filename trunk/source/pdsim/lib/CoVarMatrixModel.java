package mesquite.pdsim.lib;

import mesquite.cont.lib.ContinuousState;
import mesquite.pdsim.PDAnova.*;

import mesquite.lib.*; //RadioButtons;
import mesquite.lib.characters.CharacterModel;
import mesquite.stochchar.lib.ProbabilityContCharModel;
import mesquite.stochchar.lib.SimModelCompatInfo;
import mesquite.cont.lib.MContinuousDistribution;
import java.util.Vector;
import java.awt.Checkbox;

public class CoVarMatrixModel  extends ProbabilityContCharModel implements CorTraitModel, DataSource {
		ProbabilityCorContCharModel[] CorTraitTask={null,null,null};

		MesquiteModule module;
		
		PDSIMDataWindow window = null;
		boolean showCholesky=false;

		int num_of_traits;

		MesquiteNumber utilityNumber;
		boolean failed=false;
		boolean initialized;
		

		public CorTrait traits;
		
		Tree tree;
		Taxa taxa;
		MContinuousDistribution charData=null;


		public boolean isEditable(){
			return true;
		}
		public boolean isEditableDiagonal(){
			return false;
		}
		
		public void setTree(Tree tree){
			this.tree=tree;
		}	
		
		public void setCharData(MContinuousDistribution charData){
			this.charData=charData;
		}	
		
		public void setTaxa (Taxa taxa){
			this.taxa=taxa;
		}
		public int getNumTaxa(){
			return taxa.getNumTaxa();
		}
		public boolean getEditCancel(){
			return failed;
		}

		
				public double evolveState (double beginState, Tree tree, int node){
					if(this.isFullySpecified()){
						return CorTraitTask[0].evolveState(beginState, tree, node);
					}
					else return MesquiteDouble.unassigned;
				}
			 	/*.................................................................................................................*/
				/** Returns (possibly by randomly generating) according to model an ancestral state for root of tree*/
				public double getRootState (Tree tree){
					return 0;  //todo: stochastic?
				}			
			 	/*.................................................................................................................*/
				/**Return Parameters in user readable form (sort of);*/
				public String getParameters() {
					String parm=new String();
					parm="tree: " + tree.getName()+" taxa: "+taxa.getName()+" charData "+charData.getName()+" CorTraitTasks: ";
					for(int x=0;x<num_of_traits;x++){
						parm+=""+CorTraitTask[x].getName()+" ";// cellValues; 
					}
					parm+="cellValues: ";
					for(int x=0;x<num_of_traits;x++){
						for(int y=0;y<num_of_traits;y++){
							parm+=this.getCellValue(x, y, null)+" ";
						}
					}
					return parm;
				}
			 	/*.................................................................................................................*/
			 	/*.................................................................................................................*/
			 	/** Performs command (for Commandable interface) */
				public CharacterModel cloneModelWithMotherLink(CharacterModel formerClone){
					CoVarMatrixModel bmm = new CoVarMatrixModel(name, getProject(), module, tree, taxa, charData);// treeTask, charTask);
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
					if(tree != null && charData != null){
						boolean b=true;
						for(int x=0;x<num_of_traits;x++){
							if(CorTraitTask[x]==null)
								b=false;
						}
						return b;
					}
					else return false;// != MesquiteDouble.unassigned;
				}
				public void setSeed(long seed){
					//trait.setSeed(seed);
				}
				
				public long getSeed(){
					return traits.getSeed();
				}
		
		public CoVarMatrixModel (String name, Class dataClass) {
			super(name, dataClass);
		//	getDefaults();
		}
		
		private void getDefaults(){
		//	charData.getNumChars();

			System.out.println("Meep?");
//			taxa=project.getTaxa(0);
			int size=taxa.getNumTaxa();
			Vector Group1= new Vector();
			Vector Group2= new Vector();

			System.out.println("GRR!");
			for (int y=0; y<num_of_traits;y++){
				
				for(int z=y;z<num_of_traits;z++){
					Group1.clear();
					Group2.clear();
					for(int x=0;x<size;x++){
						Group1.add(new MesquiteDouble(charData.getState(y, x, 0)));
//						System.out.println(group1[x]);
						Group2.add(new MesquiteDouble(charData.getState(z, x, 0)));
//						System.out.println(group2[x]);
					}
					double cor;
					cor=PDAnova.getCor(Group1, Group2);
		//			System.out.println(""+cor);
					MesquiteNumber n=new MesquiteNumber();
					n.setValue(cor);
					setTransitionValue(y,z,n,true);
					setTransitionValue(z,y,n,true);
				}
			}
		//	System.out.println("where am I now?");
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
	//		System.out.println("Heading:"+str);
			for(int x=0;x<num_of_traits;x++){
				str=ParseUtil.getToken(description, stringPos); //eating token
				System.out.println(x+":"+str);
				//ProbabilityCorContCharModel temp=null;
				//MesquiteTrunk.mesquiteTrunk.getProject().getCharacterModel("Ornstein_Uhlenbeck default");
				System.out.println("yup.");
				//System.out.println(temp.getName());
				ModelList+=" "+"'"+str+"'";
			}
	//		System.out.println("hi3");
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
	//		System.out.println("hi");
			specs="numoftraits ";
			specs+=num_of_traits;
	//		System.out.println("set 1");
			specs+=" CorTraitTasks ";
			for (int x=0;x<num_of_traits;x++){
				specs+="'"+CorTraitTask[x].getName()+"'"+" ";
				//getProject().getCharacterModel(new SimModelCompatInfo(ProbabilityCorContCharModel.class, ContinuousState.class),0);
			}
			System.out.println("set 2");
			for(int x=0;x<num_of_traits;x++){
				for(int y=0;y<num_of_traits;y++){
					specs+=traits.getCorCell(x, y)+" ";				
				}
			}
	//		System.out.println("set 3");
			return specs;
		}
		public CoVarMatrixModel (String name, MesquiteProject project, MesquiteModule module, Tree tree, Taxa taxa, MContinuousDistribution charData) {
			super(name, ContinuousState.class);
		
			this.module = module;
			this.tree=tree;
			this.taxa=taxa;
			this.charData=charData;

			if(tree==null || charData==null || taxa==null){
				ExtensibleDialog failDailog=new ExtensibleDialog(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "FAIL!");
				if (tree==null) failDailog.addLargeOrSmallTextLabel("No tree found");
				if (charData==null) failDailog.addLargeOrSmallTextLabel("No character matrix found");
				if (taxa==null) failDailog.addLargeOrSmallTextLabel("No taxa found");
				failDailog.completeAndShowDialog();
				failed=true;
			}
			else {
				num_of_traits=charData.getNumChars(); //charTask.getNumberOfCharacters(taxa);		
				traits = new CorTrait (num_of_traits);
				CorTraitTask=new ProbabilityCorContCharModel[num_of_traits];
				for(int x=0;x<num_of_traits;x++){
					CorTraitTask[x]=null;
				}
				traits.setTraitNum(num_of_traits);
				getDefaults();
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
				utilityNumber = new MesquiteNumber();
				failed=false;
			}
			if (module==null){
				ExtensibleDialog failDailog=new ExtensibleDialog(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "FAIL!");
				failDailog.addLargeOrSmallTextLabel("a null module was passed durring initilazation. Behavoir may be unstable.");
				failDailog.completeAndShowDialog();
				failed=true;
			}
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
			if (checker.compare(this.getClass(), "Displays the cholesky for the current covariance matrix", "[number of traits]", commandName, "toggelCholesky")) {
				traits.get_cholesky();
				if(showCholesky)
					showCholesky=false;
				else showCholesky=true;
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN), DataSource.class, true);
				for(int x=0;x<num_of_traits;x++){
					for(int y=0;y<num_of_traits;y++){	
						System.out.print(""+traits.getCholCell(x, y));
					}
					System.out.println();					
				}
				
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
				//	window.setTraitNum(new_trait_num);
				}
			}
			else if (checker.compare(this.getClass(), "Sets the models to NEXUS list", "[error]", commandName, "setToList")) {
				setToList();
			}
			if (checker.compare(this.getClass(), "Sets model to control trait", "[number of trait to set]", commandName, "setModel")) {
				//ModelCompatibilityInfo mci = new ModelCompatibilityInfo(ProbabilityCorContCharModel.class, ContinuousState.class);
				CTSubModelCurator newCorTraitTask;
				//(dutyClass, explanation)
				//newCorTraitTask=(CT)ProbabilityContCharModel.chooseExistingCharacterModel(this.show(), mci, "please chose a model.");
				newCorTraitTask=(CTSubModelCurator)MesquiteTrunk.mesquiteTrunk.hireEmployee(CTSubModelCurator.class, "Please Chose a Model:");
				newCorTraitTask.doCommand("newModel", "Untitled", CommandChecker.defaultChecker);
				
			}
			else
				return  super.doCommand(commandName, arguments, checker);
			return null;
			//if(newCorTraitTask.)
			//System.out.println("lame!");
			//System.out.print(newCorTraitTask.getName());

		}

		public String getStateSymbol(int state){
			return Integer.toString(state);
		}

		/** Returns correlatio between two state. */
		/*public MesquiteNumber getTransitionValue (int x, int y, MesquiteNumber result){
			if (result ==null)
				result = new MesquiteNumber();
//			if (beginState>=numStates || endState>=numStates)
//				result.setToInfinite();
//			else
			traits.getTransitionValue(x, y, result, false);
			return result;
		}*/
		/** Currently simply returns true. */
		public boolean isSymmetrical(){
//			for (int i=0; i< numStates; i++){
//				for (int j=i+1; j< numStates; j++)
//					if (!costs.equal(i*maxNumStates+j, j*maxNumStates+i))
//						return false;f//			}
			return true;
		}
		
		public boolean failed(){
			return failed;
		}
		
		/** Sets correlation between two state.*/
		public void setTransitionValue (int beginState, int endState, MesquiteNumber result, boolean notify){
			//if (beginState>=numStates || endState>=numStates)
			//	return;
			traits.setTransitionValue(beginState, endState, result, false);
			if (notify){
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN),CharacterModel.class, true);
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN),CharacterModel.class, false);
			}
		}
		
		public void setWindow(PDSIMDataWindow window){
			this.window = window;
		}

		public String getExplanation(){	
			return "Covariance between coloum trait and row trait";
		}

		/*public String getLongModelName( int x){
		}*/

		public String getTraitName( int x){
			//String[] names = new String[num_of_traits];
			//names=charData.getStrings();
			if (x<num_of_traits){
				return charData.getCharacterDistribution(x).getName(); //getItemName(x); //getCharacterName(x);
			}
			else return "Programer Error";
		}
		public void rowTouched (int x){
			ModelNamePopup(x);
		}
		
		public void ModelNamePopup(int x){
			ProbabilityCorContCharModel charSimulatorTask = chooseModel();
			setModel(charSimulatorTask, x);
		
			ExtensibleDialog PopUp=new ExtensibleDialog(module, name);
			if(charSimulatorTask.isBuiltIn()){
				PopUp.addLabel("Warning: Parameters for default (Bultin) models differ from default parameters for user models.");
				PopUp.completeAndShowDialog();
			}
			else{
				Checkbox defaults;
				defaults=PopUp.addCheckBox("Set Model to defaults for this trait?", false);
				PopUp.completeAndShowDialog();
				if (defaults.getState())
					CorTraitTask[x].setDefaults(taxa, charData, x);
			}			
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
			public void setCellValue(int row, int column, MesquiteNumber i, boolean notify){
					//if (beginState>=numStates || endState>=numStates)
					//	return;
				traits.setTransitionValue(row, column, i, false);
				if (notify){
					notifyListeners(this, new Notification(MesquiteListener.UNKNOWN),CharacterModel.class, true);
					notifyListeners(this, new Notification(MesquiteListener.UNKNOWN),CharacterModel.class, false);
				}
			}
			public String getRowName(int row){
				if (row<num_of_traits){
					if (CorTraitTask[row]!=null){
						return "("+charData.getCharacterDistribution(row).getName()+") "+(CorTraitTask[row].getName()); //getmodelName()); //.getName(); //getNameForMenuItem(); //getName(); //.getModelName();
					}
					else return "unassiagned";
				}
				else return "Programer Error";
			}
			public String getColumnName(int column){
				if (column<num_of_traits){
					return charData.getCharacterDistribution(column).getName();
				}
				else return "Programer Error";				
			}			
			public MesquiteNumber getCellValue(int row, int column, MesquiteNumber i){
				if (!showCholesky)
					return new MesquiteNumber(traits.getCorCell(row, column)); //getTransitionValue(row, column, i, false));
				else return new MesquiteNumber(traits.getCholCell(row, column));
			}
			public int getNumRows(){
				return num_of_traits;
			}
			public int getNumColumns(){
				return num_of_traits;
			}
			public String getRowHeader(){
				return "(Trait) Model Name";
			}
			public String getColumnHeader(){
				return "Trait Name";
			}
			public Tree getTree(){
				return tree;
			}
			public Taxa getTaxa(){
				return taxa;
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

