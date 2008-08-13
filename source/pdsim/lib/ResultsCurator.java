package mesquite.pdsim.lib;

//import mesquite.pdsim.lib;
import java.util.Vector;

import mesquite.lib.CommandChecker;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteString;
import mesquite.lib.MesquiteThread;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.ObjectContainer;
import mesquite.lib.ParseUtil;
import mesquite.lib.Snapshot;
import mesquite.lib.characters.CharacterModel;
import mesquite.lib.duties.CharModelCurator;
import mesquite.lib.MesquiteProject;
import mesquite.lib.ListableVector;
import mesquite.lib.Listable;


public abstract class ResultsCurator extends MesquiteModule implements DataCurator {

	Vector resultsToEdit= new Vector();  //provided to keep track of modules being edited (management methods also provided)
	Vector windowServers= new Vector();//provided in case window holder modules used

   	 public Class getDutyClass() {
   	 	return ResultsCurator.class;
   	 }
 	public String getDutyName() {
 		return "Result Curator";
   	 }
 	public String getName(){
 		return "moop";
 	}
   	
	/**Edit model.  Editing must be done in a modal dialog box becuase the results are needed immediately.*
   	public abstract void editModelModal(CharacterModel model);  //NOT YET IMPLEMENTED
	*/
	public abstract String getNameOfResultClass();
	public String getNEXUSNameOfResultClass(){
		return "ERROR!!";
	}
//	public abstract String getNEXUSCommand();
//	public abstract boolean curatesModelClass(Class modelClass);
	public abstract Class getResultClass();
	/**Make new model and return (don't add to file or show to edit)*/
   	public abstract  Results makeNewResults(String name);
   	public abstract  Results readResults(String name, MesquiteInteger stringPos, String description, int format);
   	public boolean isSubstantive(){
   		return false;  
   	}
	
	public void endJob(){
			for (int i=0; i<resultsToEdit.size(); i++) {
	 			Results results = getResult(i);
				if (results!=null)
					results.removeListener(this);
			}
			super.endJob();
	}
	/*.................................................................................................................*/
	/** returns whether this module is requesting to appear as a primary choice */
   	public boolean requestPrimaryChoice(){
   		return true;  
   	}
	/*.................................................................................................................*/
   	protected MesquiteWindow getWindow(int i){
 		if (i>=0 && i< windowServers.size()) {
 			Object obj = windowServers.elementAt(i);
 			if (obj instanceof MesquiteModule)
 				return (MesquiteWindow)((MesquiteModule)windowServers.elementAt(i)).getModuleWindow();
 		}
 		return null;
   	}
   	protected MesquiteModule getWindowHolder(int i){
 		if (i>=0 && i< windowServers.size()) {
 			Object obj = windowServers.elementAt(i);
 			if (obj instanceof MesquiteModule)
 				return (MesquiteModule)windowServers.elementAt(i);
 		}
 		return null;
   	}
   	protected Results getResult(int i){
 		if (i>=0 && i< resultsToEdit.size()) {
 			Object obj = resultsToEdit.elementAt(i);
 			if (obj instanceof Results)
 				return (Results)obj;
 		}
 		return null;
   	}
   	protected int getResultNumber(Results result){
 		if(resultsToEdit.contains(result)){
 			System.out.println("Existing result found.");
 			//.indexOf(result);
 		}
 		else
 			System.out.println("No existing result found.");
 		return resultsToEdit.indexOf(result);
   	}
	/*.................................................................................................................*/
 	public void employeeQuit(MesquiteModule m){
 		int i = windowServers.indexOf(m);
 		if (i>=0) {
 			Results result = getResult(i);
			if (result!=null) {
				result.removeListener(this);
	 			resultsToEdit.removeElement(result);
			}
			windowServers.removeElement(m);
 		}
 	}
	/** passes which object was disposed*/
	public void disposing(Object obj){
		if (obj instanceof Results){
	 		int i = resultsToEdit.indexOf(obj);
	 		if (i>=0) {
	 			fireEmployee(getWindowHolder(i));
	 			windowServers.removeElement(obj);
	 			Results m = getResult(i);
				if (m!=null)
					m.removeListener(this);
	 			resultsToEdit.removeElementAt(i);
	 		}
		}
	}
	/** Asks whether it's ok to delete the object as far as the listener is concerned (e.g., is it in use?)*/
	public boolean okToDispose(Object obj, int queryUser){
		return true;
	}
	/*.................................................................................................................*/
  	 public void disposeResult(Results result) {
		getProject().removeFileElement(result);//must remove first, before disposing
		result.dispose();
	}
	/*.................................................................................................................*/
  	 public Snapshot getSnapshot(MesquiteFile file) {
   	 	if (resultsToEdit.size() ==0)
   	 		return null;
   	 	Snapshot temp = new Snapshot();
   	 	for (int i=0; i<resultsToEdit.size(); i++) {
   	 		temp.addLine("editResult " + ParseUtil.tokenize(getResult(i).getName()), getWindowHolder(i));
   	 	}
  	 	return temp;
  	 }
  	 /*...*/
  	 public Results getResults(MesquiteProject project, String name){
  		 ListableVector otherElements=project.getOtherElements();
  		 Results[] list;
  		 list=(Results[])otherElements.elementsOfClass(Results.class);
  		 for(int x=0;x<list.length;x++){
  			 if(name.equalsIgnoreCase(list[x].getName()))
  				 return list[x];
  		 }
  		 return null;
  	 }
	/*.................................................................................................................*/
    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {
    	 	if (checker.compare(this.getClass(), "Open Data in window", "[name of character model]", commandName, "openResults")) {
			Results result = getResults(getProject(), parser.getFirstToken(arguments));
			if (result !=null) {
				return showEditor(result);
			}
			return null;
		}
		else if (checker.compare(this.getClass(), "Generate new Data", "[name of character model]", commandName, "newResults")) {
			System.out.println("156");
			System.out.println("New "+getName());
			//System.out.println("Name of new "+getName()+ ":");
			//System.out.println(""+getProject().getName());
			//System.out.println(""+getProject().getOtherElements().getName()); //.getUniqueName(getName());
			
			String name = MesquiteString.queryShortString(containerOfModule(), "New "+getName(), "Name of new "+getName()+":", "TODO:FIXME");//getProject().getCharacterModels().getUniqueName(getName()));
			System.out.println("157");
			if (name==null)
				return null;
			System.out.println("160");
			Results result = makeNewResults(name);
	   		result.addToFile(null, getProject(), null); //TODO:ok to add to homefile, or query user?
		 	//MesquiteModule[] curators = null;
			//curators = CharacterModel.findCurators(this, model.getClass());
			System.out.println("165");
	   		showEditor(result);
	   		if (result.getEditCancel()) {
	   			disposeResult(result);
	   			return null;
	   		}
			System.out.println("171");
	   		resetAllMenuBars();
			return result;
		}
    	 	else
 			return  super.doCommand(commandName, arguments, checker);
   	 }
	/**Edit model.  Editing to be done in non-modal window, which is to be returned in the ObjectContainer.
	Returns the module to whom commands are to be sent (in case WindowServer used).  Methods using window holder service should override this*/
   	public MesquiteModule editResultNonModal(Results result, ObjectContainer window){
		System.out.println("resultsCurator editResultNonModal called.");
   		return this;
   	}
   	
