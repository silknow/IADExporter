package org.uv.es.silknow;

/**
* The 
* The IADEXporter main class
* 
* The main function receives thre parameters:
* - The from file
* - The outputfile
* - The operation KEY (TO_CVS or TO_RDF)
*
* @version 1.0
* @since   18/12/2018 
*/

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

public class IADExporter {
	
	// Herlange CIDOC-CRM implementation URI classes
	public final static String ECRM = "http://erlangen-crm.org/current/";
	public final static String IMAGE = "E38_Image";
	public final static String MOVE = "E9_Move";
	public final static String MAN_MADE= "E22_Man-Made_Object";
	public final static String PRODUCTION = "E12_Production";
	
	// Tool messages
	public final static String FILES_NOT_LOADED = "ONTOLOGY DATA DON'T LOADED.";
	public final static String FILES_LOADED = "ONTOLOGY DATA LOADED";
	public final static String FILE_WRITING_ERROR ="ERROR WRITING OUTPUT FILE";
	public final static String FILE_GENERATED = "FILE GENERATED";
	public final static String USAGE="IADExporter <file from> <file to> <TO_CSV>";
	
	public final static String TO_CSV = "TO_CSV";
	
	/**
	 * Main function
	 * @param args
	 * - The from file
	 * - The outputfile
	 * - The operation KEY (TO_CVS or TO_RDF)
	 */
	public static void main(String[] args) {
		
		String fileFrom = "./src/org/uv/es/silknow/data/T000053.ttl";
		String fileTo = "ouput.csv";
		String operation = TO_CSV;
		
		if (args.length==3) {
			fileFrom = args[0];
			fileTo = args[1];
			operation = args[2];
		}
		else
			log(USAGE);
		
        OntModel ontologyModel =
                ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        
        OntModel ontologyModelD =
                ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        
        if (!operation.equals(TO_CSV))
        	return;
        
        try {
        	ontologyModel.read("./src/org/uv/es/silknow/data/erlangen.rdf", "RDF/XML-ABBREV");
        	ontologyModelD.read(fileFrom, "RDF/XML-ABBREV");
        }
        catch (Exception e) {
        	IADExporter.log(FILES_NOT_LOADED);
        	System.out.println(e.getMessage());
        	return;
        }
	     
        IADExporter.log(FILES_LOADED);
        
        Vector<Production> productionList = new Vector<Production>();
        
		OntClass productionClass = IADExporter.getClass(ontologyModel, ECRM, PRODUCTION);
		Property hasProduced = ontologyModelD.getProperty(ECRM + "P108_has_produced");
		Property hasTimeSpan = ontologyModelD.getProperty(ECRM + "P4_has_time-span");
		Property hasRepresentation = ontologyModelD.getProperty(ECRM + "P138i_has_representation");
		Property employed = ontologyModelD.getProperty(ECRM + "P126_employed");
		Property moved  =  ontologyModelD.getProperty( ECRM + "P25_moved");
		Property movedTo  =  ontologyModelD.getProperty( ECRM + "P26_moved_to");
		
		OntClass imageClass = IADExporter.getClass(ontologyModel,ECRM,IMAGE);
		OntClass moveClass = IADExporter.getClass(ontologyModel,ECRM,MOVE);
		StmtIterator productionIter = ontologyModelD.listStatements(null, RDF.type, productionClass);
		
		while (productionIter.hasNext()) {
			Statement productInst = productionIter.next();
			Production production = new Production(productInst);
			production.loadManMade(hasProduced);
			production.loadTimeSpan(hasTimeSpan);
			StmtIterator iterE = productInst.getSubject().listProperties(employed);
			while (iterE.hasNext()) 
				production.addMaterial(iterE.next());
			NodeIterator iterNode = ontologyModelD.listObjectsOfProperty(production.getManMade().getResource(), hasRepresentation);
			while (iterNode.hasNext()) {
				RDFNode node = iterNode.next();
				Statement imageSt = findClassInstance(ontologyModelD, imageClass, node.toString());
				if (imageSt!=null)
					production.getManMade().addRepresentation(imageSt);
			}
			
			Statement mvStmt = findClassInstanceWithProperty(ontologyModelD, moveClass, moved, production.getManMade().getStatement().getResource().getURI());
			Statement mvToStmt = mvStmt.getSubject().getProperty(movedTo);
		
			production.getManMade().setPlace(mvToStmt);
			
			productionList.add(production);
		}
		
		
		writeFile(fileTo,productionList,ontologyModelD);
		
	}
	
