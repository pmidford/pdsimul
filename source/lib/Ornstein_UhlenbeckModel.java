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
package mesquite.pdsim.lib;

/* ~~ */

import java.util.*;
// import java.awt.*;
import mesquite.cont.lib.ContinuousData;
import mesquite.cont.lib.ContinuousHistory;
import mesquite.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.AlertDialog;
import mesquite.lib.MesquiteModule.*;
// import mesquite.lib.duties.*;
import mesquite.cont.lib.*;
// import mesquite.stochchar.lib.*;
import mesquite.pdsim.PDAnova.PDAnova;

/* ======================================================================== */
public class Ornstein_UhlenbeckModel extends ProbabilityCorContCharModel {
	Random rng = new Random(System.currentTimeMillis());

	double trait_var = 1.0;

	double peak_var = 0;

	double nord_var = 1;

	double peak_vel = 0;

	double trait_vel = 0;

	double root_state = 0;

	double root_peak = 0;

	double mean_tip_peak = 0;

	double mean_tip_state = 0;

	double decay = 1;

	long seedSet = 0;

	boolean use_bounds = false;

	boolean ignore_length = false;

	boolean punctuated = false;

	String bounding_type = "Truncation";

	double lower_bound = 0;

	double upper_bound = 1;

	public void setDefaults(Taxa taxa, MContinuousDistribution data, int ic) {
		int size = taxa.getNumTaxa();
		Vector states = new Vector();

		for (int it = 0; it < size; it++) {
			states.add(new MesquiteDouble(data.getState(ic, it, 0)));
		}
		MesquiteCommand setTraitVar = new MesquiteCommand("settrait_var", this);
		MesquiteCommand setRootState = new MesquiteCommand("setRootState", this);
		MesquiteCommand setRootPeak = new MesquiteCommand("setRootPeak", this);
		MesquiteCommand setFinalState = new MesquiteCommand("setMeanTipPeak", this);
		MesquiteCommand setFinalPeak = new MesquiteCommand("setMeanTipState", this);
		setTraitVar.doIt(MesquiteDouble.toString(PDAnova.getVar(states)));
		// setMeanVar.doIt(MesquiteDouble.toString(PDAnova.getMean(states)));
		setRootPeak.doIt(MesquiteDouble.toString(PDAnova.getMean(states)));
		setRootState.doIt(MesquiteDouble.toString(PDAnova.getMean(states)));
		setFinalState.doIt(MesquiteDouble.toString(PDAnova.getMean(states)));
		setFinalPeak.doIt(MesquiteDouble.toString(PDAnova.getMean(states)));
	}

	public Ornstein_UhlenbeckModel(String name, Class dataClass) {
		super(name, dataClass);
	}

	public double getRootValue(int x) {
		if (x == 0)
			return root_state;
		if (x == 1)
			return root_peak;
		else
			return 0;
	}

	public void initialize(Tree tree) {
		if (tree != null) {
			double nordheim, span, mean_height;
			nordheim = getNordheim(tree, ignore_length);
			span = findspan(tree, tree.getRoot(), ignore_length, 0, 0);
			mean_height = span / tree.numberOfTerminalsInClade(tree.getRoot());
			System.out.println("(span,numberOfTerminals):(" + span + ","
					+ tree.numberOfTerminalsInClade(tree.getRoot()) + ")");

			peak_vel = (mean_tip_peak - root_peak) / (mean_height);
			nord_var = trait_var / nordheim;
			trait_vel = (mean_tip_state - root_state) / (mean_height);
			System.out.println("(peak,nordheim,averagehieght):(" + trait_vel
					+ "," + nordheim + "," + mean_height + ")");

			initialized = true;
		} else {
			System.out
					.println("Tried to initialize mesquite.pdsim.lib.PDSIM_BrownianModel w/ null tree!");
		}
	}

