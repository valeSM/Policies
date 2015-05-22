/**
 * 
 */
package Core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
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

public class ComposeLicense 
{
	public String ns   = "http://privacy-lookout.net/ontologies/current/pl-ontology#" ;
	public String nsFile = "./pl-ontology.n3";
	public String ns_w   = "http://privacy-lookout.net/ontologies/current/pl-workflow#" ;
	public String ns_wFile = "./pl-ontology.n3";
	public String ns_t = "http://privacy-lookout.net/ontologies/current/pl-usage-terms#" ;
	public String ns_tFile = "./pl-usage-terms.n3";
	public String ns_l = "http://privacy-lookout.net/ontologies/current/pl-licenses#" ;
	public String ns_lFile = "./pl-licenses.n3";
	public String sparqlDir = "./";
	public String rulesDir = "./";
	public String policiesDir = "./";
	
	private String fileName = "";
	private String fileType = "";
	private int num_licenses = 0;
	private Model loadedModel;
	private Model outputModel;
	private Resource new_resource;
	
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
		this.loadedModel = ModelFactory.createDefaultModel();
		this.outputModel = ModelFactory.createDefaultModel();
	    try {
	    	this.loadedModel.read(new FileInputStream(this.fileName), null, this.fileType);
	    	this.outputModel.setNsPrefixes(this.loadedModel.getNsPrefixMap());
			this.loadedModel.write(System.out,"TTL");
			System.out.println();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private void mergePolicies()
	{
		//outputModel.setNsPrefixes(loadedModel.getNsPrefixMap());
		new_resource = outputModel.createResource(this.ns_l + "resultedPolicy");
		
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
		
		outputModel.write(System.out, "TTL");
		this.writeFile();
	}
	
	private void analyzeTerm(String nameProperty, String operationType)
	{	
		Property p = ResourceFactory.createProperty(this.ns, nameProperty);
		ArrayList<RDFNode> setOfNodes = queryTerms(p);
		Hashtable<String, Integer> selection = selectTerms(setOfNodes);
		
		Enumeration<String> keys = selection.keys();
		while (keys.hasMoreElements()) 
		{
			String prop_value = keys.nextElement();
			switch (operationType)
			{
				case "AND":
					if (selection.get(prop_value) == this.num_licenses)
						addTerm(nameProperty, prop_value.toString());
					break;
				case "OR":
					addTerm(nameProperty, prop_value.toString());
					break;
				default:
					break;
			}	
		}
	}
	
	private ArrayList<RDFNode> queryTerms(Property prop)
	{
		ArrayList<RDFNode> list = new ArrayList<RDFNode>();
		ResIterator iter4 = loadedModel.listResourcesWithProperty(prop);
	    while (iter4.hasNext())
	    {
	    	Resource r = iter4.nextResource();
	    	//System.out.println(r.getLocalName());
		    StmtIterator iter5 = r.listProperties(prop);
	        while (iter5.hasNext())
	        {
	        	Statement s = iter5.nextStatement();
	        	RDFNode n = s.getObject();
	        	//System.out.println(n.asResource().getLocalName());
	        	list.add(n);
	        }
	        this.num_licenses = this.num_licenses + 1;
	    }
	    return list;
	}
	
	
	private Hashtable<String, Integer> selectTerms(ArrayList<RDFNode> a)
	{
		Hashtable<String, Integer> contenedor = new Hashtable<String,Integer>();
	    for(int i = 0; i < a.size(); i++)
	    {
	    	RDFNode n = a.get(i);
	    	String text_val = n.asResource().getLocalName();
	    	//System.out.println(n.toString());
	    	if (contenedor.containsKey(text_val) == false)
	    		contenedor.put(text_val, 1);
	    	else
	    	{
	    		int value = contenedor.get(text_val);
	    		value = value + 1;
	    		contenedor.put(text_val, value);
	    	}
	    }
	    return contenedor;
	}
	
	
	private void addTerm(String p, String obj)
	{
		Statement new_tripple = outputModel.createStatement(
				outputModel.getResource(this.ns_l + "resultedPolicy"),
				outputModel.createProperty(this.ns + p),
				outputModel.createProperty(this.ns_t, obj));
		outputModel.add(new_tripple);
	}
	
	private boolean writeFile()
	{
		File f = new File(this.fileName);
		String aux = f.getName();
		int index = aux.indexOf(".", aux.length()-5);
		String fname = aux.substring(0, index) + "_composed.ttl";
		
		try
		{ 
			FileOutputStream fo = new FileOutputStream("/Users/valentina/Documents/workspace/PoliciesComposition/data/" + fname);
			outputModel.write(fo, "TTL");
	    } catch(java.io.FileNotFoundException e) {
	    	System.err.println(e.getMessage());
	    	return false;
	    }
		return true;
	}
}
