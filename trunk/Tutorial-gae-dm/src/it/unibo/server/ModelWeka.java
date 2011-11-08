package it.unibo.server;

/**
 * Programma per classificare un dataset.
 *
 * @author Fabio Magnani, Enrico Gramellini
 *
 */
import it.unibo.shared.DownloadableFile;
import it.unibo.shared.PMF;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.catalina.session.FileStore;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ModelWeka extends RemoteServiceServlet {	
	
	// Training set
	private Instances m_Data = null;
	// Classificatore scelto
	private Classifier m_Classifier = new J48();
	
	public ModelWeka() {
	}
	
	/**
	 * Metodo che configura il classificatore a partire dal nome del file che lo contiene
	 *
	 * @param modelName Nome del file che contiene il modello
	 */
	public void setClassifier(String modelName){
		try {
			Classifier model;
			//Load the model
			ObjectInputStream modelInObjectFile = new ObjectInputStream(new FileInputStream(modelName));
			model = (Classifier) modelInObjectFile.readObject();
			modelInObjectFile.close();
   
			this.m_Classifier = model;
			System.out.println("Model "+modelName+" lodaded.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	/**
	 * Metodo che converte una stringa in una istanza (record)
	 *
	 * @param data La stringa nel formato Tempo,Temperatura,Umidita',Vento, [Gioca]
	 * @param dataSet Il dataset a cui verra' aggiunta
	 * @return L'istanza creata
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
					if(dataSet.firstInstance().attribute(i).isString())
						instance.setValue(i, values[i]);
					else if(dataSet.firstInstance().attribute(i).isNumeric())
						instance.setValue(i, Integer.parseInt(values[i]));
				}
				  
				// Give instance access to attribute information from the dataset.
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
	 * Classifica un messaggio passato in ingresso
	 *
	 * @return Una rappresentazione testuale della classe del messaggio
	 */
	public String classifyMessage(String message) throws Exception {
		// Check whether classifier has been built.
		if (m_Data.numInstances() == 0) {
			throw new Exception("No classifier available.");
		}
		  
		// Make message into test instance.
		Instance instance = makeInstance(message, m_Data);
		if(instance != null)
		{
			// Get index of predicted class value.
			double predicted = m_Classifier.classifyInstance(instance);
			// Output class value.
			String msg = "Instance (" + message + ") classified as: " + m_Data.classAttribute().value((int) predicted);
			return msg;
		}
		else
			return null;
	}
	  
	public String doJob(String dataFile) {
		
		byte[] buffer;   
		try{
			PersistenceManager pm = PMF.get().getPersistenceManager();
			// Lettura del file.
			Query query = pm.newQuery(DownloadableFile.class);
	        query.setFilter("fileName == argFileName");
	        query.declareParameters("String argFileName");
			try {
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
						 
						System.out.println(classifyMessage("overcast,81,75,FALSE"));
						   
						return m_Classifier.toString();
					}
					return "TROVATI TROPPI FILE ARFF";
				}
				return "NESSUN FILE ARFF TROVATO";
			} 
			finally {
				query.closeAll();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		   
		return "ERRORE NELLA CLASSIFICAZIONE";
	}
}