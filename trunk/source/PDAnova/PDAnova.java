package mesquite.pdsim.PDAnova;

import mesquite.lib.*;

import java.awt.Checkbox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowListener;

import mesquite.lib.Taxa;
import mesquite.cont.lib.ContinuousHistory;
import mesquite.pdsim.lib.*;
import mesquite.cont.lib.*;
import java.util.*;
//import java.awt.Button;
import java.awt.*;
import java.awt.event.*;

//import mesquite.lib.duties.*;

public class PDAnova extends MesquiteModule {
	int page=0,maxPage=0;

	public Class getDutyClass(){
		return PDAnova.class;
	}
	//Checkbox mx[][];
	int numOfTaxa=3;
	int numOfTraits=2;
	//Vector metaGroup;
	int numOfGroups=0;
	int numOfRuns=0;
	Vector[] group;
	public int[] nodeVector;
	//public String[] nodeNameVector;
	double AVE[][]; 
	double MED[][]; 
	double VAR[][]; 
	double MIM[][]; 
	//TODO: COR is too large, but I don't think I can specify something smaller.
	double COR[][][]; 
	double F[][];
	boolean cancel=false;
	String[] RowTitle;//=new String[];
	
	ContinuousHistory[][] sim;
	
	public PDAnova(){
	}
	
	public PDAnova(CoVarMatrixModel model, ContinuousHistory[][] sim, int numOfRuns, String[] RowTitle){
		if(model==null||sim==null||RowTitle==null){
			ExtensibleDialog PopUp=new ExtensibleDialog(module, "Error Dialog");
			PopUp.addLabel("Warning: Statistical test started with insufficent parameters.");
			if(model==null) PopUp.addLabel("No CoVarMatrixModel");
			if(sim==null) PopUp.addLabel("No ContinuousHistory");
			if(numOfRuns>0) PopUp.addLabel("Silly number of runs specifided");
			if(RowTitle==null) PopUp.addLabel("No Taxon labels");
			PopUp.completeAndShowDialog();
			cancel=true;
		}
		
		numOfTaxa=model.getNumTaxa();
		numOfTraits=model.getTraitNum();
		this.sim=sim;
		this.numOfRuns=numOfRuns;
		this.RowTitle=RowTitle;
		nodeVector=new int[numOfTaxa];
			nodeVector=PDAnova.getTipNodeVector(nodeVector, model.getTree(), model.getTree().getRoot(), 0);
			//nodeVector=PDAnova.getTipNameVector(nodeVector, model.getTree(), model.getTree().getRoot(), 0);
		
			//for(int x=0; x<numOfTaxa;x++){
			//	System.out.println(nodeVector[x]);
			//}
			//TODO: I don't know why this line of code doesn't return the right nodes.
			//model.gettreeTask().getTree(model.getTaxa(),0).getTerminalTaxa(model.gettreeTask().getTree(model.getTaxa(),0).getRoot());

		//return this;
	}