	public double[] evolveMultiState(double beginStates[], CorTrait rnd,
			double BranchLength, int node, int trait, boolean bound) {
		// model.traits.make_traits();
		// System.out.println("Starting submodel Sim");
		double step;
		double step_trait_vel;
		double step_peak;
		// double BranchLength=tree.getBranchLength(node, 1.0);
		double peakState = beginStates[1]; // TODO change this back.
		double traitState = beginStates[0];
		// System.out.println("peakSate:"+peakState);
		step_peak = super.getchangevelocity(BranchLength, rng.nextGaussian(),
				peak_var, peak_vel);
		step_trait_vel = super.getchangevelocity(BranchLength, rnd.get_num(0,
				trait), nord_var, trait_vel);
		// TODO decay cannot = 0;
		double temp;
		if (decay != 0) {
			temp = traitState - peakState - (step_trait_vel + step_peak)
					/ decay;
		} else
			temp = 0;
		// System.out.println("temp calculate.");
		if (decay * BranchLength > 80) {
			step = step_trait_vel * BranchLength - temp;
		} else
			step = temp * Math.exp(-1 * decay * BranchLength) + step_trait_vel
					* BranchLength - temp;
		// System.out.println("step.");
		if (bound) {
			System.out.println("checking bounds!");
			MesquiteBoolean do_over = new MesquiteBoolean(false);
			traitState = super.bound(traitState, step, bounding_type,
					lower_bound, upper_bound, do_over);
			if (do_over.getValue()) {
				System.out.println("Boing!");
				evolveMultiState(beginStates, rnd, BranchLength, node, trait,
						bound);
			}
		} else
			traitState = traitState + step;
		// System.out.println("bound.");
		peakState = peakState + step_trait_vel * BranchLength;
		// System.out.println("set beginState[0]");
		// System.out.println("from:"+beginStates[0]+" to:"+traitState);
		beginStates[0] = traitState;
		// System.out.println("set beginState[0]");
		beginStates[1] = peakState; // TODO change this back.
		return beginStates;
	}

	boolean initialized = false;

	public boolean isInitialized() {
		return initialized;
	}

	/* ................................................................................................................. */
	/**
	 * Randomly generates according to model an end state on branch from
	 * beginning states
	 */
	ContinuousHistory XtraHistory;

	public double evolveState(double beginState, Tree tree, int node) {
		double step;
		double step_trait_vel;
		double step_peak;

		double BranchLength;
		System.out.println("??"); //toString());
		if (tree.getRoot()==node||!initialized) {
			System.out.println("initialize"); //toString());
			initialize(tree);
			initialized = true;
			System.out.println("make XtraHistory"); //toString());
			XtraHistory = new ContinuousHistory(tree.getTaxa(), tree.getNumNodeSpaces(), null);
			XtraHistory.setState(tree.getRoot(), root_peak);
		}
		if (ignore_length) {
			BranchLength = 1;
		} else
			BranchLength = tree.getBranchLength(node, 1);
		if(XtraHistory.getState(node)==MesquiteDouble.unassigned)
			XtraHistory.setState(node, root_peak);
		double peakState = XtraHistory.getState(node);
		System.out.println("PeakState "+peakState); //toString());
		// TODO change this back.
		double traitState = beginState;
		// System.out.println("peakSate:"+peakState);
		step_peak = super.getchangevelocity(BranchLength, rng.nextGaussian(),
				peak_var, peak_vel);
		step_trait_vel = super.getchangevelocity(BranchLength, rng
				.nextGaussian(), nord_var, trait_vel);
		// TODO decay cannot = 0;
		double temp;
		if (decay != 0) {
			temp = traitState - peakState - (step_trait_vel + step_peak)
					/ decay;
		} else
			temp = 0;
		// System.out.println("temp calculate.");
		if (decay * BranchLength > 80) {
			step = step_trait_vel * BranchLength - temp;
		} else
			step = temp * Math.exp(-1 * decay * BranchLength) + step_trait_vel
					* BranchLength - temp;
		// System.out.println("step.");
		System.out.println("checing bounds? "+use_bounds+" "+this.getName()+" "+this.getNexusSpecification()); //toString());

		if (use_bounds) {
			System.out.println("checking bounds!");
			MesquiteBoolean do_over = new MesquiteBoolean(false);
			traitState = super.bound(traitState, step, bounding_type,
					lower_bound, upper_bound, do_over);
			if (do_over.getValue()) {
				System.out.println("Boing!");
				evolveState(beginState, tree, node);
			}
		} else
			traitState = traitState + step;
		// System.out.println("bound.");
		peakState = peakState + step_trait_vel * BranchLength;
		// System.out.println("set beginState[0]");
		// System.out.println("from:"+beginStates[0]+" to:"+traitState);
		beginState = traitState;
		XtraHistory.setState(node, peakState);
		// System.out.println("set beginState[0]");
		// beginState=peakState; //TODO change this back.
		return beginState;
	}

