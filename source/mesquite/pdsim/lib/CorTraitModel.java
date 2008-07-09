package mesquite.pdsim.lib;

import java.awt.*;
import java.util.*;
import mesquite.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.duties.*;

public interface CorTraitModel extends Listenable, Listable {
	/*.................................................................................................................*/
	/** Returns true if size (maximum state allowed) can be set externally. */
	public boolean canChangeSize();
	/*.................................................................................................................*/
	/** Sets maximum state allowed. */
	public void setTraitNum(int num_of_traits);
	/*.................................................................................................................*/
	/** Returns maximum state allowed. */
	public int getTraitNum();
	/*.................................................................................................................*/
	/** Gets symbol for state. */
//	public String getStateSymbol(int state);
	/*.................................................................................................................*/
	/** Returns the value (cost, rate, etc.) for a transition between beginState and endState. */
	public MesquiteNumber getTransitionValue (int beginState, int endState, MesquiteNumber result);
	/*.................................................................................................................*/
	/** Sets the value (cost, rate, etc.) for a transition between beginState and endState. */
	public void setTransitionValue (int beginState, int endState, MesquiteNumber result, boolean notify);
	public void ModelNamePopup(int x);
	public void setModel(ProbabilityCorContCharModel CorTraitTask, int x);
	public String getLongModelName( int x);
	
//	public String getLongModelName(int x);
//	public String getShortModelName(int x);
	/***/
//	public cloneModelWithMotherLink
	/***/
//	public setModel
	/***/
//	public evolveCorState
	/***/
//	public setCharacterDistribution
	/** retruns the root state of the current tree*/
//	public getRootState
	/** Sets the random seed to start the simulation*/
//	public void setSeed(long x);
	/** returns the current random seed*/
//	public long getSeed();
	/** Is it ready to start running?*/
//	public boolean isFullySpecified();
}
