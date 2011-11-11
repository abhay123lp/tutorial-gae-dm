package it.unibo.client;


import it.unibo.shared.RecordQuestbook;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Interfaccia delle chiamate RPC.
 * @author Fabio Magnani, Enrico Gramellini.
 */
public interface GreetingServiceAsync {
	/**
	 * Legge tutti i dati della guestbook.
	 * @param callback Vettore contenente tutti i messaggi scritti nel guestbook.
	 */
	void dataCloud(AsyncCallback<Vector<RecordQuestbook>> callback);
	
	/**
	 * Legge quali dataset sono stati gia' caricati.
	 * @param callback Nomi dei dataset gia' caricati.
	 */ 
	void datasetWeka(AsyncCallback<Vector<String>> callback);
	
	/**
	 * Generazione del modello.
	 * @param nameFile Nome del file contenente il dataset.
	 * @param callback Modello generato.
	 */
	void serviceWeka(String nameFile,AsyncCallback<String> callback);
	
	/**
	 * Verifica se e' gi' stato fatto la richiesta di autorizzazione.
	 * @param callback Uno se l'autorizzazione e' gia stata eseguita, zero altrimenti.
	 */
	void authorization(AsyncCallback<Integer> callback);
	
	/**
	 * Esegue la predizione con le Prediction API.
	 * @param input Query da predire.
	 * @param callback Esito della predizione.
	 */
	void doPredict(String input, AsyncCallback<String> callback);
	
	/**
	 * Esegue il train dei dati.
	 * @param callback Esito del train.
	 */
	void doTrain(AsyncCallback<String> callback);
}
