package org.uv.es.silknow;

/**
* 
* The class that represents the Time-Span class in Herlange CIDOC-CRM 
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
import org.apache.jena.rdf.model.Statement;

public class TimeSpan extends OntologyObject {

	public TimeSpan(Statement statement) {
		super(statement);
		this.valuePropertyName = IADExporter.ECRM + "P78_is_identified_by";
		// TODO Auto-generated constructor stub
	}
	
	public String getURI(){
		return this.statement.getResource().getURI();
	}
	
	public String getValue(OntModel model) {
		
		String result = "";
		Property valueProp  =  model.getProperty(this.valuePropertyName);
		result = this.statement.getResource().getProperty(valueProp).getString();

		
		return result;
		
	}

}
