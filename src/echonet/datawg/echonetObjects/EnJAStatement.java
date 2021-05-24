package echonet.datawg.echonetObjects;


public class EnJAStatement {
	public EnJAStatement() {
		
	}
	public EnJAStatement(String en, String ja) {
		setEn(en);
		setJa(ja);
	}
	private String ja;
	private String en;
	public String getJa() {
		return ja;
	}
	public void setJa(String ja) {
		this.ja = ja;
	}
	public String getEn() {
		return en;
	}
	public void setEn(String en) {
		this.en = en;
	}
}
