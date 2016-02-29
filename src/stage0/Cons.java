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
  
  /**
   * @author ed
   */
public class Cons extends Step {

	public Cons() {
		this("pl-config.xml");
	}
	public Cons(String config_file) {
		super(config_file);
	}

	protected Model apply(String myFile, String queryString) {
		loadOntology();
		Model m = ModelFactory.createDefaultModel();
		m.read(myFile,"N3");
		return apply(ModelFactory.createUnion(m, owlmodel), queryString) ;
	}

	public void doIt(String myFile) {
		Model model_get = ModelFactory.createDefaultModel() ;
		Model priloo = loadOntology();
	    model_get.read(myFile,"N3");
	    priloo.add(model_get);
		String myQuery = loadFileString(sparqlDir+"consolidate-1.sparql");
		Model un = apply(priloo, myQuery);
		model_get.add(un);
    	myQuery = loadFileString(sparqlDir+"consolidate-2.sparql");
    	priloo.add(un);
		Model deux = apply(priloo, myQuery);
		model_get.add(deux);
		model_get.setNsPrefix( "pl", ns );
		model_get.setNsPrefix( "wkf", ns_w );
		model_get.setNsPrefix( "term", ns_t );
		model_get.setNsPrefix( "lic", ns_l );
      	model_get.write(System.out, "N3") ;
	}
 	
	public static void main(String[] args) {
		  String myFile = args[0];
		  Cons c = new Cons();
		  c.doIt(myFile);
	}
}
