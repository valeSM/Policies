/**
 * 
 */
package Core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Valeria Soto-Mendoza
 * @email vsoto@cicese.edu.mx
 * 19/05/2015
 */
public class License {
	private String LicenseFamily = "";
	private String LicenseName = "";
	private Hashtable<LicenseProperty, Boolean> listOfproperties = new Hashtable<LicenseProperty, Boolean>();
	
	private ArrayList<String> listLegalTerms;
	private ArrayList<String> listPurposes;
	private ArrayList<String> listOperations;
	
	public License()
	{
		initProperties();
	}
	
	private void initProperties()
	{
		initLegalTerms();
		initOperations();
		initPurposes();
		
		// legalTerms
		for (int n = 1; n <listLegalTerms.size(); n++)
		{
			this.listOfproperties.put(new LicenseProperty(listLegalTerms.get(n)), false);
			//((List<String>) listOfproperties).add(new LicenseProperty(listLegalTerms.get(n)), false);
		}
		
		// operations
		for (int n = 1; n < listOperations.size(); n++)
		{
			listOfproperties.put(new LicenseProperty(listOperations.get(n)), false);
		}
		
		// purposes
		for (int n = 1; n < listPurposes.size(); n++)
		{
			listOfproperties.put(new LicenseProperty(listPurposes.get(n)), false);
		}
	}
	
	private void initLegalTerms()
	{
		this.listLegalTerms = new ArrayList<String>();
		this.listLegalTerms.add("by");
		this.listLegalTerms.add("sa");
		this.listLegalTerms.add("history");
		this.listLegalTerms.add("origin");
	}
	
	private void initOperations()
	{
		this.listOperations = new ArrayList<String>();
		this.listOperations.add("read");
		this.listOperations.add("write");
		this.listOperations.add("unlimitedDisclosure");
		this.listOperations.add("constraintDerivative");
	}
	
	private void initPurposes()
	{
		this.listPurposes = new ArrayList<String>();
		this.listPurposes.add("commercial");
		this.listPurposes.add("private");
		this.listPurposes.add("medical");
		this.listPurposes.add("wellbeing");
	}
	
	public void addProperty(String name, String level)
	{
		//this.properties.(new LicenseProperty(name, level), true);
		//listOfproperties.g
		Object l = listOfproperties.get(new LicenseProperty(name,"false"));
		//l.checkLevel(level);
	}
	
	public class LicenseProperty {
		// types
		public boolean isStatement = false;
		public boolean isOperation = false;
		public boolean isPurpose = false;
		
		// levels
		public boolean isAllowed = false;
		public boolean isProhibited = false;
		public boolean isMandatory = false;
		
		public LicenseProperty(String value)
		{
			checkType(value);
		}
		
		public LicenseProperty(String value, String level)
		{
			checkType(value);
			checkLevel(level);
		}
		
		public void checkLevel(String l)
		{
			if (l.contains("allowed"))
				this.isAllowed = true;
			if (l.contains("prohibited"))
				this.isProhibited = true;
			if (l.contains("mandatory"))
				this.isMandatory = true;
		}
		
		/*private void initialize()
		{
			initLegalTerms();
			initOperations();
			initPurposes();
		}
		
		private void initLegalTerms()
		{
			this.listLegalTerms = new ArrayList<String>();
			this.listLegalTerms.add("by");
			this.listLegalTerms.add("sa");
			this.listLegalTerms.add("history");
			this.listLegalTerms.add("origin");
		}
		
		private void initOperations()
		{
			this.listOperations = new ArrayList<String>();
			this.listOperations.add("read");
			this.listOperations.add("write");
			this.listOperations.add("unlimitedDisclosure");
			this.listOperations.add("constraintDerivative");
		}
		
		private void initPurposes()
		{
			this.listPurposes = new ArrayList<String>();
			this.listPurposes.add("commercial");
			this.listPurposes.add("private");
			this.listPurposes.add("medical");
			this.listPurposes.add("wellbeing");
		}*/
		
		private void checkType(String n)
		{
			if (listLegalTerms.contains(n))
				this.isStatement = true;
			if (listOperations.contains(n))
				this.isOperation = true;
			if (listPurposes.contains(n))
				this.isPurpose = true;
		}
		
	}
}



/*public class Statement : LicenseProperty {
	private LegalTerm name;
	//private 
}

public class Operation : LicenseProperty {
	
}

public class Purpose : LicenseProperty {
	
}*/
