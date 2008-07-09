package mesquite.pdsim.lib;

import mesquite.lib.CommandChecker;
import mesquite.lib.MesquiteModule;
import mesquite.lib.ObjectContainer;
import mesquite.lib.Tree;
import mesquite.lib.characters.*;
import mesquite.lib.duties.*;

public interface CTSubModelCurator extends EditingCurator {
	public String getName();
	public CharacterModel makeNewModel (String str);
 	public MesquiteModule editModelNonModal(CharacterModel model, ObjectContainer w);
 	public Object doCommand(String str, String str2, CommandChecker checker);
 	public String getModelName();
}
