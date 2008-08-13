package mesquite.pdsim.lib;

import java.util.Random;

import mesquite.categ.lib.CategoricalData;
import mesquite.categ.lib.CategoricalState;
import mesquite.cont.lib.*;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteProject;
import mesquite.lib.StringUtil;
import mesquite.lib.Tree;
import mesquite.lib.characters.CharacterModel;
import mesquite.lib.characters.CharacterState;
import mesquite.stochchar.lib.ProbabilityContCharModel;
import mesquite.parsimony.lib.*;

//EditingCurator might be good to implement.

public class CorTrait {
	long seed=System.currentTimeMillis();
	Random rng = new Random(seed);
	double CoVar[][];
	double cholesky[][];
	double results[][];
	int num_of_traits;
	int num_of_nodes=0;
	boolean initialized = false;
	double eigenvalues[];
	MesquiteProject project;
	
	public void newfunction(int x){
		
	}
	public CorTrait (int x){
		num_of_traits=x;
		CoVar = new double[num_of_traits][num_of_traits];
		cholesky = new double[num_of_traits][num_of_traits];

		//results = new double[num_of_traits];
		eigenvalues=new double [num_of_traits];
		for (int z=0;z< num_of_traits; z++){
			for (int w=0;w<num_of_traits;w++){
				CoVar[z][w]=0;
				cholesky[z][w]=0;
			}
//			results[z]=0;
			eigenvalues[z]=0;
		}
	}	
	/*CorTrait(int x, long y){
		num_of_traits=x;
		CoVar = new double[num_of_traits][num_of_traits];
		cholesky = new double[num_of_traits][num_of_traits];

		results = new double[num_of_traits];
		eigenvalues=new double [num_of_traits];
		for (int z=0;z< num_of_traits; z++){
			for (int w=0;w<num_of_traits;w++){
				CoVar[z][w]=0;
				cholesky[z][w]=0;
			}
			results[z]=0;
			eigenvalues[z]=0;
		}
		rng.setSeed(y);
	}*/
	public void setNodeNum(int z){
		num_of_nodes=z;
		results = new double[num_of_nodes][num_of_traits];
	}
	public void make_traits(){
		double GaussianVector[][] = new double[num_of_nodes][num_of_traits];
		if(valid_CoVar()){
			if (!initialized){
				get_cholesky();		//the cholesky decomposition should provide for fast calculation of covaring traits.	
				initialized=true;
			}
			for (int z=0;z<num_of_nodes;z++){
				for (int x=0;x<num_of_traits;x++){
					GaussianVector[z][x]=rng.nextGaussian();
					results[z][x]=0;
				}
			}
			for (int z=0;z<num_of_nodes;z++){
				for (int x=0;x<num_of_traits;x++){
					for (int y=0;y<(x+1);y++){
						results[z][x]=results[z][x]+GaussianVector[z][y]*cholesky[y][x];
					}
				}
			}
		}
	}
	
	public void get_cholesky(){		
		for (int x=0;x<num_of_traits;x++){
			for (int y=x;y<num_of_traits;y++){
				if (x==y){
					cholesky[x][y]=0;
					//cholesky[0][0]=(CoVar[0][0]);
					if (x > 0) for (int z=0; z<(x); z++) cholesky[x][y]=cholesky[x][y]+cholesky[z][x]*cholesky[z][x]; //I'm not sure if the if statment is neccisary here, since
					cholesky[x][y]=Math.sqrt(CoVar[x][y]-cholesky[x][y]);							// I don't know how java handels the for loop.
				}
				else{
					cholesky[x][y]=0;
					if (x > 0) for (int z=0;z<(x);z++) cholesky[x][y]=cholesky[x][y]+cholesky[z][x]*cholesky[z][y];
					cholesky[x][y]=(CoVar[x][y]-cholesky[x][y])/cholesky[x][x];
				}
			}
		}
//		WHOOPS! I just computed the wrong half of the cholesky, and I have to transpose it. This needs changed.
/*		for (int x=0;x<num_of_traits;x++){
			for (int y=0;y<x;y++){
				cholesky[y][x]=cholesky[x][y];
			//	if(x!=y) cholesky[x][y]=0;
			}
		}*/
//		for (int x=0;x<num_of_traits;x++){
//			for (int y=0;y<num_of_traits;y++){
//				CoVar[x][y]=cholesky[x][y];
//				//cholesky[x][y]=0;
//			}
//		}		
	}

	public boolean valid_CoVar(){
		// is the matrix a positive-semi-definite symmetric matrix?
		// symetry is simply [x][y]=[y][x];
		// postive semi-definite means all eigenvalues are nonnegative.
		// later I use a chomesky decomposition which requires a positive definite matrix, so I should
		// prolly put on more (ge)stringent requisits.
		//TODO: currently just testing symetry, EigenAnalysis doesn't seem to work.
		
		//for (int z=0;z< num_of_traits; z++){
		//	for (int w=0;w<num_of_traits;w++){
		//		if (CoVar[z][w]<0 || CoVar[z][w]!=CoVar[w][z]) return false; 		
		//	}
		//}
//		EigenAnalysis test = new EigenAnalysis(CoVar,false,false,false);
//		eigenvalues=test.getEigenvalues();
//		for (int x=0;x<num_of_traits;x++) if (eigenvalues[x]<0)return false;
		return true;
	}
	
	public double get_num(int node, int trait){
		if (initialized){
			//return 2;
			return results[node][trait];
		}
		else return -1; 
	}
	public void setSeed(long x){
		seed=x;
		rng.setSeed(x);
	}
	public long getSeed(){
		return seed;
		//	return rng.;
	}
	
	public void setTraitNum(int x){
		initialized=false;

		num_of_traits=x;
		CoVar = new double[num_of_traits][num_of_traits];
		cholesky = new double[num_of_traits][num_of_traits];

		results = new double[num_of_traits][num_of_nodes];
		eigenvalues=new double [num_of_traits];
		for (int z=0;z< num_of_traits; z++){
			for (int w=0;w<num_of_traits;w++){
				CoVar[z][w]=0;
				cholesky[z][w]=0;
			}
			CoVar[z][z]=1;
//			results[z]=0;
			eigenvalues[z]=0;
		}		
		
	}
	public int getTraitNum(){
		return num_of_traits;
	}
	public void setTransitionValue(int x, int y, MesquiteNumber z, boolean a){
		CoVar[x][y]=z.getDoubleValue();		
	}
	public void setTransitionValue(int x, int y, double z, boolean a){
		CoVar[x][y]=z;

	}
	public double getCorCell(int x, int y){
		 return CoVar[x][y];
	}
	public double getCholCell(int x, int y){
		 return cholesky[x][y];
	}
	
	public MesquiteNumber getTransitionValue(int x, int y, MesquiteNumber z){
		z.setValue(CoVar[x][y]);
		return z;
	}
	public void getTransitionValue(int x, int y, MesquiteNumber z, boolean a){
		z.setValue(CoVar[x][y]);
//		return z;
	}

	public String getStateSymbol(int state){
		return Integer.toString(state);
	}
	public String toString(int x, int y){
		String str = Double.toString(CoVar[x][y]);
		return str;
	}
	public String getModelName(int x){
		String str = "The One";
		return str;
	}
	public void run_sim(){
		
	}
} 
