package echonet.datawg.dataTypeObjects;

import java.util.List;

import org.apache.jena.rdf.model.Literal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.owlcs.ontapi.jena.model.OntClass;
import com.github.owlcs.ontapi.jena.model.OntDataProperty;
import com.github.owlcs.ontapi.jena.model.OntDataRange;
import com.github.owlcs.ontapi.jena.model.OntModel;
import com.github.owlcs.ontapi.jena.model.OntObjectProperty;
import com.github.owlcs.ontapi.jena.vocabulary.XSD;

import echonet.datawg.inputParsers.SarefOntologyParser;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.SAREFConstants;
import echonet.datawg.utils.eConstants;

public class ArrayType extends DataType{
	public ArrayType() {
		this.type = Constants.KEYWORD_ARRAY;
	}
	public ArrayType(String name) {
		this.type = Constants.KEYWORD_ARRAY;
		this.name = name;
	}
	private List<DataType> items;
	private Integer itemSize;
	private Integer minItems;
	private Integer maxItems;
	
	
	public Integer getItemSize() {
		return itemSize;
	}
	public void setItemSize(Integer itemSize) {
		this.itemSize = itemSize;
	}
	public Integer getMinItems() {
		return minItems;
	}
	public void setMinItems(Integer minItems) {
		this.minItems = minItems;
	}
	public Integer getMaxItems() {
		return maxItems;
	}
	public void setMaxItems(Integer maxItems) {
		this.maxItems = maxItems;
	}
	public List<DataType> getItems() {
		return items;
	}
	public void setItems(List<DataType> items) {
		this.items = items;
	}
	@Override
	public ObjectNode toWebAPIDeviceDescription() {
		System.out.println("------Exporting Data Type: " + this.type + " Name: " + this.name);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, this.type);
		if(minItems != null)
			rootNode.put(eConstants.KEYWORD_MIN_ITEM, minItems.intValue());
		if(maxItems != null)
			rootNode.put(eConstants.KEYWORD_MAX_ITEM, maxItems.intValue());
		
		ObjectNode objOneItemType = null;
		ObjectNode objMultipleItemType = null;
		if(items != null && items.size() == 1) {
			for(DataType type : items) {
				objOneItemType = type.toWebAPIDeviceDescription();
			}
		} else if(items != null && items.size() > 1) {
			ArrayNode typeArray = mapper.createArrayNode();
			for(DataType type : items) {
				typeArray.add(type.toWebAPIDeviceDescription());
			}
			objMultipleItemType = mapper.createObjectNode();
			objMultipleItemType.set(eConstants.KEYWORD_ONEOF, typeArray);
		}
		
		if(objOneItemType != null) {
			rootNode.set(eConstants.KEYWORD_ITEMS, objOneItemType);
		} else if(objMultipleItemType != null) {
			rootNode.set(eConstants.KEYWORD_ITEMS,objMultipleItemType);
		}
		
