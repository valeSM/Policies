package Core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.lang.*;//.time.format.DateTimeFormatter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import Core.License.Term;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PropertyNotFoundException;
import com.hp.hpl.jena.vocabulary.RDF;

public class LicenseCompositionPriorities {
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
	public String ns_rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public String ns_rdf_t = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public String ns_resulted = "<file:///Users/valentina/Documents/workspace/PriLoo/policies/ResultedData.n3>";
	public String ns_xsd = "<http://www.w3.org/2001/XMLSchema#>";
	public String ns_purpose   = "http://privacy-lookout.net/ontologies/current/pl-ontology.n3#purpose" ;
	
	//private String fileName = "";
	//private String fileType = "";
	//private int num_licenses = 0;
	private Model input_model_1;
	private Model input_model_2;
	private Model output_model = null;
	private String filename_resulted = "";
	private ArrayList<License> licenses_list = null;
	private int num_license_actual = 0;
	
	static protected OntModel owlmodel = null;
	
	public LicenseCompositionPriorities(File policy_1, File policy_2, String format, String user_purpose)
	{
		String output_filename = "/Users/valentina/Documents/workspace/PoliciesComposition/data/Resulted_policy.ttl";
		this.input_model_1 = this.loadFile(this.input_model_1, policy_1.getAbsolutePath(), format);
		if (this.input_model_1 != null)
		{
			this.input_model_2 = this.loadFile(this.input_model_2, policy_2.getAbsolutePath(), format);
			if (this.input_model_2 != null)
			{
				printInputModel(this.input_model_1, format);
				printInputModel(this.input_model_2, format);
				
				// process PUC
				if (this.processPUC(user_purpose))
				{
					// process license
					this.processLicense();
				}
				// merge
				
				
				//this.exploreModel();
				//this.compose();
			}
		}
	}
	
	private boolean processPUC(String purpose)
	{
		if (processDate())
		{
			if (processGrants())
			{
				if (processPreferences())
				{
					if (processLocalities())
					{
						// add object, hasLicense, getPurposeFrom
						
						if (processPurposes(purpose))
						{
							this.printOutputModel("TTL");
							return true;
						}
					}
				}
			}
		}
		else
		{
			System.out.println("--- Dates incompatibility. The process can not be achieved.");
		}
		
		return false;
	}
	
	private boolean processPurposes(String p)
	{
		if (processPermitsPurposes(p))
		{
			if(processProhibitsPurposes(p))
			{
				return true;				
			}
			else
			{
				System.out.println("--- Prohibits Purposes incompatibility. The process can not be achieved.");
			}
		}
		else
		{
			System.out.println("--- Permits Purposes incompatibility. The process can not be achieved.");
		}
		return false;
	}
	
