package it.unibo.server;

/**
 * Programma per classificare un dataset.
 *
 * @author Fabio Magnani, Enrico Gramellini
 *
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;

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
		Instance instance = new Instance(5);
		String[] values = data.split(",");
		Attribute tempo = dataSet.attribute("outlook");
		Attribute temperatura = dataSet.attribute("temperature");
		Attribute umidita = dataSet.attribute("humidity");
		Attribute vento = dataSet.attribute("windy");
		Attribute gioca = dataSet.attribute("play");
		instance.setValue(tempo, values[0]);
		instance.setValue(temperatura, Integer.parseInt(values[1]));
		instance.setValue(umidita,Integer.parseInt(values[2]));
		instance.setValue(vento, values[3]);
		if(values.length > 4){
			instance.setValue(gioca, values[4]);
	    }
		  
		// Give instance access to attribute information from the dataset.
		instance.setDataset(dataSet);
		return instance;
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
		  
		// Make separate little test set so that message
		// does not get added to string attribute in m_Data.
		Instances testset = m_Data.stringFreeStructure();
		// Make message into test instance.
		Instance instance = makeInstance(message, testset);
		// Get index of predicted class value.
		double predicted = m_Classifier.classifyInstance(instance);
		// Output class value.
		String msg ="Weather classified as: " + m_Data.classAttribute().value((int) predicted);
		return msg;
	}
	  
	public String doJob(String dataFile) {
		   
		try{
			Instances datas = null;
			datas = new Instances(new BufferedReader(new FileReader(dataFile)));
			datas.setClassIndex(datas.numAttributes() - 1);
			m_Classifier.buildClassifier(datas);
			 
			m_Data = datas;
			m_Classifier.buildClassifier(m_Data);
			System.out.println("Classificazione:" + classifyMessage("overcast,81,75,FALSE"));
			   
			return m_Classifier.toString();
			   		   
		}catch (Exception e) {
			e.printStackTrace();
		}
		   
		return "ERRORE NELLA CLASSIFICAZIONE";
	}
}