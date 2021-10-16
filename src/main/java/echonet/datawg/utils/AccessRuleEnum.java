package echonet.datawg.utils;

public enum AccessRuleEnum {
	
	optional("optional"),required("required"),notApplicable("notApplicable"),
	require_c("required_c"),require_o("required_o");
	private  String name;       

    AccessRuleEnum(String s) {
        name = s;
    }

    public String getTextValue() {
        return this.name;
    }
    public static AccessRuleEnum getByName(String text) {
    	for(AccessRuleEnum rule :AccessRuleEnum.values()) {
    		if(text.equalsIgnoreCase(rule.name)) {
    			return rule;
    		}
    	}
    	return null;
    }
    public String toString() {
       return this.name;
    }
	
}
