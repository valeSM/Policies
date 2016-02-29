package stage0;
  import java.io.*;
  import java.util.*;

  import org.joda.time.*;

  import com.hp.hpl.jena.rdf.model.*;
  import com.hp.hpl.jena.util.*;
  import org.apache.jena.atlas.logging.*;
  import com.hp.hpl.jena.ontology.*;
  import com.hp.hpl.jena.query.*;
  import com.hp.hpl.jena.datatypes.* ;
  import com.hp.hpl.jena.reasoner.*;
import com.hp.hpl.jena.reasoner.rulesys.*;
import com.hp.hpl.jena.vocabulary.*;

  /**
   * @author ed
   */
public class Apply extends Step {

	public Apply() {
		this("pl-config.xml");
	}
	public Apply(String config_file) {
		super(config_file);
	}

	public static InfModel processRulesF(String fileloc, Model modelIn) {return processRules(fileloc, modelIn, "forward");}

	public static InfModel processRulesB(String fileloc, Model modelIn) {return processRules(fileloc, modelIn, "backward");}

	public static InfModel processRulesH(String fileloc, Model modelIn) {return processRules(fileloc, modelIn, "hybrid");}

	public static InfModel processRules(String fileloc, Model modelIn, String method) {
	 // create a simple model; create a resource and add rules from a file
	 Resource configuration = modelIn.createResource();
	 configuration.addProperty(ReasonerVocabulary.PROPruleSet, fileloc );
	 configuration.addProperty(ReasonerVocabulary.PROPruleMode, method);
	 // Create an instance of a reasoner
	 Reasoner reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);

	 // Now with the rawdata model & the reasoner, create an InfModel
	 InfModel infmodel = ModelFactory.createInfModel(reasoner, modelIn);
	 ValidityReport vr = infmodel.validate();
	 if (!vr.isClean()) {
	 	Iterator<ValidityReport.Report> it = vr.getReports();
	 	while(it.hasNext()) {
	 		ValidityReport.Report vrr = it.next();
	 		System.err.println((vrr.isError()?"err:":"war:") + vrr.getDescription());
	 	}
	 }
	 return infmodel;
	}
		
	public void doIt(String myFile1, String myFile2) {
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF,
													  ModelFactory.createDefaultModel()) ;
	    m.read(myFile1,"N3");
	    OntModel o = loadOntology();
	    o.add(m);
	    InfModel deux = processRulesF(myFile2,o);
	    m.add(deux.getDeductionsModel());
      	m.write(System.out, "N3") ;
	}
 	
	public static void main(String[] args) {
		  String myFile1 = args[0];
		  String myFile2 = args[1];
		  Apply c = new Apply();
		  c.doIt(myFile1, myFile2);
	}
}
