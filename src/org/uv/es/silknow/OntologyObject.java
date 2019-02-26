package org.uv.es.silknow;

/**
* 
* The parent class that represents in Herlange CIDOC-CRM 
* implementation
* 
* 
*
* @version 1.0
* @since   18/12/2018 
*/

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class OntologyObject {
	
	protected Statement statement;
	protected String valuePropertyName;
	
	
	public OntologyObject(Statement statement) {
		super();
		this.statement = statement;
		this.valuePropertyName=null;
	}

	public Statement getStatement() {
		return statement;
	}
	
	public void setStatement(Statement statement) {
		this.statement = statement;
	}
	
	public Resource getResource() {
		return this.statement.getResource();
	}
	
	public String getURI(){
		return this.statement.getSubject().getURI();
	}
	
	public String getValue(OntModel ontModel) {
		
		String result=this.statement.getString();
		
		return result;
		
	}
	
	
}
