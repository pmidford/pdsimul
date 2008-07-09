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
/*~~  */

import java.util.*;

import mesquite.cont.lib.ContinuousState;
import mesquite.lib.CommandChecker;
import mesquite.lib.MesquiteDouble;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteListener;
import mesquite.lib.MesquiteTrunk;
import mesquite.lib.Notification;
import mesquite.lib.ParseUtil;
import mesquite.lib.Tree;
import mesquite.lib.characters.CharacterModel;
import mesquite.lib.duties.TreeSource;
import mesquite.stochchar.lib.SimModelCompatInfo;


/* ======================================================================== */
public class PDSIM_BrownianModel  extends ProbabilityCorContCharModel {
	Random rng = new Random(System.currentTimeMillis());
	double trait_var = 1.0;
	double mean_var = 0;
	double nord_var = 1;
	double mean_vel = 0;
	double meanTipState = 0;
	double trait_vel = 0;
	double root_state = 0;
	
	long seedSet = 0;
	boolean use_bounds=false;
	boolean ignore_length=false;
	boolean punctuated=false;
	
	String bounding_type="Truncation";
	double lower_bound=0;
	double upper_bound=1;
	
	
	public PDSIM_BrownianModel (String name, Class dataClass) {
		super(name, dataClass);
	}

	public double getRootValue(int x){
		if (x==0) return root_state;
		else return 0;
	}
	public boolean isBound (){
		return use_bounds;
	}
	public boolean isPunctuational (){
		return punctuated;
	}
	public boolean isSpeciational (){
		return ignore_length;
	}
	
	public double[] evolveMultiState (double beginState[], CorTrait rnd, double BranchLength, int node, int trait, boolean bound){
		//model.traits.make_traits();
	//	System.out.println("Starting submodel Sim");
		double step;
		double step_trait_vel;
		double step_mean_vel;

		//p.nordvar[1]:=sqrt(p.selvariance[1])/p.nordheim;

		
		step_mean_vel=super.getchangevelocity(BranchLength,rng.nextGaussian(),mean_var,mean_vel);
		step_trait_vel=super.getchangevelocity(BranchLength,rnd.get_num(0,trait),nord_var,trait_vel);				
		step=BranchLength*(step_mean_vel+step_trait_vel);   //pre-1.05 this failed to take the sqrt!!!!

		if (bound){
			boolean do_over=false;
			beginState[0]=super.bound(beginState[0],step,bounding_type,lower_bound,upper_bound,do_over);
			if(do_over)evolveMultiState(beginState,  rnd,  BranchLength,  node,  0,  bound);
		}
		else beginState[0]=beginState[0]+step;
		return beginState;
	}
 	/*.................................................................................................................*/
	/** Randomly generates according to model an end state on branch from beginning states*/
	public double evolveState (double beginState, Tree tree, int node){
		return beginState + trait_var* rng.nextGaussian(); //Math.sqrt (tree.getBranchLength(node, 1.0));   //pre-1.05 this failed to take the sqrt!!!!
	}
 	/*.................................................................................................................*/
	/** Returns (possibly by randomly generating) according to model an ancestral state for root of tree*/
	public double getRootState (Tree tree){
		return 0;  //todo: stochastic?
	}
	//TODO none of these are right;
	//double nord_var = 1;

