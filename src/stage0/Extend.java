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
public class Extend extends Step {
private Model model_get = ModelFactory.createDefaultModel() ;

	public Extend() {
		this("pl-config.xml");
	}
	public Extend(String config_file) {
		super(config_file);
	}

	// "extend" ajoute des éléments contextuels à la politique négociée à priori
	private void extend(String myFile) {
		  Statement st, new_tripple ;
	      String inputFileName = myFile+"_neg2.ttl" ;
	      model_get.read(inputFileName,"N3");
	      
	      //Récupération du sujet principal
	      st = getUniqueSt(model_get,ns,"hasLicense");
		  Resource subject ;
		  
	      if (st != null) {
			  subject = st.getSubject() ;
	      
		      //Calcul de la date de fin s'il existe une date de début et une durée
			  st = getUniqueSt(model_get,ns_w,"begin"); 
			  if (st != null) {
		    	DateTime begin = new DateTime(st.getLiteral().getString());
		    	st = getUniqueSt(model_get,ns_w,"duration"); 
		  		System.out.println("	Calcul de la date de terminaison");
			    if (st != null) {
		    	  Period p = new Period(st.getLiteral().getString());
		    	  new_tripple = model_get.createStatement(
		    	                             st.getSubject(),
		    	                             model_get.createProperty(ns_w,"end"),
		    	                             begin.plus(p).toString()) ;
		    	  model_get.add(new_tripple);
			    }
		      }
		      
		      //Gestion d'une contrainte sur le nb d'usage
			  st = getUniqueSt(model_get,ns,"maxUses");
			  if (st != null) {
			  	  System.out.println("	Mise en place des connaissances dynamiques");
		    	  Model current = ModelFactory.createDefaultModel() ;
		    	  current.setNsPrefix( "", subject.getNameSpace() );
		    	  current.setNsPrefix( "pl", ns );
		    	  current.setNsPrefix( "wkf", ns_w );
		    	  current.setNsPrefix( "xsd", "http://www.w3.org/2001/XMLSchema#" );
		    	  new_tripple = current.createStatement(
		    	                             subject,
		    	                             current.createProperty(ns_w,"nbUses"),
		    	                             current.createTypedLiteral(new Integer(0))) ;
		    	  current.add(new_tripple);
		    	  try{ 
			      	FileOutputStream fo = new FileOutputStream(myFile+"_current.ttl");
			      	current.write(fo,"N3");
			      } catch(java.io.FileNotFoundException e) {}	
			  }
			    
		      //Enregistrement 
		      try{ 
		      	FileOutputStream fo = new FileOutputStream(myFile+"_neg2.ttl");
		      	model_get.write(fo,"N3");
		      } catch(java.io.FileNotFoundException e) {}	
	      } else System.out.println("Fichier source corrompu.");
	}

	public void convert(String myFile) {
		System.out.println("* Consolidation de la politique négociée");
		extend(policiesDir+myFile) ;
	    System.out.println("	Ajout des termes de la licence");
		String myQuery = loadFileString(sparqlDir+"consolidate.sparql");
		Model m = ModelFactory.createDefaultModel() ;
		m.read(policiesDir+myFile +"_neg2.ttl");
		Model un = ModelFactory.createUnion(model_get, 
		                                    apply(m, myQuery));

		Statement st = getUniqueSt(model_get,ns_w,"policyStatus");
		if (st != null) {
			  Resource r = un.createResource(ns+"final");
			  Statement new_tripple = un.createStatement(
		    	                             st.getSubject(),
		    	                             un.createProperty(ns_w,"policyStatus"),
		    	                             r) ;
		      un.remove(st);
		      un.add(new_tripple);
		}
        try{ 
      	  FileOutputStream fo = new FileOutputStream(policiesDir+myFile +"_cons.ttl");
      	  un.write(fo,"N3");
        } catch(java.io.FileNotFoundException e) {}	
	}
 	
	public static void main(String[] args) {
		  String myFile = args[0];
		  Extend c = new Extend();
		  c.convert(myFile);
	}
}