	/* ................................................................................................................. */
	/**
	 * Returns (possibly by randomly generating) according to model an ancestral
	 * state for root of tree
	 */
	public double getRootState(Tree tree) {
		return 0; // todo: stochastic?
	}

	/* ................................................................................................................. */
	public void fromString(String description, MesquiteInteger stringPos,
			int format) {
		String name, value;
		name = ParseUtil.getToken(description, stringPos); // eating token
		value = ParseUtil.getToken(description, stringPos); // eating token
		System.out.println(name + " : " + value);

		while (name != null && value != null) {
			if (MesquiteDouble.isCombinable(MesquiteDouble.fromString(value))) {
				System.out.println("value is double");
				if (name.equalsIgnoreCase("traitvar")) {
					System.out.println("setting " + name + " to " + value);
					trait_var = MesquiteDouble.fromString(value);
				}

				if (name.equalsIgnoreCase("nordvar")) {
					System.out.println("setting " + name + " to " + value);
					nord_var = MesquiteDouble.fromString(value);
				} else if (name.equalsIgnoreCase("decay")) {
					System.out.println("setting " + name + " to " + value);
					decay = MesquiteDouble.fromString(value);
				} else if (name.equalsIgnoreCase("peakvar")) {
					System.out.println("setting " + name + " to " + value);
					peak_var = MesquiteDouble.fromString(value);
				} else if (name.equalsIgnoreCase("peakvel")) {
					System.out.println("setting " + name + " to " + value);
					peak_vel = MesquiteDouble.fromString(value);
				} else if (name.equalsIgnoreCase("rootpeak")) {
					System.out.println("setting " + name + " to " + value);
					root_peak = MesquiteDouble.fromString(value);
				} else if (name.equalsIgnoreCase("meantippeak")) {
					System.out.println("setting " + name + " to " + value);
					mean_tip_peak = MesquiteDouble.fromString(value);
				} else if (name.equalsIgnoreCase("meantipstate")) {
					System.out.println("setting " + name + " to " + value);
					mean_tip_state = MesquiteDouble.fromString(value);
				} else if (name.equalsIgnoreCase("traitvel")) {
					System.out.println("setting " + name + " to " + value);
					trait_vel = MesquiteDouble.fromString(value);
				} else if (name.equalsIgnoreCase("rootstate")) {
					System.out.println("setting " + name + " to " + value);
					root_state = MesquiteDouble.fromString(value);
				} else if (name.equalsIgnoreCase("lowerbound")) {
					System.out.println("setting " + name + " to " + value);
					lower_bound = MesquiteDouble.fromString(value);
				} else if (name.equalsIgnoreCase("upperbound")) {
					System.out.println("setting " + name + " to " + value);
					upper_bound = MesquiteDouble.fromString(value);
				}
			} else if (value.equalsIgnoreCase("true")
					|| value.equalsIgnoreCase("false")) {

				if (name.equalsIgnoreCase("usebounds")) {
					System.out.println("setting " + name + " to " + value);
					if (value.equalsIgnoreCase("true"))
						use_bounds = true;
					else if (value.equalsIgnoreCase("false"))
						use_bounds = false;
				} else if (name.equalsIgnoreCase("ignorelength")) {
					System.out.println("setting " + name + " to " + value);
					if (value.equalsIgnoreCase("true"))
						ignore_length = true;
					else if (value.equalsIgnoreCase("false"))
						ignore_length = false;
				} else if (name.equalsIgnoreCase("punctuated")) {
					System.out.println("setting " + name + " to " + value);
					if (value.equalsIgnoreCase("true"))
						punctuated = true;
					else if (value.equalsIgnoreCase("false"))
						punctuated = false;
				}
			} else {
				System.out.println("setting " + name + " to " + value);
				setbounding_type(value);
			}
			name = ParseUtil.getToken(description, stringPos); // eating token
			value = ParseUtil.getToken(description, stringPos); // eating token
			System.out.println(name + " : " + value);
		}
	}

