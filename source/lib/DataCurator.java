package mesquite.pdsim.lib;
import mesquite.lib.*;
import mesquite.lib.duties.*;

public abstract interface DataCurator {
	public abstract String getName();
	public abstract MesquiteModule getModule();
}
