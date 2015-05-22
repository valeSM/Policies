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

import Core.License.Term;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PropertyNotFoundException;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author Valeria Soto-Mendoza
 * @email vsoto@cicese.edu.mx
 * 21/05/2015
 */

public class LicenseComposition {
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
	public String ns_rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public String ns_rdf_t = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	//private String fileName = "";
	//private String fileType = "";
	//private int num_licenses = 0;
	private Model input_model;
	private Model output_model = null;
	private String filename_resulted = "";
	private ArrayList<License> licenses_list = null;
	
	public String analyzeFile(File policy_1, File policy_2, String format)
	{
		String output_filename = "/Users/valentina/Documents/workspace/PoliciesComposition/data/Resulted_policy.ttl";
		this.licenses_list = new ArrayList<License>();
		try
		{
			// when there is only one file as an input
			if (policy_2 == null)
			{
				// load the first file to the input_model
				if (this.loadFile(policy_1.getAbsolutePath(), format))
				{
					printInputModel(format);
					// merge
					this.exploreModel();
					this.compose();
				}
			}
			else	// two files as input
			{
				// load two files to the input_model
				if (this.loadFile(policy_1.getAbsolutePath(), format) && this.loadFile(policy_2.getAbsolutePath(), format))
				{
					printInputModel(format);
					this.exploreModel();
					this.compose();					
				}
			}
			this.writeOutputFile(output_filename, format);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.input_model = null;
		this.output_model = null;
		this.licenses_list = null;
		return this.filename_resulted = output_filename;
	}
	
	// merges the licenses in licenses_list
	private void compose()
	{
		License final_license = null;
		try
		{
			for (int i = 0; i < this.licenses_list.size(); i++)
			{
				final_license = mergeLicenses(final_license, this.licenses_list.get(i));
			}
			this.addLicenseToOutput(final_license);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public License mergeLicenses(License l1, License l2)
	{
		License l3 = null;
		if (l1 == null)
		{
			l3 = l2;
			//this.output_model.setNsPrefixes(this.input_model.getNsPrefixMap());
			//Resource new_resource = this.output_model.createResource(this.ns_l + "resultedPolicy");
		}
		else
		{
			l3 = this.operateLicense(l1,l2);
			this.addLicenseToOutput(l3);
		}
		return l3;
	}
	
	private License operateLicense(License l1, License l2)
	{
		License r = new License();
		r.setLicenseName("resultedPolicy");
		r.addAllowedTerm(this.combineTerms(l1.getAllowedTermsList(), l2.getAllowedTermsList(), "AND"));
		r.addMandatoryTerm(this.combineTerms(l1.getMandatoryTermsList(), l2.getMandatoryTermsList(), "OR"));
		r.addProhibitedTerm(this.combineTerms(l1.getProhibitedTermsList(), l2.getProhibitedTermsList(), "OR"));
		return r;
	}
	
	private ArrayList<Term> combineTerms(ArrayList<Term> t1, ArrayList<Term> t2, String operation)
	{
		ArrayList<Term> comb = new ArrayList<Term>();
		ArrayList<Term> out = new ArrayList<Term>();
		//comb = new ArrayList<Term>(t1,t2);
		comb.addAll(t1);
		comb.addAll(t2);
		
		Hashtable<String, Integer> contenedor = new Hashtable<String,Integer>();
	    for(int i = 0; i < comb.size(); i++)
	    {
	    	Term n = comb.get(i);
	    	String text_val = n.getName();
	    	//System.out.println(n.toString());
	    	if (contenedor.containsKey(text_val) == false)
	    	{
	    		contenedor.put(text_val, 1);
	    		comb.get(i).setStatus(false);
	    		out.add(comb.get(i));
	    	}
	    	else
	    	{
	    		int value = contenedor.get(text_val);
	    		value = value + 1;
	    		contenedor.put(text_val, value);
	    	}
	    }
	    
	    Enumeration<String> keys = contenedor.keys();
		while (keys.hasMoreElements()) 
		{
			String prop_value = keys.nextElement();
			switch (operation)
			{
				case "AND":
					if (contenedor.get(prop_value) == this.licenses_list.size())
					{
						for (int m = 0; m < out.size(); m++)
						{
							Term t = out.get(m);
							if(t.getName().equals(prop_value))
								t.setStatus(true);
						}
					}
						//addTerm(nameProperty, prop_value.toString());
					break;
				case "OR":
					for (int m = 0; m < out.size(); m++)
					{
						Term t = out.get(m);
						if(t.getName().equals(prop_value))
							t.setStatus(true);
					}
					//addTerm(nameProperty, prop_value.toString());
					break;
				default:
					break;
			}	
		}
		return out;
	}
	
	private void addLicenseToOutput(License l)
	{
		this.output_model = ModelFactory.createDefaultModel();
		this.output_model.setNsPrefixes(this.input_model.getNsPrefixMap());
		this.addLicense2Model("License", "a", l.getLicenseName());
		Resource new_resource = this.output_model.createResource(this.ns_l + l.getLicenseName());
		
		
		// add mandatory terms
		for (int i = 0; i < l.getMandatoryTermsList().size(); i++)
		{
			Term aux = l.getMandatoryTermsList().get(i);
			if (aux.isStatus())
				this.addTerm(l.getLicenseName(), "mandatory" + aux.getType(), aux.getName());
		}
		
		// add allowed terms
		for (int i = 0; i < l.getAllowedTermsList().size(); i++)
		{
			Term aux = l.getAllowedTermsList().get(i);
			if (aux.isStatus())
				this.addTerm(l.getLicenseName(), "allowed" + aux.getType(), aux.getName());
		}
		
		// add prohibited terms
		for (int i = 0; i < l.getProhibitedTermsList().size(); i++)
		{
			Term aux = l.getProhibitedTermsList().get(i);
			if (aux.isStatus())
				this.addTerm(l.getLicenseName(), "prohibited" + aux.getType(), aux.getName());
		}
	}
	
	private void addLicense2Model(String sub, String p, String obj)
	{
		Statement new_tripple = this.output_model.createStatement(
				this.output_model.getResource(this.ns_l + obj),
				RDF.type,
				this.output_model.createProperty(this.ns, sub));
		this.output_model.add(new_tripple);
	}
	
	private void addTerm(String sub, String p, String obj)
	{
		Statement new_tripple = this.output_model.createStatement(
				this.output_model.getResource(this.ns_l + sub),
				this.output_model.createProperty(this.ns + p),
				this.output_model.createProperty(this.ns_t, obj));
		this.output_model.add(new_tripple);
	}
	
	// analyzes the input_model to search for licenses
	private void exploreModel()
	{
		System.out.println();
		try
		{
			License l;
			Property p;			
			ResIterator q = this.input_model.listSubjects();
			while (q.hasNext())
			{
				Resource lic = q.nextResource();
				l = new License();
				l.setLicenseName(lic.getLocalName());
				
				// obtain the different properties
				// all allowed terms
				ArrayList<String> aux = getSpecificLocalNameOfProperty(lic, this.ns, "allowedOperation");
				if (!aux.equals(""))
					l.addAllowedTerm(aux, "Operation");
				
				aux = getSpecificLocalNameOfProperty(lic, this.ns, "allowedStatement");
				if (!aux.equals(""))
					l.addAllowedTerm(aux, "Statement");
				
				aux = getSpecificLocalNameOfProperty(lic, this.ns, "allowedPurpose");
				if (!aux.equals(""))
					l.addAllowedTerm(aux, "Purpose");
				
				// all mandatory terms
				aux = getSpecificLocalNameOfProperty(lic, this.ns, "mandatoryOperation");
				if (!aux.equals(""))
					l.addMandatoryTerm(aux, "Operation");
				
				aux = getSpecificLocalNameOfProperty(lic, this.ns, "mandatoryStatement");
				if (!aux.equals(""))
					l.addMandatoryTerm(aux, "Statement");
				
				aux = getSpecificLocalNameOfProperty(lic, this.ns, "mandatoryPurpose");
				if (!aux.equals(""))
					l.addMandatoryTerm(aux, "Purpose");
				
				// all prohibited terms
				aux = getSpecificLocalNameOfProperty(lic, this.ns, "prohibitedOperation");
				if (!aux.equals(""))
					l.addProhibitedTerm(aux, "Operation");
				
				aux = getSpecificLocalNameOfProperty(lic, this.ns, "prohibitedStatement");
				if (!aux.equals(""))
					l.addProhibitedTerm(aux, "Statement");
				
				aux = getSpecificLocalNameOfProperty(lic, this.ns, "prohibitedPurpose");
				if (!aux.equals(""))
					l.addProhibitedTerm(aux, "Purpose");
				
				this.licenses_list.add(l);
			}
			//System.out.println("number of licenses found = " + this.licenses_list.size());
		    System.out.println();
		} catch(PropertyNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<String> getSpecificLocalNameOfProperty(Resource r, String namespace, String property_localname)
	{
		ArrayList<String> result_list = new ArrayList<String>();
		String result = "";
		Property p = ResourceFactory.createProperty(namespace, property_localname);
		if (r.hasProperty(p)) {
			StmtIterator it = r.listProperties(p);
			while (it.hasNext())
			{
				Statement w = it.next();
				//Statement w = r.getRequiredProperty(p);
				result = w.getObject().asNode().getLocalName();
				//System.out.println(result);
				//l.addAllowedTerm(w.getObject().asNode().getLocalName(), "operation");
				result_list.add(result);
			}
		}
		return result_list;
	}
	
	// loads a file into the input_model
	private boolean loadFile(String filename, String fileformat)
	{
		if (this.input_model == null)
			this.input_model = ModelFactory.createDefaultModel();
	    try
	    {
	    	this.input_model.read(new FileInputStream(filename), null, fileformat);
			System.out.println("The file " + filename + " was loaded into the model...");
	    	//printInputModel(fileformat);
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	// displays the input_model in the console
	private void printInputModel(String format)
	{
		this.input_model.write(System.out, format);
		System.out.println();
	}
	
	// displays the output_model in the console
	private void printOutputModel(String format)
	{
		this.output_model.write(System.out, format);
		System.out.println();
	}
	
	// writes the output_model to a file in the format specified
	private void writeOutputFile(String filename, String format)
	{
		File f = new File(filename);
		try
		{
			FileOutputStream fo = new FileOutputStream(filename);
			this.output_model.write(fo, format);
			this.filename_resulted = f.getAbsolutePath();
	    } catch(java.io.FileNotFoundException e) {
	    	System.err.println(e.getMessage());
	    }
	}
	
	// duplicates the input_model into the output_model
	private void duplicateModel()
	{
		//this.input_model
		this.output_model = this.input_model;
		return;
	}
	
	public void printFile(String filename, String fileformat)
	{
		try
	    {
			Model temp = ModelFactory.createDefaultModel();
			temp.read(new FileInputStream(filename), null, fileformat);
			temp.write(System.out, fileformat);
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
