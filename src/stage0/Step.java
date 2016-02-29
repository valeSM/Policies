package stage0;
import java.io.*;
import java.util.*;

import org.xml.sax.*;
import javax.xml.parsers.SAXParserFactory; 
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser; 
import org.xml.sax.helpers.DefaultHandler;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.*;
import org.apache.jena.atlas.logging.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.query.*;

import com.hp.hpl.jena.vocabulary.*;

  /**
   * @author ed
   */
public class Step {
	private String base="";
	static protected OntModel owlmodel = null;

	class SAXConfig extends DefaultHandler {
		private Step ref ;
		public SAXConfig(Step s) {
			ref = s;
		}
		public void startElement(String namespaceURI,
							        String sName, // simple name
							        String qName, // qualified name
							        Attributes attrs) throws SAXException {
		  if (qName == "sparql-queries") ref.sparqlDir = attrs.getValue("dir")+'/';
		  else if (qName == "pl-ontology") {
		  	ref.ns = attrs.getValue("ns");
		  	ref.nsFile = base+'/'+attrs.getValue("url");
		  } else if (qName == "pl-terms") {
		  	ref.ns_t = attrs.getValue("ns");
		  	ref.ns_tFile = base+'/'+attrs.getValue("url");
		  } else if (qName == "pl-licences") {
		  	ref.ns_l = attrs.getValue("ns");
		  	ref.ns_lFile = base+'/'+attrs.getValue("url");
		  } else if (qName == "pl-workflow") {ref.ns_w = attrs.getValue("ns");
		  	ref.ns_wFile = base+'/'+attrs.getValue("url");
		  } else if (qName == "config") base = attrs.getValue("base");
		  else if (qName == "policies") ref.policiesDir = attrs.getValue("dir")+'/';
		} 		
	}
	
	
	public String ns   = "http://privacy-lookout.net/ontologies/current/pl-ontology.n3#" ;
	public String nsFile = "./pl-ontology.n3";
	public String ns_w   = "http://privacy-lookout.net/ontologies/current/pl-workflow.n3#" ;
	public String ns_wFile = "./pl-ontology.n3";
	public String ns_t = "http://privacy-lookout.net/ontologies/current/pl-usage-terms.n3#" ;
	public String ns_tFile = "./pl-usage-terms.n3";
	public String ns_l = "http://privacy-lookout.net/ontologies/current/pl-licenses.n3#" ;
	public String ns_lFile = "./pl-licenses.n3";
	public String sparqlDir = "./";
	public String rulesDir = "./";
	public String policiesDir = "./";

	public Step(String config_file) {
		if (config_file != null) {
	  		DefaultHandler handler = new SAXConfig(this); 
	  		SAXParserFactory factory = SAXParserFactory.newInstance();
	  		try {
	   			 SAXParser saxParser = factory.newSAXParser();
	   			 saxParser.parse(new File(config_file), handler ); 
	  		} catch (Throwable t) {t.printStackTrace();}
	  	}
	}
	
	 protected OntModel loadOntology() {
		if (owlmodel == null) {
			owlmodel = ModelFactory.createOntologyModel(
									OntModelSpec.OWL_MEM_MICRO_RULE_INF, 
										ModelFactory.createDefaultModel()) ;	  
			owlmodel.read(nsFile,"N3");
			owlmodel.read(ns_tFile,"N3");
			owlmodel.read(ns_lFile,"N3");
			owlmodel.read(ns_wFile,"N3");			
		}
		return ModelFactory.createOntologyModel(
									OntModelSpec.OWL_MEM_MICRO_RULE_INF, 
									owlmodel)  ;
	}

	// "apply" applique une requete SPARQL dans l'env. de l'ontologie

	protected Model apply(Model m, String queryString) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		Model resultModel = qexec.execConstruct();
		qexec.close();
		return resultModel ;
	}

	public Statement getUniqueSt(Model m, String ns, String prop) {
		StmtIterator sts = m.listStatements(null,
			                                m.createProperty(ns,prop),
			                                (RDFNode)null);
		if (sts.hasNext()) {
			Statement st = sts.nextStatement() ;
			return st;
		} else return null;
	}
	
	public Set<Statement> getAllSt(Model m, String ns, String prop) {
		StmtIterator sts = m.listStatements(null,
			                                m.createProperty(ns,prop),
			                                (RDFNode)null);
		Set<Statement> ens = new HashSet<Statement>();
		while (sts.hasNext()) {
			Statement st = sts.nextStatement() ;
			ens.add(st);
		} 
		return ens;
	}
	
	public Set<Statement> getAllSt(Model m, Resource r) {
		StmtIterator sts = m.listStatements(r,
			                                null,
			                                (RDFNode)null);
		Set<Statement> ens = new HashSet<Statement>();
		while (sts.hasNext()) {
			Statement st = sts.nextStatement() ;
			ens.add(st);
		} 
		return ens;
	}
	
	public static String loadFileString(String fileName) {
		String txt = "";
		try{
			InputStream flux=new FileInputStream(new File(fileName)); 
			InputStreamReader lecture=new InputStreamReader(flux);
			BufferedReader buff=new BufferedReader(lecture);
			String ligne;
			while ((ligne=buff.readLine())!=null){
				txt += ligne+"\n";
			}
			buff.close();
			return txt;
		} catch (Exception e){
			System.out.println(e.toString());
			return "";
		}
	}

	public static void printStatements(Model m, Resource s, Property p, Resource o) {
		PrintUtil.registerPrefix("x", "http://www.codesupreme.com/#");
		for (StmtIterator i = m.listStatements(s,p,o); i.hasNext(); ) {
			Statement stmt = i.nextStatement();
			System.out.println(" - " + PrintUtil.print(stmt));
		}
	}
	
	public static void main(String[] argv) {
		try
		{
			Step s = new Step("pl-config.xml");
			System.out.println(s.sparqlDir);
			System.out.println(s.ns);
			System.out.println(s.nsFile);
			System.out.println(s.policiesDir);
			System.out.println(loadFileString("pl-config.xml"));
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
