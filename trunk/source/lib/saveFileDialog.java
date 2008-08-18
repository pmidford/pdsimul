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