	/*................................................................................................................*/
	public void fromString (String description, MesquiteInteger stringPos, int format) {
		String name, value;
		name=ParseUtil.getToken(description, stringPos); //eating token
		value=ParseUtil.getToken(description, stringPos); //eating token
		System.out.println(name+" : "+value);
		
		while(name!=null&&value!=null){
   			if(MesquiteDouble.isCombinable(MesquiteDouble.fromString(value))){
   				System.out.println("value is double");
   			if(name.equalsIgnoreCase("traitvar")){
  				System.out.println("setting "+name+" to "+value);
   				trait_var =  MesquiteDouble.fromString(value);
   			}
   			
   			if(name.equalsIgnoreCase("nordvar")){
  				System.out.println("setting "+name+" to "+value);
   				nord_var =  MesquiteDouble.fromString(value);
   			}
   			else if(name.equalsIgnoreCase("meanvel")){
  				System.out.println("setting "+name+" to "+value);
   				mean_vel = MesquiteDouble.fromString(value);
   			}
   			else if(name.equalsIgnoreCase("meanvar")){
  				System.out.println("setting "+name+" to "+value);
   				mean_var = MesquiteDouble.fromString(value);
   			}
   			else if(name.equalsIgnoreCase("meantipstate")){
  				System.out.println("setting "+name+" to "+value);
   		   		meanTipState = MesquiteDouble.fromString(value);
   			}
   			else if(name.equalsIgnoreCase("traitvel")){
  				System.out.println("setting "+name+" to "+value);
		   		trait_vel = MesquiteDouble.fromString(value);
   			}
   			else if(name.equalsIgnoreCase("rootstate")){
  				System.out.println("setting "+name+" to "+value);
		   		root_state = MesquiteDouble.fromString(value);
   			}
   			else if(name.equalsIgnoreCase("lowerbound")){
  				System.out.println("setting "+name+" to "+value);
		   		lower_bound = MesquiteDouble.fromString(value);
   			}
   			else if(name.equalsIgnoreCase("upperbound")){
  				System.out.println("setting "+name+" to "+value);
		   		upper_bound = MesquiteDouble.fromString(value);
   			}
   			}
   			else if(value.equalsIgnoreCase("true")||value.equalsIgnoreCase("false")){
   				
   			if(name.equalsIgnoreCase("usebounds")){
  				System.out.println("setting "+name+" to "+value);
   				if (value.equalsIgnoreCase("true"))
   					use_bounds = true;
   				else if (value.equalsIgnoreCase("false"))
   					use_bounds = false;
   			}
  			else if(name.equalsIgnoreCase("ignorelength")){
  				System.out.println("setting "+name+" to "+value);
   				if (value.equalsIgnoreCase("true"))
   					ignore_length = true;
   				else if (value.equalsIgnoreCase("false"))
   					ignore_length = false;
   			}
  			else if(name.equalsIgnoreCase("punctuated")){
  				System.out.println("setting "+name+" to "+value);
   				if (value.equalsIgnoreCase("true"))
   					punctuated = true;
   				else if (value.equalsIgnoreCase("false"))
   					punctuated = false;
   			}
   			}
   			else {
  				System.out.println("setting "+name+" to "+value);
  				setbounding_type(value);
  			}
   			name=ParseUtil.getToken(description, stringPos); //eating token
   			value=ParseUtil.getToken(description, stringPos); //eating token
   			System.out.println(name+" : "+value);
   		}
   	}
 	/*.................................................................................................................*/
	public String getParameters() {
		System.out.println("gettingParameters");
		return "traitvar " + MesquiteDouble.toString(trait_var)+" nordvar "+MesquiteDouble.toString(nord_var)+" meanvel "+MesquiteDouble.toString(mean_vel)+" meanvar "+MesquiteDouble.toString(mean_var)+" meanTipState "+MesquiteDouble.toString(meanTipState)+" traitvel "+MesquiteDouble.toString(trait_vel)+" rootstate "+MesquiteDouble.toString(root_state)+" usebounds "+use_bounds+" ignorelength "+ignore_length+" punctuated "+punctuated+" boundingtype "+bounding_type+" lowerbound "+MesquiteDouble.toString(lower_bound)+" upperbound "+MesquiteDouble.toString(upper_bound);
		}
 	/*.................................................................................................................*/
	public String getNexusSpecification () {
		System.out.println("gettingNesusSpecification");
		return "traitvar " + MesquiteDouble.toString(trait_var)+" nordvar "+MesquiteDouble.toString(nord_var)+" meanvel "+MesquiteDouble.toString(mean_vel)+" meanvar "+MesquiteDouble.toString(mean_var)+" meanTipState "+MesquiteDouble.toString(meanTipState)+" traitvel "+MesquiteDouble.toString(trait_vel)+" rootstate "+MesquiteDouble.toString(root_state)+" usebounds "+use_bounds+" ignorelength "+ignore_length+" punctuated "+punctuated+" boundingtype "+bounding_type+" lowerbound "+MesquiteDouble.toString(lower_bound)+" upperbound "+MesquiteDouble.toString(upper_bound);
	}
	public void initialize(Tree tree){
		if (tree!=null) {
			double nordheim,span,mean_height;
			nordheim=getNordheim(tree, ignore_length);
			span=findspan(tree, tree.getRoot(), ignore_length, 0, 0);
			mean_height=span/tree.numberOfTerminalsInClade(tree.getRoot());
			mean_vel=(meanTipState-root_state)/(mean_height);
			nord_var=trait_var/nordheim;
		}
		else{
			System.out.println("Tried to initialize mesquite.pdsim.lib.PDSIM_BrownianModel w/ null tree!");
		}
	}
 	/*.................................................................................................................*/
	public void settrait_var(double trait_var){
		this.trait_var = trait_var;
		notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
	}
 	/*.................................................................................................................*/
	public double gettrait_var(){
		return trait_var;
	}
	MesquiteInteger pos = new MesquiteInteger();
 	/*.................................................................................................................*/
 	/** Performs command (for Commandable interface) */
   	public Object doCommand(String commandName, String arguments, CommandChecker checker){
	 	/*if (checker.compare(this.getClass(), "Sets the instantaneous trait_var of change in the model", "[trait_var of change; must be > 0]", commandName, "settrait_var")) {
	 		pos.setValue(0);
		double newtrait_var = MesquiteDouble.fromString(arguments, pos);
		double a = gettrait_var();
		if (!MesquiteDouble.isCombinable(newtrait_var)) {
			newtrait_var= MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Set trait_var", "trait_var of change:", a);
	 		}
	 		if (newtrait_var>=0  && newtrait_var!=a && MesquiteDouble.isCombinable(newtrait_var)) {
	 			trait_var=newtrait_var;
	 		}
	 	}*/
	 	if (checker.compare(this.getClass(), "Sets the instantaneous trait_var of change in the model", "[trait_var of change; must be > 0]", commandName, "setmean_var")) {
	 		pos.setValue(0);
	 		double newvar = MesquiteDouble.fromString(arguments, pos);
	 		double a = getmean_var();
	 		if (!MesquiteDouble.isCombinable(newvar)) {
	 			newvar= MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Set mean_var", "mean_var of change:", a);
 			}
 			if (newvar>=0  && newvar!=a && MesquiteDouble.isCombinable(newvar)) {
 				setmean_var(newvar);
 			}
	 	}
	 	else if (checker.compare(this.getClass(), "Sets the instantaneous trait_var of change in the model", "[trait_var of change; must be > 0]", commandName, "setroot_state")) {
	 		pos.setValue(0);
	 		double newvar = MesquiteDouble.fromString(arguments, pos);
	 		double a = getroot_state();
	 		if (!MesquiteDouble.isCombinable(newvar)) {
	 			newvar= MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Set nord_var", "nord_var of change:", a);
	 		}
	 		if (newvar!=a && MesquiteDouble.isCombinable(newvar)) {
	 			root_state=newvar;
	 		}
	 	}
		else if (checker.compare(this.getClass(), "Sets the instantaneous trait_var of change in the model", "[trait_var of change; must be > 0]", commandName, "setmeanTipState")) {
			pos.setValue(0);
	 		double newvar = MesquiteDouble.fromString(arguments, pos);
	 		double a = getmean_vel();
	 		if (!MesquiteDouble.isCombinable(newvar)) {
	 			newvar= MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Set mean_vel", "mean_vel of change:", a);
	 		}
	 		if (newvar!=a && MesquiteDouble.isCombinable(newvar)) {
	 			meanTipState=newvar;
	 		}
	 	}		
		else if (checker.compare(this.getClass(), "Sets the instantaneous trait_var of change in the model", "[trait_var of change; must be > 0]", commandName, "toggel_branchlength")) {
	 		pos.setValue(0);
	 		if(arguments=="1"){
	 			ignore_length=true;
	 		}
	 		else if (arguments=="0"){
	 			ignore_length=false;
	 		}
	 	}
		else if (checker.compare(this.getClass(), "Sets the instantaneous trait_var of change in the model", "[trait_var of change; must be > 0]", commandName, "toggel_punctuated")) {
	 		pos.setValue(0);
	 		if(arguments=="1"){
	 			punctuated=true;
	 		}
	 		else if (arguments=="0"){
	 			punctuated=false;
	 		}
	 	}
		else if (checker.compare(this.getClass(), "Sets the instantaneous trait_var of change in the model", "[trait_var of change; must be > 0]", commandName, "toggel_bounds")) {
	 		pos.setValue(0);
	 		System.out.print(arguments);
	 		
	 		if(arguments=="1"){
		 		System.out.println(" equals 1");	 			
	 			use_bounds=true;
	 		}
	 		else if (arguments=="0"){
		 		System.out.println(" equals 0");	 			
	 			use_bounds=false;
	 		}
	 	}
		else if (checker.compare(this.getClass(), "Sets the instantaneous trait_var of change in the model", "[trait_var of change; must be > 0]", commandName, "setlower_bound")) {
	 		pos.setValue(0);
	 		double newvar = MesquiteDouble.fromString(arguments, pos);
	 		double a = getlower_bound();
	 		if (!MesquiteDouble.isCombinable(newvar)) {
	 			newvar= MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Set mean_vel", "mean_vel of change:", a);
	 		}
	 		if (newvar < this.upper_bound && newvar!=a && MesquiteDouble.isCombinable(newvar)) {
	 			lower_bound=newvar;
	 		}
	 	}
		else if (checker.compare(this.getClass(), "Sets the instantaneous trait_var of change in the model", "[trait_var of change; must be > 0]", commandName, "setupper_bound")) {
	 		pos.setValue(0);
	 		double newvar = MesquiteDouble.fromString(arguments, pos);
	 		double a = getupper_bound();
	 		System.out.println("command acknowlaged:"+newvar);
	 		if (!MesquiteDouble.isCombinable(newvar)) {
	 			newvar= MesquiteDouble.queryDouble(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Set mean_vel", "mean_vel of change:", a);
	 		}
	 		if (newvar>this.lower_bound && newvar!=a && MesquiteDouble.isCombinable(newvar)) {
	 			upper_bound=newvar;
	 		}
	 	}																																							
		else if (checker.compare(this.getClass(), "Sets the instantaneous trait_var of change in the model", "[trait_var of change; must be > 0]", commandName, "setbound_type")) {
	 		//pos.setValue(0);
	 		String newvar = arguments;
	 		System.out.println("command acknowlaged:"+newvar);
	 		setbounding_type(newvar);
	 	}
    	else
    		return  super.doCommand(commandName, arguments, checker);
		return null;
 	}
 
   	public double getmean_vel(){
   		return mean_vel;
   	}
 	public double getlower_bound(){
   		return lower_bound;
   	}
   	public double getupper_bound(){
   		return upper_bound;
   	}
	public List getbounding_list(){
   		return super.get_boundingtypes();
   	}
	public boolean getbounding(){
   		return use_bounds;
   	}
   	public boolean getspeciation(){
   		return ignore_length;
   	}
	public boolean getpunctuated(){
		return punctuated;
	}
	public void setbounding_type(String str){
   		List types;  		
   		types=get_boundingtypes();
   		for(int x=0;x<types.size();x++){
   			if (types.contains(str)){
   				bounding_type=str;
   			}
   		}
   	}
   	public String getbound_type(){
   		return bounding_type;
   	}
/*   	public void setlower_bound(double x){
   		lower_bound=x;
   	}
   	public void setupper_bound(double x){
   		upper_bound=x;
   	}   	
  	public void setnord_var(double x){
   		nord_var=x;
   	}*/
   	public double getnord_var(){
   		return nord_var;
   	}
   	public void setmean_var(double x){
   		mean_var=x;
   	}
   	public double getmean_var(){
   		return mean_var;
   	}
   	public double getmeanTipState(){
   		return meanTipState;
   	}
   	public void setroot_state(double x){
   		root_state=x;
   	}
   	public double getroot_state(){
   		return root_state;
   	}   	
   	public void setbounding(boolean a){
   		use_bounds=a;
   	}
   	public void setspeciation(boolean a){
   		ignore_length=a;
   	}
	public void setpunctuated(boolean a){
		punctuated=a;
	}
	public CharacterModel cloneModelWithMotherLink(CharacterModel formerClone){
		PDSIM_BrownianModel bmm = new PDSIM_BrownianModel(name, getStateClass());
		completeDaughterClone(formerClone, bmm);
		return bmm;
	}
 	/* copy information from this to model passed (used in cloneModelWithMotherLink to ensure that superclass info is copied); should call super.copyToClone(pm) */
	public void copyToClone(CharacterModel md){
		if (md == null || !(md instanceof PDSIM_BrownianModel))
			return;
		PDSIM_BrownianModel model = (PDSIM_BrownianModel)md;
		model.settrait_var(trait_var);
		super.copyToClone(md);
	}
	public boolean isFullySpecified(){
		return trait_var != MesquiteDouble.unassigned;
	}
	public void setSeed(long seed){
		seedSet = seed;
		rng.setSeed(seed);
	}
	
	public long getSeed(){
		return rng.nextLong();
	}
	
	/** return an explanation of the model. */
	public String getExplanation (){
		return "A stochastic model in which expected change is distributed normally with mean 0, and variance proportional to the trait_var. Branch Length is ignored.  The current trait_var is " + MesquiteDouble.toString(trait_var);
	}
	public String getNEXUSClassName(){
		return "PDSIM_Brownian";
	}
	public String getModelTypeName(){
		return "PDSIM_Brownian";
	}
	public int get_obj_num(){
		return 1;
	}
}
