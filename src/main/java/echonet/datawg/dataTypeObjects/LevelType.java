package echonet.datawg.dataTypeObjects;



import java.math.BigDecimal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.owlcs.ontapi.jena.model.OntClass;
import com.github.owlcs.ontapi.jena.model.OntDataProperty;
import com.github.owlcs.ontapi.jena.model.OntDataRange;
import com.github.owlcs.ontapi.jena.model.OntFacetRestriction;
import com.github.owlcs.ontapi.jena.model.OntModel;
import com.github.owlcs.ontapi.jena.vocabulary.XSD;

import echonet.datawg.inputParsers.SarefOntologyParser;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.SAREFConstants;
import echonet.datawg.utils.eConstants;

public class LevelType extends DataType{
	public LevelType() {
		this.type = Constants.TYPE_LEVEL;
	}
	public LevelType(String name) {
		this.type = Constants.TYPE_LEVEL;
		this.name = name;
	}
	private String base;
	private Integer maximum;
	private Integer minumum;
	private Float multipleOf;
	
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
	public Integer getMaximum() {
		return maximum;
	}
	public void setMaximum(Integer maximum) {
		this.maximum = maximum;
	}
	public ObjectNode toObjectType() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_OBJECT);
		
		
		ObjectNode propertyNode = mapper.createObjectNode();
		NumberType level = new NumberType();
		level.setMinimum(BigDecimal.valueOf(0));
		level.setMaximum(BigDecimal.valueOf(maximum));
		
		propertyNode.set(eConstants.KEYWORD_BASE, new RawType().toWebAPIDeviceDescription());
		propertyNode.set("level", level.toWebAPIDeviceDescription());
		
		rootNode.set(eConstants.KEYWORD_PROPERTIES, propertyNode);
		ArrayNode requireNode = mapper.createArrayNode();
		requireNode.add("level");
		rootNode.set("required", requireNode);
		return rootNode;
	}
	public ObjectNode toNumberType() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_NUMBER);
		if(this.getMinumum() == null) {
			rootNode.put(eConstants.KEYWORD_MINIMUM, 1);
		} else {
			rootNode.put(eConstants.KEYWORD_MINIMUM, this.getMinumum().intValue());
		}
		if(this.getMaximum()!= null)
			rootNode.put(eConstants.KEYWORD_MAXIMUM, this.maximum.intValue());
		return rootNode;
	}
	@Override
	public ObjectNode toWebAPIDeviceDescription() {
		return toNumberType();
	}
	
	@Override
	public ObjectNode toFiwareSchemaJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_OBJECT);
		
		ObjectNode propertyNode = mapper.createObjectNode();
		propertyNode.set(eConstants.KEYWORD_BASE, new RawType().toFiwareSchemaJSON());
		NumberType level = new NumberType();
		level.setMinimum(BigDecimal.valueOf(0));
		level.setMaximum(BigDecimal.valueOf(maximum));
		
		propertyNode.set("level",level.toFiwareSchemaJSON());
		
		rootNode.set(eConstants.KEYWORD_PROPERTIES, propertyNode);
		ArrayNode requireNode = mapper.createArrayNode();
		requireNode.add("level");
		rootNode.set("required", requireNode);
		return rootNode;
	}
	@Override
	public OntClass toObjectPropertyTypeRestriction(SarefOntologyParser owlHanlder, String propertyName) {
		OntModel baseModel = owlHanlder.getBaseModel();
		OntClass baseMeasurementClass = baseModel.getOntClass(owlHanlder.SAREF_NS+ SAREFConstants.MEASUREMENT_CLASS);
		//Create echonetMeasurement
		OntClass echonetLevelMeasurement = baseModel.getOntClass(owlHanlder.ECHONET_NS + toTypeURI(propertyName));
		OntDataProperty baseHasValue = baseModel.getDataProperty(owlHanlder.SAREF_NS + SAREFConstants.HAS_VALUE_PP);
		if(echonetLevelMeasurement == null) {
			echonetLevelMeasurement = baseModel.createOntClass(owlHanlder.ECHONET_NS + toTypeURI(propertyName));
			echonetLevelMeasurement.addSuperClass(baseMeasurementClass);
			echonetLevelMeasurement.addSuperClass(baseModel.createDataMinCardinality(baseHasValue,1, levelValueRestriction(baseModel)));
		}
		return echonetLevelMeasurement;
	}
	private OntDataRange levelValueRestriction(OntModel baseModel) {
		OntDataRange.Named valueType = baseModel.getDatatype(XSD.xint);
		OntFacetRestriction min = baseModel.createFacetRestriction(OntFacetRestriction.MinInclusive.class, valueType.createLiteral(0));
		OntFacetRestriction max = baseModel.createFacetRestriction(OntFacetRestriction.MaxInclusive.class, valueType.createLiteral(this.getMaximum()));
		return baseModel.createDataRestriction(valueType, min,max);
	}
	private String toTypeURI(String PropertyName) {
		return PropertyName + "LevelMeasurement";
	}
	public Integer getMinumum() {
		return minumum;
	}
	public void setMinumum(Integer minumum) {
		this.minumum = minumum;
	}
	public Float getMultipleOf() {
		return multipleOf;
	}
	public void setMultipleOf(Float multipleOf) {
		this.multipleOf = multipleOf;
	}


}
