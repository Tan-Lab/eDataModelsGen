package echonet.datawg.inputParsers;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import echonet.datawg.dataTypeObjects.BooleanType;
import echonet.datawg.dataTypeObjects.DataType;
import echonet.datawg.dataTypeObjects.DateTimeType;
import echonet.datawg.dataTypeObjects.DateType;
import echonet.datawg.dataTypeObjects.LevelType;
import echonet.datawg.dataTypeObjects.NullType;
import echonet.datawg.dataTypeObjects.NumberType;
import echonet.datawg.dataTypeObjects.RawType;
import echonet.datawg.dataTypeObjects.StateEnumValue;
import echonet.datawg.dataTypeObjects.StateType;
import echonet.datawg.dataTypeObjects.TimeType;
import echonet.datawg.echonetObjects.EnJAStatement;
import echonet.datawg.utils.Constants;

public class SimpleDataTypeParser {
	public static DataType toTypeDefinition(String key, JSONObject jsonObj) {
		DataType rs = null;
		switch (jsonObj.get(Constants.KEYWORD_TYPE).toString().trim()) {
			case Constants.TYPE_NUMBER:
				rs = toNumberType(key, jsonObj);
				break;
			case Constants.TYPE_STATE:
				rs = toStateType(key, jsonObj);
				break;	
			case Constants.TYPE_LEVEL:
				rs = toLevelType(key, jsonObj);
				break;
			case Constants.TYPE_RAW:
				rs = toRawType(key, jsonObj);
				break;
			case Constants.TYPE_NULL:
				rs = toNullType(key, jsonObj);
				break;
			case Constants.TYPE_BOOLEAN:
				rs = toBooleanType(key, jsonObj);
				break;
			default:
				rs = null;
				break;
		}
		return rs;
	}
	public static DataType toNumberType(String key, JSONObject jsonObj)
	{
		NumberType numberType = new NumberType(key);
		if(jsonObj.get(Constants.KEYWORD_FORMAT) != null)
			numberType.setFormat(jsonObj.get(Constants.KEYWORD_FORMAT).toString());
		if(jsonObj.get(Constants.KEYWORD_MAXIMUM) != null)
			numberType.setMaximum(BigDecimal.valueOf(Long.valueOf(jsonObj.get(Constants.KEYWORD_MAXIMUM).toString())));
		if(jsonObj.get(Constants.KEYWORD_MINIMUM) != null)
			numberType.setMinimum(BigDecimal.valueOf(Long.valueOf(jsonObj.get(Constants.KEYWORD_MINIMUM).toString())));
		if(jsonObj.get(Constants.KEYWORD_UNIT) !=null) {
			numberType.setUnit(jsonObj.get(Constants.KEYWORD_UNIT).toString());
		}
		EnJAStatement desc = null;
		JSONObject description = (JSONObject) jsonObj.get(Constants.KEYWORD_DESCRIPTIONS);
		if(description != null) {
			desc = new EnJAStatement();
			if(description.get(Constants.KEYWORD_EN).toString() != null)
				desc.setEN(description.get(Constants.KEYWORD_EN).toString());
			if(description.get(Constants.KEYWORD_JA).toString() != null)
				desc.setJP(description.get(Constants.KEYWORD_JA).toString());
		} 
		if(desc != null) {
			numberType.setDescription(desc);
		}
		if(jsonObj.get(Constants.KEYWORD_MULTIPLE) !=null) {
			numberType.setMultiple(Float.valueOf(jsonObj.get(Constants.KEYWORD_MULTIPLE).toString()));
		}
		if(jsonObj.get(Constants.KEYWORD_MULTIPLE_OF)!=null) {
			numberType.setMultipleOf(Float.valueOf(jsonObj.get(Constants.KEYWORD_MULTIPLE_OF).toString()));
		}
		if(jsonObj.get(Constants.KEYWORD_COEFFICIENT) !=null) {
			JSONArray array = (JSONArray) jsonObj.get(Constants.KEYWORD_COEFFICIENT);
			String[] strArray = new String[array.size()];
			for(int i = 0; i < array.size(); i ++) {
				strArray[i] =array.get(i).toString();
			}
			numberType.setCoefficient(strArray); 
		}
		
		if(jsonObj.get(Constants.KEYWORD_ENUM) !=null) {
			JSONArray intEnum = (JSONArray) jsonObj.get(Constants.KEYWORD_ENUM);
			int[] intArray = new int[intEnum.size()];
			for(int i = 0; i < intEnum.size(); i ++) {
				intArray[i] =Integer.parseInt(intEnum.get(i).toString());
			}
			numberType.setEnumValue(intArray);
		}
		return numberType;
	}
	public static DataType toNumberType(JSONObject jsonObj)
	{
		return toNumberType(null, jsonObj);
	}
	public static DataType toStateType(String key, JSONObject jsonObj)
	{
		StateType stateType = new StateType(key);
		if(jsonObj.get(Constants.KEYWORD_SIZE) != null)
			stateType.setSize(Integer.valueOf(jsonObj.get(Constants.KEYWORD_SIZE).toString()));
		if(jsonObj.get(Constants.KEYWORD_ENUM) != null)
			stateType.setEnumValue(toStateEnumList((JSONArray)jsonObj.get(Constants.KEYWORD_ENUM)));
		return stateType;
	}
	public static DataType toStateType(JSONObject jsonObj)
	{
		return toStateType(null, jsonObj);
	}
	public static List<StateEnumValue> toStateEnumList(JSONArray jsonArray) {
		List<StateEnumValue> rs = new ArrayList<StateEnumValue>();
		Iterator<?> iterator = jsonArray.iterator();
		while(iterator.hasNext()) {
		   JSONObject obj = (JSONObject) iterator.next();
		   rs.add(toStateEnum(obj));
		}
		return rs;
	}
	public static StateEnumValue toStateEnum(JSONObject obj) {
		StateEnumValue stateObj = new StateEnumValue();
		EnJAStatement desc = null;
		if(obj.get(Constants.KEYWORD_EDT) !=null)
			stateObj.setEdt(obj.get(Constants.KEYWORD_EDT).toString());
		if(obj.get(Constants.KEYWORD_READ_ONLY) !=null) {
			stateObj.setReadOnly(Boolean.valueOf(obj.get(Constants.KEYWORD_READ_ONLY).toString()));
		}
		if(obj.get(Constants.KEYWORD_NAME) !=null) {
			stateObj.setName(obj.get(Constants.KEYWORD_NAME).toString());
		} else {
		}
		JSONObject description = (JSONObject) obj.get(Constants.KEYWORD_DESCRIPTIONS);
		if(description != null) {
			desc = new EnJAStatement();
			if(description.get(Constants.KEYWORD_EN).toString() != null)
				desc.setEN(description.get(Constants.KEYWORD_EN).toString());
			if(description.get(Constants.KEYWORD_JA).toString() != null)
				desc.setJP(description.get(Constants.KEYWORD_JA).toString());
		} 
		if(desc != null) {
			stateObj.setDescription(desc);
		}
		return stateObj;
	}
	public static DataType toLevelType(String key, JSONObject jsonObj)
	{
		LevelType levelType = new LevelType(key);
		if(jsonObj.get(Constants.KEYWORD_BASE) != null)
			levelType.setBase(jsonObj.get(Constants.KEYWORD_BASE).toString());
		if(jsonObj.get(Constants.KEYWORD_MAXIMUM) != null)
			levelType.setMaximum(Integer.valueOf(jsonObj.get(Constants.KEYWORD_MAXIMUM).toString()));
		if(jsonObj.get(Constants.KEYWORD_MINIMUM) != null)
			levelType.setMinumum(Integer.valueOf(jsonObj.get(Constants.KEYWORD_MINIMUM).toString()));
		if(jsonObj.get(Constants.KEYWORD_MULTIPLE_OF) != null)
			levelType.setMultipleOf(Float.valueOf(jsonObj.get(Constants.KEYWORD_MULTIPLE_OF).toString()));
		return levelType;
	}
	public static DataType toLevelType(JSONObject jsonObj)
	{
		return toLevelType(null, jsonObj);
	}
	public static DataType toRawType(String key, JSONObject jsonObj)
	{
		RawType rawType = new RawType(key);
		if(jsonObj.get(Constants.KEYWORD_MIN_SIZE) != null)
			rawType.setMinSize(Integer.valueOf(jsonObj.get(Constants.KEYWORD_MIN_SIZE).toString()));
		if(jsonObj.get(Constants.KEYWORD_MAX_SIZE) != null)
			rawType.setMaxSize(Integer.valueOf(jsonObj.get(Constants.KEYWORD_MAX_SIZE).toString()));
		return rawType;
	}
	public static DataType toNullType(String key, JSONObject jsonObj)
	{
		NullType nullType = new NullType(key);
		if(jsonObj.get(Constants.KEYWORD_EDT) != null)
			nullType.setEdt(jsonObj.get(Constants.KEYWORD_EDT).toString());
		return nullType;
	}
	public static DataType toBooleanType(String key, JSONObject jsonObj)
	{
		BooleanType boolType = new BooleanType(key);
		return boolType;
	}
	public static DataType toBooleanType(JSONObject jsonObj)
	{
		BooleanType boolType = new BooleanType();
		return boolType;
	}
	public static DataType toRawType(JSONObject jsonObj)
	{
		return toRawType(null, jsonObj);
	}public static DataType toNullType(JSONObject jsonObj)
	{
		return toNullType(null, jsonObj);
	}
	public static DataType toTimeType(JSONObject jsonObj) {
		TimeType timeType = new TimeType();
		if(jsonObj.get(Constants.KEYWORD_SIZE) != null)
			timeType.setSize(Integer.valueOf(jsonObj.get(Constants.KEYWORD_SIZE).toString()));
		return timeType;
	}
	public static DataType toDateTimeType(JSONObject jsonObj) {
		DateTimeType dateTimeType = new DateTimeType();
		if(jsonObj.get(Constants.KEYWORD_SIZE) != null)
			dateTimeType.setSize(Integer.valueOf(jsonObj.get(Constants.KEYWORD_SIZE).toString()));
		return dateTimeType;
	}
	public static DataType toDateType(JSONObject jsonObj) {
		DateType dateType = new DateType();
		if(jsonObj.get(Constants.KEYWORD_SIZE) != null)
			dateType.setSize(Integer.valueOf(jsonObj.get(Constants.KEYWORD_SIZE).toString()));
		return dateType;
	}
}
