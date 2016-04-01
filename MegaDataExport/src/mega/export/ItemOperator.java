package mega.export;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.mega.modeling.api.MegaCollection;
import com.mega.modeling.api.MegaObject;
import com.mega.modeling.api.MegaRoot;

public class ItemOperator {

	// // metafield of some attributes
	static String shortName = "~Z20000000D60[Short Name]";
	static String name = "~210000000900[Name]";
	static String absID = "~310000000D00[Absolute Identifier]";
	static String defintion_text = "~rsAxc0Fa5530[Definition Text]";

	// get sub items
	public static MegaCollection getSubItems(MegaObject megaobj,
			MegaRoot megaroot) {
		String absid = megaobj.getProp(absID);
		String query = "Select [Item (Aker Solutions)] Where [Parent (Aker Solutions)].[Absolute Identifier]=\""
				+ absid + "\"";
		MegaCollection subItems = megaroot.getSelection(query, 1, shortName);
		return subItems;
	}

	// item.concept definition text ( the Description attr in json)
	public static String get_ConceptDefinition(MegaObject megaobj) {
		// get concepts
		MegaCollection concepts = megaobj.getCollection("Concept");
		String definition = "";
		if (concepts.size() > 0) {
			definition = concepts.get(1).getProp(defintion_text, "Display")
					.toString();
			return definition;
		}
		return "";
	}

	// get validation status (the ValidationStatus attr in json)
	public static String get_validationStatus(MegaObject megaobj) {
		MegaCollection validationObjs = megaobj.getCollection("Validation");
		if (validationObjs.size() > 0) {
			MegaCollection workStatusObjs = validationObjs.get(1)
					.getCollection("Workflow Status");
			if (workStatusObjs.size() > 0) {
				return workStatusObjs.get(1).getProp(shortName);
			}
			return "";
		}
		return "";
	}

	// get owner library (the memberof attr in json)
	public static String get_ownerLibraryName(MegaObject megaobj) {
		MegaCollection owner_libraries = megaobj.getCollection("Owner Library");
		if (owner_libraries.size() > 0) {
			return owner_libraries.get(1).getProp(shortName);
		}
		return "";
	}

	// get owner library (the group attr in json)
	public static String get_LibraryName_of_parentItem(MegaObject megaobj) {
		MegaCollection parentItems = megaobj.getCollection("Parent (Aker Solutions)");
		if (parentItems.size() > 0) {
			MegaCollection owner_libraries = parentItems.get(parentItems.size()).getCollection("Owner Library");
			if (owner_libraries.size() > 0) {
				return owner_libraries.get(1).getProp(shortName);
			}
		}
		return "";
	}

	// get subject arena (item -> concept ->owner packager)
	public static String get_SubjectArena(MegaObject megaobj) {
		MegaCollection conceptCollection = megaobj.getCollection("Concept");
		if (conceptCollection.size() > 0) {
			MegaCollection subjectArenaCollection = conceptCollection.get(1)
					.getCollection("Owner Packager");
			if (subjectArenaCollection.size() > 0) {
				return subjectArenaCollection.get(1).getProp(shortName);
			}
		}
		return "";
	}

	// get short name of parent item (the parent attr in json)
	public static String get_parentItemName(MegaObject megaobj) {
		MegaCollection parentItemCollection = megaobj
				.getCollection("Parent (Aker Solutions)");
		if (parentItemCollection.size() > 0) {
			return parentItemCollection.get(1).getProp(shortName);
		}
		return "";
	}

	// get set of Synonyms (the Labels attr in json)
	public static ArrayNode get_SynonymsSet(MegaObject megaobj,
			JsonNodeFactory nodeFactory) {
		ArrayNode json_nodeArray = nodeFactory.arrayNode();// node array
		MegaCollection conceptObjs = megaobj.getCollection("Concept");
		if (conceptObjs.size() > 0) {
			MegaObject conceptObj = conceptObjs.get(1);// concept object
			MegaCollection synonyms = conceptObj.getCollection("Synonym");
			MegaCollection designationObjs = conceptObj
					.getCollection("Designation");
			if (designationObjs.size() > 0) {
				MegaObject designationObj = designationObjs.get(1);
				// ///////the content of the concept
				ObjectNode jsonNode_concept = nodeFactory.objectNode();
				jsonNode_concept.put("Language",
						designationObj.getCollection("Language").get(1)
								.getProp(name));
				jsonNode_concept
						.put("Value", designationObj.getProp(shortName));
				jsonNode_concept.put("IsDefaultForLanguage", true);
				// add the node to array
				json_nodeArray.add(jsonNode_concept);
				// add synonyms node
				if (synonyms.size() > 0) {
					// the synonyms relate to this concept
					for (MegaObject synonymObj : synonyms) {
						ObjectNode jsonNode_synonym = nodeFactory.objectNode();
						MegaCollection languageObjs = synonymObj
								.getCollection("Language");
						if (languageObjs.size() > 0) {
							jsonNode_synonym.put("Language", languageObjs
									.get(1).getProp(name));
						} else {
							jsonNode_synonym.put("Language", "");
						}

						jsonNode_synonym.put("Value",
								synonymObj.getProp(shortName));
						json_nodeArray.add(jsonNode_synonym);
					}
				}
			}
		}
		return json_nodeArray;
	}
}
