package echonet.datawg.inputParsers;

import com.github.owlcs.ontapi.jena.OntModelFactory;
import com.github.owlcs.ontapi.jena.model.OntModel;

public class SarefOntologyParser {
	public SarefOntologyParser(String Ont_URL) {
		SAREF_NS = "https://saref.etsi.org/core/";
		ECHONET_NS = "https://echonet/releaseM/";
		this.baseModel = OntModelFactory.createModel();
		this.baseModel.read(Ont_URL,"TURTLE");
		this.baseModel.setNsPrefix("s4echonet", ECHONET_NS);
	}
	public SarefOntologyParser(String Ont_URL, String FORMAT) {
		SAREF_NS = "https://saref.etsi.org/core/";
		ECHONET_NS = "https://echonet/releaseM/";
		this.baseModel = OntModelFactory.createModel();
		this.baseModel.read(Ont_URL,FORMAT);
		this.baseModel.setNsPrefix("s4echonet", ECHONET_NS);
	}
	private OntModel baseModel;
	public String SAREF_NS;
	public String ECHONET_NS;
	public OntModel getBaseModel() {
		return baseModel;
	}
	public void setBaseModel(OntModel baseModel) {
		this.baseModel = baseModel;
	}

}
