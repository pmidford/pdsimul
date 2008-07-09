package mesquite.pdsim.PDAnova;

import mesquite.lib.ExtensibleDialog;
import mesquite.lib.MesquiteInteger;
import mesquite.lib.MesquiteTrunk;
import mesquite.lib.ParseUtil;

import java.awt.Checkbox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import mesquite.lib.Taxa;
import mesquite.cont.lib.ContinuousHistory;
import mesquite.pdsim.lib.*;
import java.util.*;

public class PDAnova implements ItemListener{
	Checkbox mx[][];
	int numOfTaxa=3;
	int numOfTraits=2;
	String[] groups;
	int numOfGroups=0;
	ContinuousHistory[][] sim;
	public void PDAnove(CoVarMatrixModel model){
		numOfTaxa=model.getNumTaxa();
		numOfTraits=model.getTraitNum();
		this.sim=model.sim;
	}
	public void runAnova(){
	String[] str=new String[numOfTaxa];
	
	for(int x=0;x<numOfTaxa;x++){
		str[x]=""+x;
	}
	MesquiteInteger buttonPressed=new MesquiteInteger(0);
	ExtensibleDialog menu = new ExtensibleDialog(MesquiteTrunk.mesquiteTrunk.containerOfModule(), "Analysis", buttonPressed);	
	//RadioButtons[] rb=new RadioButtons[numOfTaxa];

	mx=menu.addCheckboxMatrix(numOfTaxa, numOfTaxa, str, str);
	for(int x=0;x<numOfTaxa;x++){
		for(int y=0;y<numOfTaxa;y++){
				mx[x][y].addItemListener(this);
				mx[x][y].setName(""+y);
				mx[x][y].setState(false);
		}			
	}
	menu.completeAndShowDialog("OK",null,null,"OK");
	
	boolean isempty=true;
	
	for(int x=0;x<numOfTaxa;x++){
		isempty=true;
		for(int y=0;y<numOfTaxa;y++)
			if(mx[x][y].getState()) isempty=false;
		if (isempty) numOfGroups++;
	}
	//numOfGroups++;
	groups=new String[numOfGroups];

	int currentGroup=1;
	
	for(int x=0;x<numOfTaxa;x++){
		isempty=true;
		for(int y=0;y<numOfTaxa;y++)
			if(mx[x][y].getState()){
				isempty=false;
				groups[currentGroup]+=" "+y;
			}
		if (isempty) currentGroup++;
	}

	MesquiteInteger stringPos=new MesquiteInteger(0);

	for(int x=0;x<numOfGroups;x++){
		String GroupToken;
		stringPos.setValue(0);
		GroupToken=ParseUtil.getToken(groups[x], stringPos);
		while (GroupToken != null){
			currentGroup=MesquiteInteger.fromString(GroupToken);
			GroupToken=ParseUtil.getToken(groups[x], stringPos);
			if(!groups[0].contains(GroupToken)){
				groups[0]+=" "+GroupToken;
				System.out.print(" "+GroupToken);
			}
		}		
	}
	double sqrs[], SS[], fStat[];
	sqrs=new double[numOfGroups];
	SS=new double[numOfGroups];
	fStat=new double[numOfGroups];
	int numOfRuns=100;
	double AVE[][] = new double[numOfGroups*numOfTraits][numOfRuns];
	double MED[][] = new double[numOfGroups*numOfTraits][numOfRuns];
	double VAR[][] = new double[numOfGroups*numOfTraits][numOfRuns];
	double MIM[][] = new double[numOfGroups*numOfTraits*2][numOfRuns];
	double COR[][] = new double[numOfGroups*(numOfTraits*(numOfTraits-1))/2][numOfRuns];
	double F[][] = new double[numOfGroups][numOfRuns];
/*	Noname.AVE contains the subset means in the following order:
		1 - 2) Set 0 ("Overall") : first, second
		3 - 4) Set 1 ("first") : first, second
		5 - 6) Set 2 ("second") : first, second

		Noname.MED contains the subset medians in the following order:
		1 - 2) Set 0 ("Overall") : first, second
		3 - 4) Set 1 ("first") : first, second
		5 - 6) Set 2 ("second") : first, second

		Noname.VAR contains the subset variances in the following order:
		1 - 2) Set 0 ("Overall") : first, second
		3 - 4) Set 1 ("first") : first, second
		5 - 6) Set 2 ("second") : first, second

		Noname.MIM contains the subset minimums and maximums in the following order:
		1 - 4) Set 0 ("Overall") : first (min, max); second (min, max)
		5 - 8) Set 1 ("first") : first (min, max); second (min, max)
		9 - 12) Set 2 ("second") : first (min, max); second (min, max)

		Noname.COR contains the within-set correlations between traits for 
		1) Set 0 ("Overall") Between trait 1 and trait 2,
		2) Set 0 ("Overall") Between trait 1 and trait 3, . . .
		 .
		 .
		 .
		3) Set 1 ("first")
		.
		.
		.
		5) Set 2 ("second")

		Noname.F contains six F-ratios from ANOVA and ANCOVA in the following order:
		1) ANOVA of first
		2)   "   of second
		3) second as covariate in ANCOVA of first
		4) first(adjusted) in ANCOVA with second as covariate
		5) explained variance in ANCOVA of first with second as covariate
		6) difference between slopes

		Noname.LEV contains six F-ratios from Levene's tests:
		1) ANOVA of absolute values of deviations of Trait1 from within-group medians
		2)   "   "      "      "     "      "     "    "     "      "     "   means
		3)   "   "      "      "     "      "     "  Trait2  "      "     "   medians
		4)   "   "      "      "     "      "     "    "     "      "     "   means
		5)   "   "  absolute value of residuals of   first from within-group regressions on second
		6)   "   "  squared residuals of   first from within-group regressions on second
*/
	int node=0;
	for(int x=0;x<numOfRuns;x++){
		for(int y=0;y<numOfTraits;y++){
			for(int z=0; z<numOfGroups; z++){
				String nodeToken;
				stringPos.setValue(0);
				nodeToken=ParseUtil.getToken(groups[z], stringPos);
				double State;
				double sum=0;
				double ss=0;
				int num=0;
				double min=0, max=0;
				double[] setOfValues=new double[numOfTaxa];
				while (nodeToken != null){
					node=MesquiteInteger.fromString(nodeToken);
					State=sim[x][y].getState(node);
					if(num==0) {min=State;max=State;}
					else {
						if (State>max)
							max=State;
						if (State<min)
							min=State;
					}
					sum+=State;
					ss+=State*State;
					setOfValues[num]=State;
					num++;
				    nodeToken=ParseUtil.getToken(groups[z], stringPos);
				}
				double[] sizedSetOfValues=new double[num];
				for (int w=0;w<num;w++){
					sizedSetOfValues[w]=setOfValues[w];
				}
				Arrays.sort(sizedSetOfValues);
				AVE[z*numOfGroups+y][x]=sum/num;

				if (MesquiteInteger.isDivisibleBy(num,2)){
					MED[z*numOfGroups+y][x]=(sizedSetOfValues[num/2]+sizedSetOfValues[(num/2)+1])/2;
				}
				else MED[z*numOfGroups+y][x]=(sizedSetOfValues[(int)(num/2)+1]);

				VAR[z*numOfGroups+y][x]=sum/(double)(num)-ss/(double)(num);
				MIM[z*numOfGroups*2+y][x]=min;
				MIM[z*numOfGroups*2+y+1][x]=max;
			}
		}
	}
	node=0;
	for(int x=0;x<numOfRuns;x++){
		for(int y=0;y<numOfTraits;y++){
			for(int w=y+1;w<numOfTraits;w++){
				
				for(int z=0; z<numOfGroups; z++){
				String nodeToken;
				stringPos.setValue(0);
				nodeToken=ParseUtil.getToken(groups[z], stringPos);
				double State1, State2;
				double ss=0;
				int num=0;
				double min=0, max=0;
				double[] setOfValues=new double[numOfTaxa];
	
				while (nodeToken != null){
					node=MesquiteInteger.fromString(nodeToken);
					State1=sim[x][y].getState(node);
					State2=sim[x][w].getState(node);
					ss=((State1-AVE[z*numOfGroups+y][x])*(State2-AVE[z*numOfGroups+w][x]));
					nodeToken=ParseUtil.getToken(groups[z], stringPos);
					num++;
				}
				COV[(z*numOfGroups)*?+y*?+w][x]=ss/(num*Math.sqrt(VAR[z*numOfGroups+y][x])*Math.sqrt(VAR[z*numOfGroups+w][x]));
			}
		}
	}	
}
	public void itemStateChanged(ItemEvent e){
		Checkbox temp=(Checkbox)e.getItemSelectable();
		System.out.println("event");
		int y=MesquiteInteger.fromString(temp.getName());
		System.out.println("y:"+y);
		for (int x=0;x<numOfTaxa;x++){
			System.out.println("x:"+x);
			if (mx[x][y].getState()){
				mx[x][y].setState(false);
			}
		}
		temp.setState(true);
	}
}
