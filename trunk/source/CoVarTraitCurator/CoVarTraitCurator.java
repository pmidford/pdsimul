package mesquite.pdsim.CoVarTraitCurator;

import mesquite.pdsim.SimCurator.SimCurator;
import mesquite.pdsim.lib.*;
import mesquite.lib.EmployeeNeed;
import mesquite.lib.duties.*;

public class CoVarTraitCurator extends CoVarMatrixCurator implements EditingCurator {
	
	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed
		
		 EmployeeNeed e = registerEmployeeNeed(WindowHolder.class, getName() + " need assistance to hold a window" ,
				 "This is arranged automatically");
		 EmployeeNeed e2 = registerEmployeeNeed(CTSubModelCurator.class, getName() + " need CorTrait Models.", "This should grab them");
		 
		 EmployeeNeed e3 = registerEmployeeNeed(ResultsCurator.class, getName() , "good");
	}
}
