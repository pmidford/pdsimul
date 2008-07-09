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
package mesquite.pdsim.Ornstein_Uhlenbeck;
/*~~  */

import java.util.*;
import java.util.List;
import java.awt.*;

import mesquite.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.duties.*;
import mesquite.cont.lib.*;
import mesquite.stochchar.lib.*;
import mesquite.pdsim.lib.*;

/* ======================================================================== */
public class Ornstein_Uhlenbeck extends WholeCharModelCurator implements CTSubModelCurator  {
	String name;
	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed
		 EmployeeNeed e = registerEmployeeNeed(WindowHolder.class, getName() + " needs assistance to hold a window" ,
				 "This is arranged automatically");
	}
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
 		return true;
	}

	public void projectEstablished(){
  		Ornstein_UhlenbeckModel Ornstein_Uhlenbeck = new Ornstein_UhlenbeckModel("Ornstein_Uhlenbeck default", ContinuousState.class);
  		Ornstein_Uhlenbeck.settrait_var(1.0);
  		Ornstein_Uhlenbeck.setBuiltIn(true);
  		Ornstein_Uhlenbeck.addToFile(null, getProject(), null);
		ContinuousData.registerDefaultModel("Likelihood", "Ornstein_Uhlenbeck default");
    //		super.projectEstablished();
  	 }
	/*.................................................................................................................*/
  	public MesquiteModule editModelNonModal(CharacterModel model, ObjectContainer w){
		if (model!=null && model instanceof Ornstein_UhlenbeckModel) {
			Ornstein_UhlenbeckModel modelToEdit =  ((Ornstein_UhlenbeckModel)model);
			
			double trait_var = modelToEdit.getTraitVar();
			double root_state = modelToEdit.getRootState();
			double peak_var = modelToEdit.getPeakVar();
			double mean_tip_peak = modelToEdit.getMeanTipPeak();
			double mean_tip_state = modelToEdit.getMeanTipState();
			double root_peak = modelToEdit.getRootPeak();
			double decay= modelToEdit.getdecay();
			boolean bounding = modelToEdit.isBound();
			boolean punc = modelToEdit.isPunctuational();
			boolean use_length = modelToEdit.isSpeciational();
			double lower_bound = modelToEdit.getlower_bound();
			double upper_bound = modelToEdit.getupper_bound();
			
			String bound_type = modelToEdit.getbound_type();
			
			List bounding_list = modelToEdit.getbounding_list();	
			
			MesquiteModule windowServer = hireNamedEmployee(WindowHolder.class, "#WindowBabysitter");
				if (windowServer == null)
			return null;
				
			CorTraitCurWindow cw=new CorTraitCurWindow(windowServer, "Edit model", "Model \"" + modelToEdit.getName() + "\"", makeCommand("setRate", modelToEdit), modelToEdit.gettrait_var(),0.0, MesquiteDouble.infinite, 1.0, 5.0);
			
			cw.makeCommandField("Variance of trait at tips:",trait_var,makeCommand("settrait_var", modelToEdit));
			cw.makeCommandFieldln("Root state of trait :",root_state,makeCommand("setRootState", modelToEdit));
			cw.makeCommandField("Root state of peak",root_peak,makeCommand("setRootPeak", modelToEdit));
			cw.makeCommandFieldln("Variance of peak at tips :",peak_var,makeCommand("setpeak_var", modelToEdit));
			cw.makeCommandField("Mean of peak at tips :",mean_tip_peak,makeCommand("setMeanTipPeak", modelToEdit));
			cw.makeCommandField("Mean of trait at tips :",mean_tip_state,makeCommand("setMeanTipState", modelToEdit));
			cw.makeCommandFieldln("decay :",decay,makeCommand("setdecay", modelToEdit));
			cw.makeCommandField("lower_bound :",lower_bound,makeCommand("setlower_bound", modelToEdit));
			cw.makeCommandFieldln("upper_bound :",upper_bound,makeCommand("setupper_bound", modelToEdit));
			cw.makeCommandComboBox("Bounding Type: ",bound_type,bounding_list,makeCommand("setbound_type",modelToEdit));
			cw.makeCommandCheckBox("Ignore Branch Length: ",use_length,makeCommand("toggel_branchlength",modelToEdit));
			cw.makeCommandCheckBox("Punctuated: ",punc,makeCommand("toggel_punctuated",modelToEdit));
			cw.makeCommandCheckBox("Use Bounding: ",bounding,makeCommand("toggel_bounds",modelToEdit));
			
			//cw.makeCommandButton("Get Defaults from Tree",makeCommand("get_defaults",this));				        

			if (w!=null)
				w.setObject(cw);
			return windowServer;
		}
		return null;
   	}
   	
   	
   	
			/*if (modal){
				a = MesquiteDouble.queryDouble(containerOfModule(), "Jukes Cantor rate", "Set rate of Jukes Cantor model", a);
				modelToEdit.setRate(a);
			}
			else */

	/*.................................................................................................................*/
	public boolean curatesModelClass(Class modelClass){
		return Ornstein_UhlenbeckModel.class.isAssignableFrom(modelClass);
	}
	/*.................................................................................................................*/
	public String getNameOfModelClass() {
		return "Ornstein_Uhlenbeck";
	}
	/*.....................getModelName............................................................................................*/
	public String getNEXUSNameOfModelClass() {
		return "Ornstein_Uhlenbeck";
	}
	/*.................................................................................................................*/
	public Class getModelClass() {
		return Ornstein_UhlenbeckModel.class;
	}
	/*.................................................................................................................*/
   	public CharacterModel makeNewModel(String name) {
   		this.name=name;
   		Ornstein_UhlenbeckModel Ornstein_Uhlenbeck = new Ornstein_UhlenbeckModel(name, ContinuousState.class);
   		Ornstein_Uhlenbeck.settrait_var(1.0);
       		return Ornstein_Uhlenbeck;
   	}
	/*.................................................................................................................*/
   	public CharacterModel readCharacterModel(String name, MesquiteInteger stringPos, String description, int format) {
   		Ornstein_UhlenbeckModel Ornstein_Uhlenbeck = new Ornstein_UhlenbeckModel(name, ContinuousState.class);
   		ParseUtil.getToken(description, stringPos);
   		double a =  MesquiteDouble.fromString(ParseUtil.getToken(description, stringPos));
 		if (a>=0 && MesquiteDouble.isCombinable(a)) {
 			Ornstein_Uhlenbeck.settrait_var(a);
 		}
  		return Ornstein_Uhlenbeck;
   	}
	/*.................................................................................................................*/
   	 public String getName() {
		return "Ornstein-Uhlenbeck Model";
   	 }
   	public String getModelName(){
   		return name;
   	}
	/*.................................................................................................................*/
    	 public String getNameForMenuItem() {
		return "Ornstein-Uhlenbeck Model...";
   	 }
   	 
	/*.................................................................................................................*/
   	 
 	/** returns an explanation of what the module does.*/
 	public String getExplanation() {
 		return "Initializes Ornstein_Uhlenbeck motion character model for likelihood and other probability calculations." ;
   	 }
}
