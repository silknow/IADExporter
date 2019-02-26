package org.uv.es.silknow;

/**
* 
* The class that represents the Production class in Herlange CIDOC-CRM 
* implementation
* 
* Extends the OntologyObject class
* 
*
* @version 1.0
* @since   18/12/2018 
*/

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;

public class Production extends OntologyObject {

	protected ManMade manMade;
	protected TimeSpan timeSpan;
	protected List<Material> materialList;

	
	public Production(Statement statement) {
		super(statement);
	}
	
	public void loadManMade(Property hasProduced) {
		Statement manMadeSt = statement.getSubject().getProperty(hasProduced);
		this.setManMade(new ManMade(manMadeSt));
	}
	
	public void loadTimeSpan(Property hasTimeSpan) {
		Statement timeSpanSt = statement.getSubject().getProperty(hasTimeSpan);
		this.setTimeSpan(new TimeSpan(timeSpanSt));
	}
	
	public ManMade getManMade() {
		return manMade;
	}
	
	public void setManMade(ManMade manMade) {
		this.manMade = manMade;
	}
	
	public TimeSpan getTimeSpan() {
		return timeSpan;
	}
	
	public void setTimeSpan(TimeSpan timeSpan) {
		this.timeSpan = timeSpan;
	}
	
	public List getMaterials() {
		return materialList;
	}
	
	public void addMaterial(Statement materStm) {
		if (materialList==null)
			materialList = new Vector();
		Material material = new Material(materStm);
		this.materialList.add(material);
	}

	
	public void print(OntModel ontModel){
		String content = "";
		
		content = "Production subject URI =" + this.getURI()+"\n";
		content += "-- Time Span URI = " + this.getTimeSpan().getValue(ontModel)+"\n";//getURI()+"\n";
		Iterator<Material> iterM = this.materialList.iterator();
		while (iterM.hasNext())
			content+="----- material "+iterM.next().getStatement().getString()+"\n";
		content += "-- Man made URI  = " + this.getManMade().getURI()+"\n";
		Iterator<Representation> iterR = this.getManMade().getRepresentations().iterator();
		while (iterR.hasNext())
			content += "----- representation "+iterR.next().getValue(ontModel)+"\n"; //getURI()+"\n";

		content += "----- place "+this.getManMade().getPlace().getValue(ontModel)+"\n";
		
		System.out.println(content);
	}
	
	public void printOld(){
		String content = "";
		
		content = "Production subject URI =" + this.getStatement().getSubject().getURI()+"\n";
		content += "-- Time Span URI = " + this.getTimeSpan().getStatement().getResource().getURI()+"\n";
		Iterator<Material> iterM = this.materialList.iterator();
		while (iterM.hasNext())
			content+="----- material "+iterM.next().getStatement().getString()+"\n";
		content += "-- Man made URI  = " + this.getManMade().getStatement().getResource().getURI()+"\n";
		Iterator<Representation> iterR = this.getManMade().getRepresentations().iterator();
		while (iterR.hasNext())
			content += "----- representation "+iterR.next().getStatement().getSubject().getURI()+"\n";
		//Iterator<Place> iterP = this.getManMade().getPlaces()
		//while (iterP.hasNext())
			//content += "----- place "+iterP.next().getStatement().getResource().getURI()+"\n";
		
		System.out.println(content);
	}
	
}
