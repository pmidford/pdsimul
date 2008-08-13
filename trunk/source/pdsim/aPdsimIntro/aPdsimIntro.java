package mesquite.pdsim.aPdsimIntro;

	import mesquite.lib.*;
	import mesquite.lib.characters.*;
	import mesquite.lib.duties.*;


	/* ======================================================================== */
	public class aPdsimIntro extends PackageIntro {
	/*.................................................................................................................*/
		public boolean startJob(String arguments, Object condition, boolean hiredByName) {
	 		return true;
	  	 }
	  	 public Class getDutyClass(){
	  	 	return aPdsimIntro.class;
	  	 }
		/*.................................................................................................................*/
	    	 public String getExplanation() {
			return "Serves as an introduction to the pdsim package, which implements models of corelated character evolution.";
	   	 }
	   
		/*.................................................................................................................*/
	    	 public String getName() {
			return "Pdsim Introduction";
	   	 }
		/*.................................................................................................................*/
		/** Returns the name of the package of modules (e.g., "Basic Mesquite Package", "Rhetenor")*/
	 	public String getPackageName(){
	 		return "Pdsim Package";
	 	}
		/*.................................................................................................................*/
		/** Returns citation for a package of modules*/
	 	public String getPackageCitation(){
	 		return "Matthew Ackerman.  2008.  pdsim: models of corelated character evolution.  A package of modules for Mesquite. version 1.5.";
	 	}
	 	
		/*.................................................................................................................*/
	  	 public String getPackageVersion() {
			return "0.5";
	   	 }
		/*.................................................................................................................*/
	  	 public String getPackageAuthors() {
			return "Matt Ackerman";
	   	 }
		/*.................................................................................................................*/
		/** Returns whether there is a splash banner*/
		public boolean hasSplash(){
	 		return true; 
		}
	}

