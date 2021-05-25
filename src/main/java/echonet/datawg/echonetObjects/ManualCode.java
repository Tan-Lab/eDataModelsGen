package echonet.datawg.echonetObjects;

import java.util.ArrayList;
import java.util.List;


public class ManualCode {
	private String eoj;
	private List<EPCManualCode> epcs;
	
	public ManualCode() {
		this.setEpcs(new ArrayList<EPCManualCode>());
	}
	public String getEoj() {
		return eoj;
	}
	public void setEoj(String eoj) {
		this.eoj = eoj;
	}
	public List<EPCManualCode> getEpcs() {
		return epcs;
	}
	public void setEpcs(List<EPCManualCode> epcs) {
		this.epcs = epcs;
	}
	public void addEpcs(EPCManualCode epc) {
		this.epcs.add(epc);
	}
	
}


