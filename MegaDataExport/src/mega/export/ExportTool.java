package mega.export;

import java.io.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import mega.run.SystemLog;

public class ExportTool {

	public static void exportJson(ArrayNode aker_item_root_nodes,
			String output_filePath) {

		try {
			SystemLog.log("============Start exporting data=======");
			// mapper used to output node object
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			// write json to file
			//if(aker_item_root_nodes.size()>1){
				writer.writeValue(new File(output_filePath), aker_item_root_nodes);				
			//}else {
				//writer.writeValue(new File(output_filePath), aker_item_root_nodes.get(0));			
			//}			
			SystemLog.log("============Data Exported=============");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