	/**
	 * Writes the productionList content into a CSV file in UTF-8 codification
	 * @param fileName The name of the file
	 * @param productionList The List with production instances to be processed
	 * @param ontModel The data ontology model
	 */
	protected static void writeFile(String fileName,List <Production> productionList, OntModel ontModel) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			
			Iterator<Production> iterProduction = productionList.iterator();
			while (iterProduction.hasNext()) {
				Production production = iterProduction.next();
				
				Iterator<Representation> iterRep = production.getManMade().getRepresentations().iterator();
				while (iterRep.hasNext()) {
					Representation representation = iterRep.next();
					if (production.getTimeSpan()!=null) 
						writer.write(production.getTimeSpan().getURI()+","+representation.getValue(ontModel)+","+production.getTimeSpan().getValue(ontModel)+"\n");
					if (production.getManMade().getPlace()!=null)
						writer.write(production.getManMade().getPlace().getURI()+","+representation.getValue(ontModel)+","+production.getManMade().getPlace().getValue(ontModel)+"\n");
					if (production.getMaterials()!=null) {
						Iterator<Material> iterMat = production.getMaterials().iterator();
						String mats = "";
						while (iterMat.hasNext()){
							Material mat = iterMat.next();
							if (mats.isEmpty())
								mats = mat.getValue(ontModel);
							else
								mats = mats + "||"+ mat.getValue(ontModel);
						}
						writer.write(production.getURI()+","+representation.getValue(ontModel)+","+mats+"\n");
					}
					//writer.write(production.getURI()+","+representation.getValue(ontModel)+","+"Tejido"+"\n");
					
				}
				
			}
			writer.close();
			log(FILE_GENERATED+" -- "+fileName);
		} catch (FileNotFoundException e) {
			log(FILE_WRITING_ERROR);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			log(FILE_WRITING_ERROR);
			e.printStackTrace();
		}
	
	}
	
	/**
	 * Displays a message in the tool
	 * @param message
	 */
	public static void log(String message) {
		System.out.println("----------------------------------------------");
		System.out.println(message);
		System.out.println("----------------------------------------------");
	}

	/**
	 * Find a instance of the class <ontClass> in the data ontology model 
	 * with the URI specified in the parameter 
	 * @param model
	 * @param ontClass
	 * @param URI
	 * @return
	 */
	public static Statement findClassInstance(OntModel model,OntClass ontClass, String URI) {
		
		Statement statement = null;
 
		StmtIterator i = model.listStatements(null, RDF.type, ontClass);
		
		Property contentURL  =  model.getProperty( "http://schema.org/contentUrl");

		
		while (i.hasNext() && statement==null) {
			Statement st = i.next();
			Resource image = st.getSubject();
			Statement stm2 = image.getProperty(contentURL);
			
			if (URI.equals(image.getURI())) 
				statement = st;
		}
		
		return statement;
	}
	
	/**
	 * 
	 * @param model
	 * @param ontClass
	 * @param property
	 * @param URI
	 * @return
	 */
	public static Statement findClassInstanceWithProperty(OntModel model,OntClass ontClass, Property property, String URI) {
		
		Statement statement = null;
		 
		StmtIterator i = model.listStatements(null, RDF.type, ontClass);
		
		while (i.hasNext() && statement==null) {
			Statement st = i.next();
			Resource image = st.getSubject();
			Statement stm2 = image.getProperty(property);
			if (URI.equals(stm2.getResource().getURI())) 
				statement = st;
		}
		
		return statement;
	}
	
	/**
	 * Get a OntClass from the ontology model <model> with the <NS> and the name <className> 
	 * @param model
	 * @param NS
	 * @param className
	 * @return
	 */
	public static OntClass getClass(Model model, String NS, String className) {
		
		Resource r = model.getResource(NS + className );
		OntClass oClass = (OntClass) r.as( OntClass.class );
		
		return oClass;
	}

}
