package echonet.datawg.dataTypeObjects;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.owlcs.ontapi.jena.model.OntClass;
import com.github.owlcs.ontapi.jena.model.OntModel;
import com.github.owlcs.ontapi.jena.model.OntObjectProperty;

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
	
	
	
	public List<ObjectProperty> getProperties() {
		return properties;
	}
	public void setProperties(List<ObjectProperty> properties) {
		this.properties = properties;
	}
	@Override
	public ObjectNode toWebAPIDeviceDescription() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, this.type);
		
		ObjectNode propertyNode = mapper.createObjectNode();
		
		for(ObjectProperty pp : properties) {
			if(!pp.isReservedForFutureUse() && !pp.isDELProperty() && !pp.isDELProperty2()) {
				propertyNode.set(pp.getName(),  pp.toJSONObjDescription());
			}
		}
		if(propertyNode.size() != 0)
			rootNode.set(eConstants.KEYWORD_PROPERTIES, propertyNode);
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

}