	/* ................................................................................................................. */
	public String getParameters() {
		System.out.println("gettingParameters");
		return "traitvar " + MesquiteDouble.toString(trait_var) + " nordvar "
				+ MesquiteDouble.toString(nord_var) + " rootpeak "
				+ MesquiteDouble.toString(root_peak) + " meantippeak "
				+ MesquiteDouble.toString(mean_tip_peak) + " meanTipState "
				+ MesquiteDouble.toString(mean_tip_state) + " traitvel "
				+ MesquiteDouble.toString(trait_vel) + " rootstate "
				+ MesquiteDouble.toString(root_state) + " usebounds "
				+ use_bounds + " ignorelength " + ignore_length
				+ " punctuated " + punctuated + " boundingtype "
				+ bounding_type + " lowerbound "
				+ MesquiteDouble.toString(lower_bound) + " upperbound "
				+ MesquiteDouble.toString(upper_bound);
	}

	/* ................................................................................................................. */
	public String getNexusSpecification() {
		System.out.println("gettingNesusSpecification");
		return "traitvar " + MesquiteDouble.toString(trait_var) + " nordvar "
				+ MesquiteDouble.toString(nord_var) + " rootpeak "
				+ MesquiteDouble.toString(root_peak) + " meantippeak "
				+ MesquiteDouble.toString(mean_tip_peak) + " meanTipState "
				+ MesquiteDouble.toString(mean_tip_state) + " traitvel "
				+ MesquiteDouble.toString(trait_vel) + " rootstate "
				+ MesquiteDouble.toString(root_state) + " usebounds "
				+ use_bounds + " ignorelength " + ignore_length
				+ " punctuated " + punctuated + " boundingtype "
				+ bounding_type + " lowerbound "
				+ MesquiteDouble.toString(lower_bound) + " upperbound "
				+ MesquiteDouble.toString(upper_bound);
	} /* ................................................................................................................. */

	public void settrait_var(double trait_var) {
		this.trait_var = trait_var;
		notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
	}

	/* ................................................................................................................. */
	public double gettrait_var() {
		return trait_var;
	}

	MesquiteInteger pos = new MesquiteInteger();

