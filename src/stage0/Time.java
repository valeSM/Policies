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
public class Time extends Step {
private Model mod = ModelFactory.createDefaultModel() ;

	public Time() {
		this("pl-config.xml");
	}
	public Time(String config_file) {
		super(config_file);
	}
	
	private boolean usageControl(Model m) {
		Statement stMaxUses = getUniqueSt(m,ns,"maxUses");
		if (stMaxUses != null) {
			int max = stMaxUses.getInt();
			Statement stNbUses = getUniqueSt(m,ns,"nbUses");
			int nb = stNbUses.getInt();
			return (nb <= max) ;
		} else return true;
	}
	
	private int dateControl(Model m) {
		Statement stStart = getUniqueSt(m,ns,"begin");
		DateTime begin = null ;
		Statement stEnd = getUniqueSt(m,ns,"end");
		DateTime end = null;
		//DateTime now = DateTime.now();
		int v = 0;
		if (stStart != null) {
			begin = new DateTime(stStart.getLiteral().getString());
			//System.out.print("From:"+begin);
			if (begin.isAfterNow()) v = -1 ; 
			else {
				v = 0; 
				if (stEnd != null) {
					end = new DateTime(stEnd.getLiteral().getString());
					//System.out.println(" to:"+end);
					if (end.isBeforeNow()) v = 1 ;
				}
			}
		} else if (stEnd != null) {
				end = new DateTime(stEnd.getLiteral().getString());
				if (end.isBeforeNow()) v = 1 ;
		} 
		return v;
	}
	
	public void go(String myFile) {
		myFile = policiesDir+myFile ;
		System.out.println("* Mise à jour de la politique");
		/*OntModel owlmodel = ModelFactory.createOntologyModel(
									OntModelSpec.OWL_MEM_MICRO_RULE_INF, 
									model_get) ;*/
	    mod.read(myFile+"_cons.ttl","N3");
	    mod.read(myFile+"_current.ttl","N3");
	    
	    Statement sts = getUniqueSt(mod,ns,"status");
		mod.remove(sts);
		if (sts != null) {
			boolean us =  usageControl(mod);
			System.out.println("usage:"+us);
			int pos = dateControl(mod);
			System.out.println("date:"+pos);
			Statement new_tripple = mod.createStatement(
		    	                             sts.getSubject(),
		    	                             mod.createProperty(ns,"status"),
		    	                             mod.createResource(ns+"died")) ; ;
			if (us && (pos==0)) {
			  new_tripple = mod.createStatement(
		    	                             sts.getSubject(),
		    	                             mod.createProperty(ns,"status"),
		    	                             mod.createResource(ns+"active")) ;
			} else if (us && (pos<0)) {
			  new_tripple = mod.createStatement(
		    	                             sts.getSubject(),
		    	                             mod.createProperty(ns,"status"),
		    	                             mod.createResource(ns+"waiting")) ;
		    } else if (us && (pos>0)) {
			  new_tripple = mod.createStatement(
		    	                             sts.getSubject(),
		    	                             mod.createProperty(ns,"status"),
		    	                             mod.createResource(ns+"died")) ;
		    } else if (!us) {
			  new_tripple = mod.createStatement(
		    	                             sts.getSubject(),
		    	                             mod.createProperty(ns,"status"),
		    	                             mod.createResource(ns+"died")) ;
		    }
		  	mod.add(new_tripple);		
		} 
				
        try{ 
      	  FileOutputStream fo = new FileOutputStream(myFile +"_cons2.ttl");
      	  mod.write(fo,"N3");
        } catch(java.io.FileNotFoundException e) {}	
	}
 	
	public static void main(String[] args) {
		  String myFile = args[0];
		  Time t = new Time();
		  t.go(myFile);
	}
}
