package mesquite.pdsim.lib;

import mesquite.lib.FileElement;
import mesquite.lib.Listenable;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteMessage;
import mesquite.lib.MesquiteProject;
import mesquite.lib.MesquiteTrunk;
import mesquite.lib.NexusBlock;
import mesquite.lib.StringUtil;
import mesquite.lib.characters.CharacterState;
import mesquite.lib.duties.CharMatrixManager;
import mesquite.lib.duties.ElementManager;

public abstract class Results extends FileElement implements Listenable, DataSource {
	String name;
	Class stateClass;
	static int numResults=0;
	public CoVarMatrixModel model;
	public abstract String getName(); 
	public abstract boolean getEditCancel();
	public String getNEXUSClassName(){
		return "RESULTS";
	}
	public String getNEXUSCommand() {
		return "RESULTS";
	}
	abstract public String toString();
	//abstract public String finish(String str, MesquiteInteger s, int format);
	abstract public String getNexusSpecification();
	abstract public void fromString(String description, MesquiteInteger stringPos, int format);
	public Results (String name, Class stateClass) {
		this.name = name;
		this.stateClass = stateClass;
		
		/*if (stateClass != Results.class && Results.class.isAssignableFrom(stateClass)){
			try {
				 s = (CharacterState)stateClass.newInstance();
				if (s!=null) {
					stateClassName = s.getDataTypeName();
				}
			}
			catch (IllegalAccessException e){MesquiteTrunk.mesquiteTrunk.alert("iae csmmm");e.printStackTrace(); }
			catch (InstantiationException e){MesquiteTrunk.mesquiteTrunk.alert("ie csmmm"); e.printStackTrace();}
		}*/
		numResults++;
		if (name == null)
			this.name = "Result" + numResults;
	}
	/*public CharMatrixManager getMatrixManager() {
		return matrixManager;
	}*/
	//abstract public ResultsCurator getResultsCurator();
}
