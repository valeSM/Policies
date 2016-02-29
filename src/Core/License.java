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
	private ArrayList<Term> allowedTermsList = new ArrayList<Term>();
	private ArrayList<Term> mandatoryTermsList = new ArrayList<Term>();
	private ArrayList<Term> prohibitedTermsList = new ArrayList<Term>();
	
//	private Hashtable<LicenseProperty, Boolean> listOfproperties = new Hashtable<LicenseProperty, Boolean>();
	
//	private ArrayList<String> listLegalTerms;
//	private ArrayList<String> listPurposes;
//	private ArrayList<String> listOperations;
	
	public License()
	{
		//initProperties();
	}
	
	public String getLicenseFamily() {
		return LicenseFamily;
	}

	public void setLicenseFamily(String licenseFamily) {
		LicenseFamily = licenseFamily;
	}

	public String getLicenseName() {
		return LicenseName;
	}

	public void setLicenseName(String licenseName) {
		LicenseName = licenseName;
	}
	
	public void addAllowedTerm(Term t) {
		t.setStatus(true);
		this.allowedTermsList.add(t);
	}
	
	public void addAllowedTerm(ArrayList<String> list, String term_type) {
		for (int i = 0; i < list.size(); i++)
			this.allowedTermsList.add(new Term(list.get(i), term_type));
	}
	
	public void addAllowedTerm(ArrayList<Term> list) {
		this.allowedTermsList = list;
	}
	
	public void addAllowedTerm(String term_name, String term_type) {
		//Term t = new Term(term_name, term_type);
		Term t = new Term(term_name, term_type, true);
		this.allowedTermsList.add(t);
	}
	
	public void addProhibitedTerm(Term t) {
		t.setStatus(true);
		this.prohibitedTermsList.add(t);
	}
	
	public void addProhibitedTerm(ArrayList<String> list, String term_type) {
		for (int i = 0; i < list.size(); i++)
			this.prohibitedTermsList.add(new Term(list.get(i), term_type));
	}
	
	public void addProhibitedTerm(ArrayList<Term> list) {
		this.prohibitedTermsList = list;
	}
	
	public void addProhibitedTerm(String term_name, String term_type) {
		//Term t = new Term(term_name, term_type);
		Term t = new Term(term_name, term_type, true);
		this.allowedTermsList.add(t);
	}
	
	public void addMandatoryTerm(Term t) {
		t.setStatus(true);
		this.mandatoryTermsList.add(t);
	}
	
	public void addMandatoryTerm(ArrayList<String> list, String term_type) {
		for (int i = 0; i < list.size(); i++)
			this.mandatoryTermsList.add(new Term(list.get(i), term_type));
	}
	
	public void addMandatoryTerm(ArrayList<Term> list) {
		this.mandatoryTermsList = list;
	}
	
	public void addMandatoryTerm(String term_name, String term_type) {
		//Term t = new Term(term_name, term_type);
		Term t = new Term(term_name, term_type, true);
		this.allowedTermsList.add(t);
	}
	
	public ArrayList<Term> getAllowedTermsList() {
		return allowedTermsList;
	}

	public ArrayList<Term> getMandatoryTermsList() {
		return mandatoryTermsList;
	}

	public ArrayList<Term> getProhibitedTermsList() {
		return prohibitedTermsList;
	}

//	public Hashtable<LicenseProperty, Boolean> getListOfproperties() {
//		return listOfproperties;
//	}

//	public ArrayList<String> getListLegalTerms() {
//		return listLegalTerms;
//	}
//
//	public ArrayList<String> getListPurposes() {
//		return listPurposes;
//	}
//
//	public ArrayList<String> getListOperations() {
//		return listOperations;
//	}
	
	/*public License compare(License l)
	{
		License r = new License();
		// AND operation
		for (int k = 0; k < this.allowedTermsList.size(); k++)
		{
			
		}
	}*/
	
	
	public class Term {
		private String name = "";
		private String type = "";
		private boolean status = false;
		
		public Term(String name, String type) {
			this.name = name;
			this.type = type;
		}
		
		public Term(String name, String type, boolean status) {
			this.name = name;
			this.type = type;
			this.status = status;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}
		
		public boolean isEqual(Term t)
		{
			if (this.name.equals(t.name) && this.type.equals(t.type))
				return true;
			return false;
		}
	}
	
	
