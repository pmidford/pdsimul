package mesquite.pdsim.lib;

import java.awt.Image;
import java.util.Vector;

import mesquite.lib.ClosablePanelContainer;
import mesquite.lib.ColorTheme;
import mesquite.lib.CommandChecker;
import mesquite.lib.MesquiteCommand;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteImage;
import mesquite.lib.MesquiteWindow;
import mesquite.minimal.BasicFileCoordinator.BasicFileCoordinator;
import mesquite.lib.ExtensibleDialog;
import mesquite.lib.MesquiteProject;
import mesquite.lib.MesquiteTrunk;
import mesquite.lib.duties.FileCoordinator;


public class saveFileDialog extends ExtensibleDialog {
		MesquiteFile mf;
		FileCoordinator bfc;
		Vector commands = new Vector();
		public saveFileDialog (Object parent, String title, FileCoordinator bfc) {
			super(parent,title);
			this.bfc=bfc;
			bfc.getProject().getHomeDirectoryName();
			
			Image im;
			//MesquiteFile.showDirectory(mf.getDirectoryName());

			im = 	MesquiteImage.getImage(bfc.getPath()+ "projectHTML" + MesquiteFile.fileSeparator + "fileSmall.gif");
			
		}
		/*.................................................................................................................*/
		public Object doCommand(String commandName, String arguments, CommandChecker checker) {
			if (checker.compare(this.getClass(), "Shows file location on disk", null, commandName, "show")) {
			}
			else
				return  super.doCommand(commandName, arguments, checker);
			return null;
		}

	}

	protected void addCommand(boolean menuOnly, String iconFileName, String label, String shortLabel, MesquiteCommand command){
		ElementCommand ec = new ElementCommand(menuOnly, iconFileName, label, shortLabel, command);
		if (iconFileName != null)
			ec.icon = MesquiteImage.getImage(bfc.getPath() + "projectHTML" + MesquiteFile.fileSeparator + iconFileName);
		commands.addElement(ec);
	}
}
class ElementCommand {
	boolean menuOnly;
	String iconFileName;
	String label;
	String shortLabel;
	MesquiteCommand command;
	Image icon=null;
	int left = -1;
	int right = -1;
	public ElementCommand(boolean menuOnly, String iconFileName, String label, String shortLabel, MesquiteCommand command){
		this.menuOnly =menuOnly;
		this.iconFileName =iconFileName;
		this.label =label;
		this.shortLabel =shortLabel;
		this.command =command;
	}
}
