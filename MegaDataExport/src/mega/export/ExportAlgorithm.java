package mega.export;

import mega.run.SystemLog;
import org.codehaus.jackson.node.*;
import com.mega.modeling.api.MegaCollection;
import com.mega.modeling.api.MegaObject;
import com.mega.modeling.api.MegaRoot;

public class ExportAlgorithm {
	MegaRoot megaroot;
	MegaCollection rootItems;
	ObjectNode aker_json_root_node;
	ArrayNode aker_json_root_nodes;
	JsonNodeFactory nodeFactory;
	// metafield of some attributes
	String shortName = "~Z20000000D60[Short Name]";
	String universalID = "~zAF92NYBLziR[GUID (Aker Solutions)]";
	String deprecated = "~L8F9cLYBLDeR[Deprecated (Aker Solutions)]";
	String selectable = "~CAF9gMYBLPhR[Selectable (Aker Solutions)]";
	String modification_date = "~610000000P00[Modification Date]";
	String modifier_name = "~c10000000P20[Modifier Name]";
	String editable = "~koQrQ9kvL98D[Editable (Aker Solutions)]";
	String owner = "~dmQrk7kvLX6D[Owner (Aker Solutions)]";
	public ExportAlgorithm(MegaRoot megaroot, MegaCollection rootItems) {
		this.megaroot = megaroot;
		this.rootItems = rootItems;
		nodeFactory = JsonNodeFactory.instance;
		aker_json_root_node = nodeFactory.objectNode();
		aker_json_root_nodes = nodeFactory.arrayNode();
	}

	public void run() {
		SystemLog.log("============Start collecting data=======");
		// items on top level
		for (MegaObject rootItem : rootItems) {
			try {
				// create objectnode of json, top level
				ObjectNode jsonNode_top = nodeFactory.objectNode();
				fillNodeContent_top(rootItem, jsonNode_top);
				// level 2 items
				MegaCollection megaobjItems_lv2 = ItemOperator.getSubItems(rootItem,megaroot);
				ArrayNode json_nodeArray_lv2 = nodeFactory.arrayNode();
				for (MegaObject megaobjItem_lv2 : megaobjItems_lv2) {
					ObjectNode jsonNode_lv2 = nodeFactory.objectNode();
					// add content to json node
					fillNodeContent_level_2(megaobjItem_lv2, jsonNode_lv2);

					// recursive explore the item/////////////////////////
					explore_recursive(megaobjItem_lv2, jsonNode_lv2);

					// add the node to array lv2
					json_nodeArray_lv2.add(jsonNode_lv2);
				}
				jsonNode_top.put("TermSets", json_nodeArray_lv2);
				// ///////////level 2 end here//////////////////////////
				aker_json_root_nodes.add(jsonNode_top);// add to the top items
														// array

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void explore_recursive(MegaObject target_item, ObjectNode target_node) {
		// get subitems
		MegaCollection megaobjItems = ItemOperator.getSubItems(target_item,megaroot);
		if (megaobjItems.size() > 0) {
			ArrayNode json_nodeArray = nodeFactory.arrayNode();
			for (MegaObject megaobjItem : megaobjItems) {
				ObjectNode jsonNode = nodeFactory.objectNode();
				// add content to json node
				fillNodeContent_level_x(megaobjItem, jsonNode);
				// recursively deep down
				explore_recursive(megaobjItem, jsonNode);

				// add the node to array
				json_nodeArray.add(jsonNode);
			}
			// add those sub nodes to "target node"
			target_node.put("Terms", json_nodeArray);
		}
	}

	// json node level 1 content
	private void fillNodeContent_top(MegaObject megaobj, ObjectNode objnode) {
		objnode.put("Id", megaobj.getProp(universalID));
		objnode.put("Name", megaobj.getProp(shortName));
		objnode.put("Description", ItemOperator.get_ConceptDefinition(megaobj));
		objnode.put("SubjectArea", ItemOperator.get_SubjectArena(megaobj));
		objnode.put("LastModifiedDate", megaobj.getProp(modification_date));
		SystemLog.logNP("Top level item: " + megaobj.getProp(shortName));
	}

	// json node level 2 content
	private void fillNodeContent_level_2(MegaObject megaobj, ObjectNode objnode) {
		objnode.put("Id", megaobj.getProp(universalID));
		objnode.put("Name", megaobj.getProp(shortName));
		objnode.put("Description", ItemOperator.get_ConceptDefinition(megaobj));
		objnode.put("Group", ItemOperator.get_LibraryName_of_parentItem(megaobj));
		objnode.put("Owner", megaobj.getProp(owner));
		objnode.put("LastModifiedDate", megaobj.getProp(modification_date));
		objnode.put("ValidationStatus", ItemOperator.get_validationStatus(megaobj));
		objnode.put("ModifiedBy", megaobj.getProp(modifier_name));
		objnode.put("IsAvailableForTagging", (Integer.parseInt(megaobj
				.getProp(selectable)) == 1) ? true : false);
		objnode.put("IsDeprecated", (Integer.parseInt(megaobj
				.getProp(deprecated)) == 1) ? true : false);
		objnode.put("IsOpenForTermCreation", (myIntParser(megaobj.getProp(editable)) == 1) ? true : false);
	}
	
	// the content of json node of the rest of level
	private void fillNodeContent_level_x(MegaObject megaobj, ObjectNode objnode) {
		objnode.put("Id", megaobj.getProp(universalID));
		objnode.put("Name", megaobj.getProp(shortName));
		objnode.put("Description", ItemOperator.get_ConceptDefinition(megaobj));
		objnode.put("MemberOf", ItemOperator.get_ownerLibraryName(megaobj));
		objnode.put("Parent", ItemOperator.get_parentItemName(megaobj));
		objnode.put("Owner", megaobj.getProp(owner));
		objnode.put("LastModifiedDate", megaobj.getProp(modification_date));
		objnode.put("ModifiedBy", megaobj.getProp(modifier_name));
		objnode.put("ValidationStatus", ItemOperator.get_validationStatus(megaobj));
		objnode.put("IsAvailableForTagging", (Integer.parseInt(megaobj
				.getProp(selectable)) == 1) ? true : false);
		objnode.put("IsDeprecated", (Integer.parseInt(megaobj
				.getProp(deprecated)) == 1) ? true : false);
		objnode.put("Labels", ItemOperator.get_SynonymsSet(megaobj,nodeFactory));

	}
	//convert string to int
	private int myIntParser(String value) { 
		 try {  
		     return Integer.parseInt(value);  
		  } catch(Exception e) {  
		      // Log exception.
		      return 0;
		  }  
		}
}