	public void runAnova(){
	PDAnovaMenu menu=new PDAnovaMenu(this,"Analysis",numOfTaxa,RowTitle);
	boolean isempty=true;
	
	//System.out.println("111");
	for(int y=0;y<menu.maxGroupNum;y++){
		isempty=true;
		for(int x=0;x<numOfTaxa;x++)
			if(menu.state[x][y]) isempty=false;
		if (!isempty) numOfGroups++;
	}
	numOfGroups++;
	
	if(numOfGroups==1){
		ExtensibleDialog PopUp=new ExtensibleDialog(module, "Error Dialog");
		PopUp.addLabel("Warning: all groups were empty, no analysis performed.");
		PopUp.completeAndShowDialog();
		
	}
	//groups;
	//Vector group=new Vector();
//	metaGroup= new Vector();
	group=new Vector[numOfGroups];
	//groups=new String[numOfGroups];
	
	//System.out.println("120: numOfGroups"+numOfGroups);
	for(int x=0;x<numOfGroups;x++)
		group[x]=new Vector();
	int currentGroup=1;
	for(int y=0;y<menu.maxGroupNum;y++){
		isempty=true;
		for(int x=0;x<numOfTaxa;x++){
			//System.out.print("x,y:"+x+","+y+" ");
			if(menu.state[x][y]){
				isempty=false;
				//System.out.println("adding element?");
				//System.out.println("Adding Taxon "+x+" to group "+currentGroup);
				group[currentGroup].addElement(new MesquiteInteger(x));
			}
		}
		if (!isempty) currentGroup++;
	}
	
	
	//System.out.println("133");
	//TODO: this is slow, but a faster method skipped groups, try to fix in future.
	for(int x=1;x<numOfGroups;x++){
		for (int y=0;y<group[x].size();y++){
			boolean addme=true;
			int temptaxon;
			temptaxon=((MesquiteInteger)(group[x].get(y))).getValue();
			for (int z=0;z<group[0].size();z++)
				if(temptaxon==((MesquiteInteger)(group[0].get(z))).getValue()) addme=false;
			if(addme) group[0].add(group[x].get(y));
		}
	}	

	if(group[0].size()==0){
		ExtensibleDialog PopUp=new ExtensibleDialog(module, "Error Dialog");
		PopUp.addLabel("Warning: all groups were empty, no analysis performed.");
		PopUp.completeAndShowDialog();
		cancel=true;
	}
	if(!cancel){	
	//System.out.println("143");
	//int numOfRuns=;
	AVE = new double[numOfGroups*numOfTraits][numOfRuns];
	MED = new double[numOfGroups*numOfTraits][numOfRuns];
	VAR = new double[numOfGroups*numOfTraits][numOfRuns];
	MIM = new double[numOfGroups*numOfTraits*2][numOfRuns];
	//TODO: COR is too large, but I don't think I can specify something smaller.
	COR = new double[(numOfTraits*(numOfTraits+1))/2][numOfGroups][numOfRuns];
	F = new double[numOfTraits][numOfRuns];
	
	
	//System.out.println("206");
	Vector[][] DataVector=new Vector[numOfTraits][numOfGroups];
	for(int x=0;x<numOfGroups;x++){
		for(int y=0;y<numOfTraits;y++){
			DataVector[y][x]=new Vector();
		}
	}
	
	for(int x=0;x<numOfRuns;x++){
		//System.out.println("Building data vector");
		for(int y=0;y<numOfTraits;y++){
			for(int z=0;z<numOfGroups;z++){
				//System.out.println("clearing vector");					
				DataVector[y][z].clear();
			}
			for(int z=0;z<numOfGroups;z++)
				for (int a=0;a<group[z].size();a++){
					//System.out.println("x,y,z,a:"+x+","+y+","+z+","+a);
					DataVector[y][z].add(new MesquiteDouble((sim[x][y].getState(nodeVector[((MesquiteInteger)group[z].get(a)).getValue()]))));
				}
		}
		for(int y=0;y<numOfTraits;y++){
			for(int z=0; z<numOfGroups; z++){
				System.out.println("geting Mean");
				AVE[z*numOfTraits+y][x]=PDAnova.getMean(DataVector[y][z]);	
				System.out.println("geting Median");
				MED[z*numOfTraits+y][x]=PDAnova.getMedian(DataVector[y][z]);
				System.out.println("geting Variance");
				VAR[z*numOfTraits+y][x]=PDAnova.getVar(DataVector[y][z]);
				System.out.println("geting Min");
				MIM[z*numOfTraits*2+y*2][x]=PDAnova.getMin(DataVector[y][z]);
				System.out.println("geting Max");
				MIM[z*numOfTraits*2+y*2+1][x]=PDAnova.getMax(DataVector[y][z]);
				System.out.println("geting Correlation");
				for(int w=0;w<numOfTraits;w++){
					COR=setCor(COR,numOfTraits,y,w,z,x, PDAnova.getCor(DataVector[y][z], DataVector[w][z]));
				}
			}
			System.out.println("geting F Statistic");
			Vector[] temp=new Vector[numOfGroups-1];
			for(int q=0;q<numOfGroups-1;q++){
				temp[q]=new Vector();
			}
			for(int q=0;q<numOfGroups-1;q++){
				for(int r=0;r<DataVector[y][q+1].size();r++){
					temp[q].add(DataVector[y][q+1].get(r));
				}
				System.out.println("temp[q]:"+temp[q].size());
			}
				F[y][x]=PDAnova.getFStat(temp);
			}
		}
	}
}
	public void dispose(){
		//mx=null;
		//for(int x=0;x<numOfGroups;x++){
		//	group[x]=null;
		//}
		/*int[] nodeVector;
		double AVE[][]; 
		double MED[][]; 
		double VAR[][]; 
		double MIM[][]; 
		//TODO: COR is too large, but I don't think I can specify something smaller.
		double COR[][][]; 
		double F[][];
		String[] RowTitle;//=new String[];*/
		
	}
	public static double[][][] setCor(double[][][] COR,int numOfTraits, int trait1, int trait2, int group, int run, double setto){
		int temp;
		if(trait1>trait2){
			temp=trait2;
			trait2=trait1;
			trait1=temp;
		}
		temp=trait1*(2*numOfTraits-trait1*trait1)/2; //(+traits2)
		COR[temp+trait2][group][run]=setto;
		return COR;
	}
	public static double getCor(double[][][] COR,int numOfTraits, int trait1, int trait2, int group, int run){
		int temp;
		if(trait1>trait2){
			temp=trait2;
			trait2=trait1;
			trait1=temp;
		}
		if(trait1<numOfTraits&&trait1>=0&&trait2<numOfTraits&&trait2>=0){
			temp=trait1*(2*numOfTraits-trait1*trait1)/2; //(+traits2)
			return COR[temp+trait2][group][run];
		}
		else return MesquiteDouble.unassigned;
	}
	public double[][] getAVE(){
		return AVE;
	}
	public double[][] getMED(){
		return MED;
	}
	public double[][][] getCOR(){
		return COR;
	}
	public double[][] getMIM(){
		return MIM;
	}
	public double[][] getF(){
		return F;
	}
	public double[][] getVAR(){
		return VAR;
	}
	public int getNumOfGroups(){
		return numOfGroups;
	}
	public Vector[] getGroups(){
		return group;
	}
	

	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
		return true;
	}
	public String getName(){
		return "PDAnova";
	}
	static public int[] getTipNodeVector(int nodevector[],Tree tree,int node, int count){
		//System.out.println("node:"+node+" count: "+count);
		if(tree.numberOfDaughtersOfNode(node)==0){
			while (nodevector[count]!=0) count++;
			nodevector[count]=node;
		}
		int daughters[], num_of_daughters;
		daughters=tree.daughtersOfNode(node);
		num_of_daughters=tree.numberOfDaughtersOfNode(node);
		for (int x=0;x<num_of_daughters;x++){
			getTipNodeVector(nodevector, tree, daughters[x], count);
		}	
		return nodevector;
	}
	static public String[] getTipNameVector(String nodevector[],Tree tree,int node, int count){
		//System.out.println("node:"+node+" count: "+count);
		if(tree.numberOfDaughtersOfNode(node)==0){
			while (nodevector[count]!=null) count++;
			nodevector[count]=tree.getNodeLabel(node);
		}
		int daughters[], num_of_daughters;
		daughters=tree.daughtersOfNode(node);
		num_of_daughters=tree.numberOfDaughtersOfNode(node);
		for (int x=0;x<num_of_daughters;x++){
			getTipNameVector(nodevector, tree, daughters[x], count);
		}	
		return nodevector;
	}
	
	/**
	 * Performs an Anova test on a vector array
	 * @param Group an array of Vectors containing MesquiteDoubles 
	 * @param numOfGroups size of the vector array
	 * @return (double)F statistic.
	 */	static public double getFStat(Vector[] Group){
		double GrandMean=0, GroupMean[];
		int N=0;
		int numOfGroups=(Group.length);
		System.out.println("length:"+numOfGroups);
		GroupMean=new double[numOfGroups];
		
		for (int x=0;x<numOfGroups;x++){
			GroupMean[x]=0;
			for(int y=0;y<Group[x].size();y++){
				System.out.println(((MesquiteDouble)Group[x].get(y)).getValue());
				GroupMean[x]+=((MesquiteDouble)Group[x].get(y)).getValue();
			}
			GrandMean+=GroupMean[x];
			GroupMean[x]=GroupMean[x]/Group[x].size();
			N+=Group[x].size();
		}
		System.out.println("N:"+N+", Sun:"+GrandMean);
		GrandMean=GrandMean/(double)(N);
		
		double SS=0, SST=0, SSE=0, df_e=0, df_t=0, MST=0,MSE=0;
		
		for (int x=0;x<numOfGroups;x++){
			//SS[x]=0;
			for(int y=0;y<Group[x].size();y++){
				SS+=Math.pow(((MesquiteDouble)Group[x].get(y)).getValue()-GrandMean,2);
				df_e++;
			}
			SST+=Math.pow(GroupMean[x]-GrandMean, 2)*Group[x].size();
			df_t++;
			df_e--;
		}
		df_t--;
		SSE=SS-SST;
		MSE=SSE/df_e;
		MST=SST/df_t;
		System.out.println("SS, SST, SSE, MSE, MST, df_e, df_t:"+SS+","+SST+","+SSE+","+MST+","+MSE+","+"*"+(MST/MSE)+"*"+df_e+","+df_t);
		
		return MST/MSE;
		
	}
	public static double getMean(Vector Group){
		double GroupMean=0;
		for(int y=0;y<Group.size();y++)
			GroupMean+=((MesquiteDouble)Group.get(y)).getValue();
		GroupMean=GroupMean/Group.size();
		return GroupMean;
	}
	public static double getMedian(Vector Group){
		double[] sortme=new double[Group.size()];
		for (int w=0;w<Group.size();w++){
			sortme[w]=((MesquiteDouble)Group.get(w)).getValue();
		}
		Arrays.sort(sortme);
	
		if (MesquiteInteger.isDivisibleBy(Group.size(),2)){
			return (double)(sortme[(Group.size()/2)-1]+sortme[Group.size()/2])/(double)2;
		}
		else return (sortme[(int)(Group.size()/2)]);		
	}
	public static double getMin (Vector Group){
		double min=((MesquiteDouble)Group.get(0)).getValue();
		//double value;
		for (int x=1;x<Group.size();x++){
			if (((MesquiteDouble)Group.get(x)).getValue()<min)
				min=((MesquiteDouble)Group.get(x)).getValue();
		}
		return min;
	}
	public static double getMax (Vector Group){
		double max=((MesquiteDouble)Group.get(0)).getValue();
		//double value;
		for (int x=1;x<Group.size();x++){
			if (((MesquiteDouble)Group.get(x)).getValue()>max)
				max=((MesquiteDouble)Group.get(x)).getValue();
		}
		return max;
	}
	public static double getVar (Vector Group){
		double GroupMean;
		if (Group.size()<2)
			return MesquiteDouble.unassigned;
		GroupMean=PDAnova.getMean(Group);
		double SS=0;		
		for(int y=0;y<Group.size();y++)
			SS+=Math.pow(((MesquiteDouble)Group.get(y)).getValue()-GroupMean,2);	
		return SS/(double)(Group.size()-1);
		
	}
	public static double getCor (Vector Group1, Vector Group2){
			
			double State1, State2, Group1Mean, Group2Mean;
			Group1Mean=PDAnova.getMean(Group1);
			Group2Mean=PDAnova.getMean(Group2);
			
			double ss=0;
			if(Group1.size()!=(int)(Group2.size())){
				System.out.println("Error:Unequal group sizes for calculation in PDAnova.getCor");
				return MesquiteDouble.unassigned;
			}
			for(int Q=0;Q<Group1.size();Q++){
				State1=((MesquiteDouble)Group1.get(Q)).getValue();
				State2=((MesquiteDouble)Group2.get(Q)).getValue();
				//System.out.println(":"+(State1-Group1Mean)*(State2-Group2Mean));
				ss+=((State1-Group1Mean)*(State2-Group2Mean));
			}
			//System.out.println(Group1.toString());
			//System.out.println(Group2.toString());

			//System.out.println("ss="+ss);
			//System.out.println("var1="+PDAnova.getVar(Group1));
			//System.out.println("var2="+PDAnova.getVar(Group2));
			
			return ss/((double)(Group1.size()-1)*Math.sqrt(PDAnova.getVar(Group1))*Math.sqrt(PDAnova.getVar(Group2)));
	}
	public static Vector[] charDatatoGroupData(MContinuousDistribution charData, Vector[] Group1, int numOfGroups, int ic){
		//System.out.println("394");
		Vector[] newGroup=new Vector[numOfGroups];
		//System.out.println("395");
		for (int x=0;x<numOfGroups;x++){
			//System.out.println("396:"+x);
			newGroup[x]=new Vector();
			//System.out.println("397");
			for (int y=0;y<Group1[x].size();y++){
				//System.out.println("398:"+y);
				newGroup[x].add(new MesquiteDouble(charData.getState(ic, ((MesquiteInteger)Group1[x].get(y)).getValue(), 0)));
			}
		}
		return newGroup;
	}
 	/*public String getNameOfResultClass(){
 		return "boo";
 	}*/

}
class PDAnovaMenu extends ExtensibleDialog implements ItemListener, ActionListener {
	int maxGroupNum=5;
	int numOfTaxa=0;
	String[] ColumnTitles;
	String[] DisplayTitle;
	PDAnovaDialog menu;
	Checkbox[][] mx;
	int page=0;
	int maxPage=0;	
	int maxDisplay=15;
	boolean[][] state;
	Label[] names;
	public PDAnovaMenu(MesquiteModule module, String name, int numOfTaxa, String[] ColumnTitles){
		super(module, name);
		this.numOfTaxa=numOfTaxa;
		this.ColumnTitles=ColumnTitles;


		IntegerField maxGroupField=null;
	
		if(numOfTaxa>maxGroupNum){
				ExtensibleDialog maxGroup = new ExtensibleDialog(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Set Maximum Groups");
				maxGroupField=maxGroup.addIntegerField("Chose Maximum Number of Groups", 5, 3); //addTextFied("Chose Maximum number of groups", 5, 1);
				maxGroup.completeAndShowDialog();
				maxGroupNum=maxGroupField.getValue();
		}
		
		if(maxDisplay>numOfTaxa){
			maxDisplay=numOfTaxa;
		}
		
		if(maxGroupNum>numOfTaxa){
			maxGroupNum=numOfTaxa;
		}

		String[] RowTitle=new String[maxGroupNum];

		for(int x=0;x<maxGroupNum;x++){
			RowTitle[x]="Group "+(x+1);
		}

		DisplayTitle=new String[maxDisplay];
		
		for(int x=0;x<maxDisplay;x++){
			DisplayTitle[x]=ColumnTitles[x];
		}
		
		menu=new PDAnovaDialog(module, name);
		state=new boolean[numOfTaxa][maxGroupNum];
		
		for (int x=0;x<numOfTaxa;x++){
			for(int y=0;y<maxGroupNum;y++)
				state[x][y]=false;
		}
		
		mx=menu.addPageFlipingCheckboxMatrix(maxDisplay, maxGroupNum, DisplayTitle, RowTitle);
	
		names=menu.getLabels();

		maxPage=numOfTaxa/maxDisplay;

		if(numOfTaxa>maxDisplay){
			Button leftB,rightB;
			Panel left=menu.addNewDialogPanel();
			Panel right=menu.addNewDialogPanel();
			leftB=menu.addAButton("Scroll Left", left);
			rightB=menu.addAButton("Scroll Right", right);
			leftB.setName("leftB");
			rightB.setName("rightB");
			leftB.addActionListener(this);
			rightB.addActionListener(this);
		}
		
		//System.out.println("what the crap");	

		for(int x=0;x<maxDisplay;x++){
			for(int y=0;y<maxGroupNum;y++){
					mx[x][y].addItemListener(this);
					mx[x][y].setName(x+" "+y);
					mx[x][y].setState(false);
			}			
		}
		
		menu.completeAndShowDialog();
		//System.out.println("looks good!");	
	}
	public void itemStateChanged(ItemEvent e){
		System.out.println(e.paramString());
		System.out.println(e.toString());		
		System.out.println(e.getID());
		
		if(e.getItemSelectable() instanceof Checkbox ){
		//print(e.getID());
			Checkbox temp=(Checkbox)e.getItemSelectable();
			MesquiteInteger pos=new MesquiteInteger(0);
			int xloc=MesquiteInteger.fromString(ParseUtil.getToken(temp.getName(), pos));
			int yloc=MesquiteInteger.fromString(ParseUtil.getToken(temp.getName(), pos));
			System.out.println("x:"+xloc+",y:"+yloc);
			for (int y=0;y<maxGroupNum;y++){
				if (temp.getState()){
					if(!(temp.getName().equalsIgnoreCase(mx[xloc][y].getName()))){
						state[xloc+page*maxDisplay][y]=false;
						System.out.println("x:"+xloc+",y:"+y+" to false");
						mx[xloc][y].setState(false);
					}
				}
			}
			state[xloc+page*maxDisplay][yloc]=temp.getState();
		}
		menu.repaintAll();
		menu.repaint();
		//menu.r
		//if(MesquiteTrunk.mesquiteTrunk)
		menu.hide();
		menu.showDialog();
		//temp.setState(!temp.getState());
	}
	public void actionPerformed(ActionEvent e){
		System.out.println(e.paramString());
		System.out.println(e.toString());		
		System.out.println(e.getID());
		
		Object obj=e.getSource();
		Button r=(Button)obj;
		String str;
		str=r.getName();
		
		if(str.equalsIgnoreCase("leftB")){
			if(page!=0){
				System.out.println(page);
				page--;
				System.out.println(page);
				for(int x=0;x<maxDisplay;x++){
					for(int y=0;y<maxGroupNum;y++){
						if(x+page*maxDisplay<numOfTaxa){
							names[x].setText(ColumnTitles[x+page*maxDisplay]);
							mx[x][y].setState(state[x+page*maxDisplay][y]);
							mx[x][y].setVisible(true);
						}
						else{
							names[x].setText("");
							mx[x][y].setVisible(false);
						}
					}			
				}			
				menu.repaintAll();
				menu.repaint();
				menu.setFoci();
				menu.hide();
//				menu.showDialog();
				//Matrix #1 simulated by Evolve Continuous Charactersmenu.
			}
		}
		else if(str.equalsIgnoreCase("rightB")){
			if(page!=maxPage){
				System.out.println(page);
				page++;
				System.out.println(page);
				for(int x=0;x<maxDisplay;x++){
					for(int y=0;y<maxGroupNum;y++){
						if(x+page*maxDisplay<numOfTaxa){
							names[x].setText(ColumnTitles[x+page*maxDisplay]);
							mx[x][y].setState(state[x+page*maxDisplay][y]);
							mx[x][y].setVisible(true);
						}
						else{
							names[x].setText("");
							mx[x][y].setVisible(false);
						}
					}			
				}			
				menu.repaintAll();
				menu.repaint();
				menu.hide();
				menu.showDialog();
//				notifyListeners(this, new Notification(MesquiteListener.UNKNOWN));
			}	
		}
	}
}
