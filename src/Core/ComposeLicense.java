/**
 * 
 */
package Core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author Valeria Soto-Mendoza
 * @email vsoto@cicese.edu.mx
 * 
 * 14/05/2015
 */

public class ComposeLicense {

	private String fileName = "";
	private String fileType = "";
	private String ns   = "http://privacy-lookout.net/ontologies/current/pl-ontology#" ;
	private Model loadedModel;
	private Model outputModel;
	
	public void compose(String fileName, String fileType)
	{
		this.fileName = fileName;
		this.fileType = fileType;
		if(loadFile())
		{
			mergePolicies();
		}
	}
	
	private boolean loadFile()
	{
		loadedModel = ModelFactory.createDefaultModel();
	    try {
	    	loadedModel.read(new FileInputStream(this.fileName), null, this.fileType);
			//loadedModel.write(System.out);
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private void mergePolicies()
	{
		// for allowed properties
		analyzeTerm("allowedOperation", "AND");
		analyzeTerm("allowedStatement", "AND");
		analyzeTerm("allowedPurpose", "AND");
		
		// for prohibited properties
		analyzeTerm("prohibitedOperation", "OR");
		analyzeTerm("prohibitedStatement", "OR");
		analyzeTerm("prohibitedPurpose", "OR");
		
		// for mandatory properties
		analyzeTerm("mandatoryOperation", "OR");
		analyzeTerm("mandatoryStatement", "OR");
	}
	
	private void analyzeTerm(String nameProperty, String operationType)
	{	
		Property p = ResourceFactory.createProperty(this.ns, nameProperty);
		ArrayList<RDFNode> setOfNodes = queryTerms(p);
		
		
	}
	
	private ArrayList<RDFNode> queryTerms(Property prop)
	{
		ArrayList<RDFNode> list = new ArrayList<RDFNode>();
		//ArrayList<String> result = new ArrayList<String>();
		ResIterator iter4 = loadedModel.listResourcesWithProperty(prop);
	    while (iter4.hasNext())
	    {
	    	Resource r = iter4.nextResource();
	    
		    StmtIterator iter5 = r.listProperties(prop);
	        while (iter5.hasNext())
	        {
	        	Statement s = iter5.nextStatement();
	        	//System.out.println(s.toString());
	        	RDFNode n = s.getObject();
	        	list.add(n);
	        	
	        	//System.out.println(n.toString());
	        	//result.add(n.toString());
	        }
	        System.out.println("");
	    }
	    return list;
	}
	
	private boolean writeFile()
	{
		return true;
	}
}
