/**
 * 
 */
package Core;

import java.io.*;
import java.util.*;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.atlas.logging.*;

import com.hp.hpl.jena.datatypes.* ;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.util.*;
import com.hp.hpl.jena.vocabulary.VCARD;

/**
 * @author		Valeria Soto-Mendoza
 * @email		vsoto@cicese.edu.mx
 * 13/05/2015
 */

public class Program {
	
	public static String ns   = "http://privacy-lookout.net/ontologies/current/pl-ontology#" ;
	public String nsFile = "./pl-ontology.n3";
	public String ns_w   = "http://privacy-lookout.net/ontologies/current/pl-workflow#" ;
	public String ns_wFile = "./pl-ontology.n3";
	public String ns_t = "http://privacy-lookout.net/ontologies/current/pl-usage-terms#" ;
	public String ns_tFile = "./pl-usage-terms.n3";
	public static String ns_l = "http://privacy-lookout.net/ontologies/current/pl-licenses#" ;
	public String ns_lFile = "./pl-licenses.n3";
	public String sparqlDir = "./";
	public String rulesDir = "./";
	public String policiesDir = "./";

	/*public void loadLicenses(String inputFileName)
	{
		//String inputFileName = "";
		
		// create an empty model
		Model model = ModelFactory.createDefaultModel();

		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(inputFileName);
		if (in == null)
		{
		    throw new IllegalArgumentException("File: " + inputFileName + " not found");
		}

		// read the RDF/XML file
		model.read(in, "TTL");

		// write it to standard out
		model.write(System.out);
	}
	
	public void loadLicense2(String inputFileName)
	{
		// Create a model and read into it from file 
		// "data.ttl" assumed to be Turtle.
		Model model = RDFDataMgr.loadModel(inputFileName) ;
		
		// Read into an existing Model
		RDFDataMgr.read(model, inputFileName) ;
		
		// write it to standard out
		model.write(System.out);
	}*/
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Program p  = new Program();
		//p.loadLicenses("test.ttl");
		//p.loadLicense2("Health-license.ttl");
		
		// Load file with different licenses to compose
		Model model = ModelFactory.createDefaultModel();
	    try {
			model.read(new FileInputStream("/Users/valentina/Documents/workspace/PoliciesComposition/data/Health-license.ttl"),null,"TTL");
			//model.write(System.out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    Property propertyAllowedOperation = ResourceFactory.createProperty(
	            ns, "allowedOperation");
	    
	    /*ResIterator iter2 = model.listSubjectsWithProperty(propertyAllowedOperation);
	    while (iter2.hasNext()) {
	    	//iter2.
	        Resource r = iter2.nextResource();
	        System.out.println(r.toString());
	        //iter2.next();
	    }
	    
	    System.out.println("\n");*/
	    
	    /*ResIterator iter4 = model.listResourcesWithProperty(propertyAllowedOperation);
	    while (iter4.hasNext()) {
	    	//iter2.
	        Resource r = iter4.nextResource();
	        System.out.println(r.toString());
	        StmtIterator iter5 = r.listProperties(propertyAllowedOperation);
	        while (iter5.hasNext())
	        {
	        	Statement s = iter5.nextStatement();
	        	System.out.println(s.toString());
	        }
	    }*/
	    
	    ArrayList<RDFNode> listOfNodes = new ArrayList<RDFNode>();
	    
	    int numResources = 0;
	    ResIterator iter4 = model.listResourcesWithProperty(propertyAllowedOperation);
	    //System.out.println("Number of resources: " + iter4.toString());
	    while (iter4.hasNext())
	    {
	    	Resource r = iter4.nextResource();
	    
		    StmtIterator iter5 = r.listProperties(propertyAllowedOperation);
	        while (iter5.hasNext())
	        {
	        	Statement s = iter5.nextStatement();
	        	//System.out.println(s.toString());
	        	RDFNode n = s.getObject();
	        	listOfNodes.add(n);
	        	//System.out.println(n.toString());
	        }
	        //System.out.println("");
	        numResources = numResources + 1;
	    }
	    System.out.println("Number of licenses: " + numResources + "\n");
	    
	    System.out.println("Number of nodes: " + listOfNodes.size());
	    
	    Hashtable<String, Integer> contenedor=new Hashtable<String,Integer>();
	    for(int i = 0; i < listOfNodes.size(); i++)
	    {
	    	RDFNode n = listOfNodes.get(i);
	    	System.out.println(n.toString());
	    	if (contenedor.containsKey(n.toString()) == false)
	    		contenedor.put(n.toString(), 1);
	    	else
	    	{
	    		int value = contenedor.get(n.toString());
	    		value = value + 1;
	    		contenedor.put(n.toString(), value);
	    	}
	    	//System.out.println(contenedor.toString() + "\n");
	    }
	    
	    System.out.println("\n" + contenedor.toString());
	    System.out.println(contenedor.size());
	    
	    // write file and apply operations
//	    Model model_out = ModelFactory.createDefaultModel();
//	    Statement st, new_tripple;
//	    //st = new Statement();
//	    new_tripple = model_out.createStatement(
//                st.getSubject(),
//                model_out.createProperty(ns_w,"end"),
//                begin.plus(p).toString()) ;
//	    model_out.add(new_tripple);
	    
	    
	    /*System.out.println("\n");
	    
	    NodeIterator iter3 = model.listObjectsOfProperty(propertyAllowedOperation);
	    while (iter3.hasNext()) {
	    	//iter2.
	        RDFNode r = iter3.nextNode();
	        System.out.println(r.toString());
	        //iter2.next();
	    }*/
	    
	    /*
	    // list the statements in the Model
	    StmtIterator iter = model.listStatements();

	    // print out the subject, predicate and object of each statement
	    while (iter.hasNext())
	    {
	        Statement stmt      = iter.nextStatement();  // get next statement
	        Resource  subject   = stmt.getSubject();     // get the subject
	        Property  predicate = stmt.getPredicate();   // get the predicate
	        RDFNode   object    = stmt.getObject();      // get the object

	        
	        
	        //if (predicate.hasProperty(predicate, ns, "allowedOperation"))
	        //{
		        System.out.print(subject.toString());
		        System.out.print(" " + predicate.toString() + " ");
		        if (object instanceof Resource) {
		           System.out.print(object.toString());
		        } else {
		            // object is a literal
		            System.out.print(" \"" + object.toString() + "\"");
		        }
	
		        System.out.println(" .");
	        //}
	    }*/
	    
	    // Query all prohibited elements of all policies contained in the file
	    /*Statement st;
	    st = getUniqueSt(model, ns_l, "prohibitedPurpose");
	    if (st != null) {
	    	System.out.println(st.getLiteral().getString());
	    }*/
	    
	    // select all the resources with a VCARD.FN property
	    /*ResIterator iter = model.listsublistSubjectsWithProperty("prohibitedPurpose");
	    if (iter.hasNext()) {
	        System.out.println("The database contains vcards for:");
	        while (iter.hasNext()) {
	            System.out.println("  " + iter.nextResource()
	                                          .getProperty(VCARD.FN)
	                                          .getString());
	        }
	    } else {
	        System.out.println("No vcards were found in the database");
	    }*/
	}

}