	private boolean processPermitsPurposes(String p)
	{
		ArrayList<String> resultArray1 = this.searchProp(this.input_model_1, this.ns, "permits");
		ArrayList<String> resultArray2 = this.searchProp(this.input_model_2, this.ns, "permits");
		
		ArrayList<String> permitsArray = this.compareArrays(resultArray1, resultArray2, "AND");
		
		if (permitsArray != null)
		{
			// add all permited purposes to the PUC
			for (int i = 0; i < permitsArray.size(); i++)
			{
				this.addStatement2PUC(this.ns_resulted + "composed", this.ns + "permits", permitsArray.get(i));
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private ArrayList<String> seachObjects(Model m, Resource s, Property p)
	{
		try
		{
			ArrayList<String> resultArray = new ArrayList<String>();
			//Property p = m.getProperty(namespace, propname);
			System.out.println("\t" + s.toString());
			System.out.println("\t" + p.toString());
			
			int i = 0;
			StmtIterator r = m.listStatements(s, p, (String)null);
			//StmtIterator r = m.listStatements(s, (Property)null, (String)null);
			while (r.hasNext())
			{
				Statement node = r.nextStatement();//.next();
				resultArray.add(i, node.getObject().toString());
				System.out.println("\t\t+ " + node.toString());
				i = i + 1;
			}
			System.out.println();
			return resultArray;
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
//	private String getSubject(String namespace, String literal)
//	{
//		String subject = "";
//		
//		StmtIterator s = this.input_model_1.listStatements(new SimpleSelector(null, null, this.output_model.createResource(namespace+literal)));
//		if (s.hasNext()) 
//		{
//		    while (s.hasNext()) 
//		    {
//		    	Statement m = s.nextStatement();
//		    	//System.out.println(m.toString());
//		        //System.out.println(m.getSubject().getLocalName());
//		    	subject = m.getSubject().toString();
//		    }
//		}
//		else 
//		{
//		    System.out.println("--- No PUC subject was found in model.");			
//		}
//		return subject;
//	}
	
	
	private boolean processProhibitsPurposes(String p)
	{
				
		String subjectPUC = "";
		
		StmtIterator s = this.input_model_1.listStatements(new SimpleSelector(null, null, this.input_model_1.getResource(this.ns+"PUC")));
		if (s.hasNext()) 
		{
		    while (s.hasNext()) 
		    {
		    	Statement m = s.nextStatement();
		    	//System.out.println(m.toString());
		        //System.out.println(m.getSubject().getLocalName());
		        subjectPUC = m.getSubject().toString();
		    }
		}
		else 
		{
		    System.out.println("--- No PUC subject was found in model.");
			return false;
		}
		
		ArrayList<String> resultArray1 = this.seachObjects(this.input_model_1, this.input_model_1.getResource(subjectPUC), this.input_model_1.getProperty(this.ns, "prohibits"));
		ArrayList<String> resultArray2 = this.seachObjects(this.input_model_2, this.input_model_2.getResource(subjectPUC), this.input_model_2.getProperty(this.ns, "prohibits"));

		
		/*
		 * this.output_model.getResource(this.ns_resulted + "composed"),
					RDF.type,
					this.output_model.createProperty(this.ns, "PUC"));
		 */
		
		ArrayList<String> prohibitsArray = this.compareArrays(resultArray1, resultArray2, "OR");
		
		if (prohibitsArray != null)
		{
			// add all permited purposes to the PUC
			for (int i = 0; i < prohibitsArray.size(); i++)
			{
				this.addStatement2PUC(this.ns_resulted + "composed", this.ns + "prohibits", prohibitsArray.get(i));
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean processLocalities()
	{
		if(processStorageLoc())
		{
			if(processUsageLoc())
			{
				return true;
			}
			else
			{
				System.out.println("--- Usage Locality incompatibility. The process can not be achieved.");
			}
		}
		else
		{
			System.out.println("--- Storage Locality incompatibility. The process can not be achieved.");
		}
		return false;
	}
	
	private boolean processPreferences()
	{
		if(processImplicitProperties())
		{
			if(processGlobalPreferences())
			{
				return true;
			}
			else
			{
				System.out.println("--- Global Preferences incompatibility. The process can not be achieved.");
			}
		}
		else
		{
			System.out.println("--- Implicit Properties incompatibility. The process can not be achieved.");
		}
		return false;
	}
	
	private boolean processGrants()
	{
		if (processGrantees())
		{
			if(processGrantors())
			{
				return true;				
			}
			else
			{
				System.out.println("--- Grantors incompatibility. The process can not be achieved.");
			}
		}
		else
		{
			System.out.println("--- Grantees incompatibility. The process can not be achieved.");
		}
		return false;
	}
	
	private boolean processUsageLoc()
	{
		ArrayList<String> resultArray1 = this.searchProp(this.input_model_1, this.ns, "usageLocality");
		ArrayList<String> resultArray2 = this.searchProp(this.input_model_2, this.ns, "usageLocality");
		
		ArrayList<String> usageLocArray = this.compareArrays(resultArray1, resultArray2, "AND");
		
		if (usageLocArray != null)
		{
			// add all usageLocality to the PUC
			for (int i = 0; i < usageLocArray.size(); i++)
			{
				this.addStatement2PUC(this.ns_resulted + "composed", this.ns + "usageLocality", usageLocArray.get(i));
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean processStorageLoc()
	{
		ArrayList<String> resultArray1 = this.searchProp(this.input_model_1, this.ns, "storageLocality");
		ArrayList<String> resultArray2 = this.searchProp(this.input_model_2, this.ns, "storageLocality");
		
		ArrayList<String> storageLocArray = this.compareArrays(resultArray1, resultArray2, "AND");
		
		if (storageLocArray != null)
		{
			// add all storageLocality to the PUC
			for (int i = 0; i < storageLocArray.size(); i++)
			{
				this.addStatement2PUC(this.ns_resulted + "composed", this.ns + "storageLocality", storageLocArray.get(i));
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean processGlobalPreferences()
	{
		ArrayList<String> resultArray1 = this.searchProp(this.input_model_1, this.ns, "global-preference");
		ArrayList<String> resultArray2 = this.searchProp(this.input_model_2, this.ns, "global-preference");
		
		ArrayList<String> globalPrefArray = this.compareArrays(resultArray1, resultArray2, "AND");
		
		if (globalPrefArray != null)
		{
			// add all global-preference to the PUC
			for (int i = 0; i < globalPrefArray.size(); i++)
			{
				this.addStatement2PUC(this.ns_resulted + "composed", this.ns + "global-preference", globalPrefArray.get(i));
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean processImplicitProperties()
	{
		ArrayList<String> resultArray1 = this.searchProp(this.input_model_1, this.ns, "implicitProperties");
		ArrayList<String> resultArray2 = this.searchProp(this.input_model_2, this.ns, "implicitProperties");
		
		ArrayList<String> implicitPropArray = this.compareArrays(resultArray1, resultArray2, "AND");
		
		if (implicitPropArray != null)
		{
			// add all implicitProperties to the PUC
			for (int i = 0; i < implicitPropArray.size(); i++)
			{
				this.addStatement2PUC(this.ns_resulted + "composed", this.ns + "implicitProperties", implicitPropArray.get(i));
			}
			return true;
		}
		else
		{
			return false;
		}
	}
		
	private boolean processGrantees()
	{
		ArrayList<String> resultArray1 = this.searchProp(this.input_model_1, this.ns, "grantee");
		ArrayList<String> resultArray2 = this.searchProp(this.input_model_2, this.ns, "grantee");
		
		ArrayList<String> granteesArray = this.compareArrays(resultArray1, resultArray2, "AND");
		
		if (granteesArray != null)
		{
			// add all grantees to the PUC
			for (int i = 0; i < granteesArray.size(); i++)
			{
				this.addStatement2PUC(this.ns_resulted + "composed", this.ns + "grantee", granteesArray.get(i));
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private ArrayList<String> compareArrays(ArrayList<String> a, ArrayList<String> b, String condition)
	{
		ArrayList<String> c = new ArrayList<String>();
		ArrayList<String> d = new ArrayList<String>();
		if (a != null && b != null)
		{
			for (int i = 0; i < a.size(); i++)
			{
				for (int j = 0; j < b.size(); j++)
				{
					if (a.get(i).equals(b.get(j)))
					{
						if (! c.contains(a.get(i)))
							c.add(a.get(i));
						if (! d.contains(a.get(i)))
							d.add(a.get(i));						
					}
					else
					{
						if (! c.contains(a.get(i)))
							c.add(a.get(i));
						if (! c.contains(b.get(j)))
							c.add(b.get(j));
					}
				}
			}
				
			if (condition.equals("AND"))
			{
				return d;
			}
			if (condition.equals("OR"))
			{
				return c;
			}
		}
		return null;
	}
	
	private ArrayList<String> searchProp(Model m, String namespace, String propname)
	{
		try
		{
			ArrayList<String> resultArray = new ArrayList<String>();
			Property p = m.getProperty(namespace, propname);
			System.out.println(p.toString());
			
			int i = 0;
			NodeIterator r = m.listObjectsOfProperty(p);//getResource(this.ns);
			while (r.hasNext())
			{
				RDFNode node = r.nextNode();//.next();
				resultArray.add(i, node.toString());
				System.out.println("+ " + node.toString());
				i = i + 1;
			}
			System.out.println();
			return resultArray;
		} catch(Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	private boolean processGrantors()
	{
		ArrayList<String> resultArray1 = this.searchProp(this.input_model_1, this.ns, "grantor");
		ArrayList<String> resultArray2 = this.searchProp(this.input_model_2, this.ns, "grantor");
		
		ArrayList<String> grantorsArray = this.compareArrays(resultArray1, resultArray2, "OR");
		
		if (grantorsArray != null)
		{
			// add all grantors to the PUC
			for (int i = 0; i < grantorsArray.size(); i++)
			{
				this.addStatement2PUC(this.ns_resulted + "composed", this.ns + "grantor", grantorsArray.get(i));
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean processDate()
	{
		ArrayList<String> datesArray = new ArrayList<String>();
		datesArray.add(0, this.searchProp(this.input_model_1, this.ns, "begin").get(0));
		datesArray.add(1, this.searchProp(this.input_model_2, this.ns, "begin").get(0));
		datesArray.add(2, this.searchProp(this.input_model_1, this.ns, "end").get(0));
		datesArray.add(3, this.searchProp(this.input_model_2, this.ns, "end").get(0));
		
//		// Recover the begin dates
//		Property p = this.input_model.getProperty(this.ns, "begin");
//		System.out.println(p.toString());
//		
//		int i = 0;
//		NodeIterator r = this.input_model.listObjectsOfProperty(p);//getResource(this.ns);
//		while (r.hasNext())
//		{
//			RDFNode node = r.nextNode();//.next();
//			datesArray.add(i, node.toString());
//			System.out.println("+ " + node.toString());
//			i = i + 1;
//		}
//		System.out.println();
		
//		p = null;
//		r = null;
//		
//		// Recover the end dates
//		p = this.input_model.getProperty(this.ns, "end");
//		System.out.println(p.toString());
//		
//
//		r = this.input_model.listObjectsOfProperty(p);//getResource(this.ns);
//		while (r.hasNext())
//		{
//			RDFNode node = r.nextNode();//.next();
//			datesArray.add(i, node.toString());
//			System.out.println("+ " + node.toString());
//			i = i + 1;
//		}
//		System.out.println();
					
		String beg1 = datesArray.get(0);//"2014-02-03T00:00:00.000+01:00";
		String beg2 = datesArray.get(1);//"2014-02-03T00:00:00.000-06:00";
		String end1 = datesArray.get(2);//"2015-02-03T00:00:00.000+01:00";
		String end2 = datesArray.get(3);//"2015-02-03T00:00:00.000-06:00";
		
		ArrayList<String> comparedDates = compareDates(beg1, end1, beg2, end2);
		if (comparedDates == null)
		{
			return false;
		}
		else
		{
			this.output_model = ModelFactory.createDefaultModel();
			// add to the map of prefix a new one 
			Map prefixes = this.input_model_1.getNsPrefixMap();
			prefixes.putAll(this.input_model_2.getNsPrefixMap());
			prefixes.put("res",  this.ns_resulted);
			this.output_model.setNsPrefixes(prefixes);

			Statement new_tripple = this.output_model.createStatement(
					this.output_model.getResource(this.ns_resulted + "composed"),
					RDF.type,
					this.output_model.createProperty(this.ns, "PUC"));
			this.output_model.add(new_tripple);	
			
			// add dates
			this.addLiteral2PUC(this.ns_resulted + "composed", this.ns + "begin", comparedDates.get(0));	//this.ns_xsd + comparedDates.get(0));
			this.addLiteral2PUC(this.ns_resulted + "composed", this.ns + "end", comparedDates.get(1));		//this.ns_xsd + comparedDates.get(1));
			
			//this.printOutputModel("TTL");
		}
		return true;
	}
	
	private void addLiteral2PUC(String sub, String p, String obj)
	{
		Statement new_tripple = this.output_model.createLiteralStatement(
				this.output_model.getResource(sub),
				this.output_model.createProperty(p), 
				obj);
		this.output_model.add(new_tripple);
	}
	
	private void addStatement2PUC(String sub, String p, String obj)
	{
		Statement new_tripple = this.output_model.createStatement(
				this.output_model.getResource(sub),
				this.output_model.createProperty(p),
				this.output_model.getResource(obj));
		this.output_model.add(new_tripple);
	}
	
	private ArrayList<String> compareDates(String begin1, String end1, String begin2, String end2)
	{
		ArrayList<String> RespDates = null;
		RespDates = new ArrayList<String>();
		DateTime b1 = convertDateFormat(begin1);
		DateTime e1 = convertDateFormat(end1);
		DateTime b2 = convertDateFormat(begin2);
		DateTime e2 = convertDateFormat(end2);
		
		// case 1: excluyentes
		if (((b1.getMillis() > b2.getMillis()) && (b1.getMillis() >= e2.getMillis())) || ((b2.getMillis() > b1.getMillis()) && (b2.getMillis() >= e1.getMillis())))
		{
			//System.out.println("Excluyentes");
			return null;
		}
		
		// case 2: dates iguales
		if ((b1.getMillis() == b2.getMillis()) && (e1.getMillis() == e2.getMillis()))
		{
			//System.out.println("Iguales");
			RespDates.add(b1.toString());
			RespDates.add(e1.toString());
		}
		
		// case 3: traslapes
		if(((b2.getMillis() <= b1.getMillis()) && (e2.getMillis() <= e1.getMillis())))
		{
			//System.out.println("case a");
			RespDates.add(b1.toString());
			RespDates.add(e2.toString());
		}
		
		if(((b1.getMillis() <= b2.getMillis()) && (e1.getMillis() <= e2.getMillis())))
		{
			//System.out.println("case b");
			RespDates.add(b2.toString());
			RespDates.add(e1.toString());
		}
		return RespDates;
	}
	
	private DateTime convertDateFormat(String d)
	{
		//System.out.println(d);
		String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
		DateTime dt = dtf.parseDateTime(d);
		//System.out.println(dt.toString());
		//System.out.println();
		return dt;
	}
	
	private void processLicense()
	{
		
	}
	
//	public String analyzeFile(File policy_1, File policy_2, String format, String purpose)
//	{
//		String output_filename = "/Users/valentina/Documents/workspace/PoliciesComposition/data/Resulted_policy.ttl";
//		this.licenses_list = new ArrayList<License>();
//		try
//		{
//			// when there is only one file as an input
//			if (policy_2 == null)
//			{
//				// load the first file to the input_model
//				if (this.loadFile(policy_1.getAbsolutePath(), format))
//				{
//					printInputModel(format);
//					// merge
//					this.exploreModel();
//					this.compose();
//				}
//			}
//			else	// two files as input
//			{
//				// load two files to the input_model
//				if (this.loadFile(policy_1.getAbsolutePath(), format) && this.loadFile(policy_2.getAbsolutePath(), format))
//				{
//					printInputModel(format);
//					this.exploreModel();
//					this.compose();					
//				}
//			}
//			this.writeOutputFile(output_filename, format);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		this.input_model = null;
//		this.output_model = null;
//		this.licenses_list = null;
//		return this.filename_resulted = output_filename;
//	}
	
	// merges the licenses in licenses_list
//	private void compose()
//	{
//		License final_license = null;
//		try
//		{
//			for (int i = 0; i < this.licenses_list.size(); i++)
//			{
//				this.num_license_actual = i;
//				final_license = mergeLicenses(final_license, this.licenses_list.get(i));
//			}
//			this.addLicenseToOutput(final_license);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	public License mergeLicenses(License l1, License l2)
//	{
//		License l3 = null;
//		if (l1 == null)
//		{
//			l3 = l2;
//			//this.output_model.setNsPrefixes(this.input_model.getNsPrefixMap());
//			//Resource new_resource = this.output_model.createResource(this.ns_l + "resultedPolicy");
//		}
//		else
//		{
//			l3 = this.operateLicense(l1,l2);
//			this.addLicenseToOutput(l3);
//		}
//		return l3;
//	}
	
	private License operateLicense(License l1, License l2)
	{
		License r = new License();
		r.setLicenseName("resultedPolicy");
		r.addAllowedTerm(this.combineTerms(l1.getAllowedTermsList(), l2.getAllowedTermsList(), "AND"));
		r.addMandatoryTerm(this.combineTerms(l1.getMandatoryTermsList(), l2.getMandatoryTermsList(), "OR"));
		r.addProhibitedTerm(this.combineTerms(l1.getProhibitedTermsList(), l2.getProhibitedTermsList(), "OR"));
		return r;
	}
	
	//private ArrayList
	
	private ArrayList<Term> combineTerms(ArrayList<Term> t1, ArrayList<Term> t2, String operation)
	{
		ArrayList<Term> out = new ArrayList<Term>();
		Hashtable<String, Integer> contenedor = new Hashtable<String,Integer>();

		for (int i = 0; i < t1.size(); i++)
		{
			Term n = t1.get(i);
			String text_val = n.getName();
			if(contenedor.contains(text_val) == false)
			{
				contenedor.put(text_val, 1);
				out.add(n);
			}
		}
		for (int j = 0; j < t2.size(); j++)
		{
			Term n = t2.get(j);
			String text_val = n.getName();
			if(contenedor.containsKey(text_val) == false)
			{
				contenedor.put(text_val, 1);
				out.add(n);
			}
			else
			{
				contenedor.put(text_val, 2);
				// update state of the term in the output array
				for (int k = 0; k < out.size(); k++)
				{
					Term t = out.get(k);
					if (t.getName().equals(text_val))
					{
						if (this.num_license_actual == 1)
							t.setStatus(true);
						else
							t.setStatus(t.isStatus() && true);
					}
				}
			}
		}

	    Enumeration<String> keys = contenedor.keys();
		while (keys.hasMoreElements()) 
		{
			String prop_value = keys.nextElement();
			switch (operation)
			{
				case "AND":
					//System.out.println("evaluate AND " + contenedor.toString() + "    with key = " + prop_value);
					if (contenedor.get(prop_value) == 2)
					{
						for (int k = 0; k < out.size(); k++)
						{
							Term t = out.get(k);
							if (t.getName().equals(prop_value))
							{
								t.setStatus(t.isStatus() && true);
								//System.out.println(t.getName() + " = " + t.isStatus());		
							}
						}
					}
					else		// indicates that the term was only present in one of the actuals policies, but can be true from previous steps
					{
						for (int k = 0; k < out.size(); k++)
						{
							Term t = out.get(k);
							if (t.getName().equals(prop_value))
							{
								t.setStatus(t.isStatus() && false);
								//System.out.println(t.getName() + " = " + t.isStatus());		
							}
						}
					}
					break;
				case "OR":
					for (int m = 0; m < out.size(); m++)
					{
						Term t = out.get(m);
						if(t.getName().equals(prop_value))
							t.setStatus(true);
					}
					break;
				default:
					break;
			}	
		}
		//System.out.println();
		return out;
	}
	
//	private void addLicenseToOutput(License l)
//	{
//		this.output_model = ModelFactory.createDefaultModel();
//		this.output_model.setNsPrefixes(this.input_model.getNsPrefixMap());
//		this.addLicense2Model("License", "a", l.getLicenseName());
//		Resource new_resource = this.output_model.createResource(this.ns_l + l.getLicenseName());
//		
//		
//		// add mandatory terms
//		for (int i = 0; i < l.getMandatoryTermsList().size(); i++)
//		{
//			Term aux = l.getMandatoryTermsList().get(i);
//			if (aux.isStatus())
//				//this.addTerm(l.getLicenseName(), "mandatory" + aux.getType(), aux.getName());
//				this.addTerm(l.getLicenseName(), "obliges", aux.getName());
//		}
//		
//		// add allowed terms
//		for (int i = 0; i < l.getAllowedTermsList().size(); i++)
//		{
//			Term aux = l.getAllowedTermsList().get(i);
//			if (aux.isStatus())
//				//this.addTerm(l.getLicenseName(), "allowed" + aux.getType(), aux.getName());
//				this.addTerm(l.getLicenseName(), "permits", aux.getName());
//		}
//		
//		// add prohibited terms
//		for (int i = 0; i < l.getProhibitedTermsList().size(); i++)
//		{
//			Term aux = l.getProhibitedTermsList().get(i);
//			if (aux.isStatus())
//				//this.addTerm(l.getLicenseName(), "prohibited" + aux.getType(), aux.getName());
//				this.addTerm(l.getLicenseName(), "prohibits", aux.getName());
//		}
//	}
	
//	private void addLicense2Model(String sub, String p, String obj)
//	{
//		Statement new_tripple = this.output_model.createStatement(
//				this.output_model.getResource(this.ns_l + obj),
//				RDF.type,
//				this.output_model.createProperty(this.ns, sub));
//		this.output_model.add(new_tripple);
//	}
	
//	private void addTerm(String sub, String p, String obj)
//	{
//		Statement new_tripple = this.output_model.createStatement(
//				this.output_model.getResource(this.ns_l + sub),
//				this.output_model.createProperty(this.ns + p),
//				this.output_model.createProperty(this.ns_t, obj));
//		this.output_model.add(new_tripple);
//	}
	
	// analyzes the input_model to search for licenses
//	private void exploreModel()
//	{
//		System.out.println();
//		try
//		{
//			License l;
//			Property p;			
//			ResIterator q = this.input_model.listSubjects();
//			while (q.hasNext())
//			{
//				Resource lic = q.nextResource();
//				l = new License();
//				l.setLicenseName(lic.getLocalName());
//				
//				// obtain the different properties
//				// all allowed terms
//				ArrayList<String> aux;
//				aux = getSpecificLocalNameOfProperty(lic, this.ns, "permits");
//				if(aux.size() != 0)
//					l.addAllowedTerm(aux, "Permits");
//				
//				aux = getSpecificLocalNameOfProperty(lic, this.ns, "prohibits");
//				if(aux.size() != 0)
//					l.addProhibitedTerm(aux, "Prohibitions");
//				
//				aux = getSpecificLocalNameOfProperty(lic, this.ns, "obliges");
//				if(aux.size() != 0)
//					l.addMandatoryTerm(aux, "Obligations");
//				
//				/*aux = getSpecificLocalNameOfProperty(lic, this.ns, "allowedOperation");
//				if(aux.size() != 0)
//					l.addAllowedTerm(aux, "Operation");
//				
//				aux = getSpecificLocalNameOfProperty(lic, this.ns, "allowedStatement");
//				if(aux.size() != 0)
//					l.addAllowedTerm(aux, "Statement");
//				
//				aux = getSpecificLocalNameOfProperty(lic, this.ns, "allowedPurpose");
//				if(aux.size() != 0)
//					l.addAllowedTerm(aux, "Purpose");
//				
//				// all mandatory terms
//				aux = getSpecificLocalNameOfProperty(lic, this.ns, "mandatoryOperation");
//				if(aux.size() != 0)
//					l.addMandatoryTerm(aux, "Operation");
//				
//				aux = getSpecificLocalNameOfProperty(lic, this.ns, "mandatoryStatement");
//				if(aux.size() != 0)
//					l.addMandatoryTerm(aux, "Statement");
//				
//				aux = getSpecificLocalNameOfProperty(lic, this.ns, "mandatoryPurpose");
//				if(aux.size() != 0)
//					l.addMandatoryTerm(aux, "Purpose");
//				
//				// all prohibited terms
//				aux = getSpecificLocalNameOfProperty(lic, this.ns, "prohibitedOperation");
//				if(aux.size() != 0)
//					l.addProhibitedTerm(aux, "Operation");
//				
//				aux = getSpecificLocalNameOfProperty(lic, this.ns, "prohibitedStatement");
//				if(aux.size() != 0)
//					l.addProhibitedTerm(aux, "Statement");
//				
//				aux = getSpecificLocalNameOfProperty(lic, this.ns, "prohibitedPurpose");
//				if(aux.size() != 0)
//					l.addProhibitedTerm(aux, "Purpose");*/
//				
//				this.licenses_list.add(l);
//			}
//			//System.out.println("number of licenses found = " + this.licenses_list.size());
//		    System.out.println();
//		} catch(PropertyNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
	
	private ArrayList<String> getSpecificLocalNameOfProperty(Resource r, String namespace, String property_localname)
	{
		//System.out.println(r.toString());
		/*StmtIterator u = r.listProperties();
		while (u.hasNext())
		{
			Statement z = u.nextStatement();
			String the_pred = z.getPredicate().toString();
			String the_compare = namespace + property_localname;
			System.out.println("PRED = " + the_pred);
			System.out.println("QUERY = " + the_compare);
			if (the_pred.equals(the_compare))
				System.out.println(z.getObject().toString());
		}*/
		
		
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
	private Model loadFile(Model m, String filename, String fileformat)
	{
		if (m == null)
			m = ModelFactory.createDefaultModel();
	    try
	    {
	    	m.read(new FileInputStream(filename), null, fileformat);
			System.out.println("The file " + filename + " was loaded into the model...");
	    	//printInputModel(fileformat);
			return m;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	// displays the input_model in the console
	private void printInputModel(Model m, String format)
	{
		try
		{
			if (m != null)
			{
				m.write(System.out, format);
				System.out.println();
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
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
//	private void duplicateModel()
//	{
//		//this.input_model
//		this.output_model = this.input_model;
//		return;
//	}
	
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
	
	/*protected OntModel loadOntology() {
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
	}*/
}
