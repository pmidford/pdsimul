package mesquite.pdsim.ManageResults;

import java.util.Enumeration;

//import mesquite.charMatrices.ManageAssumptionsBlock.AssumptionsBlock;
//import mesquite.assoc.ManageAssociations.AssociationsBlock;
import mesquite.assoc.lib.TaxaAssociation;
import mesquite.lib.CommandChecker;
import mesquite.lib.EmployeeNeed;
import mesquite.lib.FileElement;
import mesquite.lib.ListableVector;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteProject;
import mesquite.lib.MesquiteString;
import mesquite.lib.MesquiteSubmenuSpec;
import mesquite.lib.MesquiteThread;
import mesquite.lib.MesquiteTrunk;
import mesquite.lib.NexusBlock;
import mesquite.lib.NexusCommandTest;
import mesquite.lib.ParseUtil;
import mesquite.lib.Snapshot;
import mesquite.lib.StringLister;
import mesquite.lib.StringUtil;
import mesquite.lib.Taxa;
import mesquite.lib.characters.CharacterModel;
import mesquite.lib.characters.CharacterState;
import mesquite.lib.characters.CharacterSubmodel;
import mesquite.lib.characters.WholeCharacterModel;
import mesquite.lib.duties.CharModelCurator;
import mesquite.lib.duties.CharSubmodelCurator;
import mesquite.lib.duties.CuratorWithSettings;
import mesquite.lib.duties.EditingCurator;
import mesquite.lib.duties.ElementManager;
import mesquite.lib.duties.FileInit;
import mesquite.lib.duties.ManagerAssistant;
import mesquite.lib.duties.WholeCharModelCurator;
import mesquite.pdsim.lib.*;
import mesquite.pdsim.ManageSIMDATABlock.*;
import mesquite.lib.*;
import mesquite.lib.duties.FileElementManager;