	/* ................................................................................................................. */
	/** Performs command (for Commandable interface) */
	public Object doCommand(String commandName, String arguments,
			CommandChecker checker) {
		if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"settrait_var")) {
			pos.setValue(0);
			double newtrait_var = MesquiteDouble.fromString(arguments, pos);
			double a = gettrait_var();
			if (!MesquiteDouble.isCombinable(newtrait_var)) {
				newtrait_var = MesquiteDouble.queryDouble(
						MesquiteTrunk.mesquiteTrunk.containerOfModule(),
						"Set trait_var", "trait_var of change:", a);
			}
			if (newtrait_var >= 0 && newtrait_var != a
					&& MesquiteDouble.isCombinable(newtrait_var)) {
				settrait_var(newtrait_var);
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"setlower_bound")) {
			pos.setValue(0);
			double newvar = MesquiteDouble.fromString(arguments, pos);
			double a = getlower_bound();
			if (!MesquiteDouble.isCombinable(newvar)) {
				newvar = MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk
						.containerOfModule(), "Set mean_vel",
						"mean_vel of change:", a);
			}
			if (newvar < upper_bound && newvar != a
					&& MesquiteDouble.isCombinable(newvar)) {
				lower_bound = newvar;
			} else if (newvar >= upper_bound) {
				System.out
						.println("assignment failure:lower bound must be less than upper bound");
				MesquiteInteger buttonPressed = new MesquiteInteger(1);
				ExtensibleDialog boo = new ExtensibleDialog(
						MesquiteTrunk.mesquiteTrunk.containerOfModule(),
						"boo!", buttonPressed);
				boo.addLabel("lower bound must be less than upper bound", 1);
				boo.completeAndShowDialog("OK", null, null, "OK");
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"setupper_bound")) {

			pos.setValue(0);
			double newvar = MesquiteDouble.fromString(arguments, pos);
			double a = getupper_bound();
			System.out.println("command acknowlaged:" + newvar);
			if (!MesquiteDouble.isCombinable(newvar)) {
				newvar = MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk
						.containerOfModule(), "Set mean_vel",
						"mean_vel of change:", a);
			}
			if (newvar > lower_bound && newvar != a
					&& MesquiteDouble.isCombinable(newvar)) {
				upper_bound = newvar;
			} else if (newvar <= upper_bound) {
				// MesquitePopup a=new MesquitePopup()
				MesquiteInteger buttonPressed = new MesquiteInteger(1);
				ExtensibleDialog boo = new ExtensibleDialog(
						MesquiteTrunk.mesquiteTrunk.containerOfModule(),
						"boo!", buttonPressed);
				boo.addLabel("upper bound must be greater than lower bound", 1);
				boo.completeAndShowDialog("OK", null, null, "OK");
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"setpeak_var")) {
			pos.setValue(0);
			double newvar = MesquiteDouble.fromString(arguments, pos);
			double a = peak_var;
			if (!MesquiteDouble.isCombinable(newvar)) {
				newvar = MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk
						.containerOfModule(), "Set mean_var",
						"mean_var of change:", a);
			}
			if (newvar >= 0 && newvar != a
					&& MesquiteDouble.isCombinable(newvar)) {
				setpeak_var(newvar);
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"setnord_var")) {
			pos.setValue(0);
			double newvar = MesquiteDouble.fromString(arguments, pos);
			double a = getnord_var();
			if (!MesquiteDouble.isCombinable(newvar)) {
				newvar = MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk
						.containerOfModule(), "Set nord_var",
						"nord_var of change:", a);
			}
			if (newvar >= 0 && newvar != a
					&& MesquiteDouble.isCombinable(newvar)) {
					setnord_var(newvar);
					notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"setpeak_vel")) {
			pos.setValue(0);
			double newvar = MesquiteDouble.fromString(arguments, pos);
			double a = peak_vel;
			if (!MesquiteDouble.isCombinable(newvar)) {
				newvar = MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk
						.containerOfModule(), "Set mean_vel",
						"mean_vel of change:", a);
			}
			if (newvar >= 0 && newvar != a
					&& MesquiteDouble.isCombinable(newvar)) {
				peak_vel = newvar;
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName, "setdecay")) {
			pos.setValue(0);
			double newvar = MesquiteDouble.fromString(arguments, pos);
			double a = decay;
			if (!MesquiteDouble.isCombinable(newvar)) {
				newvar = MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk
						.containerOfModule(), "Set mean_vel",
						"mean_vel of change:", a);
			}
			if (newvar >= 0 && newvar != a
					&& MesquiteDouble.isCombinable(newvar)) {
				decay = newvar;
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"setMeanTipPeak")) {
			pos.setValue(0);
			double newvar = MesquiteDouble.fromString(arguments, pos);
			double a = mean_tip_peak;
			if (!MesquiteDouble.isCombinable(newvar)) {
				newvar = MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk
						.containerOfModule(), "Set mean_vel",
						"mean_vel of change:", a);
			}
			if (newvar != a && MesquiteDouble.isCombinable(newvar)) {
				mean_tip_peak = newvar;
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"setRootPeak")) {
			pos.setValue(0);
			double newvar = MesquiteDouble.fromString(arguments, pos);
			double a = root_peak;
			if (!MesquiteDouble.isCombinable(newvar)) {
				newvar = MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk
						.containerOfModule(), "Set mean_vel",
						"mean_vel of change:", a);
			}
			if (newvar != a && MesquiteDouble.isCombinable(newvar)) {
				root_peak = newvar;
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"setRootState")) {
			pos.setValue(0);
			double newvar = MesquiteDouble.fromString(arguments, pos);
			double a = root_state;
			if (!MesquiteDouble.isCombinable(newvar)) {
				newvar = MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk
						.containerOfModule(), "Set mean_vel",
						"mean_vel of change:", a);
			}
			if (newvar != a && MesquiteDouble.isCombinable(newvar)) {
				root_state = newvar;
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"setMeanTipState")) {
			pos.setValue(0);
			double newvar = MesquiteDouble.fromString(arguments, pos);
			double a = mean_tip_state;
			if (!MesquiteDouble.isCombinable(newvar)) {
				newvar = MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk
						.containerOfModule(), "Set mean_vel",
						"mean_vel of change:", a);
			}
			if (newvar != a && MesquiteDouble.isCombinable(newvar)) {
				mean_tip_state = newvar;
				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"toggel_branchlength")) {
			pos.setValue(0);
			if (arguments == "1") {
				ignore_length = true;
			} else if (arguments == "0") {
				ignore_length = false;
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"toggel_punctuated")) {
			pos.setValue(0);
			if (arguments == "1") {
				punctuated = true;
			} else if (arguments == "0") {
				punctuated = false;
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"toggel_bounds")) {
			pos.setValue(0);
			// System.out.print(arguments);

			if (arguments == "1") {
				// System.out.println(" equals 1");
				use_bounds = true;
			} else if (arguments == "0") {
				// System.out.println(" equals 0");
				use_bounds = false;
			}
		} else if (checker.compare(this.getClass(),
				"Sets the instantaneous trait_var of change in the model",
				"[trait_var of change; must be > 0]", commandName,
				"setbound_type")) {
			// pos.setValue(0);
			String newvar = arguments;
			System.out.println("command acknowlaged:" + newvar);
			setbounding_type(newvar);
		} else {
			// System.out.println("failed :"+commandName);
			return super.doCommand(commandName, arguments, checker);
		}
		return null;
	}

	public void setpeak_vel(double x) {
		peak_vel = x;
		notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
	}

	public double getPeakVel() {
		return peak_vel;
	}

	public void setnord_var(double x) {
		nord_var = x;
		notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
	}

	public double getnord_var() {
		return nord_var;
	}

	public void setpeak_var(double x) {
		peak_var = x;
		notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
	}

	public double getPeakVar() {
		return peak_var;
	}

	public void setdecay(double x) {
		decay = x;
		notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
	}

	public double getdecay() {
		return decay;
	}

	public void setbounding(boolean a) {
		use_bounds = a;
		notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
	}

	public void setspeciation(boolean a) {
		ignore_length = a;
	}

	public void setpunctuated(boolean a) {
		punctuated = a;
	}

	public boolean isBound() {
		return use_bounds;
	}

	public boolean isSpeciational() {
		return ignore_length;
	}

	public boolean isPunctuational() {
		return punctuated;
	}

	public double getTraitVar() {
		return trait_var;
	}

	public double getRootState() {
		return root_state;
	}

	public double getMeanTipPeak() {
		return mean_tip_peak;
	}

	public double getRootPeak() {
		return root_peak;
	}

	public CharacterModel cloneModelWithMotherLink(CharacterModel formerClone) {
		Ornstein_UhlenbeckModel bmm = new Ornstein_UhlenbeckModel(name,
				getStateClass());
		completeDaughterClone(formerClone, bmm);
		return bmm;
	}

	/*
	 * copy information from this to model passed (used in
	 * cloneModelWithMotherLink to ensure that superclass info is copied);
	 * should call super.copyToClone(pm)
	 */
	public void copyToClone(CharacterModel md) {
		if (md == null || !(md instanceof Ornstein_UhlenbeckModel))
			return;
		Ornstein_UhlenbeckModel model = (Ornstein_UhlenbeckModel) md;
		model.fromString(this.getNexusSpecification(), new MesquiteInteger(0), 0);
		super.copyToClone(md);
	}

	public boolean isFullySpecified() {
		return trait_var != MesquiteDouble.unassigned;
	}

	public void setSeed(long seed) {
		seedSet = seed;
		rng.setSeed(seed);
	}

	public long getSeed() {
		return rng.nextLong();
	}

	/** return an explanation of the model. */
	public String getExplanation() {
		return "A stochastic model in which expected change is distributed normally with mean 0, and variance proportional to the rate. Branch Length is ignored.  The current rate is "
				+ MesquiteDouble.toString(trait_var);
	}

	public String getNEXUSClassName() {
		return "Ornstein_Uhlenbeck";
	}

	public String getModelTypeName() {
		return "Ornstein_Uhlenbeck";
	}

	public int get_obj_num() {
		return 2;
	}

	public void setbounding_type(String str) {
		List types;
		types = get_boundingtypes();
		for (int x = 0; x < types.size(); x++) {
			if (types.contains(str)) {
				bounding_type = str;
			}
		}
	}

	public String getbound_type() {
		return bounding_type;
	}

	public double getlower_bound() {
		return lower_bound;
	}

	public double getupper_bound() {
		return upper_bound;
	}

	public double getMeanTipState() {
		return mean_tip_state;
	}

	public List getbounding_list() {
		return super.get_boundingtypes();
	}
}