//	private void initProperties()
//	{
//		initLegalTerms();
//		initOperations();
//		initPurposes();
//		
//		// legalTerms
//		for (int n = 1; n <listLegalTerms.size(); n++)
//		{
//			this.listOfproperties.put(new LicenseProperty(listLegalTerms.get(n)), false);
//			//((List<String>) listOfproperties).add(new LicenseProperty(listLegalTerms.get(n)), false);
//		}
//		
//		// operations
//		for (int n = 1; n < listOperations.size(); n++)
//		{
//			listOfproperties.put(new LicenseProperty(listOperations.get(n)), false);
//		}
//		
//		// purposes
//		for (int n = 1; n < listPurposes.size(); n++)
//		{
//			listOfproperties.put(new LicenseProperty(listPurposes.get(n)), false);
//		}
//	}
//	
//	private void initLegalTerms()
//	{
//		this.listLegalTerms = new ArrayList<String>();
//		this.listLegalTerms.add("by");
//		this.listLegalTerms.add("sa");
//		this.listLegalTerms.add("history");
//		this.listLegalTerms.add("origin");
//	}
//	
//	private void initOperations()
//	{
//		this.listOperations = new ArrayList<String>();
//		this.listOperations.add("read");
//		this.listOperations.add("write");
//		this.listOperations.add("unlimitedDisclosure");
//		this.listOperations.add("constraintDerivative");
//	}
//	
//	private void initPurposes()
//	{
//		this.listPurposes = new ArrayList<String>();
//		this.listPurposes.add("commercial");
//		this.listPurposes.add("private");
//		this.listPurposes.add("medical");
//		this.listPurposes.add("wellbeing");
//	}
//	
//	public void addProperty(String name, String level)
//	{
//		//this.properties.(new LicenseProperty(name, level), true);
//		//listOfproperties.g
//		Object l = listOfproperties.get(new LicenseProperty(name,"false"));
//		//l.checkLevel(level);
//	}
//	
//	public class LicenseProperty {
//		// types
//		public boolean isStatement = false;
//		public boolean isOperation = false;
//		public boolean isPurpose = false;
//		
//		// levels
//		public boolean isAllowed = false;
//		public boolean isProhibited = false;
//		public boolean isMandatory = false;
//		
//		public LicenseProperty(String value)
//		{
//			checkType(value);
//		}
//		
//		public LicenseProperty(String value, String level)
//		{
//			checkType(value);
//			checkLevel(level);
//		}
//		
//		public void checkLevel(String l)
//		{
//			if (l.contains("allowed"))
//				this.isAllowed = true;
//			if (l.contains("prohibited"))
//				this.isProhibited = true;
//			if (l.contains("mandatory"))
//				this.isMandatory = true;
//		}
//		
//		/*private void initialize()
//		{
//			initLegalTerms();
//			initOperations();
//			initPurposes();
//		}
//		
//		private void initLegalTerms()
//		{
//			this.listLegalTerms = new ArrayList<String>();
//			this.listLegalTerms.add("by");
//			this.listLegalTerms.add("sa");
//			this.listLegalTerms.add("history");
//			this.listLegalTerms.add("origin");
//		}
//		
//		private void initOperations()
//		{
//			this.listOperations = new ArrayList<String>();
//			this.listOperations.add("read");
//			this.listOperations.add("write");
//			this.listOperations.add("unlimitedDisclosure");
//			this.listOperations.add("constraintDerivative");
//		}
//		
//		private void initPurposes()
//		{
//			this.listPurposes = new ArrayList<String>();
//			this.listPurposes.add("commercial");
//			this.listPurposes.add("private");
//			this.listPurposes.add("medical");
//			this.listPurposes.add("wellbeing");
//		}*/
//		
//		private void checkType(String n)
//		{
//			if (listLegalTerms.contains(n))
//				this.isStatement = true;
//			if (listOperations.contains(n))
//				this.isOperation = true;
//			if (listPurposes.contains(n))
//				this.isPurpose = true;
//		}
//		
//	}
}



/*public class Statement : LicenseProperty {
	private LegalTerm name;
	//private 
}

public class Operation : LicenseProperty {
	
}

public class Purpose : LicenseProperty {
	
}*/
