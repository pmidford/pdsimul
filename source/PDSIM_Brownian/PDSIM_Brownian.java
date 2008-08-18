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
package mesquite.pdsim.PDSIM_Brownian;
/*~~  */


import java.util.*;

import mesquite.lib.ExtensibleDialog;
import mesquite.lib.RadioButtons;
import mesquite.lib.Tree;
import mesquite.lib.CommandChecker;
import mesquite.lib.EmployeeNeed;
import mesquite.lib.MesquiteDouble;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteWindow;
import mesquite.lib.ObjectContainer;
import mesquite.lib.ParseUtil;
import mesquite.lib.characters.CharacterModel;
import mesquite.lib.duties.*;
import mesquite.cont.lib.*;
import mesquite.pdsim.lib.*;


/* ======================================================================== */
public class PDSIM_Brownian extends WholeCharModelCurator implements CTSubModelCurator  {
	String name;
	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed
		 EmployeeNeed e = registerEmployeeNeed(WindowHolder.class, getName() + " needs assistance to hold a window" ,
				 "This is arranged automatically");
	}
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
 		return true;
	}

	MesquiteInteger pos = new MesquiteInteger();

	public Object doCommand(String commandName, String arguments, CommandChecker checker) {
		//System.out.println("command checker called");
		/*if (checker.compare(this.getClass(), "Edits the character model", "[name of character model]", commandName, "editModel")) {
			System.out.println("editing model");
			CharacterModel model = getProject().getCharacterModel(parser.getFirstToken(arguments));
			if (model !=null && model instanceof PDSIM_BrownianModel) {
				editModelNonModal(model, null);
			}
			return model;
		}*/
		if (checker.compare(this.getClass(), "Sets Model to default", "[name of character model]", commandName, "setDefault")) {
			System.out.println("seting defaults");
			CharacterModel model = getProject().getCharacterModel(parser.getFirstToken(arguments));
			if (model !=null && model instanceof PDSIM_BrownianModel) {
				PDSIM_BrownianModel modelToEdit=(PDSIM_BrownianModel)model;
				

				MContinuousDistribution testChar;
				CharMatrixSource charTask = (CharMatrixSource)this.hireEmployee(CharMatrixSource.class, "Matrix to test");
				testChar=(MContinuousDistribution)charTask.getMatrix(getProject().getTaxa(0), charTask.queryUserChoose(getProject().getTaxa(0), "Please chose matrix to test")); //getCcharTask.queryUserChoose(taxa, "Please chose a character Data");
				
				String[] str=new String [testChar.getNumChars()]; //getNumItems()];
				for (int x=0;x<str.length;x++){
					str[x]=testChar.getCharacterDistribution(x).getName();
				}
				ExtensibleDialog first=new ExtensibleDialog(this,"Choose a character");
				RadioButtons rb;
				rb=first.addRadioButtons(str, 0);
				first.completeAndShowDialog();
				
				modelToEdit.setDefaults(testChar.getTaxa(), testChar, rb.getValue());
				return model;
				
			}
			else {
				System.out.println("Passed bad model name"+arguments);
				return null;
			}
		}
		else{
			System.out.println("Checking super"+arguments);
			return super.doCommand(commandName, arguments, checker);
		}
		//return null;
	}
	
  	 public void projectEstablished(){
  		PDSIM_BrownianModel PDSIM_Brownian = new PDSIM_BrownianModel("PDSIM_Brownian default", ContinuousState.class);
  		PDSIM_Brownian.settrait_var(1.0);
  		PDSIM_Brownian.setBuiltIn(true);
  		PDSIM_Brownian.addToFile(null, getProject(), null);
  		ContinuousData.registerDefaultModel("Likelihood", "PDSIM_Brownian default");
    	super.projectEstablished();
  	 }
	/*.................................................................................................................*/
  	public MesquiteModule editModelNonModal(CharacterModel model, ObjectContainer w){
		if (model!=null && model instanceof PDSIM_BrownianModel) {
			PDSIM_BrownianModel modelToEdit =  ((PDSIM_BrownianModel)model);
			
			double trait_var = modelToEdit.gettrait_var();
			double mean_var = modelToEdit.getmean_var();
			double meanTipState = modelToEdit.getmeanTipState();
			double root_state = modelToEdit.getroot_state();
			double lower_bound = modelToEdit.getlower_bound();
			double upper_bound = modelToEdit.getupper_bound();
			
			String bound_type = modelToEdit.getbound_type();
			
			boolean bounding = modelToEdit.getbounding();
			boolean punc = modelToEdit.getpunctuated();
			boolean use_length = modelToEdit.getspeciation();
				
			List bounding_list = modelToEdit.getbounding_list();
			
			MesquiteModule windowServer = hireNamedEmployee(WindowHolder.class, "#WindowBabysitter");
				if (windowServer == null)
			return null;
				
			CorTraitCurWindow cw=new CorTraitCurWindow(windowServer, "Edit model", "Model \"" + modelToEdit.getName() + "\"");
			
			cw.makeCommandField("Variance of state at tips::", "traitvar",modelToEdit, trait_var,makeCommand("settrait_var", modelToEdit));
			cw.makeCommandFieldln("Root state:", "rootstate",modelToEdit, root_state,makeCommand("setroot_state", modelToEdit));
			cw.makeCommandField("Variance of mean-velocity:","meanvar",modelToEdit, mean_var,makeCommand("setmean_var", modelToEdit));
			cw.makeCommandFieldln("Mean state at tips :","meanTipState",modelToEdit, meanTipState,makeCommand("setmeanTipState", modelToEdit));
			cw.makeCommandCheckBox("Ignore branch length: ",use_length,makeCommand("toggel_branchlength",modelToEdit));
			cw.makeCommandCheckBox("Punctuated: ",punc,makeCommand("toggel_punctuated",modelToEdit));
			cw.makeCommandCheckBoxln("Use bounding: ",bounding,makeCommand("toggel_bounds",modelToEdit));

			cw.makeCommandField("Lower bound :","lowerbound",modelToEdit, lower_bound,makeCommand("setlower_bound", modelToEdit));
			cw.makeCommandFieldln("Upper bound :","upperbound",modelToEdit, upper_bound,makeCommand("setupper_bound", modelToEdit));
			cw.makeCommandComboBox("Bounding type: ",bound_type,bounding_list,makeCommand("setbound_type",modelToEdit));
			cw.makeCommandButton("Set to defaults",makeCommand("setDefault",this), "'"+modelToEdit.getName()+"'"+super.getModelNumber(modelToEdit));
			
			windowServer.makeMenu("PDSIM_Brownian");

			MesquiteWindow.centerWindow(cw);
			if (w!=null)
				w.setObject(cw);
			return windowServer;
		}
		return null;
   	}
  	/*public Object doCommand(String commandName, String arguments, CommandChecker checker){

  	}*/
   	
   	
   	
			/*if (modal){
				a = MesquiteDouble.queryDouble(containerOfModule(), "Jukes Cantor trait_var", "Set trait_var of Jukes Cantor model", a);
				modelToEdit.settrait_var(a);
			}
			else */

	/*.................................................................................................................*/
	public boolean curatesModelClass(Class modelClass){
		return PDSIM_BrownianModel.class.isAssignableFrom(modelClass);
	}
	/*.................................................................................................................*/
	public String getNameOfModelClass() {
		return "PDSIM_Brownian";
	}
	/*.....................getModelName............................................................................................*/
	public String getNEXUSNameOfModelClass() {
		return "PDSIM_Brownian";
	}
	/*.................................................................................................................*/
	public Class getModelClass() {
		return PDSIM_BrownianModel.class;
	}
	/*.................................................................................................................*/
   	public CharacterModel makeNewModel(String name) {
   		this.name=name;
   		PDSIM_BrownianModel PDSIM_Brownian = new PDSIM_BrownianModel(name, ContinuousState.class);
   		//PDSIM_Brownian.settrait_var(1.0);
   		System.out.println("HI!");
   		PDSIM_Brownian.fromString(PDSIM_Brownian.getNexusSpecification(), new MesquiteInteger(0), 0);
       	return PDSIM_Brownian;
   	}
	/*.................................................................................................................*/
 	public CharacterModel readCharacterModel(String name, MesquiteInteger stringPos, String description, int format) {
   		PDSIM_BrownianModel pDSIM_Brownian = new PDSIM_BrownianModel(name, ContinuousState.class);
   		//String varname,value;
		//varname=ParseUtil.getToken(description, stringPos);
   		//value=ParseUtil.getToken(description, stringPos);
   		System.out.println("help!");
   		//System.out.println(value);  		
   			pDSIM_Brownian.fromString(description, stringPos, format);
   	 	//	str=ParseUtil.getToken(description, stringPos);
   	   	 //  	System.out.println(str);
   		//}
  		return pDSIM_Brownian;
   	}
	/*..............................n...................................................................................*/
   	 public String getName() {
		return "PDSIM_BrownianModel";
   	 }
   	public String getModelName(){
   		return name;
   	}
	/*.................................................................................................................*/
    	 public String getNameForMenuItem() {
		return "PDSIM_Brownian...";
   	 }
   	 
	/*.................................................................................................................*/
   	 
 	/** returns an explanation of what the module does.*/
 	public String getExplanation() {
 		return "Initializes PDSIM_Brownian motion character model for likelihood and other probability calculations." ;
   	 }

}

