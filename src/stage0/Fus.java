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
   * @description Combine two parts of a Usage Policy
   */
public class Fus extends Step {
private Model model_get = ModelFactory.createDefaultModel() ;

	public Fus() {super(null);}

	public void doIt(String myFile1, String myFile2) {
	      model_get.read(myFile1,"N3");
	      model_get.read(myFile2,"N3");
		  model_get.write(System.out,"N3");
	}
 	
	public static void main(String[] args) {
		try{
		  String myFile1 = args[0];
		  String myFile2 = args[1];
		  Fus c = new Fus();
		  c.doIt(myFile1,myFile2);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
