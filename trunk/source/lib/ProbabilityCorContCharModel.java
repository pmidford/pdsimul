//Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison. 
package mesquite.pdsim.lib;

import java.util.*;
//import java.awt.*;
import mesquite.stochchar.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.Taxa;
import mesquite.lib.Tree;
import mesquite.cont.lib.*;
import mesquite.lib.MesquiteBoolean;
/*==========================  Mesquite Basic Class Library ==========================*/
/*===  the basic classes used by the kernel of Mesquite (i.e. the trunk object) and available to the modules

/* ======================================================================== */
/** A character model for continuous characters to be used in stochastic simulations and in likelihood calculations.
Needs to include other methods, such as ones dealing with pdf for transitions*/
public abstract class ProbabilityCorContCharModel  extends ProbabilityContCharModel {
	
	public ProbabilityCorContCharModel (String name, Class dataClass) {
		super(name, dataClass);
	}
	public abstract boolean isBound();
	public abstract boolean isSpeciational();
	public abstract boolean isPunctuational();
	public abstract double getRootValue(int x);
	public abstract void setDefaults(Taxa taxa, MContinuousDistribution data, int ic);
	
	public abstract double[] evolveMultiState (double beginStates[], CorTrait model, double time, int node, int trait_num, boolean bound);
	//public abstract double bound (double beginState, double step);
	public static boolean bound_is_iterative(String bounding_type){
		if (bounding_type=="Replace" || bounding_type=="Combination" || bounding_type=="Throw") return true;
		else return false;
	}
	
	public List get_boundingtypes(){
		List bounding_types=new ArrayList();
		bounding_types.add("Truncation");
		bounding_types.add("Hard_Bounce");
		bounding_types.add("Flip");
		bounding_types.add("Replace");
		
		return bounding_types;
	}
	/**implements various bounding routiens all models can use*/
	public double bound (double beginState, double step, String bounding_type, double lower_bound, double upper_bound, MesquiteBoolean do_over){
		double endState=beginState+step;
		System.out.println("testing123");
		if(bounding_type.equalsIgnoreCase("Truncation")){
			System.out.println("bounding type truncation");
			if(endState<lower_bound){
				System.out.println("returning lower bound");
				return lower_bound;
			}
			else if (endState>upper_bound){
				System.out.println("returning upper bound");
				return upper_bound;
			}
			else return endState;
		}
		else if (bounding_type.equalsIgnoreCase("Hard_Bounce")){
			if(endState<lower_bound){
				return (beginState+lower_bound-endState);
			}
			if(endState>upper_bound){
				return (beginState-upper_bound+endState);
			}
			else return endState;		
		}
		else if (bounding_type=="Soft_Bounce"){
			return endState;		
		}
		else if (bounding_type.equalsIgnoreCase("Flip")){
			if (endState<lower_bound || endState>upper_bound){
				endState=beginState-step;
				if(endState<lower_bound || endState>upper_bound){
					bounding_type="Truncate";
					endState=bound(endState,step,bounding_type,lower_bound,upper_bound,do_over);
					bounding_type="Flip";
					return endState;
				}
				else return endState;
			}
			return endState;
		}
		else if (bounding_type.equalsIgnoreCase("Replace")){
			if (endState<lower_bound || endState>upper_bound){
				do_over.setValue(true);
				return endState;
			}
			else return endState;
		}
		else if (bounding_type=="Combination"){
			//random 
		}
		return endState;
	}
	/**calculates the sum of all branchlengths in the tree*/
	public static double findspan(Tree tree, int node, boolean ignore_length, double hieght, double span){
		//double ht=0;//what is ht?
		if (node!=tree.getRoot()){
			if (!ignore_length){
				hieght=hieght+tree.getBranchLength(node,1);
			}
			else hieght=hieght+1;
		}
		for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter)){
			span=findspan(tree, daughter, ignore_length, hieght, span);
		}
		if(tree.numberOfDaughtersOfNode(node)==0){
			span=span+hieght;
		}
		//System.out.println("inside_span:"+span);
		return span;
	}
	/**This procedure finds the k term which is the sumamation of the following
	for each branch (k=number of tips descending from the branch):
	             k*(k-1)*branchlength.
	Note that the branchlength is assumed to be 1 for speciational and
	for punctuated equilibrium models.*/
	public static double findkterm(Tree tree, int node, boolean ignore_length, double kterm){
		int tips;
		tips=tree.numberOfTerminalsInClade(node);
		//System.out.println("tips:"+tips);
		if(node!=tree.getRoot()){
			if(!ignore_length)
				kterm=kterm+tips*(tips-1)*tree.getBranchLength(node,1);
			else
				kterm=kterm+tips*(tips-1);
		}
		for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter)){
			kterm=findkterm(tree, daughter, ignore_length, kterm);
		}		
		//System.out.println("insdie_kterm:"+kterm);
		return kterm;
	}
	
	public static double getNordheim(Tree tree, boolean ignore_length){
			double kterm=0,span=0;
			span=findspan(tree, tree.getRoot(), ignore_length, 0, span);
			kterm=findkterm(tree, tree.getRoot(), ignore_length, kterm);
			//System.out.println("span:"+span);
			//System.out.println("kterm:"+kterm);
			int tips=tree.numberOfTerminalsInClade(tree.getRoot());
			double nordheim=Math.sqrt(span/tips-kterm/(tips*tips-tips));
			return nordheim;
		}
	public abstract void initialize(Tree tree);
	public abstract boolean isInitialized();
	
		
/*		  procedure findspan(gradual: integer; rt,nd:nodeptr;
          ht:real; var span:real);
begin
if(nd<>rt) then
if (gradual=0) then
ht:=ht+nd^.length
else
ht:=ht+1;
if(nd^.sibl<>nil) then
begin
findspan(gradual, rt, nd^.sibL, ht, span);
findspan(gradual, rt, nd^.sibR, ht, span);
end
else
span:=span+ht;
end;

begin
if p.root <> nil then
begin
span:=0;
kterm:=0;
ht:=0;
findkterm(p.gradual, p.root, p.root, kterm);
findspan(p.gradual, p.root, p.root, ht, span);
number:=p.tipnumber;
p.height:=span/number;  {Average height of tree.}
p.nordheim:=sqrt((span/number - kterm/(number*(number - 1.0))));
{This is what the Nordheim number equals}
end
else
p.nordheim:=0.0;
end;*/
//	public abstract double gettrait_var();
//	public abstract void setnord_var(double nord_var);

	public double getchangevelocity(double time, double rng, double var, double vel){
		if (time>0)
			return rng*var/Math.sqrt(time)+vel;
		else return 0;
	}
	/** Returns the number of doubles need by the model for each node*/ 
	public abstract int get_obj_num();

	/** Randomly generates according to model an end state on branch from beginning states*/
	public abstract double getRootState (Tree tree);
	
	/** Randomly generates according to model an ancestral state for root of tree*/
	public CharacterState getRootState (CharacterState state, Tree tree){
		if (state==null || !(state instanceof ContinuousState))
			state = new ContinuousState();
		((ContinuousState)state).setValue(0,getRootState(tree));
		return state;
	}	
}