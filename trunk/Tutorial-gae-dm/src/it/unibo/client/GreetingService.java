package it.unibo.client;


import it.unibo.shared.RecordQuestbook;

import java.util.Vector;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Fabio Magnani, Enrico Gramellini.
 * Parte relativa al client per le chiamate RPC.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	/**
	 * Legge tutti i dati della guestbook.
	 * @return Vettore contenente tutti i messaggi scritti nel guestbook.
	 * @throws Exception Errore nella lettura dal database.
	 */
	Vector<RecordQuestbook> dataCloud() throws Exception;
	
	/**
	 * Legge quali dataset sono stati gia' caricati.
	 * @return Nomi dei dataset gia' caricati.
	 */
	Vector<String> datasetWeka();
	
	/**
	 * Generazione del modello.
	 * @param nameFile Nome del file contenente il dataset.
	 * @return Modello generato.
	 * @throws Exception Errore nel costruire il classificatore. 
	 */
	String serviceWeka(String nameFile) throws Exception;
	
	/**
	 * Verifica se e' gi' stato fatto la richiesta di autorizzazione.
	 * @return Uno se l'autorizzazione e' gia stata eseguita, zero altrimenti.
	 */
	int authorization();
	
	/**
	 * Esegue la predizione con le Prediction API.
	 * @param input Query da predire.
	 * @return Esito della predizione.
	 */
	String doPredict(String input);
	
	/**
	 * Esegue il train dei dati.
	 * @return Esito del train.
	 */
	String doTrain();

	/**
	 * Predizione dell'istanza.
	 * @param instance Instanza da predire.
	 * @return Predizione.
	 * @throws Exception Modello non caricato o istanza non corretta.
	 */
	String classifyMessage(String instance) throws Exception;
}
