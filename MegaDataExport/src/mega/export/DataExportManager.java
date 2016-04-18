package mega.export;

import com.mega.modeling.api.*;

public class DataExportManager {
	MegaRoot megaroot;
	String output_filePath;
	final static String get_root_item_query = "Select [Item (Aker Solutions)] Where NOT [Parent (Aker Solutions)]";

	public DataExportManager(MegaRoot mroot, String filepath) {
		this.output_filePath = filepath;
		this.megaroot = mroot;
	}

	public void start() {
		// get the root items t
		MegaCollection rootAkerItems = megaroot
				.getSelection(get_root_item_query);
		
		// run export algorithm
		ExportAlgorithm exportAlgorithm = new ExportAlgorithm(megaroot,
				rootAkerItems);
		exportAlgorithm.run();
		
		// run export tool		
		ExportTool.exportJson(exportAlgorithm.aker_json_root_nodes,
				output_filePath);

	}

}
