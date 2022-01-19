package echonet.datawg.dataTypeObjects;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.owlcs.ontapi.jena.model.OntClass;
import com.github.owlcs.ontapi.jena.model.OntModel;
import com.github.owlcs.ontapi.jena.model.OntObjectProperty;

import echonet.datawg.echonetObjects.EnJAStatement;
import echonet.datawg.inputParsers.SarefOntologyParser;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.SAREFConstants;
import echonet.datawg.utils.eConstants;


public class ObjectType extends DataType{
	public ObjectType(String name) {
		this.type = Constants.KEYWORD_OBJECT;
		this.name = name;
	}
	public ObjectType() {
		this.type = Constants.KEYWORD_OBJECT;
	}
	private List<ObjectProperty> properties;
	private boolean isONE_OF;
	private EnJAStatement description;
	
	public List<ObjectProperty> getProperties() {
		return properties;
	}
	public void setProperties(List<ObjectProperty> properties) {
		this.properties = properties;
	}
	public void setProperty(ObjectProperty property) {
		if(this.properties == null) {
			this.properties = new ArrayList<ObjectProperty>();
		}
		this.properties.add(property);
	}
	@Override
	public ObjectNode toWebAPIDeviceDescription() {
		System.out.println("------Exporting Data Type: " + this.type + " Name: " + this.name);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, this.type);
		
		ObjectNode propertyNode = mapper.createObjectNode();
		ObjectNode oneOfNode = mapper.createObjectNode();
		ArrayNode oneOf =mapper.createArrayNode();
		for(ObjectProperty pp : properties) {
			if(!pp.isReservedForFutureUse() && !pp.isDELProperty() && !pp.isDELProperty2()) {
				if(isONE_OF) {
					
					oneOf.add(pp.toPPDescription());
					oneOfNode.set(eConstants.KEYWORD_ONEOF, oneOf);
				} else {
					propertyNode.set(pp.getName(),  pp.toJSONObjDescription());
				}
			}
		}
		if(propertyNode.size()!=0 && oneOfNode.size() == 0) {
			rootNode.set(eConstants.KEYWORD_PROPERTIES, propertyNode);
		} else if(oneOfNode.size() != 0) {
			rootNode.set(eConstants.KEYWORD_PROPERTIES, oneOfNode);
		}
		return rootNode;
	}
	@Override
	public ObjectNode toFiwareSchemaJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, this.type);
		
		ObjectNode propertyNode = mapper.createObjectNode();
		
		for(ObjectProperty pp : properties) {
			if(pp.getElement().size() > 1) {
				ArrayNode oneOf = mapper.createArrayNode();
				for(DataType type : pp.getElement()) {
					oneOf.add(type.toFiwareSchemaJSON());
				}
				ObjectNode multipleTypePropertyNode = mapper.createObjectNode();
				multipleTypePropertyNode.set(eConstants.KEYWORD_ONEOF, oneOf);
				propertyNode.set(pp.toPropertyNameLowerCamel(),  multipleTypePropertyNode);
			} else if (pp.getElement().size() == 1){
				propertyNode.set(pp.toPropertyNameLowerCamel(), pp.getElement().get(0).toFiwareSchemaJSON());
			}
			
		}
		if(propertyNode.size() != 0)
			rootNode.set(eConstants.KEYWORD_PROPERTIES, propertyNode);
		return rootNode;
	}

	@Override
	public OntClass toObjectPropertyTypeRestriction(SarefOntologyParser owlHanlder, String propertyName) {
		OntModel baseModel = owlHanlder.getBaseModel();
		//Create echonetMeasurement
		OntClass echonetObject = baseModel.getOntClass(owlHanlder.ECHONET_NS + toTypeURI(propertyName));
		if(echonetObject == null) {
			echonetObject = baseModel.createOntClass(owlHanlder.ECHONET_NS + toTypeURI(propertyName));	
			echonetObject.addSuperClass(baseModel.getOntClass(owlHanlder.SAREF_NS + SAREFConstants.MEASUREMENT_CLASS));
		}
		for(ObjectProperty property:getProperties()) {
			String dataTypeProperty = toTypeURI(propertyName) + "has" + property.toPropertyNameCamel();
			if(property.getElement().size() == 1) {
				echonetObject.addSuperClass(toSingleItemDataType(owlHanlder,property.getElement().get(0),dataTypeProperty));
			} else {
				for(DataType dt : property.getElement()) {
					echonetObject.addSuperClass(toSingleItemDataType(owlHanlder,dt,dataTypeProperty));
				}
			}
		}

		return echonetObject;
	}
	public OntClass toSingleItemDataType(SarefOntologyParser owlHanlder, DataType dt, String dataTypePropertyName) {
		OntModel baseModel = owlHanlder.getBaseModel();
		OntClass rs = null;
		OntObjectProperty itemDataType = baseModel.createObjectProperty(owlHanlder.ECHONET_NS + dataTypePropertyName);
		rs = baseModel.createObjectMinCardinality(itemDataType, 1, dt.toObjectPropertyTypeRestriction(owlHanlder, dataTypePropertyName));

		return rs;
	}
	private String toTypeURI(String PropertyName) {
		return PropertyName + "Object";
	}
	public boolean isONE_OF() {
		return isONE_OF;
	}
	public void setONE_OF(boolean isONE_OF) {
		this.isONE_OF = isONE_OF;
	}
	@Override
	public ObjectNode toThingDescriptionDataSchema() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, this.type);
		if(this.description != null) {
			rootNode.put(Constants.KEYWORD_DESCRIPTION, description.getEN());
			rootNode.set(eConstants.KEYWORD_DESCRIPTIONS, toDescription());
		}
		
		ObjectNode propertyNode = mapper.createObjectNode();
		ObjectNode oneOfNode = mapper.createObjectNode();
		for(ObjectProperty pp : properties) {
			if(!pp.isReservedForFutureUse() && !pp.isDELProperty() && !pp.isDELProperty2()) {
				if(isONE_OF) {
					ArrayNode oneOf =mapper.createArrayNode();
					propertyNode.set(pp.getName(),  pp.toJSONTDPropertySchema());
					oneOf.add(propertyNode);
					oneOfNode.set(eConstants.KEYWORD_ONEOF, oneOf);
				} else {
					propertyNode.set(pp.getName(),  pp.toJSONTDPropertySchema());
				}
			}
		}
		if(propertyNode.size()!=0 && oneOfNode.size() == 0) {
			rootNode.set(eConstants.KEYWORD_PROPERTIES, propertyNode);
		} else if(oneOfNode.size() != 0) {
			rootNode.set(eConstants.KEYWORD_PROPERTIES, oneOfNode);
		}
		return rootNode;
	}
	public EnJAStatement getDescription() {
		return description;
	}
	public void setDescription(EnJAStatement description) {
		this.description = description;
	}
	public ObjectNode toDescription() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rs = null;
		if(this.getDescription()!=null) {
			rs = mapper.createObjectNode();
			rs.put(Constants.KEYWORD_EN, this.getDescription().getEN());
			rs.put(Constants.KEYWORD_JA, this.getDescription().getJP());
		}
		return rs;
	}

}