public class ManageResults extends FileInit implements ElementManager {
		public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed
			EmployeeNeed e = registerEmployeeNeed(ResultsCurator.class, getName() + " needs curators to create and edit the various types of data.",
					"You can make new or edit previously made data using the menu items in the Results menu.");
			e.setAlternativeEmployerLabel("Editors for Results");
		}
		ResultsNamesLister resultNames;
		public Taxa passtaxa;
		/*.................................................................................................................*/
		public boolean startJob(String arguments, Object condition, boolean hiredByName) {
			hireAllEmployees(ResultsCurator.class);
			System.out.println("IT'S A ME!");
	 		return true;
		}
		public void elementsReordered(ListableVector v){
		}
	/*.................................................................................................................*/
	   	 public boolean isSubstantive(){
	   	 	return true;
	   	 }
	   	 public Class getDutyClass(){
	   		 return ManageResults.class;
	   	 }
		/*.................................................................................................................*/
	   	 public boolean isPrerelease(){
	   	 	return true;
	   	 }
		/*.................................................................................................................*/
		public MesquiteModule showElement(FileElement e){
			if (e instanceof Results){
				Results t = (Results)e;
				ResultsCurator curator = findReader(t.getClass());
				if (curator !=null)
			   		curator.showEditor(t);
			}
			return null;
		}
		/*.................................................................................................................*/
		public void deleteElement(FileElement e){
			if (e instanceof Results){
				Results t = (Results)e;
				t.doom();
				getProject().removeFileElement(t);//must remove first, before disposing
				t.dispose();
			}
		}
	 	public void fileReadIn(MesquiteFile f) {
	 		NexusBlock[] bs = getProject().getNexusBlocks(SIMDATAblock.class, f); //added Dec 01
			if (bs == null || bs.length ==0){ 
				SIMDATAblock sdb = new SIMDATAblock(f, this);
				//numBlocks++;
				addNEXUSBlock(sdb);
			}
			
		}
		public void endJob(){
			if (resultNames !=null)
				resultNames.dispose();
			resultNames = null;
			super.endJob();
		}
		public NexusBlock elementAdded(FileElement e){
			if (e==null || !(e instanceof TaxaAssociation))
				return null;
			TaxaAssociation assoc = (TaxaAssociation)e;
			NexusBlock nb = findNEXUSBlock(assoc);
			if (nb==null) {
			//	SIMDATABlock t = new SIMDATABlock(assoc.getFile(), this);
				//Taxa tax = assoc.getTaxa(0);
				//if (tax!=null && !tax.amIListening(this))
				//	tax.addListener(this);
				//tax = assoc.getTaxa(1);
				//if (tax!=null && !tax.amIListening(this))
				//	tax.addListener(this);
				//t.setAssociation(assoc);
			//	addNEXUSBlock(t);
				resetAllMenuBars();
				
				/*if (blocks.indexOf(t)<0)
					blocks.addElement(t, false);
				if (associationsVector.indexOf(assoc)<0)
					associationsVector.addElement(assoc, false);*/
			//	return t;
			}
			return nb;
		}
		public void elementDisposed(FileElement e){
			//nothing needs doing since separate reference not stored locally
		}
		public Class getElementClass(){
			return Results.class;
		}
		public void projectEstablished() {
			getFileCoordinator().addMenuItem(MesquiteTrunk.charactersMenu , "List Simulation Data", makeCommand("showResults",  this));
			resultNames = new ResultsNamesLister(getProject(), Results.class);
			getFileCoordinator().addSubmenu(MesquiteTrunk.charactersMenu, "Open Simulation Data", makeCommand("editResults", this), resultNames);
			MesquiteSubmenuSpec mset = getFileCoordinator().addSubmenu(MesquiteTrunk.charactersMenu,"Model Settings", makeCommand("modelSettings", this), WholeCharModelCurator.class);
			mset.setListableFilter(CuratorWithSettings.class);
			getFileCoordinator().addMenuItem(MesquiteTrunk.charactersMenu,"-", null);
			super.projectEstablished();
		}
		/*.................................................................................................................*/
	  	 public Snapshot getSnapshot(MesquiteFile file) { 
	   	 	Snapshot temp = new Snapshot();
			for (int i = 0; i<getNumberOfEmployees(); i++) {
				MesquiteModule e=(MesquiteModule)getEmployeeVector().elementAt(i);
				if (e instanceof ManagerAssistant && (e.getModuleWindow()!=null) && e.getModuleWindow().isVisible()) {
	  	 				temp.addLine("showModels ", e); 
	  	 		}
			}
	  	 	return temp;
	  	 }
		/*.................................................................................................................*/
	    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {
	    	 	if (checker.compare(this.getClass(), "Edits the simulation data", "[name of character model]", commandName, "editResults")) {
	    	 		String name = parser.getFirstToken(arguments);
	    	 		int num = MesquiteInteger.fromString(parser.getNextToken());
				Results result = null;
	    	 	if (MesquiteInteger.isCombinable(num)){
	    	 		System.out.println("Grabing existing result");
			 		result = (Results)getProject().getFileElement(Results.class,num);
			 		System.out.println(result.getName()+","+result.getNumColumns());
	    	 	}
				if (result == null){
	    	 		System.out.println("Grabing named results");
					result = getNamedResults(name);
				}
				if (result !=null) {
	    	 		System.out.println("results set up");
					ResultsCurator curator = findReader(result.getClass());
					if (curator==null)
						alert("Sorry, no curator module was found for that sort of data (category: " + result.getTypeName() + ")");
					else 
						curator.showEditor(result);
				}
				return result;
			}
	    	 	else if (checker.compare(this.getClass(), "Shows a list of the available simulation data", null, commandName, "showResults")) {
	    	 		//Check to see if already has lister for this
	    	 		boolean found = false;
				for (int i = 0; i<getNumberOfEmployees(); i++) {
					Object e=getEmployeeVector().elementAt(i);
					if (e instanceof ManagerAssistant)
						if (((ManagerAssistant)e).getName().equals("Result List")) {
							((ManagerAssistant)e).getModuleWindow().setVisible(true);
							return e;
						}
				}
				ManagerAssistant lister= (ManagerAssistant)hireNamedEmployee(ManagerAssistant.class, StringUtil.tokenize("Result List"));
	 			if (lister==null){
	 				alert("Sorry, no module was found to present a list of character models");
	 				return null;
	 			}
	 			lister.showListWindow(null);
	 			if (!MesquiteThread.isScripting() && lister.getModuleWindow()!=null){
	 				lister.getModuleWindow().setVisible(true);
	 				return lister;
	 			}
	    	 	else
	    	 		return  super.doCommand(commandName, arguments, checker);
	    	 }
			return null;
	   	 }
		/*.................................................................................................................*/
		public String getNexusCommands(MesquiteFile file, String blockName){ 
			if (blockName.equalsIgnoreCase("SIMDATA")) {
				String s= "";
				Enumeration enumeration = file.getProject().getCharacterModels().elements();
				while (enumeration.hasMoreElements()){   // write the submodels
					Object obj = enumeration.nextElement();
					CharacterModel cm = (CharacterModel)obj;
					if (!cm.isBuiltIn() && !("USERTYPE".equalsIgnoreCase(cm.getNEXUSCommand())) &&cm.getFile()==file && cm instanceof CharacterSubmodel) {
						s += "\t"+ cm.getNEXUSCommand() + " ";  
						s += StringUtil.tokenize(cm.getName()) + " (" ;  
						s += StringUtil.tokenize(cm.getNEXUSClassName()) + ") = " + StringUtil.lineEnding();
						s += "\t\t"+ cm.getNexusSpecification()+";" + StringUtil.lineEnding(); 
					}
					//else if (!cm.isBuiltIn() && cm.getFile()!=file)
					//	MesquiteMessage.println("Character model (" + cm.getName() + ") not in file");				
				}
				enumeration = file.getProject().getCharacterModels().elements();
				while (enumeration.hasMoreElements()){   // write everything else
					Object obj = enumeration.nextElement();
					CharacterModel cm = (CharacterModel)obj;
					if (!cm.isBuiltIn() && !("USERTYPE".equalsIgnoreCase(cm.getNEXUSCommand())) &&cm.getFile()==file && (!( cm instanceof CharacterSubmodel))) {
						s += "\t"+ cm.getNEXUSCommand() + " ";  
						s += StringUtil.tokenize(cm.getName()) + " (" ;  
						s += StringUtil.tokenize(cm.getNEXUSClassName()) + ") = " + StringUtil.lineEnding();
						s += "\t\t"+ cm.getNexusSpecification()+";" + StringUtil.lineEnding(); 
					}
					//else if (!cm.isBuiltIn() && cm.getFile()!=file)
					//	MesquiteMessage.println("Character model (" + cm.getName() + ") not in file");				
				}
				return s;
			}
			else if (blockName.equalsIgnoreCase("ASSUMPTIONS")) {
				System.out.println("writing assumptions");
				String s= "";
				int numOfElements=file.getProject().getNumberOfFileElements(Results.class);
				Listable[] enumeration = file.getProject().getFileElements(Results.class);
				for(int x=0;x<numOfElements;x++){
					System.out.println("1?");
					//Object obj = enumeration[x];
					Results r = (Results)enumeration[x];
					if ("USERTYPE".equalsIgnoreCase(r.getNEXUSCommand()) && r.getFile()==file) {
						s += "\t"+ r.getNEXUSCommand() + " ";  
						s += StringUtil.tokenize(r.getName()) + " (" ;  
						s += StringUtil.tokenize(r.getNEXUSClassName()) + ") = " + StringUtil.lineEnding();
						s += "\t\t"+ r.getNexusSpecification()+";" + StringUtil.lineEnding(); 
					}
					//else if (!cm.isBuiltIn() && cm.getFile()!=file)
					//	MesquiteMessage.println("Character model (" + cm.getName() + ") not in file.");				
				}
				return s;
			}
			return null;
		}
		public ResultsCurator findReader(Class resultType) {
			ResultsCurator readerTask=null;
			for (int i = 0; i<getNumberOfEmployees() && readerTask==null; i++) {
				Object e=getEmployeeVector().elementAt(i);
				if (e instanceof ResultsCurator)
					if (((ResultsCurator)e).getResultClass().isAssignableFrom(resultType)) {
						readerTask=(ResultsCurator)e;
					}
			}
			return readerTask;
		}
		public ResultsCurator findReader(String modelType) {
			ResultsCurator readerTask=null;
			for (int i = 0; i<getNumberOfEmployees() && readerTask==null; i++) {
				Object e=getEmployeeVector().elementAt(i);
				if (e instanceof ResultsCurator){
					System.out.println(((ResultsCurator)e).getNEXUSNameOfResultClass()+" "+modelType);
					if (((ResultsCurator)e).getNEXUSNameOfResultClass().equalsIgnoreCase(modelType)) {
						readerTask=(ResultsCurator)e;
						System.out.println("Found Reader!");
					}
					else System.out.println("not yet");
				}
			}
			return readerTask;
		}
		/*.................................................................................................................*/
		public boolean readNexusCommand(MesquiteFile file, NexusBlock nBlock, String blockName, String command, MesquiteString comment){ 
			System.out.println("checked read command");
			if (blockName.equalsIgnoreCase("ASSUMPTIONS") || blockName.equalsIgnoreCase("SIMDATA")) {
				System.out.println("here is the start");
				MesquiteInteger startCharT = new MesquiteInteger(0);
				int format;
				String commandName = ParseUtil.getToken(command, startCharT);
				if (commandName.equalsIgnoreCase("USERTYPE") || commandName.equalsIgnoreCase("RESULT")) {
					String token = ParseUtil.getToken(command, startCharT);
					String nameOfModel = (token); // name of model
					token = ParseUtil.getToken(command, startCharT); //parenthesis
					String nameOfResultClass = "";
					if ("(".equalsIgnoreCase(token) || commandName.equalsIgnoreCase("RESULT")) {
						nameOfResultClass = ParseUtil.getToken(command, startCharT); //name of model class
						token = ParseUtil.getToken(command, startCharT); //parenthesis
						token = ParseUtil.getToken(command, startCharT); //=
					}
					/*else {
						nameOfModelClass = "Stepmatrix";
						if (!"=".equalsIgnoreCase(token))
							token = ParseUtil.getToken(command, startCharT); //=
					}*/
					ResultsCurator readerTask = findReader(nameOfResultClass);
					if (readerTask!=null){
						Results result = readerTask.readResults(nameOfModel, startCharT, command, 0);
			   			if (result!=null) {
			   				result.addToFile(file, getProject(), this);
			   				if (comment !=null &&  !comment.isBlank()) {
			   					String s = comment.toString();
			   					result.setAnnotation(s.substring(1, s.length()), false);
			   				}
			   				return true;
			   			}
			   		}
			   		else {
			   			
						String specification = StringUtil.stripLeadingWhitespace(command.substring(startCharT.getValue(), command.length()-1)); //strip leading added 22 Dec 01
			   			ForeignResults model = new ForeignResults(nameOfModel, nameOfResultClass, commandName, specification);
			   			model.addToFile(file, getProject(), this);
			   			return true;
			   		}
				}
			}
			return false;
		}
		public ResultsCurator halffromString(String description, MesquiteInteger stringPos, int format){ 
			String token = ParseUtil.getToken(description, stringPos);
			System.out.println("1"+token);
			String nameOfModel = (token); // name of model
			token = ParseUtil.getToken(description, stringPos); //parenthesis
			System.out.println("2"+token);
			String nameOfResultClass = token;
			System.out.println(nameOfModel);
				if ("(".equalsIgnoreCase(token)) {
					nameOfResultClass = ParseUtil.getToken(description, stringPos); //name of model class
					token = ParseUtil.getToken(description, stringPos); //parenthesis
					System.out.println(nameOfResultClass);
					System.out.println("3"+token);
						//token = ParseUtil.getToken(description, stringPos); //=
					//System.out.println("4"+token);
				}
				ResultsCurator readerTask = findReader(nameOfResultClass);
				//readerTask.passtaxa=passtaxa;
				if (readerTask!=null){
					System.out.println("here is the Result");
					readerTask.makeempty(nameOfModel); //readResults(nameOfModel, stringPos, description, 0);
					return readerTask;
				}
				return null;
			}
		public void finish(ResultsCurator readerTask, CoVarMatrixModel model, String description, MesquiteInteger stringPos, int format){ 
			System.out.println("reader null?");
			Results result;
			if (readerTask!=null){
					System.out.println("finishing the result");
				//	result=readerTask.finish(model, nameOfModel, description, stringPos, format); //readResults(nameOfModel, stringPos, description, 0);	
				}
			}
		public boolean fromString(CoVarMatrixModel model, String description, MesquiteInteger stringPos, int format){ 
			String token = ParseUtil.getToken(description, stringPos);
			System.out.println("1"+token);
			String nameOfModel = (token); // name of model
			token = ParseUtil.getToken(description, stringPos); //parenthesis
			System.out.println("2"+token);
			String nameOfResultClass = token;
			System.out.println(nameOfModel);
				if ("(".equalsIgnoreCase(token)) {
					nameOfResultClass = ParseUtil.getToken(description, stringPos); //name of model class
					token = ParseUtil.getToken(description, stringPos); //parenthesis
					System.out.println(nameOfResultClass);
					System.out.println("3"+token);
						//token = ParseUtil.getToken(description, stringPos); //=
					//System.out.println("4"+token);
				}
				ResultsCurator readerTask = findReader(nameOfResultClass);
				readerTask.passtaxa=passtaxa;
				if (readerTask!=null){
					Results result = readerTask.finish(model,nameOfModel, description, stringPos, format); //readResults(nameOfModel, stringPos, description, 0);	
	   				//}readerTask.readResults(nameOfModel, stringPos, description, 0);
		   			if (result!=null) {
			   				//result.addToFile(file, getProject(), this);
			   				//if (comment !=null &&  !comment.isBlank()) {
			   				//	String s = comment.toString();
		   					//result=
			   				return true;
			   			}
			   		}
			   		/*else {
			   			
						String specification = StringUtil.stripLeadingWhitespace(command.substring(startCharT.getValue(), command.length()-1)); //strip leading added 22 Dec 01
			   			ForeignResults model = new ForeignResults(nameOfModel, nameOfResultClass, commandName, specification);
			   			model.addToFile(file, getProject(), this);
			   			return true;
			   		}*/
					return false;
		}
		/*.................................................................................................................*/
		public NexusCommandTest getNexusCommandTest(){ 
			return new ResultNexusCmdTest();
		}
		/*.................................................................................................................*/
	    	 public String getName() {
			return "Manage Simulation Data";
	   	 }
	   	 
		/*.................................................................................................................*/
	   	 
	 	/** returns an explanation of what the module does.*/
	 	public String getExplanation() {
	 		return "Manages simulation data." ;
	   	 }
	 	MesquiteProject project;
		
	 	public Results getNamedResults(String name){
	 		Listable[] names;
	 		int numOfElements;
	 		project=getProject();
	 		System.out.println(""+project.getName());
	 		System.out.println("NO!");
	 		names=getProject().getFileElements(Results.class);
	 		numOfElements=getProject().getNumberOfFileElements(Results.class);
	 		for(int i=0;i<numOfElements;i++){
	 			if(names[i].getName().equalsIgnoreCase(name))
	 				return (Results)getProject().getFileElement(Results.class, i);
	 		}
	 		return null;
	 	}
	 	public Results[] getAllResults(){
	 		Listable[] names;
	 		System.out.println("1");
	 		 		int numOfElements;
	 		// if (project==null)
	 		//;
	 		if(project==null) System.out.println("GR!");
	 		System.out.println(""+getProject().getName());
	 		names=getProject().getFileElements(Results.class);
	 		System.out.println("2");
	 		numOfElements=getProject().getNumberOfFileElements(Results.class);
	 		System.out.println("3");
	 		Results[] s=new Results[numOfElements];
	 		System.out.println("4");
	 		for(int i=0;i<numOfElements;i++){
		 		System.out.println(""+i);
	 			s[i]=(Results)getProject().getFileElement(Results.class, i);
	 		}
	 		return s;
	 	}
		/*.................................................................................................................*/
	   	 
	}
	/*======================================*/
	class ResultNexusCmdTest  extends NexusCommandTest{
		public boolean readsWritesCommand(String blockName, String commandName, String command){  //returns whether or not can deal with command
			System.out.println("checked data block");
			return ((blockName.equalsIgnoreCase("ASSUMPTIONS")||blockName.equalsIgnoreCase("SIMDATA")) && (commandName.equalsIgnoreCase("USERTYPE")|| commandName.equalsIgnoreCase("RESULT")));
		}
	}

	/*======================================*/
	class ResultsNamesLister implements StringLister{
		MesquiteProject proj;
		Class subclass;
		public ResultsNamesLister(MesquiteProject proj, Class subclass){
			this.proj = proj;
			this.subclass = subclass;
		}
		private boolean correctSubclass(Results c){
			if (subclass == null)
				return true;
			else return (subclass == Results.class || subclass.isAssignableFrom(c.getClass()));
		}
		public String[] getStrings() {
			int numResults = 0;
			for (int i=0; i<proj.getNumberOfFileElements(Results.class); i++){// getNum` //getNumModels(); i++) {
				if (correctSubclass((Results)proj.getFileElement(Results.class, i))) {
					numResults++;
				}
			}
			String[] result = new String[numResults];
			for (int i=0; i<numResults; i++) {
				result[i]= "test";
			}
			int resultNum=0;
			for (int i=0; i<proj.getNumberOfFileElements(Results.class); i++) {
				if (correctSubclass((Results)proj.getFileElement(Results.class, i))) {
					result[resultNum]=((Results)proj.getFileElement(Results.class, i)).getName();
					resultNum++;
				}
			}
			return result;
		}
		public void dispose(){
			proj = null;
		}
	}

	/*======================================*/
	class ForeignResults extends Results{
		String command;
		String commandName;
		String NEXUSClassName;
		public void fromString(String str, MesquiteInteger i, int x){
			Results res=null;
			//return res;
		}
		public String toString(){
			return "ERROR";
			//return res;
		}
		public ForeignResults(String name, String NEXUSClassName, String commandName, String command){
			super(name, CharacterState.class);
			this.command = command;
			this.commandName = commandName;
			this.NEXUSClassName = NEXUSClassName;
//			setBuiltIn(false);
		}
		public boolean isEditable(){
			return false;
		}
		public boolean isEditableDiagonal(){
			return false;
		}
		public String getNEXUSName(){
			return getName();
		}
		
		public String getNEXUSCommand() {
			return commandName;
		}
		public String getModelTypeName() {
			return "Unrecognized";
		}
		public String getNEXUSClassName(){
			return NEXUSClassName;
		}
		public String getParadigm(){
			return "unknown";
		}
		public String getNexusSpecification(){
			return command;
		}
		/** return an explanation of the model. */
		public String getExplanation (){
			return "A unidentified character model (no module available to read or process it)";
		}
		public String getColumnName(int num){
			return "Undefined";
		}
		public String getRowName(int num){
			return "Undefined";
		}
		public String getRowHeader(){
			return "Undefined";
		}
		public String getColumnHeader(){
			return "Undefined";
		}
		public int getNumRows(){
			return 0;
		}
		public int getNumColumns(){
			return 0;
		}
		public String getName(){
			return "Undefined";
		}
		public MesquiteNumber getCellValue(int row, int column, MesquiteNumber i){
			return new MesquiteNumber(MesquiteDouble.unassigned);
		}
		public void setCellValue(int row, int column, MesquiteNumber i, boolean b){
		}
		public void rowTouched(int row){
		}
		public boolean getEditCancel(){
			return false;
		}
	}