		return rootNode;
	}
	@Override
	public ObjectNode toFiwareSchemaJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, this.type);
		if(minItems != null)
			rootNode.put(eConstants.KEYWORD_MIN_ITEM, minItems.intValue());
		if(maxItems != null)
			rootNode.put(eConstants.KEYWORD_MAX_ITEM, maxItems.intValue());
		
		ObjectNode objOneItemType = null;
		ObjectNode objMultipleItemType = null;
		if(items != null && items.size() == 1) {
			for(DataType type : items) {
				objOneItemType = type.toFiwareSchemaJSON();
			}
		} else if(items != null && items.size() > 1) {
			ArrayNode typeArray = mapper.createArrayNode();
			for(DataType type : items) {
				typeArray.add(type.toFiwareSchemaJSON());
			}
			objMultipleItemType = mapper.createObjectNode();
			objMultipleItemType.set(eConstants.KEYWORD_ONEOF, typeArray);
		}
		
		if(objOneItemType != null) {
			rootNode.set(eConstants.KEYWORD_ITEMS, objOneItemType);
		} else if(objMultipleItemType != null) {
			rootNode.set(eConstants.KEYWORD_ITEMS,objMultipleItemType);
		}
		
		return rootNode;
	}
	@Override
	public OntClass toObjectPropertyTypeRestriction(SarefOntologyParser owlHanlder, String propertyName) {
		OntModel baseModel = owlHanlder.getBaseModel();
		//Create echonetMeasurement
		OntClass echonetArray = baseModel.createOntClass(owlHanlder.ECHONET_NS + toTypeURI(propertyName));
		echonetArray.addSuperClass(baseModel.getOntClass(owlHanlder.SAREF_NS + SAREFConstants.MEASUREMENT_CLASS));
		OntDataProperty hasMaxItems = null;
		OntDataProperty hasItemSize = null;
		OntDataProperty hasMinItem = null;
		if(this.getMaxItems() != null) {
			hasMaxItems =  baseModel.createDataProperty(owlHanlder.ECHONET_NS + "hasMaxItems");
			echonetArray.addSuperClass(baseModel.createDataHasValue(hasMaxItems, maxItemsLiteral(baseModel)));
		}
		if(minItems != null) {
			hasMinItem =  baseModel.createDataProperty(owlHanlder.ECHONET_NS + "hasMinItems");
			echonetArray.addSuperClass(baseModel.createDataHasValue(hasMinItem, minItemsLiteral(baseModel)));
		}
		if(this.getItemSize() != null) {
			hasItemSize = baseModel.createDataProperty(owlHanlder.ECHONET_NS + "hasItemsize");
			echonetArray.addSuperClass(baseModel.createDataHasValue(hasItemSize, itemsizeLiteral(baseModel)));
		}
		
		String dataTypePropertyName = "has" + toTypeURI(propertyName) +"ItemDataType";
		if(getItems().size() == 1) {
			echonetArray.addSuperClass(toSingleItemDataType(owlHanlder, getItems().get(0), dataTypePropertyName));
		} else {
			for(DataType dt: getItems()) {
				echonetArray.addSuperClass(toSingleItemDataType(owlHanlder, dt, dataTypePropertyName));
			}
		}

		return echonetArray;
	}
	public OntClass toSingleItemDataType(SarefOntologyParser owlHanlder, DataType dt, String dataTypePropertyName) {
		OntModel baseModel = owlHanlder.getBaseModel();
		OntClass rs = null;
		OntObjectProperty itemDataType = baseModel.createObjectProperty(owlHanlder.ECHONET_NS + dataTypePropertyName);
		rs = baseModel.createObjectMinCardinality(itemDataType, 1, dt.toObjectPropertyTypeRestriction(owlHanlder, dataTypePropertyName));
		return rs;
	}
	private String toTypeURI(String PropertyName) {
		return PropertyName + "Array";
	}
	private Literal maxItemsLiteral(OntModel baseModel){
		OntDataRange.Named numberRestriction =  baseModel.getDatatype(XSD.xint);
		return numberRestriction.createLiteral(this.getMaxItems());
	}
	private Literal minItemsLiteral(OntModel baseModel){
		OntDataRange.Named numberRestriction =  baseModel.getDatatype(XSD.xint);
		return numberRestriction.createLiteral(this.getMinItems());
	}
	private Literal itemsizeLiteral(OntModel baseModel){
		OntDataRange.Named numberRestriction =  baseModel.getDatatype(XSD.xint);
		return numberRestriction.createLiteral(this.getItemSize());
	}
	@Override
	public ObjectNode toThingDescriptionDataSchema() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, this.type);
		if(minItems != null)
			rootNode.put(eConstants.KEYWORD_MIN_ITEM, minItems.intValue());
		if(maxItems != null)
			rootNode.put(eConstants.KEYWORD_MAX_ITEM, maxItems.intValue());
		
		ObjectNode objOneItemType = null;
		ObjectNode objMultipleItemType = null;
		if(items != null && items.size() == 1) {
			for(DataType type : items) {
				objOneItemType = type.toThingDescriptionDataSchema();
			}
		} else if(items != null && items.size() > 1) {
			ArrayNode typeArray = mapper.createArrayNode();
			for(DataType type : items) {
				typeArray.add(type.toThingDescriptionDataSchema());
			}
			objMultipleItemType = mapper.createObjectNode();
			objMultipleItemType.set(eConstants.KEYWORD_ONEOF, typeArray);
		}
		
		if(objOneItemType != null) {
			rootNode.set(eConstants.KEYWORD_ITEMS, objOneItemType);
		} else if(objMultipleItemType != null) {
			rootNode.set(eConstants.KEYWORD_ITEMS,objMultipleItemType);
		}
		
		return rootNode;
	}

}
