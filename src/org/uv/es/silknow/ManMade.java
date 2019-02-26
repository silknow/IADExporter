package org.uv.es.silknow;

/**
* 
* The class that represents the Man-Made class in Herlange CIDOC-CRM 
* implementation
* 
* Extends the OntologyObject class
* 
*
* @version 1.0
* @since   18/12/2018 
*/

import java.util.List;
import java.util.Vector;

import org.apache.jena.rdf.model.Statement;

public class ManMade extends OntologyObject {
	
	protected List<Representation> representations;
	protected Place place;

	public ManMade(Statement statement) {
		super(statement);
		// TODO Auto-generated constructor stub
	}
	
	public String getURI(){
		return this.statement.getResource().getURI();
	}
	
	public List<Representation> getRepresentations() {
		return representations;
	}
	
	public void setRepresentations(List<Representation> representations) {
		this.representations = representations;
	}
	
	public void setPlace(Statement moveToStatement) {
		this.place = new Place(moveToStatement);
	}
	
	public void addRepresentation(Statement represStm){
		if (this.representations ==null)
			this.representations = new Vector<Representation>();
		
		Representation representation = new Representation(represStm);
		this.representations.add(representation);
	}
	
	public Place getPlace() {
		return this.place;
	}
	

}
