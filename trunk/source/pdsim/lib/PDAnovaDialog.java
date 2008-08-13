package mesquite.pdsim.lib;

import java.awt.Checkbox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import mesquite.lib.ExtensibleDialog;

public class PDAnovaDialog extends ExtensibleDialog {
	public PDAnovaDialog (Object parent, String title) {
		super(parent,title);
	}
		Label[] Labels;
		public Checkbox[][] addPageFlipingCheckboxMatrix(int numColumns, int numRows, String[] columnLabels, String[] rowLabels) {
			Checkbox[][] textFields = new Checkbox [numColumns][numRows]; 

			GridBagLayout gridBag = new GridBagLayout();
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridwidth=1;
			constraints.gridheight=1;
			constraints.fill=GridBagConstraints.BOTH;

			Panel newPanel = new Panel();
			newPanel.setLayout(gridBag);
			gridBag.setConstraints(newPanel,constraints);
			constraints.gridy = 1;

			Labels=new Label[numColumns];
			
			if (columnLabels!=null){
				for (int i = 0; i<numColumns && i<columnLabels.length; i++) {
					constraints.gridx=i+2;
					Labels[i]=new Label(columnLabels[i]);
					newPanel.add(Labels[i],constraints);
				}
			}

			for (int j = 0; j<numRows; j++) {
				constraints.gridy=j+2;
				constraints.gridx=1;

				newPanel.add(new Label(rowLabels[j]),constraints);
				for (int i = 0; i<numColumns; i++) {
					constraints.gridx=i+2;
					textFields[i][j] = new Checkbox("",null, true);
					newPanel.add(textFields[i][j],constraints);		
				}
			}

			addNewDialogPanel(newPanel);
			return textFields;
		}
		public Label[] getLabels() {
				return Labels;
		}
}

