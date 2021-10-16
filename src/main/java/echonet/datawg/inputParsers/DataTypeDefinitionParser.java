package echonet.datawg.inputParsers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import echonet.datawg.dataTypeObjects.ArrayType;
import echonet.datawg.dataTypeObjects.Bitmap;
import echonet.datawg.dataTypeObjects.BitmapPosition;
import echonet.datawg.dataTypeObjects.BitmapType;
import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.dataTypeObjects.NumberType;
import echonet.datawg.dataTypeObjects.NumericEnumValue;
import echonet.datawg.dataTypeObjects.NumericType;
import echonet.datawg.dataTypeObjects.ObjectProperty;
import echonet.datawg.dataTypeObjects.ObjectType;
import echonet.datawg.echonetObjects.EnJAStatement;
import echonet.datawg.utils.Constants;
import echonet.datawg.utils.eConstants;

public class DataTypeDefinitionParser {
	public DataTypeDefinitionParser(List<DataType> types) {
		predefinedTypes = types;
	}
	public DataTypeDefinitionParser() {
	}
	List<DataType> predefinedTypes;
	private  DataType predefinedTypeFromName(String name) {
		DataType rs = null;
		for(DataType type: predefinedTypes) {
			if(type.getName().equals(name)) {
				System.out.println(type.toWebAPIDeviceDescription().toString());
				rs = type;
				break;
			} 
		}
		return rs;	
	}
	public DataType toDataDefinition(JSONObject obj) {
		DataType rs = null;	
		if(obj.get(Constants.KEYWORD_TYPE) == null) {
			rs = toPredefinedRefType(obj);
		} else {
			switch (obj.get(Constants.KEYWORD_TYPE).toString()) {
				case Constants.TYPE_NUMBER:
					rs = SimpleDataTypeParser.toNumberType(obj);
					break;
				case Constants.KEYWORD_ARRAY:
					rs = toArrayType(obj);
					break;
				case Constants.KEYWORD_DATE_TIME:
					rs= SimpleDataTypeParser.toDateTimeType(obj);
					break;
				case Constants.KEYWORD_DATE:
					rs= SimpleDataTypeParser.toDateType(obj);
					break;
				case Constants.KEYWORD_BITMAP:
					rs = toBitmapType(obj);
					break;		
				case Constants.KEYWORD_NUMERICVALUE:
					rs = toNumericType(obj);
					break;
				case Constants.KEYWORD_RAW:
					rs = SimpleDataTypeParser.toRawType(obj);
					break;
				case Constants.TYPE_NULL:
					rs = SimpleDataTypeParser.toNullType(obj);
					break;
				case Constants.KEYWORD_STATE:
					rs = SimpleDataTypeParser.toStateType(obj);
					break;
				case Constants.KEYWORD_TIME:
					rs = SimpleDataTypeParser.toTimeType(obj);
					break;
				case Constants.KEYWORD_OBJECT:
					rs = toObjectType(obj);
					break;
				case Constants.TYPE_LEVEL:
					rs = SimpleDataTypeParser.toLevelType(obj);
					break;
				case Constants.TYPE_BOOLEAN:
					rs = SimpleDataTypeParser.toBooleanType(obj);
					break;
				default:
					break;
			}			
		}	
		return rs;
	}
	public DataType toPredefinedRefType(JSONObject obj) {
		DataType rs =null;
		DataType data = null;
		String refName = "";
		Float multipleOf = null;
		Float multiple = null;
		String unit = "";
		String[] coefficient = null;
		
		Iterator<?> definition = obj.keySet().iterator();
		while(definition.hasNext()) {
			String key = (String) definition.next();
			switch (key) {
				case Constants.KEYWORD_$REF:
					refName = obj.get(key).toString().substring(Constants.KEYWORD_REF_PREFIX.length());
					data = predefinedTypeFromName(refName);
					break;
				case Constants.KEYWORD_MULTIPLE_OF:
					multipleOf = Float.valueOf(obj.get(key).toString());
					break;
				case Constants.KEYWORD_MULTIPLE:
					multiple = Float.valueOf(obj.get(key).toString());
					break;
				case Constants.KEYWORD_UNIT:
					unit = obj.get(key).toString();	 
					break;
				case Constants.KEYWORD_COEFFICIENT:
					JSONArray array = (JSONArray) obj.get(key);
					String[] strArray = new String[array.size()];
					for(int i = 0; i < array.size(); i ++) {
						strArray[i] =array.get(i).toString();
					}
					coefficient = strArray;
					break;
				default :
					break;
			}
		}
		if(data != null) {
			if(data.getClass().equals(NumberType.class)) {
				NumberType number = (NumberType) data;
				NumberType numberWithUnit = new NumberType();
				numberWithUnit = number;
				if(multipleOf != null) {
					numberWithUnit.setMultipleOf(multipleOf);
				}
				if(multiple != null) {
					numberWithUnit.setMultiple(multiple);
				}
				if(number.getUnit()!= null && !number.getUnit().equals("")) {
					numberWithUnit.setUnit(number.getUnit());
				}
				if(!unit.equals("")) {
					numberWithUnit.setUnit(unit);
				}
				
				if(coefficient != null) {
					numberWithUnit.setCoefficient(coefficient);
				}
				rs = numberWithUnit;
			} else {
				rs = data;
			}
		}
		return rs;

		
	}
	private DataType toObjectType(JSONObject jsonObj) {
		return toObjectType(null,jsonObj);
	}
	public DataType toObjectType(String key, JSONObject jsonObj) {
		ObjectType objType = new ObjectType(key);
		if(jsonObj.get(Constants.KEYWORD_PROPERTIES) != null) {
			JSONArray ppArray = (JSONArray)jsonObj.get(Constants.KEYWORD_PROPERTIES);
			List<ObjectProperty> property = new ArrayList<ObjectProperty>();
			Iterator<?> iterator = ppArray.iterator();
			while(iterator.hasNext()) {
				JSONObject obj = (JSONObject) iterator.next();
			    if(obj.get(Constants.KEYWORD_ONE_OF) != null) {
				   objType.setONE_OF(true);
				   JSONArray oneOfArray = (JSONArray) obj.get(Constants.KEYWORD_ONE_OF);
				   property = toObjectProperties(oneOfArray);
			    } else {
			    	property.add(toObjectProperty(obj));
			    }
			}
			objType.setProperties(property);
		}
			
		return objType;
		
	}
	private List<ObjectProperty> toObjectProperties(JSONArray jsonArray) {
		List<ObjectProperty> rs = new ArrayList<ObjectProperty>();
		Iterator<?> iterator = jsonArray.iterator();
		while(iterator.hasNext()) {
		   JSONObject obj = (JSONObject) iterator.next();
		   rs.add(toObjectProperty(obj));
		}
		return rs;
	}
	private ObjectProperty toObjectProperty(JSONObject jsonObj) {
		ObjectProperty rs = new ObjectProperty();
		if(jsonObj.get(Constants.KEYWORD_SHORTNAME) !=null) {
			rs.setName(jsonObj.get(Constants.KEYWORD_SHORTNAME).toString());
		}
		
		if(jsonObj.get(Constants.KEYWORD_ELEMENT_NAME) !=null) {
			rs.setDescription(
					toObjectPropertyDescription(
							(JSONObject)jsonObj.get(Constants.KEYWORD_ELEMENT_NAME)));
		}
		if(jsonObj.get(Constants.KEYWORD_NOTE) !=null) {
			rs.setNote(
					toObjectPropertyDescription(
							(JSONObject)jsonObj.get(Constants.KEYWORD_NOTE)));
		}
		if(jsonObj.get(Constants.KEYWORD_ELEMENT) !=null)
			rs.setElement(toElementWithDataTypeList((JSONObject) jsonObj.get(Constants.KEYWORD_ELEMENT)));
		return rs;
	}
	private EnJAStatement toObjectPropertyDescription(JSONObject jsonObj) {
		return new EnJAStatement(jsonObj.get(eConstants.KEYWORD_EN).toString(), 
								 jsonObj.get(eConstants.KEYWORD_JA).toString());
	}
	private List<DataType> toElementWithDataTypeList(JSONObject jsonObj) {
		List<DataType> rs = new ArrayList<DataType>();
		if(jsonObj.get(Constants.KEYWORD_ONE_OF) != null) {
			JSONArray jsonArray = (JSONArray) jsonObj.get(Constants.KEYWORD_ONE_OF);
			Iterator<?> iterator = jsonArray.iterator();
			while(iterator.hasNext()) {
			   JSONObject obj = (JSONObject) iterator.next();
			   rs.add(toDataDefinition(obj));
			}
		} else {
			rs.add(toDataDefinition(jsonObj));
		}
		return rs;
	}
	private DataType toArrayType(JSONObject obj) {
		ArrayType rs = new ArrayType();	
		if(obj.get(Constants.KEYWORD_ITEMSIZE) != null) 
			rs.setItemSize(Integer.valueOf(obj.get(Constants.KEYWORD_ITEMSIZE).toString()));
		if(obj.get(Constants.KEYWORD_MAXITEMS) !=null)
			rs.setMaxItems(Integer.valueOf(obj.get(Constants.KEYWORD_MAXITEMS).toString()));	
		if(obj.get(Constants.KEYWORD_MINITEMS) !=null)
			rs.setMinItems(Integer.valueOf(obj.get(Constants.KEYWORD_MINITEMS).toString()));	
		
		List<DataType> itemTypes = new ArrayList<DataType>();
		
		JSONObject itemDef = (JSONObject) obj.get(Constants.KEYWORD_ITEMS);
		
		if( itemDef!= null)  {
			if(itemDef.get(Constants.KEYWORD_ONE_OF) != null) {
				JSONArray typeArray = (JSONArray) itemDef.get(Constants.KEYWORD_ONE_OF);
				for(Object o : typeArray.toArray()) {	
					itemTypes.add(toDataDefinition((JSONObject) o));
				}
			} else {
				itemTypes.add(toDataDefinition(itemDef));
			}
		}
		rs.setItems(itemTypes);
		return rs;
	}
	private DataType toBitmapType(JSONObject obj) {
		BitmapType rs = new BitmapType();
		if(obj.get(Constants.KEYWORD_SIZE) != null) {
			rs.setSize(Integer.valueOf(obj.get(Constants.KEYWORD_SIZE).toString()));
		}
			
		if(obj.get(Constants.KEYWORD_BITMAPS) != null)
			rs.setBitmaps(toBitmaps((JSONArray) obj.get(Constants.KEYWORD_BITMAPS)));
		return rs;
	}
	private List<Bitmap> toBitmaps(JSONArray array) {
		List<Bitmap> bitmaps = new ArrayList<Bitmap>();
		Iterator<?> iterator = array.iterator();
		while(iterator.hasNext()) {
			   JSONObject obj = (JSONObject) iterator.next();
			   bitmaps.add(toBitmap(obj));
			}
		return bitmaps;
		
	}
	private Bitmap toBitmap(JSONObject obj) {
		Bitmap bitmap = new Bitmap();
		if(obj.get(Constants.KEYWORD_NAME) != null) {
			bitmap.setName(obj.get(Constants.KEYWORD_NAME).toString());
		} 
		if(obj.get(Constants.KEYWORD_DESCRIPTIONS) != null) 
			bitmap.setDescriptions(toBitmapDescription((JSONObject) obj.get(Constants.KEYWORD_DESCRIPTIONS)));
		if(obj.get(Constants.KEYWORD_POSITION) != null) 
			bitmap.setPosition(toBitmapPosition((JSONObject) obj.get(Constants.KEYWORD_POSITION)));
		if(obj.get(Constants.KEYWORD_VALUE) != null)
			bitmap.setValue(toDataDefinition((JSONObject) obj.get(Constants.KEYWORD_VALUE)));
		return bitmap;
	}
	private EnJAStatement toBitmapDescription(JSONObject obj) {
		String en = obj.get(Constants.KEYWORD_EN).toString();
		String jp = obj.get(Constants.KEYWORD_JA).toString();
		return new EnJAStatement(en,jp);
	}
	private BitmapPosition toBitmapPosition(JSONObject obj) {
		BitmapPosition rs = new BitmapPosition();
		if(obj.get(Constants.KEYWORD_INDEX) != null)
			rs.setIndex(Integer.valueOf(obj.get(Constants.KEYWORD_INDEX).toString()));
		if(obj.get(Constants.KEYWORD_BITMASK) != null)
			rs.setBitMask(obj.get(Constants.KEYWORD_BITMASK).toString());
		return rs;
	}
	private DataType toNumericType(JSONObject obj) {
		NumericType rs = new NumericType();
		if(obj.get(Constants.KEYWORD_SIZE) != null)
			rs.setSize(Integer.valueOf(obj.get(Constants.KEYWORD_SIZE).toString()));
		if(obj.get(Constants.KEYWORD_ENUM) != null)
			rs.setEnumValue(toNumericEnumList((JSONArray) obj.get(Constants.KEYWORD_ENUM)));
		return rs;
	}
	private List<NumericEnumValue> toNumericEnumList(JSONArray array){
		List<NumericEnumValue> rs = new ArrayList<NumericEnumValue>();
		Iterator<?> iterator = array.iterator();
		while(iterator.hasNext()) {
			   JSONObject obj = (JSONObject) iterator.next();
			   rs.add(toNumericEnum(obj));
			}
		return rs;
		
	}
	private NumericEnumValue toNumericEnum(JSONObject obj){
		NumericEnumValue rs = new NumericEnumValue();
		if(obj.get(Constants.KEYWORD_EDT) != null)
			rs.setEdt(obj.get(Constants.KEYWORD_EDT).toString());
		if(obj.get(Constants.KEYWORD_NUMERICVALUE) != null)
			rs.setNumericValue(Float.valueOf(obj.get(Constants.KEYWORD_NUMERICVALUE).toString()));
		return rs;
		
	}

}