	/*.................................................................................................................*/
	/**Edit model.  Editing to be done in non-modal window.  Methods not using window holder service could override this.  Returns the module that owns the window produced.*/
  	public MesquiteModule showEditor(Results result){
			int i = getResultNumber(result);
			System.out.println("num of result"+i);
			System.out.println("TotalResults:"+resultsToEdit.size());
			
			if (i>=0) {
				MesquiteModule windowServer = getWindowHolder(i);
				if (windowServer == null) {
					alert("error: model found but window holder not found");
					return null;
				}
				MesquiteWindow win = windowServer.getModuleWindow();
				if (win!=null)
					win.show();
				return windowServer;
			}
			System.out.println("new editor");
			ObjectContainer w = new ObjectContainer();
			System.out.println("ObjectContainer w");
			MesquiteModule mod = editResultNonModal(result, w);
			System.out.println("editNonModal");
			MesquiteWindow window = (MesquiteWindow)w.getObject();
			System.out.println("window");
			this.resultsToEdit.addElement(result);
			System.out.println("addedElement?:"+resultsToEdit.size());
			result.addListener(this);
			System.out.println("addListener");
			
			if (mod !=null) {
				System.out.print("mod != null");
				windowServers.addElement(mod);
				if (window!=null){
					System.out.print(" && window != null");
					mod.setModuleWindow(window);
				}
				System.out.println();
			}
			if (window!=null){
				System.out.println("window != null");
			}
	 		mod.resetContainingMenuBar();
	 		resetContainingMenuBar();
	 		
			System.out.println("show window");
			
			if (window !=null){ 
				System.out.println("window != null");
				if (!MesquiteThread.isScripting())
					window.setVisible(true);
				resetAllWindowsMenus();
			}
			
			System.out.println("all done");
			
			return mod;
   	}
	/*.................................................................................................................*/
}
