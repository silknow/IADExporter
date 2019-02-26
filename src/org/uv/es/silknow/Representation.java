package org.uv.es.silknow;

/**
* 
* The class that represents the Representation class in Herlange CIDOC-CRM 
* implementation
* 
* Extends the OntologyObject class
* 
*
* @version 1.0
* @since   19/12/2018 
*/

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class Representation extends OntologyObject {

	public Representation(Statement statement) {
		super(statement);
		this.valuePropertyName = "http://schema.org/contentUrl";
		
		// TODO Auto-generated constructor stub
	}

	public String getValue(OntModel model) {
		
		String result = "";
		Property valueProp  =  model.getProperty(this.valuePropertyName);
		Statement stmt2 = this.statement.getSubject().getProperty(valueProp);
	
		if (stmt2!=null)
			result = stmt2.getResource().toString();
		
		return result;
		
	}
}
