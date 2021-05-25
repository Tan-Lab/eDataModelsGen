package echonet.datawg.echonetObjects;

import echonet.datawg.utils.AccessRuleEnum;

public class PropertyAccessRule {
	public PropertyAccessRule() {
		
	}
	private AccessRuleEnum get;
	private AccessRuleEnum set;
	private AccessRuleEnum inf;
	public AccessRuleEnum getGet() {
		return get;
	}
	public void setGet(AccessRuleEnum get) {
		this.get = get;
	}
	public AccessRuleEnum getSet() {
		return set;
	}
	public void setSet(AccessRuleEnum set) {
		this.set = set;
	}
	public AccessRuleEnum getInf() {
		return inf;
	}
	public void setInf(AccessRuleEnum inf) {
		this.inf = inf;
	}
	
}
