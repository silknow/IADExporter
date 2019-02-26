package org.uv.es.silknow;

/**
* 
* The class that represents the Place class in Herlange CIDOC-CRM 
* implementation
* 
* Extends the OntologyObject class
* 
*
* @version 1.0
* @since   21/12/2018 
*/

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;

public class Place extends OntologyObject {

	public Place(Statement statement) {
		super(statement);
		this.valuePropertyName = IADExporter.ECRM + "P1_is_identified_by";
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
