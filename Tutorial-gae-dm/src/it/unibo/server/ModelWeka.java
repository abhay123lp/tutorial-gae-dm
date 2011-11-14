package it.unibo.server;


import it.unibo.shared.Attributo;
import it.unibo.shared.DownloadableFile;
import it.unibo.shared.PMF;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Programma per classificare un dataset.
 *
 * @author Fabio Magnani, Enrico Gramellini
 *
 */
@SuppressWarnings("serial")
public class ModelWeka extends RemoteServiceServlet {	
	
	// Training set
	private Instances m_Data = null;
	// Classificatore scelto
	private Classifier m_Classifier = new J48();
	
	public ModelWeka() {
	}
	 
	/**
	 * Metodo che converte una stringa in una istanza (record)
	 *
	 * @param data La stringa deve avere il formato dell'istanza. Tutti i campi tranne quello da predire
	 * @param dataSet Il dataset a cui verra' aggiunta.
	 * @return L'istanza creata.
	 */
	private Instance makeInstance(String data, Instances dataSet) {
		if(dataSet.numInstances()>0)
		{
			int numAttributes = dataSet.firstInstance().numAttributes();		
			String[] values = data.split(",");		
			if(values.length == numAttributes || values.length == numAttributes-1) {
				Instance instance = new Instance(numAttributes);
				for(int i=0;i<values.length;i++) {
					// Setto i valori dell'istanza in base al suo tipo.
					Attribute attribute = dataSet.attribute(dataSet.firstInstance().attribute(i).name());
					if(dataSet.firstInstance().attribute(i).isNumeric())
						instance.setValue(attribute, Double.parseDouble(values[i]));
					else
						instance.setValue(attribute, values[i]);
				}
				  
				// Do l'accesso all'istanza che e' da attribuire alle informazioni del dataset.
				instance.setDataset(dataSet);
				return instance;
			}
			else
				// Errore nel passaggio dei parametri.
				return null;
		}
		// Dataset vuoto.
		return null;
	}
	  
	/**
	 * Classifica un messaggio passato in ingresso.
	 *
	 * @return Una rappresentazione testuale della classe del messaggio.
	 */
	public String classifyMessage(String message) throws Exception {
		// Check whether classifier has been built.
		if (m_Data.numInstances() == 0) {
			throw new Exception("Internal Error - No classifier available.");
		}
		  
		// Creazione del messaggio.
		Instance instance = makeInstance(message, m_Data);
		if(instance != null)
		{
			// Prendo l'indice del valore previsto della classe.
			double predicted = m_Classifier.classifyInstance(instance);
			// Output.
			String msg = "Instance (" + message + ") classified as: " + m_Data.classAttribute().value((int) predicted);
			return msg;
		}
		else
			throw new Exception("Internal Error - Error creating instance");
	}
	  
	/**
	 * Generazione del classificatore.
	 * @param dataFile Nome del file contenente il dataset da usare.
	 * @return Albero del classificatore generato.
	 */
	@SuppressWarnings("unchecked")
	public String makeClassifier(String dataFile) throws Exception{
		
		byte[] buffer;
		Query query = null;
		try{
			PersistenceManager pm = PMF.get().getPersistenceManager();
			// Lettura del file.
			query = pm.newQuery(DownloadableFile.class);
	        query.setFilter("fileName == argFileName");
	        query.declareParameters("String argFileName");
			List<DownloadableFile> results = (List<DownloadableFile>) query.execute(dataFile);
			if (!results.isEmpty()) {
				if(results.size()==1){
					// Array di byte che mi rappresentano il file letto.
					buffer = results.get(0).getFile();
					OutputStream b = new ByteArrayOutputStream();
					b.write(buffer);
					InputStream is = new ByteArrayInputStream(buffer);
					// Creo i dati attraverso il file.
					m_Data = new Instances(new BufferedReader(new InputStreamReader(is)));
					m_Data.setClassIndex(m_Data.numAttributes() - 1);
					// Costruisco il classificatore.
					m_Classifier.buildClassifier(m_Data);
					
					// Chiudo gli elementi utilizzati.
					b.close();
					is.close();
					
					// Ritorno l'albero generato dal classificatore.
					return m_Classifier.toString();
				}
				throw new Exception("Internal Error - I found more file .arff");
			}
			else
				throw new Exception("Internal Error - No files .arff found");
		} catch (IOException e) {
			throw new Exception("Internal Error - No files .arff found");
		} catch (Exception e) {
			throw new Exception("Internal Error - No files .arff found");
		}
		finally {
			if(query!=null)
				query.closeAll();
		}
	}
	
	/**
	 * Legge gli attributi del dataset.
	 * @return Attributi del dataset caricato precedentemente.
	 * @throws Exception Non e' stato caricato nessun dataset.
	 */
	@SuppressWarnings("unchecked")
	public Vector<Attributo> attributesDataset() throws Exception{
		if (m_Data == null)
			throw new Exception("Internal Error - No dataset");
		else{
			Vector<Attributo> listAttribute = new Vector<Attributo>();
			Enumeration<Attribute> temp = (Enumeration<Attribute>)m_Data.enumerateAttributes();
			while(temp.hasMoreElements()){
				// Scorro tutti gli attributi e li metto in un vettore. Non ho potuto usare la 
				// classe Attribute di Weka perche' il client non ha la libreria di Weka.
				Attribute attr = temp.nextElement();
				Vector<String> valuesAttribute = null;
				if (attr.isNominal()){
					Enumeration values = attr.enumerateValues();
					valuesAttribute = new Vector<String>();
					while(values.hasMoreElements())
						valuesAttribute.add((String)values.nextElement());
				}
				// Aggiungo il nuovo attributo.
				listAttribute.add(new Attributo(attr.name(),
						attr.isNominal(),
						attr.isNumeric(),
						valuesAttribute,
					 	attr.getLowerNumericBound(),
					 	attr.getUpperNumericBound()));
			}
			return listAttribute;
		}
	}
	
}