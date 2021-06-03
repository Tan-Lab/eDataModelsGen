package echonet.datawg.dataTypeObjects;



import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.jena.rdf.model.Literal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.owlcs.ontapi.jena.model.OntClass;
import com.github.owlcs.ontapi.jena.model.OntDataProperty;
import com.github.owlcs.ontapi.jena.model.OntDataRange;
import com.github.owlcs.ontapi.jena.model.OntFacetRestriction;
import com.github.owlcs.ontapi.jena.model.OntModel;
import com.github.owlcs.ontapi.jena.model.OntObjectProperty;
import com.github.owlcs.ontapi.jena.model.OntDataRange.Restriction;
import com.github.owlcs.ontapi.jena.vocabulary.XSD;

import echonet.datawg.echonetObjects.EnJAStatement;
import echonet.datawg.inputParsers.SarefOntologyParser;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.SAREFConstants;
import echonet.datawg.utils.WoTConstants;
import echonet.datawg.utils.eConstants;

public class NumberType extends DataType{
	public NumberType() {
		this.type = Constants.KEYWORD_NUMBER;
	}
	public NumberType(String name) {
		this.type = Constants.KEYWORD_NUMBER;
		this.name = name;
	}
	private String format;
	private BigDecimal minimum;
	private BigDecimal maximum;
	private String unit;
	private Float multipleOf;
	private Float multiple;
	private String[] coefficient;
	private int[] enumValue;
	private EnJAStatement description;
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public BigDecimal getMinimum() {
		return minimum;
	}
	public void setMinimum(BigDecimal minimum) {
		this.minimum = minimum;
	}
	public BigDecimal getMaximum() {
		return maximum;
	}
	public void setMaximum(BigDecimal maximum) {
		this.maximum = maximum;
	}
	
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public int[] getEnumValue() {
		return enumValue;
	}
	public void setEnumValue(int[] enumValue) {
		this.enumValue = enumValue;
	}
	public Float getMultipleOf() {
		return multipleOf;
	}
	public void setMultipleOf(Float multipleOf) {
		this.multipleOf = multipleOf;
	}
	public String[] getCoefficient() {
		return coefficient;
	}
	public void setCoefficient(String[] coefficient) {
		this.coefficient = coefficient;
	}
	@Override
	public ObjectNode toWebAPIDeviceDescription() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_NUMBER);

		if(unit != null && !unit.equals(""))
			rootNode.put(eConstants.KEYWORD_UNIT, unit);
		if(minimum != null) {
			rootNode.setAll(multiplyProcessing(Constants.KEYWORD_MINIMUM, minimum));	
		}
		if(maximum != null) {
			rootNode.setAll(multiplyProcessing(Constants.KEYWORD_MAXIMUM, maximum));
		}
		if(multipleOf != null) {
			if(multipleOf.floatValue() >=1) {
				if(this.getMultiple() != null) {
					if(this.getMultiple().floatValue() >=1) {
						rootNode.put(eConstants.KEYWORD_MULTIPLE_OF, multipleOf.intValue() * multiple.intValue());
					} else {
						rootNode.put(eConstants.KEYWORD_MULTIPLE_OF, multipleOf.intValue() * multiple.floatValue());
					}
				} else {
					rootNode.put(eConstants.KEYWORD_MULTIPLE_OF, multipleOf.intValue());
				}
			} else {
				if(this.getMultiple() != null) {
					if(this.getMultiple().floatValue() >=1) {
						rootNode.put(eConstants.KEYWORD_MULTIPLE_OF, multipleOf.floatValue() * multiple.intValue());
					} else {
						rootNode.put(eConstants.KEYWORD_MULTIPLE_OF, multipleOf.floatValue() * multiple.floatValue());
					}
				} else {
					rootNode.put(eConstants.KEYWORD_MULTIPLE_OF, multipleOf.floatValue());
				}
			}
		} else {
			if(this.getMultiple() != null) {
				if(this.getMultiple().floatValue() >=1) {
					rootNode.put(eConstants.KEYWORD_MULTIPLE_OF, multiple.intValue());
				} else {
					rootNode.put(eConstants.KEYWORD_MULTIPLE_OF, multiple.floatValue());
				}
			}
			
		}	
		if(coefficient != null && coefficient.length != 0) {
			ArrayNode arrayNode = mapper.createArrayNode();
			for(int i =0; i < coefficient.length; i++) {
				arrayNode.add(coefficient[i]);
			}
			rootNode.set(eConstants.KEYWORD_COEFFICIENT, arrayNode);
		}
		if(this.getDescription() != null) {
			rootNode.set(Constants.KEYWORD_DESCRIPTIONS, toDescription());
		}
		return rootNode;
	}
	@Override
	public ObjectNode toFiwareSchemaJSON() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_NUMBER);
		
		if(minimum != null)
			rootNode.put(eConstants.KEYWORD_MINIMUM, minimum.longValue());
		if(maximum != null)
			rootNode.put(eConstants.KEYWORD_MAXIMUM, maximum.longValue());
		return rootNode;
	}
	public Restriction toDataTypeRestriction(SarefOntologyParser owlHanlder) {
		OntDataRange.Named numberRestriction =  owlHanlder.getBaseModel().getDatatype(XSD.xint);
		OntFacetRestriction min = owlHanlder.getBaseModel().
				createFacetRestriction(OntFacetRestriction.MinInclusive.class, numberRestriction.createLiteral(this.getMinimum()));
		OntFacetRestriction max = owlHanlder.getBaseModel().
				createFacetRestriction(OntFacetRestriction.MaxInclusive.class, numberRestriction.createLiteral(this.getMaximum()));
		return owlHanlder.getBaseModel().createDataRestriction(numberRestriction, min,max);
	}
	public boolean hasUnit() {
		boolean rs = false;
		if(unit !=null && !unit.equals("")) {
			rs = true;
		}
		return rs;
	}
	public boolean hasMultipleOf() {
		boolean rs = false;
		if(this.getMultipleOf() !=null) {
			rs = true;
		}
		return rs;
	}
	public boolean hasCoefficient() {
		boolean rs = false;
		if(this.getCoefficient() != null && this.getCoefficient().length > 0) {
			rs = true;
		}
		return rs;
	}
	private OntDataRange.Restriction minMaxvalueRetriction(OntModel baseModel){
		OntDataRange.Restriction numberValueRestriction = null;
		
			OntDataRange.Named numberRestriction =  baseModel.getDatatype(XSD.xint);
			numberValueRestriction = baseModel.createDataRestriction(numberRestriction,
					numberMinRestriction(baseModel, numberRestriction), 
					numberMaxRestriction(baseModel, numberRestriction));
		
		return numberValueRestriction;
		
	}
	private Literal multipleOfLiteral(OntModel baseModel){
		OntDataRange.Named numberRestriction = null;
		if(this.getMultipleOf().floatValue()<1) {
			numberRestriction =  baseModel.getDatatype(XSD.xfloat);
		} else {
			numberRestriction =  baseModel.getDatatype(XSD.xint);
		}
		return numberRestriction.createLiteral(this.getMultipleOf());

	}
	private Literal coefficientLiteral(OntModel baseModel){
		OntDataRange.Named stringRestriction = baseModel.getDatatype(XSD.xstring);
		return stringRestriction.createLiteral(this.getCoefficient()[0]);

		
	}
	private OntFacetRestriction numberMinRestriction(OntModel baseModel,OntDataRange.Named numberRestriction) {
		OntFacetRestriction rs = null;
		if(this.getMinimum() != null) {
			rs =baseModel.createFacetRestriction(
					OntFacetRestriction.MinInclusive.class, numberRestriction.createLiteral(this.getMinimum()));
		}
		return rs;
	}
	private OntFacetRestriction numberMaxRestriction(OntModel baseModel, OntDataRange.Named numberRestriction) {
		OntFacetRestriction rs = null;
		if(this.getMaximum() != null) {
			rs =baseModel.createFacetRestriction(
					OntFacetRestriction.MaxInclusive.class, numberRestriction.createLiteral(this.getMaximum()));
		}
		return rs;
		
	}
	private String toTypeURI(String ppName) {
		String rs = "";
		if(hasUnit()) {
			rs = "has" + ppName + "Measurement" + "In" + unit;
		} else {
			rs = "has" + ppName + "Measurement";
		}
		return rs;
	}
	private String toUnitURI() {
		String rs = "";
		switch (this.unit.toUpperCase()) {
		case "%":
		case "PERCENT":
			rs = "PercentUnit";
			break;
		case "CELSIUS":
			rs = "TemperatureUnitInCelsius";
			break;
		case "DEGREE":
			rs = "AngleUnitInDegree";
			break;
		case "KWH":
			rs = "EnergyUnitInKiloWatt-Hour";
			break;
		case "WH":
			rs = "EnergyUnitInWatt-Hour";
			break;
		case "MJ":
			rs = "EnergyUnitInMegaJoule";
			break;
		case "A":
			rs = "ElectricCurrentUnitInAmpere";
			break;
		case "W":
			rs = "PowerUnitInWatt";
			break;
		case "KINE":
			rs = "VelocityUnitInKine";
			break;
		case "SECOND":
			rs = "TimeUnitInSecond";
			break;	
		case "DAY":
			rs = "TimeUnitInDay";
			break;	
		case "MINUTES":
			rs = "TimeUnitInMinute";
			break;	
		case "LUX":
			rs = "IlluminanceUnitInLux";
			break;	
		case "KLUX":
			rs = "IlluminanceUnitInKLux";
			break;	
		case "CM":
			rs = "LengthUnitInCetimeter";
			break;	
		case "PPM":
			rs = "ConcentrationUnitInPPM";
			break;	
		case "PA":
			rs = "PressureUnitInPascals";
			break;	
		case "M/SEC":
		case "M/S":
			rs = "SpeedUnitInMeterPerSecond";
			break;	
		case "KW":
			rs = "PowerUnitInKiloWatt";
			break;
		case "V":
			rs = "ElectricPotentialDifferenceUnitInVolt";
			break;
		case "MA":
			rs = "ElectricCurrentUnitInMiliAmpere";
			break;
		case "CM3":
			rs = "VolumeUnitInCubicCentimeter";
			break;
		case "CM3/MIN":
			rs = "VolumetricFlowRateUnitInCubicCentimeterPerMinute";
			break;
		case "M3/H":
			rs = "VolumetricFlowRateUnitInCubicMeterPerHour";
			break;
		case "MSEC":
		case "MS"  :
			rs = "TimeUnitInMiliSecond";
			break;	
		case "HPA":
			rs = "PressureUnitInHectorPascals";
			break;	
		case "KPA":
			rs = "PressureUnitInKiloPascals";
			break;	
		case "M3":
			rs = "VolumeUnitInCubicMeter";
			break;
		case "L":
			rs = "VolumeUnitInLiter";
			break;
		case "AH":
			rs = "ElectricChargeUnitInAmpereHour";
			break;
		case "RPM":
			rs = "RotationSpeedUnitInRoundPerMinute";
			break;
		case "KG":
			rs = "MassUnitInKilogram";
			break;
		case "KVARH":
			rs = "ReactivePowerUnitInKiloVolAmperesReactiveHour";
			break;
		default:
			System.out.println(this.unit.toUpperCase());
			break;
		}
		return rs;
	}
	@Override
	public OntClass toObjectPropertyTypeRestriction(SarefOntologyParser owlHanlder, String propertyName) {
		if(this.getUnit() != null && this.getUnit().equals("%"))
			this.setUnit("Percent");
		// Get base classess from SAREF
		OntModel baseModel = owlHanlder.getBaseModel();
		OntClass baseMeasurementClass = baseModel.getOntClass(owlHanlder.SAREF_NS+ SAREFConstants.MEASUREMENT_CLASS);
		OntClass baseUnitOfMeasure = baseModel.getOntClass(owlHanlder.SAREF_NS+ SAREFConstants.HAS_UNIT_OF_MEASURE);
		OntDataProperty baseHasValue = baseModel.getDataProperty(owlHanlder.SAREF_NS + SAREFConstants.HAS_VALUE_PP);
		OntObjectProperty baseMeasuredIn = baseModel.getObjectProperty(owlHanlder.SAREF_NS + SAREFConstants.IS_MEASURED_IN_PP);
		//Create echonetMeasurement
		OntClass echonetMeasurement = baseModel.getOntClass(owlHanlder.ECHONET_NS + toTypeURI(propertyName));
		
		if(echonetMeasurement == null) {
			echonetMeasurement = baseModel.createOntClass(owlHanlder.ECHONET_NS + toTypeURI(propertyName));
			echonetMeasurement.addSuperClass(baseMeasurementClass);
			echonetMeasurement.addSuperClass(baseModel.createDataMinCardinality(baseHasValue, 1, minMaxvalueRetriction(baseModel)));
			// Add Unit Restriction if needed
			if(hasUnit()) {
				OntClass unit = baseModel.getOntClass(owlHanlder.ECHONET_NS + toUnitURI());
				
				if(unit == null) {
					unit = baseModel.createOntClass(owlHanlder.ECHONET_NS + toUnitURI());
					unit.addSuperClass(baseUnitOfMeasure);
				}
				echonetMeasurement.addSuperClass(baseModel.createObjectMinCardinality(baseMeasuredIn, 1, unit));
			}
			if(hasMultipleOf()) {
				OntDataProperty multipleOf = baseModel.createDataProperty(owlHanlder.ECHONET_NS +  "hasMultipleOf");
				echonetMeasurement.addSuperClass(baseModel.createDataHasValue(multipleOf, multipleOfLiteral(baseModel)));
			}
			if(hasCoefficient()) {
				OntDataProperty multipleOf = baseModel.createDataProperty(owlHanlder.ECHONET_NS +  "hasCoefficient");
				echonetMeasurement.addSuperClass(baseModel.createDataHasValue(multipleOf, coefficientLiteral(baseModel)));
			}
		}
		return echonetMeasurement;
	}
	private int powerOfTen(float input) {
		int rs = 0;
		if(input == 0.1f) {
			rs = -1;
		}
		if(input == 0.01f) {
			rs = -2;
		}
		if(input == 0.001f) {
			rs = -3;
		}
		if(input == 0.0001f) {
			rs = -4;
		}
		return rs;
	}
	public Float getMultiple() {
		return multiple;
	}
	public void setMultiple(Float multiple) {
		this.multiple = multiple;
	}
	private ObjectNode multiplyProcessing(String keyword, BigDecimal val) {
		ObjectNode node = new ObjectMapper().createObjectNode();
		if(this.getMultiple() != null) {
			if(this.getMultipleOf() != null) {
				if(multiple.floatValue() >= 1) {
					if(multipleOf >=1) {
						BigDecimal bigMultiple = new BigDecimal(multiple.intValue());
						BigDecimal bigMultipleOf = new BigDecimal(multipleOf.intValue());
						BigDecimal intMultipleIntMultipleOf = val.multiply(bigMultipleOf).multiply(bigMultiple);
						node.put(keyword, intMultipleIntMultipleOf);
					} else {
						BigDecimal bigMultiple = new BigDecimal(multiple.intValue());
						BigDecimal intMultipleFloatMultipleOf = val.scaleByPowerOfTen(powerOfTen(multipleOf)).multiply(bigMultiple);
						int scale = scaleFromNumber(multipleOf);
						node.put(keyword, intMultipleFloatMultipleOf.setScale(scale,RoundingMode.CEILING));
					}
				} else {
					if(multipleOf >=1) {
						BigDecimal bigMultipleOf = new BigDecimal(multipleOf.intValue());
						BigDecimal floatMultipleIntMultipleOf = val.multiply(bigMultipleOf).scaleByPowerOfTen(powerOfTen(multiple));
						int scale = scaleFromNumber(multiple);
						node.put(keyword, floatMultipleIntMultipleOf.setScale(scale ,RoundingMode.CEILING));
					} else {
						BigDecimal floatMultipleFloatMultipleOf = val.scaleByPowerOfTen(powerOfTen(multiple)).scaleByPowerOfTen(powerOfTen(multipleOf));
						int scaleMultiple = scaleFromNumber(multiple);
						int scaleMultipleOf = scaleFromNumber(multipleOf);
						node.put(keyword, floatMultipleFloatMultipleOf.setScale(scaleMultiple + scaleMultipleOf,RoundingMode.CEILING));
					}	
				}	
			} else {
				if(multiple.floatValue() >= 1) {
					BigDecimal bigMultiple = new BigDecimal(multiple.intValue());
					BigDecimal intMultipleNoMultipleOf = val.multiply(bigMultiple);
					node.put(keyword, intMultipleNoMultipleOf);
				} else {
					BigDecimal floatMultipleNoMultipleOf = val.scaleByPowerOfTen(powerOfTen(multiple));		
					int scale = scaleFromNumber(multiple);
					node.put(keyword, floatMultipleNoMultipleOf.setScale(scale,RoundingMode.CEILING));
				}
			}
		} else {
			if(this.getMultipleOf() != null) {
				if(multipleOf >=1) {
					BigDecimal bigMultipleOf = new BigDecimal(multipleOf.intValue());
					BigDecimal noMultipleIntMultipleOf = val.multiply(bigMultipleOf);
					node.put(keyword, noMultipleIntMultipleOf);
				} else {
					BigDecimal noMultiplefloatMultipleOf = val.scaleByPowerOfTen(powerOfTen(multipleOf));	
					int scale = scaleFromNumber(multipleOf);
					node.put(keyword, noMultiplefloatMultipleOf.setScale(scale,RoundingMode.CEILING));
				}
			} else {
				node.put(keyword, val.longValue());
			}
			
		}
		return node;
		
	}
	public static int scaleFromNumber(Float number) {
		String[] split = number.toString().split("\\.");
		return split[1].length();
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
	@Override
	public ObjectNode toThingDescriptionDataSchema() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put(eConstants.KEYWORD_TYPE, eConstants.TYPE_NUMBER);

		if(unit != null && !unit.equals(""))
			rootNode.put(eConstants.KEYWORD_UNIT, unit);
		if(minimum != null) {
			rootNode.setAll(multiplyProcessing(Constants.KEYWORD_MINIMUM, minimum));	
		}
		if(maximum != null) {
			rootNode.setAll(multiplyProcessing(Constants.KEYWORD_MAXIMUM, maximum));
		}
		if(this.getDescription() != null) {
			rootNode.put(WoTConstants.KEYWORD_DESCRIPTION, this.getDescription().getEN());
			rootNode.set(Constants.KEYWORD_DESCRIPTIONS, toDescription());
		}
		return rootNode;
	}
}
