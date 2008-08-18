package mesquite.pdsim.lib;

import mesquite.lib.FileElement;
import mesquite.lib.MesquiteFile;
import mesquite.lib.MesquiteModule;
import mesquite.lib.NexusBlock;
import mesquite.lib.ProgressIndicator;
import mesquite.lib.TaxaBlock;
import mesquite.lib.characters.CharacterData;
import mesquite.lib.characters.CharactersBlock; 

public class SIMDATAblock extends NexusBlock {
		CharacterData data = null;
		public SIMDATAblock(MesquiteFile f, MesquiteModule mb){
			super(f, mb);
		}
		public boolean getWritable(){
			if (data == null)
				return false;
			return data.getWritable();
		}
		public boolean contains(FileElement e) {
			return e != null && data == e;
		}
		public boolean mustBeAfter(NexusBlock block){ 
			if (block==null)
				return false;
			if (data!=null && block instanceof TaxaBlock) {
				return data.getTaxa() == ((TaxaBlock)block).getTaxa();
			}
			return (block.getBlockName().equalsIgnoreCase("TAXA"));
			
		}
		public String getBlockName(){
			return "SIMDATA";
		}
		public void setData(CharacterData data) {
			this.data = data;
		}
		public CharacterData getData() {
			return data;
		}
		public void written() {
			data.setDirty(false);
		}
		public String getName(){
			if (data==null)
				return "empty characters block";
			else
				return "Characters block: " + data.getName();
		}
		public void writeNEXUSBlock(MesquiteFile file, ProgressIndicator progIndicator){
			if (data==null)
				return;
			FileElement f=new FileElement();
			file.addFileElement(f);
			//if (data.getMatrixManager()!=null) {
			//	data.getMatrixManager().writeCharactersBlock(data, (CharactersBlock)this, file, progIndicator);
			//	data.resetChangedSinceSave();
			//}
		}
}
