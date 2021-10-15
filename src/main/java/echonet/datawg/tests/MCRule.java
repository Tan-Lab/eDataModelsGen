package echonet.datawg.tests;

import java.math.BigDecimal;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import echonet.datawg.echonetObjects.ECHONETLiteProperty;


public class MCRule implements Comparable<Object>{
	public MCRule() {
		
	}
	public MCRule(String eoj, ObjectNode rule) {
		this.eoj = eoj;
		this.rule = rule;
	}
	private String eoj;
	private ObjectNode rule;
	public String getEoj() {
		return eoj;
	}
	public void setEoj(String eoj) {
		this.eoj = eoj;
	}
	public ObjectNode getRule() {
		return rule;
	}
	public void setRule(ObjectNode rule) {
		this.rule = rule;
	}
	public ObjectNode toMCRule() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.set(this.eoj, this.rule);
		return rootNode;
	}
	@Override
	public int compareTo(Object o) {
		return ((MCRule)o).getEoj().compareTo(this.getEoj());
	}
	

}
